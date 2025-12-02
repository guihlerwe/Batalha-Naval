package com.batalha.dao;

import com.batalha.model.Jogador;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public class JogadorDAO {
    
    public void salvar(Jogador jogador) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(jogador);
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
    
    public Jogador buscar(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Jogador.class, id);
        } finally {
            em.close();
        }
    }
    
    public void atualizar(Jogador jogador) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(jogador);
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
            Jogador jogador = em.find(Jogador.class, id);
            if (jogador != null) {
                em.remove(jogador);
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
    
    public List<Jogador> listarTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Jogador> query = em.createQuery("SELECT j FROM Jogador j", Jogador.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public Jogador buscarPorNome(String nome) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Jogador> query = em.createQuery(
                "SELECT j FROM Jogador j WHERE j.nome = :nome", Jogador.class);
            query.setParameter("nome", nome);
            List<Jogador> resultados = query.getResultList();
            return resultados.isEmpty() ? null : resultados.get(0);
        } finally {
            em.close();
        }
    }
}