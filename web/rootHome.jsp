<%@ page import="java.sql.*" %>
<!DOCTYPE html>
<html>
<head>
    <title>Root User Dashboard</title>
</head>
<body>
    <h1>Root User Interface</h1>
    <form method="POST" action="RootServlet">
        <textarea name="sqlCommand" rows="5" cols="50" placeholder="Enter SQL Command"></textarea><br>
        <button type="submit">Execute Command</button>
        <button type="reset">Reset Form</button>
    </form>
    <div id="results">
        <%
            if (request.getAttribute("output") != null) {
                System.out.println(request.getAttribute("output"));
            }
        %>
    </div>
</body>
</html>
