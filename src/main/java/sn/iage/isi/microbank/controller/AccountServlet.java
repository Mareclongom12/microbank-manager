package sn.iage.isi.microbank.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sn.iage.isi.microbank.model.Account;
import sn.iage.isi.microbank.model.AccountType;
import sn.iage.isi.microbank.model.Client;
import sn.iage.isi.microbank.model.Operation;
import sn.iage.isi.microbank.service.AccountService;
import sn.iage.isi.microbank.service.ClientService;
import sn.iage.isi.microbank.service.OperationService;
import sn.iage.isi.microbank.util.PdfGenerator;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@WebServlet(name = "AccountServlet", urlPatterns = {
        "/accounts",
        "/accounts/create",
        "/accounts/details",
        "/accounts/statement"
})
public class AccountServlet extends HttpServlet {

    private final AccountService accountService = new AccountService();
    private final ClientService clientService = new ClientService();
    private final OperationService operationService = new OperationService();
    private final PdfGenerator pdfGenerator = new PdfGenerator();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        switch (path) {
            case "/accounts" -> listAccounts(request, response);
            case "/accounts/create" -> showCreateForm(request, response);
            case "/accounts/details" -> showDetails(request, response);
            case "/accounts/statement" -> downloadStatement(request, response);
            default -> response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        if ("/accounts/create".equals(path)) {
            createAccount(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    // ===================== LISTE =====================
    private void listAccounts(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Account> accounts = accountService.getAll();
        request.setAttribute("accounts", accounts);
        request.getRequestDispatcher("/WEB-INF/views/accounts/list.jsp").forward(request, response);
    }

    // ===================== FORMULAIRE OUVERTURE =====================
    private void showCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long clientId = parseLongOrNull(request.getParameter("clientId"));

        if (clientId != null) {
            Client client = clientService.getById(clientId);
            if (client == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Client introuvable");
                return;
            }
            request.setAttribute("client", client);
        } else {
            request.setAttribute("clientsList", clientService.getAll());
        }

        request.getRequestDispatcher("/WEB-INF/views/accounts/form.jsp").forward(request, response);
    }

    private void createAccount(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long clientId = parseLongOrNull(request.getParameter("clientId"));
        String typeStr = request.getParameter("type");
        String depotStr = request.getParameter("depotInitial");

        Client client = (clientId != null) ? clientService.getById(clientId) : null;

        try {
            if (client == null) {
                throw new IllegalArgumentException("Client invalide");
            }

            AccountType type = AccountType.valueOf(typeStr);
            BigDecimal depotInitial = new BigDecimal(depotStr);

            Account account = accountService.openAccount(client, type, depotInitial);

            response.sendRedirect(request.getContextPath()
                    + "/accounts/details?id=" + account.getId());

        } catch (IllegalArgumentException | NullPointerException e) {
            request.setAttribute("erreur", "Données invalides : " + e.getMessage());
            if (client != null) {
                request.setAttribute("client", client);
            } else {
                request.setAttribute("clientsList", clientService.getAll());
            }
            request.getRequestDispatcher("/WEB-INF/views/accounts/form.jsp").forward(request, response);
        }
    }

    // ===================== DETAILS =====================
    private void showDetails(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long id = parseLongOrNull(request.getParameter("id"));
        if (id == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID compte manquant");
            return;
        }

        Account account = accountService.getById(id);
        if (account == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Compte introuvable");
            return;
        }

        request.setAttribute("account", account);
        request.getRequestDispatcher("/WEB-INF/views/accounts/details.jsp").forward(request, response);
    }

    // ===================== RELEVE PDF =====================
    private void downloadStatement(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Long accountId = parseLongOrNull(request.getParameter("id"));
        if (accountId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID compte manquant");
            return;
        }

        Account account = accountService.getById(accountId);
        if (account == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Compte introuvable");
            return;
        }

        List<Operation> operations = operationService.getAllForReleve(accountId, null, null);

        String periodeDebut = operations.isEmpty()
                ? "-" : operations.get(0).getDateOperation().toLocalDate().toString();
        String periodeFin = LocalDate.now().toString();

        byte[] pdfBytes = pdfGenerator.generateStatement(account, operations, periodeDebut, periodeFin);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "attachment; filename=releve_" + account.getNumeroCompte() + ".pdf");
        response.setContentLength(pdfBytes.length);
        response.getOutputStream().write(pdfBytes);
        response.getOutputStream().flush();
    }

    // ===================== UTILITAIRE =====================
    private Long parseLongOrNull(String value) {
        try {
            return value != null ? Long.parseLong(value) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}