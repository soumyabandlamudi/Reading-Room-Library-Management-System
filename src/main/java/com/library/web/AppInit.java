package com.library.web;

import com.library.dao.UserDAO;
import com.library.util.DB;
import com.library.util.Passwords;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/** On startup, creates the default librarian account if none exists. */
@WebListener
public class AppInit implements ServletContextListener {
    private static volatile boolean adminReady = false;

    @Override
    public void contextInitialized(ServletContextEvent e) {
        ServletContext ctx = e.getServletContext();
        boolean show = false;
        try { show = DB.showErrors(); } catch (Throwable t) { ctx.log("Could not read db.properties", t); }
        ctx.setAttribute("showErrors", show);
        try {
            ctx.setAttribute("finePerDay", DB.finePerDay());
            ctx.setAttribute("loanDays", DB.loanDays());
            ctx.setAttribute("maxLoans", DB.maxLoans());
        } catch (Throwable t) { ctx.log("Could not read library rules from db.properties", t); }
        ensureAdmin(ctx);
    }

    /** Safe to call repeatedly; retried at login if the database was not ready at startup. */
    public static synchronized void ensureAdmin(ServletContext ctx) {
        if (adminReady) return;
        try {
            UserDAO users = new UserDAO();
            if (!users.adminExists()) {
                users.create("Librarian", "admin@library.com", Passwords.hash("admin123"), "ADMIN");
                ctx.log("Created default admin: admin@library.com / admin123 (change it after first login)");
            }
            adminReady = true;
        } catch (Throwable t) {
            ctx.log("Could not reach the database. Check db.properties, the ojdbc jar and the sql scripts", t);
        }
    }
}
