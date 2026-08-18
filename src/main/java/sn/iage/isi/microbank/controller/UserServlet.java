package sn.iage.isi.microbank.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import sn.iage.isi.microbank.model.Role;
import sn.iage.isi.microbank.model.User;
import sn.iage.isi.microbank.service.UserService;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "UserServlet", urlPatterns = {
        "/users",
        "/users/create",
        "/users/toggle"
})
public class UserServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès réservé aux administrateurs");
            return;
        }

        String path = request.getServletPath();

        switch (path) {
            case "/users" -> listUsers(request, response);
            case "/users/create" -> showCreateForm(request, response);
            case "/users/toggle" -> toggleStatus(request, response);
            default -> response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès réservé aux administrateurs");
            return;
        }

        if ("/users/create".equals(request.getServletPath())) {
            createUser(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    // ===================== LISTE =====================
    private void listUsers(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<User> users = userService.getAll();
        request.setAttribute("users", users);
        request.getRequestDispatcher("/WEB-INF/views/users/list.jsp").forward(request, response);
    }

    // ===================== FORMULAIRE CREATION =====================
    private void showCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/users/form.jsp").forward(request, response);
    }

    private void createUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String nom = request.getParameter("nom");
        String prenom = request.getParameter("prenom");
        String login = request.getParameter("login");
        String motDePasse = request.getParameter("motDePasse");
        String roleStr = request.getParameter("role");

        try {
            if (nom == null || nom.isBlank() || prenom == null || prenom.isBlank()
                    || login == null || login.isBlank() || motDePasse == null || motDePasse.isBlank()) {
                throw new IllegalArgumentException("Tous les champs sont obligatoires");
            }

            User user = new User(nom, prenom, login, motDePasse, Role.valueOf(roleStr));
            userService.createUser(user);

            response.sendRedirect(request.getContextPath() + "/users");

        } catch (IllegalArgumentException e) {
            request.setAttribute("erreur", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/users/form.jsp").forward(request, response);
        }
    }

    // ===================== ACTIVER / DESACTIVER =====================
    private void toggleStatus(HttpServletRequest request, HttpServletResponse response) throws IOException {

        Long id = parseLongOrNull(request.getParameter("id"));
        if (id != null) {
            userService.toggleStatus(id);
        }
        response.sendRedirect(request.getContextPath() + "/users");
    }

    // ===================== UTILITAIRES =====================
    private boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return false;
        User user = (User) session.getAttribute("user");
        return user != null && user.getRole() == Role.ADMIN;
    }

    private Long parseLongOrNull(String value) {
        try {
            return value != null ? Long.parseLong(value) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}