import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;

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
            e.printStackTrace();
        }
    }

    private Connection getConnection(String userType) throws Exception {
        InputStream input = getServletContext().getResourceAsStream("/WEB-INF/lib/" + userType + ".properties");
        Properties props = new Properties();
        props.load(input);

        String url = props.getProperty("db.url");
        String username = props.getProperty("db.username");
        String password = props.getProperty("db.password");

        return DriverManager.getConnection(url, username, password);
    }
}
