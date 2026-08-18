package sn.iage.isi.microbank.service;

import sn.iage.isi.microbank.dao.AccountDAO;
import sn.iage.isi.microbank.model.Account;
import sn.iage.isi.microbank.model.AccountType;
import sn.iage.isi.microbank.model.Client;

import java.math.BigDecimal;
import java.util.List;

public class AccountService {

    private final AccountDAO accountDAO = new AccountDAO();

    public Account openAccount(Client client, AccountType type, BigDecimal depotInitial) {
        if (client == null) {
            throw new IllegalArgumentException("Client obligatoire");
        }
        if (depotInitial == null || depotInitial.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Le dépôt initial ne peut pas être négatif");
        }

        String numeroCompte = generateUniqueAccountNumber();

        Account account = new Account(numeroCompte, type, depotInitial, client);
        accountDAO.save(account);
        return account;
    }

    public Account getById(Long id) {
        return accountDAO.findById(id);
    }

    public Account getByNumeroCompte(String numeroCompte) {
        return accountDAO.findByNumeroCompte(numeroCompte);
    }

    public List<Account> getByClientId(Long clientId) {
        return accountDAO.findByClientId(clientId);
    }

    public List<Account> getAll() {
        return accountDAO.findAll();
    }

    public void updateAccount(Account account) {
        accountDAO.update(account);
    }

    // Statistiques pour le tableau de bord
    public long countAll() {
        return accountDAO.countAll();
    }

    public BigDecimal sumAllSoldes() {
        return accountDAO.sumAllSoldes();
    }

    private String generateUniqueAccountNumber() {
        String numero;
        do {
            numero = String.valueOf(100000 + (long) (Math.random() * 900000));
        } while (accountDAO.existsByNumeroCompte(numero));
        return numero;
    }
}