package com.batalha.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "navio")
public class Navio implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String tipo;
    
    @Column(nullable = false)
    private int tamanho;
    
    @Column(name = "linha_inicial")
    private int linhaInicial;
    
    @Column(name = "coluna_inicial")
    private int colunaInicial;
    
    @Column(nullable = false)
    private String orientacao;
    
    @Column(name = "partes_atingidas")
    private int partesAtingidas = 0;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tabuleiro_id", nullable = false)
    private Tabuleiro tabuleiro;
    
    public Navio() {}
    
    public Navio(String tipo, int linhaInicial, int colunaInicial, String orientacao) {
        this.tipo = tipo;
        this.linhaInicial = linhaInicial;
        this.colunaInicial = colunaInicial;
        this.orientacao = orientacao;
        this.tamanho = calcularTamanho(tipo);
    }
    
    private int calcularTamanho(String tipo) {
        switch (tipo) {
            case "PORTA_AVIOES": return 5;
            case "CRUZADOR": return 4;
            case "CONTRATORPEDEIRO": return 3;
            case "SUBMARINO": return 2;
            default: return 1;
        }
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { 
        this.tipo = tipo;
        this.tamanho = calcularTamanho(tipo);
    }
    
    public int getTamanho() { return tamanho; }
    public void setTamanho(int tamanho) { this.tamanho = tamanho; }
    
    public int getLinhaInicial() { return linhaInicial; }
    public void setLinhaInicial(int linhaInicial) { this.linhaInicial = linhaInicial; }
    
    public int getColunaInicial() { return colunaInicial; }
    public void setColunaInicial(int colunaInicial) { this.colunaInicial = colunaInicial; }
    
    public String getOrientacao() { return orientacao; }
    public void setOrientacao(String orientacao) { this.orientacao = orientacao; }
    
    public int getPartesAtingidas() { return partesAtingidas; }
    public void setPartesAtingidas(int partesAtingidas) { this.partesAtingidas = partesAtingidas; }
    
    public Tabuleiro getTabuleiro() { return tabuleiro; }
    public void setTabuleiro(Tabuleiro tabuleiro) { this.tabuleiro = tabuleiro; }
    
    public void receberAtaque() {
        partesAtingidas++;
    }
    
    public boolean estaAfundado() {
        return partesAtingidas >= tamanho;
    }
    
    public boolean ocupaPosicao(int linha, int coluna) {
        if (orientacao.equals("HORIZONTAL")) {
            return linha == linhaInicial && coluna >= colunaInicial && coluna < colunaInicial + tamanho;
        } else {
            return coluna == colunaInicial && linha >= linhaInicial && linha < linhaInicial + tamanho;
        }
    }
}