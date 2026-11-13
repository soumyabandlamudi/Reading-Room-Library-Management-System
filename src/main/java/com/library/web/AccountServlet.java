package com.library.web;

import com.library.dao.UserDAO;
import com.library.model.User;
import com.library.util.Passwords;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/** Lets any logged-in user change their own password. */
@WebServlet("/account")
public class AccountServlet extends BaseServlet {
    private final UserDAO users = new UserDAO();

    @Override
    protected void get(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        view(req, resp, "account.jsp");
    }

    @Override
    protected void post(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User me = currentUser(req);
        String current = nz(req.getParameter("current")), next = nz(req.getParameter("newPassword")), confirm = nz(req.getParameter("confirm"));
        User fresh = users.findById(me.getId());

        String error = null;
        if (fresh == null || !Passwords.verify(current, fresh.getPasswordHash())) error = "Your current password is incorrect.";
        else if (!Passwords.isStrong(next)) error = "New password must be 8 to 128 characters and include a letter and a number.";
        else if (!next.equals(confirm)) error = "The two new passwords don't match.";
        else if (next.equals(current)) error = "Choose a password different from the current one.";

        if (error != null) { flash(req, "error", error); redirect(req, resp, "/account"); return; }

        users.updatePassword(me.getId(), Passwords.hash(next));
        req.getSession().removeAttribute("weakAdmin");
        req.changeSessionId();
        flash(req, "ok", "Password changed.");
        redirect(req, resp, "/account");
    }

    private static String nz(String s) { return s == null ? "" : s; }
}
