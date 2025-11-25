/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.batalha.model;

import javax.persistence.*;

@Entity
public class Jogada {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int linha;
    private int coluna;
    // getters e setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public int getLinha() { return linha; }
    public void setLinha(int linha) { this.linha = linha; }
    public int getColuna() { return coluna; }
    public void setColuna(int coluna) { this.coluna = coluna; }
}
