package sn.iage.isi.microbank.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import sn.iage.isi.microbank.model.User;
import sn.iage.isi.microbank.service.UserService;

import java.io.IOException;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String login = request.getParameter("login");
        String motDePasse = request.getParameter("motDePasse");

        if (login == null || login.isBlank() || motDePasse == null || motDePasse.isBlank()) {
            request.setAttribute("erreur", "Login et mot de passe sont obligatoires");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        User user = userService.authenticate(login, motDePasse);

        if (user == null) {
            request.setAttribute("erreur", "Login ou mot de passe incorrect, ou compte désactivé");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        HttpSession session = request.getSession();
        session.setAttribute("user", user);

        response.sendRedirect(request.getContextPath() + "/dashboard");
    }
}