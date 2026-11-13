package com.library.web;

import com.library.dao.UserDAO;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/admin/members")
public class AdminMembersServlet extends BaseServlet {
    private final UserDAO users = new UserDAO();

    @Override
    protected void get(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        req.setAttribute("members", users.members());
        view(req, resp, "admin/members.jsp");
    }

    @Override
    protected void post(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String err = users.deleteMember(intParam(req, "id", 0));
        if (err == null) flash(req, "ok", "Member removed."); else flash(req, "error", err);
        redirect(req, resp, "/admin/members");
    }
}
