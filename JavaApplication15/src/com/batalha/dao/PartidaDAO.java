/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.batalha.dao;

import com.batalha.model.Partida;
import javax.persistence.EntityManager;

public class PartidaDAO {
    public void salvar(Partida partida) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.persist(partida);
        em.getTransaction().commit();
        em.close();
    }
    public Partida buscar(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        Partida partida = em.find(Partida.class, id);
        em.close();
        return partida;
    }
}