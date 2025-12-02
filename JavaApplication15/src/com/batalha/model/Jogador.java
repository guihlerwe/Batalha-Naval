package com.batalha.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "jogador")
public class Jogador implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nome;
    
    @Column(name = "pronto_para_jogar")
    private boolean prontoParaJogar = false;
    
    @OneToOne(mappedBy = "jogador", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Tabuleiro tabuleiro;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partida_id")
    private Partida partida;
    
    public Jogador() {}
    
    public Jogador(String nome) {
        this.nome = nome;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public boolean isProntoParaJogar() { return prontoParaJogar; }
    public void setProntoParaJogar(boolean prontoParaJogar) { this.prontoParaJogar = prontoParaJogar; }
    
    public Tabuleiro getTabuleiro() { return tabuleiro; }
    public void setTabuleiro(Tabuleiro tabuleiro) { this.tabuleiro = tabuleiro; }
    
    public Partida getPartida() { return partida; }
    public void setPartida(Partida partida) { this.partida = partida; }
}