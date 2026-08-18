package sn.iage.isi.microbank.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sn.iage.isi.microbank.model.Client;
import sn.iage.isi.microbank.service.ClientService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@WebServlet(name = "ClientServlet", urlPatterns = {
        "/clients",
        "/clients/create",
        "/clients/update",
        "/clients/delete",
        "/clients/details"
})
public class ClientServlet extends HttpServlet {

    private final ClientService clientService = new ClientService();
    private static final int PAGE_SIZE = 10;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        switch (path) {
            case "/clients" -> listClients(request, response);
            case "/clients/create" -> showCreateForm(request, response);
            case "/clients/update" -> showUpdateForm(request, response);
            case "/clients/delete" -> deleteClient(request, response);
            case "/clients/details" -> showDetails(request, response);
            default -> response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        switch (path) {
            case "/clients/create" -> createClient(request, response);
            case "/clients/update" -> updateClient(request, response);
            default -> response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    // ===================== LISTE + RECHERCHE + PAGINATION =====================
    private void listClients(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String search = request.getParameter("search");
        int page = parseIntOrDefault(request.getParameter("page"), 0);

        List<Client> clients;
        long total;

        if (search != null && !search.isBlank()) {
            clients = clientService.search(search, page, PAGE_SIZE);
            total = clientService.countSearch(search);
        } else {
            clients = clientService.getPaginated(page, PAGE_SIZE);
            total = clientService.count();
        }

        int totalPages = (int) Math.ceil((double) total / PAGE_SIZE);

        request.setAttribute("clients", clients);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("search", search);

        request.getRequestDispatcher("/WEB-INF/views/clients/list.jsp").forward(request, response);
    }

    // ===================== FORMULAIRE CREATION =====================
    private void showCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/clients/form.jsp").forward(request, response);
    }

    private void createClient(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Client client = buildClientFromRequest(request, null);

        try {
            clientService.createClient(client);
            response.sendRedirect(request.getContextPath() + "/clients");
        } catch (IllegalArgumentException e) {
            request.setAttribute("erreur", e.getMessage());
            request.setAttribute("client", client);
            request.getRequestDispatcher("/WEB-INF/views/clients/form.jsp").forward(request, response);
        }
    }

    // ===================== FORMULAIRE MODIFICATION =====================
    private void showUpdateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long id = parseLongOrNull(request.getParameter("id"));
        if (id == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID client manquant");
            return;
        }

        Client client = clientService.getById(id);
        if (client == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Client introuvable");
            return;
        }

        request.setAttribute("client", client);
        request.getRequestDispatcher("/WEB-INF/views/clients/form.jsp").forward(request, response);
    }

    private void updateClient(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long id = parseLongOrNull(request.getParameter("id"));
        if (id == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID client manquant");
            return;
        }

        Client client = buildClientFromRequest(request, id);

        try {
            clientService.updateClient(client);
            response.sendRedirect(request.getContextPath() + "/clients");
        } catch (IllegalArgumentException e) {
            request.setAttribute("erreur", e.getMessage());
            request.setAttribute("client", client);
            request.getRequestDispatcher("/WEB-INF/views/clients/form.jsp").forward(request, response);
        }
    }

    // ===================== SUPPRESSION =====================
    private void deleteClient(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Long id = parseLongOrNull(request.getParameter("id"));
        if (id != null) {
            clientService.deleteClient(id);
        }
        response.sendRedirect(request.getContextPath() + "/clients");
    }

    // ===================== DETAILS =====================
    private void showDetails(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long id = parseLongOrNull(request.getParameter("id"));
        if (id == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID client manquant");
            return;
        }

        Client client = clientService.getById(id);
        if (client == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Client introuvable");
            return;
        }

        request.setAttribute("client", client);
        request.getRequestDispatcher("/WEB-INF/views/clients/details.jsp").forward(request, response);
    }

    // ===================== UTILITAIRES =====================
    private Client buildClientFromRequest(HttpServletRequest request, Long id) {
        Client client = (id != null) ? clientService.getById(id) : new Client();

        client.setNom(request.getParameter("nom"));
        client.setPrenom(request.getParameter("prenom"));
        client.setTelephone(request.getParameter("telephone"));
        client.setEmail(request.getParameter("email"));
        client.setAdresse(request.getParameter("adresse"));
        client.setNumeroPiece(request.getParameter("numeroPiece"));

        String dateNaissanceStr = request.getParameter("dateNaissance");
        if (dateNaissanceStr != null && !dateNaissanceStr.isBlank()) {
            client.setDateNaissance(LocalDate.parse(dateNaissanceStr));
        }

        return client;
    }

    private int parseIntOrDefault(String value, int defaultValue) {
        try {
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private Long parseLongOrNull(String value) {
        try {
            return value != null ? Long.parseLong(value) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}