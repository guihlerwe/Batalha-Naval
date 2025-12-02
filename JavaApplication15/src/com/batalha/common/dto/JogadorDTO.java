package com.batalha.common.dto;

import java.io.Serializable;

public class JogadorDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String nome;
    private boolean prontoParaJogar;
    
    public JogadorDTO() {}
    
    public JogadorDTO(Long id, String nome, boolean prontoParaJogar) {
        this.id = id;
        this.nome = nome;
        this.prontoParaJogar = prontoParaJogar;
    }
    
    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public boolean isProntoParaJogar() { return prontoParaJogar; }
    public void setProntoParaJogar(boolean prontoParaJogar) { this.prontoParaJogar = prontoParaJogar; }
}
