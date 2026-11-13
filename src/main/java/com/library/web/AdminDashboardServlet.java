package com.library.web;

import com.library.dao.*;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends BaseServlet {
    private final BookDAO books = new BookDAO();
    private final UserDAO users = new UserDAO();
    private final IssueDAO issues = new IssueDAO();

    @Override
    protected void get(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Map<String, Integer> stats = new LinkedHashMap<>();
        stats.put("titles", books.count());
        stats.put("copies", books.totalCopies());
        stats.put("members", users.countMembers());
        stats.put("onLoan", issues.countActive());
        stats.put("overdue", issues.countOverdue());
        req.setAttribute("stats", stats);
        req.setAttribute("overdueList", issues.list("overdue"));
        view(req, resp, "admin/dashboard.jsp");
    }
}
