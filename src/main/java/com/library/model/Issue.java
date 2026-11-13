package com.library.model;

import com.library.util.DB;
import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Issue {
    private int id, userId, bookId, fine;
    private String userName, bookTitle, bookAuthor;
    private Date issueDate, dueDate, returnDate;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    public int getFine() { return fine; }
    public void setFine(int fine) { this.fine = fine; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
    public String getBookAuthor() { return bookAuthor; }
    public void setBookAuthor(String bookAuthor) { this.bookAuthor = bookAuthor; }
    public Date getIssueDate() { return issueDate; }
    public void setIssueDate(Date issueDate) { this.issueDate = issueDate; }
    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }
    public Date getReturnDate() { return returnDate; }
    public void setReturnDate(Date returnDate) { this.returnDate = returnDate; }

    public boolean isReturned() { return returnDate != null; }

    public boolean isOverdue() {
        return returnDate == null && dueDate.toLocalDate().isBefore(LocalDate.now());
    }

    /** Fine in rupees: stored value once returned, otherwise what it would be today. */
    public int getCurrentFine() {
        if (returnDate != null) return fine;
        return fineFor(dueDate.toLocalDate(), LocalDate.now());
    }

    public static int fineFor(LocalDate due, LocalDate returned) {
        long late = ChronoUnit.DAYS.between(due, returned);
        return late > 0 ? (int) late * DB.finePerDay() : 0;
    }
}
