package com.library.web;

import com.library.model.User;
import java.io.IOException;
import java.util.UUID;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;

/**
 * Runs before every request:
 *  1. forces UTF-8 and adds browser security headers (CSP, no-sniff, no-framing, no caching of pages)
 *  2. sends anonymous visitors to the login page
 *  3. keeps /admin/* for librarians only
 *  4. rejects POSTs without the session's CSRF token
 */
@WebFilter("/*")
public class SecurityFilter implements Filter {

    private static boolean isPublic(String path) {
        return path.equals("/") || path.equals("/index.jsp") || path.equals("/login") || path.equals("/register")
                || path.equals("/logout") || path.equals("/home") || path.startsWith("/css/") || path.startsWith("/js/");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        req.setCharacterEncoding("UTF-8");

        String path = req.getServletPath();
        resp.setHeader("X-Content-Type-Options", "nosniff");
        resp.setHeader("X-Frame-Options", "DENY");
        resp.setHeader("Referrer-Policy", "same-origin");
        resp.setHeader("Content-Security-Policy",
                "default-src 'self'; img-src 'self' data:; form-action 'self'; frame-ancestors 'none'");
        if (!path.startsWith("/css/") && !path.startsWith("/js/")) {
            // stops the Back button from showing private pages after logout
            resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
            resp.setHeader("Pragma", "no-cache");
            resp.setDateHeader("Expires", 0);
        }

        if (isPublic(path)) { chain.doFilter(request, response); return; }

        HttpSession session = req.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }

        if (path.startsWith("/admin/") && !user.isAdmin()) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Librarians only");
            return;
        }

        if (session.getAttribute("csrf") == null) session.setAttribute("csrf", UUID.randomUUID().toString());
        if ("POST".equalsIgnoreCase(req.getMethod())
                && !session.getAttribute("csrf").equals(req.getParameter("csrf"))) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid form token. Reload the page and try again.");
            return;
        }
        chain.doFilter(request, response);
    }
}
