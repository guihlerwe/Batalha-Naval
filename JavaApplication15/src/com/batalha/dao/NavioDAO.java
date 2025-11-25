/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.batalha.dao;

import com.batalha.model.Navio;
import javax.persistence.EntityManager;

public class NavioDAO {
    public void salvar(Navio n) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.persist(n);
        em.getTransaction().commit();
        em.close();
    }
}
