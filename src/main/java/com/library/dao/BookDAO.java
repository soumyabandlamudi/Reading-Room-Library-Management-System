package com.library.dao;

import com.library.model.Book;
import com.library.util.DB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    private Book map(ResultSet rs) throws SQLException {
        Book b = new Book();
        b.setId(rs.getInt("id"));
        b.setTitle(rs.getString("title"));
        b.setAuthor(rs.getString("author"));
        b.setCategory(rs.getString("category"));
        b.setIsbn(rs.getString("isbn"));
        b.setTotalCopies(rs.getInt("total_copies"));
        b.setAvailableCopies(rs.getInt("available_copies"));
        return b;
    }

    /** Search by title / author / ISBN (case-insensitive), optionally filtered by category. */
    public List<Book> search(String q, String category) throws SQLException {
        String like = "%" + (q == null ? "" : q.trim().toUpperCase()) + "%";
        boolean byCategory = category != null && !category.isEmpty();
        // Oracle LIKE is case-sensitive and treats '' as NULL, hence UPPER() and a conditional clause
        String sql = "SELECT * FROM books WHERE (UPPER(title) LIKE ? OR UPPER(author) LIKE ? OR UPPER(isbn) LIKE ?)"
                   + (byCategory ? " AND category = ?" : "") + " ORDER BY title";
        List<Book> list = new ArrayList<>();
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, like); ps.setString(2, like); ps.setString(3, like);
            if (byCategory) ps.setString(4, category);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        }
        return list;
    }

    public List<String> categories() throws SQLException {
        List<String> list = new ArrayList<>();
        try (Connection c = DB.get();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT DISTINCT category FROM books ORDER BY category")) {
            while (rs.next()) list.add(rs.getString(1));
        }
        return list;
    }

    public Book findById(int id) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement("SELECT * FROM books WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }

    public void add(Book b) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(
                "INSERT INTO books (id, title, author, category, isbn, total_copies, available_copies) VALUES (books_seq.NEXTVAL, ?,?,?,?,?,?)")) {
            ps.setString(1, b.getTitle());
            ps.setString(2, b.getAuthor());
            ps.setString(3, b.getCategory());
            ps.setString(4, b.getIsbn());
            ps.setInt(5, b.getTotalCopies());
            ps.setInt(6, b.getTotalCopies());
            ps.executeUpdate();
        }
    }

    /** Keeps available copies consistent with what is currently on loan. @return error or null */
    public String update(Book b) throws SQLException {
        try (Connection c = DB.get()) {
            c.setAutoCommit(false);
            try {
                int onLoan;
                try (PreparedStatement ps = c.prepareStatement(
                        "SELECT total_copies - available_copies FROM books WHERE id = ? FOR UPDATE")) {
                    ps.setInt(1, b.getId());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) { c.rollback(); return "Book not found."; }
                        onLoan = rs.getInt(1);
                    }
                }
                if (b.getTotalCopies() < onLoan) {
                    c.rollback();
                    return onLoan + " copies are on loan, so the total can't be lower than that.";
                }
                try (PreparedStatement ps = c.prepareStatement(
                        "UPDATE books SET title=?, author=?, category=?, isbn=?, total_copies=?, available_copies=? WHERE id=?")) {
                    ps.setString(1, b.getTitle());
                    ps.setString(2, b.getAuthor());
                    ps.setString(3, b.getCategory());
                    ps.setString(4, b.getIsbn());
                    ps.setInt(5, b.getTotalCopies());
                    ps.setInt(6, b.getTotalCopies() - onLoan);
                    ps.setInt(7, b.getId());
                    ps.executeUpdate();
                }
                c.commit();
                return null;
            } catch (SQLException e) { c.rollback(); throw e; }
        }
    }

    /** @return error or null */
    public String delete(int id) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement("DELETE FROM books WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
            return null;
        } catch (SQLException e) {
            if (e.getErrorCode() == 2292) return "This book has loan records, so it can't be deleted."; // ORA-02292: child record found
            throw e;
        }
    }

    public int count() throws SQLException { return scalar("SELECT COUNT(*) FROM books"); }
    public int totalCopies() throws SQLException { return scalar("SELECT COALESCE(SUM(total_copies),0) FROM books"); }

    private int scalar(String sql) throws SQLException {
        try (Connection c = DB.get(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            rs.next();
            return rs.getInt(1);
        }
    }
}
