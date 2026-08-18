package sn.iage.isi.microbank.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import sn.iage.isi.microbank.model.*;
import sn.iage.isi.microbank.service.AccountService;
import sn.iage.isi.microbank.service.OperationService;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet(name = "OperationServlet", urlPatterns = {
        "/operations",
        "/operations/deposit",
        "/operations/withdraw",
        "/operations/transfer",
        "/operations/export"
})
public class OperationServlet extends HttpServlet {

    private final OperationService operationService = new OperationService();
    private final AccountService accountService = new AccountService();
    private static final int PAGE_SIZE = 10;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        switch (path) {
            case "/operations" -> showHistorique(request, response);
            case "/operations/deposit" -> showForm(request, response, "deposit.jsp");
            case "/operations/withdraw" -> showForm(request, response, "withdraw.jsp");
            case "/operations/transfer" -> showTransferForm(request, response);
            case "/operations/export" -> exportCsv(request, response);
            default -> response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        switch (path) {
            case "/operations/deposit" -> doDeposit(request, response);
            case "/operations/withdraw" -> doWithdraw(request, response);
            case "/operations/transfer" -> doTransfer(request, response);
            default -> response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void showForm(HttpServletRequest request, HttpServletResponse response, String jspName)
            throws ServletException, IOException {

        Long accountId = parseLongOrNull(request.getParameter("accountId"));
        if (accountId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Compte manquant");
            return;
        }

        Account account = accountService.getById(accountId);
        if (account == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Compte introuvable");
            return;
        }

        request.setAttribute("account", account);
        request.getRequestDispatcher("/WEB-INF/views/operations/" + jspName).forward(request, response);
    }

    private void showTransferForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long accountId = parseLongOrNull(request.getParameter("accountId"));
        if (accountId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Compte manquant");
            return;
        }

        Account account = accountService.getById(accountId);
        if (account == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Compte introuvable");
            return;
        }

        request.setAttribute("account", account);
        request.setAttribute("allAccounts", accountService.getAll());
        request.getRequestDispatcher("/WEB-INF/views/operations/transfer.jsp").forward(request, response);
    }

    private void doDeposit(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long accountId = parseLongOrNull(request.getParameter("accountId"));
        BigDecimal montant = parseBigDecimalOrNull(request.getParameter("montant"));
        String description = request.getParameter("description");
        Long agentId = getConnectedUserId(request);

        try {
            if (accountId == null || montant == null) {
                throw new IllegalArgumentException("Données invalides");
            }
            operationService.deposit(accountId, montant, agentId, description);
            response.sendRedirect(request.getContextPath() + "/accounts/details?id=" + accountId);

        } catch (IllegalArgumentException | IllegalStateException e) {
            request.setAttribute("erreur", e.getMessage());
            request.setAttribute("account", accountService.getById(accountId));
            request.getRequestDispatcher("/WEB-INF/views/operations/deposit.jsp").forward(request, response);
        }
    }

    private void doWithdraw(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long accountId = parseLongOrNull(request.getParameter("accountId"));
        BigDecimal montant = parseBigDecimalOrNull(request.getParameter("montant"));
        String description = request.getParameter("description");
        Long agentId = getConnectedUserId(request);

        try {
            if (accountId == null || montant == null) {
                throw new IllegalArgumentException("Données invalides");
            }
            operationService.withdraw(accountId, montant, agentId, description);
            response.sendRedirect(request.getContextPath() + "/accounts/details?id=" + accountId);

        } catch (IllegalArgumentException | IllegalStateException e) {
            request.setAttribute("erreur", e.getMessage());
            request.setAttribute("account", accountService.getById(accountId));
            request.getRequestDispatcher("/WEB-INF/views/operations/withdraw.jsp").forward(request, response);
        }
    }

    private void doTransfer(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long sourceId = parseLongOrNull(request.getParameter("accountId"));
        Long destId = parseLongOrNull(request.getParameter("destAccountId"));
        BigDecimal montant = parseBigDecimalOrNull(request.getParameter("montant"));
        String description = request.getParameter("description");
        Long agentId = getConnectedUserId(request);

        try {
            if (sourceId == null || destId == null || montant == null) {
                throw new IllegalArgumentException("Données invalides");
            }
            operationService.transfer(sourceId, destId, montant, agentId, description);
            response.sendRedirect(request.getContextPath() + "/accounts/details?id=" + sourceId);

        } catch (IllegalArgumentException | IllegalStateException e) {
            request.setAttribute("erreur", e.getMessage());
            request.setAttribute("account", accountService.getById(sourceId));
            request.setAttribute("allAccounts", accountService.getAll());
            request.getRequestDispatcher("/WEB-INF/views/operations/transfer.jsp").forward(request, response);
        }
    }

    private void showHistorique(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long accountId = parseLongOrNull(request.getParameter("accountId"));
        if (accountId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Compte manquant");
            return;
        }

        Account account = accountService.getById(accountId);
        if (account == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Compte introuvable");
            return;
        }

        int page = parseIntOrDefault(request.getParameter("page"), 0);

        OperationType type = null;
        String typeStr = request.getParameter("type");
        if (typeStr != null && !typeStr.isBlank()) {
            type = OperationType.valueOf(typeStr);
        }

        LocalDateTime dateDebut = parseDateDebut(request.getParameter("dateDebut"));
        LocalDateTime dateFin = parseDateFin(request.getParameter("dateFin"));
        BigDecimal montantMin = parseBigDecimalOrNull(request.getParameter("montantMin"));
        BigDecimal montantMax = parseBigDecimalOrNull(request.getParameter("montantMax"));

        List<Operation> operations = operationService.getHistorique(
                accountId, type, dateDebut, dateFin, montantMin, montantMax, page, PAGE_SIZE);
        long total = operationService.countHistorique(
                accountId, type, dateDebut, dateFin, montantMin, montantMax);

        int totalPages = (int) Math.ceil((double) total / PAGE_SIZE);

        request.setAttribute("account", account);
        request.setAttribute("operations", operations);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("type", typeStr);
        request.setAttribute("dateDebut", request.getParameter("dateDebut"));
        request.setAttribute("dateFin", request.getParameter("dateFin"));
        request.setAttribute("montantMin", request.getParameter("montantMin"));
        request.setAttribute("montantMax", request.getParameter("montantMax"));

        request.getRequestDispatcher("/WEB-INF/views/operations/list.jsp").forward(request, response);
    }

    // ===================== EXPORT CSV (version corrigée : OutputStream uniquement) =====================
    private void exportCsv(HttpServletRequest request, HttpServletResponse response) throws IOException {

        Long accountId = parseLongOrNull(request.getParameter("accountId"));
        if (accountId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Compte manquant");
            return;
        }

        Account account = accountService.getById(accountId);
        if (account == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Compte introuvable");
            return;
        }

        OperationType type = null;
        String typeStr = request.getParameter("type");
        if (typeStr != null && !typeStr.isBlank()) {
            type = OperationType.valueOf(typeStr);
        }

        LocalDateTime dateDebut = parseDateDebut(request.getParameter("dateDebut"));
        LocalDateTime dateFin = parseDateFin(request.getParameter("dateFin"));
        BigDecimal montantMin = parseBigDecimalOrNull(request.getParameter("montantMin"));
        BigDecimal montantMax = parseBigDecimalOrNull(request.getParameter("montantMax"));

        List<Operation> operations = operationService.getHistorique(
                accountId, type, dateDebut, dateFin, montantMin, montantMax, 0, Integer.MAX_VALUE);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        StringBuilder csv = new StringBuilder();
        csv.append("Date;Reference;Type;Montant;Description\n");

        for (Operation op : operations) {
            csv.append(op.getDateOperation().format(formatter)).append(";")
                    .append(op.getReference()).append(";")
                    .append(op.getType()).append(";")
                    .append(op.getMontant()).append(";")
                    .append(op.getDescription() != null ? op.getDescription().replace(";", ",") : "")
                    .append("\n");
        }

        byte[] bom = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
        byte[] contentBytes = csv.toString().getBytes(StandardCharsets.UTF_8);

        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition",
                "attachment; filename=historique_" + account.getNumeroCompte() + ".csv");
        response.setContentLength(bom.length + contentBytes.length);

        response.getOutputStream().write(bom);
        response.getOutputStream().write(contentBytes);
        response.getOutputStream().flush();
    }

    private Long getConnectedUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");
        return user.getId();
    }

    private Long parseLongOrNull(String value) {
        try {
            return value != null ? Long.parseLong(value) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private int parseIntOrDefault(String value, int defaultValue) {
        try {
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private BigDecimal parseBigDecimalOrNull(String value) {
        try {
            return (value != null && !value.isBlank()) ? new BigDecimal(value) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private LocalDateTime parseDateDebut(String value) {
        if (value == null || value.isBlank()) return null;
        return LocalDate.parse(value).atStartOfDay();
    }

    private LocalDateTime parseDateFin(String value) {
        if (value == null || value.isBlank()) return null;
        return LocalDate.parse(value).atTime(23, 59, 59);
    }
}