/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.batalha.dao;

import com.batalha.model.Jogada;
import javax.persistence.EntityManager;

public class JogadaDAO {
    public void salvar(Jogada j) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.persist(j);
        em.getTransaction().commit();
        em.close();
    }
}
