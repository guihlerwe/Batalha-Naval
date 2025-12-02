package com.batalha.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "partida")
public class Partida implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String status;
    
    @Column(name = "tamanho_tabuleiro")
    private int tamanhoTabuleiro = 10;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_inicio")
    private Date dataInicio;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_fim")
    private Date dataFim;
    
    @Column(name = "turno_jogador_id")
    private Long turnoJogadorId;
    
    @OneToMany(mappedBy = "partida", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Jogador> jogadores = new ArrayList<>();
    
    @OneToMany(mappedBy = "partida", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Jogada> jogadas = new ArrayList<>();
    
    @Column(name = "vencedor_id")
    private Long vencedorId;
    
    public Partida() {
        this.status = "AGUARDANDO";
        this.dataInicio = new Date();
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public int getTamanhoTabuleiro() { return tamanhoTabuleiro; }
    public void setTamanhoTabuleiro(int tamanhoTabuleiro) { this.tamanhoTabuleiro = tamanhoTabuleiro; }
    
    public Date getDataInicio() { return dataInicio; }
    public void setDataInicio(Date dataInicio) { this.dataInicio = dataInicio; }
    
    public Date getDataFim() { return dataFim; }
    public void setDataFim(Date dataFim) { this.dataFim = dataFim; }
    
    public Long getTurnoJogadorId() { return turnoJogadorId; }
    public void setTurnoJogadorId(Long turnoJogadorId) { this.turnoJogadorId = turnoJogadorId; }
    
    public List<Jogador> getJogadores() { return jogadores; }
    public void setJogadores(List<Jogador> jogadores) { this.jogadores = jogadores; }
    
    public List<Jogada> getJogadas() { return jogadas; }
    public void setJogadas(List<Jogada> jogadas) { this.jogadas = jogadas; }
    
    public Long getVencedorId() { return vencedorId; }
    public void setVencedorId(Long vencedorId) { this.vencedorId = vencedorId; }
    
    public void adicionarJogador(Jogador jogador) {
        jogadores.add(jogador);
        jogador.setPartida(this);
    }
    
    public void adicionarJogada(Jogada jogada) {
        jogadas.add(jogada);
        jogada.setPartida(this);
    }
    
    public boolean estaCheia() {
        return jogadores.size() >= 2;
    }
    
    public void iniciar() {
        if (estaCheia() && jogadores.get(0).isProntoParaJogar() && jogadores.get(1).isProntoParaJogar()) {
            this.status = "EM_ANDAMENTO";
            this.turnoJogadorId = jogadores.get(0).getId();
        }
    }
    
    public void finalizar(Long vencedorId) {
        this.status = "FINALIZADA";
        this.vencedorId = vencedorId;
        this.dataFim = new Date();
    }
}