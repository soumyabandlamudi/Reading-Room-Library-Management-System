package com.library.web;

import com.library.dao.UserDAO;
import com.library.model.User;
import com.library.util.Passwords;
import java.util.UUID;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet({"/login", "/register", "/logout", "/home"})
public class AuthServlet extends BaseServlet {
    private static final String DEFAULT_ADMIN_PASSWORD = "admin123";
    private static final String EMAIL = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";
    /** Used so a wrong email takes as long to reject as a wrong password. */
    private static final String DUMMY_HASH = Passwords.hash("dummy-password-1");

    private final UserDAO users = new UserDAO();

    private User sessionUser(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        return s == null ? null : (User) s.getAttribute("user");
    }

    @Override
    protected void get(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User u = sessionUser(req);
        switch (req.getServletPath()) {
            case "/logout":                       // logging out needs a POST with a token, not a link
            case "/home":
                redirect(req, resp, u == null ? "/login" : u.isAdmin() ? "/admin/dashboard" : "/books");
                break;
            case "/register":
                if (u != null) { redirect(req, resp, "/home"); return; }
                view(req, resp, "register.jsp");
                break;
            default:
                if (u != null) { redirect(req, resp, "/home"); return; }
                view(req, resp, "login.jsp");
        }
    }

    @Override
    protected void post(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        switch (req.getServletPath()) {
            case "/logout":   logout(req, resp); break;
            case "/register": if (sessionUser(req) != null) redirect(req, resp, "/home"); else register(req, resp); break;
            default:          if (sessionUser(req) != null) redirect(req, resp, "/home"); else login(req, resp);
        }
    }

    private void logout(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        HttpSession s = req.getSession(false);
        if (s != null && s.getAttribute("csrf") != null && s.getAttribute("csrf").equals(req.getParameter("csrf"))) s.invalidate();
        redirect(req, resp, "/login");
    }

    private void login(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        AppInit.ensureAdmin(getServletContext());   // covers "Oracle started after Tomcat"
        String email = text(req, "email");
        String password = req.getParameter("password") == null ? "" : req.getParameter("password");
        String key = LoginGuard.key(email, req.getRemoteAddr());

        long wait = LoginGuard.lockedForMs(key);
        if (wait > 0) {
            req.setAttribute("error", "Too many failed attempts. Try again in " + ((wait / 60000) + 1) + " minute(s).");
            view(req, resp, "login.jsp");
            return;
        }

        User u = users.findByEmail(email);
        boolean ok = false;
        if (u != null) ok = Passwords.verify(password, u.getPasswordHash());
        else Passwords.verify(password, DUMMY_HASH);   // same work either way, so timing doesn't reveal valid emails
        if (!ok) {
            LoginGuard.fail(key);
            req.setAttribute("error", "Incorrect email or password.");
            view(req, resp, "login.jsp");
            return;
        }
        LoginGuard.success(key);
        startSession(req, u, password);
        redirect(req, resp, "/home");
    }

    private void register(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String name = text(req, "name"), email = text(req, "email").toLowerCase();
        String password = req.getParameter("password") == null ? "" : req.getParameter("password");
        String error = null;
        if (name.isEmpty() || email.isEmpty()) error = "Name and email are required.";
        else if (name.length() > 100) error = "Name is too long (100 characters maximum).";
        else if (email.length() > 120 || !email.matches(EMAIL)) error = "Enter a valid email address.";
        else if (!Passwords.isStrong(password)) error = "Password must be 8 to 128 characters and include a letter and a number.";
        else if (!users.create(name, email, Passwords.hash(password), "MEMBER")) error = "This email is already registered.";

        if (error != null) { req.setAttribute("error", error); view(req, resp, "register.jsp"); return; }
        startSession(req, users.findByEmail(email), password);
        redirect(req, resp, "/home");
    }

    /** New session on login (prevents session fixation), a fresh CSRF token, and no password hash kept in memory. */
    private void startSession(HttpServletRequest req, User u, String plainPassword) {
        HttpSession old = req.getSession(false);
        if (old != null) old.invalidate();
        HttpSession s = req.getSession(true);
        s.setMaxInactiveInterval(30 * 60);
        boolean weakAdmin = u.isAdmin() && DEFAULT_ADMIN_PASSWORD.equals(plainPassword);
        u.setPasswordHash(null);
        s.setAttribute("user", u);
        s.setAttribute("csrf", UUID.randomUUID().toString());
        if (weakAdmin) s.setAttribute("weakAdmin", Boolean.TRUE);
    }
}
