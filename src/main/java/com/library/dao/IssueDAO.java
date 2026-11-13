package com.library.dao;

import com.library.model.Issue;
import com.library.util.DB;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class IssueDAO {

    private static final String SELECT =
            "SELECT i.*, u.name AS user_name, b.title AS book_title, b.author AS book_author "
          + "FROM issues i JOIN users u ON u.id = i.user_id JOIN books b ON b.id = i.book_id ";

    private Issue map(ResultSet rs) throws SQLException {
        Issue i = new Issue();
        i.setId(rs.getInt("id"));
        i.setUserId(rs.getInt("user_id"));
        i.setBookId(rs.getInt("book_id"));
        i.setIssueDate(rs.getDate("issue_date"));
        i.setDueDate(rs.getDate("due_date"));
        i.setReturnDate(rs.getDate("return_date"));
        i.setFine(rs.getInt("fine"));
        i.setUserName(rs.getString("user_name"));
        i.setBookTitle(rs.getString("book_title"));
        i.setBookAuthor(rs.getString("book_author"));
        return i;
    }

    /** status: "active", "overdue", "returned" or anything else for all loans. */
    public List<Issue> list(String status) throws SQLException {
        String where = "";
        if ("active".equals(status))        where = "WHERE i.return_date IS NULL ";
        else if ("returned".equals(status)) where = "WHERE i.return_date IS NOT NULL ";
        else if ("overdue".equals(status))  where = "WHERE i.return_date IS NULL AND i.due_date < ? ";
        List<Issue> list = new ArrayList<>();
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(SELECT + where + "ORDER BY i.id DESC")) {
            if ("overdue".equals(status)) ps.setDate(1, Date.valueOf(LocalDate.now()));
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        }
        return list;
    }

    public List<Issue> forUser(int userId) throws SQLException {
        List<Issue> list = new ArrayList<>();
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(SELECT + "WHERE i.user_id = ? ORDER BY i.id DESC")) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        }
        return list;
    }

    /** IDs of the books this member currently has on loan. */
    public java.util.Set<Integer> activeBookIds(int userId) throws SQLException {
        java.util.Set<Integer> ids = new java.util.HashSet<>();
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(
                "SELECT book_id FROM issues WHERE user_id = ? AND return_date IS NULL")) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) ids.add(rs.getInt(1)); }
        }
        return ids;
    }

    public int countActive() throws SQLException {
        return count("SELECT COUNT(*) FROM issues WHERE return_date IS NULL", false);
    }

    public int countOverdue() throws SQLException {
        return count("SELECT COUNT(*) FROM issues WHERE return_date IS NULL AND due_date < ?", true);
    }

    private int count(String sql, boolean withToday) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            if (withToday) ps.setDate(1, Date.valueOf(LocalDate.now()));
            try (ResultSet rs = ps.executeQuery()) { rs.next(); return rs.getInt(1); }
        }
    }

    /** Issues a book in one transaction. @return an error message, or null on success */
    public String issueBook(int userId, int bookId) throws SQLException {
        try (Connection c = DB.get()) {
            c.setAutoCommit(false);
            try {
                String error = checkAndInsert(c, userId, bookId);
                if (error != null) { c.rollback(); return error; }
                c.commit();
                return null;
            } catch (SQLException e) { c.rollback(); throw e; }
        }
    }

    private String checkAndInsert(Connection c, int userId, int bookId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT available_copies FROM books WHERE id = ? FOR UPDATE")) {
            ps.setInt(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return "Book not found.";
                if (rs.getInt(1) < 1) return "No copies of this book are available.";
            }
        }
        try (PreparedStatement ps = c.prepareStatement("SELECT 1 FROM users WHERE id = ? AND role = 'MEMBER'")) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) { if (!rs.next()) return "Member not found."; }
        }
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT COUNT(*), COALESCE(SUM(CASE WHEN due_date < ? THEN 1 ELSE 0 END), 0) "
              + "FROM issues WHERE user_id = ? AND return_date IS NULL")) {
            ps.setDate(1, Date.valueOf(LocalDate.now()));
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                if (rs.getInt(2) > 0) return "This member has overdue books. They must return those first.";
                if (rs.getInt(1) >= DB.maxLoans()) return "This member already has the maximum of " + DB.maxLoans() + " books on loan.";
            }
        }
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT 1 FROM issues WHERE user_id = ? AND book_id = ? AND return_date IS NULL")) {
            ps.setInt(1, userId); ps.setInt(2, bookId);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return "This member already has this book."; }
        }
        LocalDate today = LocalDate.now();
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO issues (id, user_id, book_id, issue_date, due_date) VALUES (issues_seq.NEXTVAL, ?,?,?,?)")) {
            ps.setInt(1, userId); ps.setInt(2, bookId);
            ps.setDate(3, Date.valueOf(today));
            ps.setDate(4, Date.valueOf(today.plusDays(DB.loanDays())));
            ps.executeUpdate();
        }
        try (PreparedStatement ps = c.prepareStatement(
                "UPDATE books SET available_copies = available_copies - 1 WHERE id = ?")) {
            ps.setInt(1, bookId);
            ps.executeUpdate();
        }
        return null;
    }

    /** Returns a book and records the fine. @return the fine in rupees, or -1 if the loan isn't active */
    public int returnBook(int issueId) throws SQLException { return returnBook(issueId, 0); }

    /** ownerId > 0 means only that member's own loan may be returned (used by the member's My books page). */
    public int returnBook(int issueId, int ownerId) throws SQLException {
        try (Connection c = DB.get()) {
            c.setAutoCommit(false);
            try {
                int bookId;
                LocalDate due;
                try (PreparedStatement ps = c.prepareStatement(
                        "SELECT book_id, due_date FROM issues WHERE id = ? AND return_date IS NULL"
                      + (ownerId > 0 ? " AND user_id = ?" : "") + " FOR UPDATE")) {
                    ps.setInt(1, issueId);
                    if (ownerId > 0) ps.setInt(2, ownerId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) { c.rollback(); return -1; }
                        bookId = rs.getInt(1);
                        due = rs.getDate(2).toLocalDate();
                    }
                }
                LocalDate today = LocalDate.now();
                int fine = Issue.fineFor(due, today);
                try (PreparedStatement ps = c.prepareStatement("UPDATE issues SET return_date = ?, fine = ? WHERE id = ?")) {
                    ps.setDate(1, Date.valueOf(today)); ps.setInt(2, fine); ps.setInt(3, issueId);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = c.prepareStatement(
                        "UPDATE books SET available_copies = available_copies + 1 WHERE id = ?")) {
                    ps.setInt(1, bookId);
                    ps.executeUpdate();
                }
                c.commit();
                return fine;
            } catch (SQLException e) { c.rollback(); throw e; }
        }
    }
}