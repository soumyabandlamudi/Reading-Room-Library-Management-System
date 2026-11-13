package com.library.web;

import com.library.model.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Shared helpers: exceptions become ServletExceptions (shown on error.jsp), flash messages, views. */
public abstract class BaseServlet extends HttpServlet {

    protected void get(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    protected void post(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    @Override
    protected final void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try { get(req, resp); } catch (ServletException | IOException e) { throw e; } catch (Exception e) { log("Request failed: GET " + req.getRequestURI(), e); throw new ServletException(e); }
    }

    @Override
    protected final void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try { post(req, resp); } catch (ServletException | IOException e) { throw e; } catch (Exception e) { log("Request failed: POST " + req.getRequestURI(), e); throw new ServletException(e); }
    }

    protected void view(HttpServletRequest req, HttpServletResponse resp, String jsp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/jsp/" + jsp).forward(req, resp);
    }

    /** One-time message shown on the next page (type: "ok" or "error"). */
    protected void flash(HttpServletRequest req, String type, String message) {
        req.getSession().setAttribute("flash", message);
        req.getSession().setAttribute("flashType", type);
    }

    protected void redirect(HttpServletRequest req, HttpServletResponse resp, String path) throws IOException {
        resp.sendRedirect(req.getContextPath() + path);
    }

    protected User currentUser(HttpServletRequest req) {
        return (User) req.getSession().getAttribute("user");
    }

    protected static int intParam(HttpServletRequest req, String name, int fallback) {
        try { return Integer.parseInt(req.getParameter(name)); } catch (Exception e) { return fallback; }
    }

    protected static String text(HttpServletRequest req, String name) {
        String v = req.getParameter(name);
        return v == null ? "" : v.trim();
    }
}
