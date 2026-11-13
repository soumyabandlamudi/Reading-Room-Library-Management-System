package com.library.web;

import com.library.dao.IssueDAO;
import com.library.model.Issue;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/my-books")
public class MyBooksServlet extends BaseServlet {
    private final IssueDAO issues = new IssueDAO();

    @Override
    protected void get(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        List<Issue> mine = issues.forUser(currentUser(req).getId());
        int fines = 0;
        for (Issue i : mine) fines += i.getCurrentFine();
        req.setAttribute("issues", mine);
        req.setAttribute("totalFines", fines);
        view(req, resp, "my-books.jsp");
    }

    /** A member returns one of their own books. Any late fine is shown in rupees and paid at the desk. */
    @Override
    protected void post(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        int fine = issues.returnBook(intParam(req, "id", 0), currentUser(req).getId());
        if (fine < 0) flash(req, "error", "That book could not be returned. It may already be returned.");
        else if (fine > 0) flash(req, "ok", "Book returned. You owe a late fine of \u20B9" + fine + ". Please pay it at the library desk.");
        else flash(req, "ok", "Book returned on time. Thank you!");
        redirect(req, resp, "/my-books");
    }
}