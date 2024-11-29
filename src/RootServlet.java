import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class RootServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String sqlCommand = request.getParameter("sqlCommand");

        try (Connection conn = getConnection("root")) {
            Statement stmt = conn.createStatement();
            boolean isResultSet = stmt.execute(sqlCommand);

            if (isResultSet) {
                ResultSet rs = stmt.getResultSet();
                StringBuilder output = new StringBuilder("<table border='1'>");
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                output.append("<tr>");
                for (int i = 1; i <= columnCount; i++) {
                    output.append("<th>").append(metaData.getColumnName(i)).append("</th>");
                }
                output.append("</tr>");

                while (rs.next()) {
                    output.append("<tr>");
                    for (int i = 1; i <= columnCount; i++) {
                        output.append("<td>").append(rs.getString(i)).append("</td>");
                    }
                    output.append("</tr>");
                }
                output.append("</table>");
                request.setAttribute("output", output.toString());
            } else {
                int updateCount = stmt.getUpdateCount();
                request.setAttribute("output", "Rows affected: " + updateCount);
            }
        } catch (Exception e) {
            request.setAttribute("output", "An error occurred while processing your request.");
            e.printStackTrace(); // For debugging logs.
        }


        RequestDispatcher dispatcher = request.getRequestDispatcher("rootHome.jsp");
        dispatcher.forward(request, response);
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
