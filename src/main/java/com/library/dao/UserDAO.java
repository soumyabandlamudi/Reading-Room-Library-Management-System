package com.library.dao;

import com.library.model.User;
import com.library.util.DB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    private User map(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setName(rs.getString("name"));
        u.setEmail(rs.getString("email"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setRole(rs.getString("role"));
        u.setCreatedAt(rs.getTimestamp("created_at"));
        return u;
    }

    public User findByEmail(String email) throws SQLException {
        try (Connection c = DB.get();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM users WHERE email = ?")) {
            ps.setString(1, email.toLowerCase());
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }

    public User findById(int id) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement("SELECT * FROM users WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }

    public void updatePassword(int id, String passwordHash) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement("UPDATE users SET password_hash = ? WHERE id = ?")) {
            ps.setString(1, passwordHash);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    /** @return false if the email is already registered */
    public boolean create(String name, String email, String passwordHash, String role) throws SQLException {
        try (Connection c = DB.get();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO users (id, name, email, password_hash, role) VALUES (users_seq.NEXTVAL, ?,?,?,?)")) {
            ps.setString(1, name);
            ps.setString(2, email.toLowerCase());
            ps.setString(3, passwordHash);
            ps.setString(4, role);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1) return false; // ORA-00001: unique constraint (duplicate email)
            throw e;
        }
    }

    public List<User> members() throws SQLException {
        List<User> list = new ArrayList<>();
        try (Connection c = DB.get();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM users WHERE role = 'MEMBER' ORDER BY name")) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public int countMembers() throws SQLException {
        try (Connection c = DB.get();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM users WHERE role = 'MEMBER'")) {
            rs.next();
            return rs.getInt(1);
        }
    }

    public boolean adminExists() throws SQLException {
        try (Connection c = DB.get();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM users WHERE role = 'ADMIN'")) {
            rs.next();
            return rs.getInt(1) > 0;
        }
    }

    /** @return an error message, or null on success */
    public String deleteMember(int id) throws SQLException {
        try (Connection c = DB.get();
             PreparedStatement ps = c.prepareStatement("DELETE FROM users WHERE id = ? AND role = 'MEMBER'")) {
            ps.setInt(1, id);
            ps.executeUpdate();
            return null;
        } catch (SQLException e) {
            if (e.getErrorCode() == 2292) return "This member has loan records, so they can't be removed."; // ORA-02292: child record found
            throw e;
        }
    }
}
