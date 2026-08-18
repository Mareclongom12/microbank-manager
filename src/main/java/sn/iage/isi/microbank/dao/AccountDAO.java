package sn.iage.isi.microbank.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import sn.iage.isi.microbank.model.Account;
import sn.iage.isi.microbank.util.JPAUtil;

import java.util.List;

public class AccountDAO {

    public void save(Account account) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(account);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void update(Account account) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(account);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    // Version corrigée : charge le client en même temps (LEFT JOIN FETCH)
    // pour éviter la LazyInitializationException dans les JSP
    public Account findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Account> query = em.createQuery(
                    "SELECT a FROM Account a LEFT JOIN FETCH a.client WHERE a.id = :id", Account.class);
            query.setParameter("id", id);
            List<Account> result = query.getResultList();
            return result.isEmpty() ? null : result.get(0);
        } finally {
            em.close();
        }
    }

    public Account findByNumeroCompte(String numeroCompte) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Account> query = em.createQuery(
                    "SELECT a FROM Account a LEFT JOIN FETCH a.client WHERE a.numeroCompte = :num", Account.class);
            query.setParameter("num", numeroCompte);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public List<Account> findByClientId(Long clientId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Account> query = em.createQuery(
                    "SELECT a FROM Account a WHERE a.client.id = :clientId ORDER BY a.dateOuverture DESC",
                    Account.class);
            query.setParameter("clientId", clientId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // Version corrigée : charge le client en même temps (LEFT JOIN FETCH)
    public List<Account> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT a FROM Account a LEFT JOIN FETCH a.client ORDER BY a.dateOuverture DESC",
                    Account.class).getResultList();
        } finally {
            em.close();
        }
    }

    public boolean existsByNumeroCompte(String numeroCompte) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Long count = em.createQuery(
                            "SELECT COUNT(a) FROM Account a WHERE a.numeroCompte = :num", Long.class)
                    .setParameter("num", numeroCompte)
                    .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }

    public long countAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT COUNT(a) FROM Account a", Long.class).getSingleResult();
        } finally {
            em.close();
        }
    }

    public java.math.BigDecimal sumAllSoldes() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT COALESCE(SUM(a.solde), 0) FROM Account a", java.math.BigDecimal.class)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }
} 