package com.batalha.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tabuleiro")
public class Tabuleiro implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private int tamanho = 10;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jogador_id", nullable = false, unique = true)
    private Jogador jogador;
    
    @OneToMany(mappedBy = "tabuleiro", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Navio> navios = new ArrayList<>();
    
    @Column(columnDefinition = "TEXT")
    private String matriz;
    
    public Tabuleiro() {
        inicializarMatriz();
    }
    
    public Tabuleiro(Jogador jogador) {
        this.jogador = jogador;
        inicializarMatriz();
    }
    
    public void inicializarMatriz() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tamanho * tamanho; i++) {
            sb.append("0");
            if ((i + 1) % tamanho == 0 && i < tamanho * tamanho - 1) {
                sb.append("|");
            }
        }
        this.matriz = sb.toString();
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public int getTamanho() { return tamanho; }
    public void setTamanho(int tamanho) { this.tamanho = tamanho; }
    
    public Jogador getJogador() { return jogador; }
    public void setJogador(Jogador jogador) { this.jogador = jogador; }
    
    public List<Navio> getNavios() { return navios; }
    public void setNavios(List<Navio> navios) { this.navios = navios; }
    
    public String getMatriz() { return matriz; }
    public void setMatriz(String matriz) { this.matriz = matriz; }
    
    public void adicionarNavio(Navio navio) {
        navios.add(navio);
        navio.setTabuleiro(this);
    }
    
    public boolean todosNaviosAfundados() {
        if (navios == null || navios.isEmpty()) {
            return false;
        }
        for (Navio navio : navios) {
            if (!navio.estaAfundado()) {
                return false;
            }
        }
        return true;
    }
    
    public void atualizarCelula(int linha, int coluna, String valor) {
        String[] linhas = matriz.split("\\|");
        StringBuilder novaLinha = new StringBuilder(linhas[linha]);
        novaLinha.setCharAt(coluna, valor.charAt(0));
        linhas[linha] = novaLinha.toString();
        this.matriz = String.join("|", linhas);
    }
    
    public String obterCelula(int linha, int coluna) {
        String[] linhas = matriz.split("\\|");
        return String.valueOf(linhas[linha].charAt(coluna));
    }
}