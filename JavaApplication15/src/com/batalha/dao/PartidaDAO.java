package com.batalha.dao;

import com.batalha.model.Partida;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public class PartidaDAO {
    
    public void salvar(Partida partida) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(partida);
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
    
    public Partida buscar(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Partida.class, id);
        } finally {
            em.close();
        }
    }
    
    public void atualizar(Partida partida) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(partida);
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
            Partida partida = em.find(Partida.class, id);
            if (partida != null) {
                em.remove(partida);
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
    
    public List<Partida> listarTodas() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Partida> query = em.createQuery("SELECT p FROM Partida p", Partida.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<Partida> buscarPartidasAguardando() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Partida> query = em.createQuery(
                "SELECT p FROM Partida p WHERE p.status = 'AGUARDANDO' ORDER BY p.dataInicio DESC", Partida.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<Partida> buscarPartidasEmAndamento() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Partida> query = em.createQuery(
                "SELECT p FROM Partida p WHERE p.status = 'EM_ANDAMENTO'", Partida.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}