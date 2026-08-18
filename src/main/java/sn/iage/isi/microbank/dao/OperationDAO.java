package sn.iage.isi.microbank.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import sn.iage.isi.microbank.model.Account;
import sn.iage.isi.microbank.model.AccountStatus;
import sn.iage.isi.microbank.model.Operation;
import sn.iage.isi.microbank.model.OperationType;
import sn.iage.isi.microbank.util.JPAUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OperationDAO {

    public Operation deposit(Long accountId, BigDecimal montant, Long agentId, String description) {
        if (montant.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant doit être supérieur à 0");
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            Account account = em.find(Account.class, accountId);
            if (account == null) {
                throw new IllegalArgumentException("Compte introuvable");
            }
            if (account.getStatut() != AccountStatus.ACTIF) {
                throw new IllegalStateException("Le compte n'est pas actif");
            }

            account.setSolde(account.getSolde().add(montant));
            em.merge(account);

            Operation operation = new Operation();
            operation.setReference(generateReference("DEP"));
            operation.setType(OperationType.DEPOT);
            operation.setMontant(montant);
            operation.setDateOperation(LocalDateTime.now());
            operation.setDescription(description);
            operation.setAccount(account);
            operation.setAgent(em.find(sn.iage.isi.microbank.model.User.class, agentId));
            em.persist(operation);

            em.getTransaction().commit();
            return operation;

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Operation withdraw(Long accountId, BigDecimal montant, Long agentId, String description) {
        if (montant.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant doit être supérieur à 0");
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            Account account = em.find(Account.class, accountId);
            if (account == null) {
                throw new IllegalArgumentException("Compte introuvable");
            }
            if (account.getStatut() != AccountStatus.ACTIF) {
                throw new IllegalStateException("Le compte n'est pas actif");
            }
            if (account.getSolde().compareTo(montant) < 0) {
                throw new IllegalStateException("Solde insuffisant");
            }

            account.setSolde(account.getSolde().subtract(montant));
            em.merge(account);

            Operation operation = new Operation();
            operation.setReference(generateReference("RET"));
            operation.setType(OperationType.RETRAIT);
            operation.setMontant(montant);
            operation.setDateOperation(LocalDateTime.now());
            operation.setDescription(description);
            operation.setAccount(account);
            operation.setAgent(em.find(sn.iage.isi.microbank.model.User.class, agentId));
            em.persist(operation);

            em.getTransaction().commit();
            return operation;

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Operation transfer(Long sourceAccountId, Long destAccountId, BigDecimal montant,
                              Long agentId, String description) {
        if (montant.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant doit être supérieur à 0");
        }
        if (sourceAccountId.equals(destAccountId)) {
            throw new IllegalArgumentException("Les comptes source et destination doivent être différents");
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            Account source = em.find(Account.class, sourceAccountId);
            Account dest = em.find(Account.class, destAccountId);

            if (source == null || dest == null) {
                throw new IllegalArgumentException("Compte source ou destination introuvable");
            }
            if (source.getStatut() != AccountStatus.ACTIF || dest.getStatut() != AccountStatus.ACTIF) {
                throw new IllegalStateException("Les deux comptes doivent être actifs");
            }
            if (source.getSolde().compareTo(montant) < 0) {
                throw new IllegalStateException("Solde insuffisant sur le compte source");
            }

            source.setSolde(source.getSolde().subtract(montant));
            dest.setSolde(dest.getSolde().add(montant));
            em.merge(source);
            em.merge(dest);

            Operation operation = new Operation();
            operation.setReference(generateReference("VIR"));
            operation.setType(OperationType.VIREMENT);
            operation.setMontant(montant);
            operation.setDateOperation(LocalDateTime.now());
            operation.setDescription(description);
            operation.setAccount(source);
            operation.setCompteDestination(dest);
            operation.setAgent(em.find(sn.iage.isi.microbank.model.User.class, agentId));
            em.persist(operation);

            em.getTransaction().commit();
            return operation;

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    private String generateReference(String prefix) {
        return prefix + System.currentTimeMillis();
    }

    public List<Operation> findByAccountFiltered(Long accountId, OperationType type,
                                                 LocalDateTime dateDebut, LocalDateTime dateFin,
                                                 BigDecimal montantMin, BigDecimal montantMax,
                                                 int page, int size) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            StringBuilder jpql = new StringBuilder(
                    "SELECT o FROM Operation o "
                            + "LEFT JOIN FETCH o.account acc "
                            + "LEFT JOIN FETCH acc.client "
                            + "LEFT JOIN FETCH o.agent "
                            + "LEFT JOIN FETCH o.compteDestination "
                            + "WHERE o.account.id = :accountId");
            appendFilters(jpql, type, dateDebut, dateFin, montantMin, montantMax);
            jpql.append(" ORDER BY o.dateOperation DESC");

            TypedQuery<Operation> query = em.createQuery(jpql.toString(), Operation.class);
            query.setParameter("accountId", accountId);
            setFilterParameters(query, type, dateDebut, dateFin, montantMin, montantMax);

            query.setFirstResult(page * size);
            query.setMaxResults(size);

            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public long countByAccountFiltered(Long accountId, OperationType type,
                                       LocalDateTime dateDebut, LocalDateTime dateFin,
                                       BigDecimal montantMin, BigDecimal montantMax) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            StringBuilder jpql = new StringBuilder(
                    "SELECT COUNT(o) FROM Operation o WHERE o.account.id = :accountId");
            appendFilters(jpql, type, dateDebut, dateFin, montantMin, montantMax);

            TypedQuery<Long> query = em.createQuery(jpql.toString(), Long.class);
            query.setParameter("accountId", accountId);
            setFilterParameters(query, type, dateDebut, dateFin, montantMin, montantMax);

            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    public List<Operation> findAllByAccount(Long accountId, LocalDateTime dateDebut, LocalDateTime dateFin) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            StringBuilder jpql = new StringBuilder(
                    "SELECT o FROM Operation o "
                            + "LEFT JOIN FETCH o.account acc "
                            + "LEFT JOIN FETCH acc.client "
                            + "WHERE o.account.id = :accountId");

            if (dateDebut != null) jpql.append(" AND o.dateOperation >= :dateDebut");
            if (dateFin != null) jpql.append(" AND o.dateOperation <= :dateFin");
            jpql.append(" ORDER BY o.dateOperation ASC");

            TypedQuery<Operation> query = em.createQuery(jpql.toString(), Operation.class);
            query.setParameter("accountId", accountId);
            if (dateDebut != null) query.setParameter("dateDebut", dateDebut);
            if (dateFin != null) query.setParameter("dateFin", dateFin);

            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public long countToday() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            LocalDateTime debut = LocalDateTime.now().toLocalDate().atStartOfDay();
            LocalDateTime fin = debut.plusDays(1);

            return em.createQuery(
                            "SELECT COUNT(o) FROM Operation o WHERE o.dateOperation >= :debut AND o.dateOperation < :fin",
                            Long.class)
                    .setParameter("debut", debut)
                    .setParameter("fin", fin)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }

    private void appendFilters(StringBuilder jpql, OperationType type, LocalDateTime dateDebut,
                               LocalDateTime dateFin, BigDecimal montantMin, BigDecimal montantMax) {
        if (type != null) jpql.append(" AND o.type = :type");
        if (dateDebut != null) jpql.append(" AND o.dateOperation >= :dateDebut");
        if (dateFin != null) jpql.append(" AND o.dateOperation <= :dateFin");
        if (montantMin != null) jpql.append(" AND o.montant >= :montantMin");
        if (montantMax != null) jpql.append(" AND o.montant <= :montantMax");
    }

    private void setFilterParameters(TypedQuery<?> query, OperationType type, LocalDateTime dateDebut,
                                     LocalDateTime dateFin, BigDecimal montantMin, BigDecimal montantMax) {
        if (type != null) query.setParameter("type", type);
        if (dateDebut != null) query.setParameter("dateDebut", dateDebut);
        if (dateFin != null) query.setParameter("dateFin", dateFin);
        if (montantMin != null) query.setParameter("montantMin", montantMin);
        if (montantMax != null) query.setParameter("montantMax", montantMax);
    }
}