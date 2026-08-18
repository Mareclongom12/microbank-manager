package sn.iage.isi.microbank.service;

import sn.iage.isi.microbank.dao.OperationDAO;
import sn.iage.isi.microbank.model.Operation;
import sn.iage.isi.microbank.model.OperationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OperationService {

    private final OperationDAO operationDAO = new OperationDAO();

    public Operation deposit(Long accountId, BigDecimal montant, Long agentId, String description) {
        return operationDAO.deposit(accountId, montant, agentId, description);
    }

    public Operation withdraw(Long accountId, BigDecimal montant, Long agentId, String description) {
        return operationDAO.withdraw(accountId, montant, agentId, description);
    }

    public Operation transfer(Long sourceId, Long destId, BigDecimal montant, Long agentId, String description) {
        return operationDAO.transfer(sourceId, destId, montant, agentId, description);
    }

    public List<Operation> getHistorique(Long accountId, OperationType type,
                                         LocalDateTime dateDebut, LocalDateTime dateFin,
                                         BigDecimal montantMin, BigDecimal montantMax,
                                         int page, int size) {
        return operationDAO.findByAccountFiltered(accountId, type, dateDebut, dateFin,
                montantMin, montantMax, page, size);
    }

    public long countHistorique(Long accountId, OperationType type,
                                LocalDateTime dateDebut, LocalDateTime dateFin,
                                BigDecimal montantMin, BigDecimal montantMax) {
        return operationDAO.countByAccountFiltered(accountId, type, dateDebut, dateFin,
                montantMin, montantMax);
    }

    public List<Operation> getAllForReleve(Long accountId, LocalDateTime dateDebut, LocalDateTime dateFin) {
        return operationDAO.findAllByAccount(accountId, dateDebut, dateFin);
    }

    public long countToday() {
        return operationDAO.countToday();
    }
}