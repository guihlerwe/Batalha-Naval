package com.batalha.dao;

import com.batalha.model.Navio;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public class NavioDAO {
    
    public void salvar(Navio navio) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(navio);
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
    
    public Navio buscar(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Navio.class, id);
        } finally {
            em.close();
        }
    }
    
    public void atualizar(Navio navio) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(navio);
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
    
    public void remover(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Navio navio = em.find(Navio.class, id);
            if (navio != null) {
                em.remove(navio);
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
    
    public List<Navio> listarTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Navio> query = em.createQuery("SELECT n FROM Navio n", Navio.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}