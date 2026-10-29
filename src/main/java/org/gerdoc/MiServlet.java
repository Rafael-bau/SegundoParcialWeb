package org.gerdoc;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serial;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/MiServlet")
public class MiServlet extends HttpServlet
{
    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        String url = "jdbc:mysql://127.0.0.1/TEST?user=root&password=n0m3l0";
        String sql = "INSERT INTO TBL_ROL(ROL,DESCRIPCION) VALUES(?,?)";
        int row = 0;
        resp.setContentType("text/html;charset=UTF-8");


        PrintWriter out = resp.getWriter();
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection( url );
            System.out.println( connection.isClosed( ) );
            preparedStatement = connection.prepareStatement(sql);
            if( preparedStatement == null )
            {
                return;
            }
            preparedStatement.setString(1, req.getParameter("exampleInputEmail1") );
            preparedStatement.setString(2, req.getParameter("exampleSelect") );
            row = preparedStatement.executeUpdate( );
            connection.close( );
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        catch (ClassNotFoundException e)
        {
            throw new RuntimeException(e);
        }
        out.println("<html>");
        out.println("<body>");
        out.println("<h1>Hola desde Servlet</h1>");
        out.println("<p>Tomcat 9 + IntelliJ + Maven</p>");
        out.println("<p>Valor del parámetro 'correo': " + req.getParameter("exampleInputEmail1") + "</p>");
        out.println("<p>Valor del parámetro 'exampleSelect': " + req.getParameter("exampleSelect") + "</p>");
        out.println("<p>Row': " + row + "</p>");


        out.println("</body>");
        out.println("</html>");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        doGet(req, resp);
    }
}
