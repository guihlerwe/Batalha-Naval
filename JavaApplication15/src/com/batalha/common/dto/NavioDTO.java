package com.batalha.common.dto;

import java.io.Serializable;

public class NavioDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String tipo;
    private int linhaInicial;
    private int colunaInicial;
    private String orientacao;
    
    public NavioDTO() {}
    
    public NavioDTO(String tipo, int linhaInicial, int colunaInicial, String orientacao) {
        this.tipo = tipo;
        this.linhaInicial = linhaInicial;
        this.colunaInicial = colunaInicial;
        this.orientacao = orientacao;
    }
    
    // Getters e Setters
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    
    public int getLinhaInicial() { return linhaInicial; }
    public void setLinhaInicial(int linhaInicial) { this.linhaInicial = linhaInicial; }
    
    public int getColunaInicial() { return colunaInicial; }
    public void setColunaInicial(int colunaInicial) { this.colunaInicial = colunaInicial; }
    
    public String getOrientacao() { return orientacao; }
    public void setOrientacao(String orientacao) { this.orientacao = orientacao; }
}
