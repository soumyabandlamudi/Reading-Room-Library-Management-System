package com.library.web;

import com.library.dao.BookDAO;
import com.library.dao.IssueDAO;
import com.library.model.User;
import com.library.util.DB;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/** Catalogue: search, filter, and (for members) borrow a book. */
@WebServlet("/books")
public class BookServlet extends BaseServlet {
    private final BookDAO books = new BookDAO();
    private final IssueDAO issues = new IssueDAO();

    @Override
    protected void get(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String q = text(req, "q"), category = text(req, "category");
        User me = currentUser(req);
        req.setAttribute("books", books.search(q, category));
        req.setAttribute("categories", books.categories());
        req.setAttribute("q", q);
        req.setAttribute("category", category);
        if (!me.isAdmin()) req.setAttribute("myBookIds", issues.activeBookIds(me.getId()));
        view(req, resp, "books.jsp");
    }

    /** A member borrows a book. The same rules apply as at the desk (stock, 5-book limit, no overdue books). */
    @Override
    protected void post(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User me = currentUser(req);
        if (me.isAdmin()) {
            flash(req, "error", "Librarians don't borrow books. Log in as a member to borrow.");
            redirect(req, resp, "/books");
            return;
        }
        String error = issues.issueBook(me.getId(), intParam(req, "bookId", 0));
        if (error == null) {
            flash(req, "ok", "Book borrowed. It is due in " + DB.loanDays() + " days. You can return it from My books.");
            redirect(req, resp, "/my-books");
        } else {
            flash(req, "error", error);
            redirect(req, resp, "/books");
        }
    }
}