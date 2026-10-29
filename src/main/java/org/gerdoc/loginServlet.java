package org.gerdoc;
    import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

    @WebServlet("/LoginServlet")
public class loginServlet  extends HttpServlet{


    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        String usuario = request.getParameter("username");
        String password = request.getParameter("password");


        String url = "jdbc:mysql://localhost:3306/ESCUELA?useSSL=false&serverTimezone=UTC";
        String dbUser = "root";
        String dbPassword = "n0m3l0";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(url, dbUser, dbPassword);
                 PreparedStatement pstmt = conn.prepareStatement(
                         "SELECT u.usuario, u.password, u.activo, r.nombre AS rol, " +
                                 "COALESCE(al.nombre, prof.nombre) AS nombre_completo " +
                                 "FROM username u " +
                                 "JOIN ROL r ON u.id_rol = r.id " +
                                 "LEFT JOIN ALUMNO al ON u.id = al.id_usuario " +
                                 "LEFT JOIN PROFESOR prof ON u.id = prof.id_usuario " +
                                 "WHERE u.usuario = ? AND u.password = ?"
                 )) {

                pstmt.setString(1, usuario);
                pstmt.setString(2, password);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        boolean activo = rs.getBoolean("activo");


                        if (!activo) {
                            mostrarError(response, "El usuario se encuentra inactivo.");
                            return;
                        }

                        String rol = rs.getString("rol");
                        String nombreCompleto = rs.getString("nombre_completo");


                        HttpSession session = request.getSession();
                        session.setAttribute("USERNAME", usuario);
                        session.setAttribute("nombre", nombreCompleto);
                        session.setAttribute("rol", rol);

                        // Redireccionar según el rol exacto solicitado en el examen
                        if ("PROFESOR".equalsIgnoreCase(rol)) {
                            response.sendRedirect("profesor/bienvenido.jsp");
                        } else if ("ALUMNO".equalsIgnoreCase(rol)) {
                            response.sendRedirect("alumno/bienvenido.jsp");
                        } else {
                            mostrarError(response, "Rol no reconocido.");
                        }

                    } else {
                        // Credenciales incorrectas
                        mostrarError(response, "Usuario o contraseña incorrectos.");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError(response, "Error en el servidor: " + e.getMessage());
        }
    }


        private void mostrarError(HttpServletResponse response, String mensaje) throws IOException {
            PrintWriter out = response.getWriter();
            out.println("<!DOCTYPE html>");
            out.println("<html><head><title>Error</title></head><body>");
            out.println("<h3 style='color:red;'>Error de Autenticación</h3>");
            out.println("<p>" + mensaje + "</p>");
            out.println("<a href='login.jsp'>Volver a intentar</a>");
            out.println("</body></html>");
        }
    }



