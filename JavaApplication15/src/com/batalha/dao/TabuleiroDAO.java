package com.batalha.dao;

import com.batalha.model.Tabuleiro;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public class TabuleiroDAO {
    
    public void salvar(Tabuleiro tabuleiro) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(tabuleiro);
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
    
    public Tabuleiro buscar(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Tabuleiro.class, id);
        } finally {
            em.close();
        }
    }
    
    public void atualizar(Tabuleiro tabuleiro) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(tabuleiro);
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
            Tabuleiro tabuleiro = em.find(Tabuleiro.class, id);
            if (tabuleiro != null) {
                em.remove(tabuleiro);
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
    
    public List<Tabuleiro> listarTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Tabuleiro> query = em.createQuery("SELECT t FROM Tabuleiro t", Tabuleiro.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}