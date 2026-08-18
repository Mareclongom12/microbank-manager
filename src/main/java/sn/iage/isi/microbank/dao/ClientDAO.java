package sn.iage.isi.microbank.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import sn.iage.isi.microbank.model.Client;
import sn.iage.isi.microbank.util.JPAUtil;

import java.util.List;

public class ClientDAO {

    public void save(Client client) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(client);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public void update(Client client) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(client);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public void delete(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Client client = em.find(Client.class, id);
            if (client != null) {
                em.remove(client);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    // Version corrigée : force le chargement de la collection "accounts"
    // avant de fermer l'EntityManager, pour éviter la LazyInitializationException
    public Client findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Client client = em.find(Client.class, id);
            if (client != null) {
                client.getAccounts().size();
            }
            return client;
        } finally {
            em.close();
        }
    }

    public List<Client> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Client> query = em.createQuery(
                    "SELECT c FROM Client c ORDER BY c.nom, c.prenom", Client.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // Recherche + pagination (nom, prénom, téléphone, numéro de pièce)
    public List<Client> search(String keyword, int page, int size) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT c FROM Client c WHERE "
                    + "LOWER(c.nom) LIKE :kw OR LOWER(c.prenom) LIKE :kw "
                    + "OR c.telephone LIKE :kw OR c.numeroPiece LIKE :kw "
                    + "ORDER BY c.nom, c.prenom";

            TypedQuery<Client> query = em.createQuery(jpql, Client.class);
            query.setParameter("kw", "%" + keyword.toLowerCase() + "%");
            query.setFirstResult(page * size);
            query.setMaxResults(size);

            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public long countSearch(String keyword) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT COUNT(c) FROM Client c WHERE "
                    + "LOWER(c.nom) LIKE :kw OR LOWER(c.prenom) LIKE :kw "
                    + "OR c.telephone LIKE :kw OR c.numeroPiece LIKE :kw";

            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            query.setParameter("kw", "%" + keyword.toLowerCase() + "%");

            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    // Pagination simple sans recherche (liste complète paginée)
    public List<Client> findPaginated(int page, int size) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Client> query = em.createQuery(
                    "SELECT c FROM Client c ORDER BY c.nom, c.prenom", Client.class);
            query.setFirstResult(page * size);
            query.setMaxResults(size);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public long count() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT COUNT(c) FROM Client c", Long.class)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }

    public boolean existsByNumeroPiece(String numeroPiece) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Long count = em.createQuery(
                            "SELECT COUNT(c) FROM Client c WHERE c.numeroPiece = :num", Long.class)
                    .setParameter("num", numeroPiece)
                    .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }
}