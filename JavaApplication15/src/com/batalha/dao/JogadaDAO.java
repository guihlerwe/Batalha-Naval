package com.batalha.dao;

import com.batalha.model.Jogada;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public class JogadaDAO {
    
    public void salvar(Jogada jogada) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(jogada);
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
    
    public Jogada buscar(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Jogada.class, id);
        } finally {
            em.close();
        }
    }
    
    public void atualizar(Jogada jogada) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(jogada);
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
            Jogada jogada = em.find(Jogada.class, id);
            if (jogada != null) {
                em.remove(jogada);
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
    
    public List<Jogada> listarTodas() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Jogada> query = em.createQuery("SELECT j FROM Jogada j", Jogada.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public List<Jogada> buscarPorPartida(Long partidaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Jogada> query = em.createQuery(
                "SELECT j FROM Jogada j WHERE j.partida.id = :partidaId ORDER BY j.dataJogada", Jogada.class);
            query.setParameter("partidaId", partidaId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}