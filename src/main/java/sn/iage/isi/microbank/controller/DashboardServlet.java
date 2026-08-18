package sn.iage.isi.microbank.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sn.iage.isi.microbank.service.AccountService;
import sn.iage.isi.microbank.service.ClientService;
import sn.iage.isi.microbank.service.OperationService;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard"})
public class DashboardServlet extends HttpServlet {

    private final ClientService clientService = new ClientService();
    private final AccountService accountService = new AccountService();
    private final OperationService operationService = new OperationService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        long nombreClients = clientService.count();
        long nombreComptes = accountService.countAll();
        BigDecimal soldeTotal = accountService.sumAllSoldes();
        long operationsDuJour = operationService.countToday();

        request.setAttribute("nombreClients", nombreClients);
        request.setAttribute("nombreComptes", nombreComptes);
        request.setAttribute("soldeTotal", soldeTotal);
        request.setAttribute("operationsDuJour", operationsDuJour);

        request.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(request, response);
    }
}