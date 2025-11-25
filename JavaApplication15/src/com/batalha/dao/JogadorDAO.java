/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.batalha.dao;

import com.batalha.model.Jogador;
import javax.persistence.EntityManager;

public class JogadorDAO {
    public void salvar(Jogador jogador) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.persist(jogador);
        em.getTransaction().commit();
        em.close();
    }
    public Jogador buscar(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        Jogador jogador = em.find(Jogador.class, id);
        em.close();
        return jogador;
    }
}
