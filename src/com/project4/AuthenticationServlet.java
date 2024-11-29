package com.project4;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

public class AuthenticationServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try (Connection conn = getConnection("systemapp")) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM usercredentials WHERE username=? AND password=?");
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                switch (role) {
                    case "root":
                        response.sendRedirect("rootHome.jsp");
                        break;
                    case "client":
                        response.sendRedirect("clientHome.jsp");
                        break;
                    case "accountant":
                        response.sendRedirect("accountantHome.jsp");
                        break;
                    default:
                        response.sendRedirect("errorpage.html");
                }
            } else {
                response.sendRedirect("errorpage.html");
            }
        } catch (Exception e) {
            e.printStackTrace(); // For debugging in logs only.
            request.setAttribute("error", "An internal error occurred. Please contact support.");
            request.getRequestDispatcher("errorpage.html").forward(request, response);
        }
    }

    private Connection getConnection(String userType) throws Exception {
        InputStream input = getServletContext().getResourceAsStream("/WEB-INF/lib/" + userType + ".properties");
        if (input == null) {
            throw new IOException("Properties file not found: " + userType + ".properties");
        }
        Properties props = new Properties();
        props.load(input);

        String url = props.getProperty("db.url");
        String username = props.getProperty("db.username");
        String password = props.getProperty("db.password");

        return DriverManager.getConnection(url, username, password);
    }

}
