package com.library.web;

import com.library.dao.IssueDAO;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/** Librarian view of all loans (read-only). Members borrow and return their own books. */
@WebServlet("/admin/loans")
public class AdminLoansServlet extends BaseServlet {
    private final IssueDAO issues = new IssueDAO();

    @Override
    protected void get(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String status = req.getParameter("status") == null ? "active" : req.getParameter("status");
        req.setAttribute("status", status);
        req.setAttribute("loans", issues.list(status));
        view(req, resp, "admin/loans.jsp");
    }
}