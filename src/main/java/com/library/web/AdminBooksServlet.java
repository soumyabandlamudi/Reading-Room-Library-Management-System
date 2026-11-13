package com.library.web;

import com.library.dao.BookDAO;
import com.library.model.Book;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/** Add, edit and delete books. */
@WebServlet("/admin/books")
public class AdminBooksServlet extends BaseServlet {
    private final BookDAO books = new BookDAO();

    @Override
    protected void get(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        int editId = intParam(req, "edit", 0);
        if (editId > 0) req.setAttribute("editBook", books.findById(editId));
        req.setAttribute("books", books.search(text(req, "q"), ""));
        req.setAttribute("q", text(req, "q"));
        view(req, resp, "admin/books.jsp");
    }

    @Override
    protected void post(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        if ("delete".equals(req.getParameter("action"))) {
            String err = books.delete(intParam(req, "id", 0));
            if (err == null) flash(req, "ok", "Book deleted."); else flash(req, "error", err);
            redirect(req, resp, "/admin/books");
            return;
        }

        Book b = new Book();
        b.setId(intParam(req, "id", 0));
        b.setTitle(text(req, "title"));
        b.setAuthor(text(req, "author"));
        b.setCategory(text(req, "category").isEmpty() ? "General" : text(req, "category"));
        b.setIsbn(text(req, "isbn"));
        b.setTotalCopies(intParam(req, "totalCopies", -1));

        String problem = null;
        if (b.getTitle().isEmpty() || b.getAuthor().isEmpty() || b.getTotalCopies() < 0)
            problem = "Title, author and a copy count of 0 or more are required.";
        else if (b.getTotalCopies() > 9999) problem = "Total copies can't be more than 9999.";
        else if (b.getTitle().length() > 200 || b.getAuthor().length() > 150 || b.getCategory().length() > 80 || b.getIsbn().length() > 20)
            problem = "One of the fields is too long (title 200, author 150, category 80, ISBN 20 characters).";
        if (problem != null) {
            flash(req, "error", problem);
            redirect(req, resp, b.getId() > 0 ? "/admin/books?edit=" + b.getId() : "/admin/books");
            return;
        }

        if (b.getId() > 0) {
            String err = books.update(b);
            if (err != null) { flash(req, "error", err); redirect(req, resp, "/admin/books?edit=" + b.getId()); return; }
            flash(req, "ok", "Book updated.");
        } else {
            books.add(b);
            flash(req, "ok", "Book added.");
        }
        redirect(req, resp, "/admin/books");
    }
}
