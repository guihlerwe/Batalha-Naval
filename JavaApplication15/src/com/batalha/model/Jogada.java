package com.batalha.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "jogada")
public class Jogada implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private int linha;
    
    @Column(nullable = false)
    private int coluna;
    
    @Column(nullable = false)
    private String resultado;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_jogada")
    private Date dataJogada;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partida_id", nullable = false)
    private Partida partida;
    
    @Column(name = "jogador_id", nullable = false)
    private Long jogadorId;
    
    @Column(name = "jogador_alvo_id", nullable = false)
    private Long jogadorAlvoId;
    
    public Jogada() {
        this.dataJogada = new Date();
    }
    
    public Jogada(int linha, int coluna, Long jogadorId, Long jogadorAlvoId) {
        this.linha = linha;
        this.coluna = coluna;
        this.jogadorId = jogadorId;
        this.jogadorAlvoId = jogadorAlvoId;
        this.dataJogada = new Date();
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public int getLinha() { return linha; }
    public void setLinha(int linha) { this.linha = linha; }
    
    public int getColuna() { return coluna; }
    public void setColuna(int coluna) { this.coluna = coluna; }
    
    public String getResultado() { return resultado; }
    public void setResultado(String resultado) { this.resultado = resultado; }
    
    public Date getDataJogada() { return dataJogada; }
    public void setDataJogada(Date dataJogada) { this.dataJogada = dataJogada; }
    
    public Partida getPartida() { return partida; }
    public void setPartida(Partida partida) { this.partida = partida; }
    
    public Long getJogadorId() { return jogadorId; }
    public void setJogadorId(Long jogadorId) { this.jogadorId = jogadorId; }
    
    public Long getJogadorAlvoId() { return jogadorAlvoId; }
    public void setJogadorAlvoId(Long jogadorAlvoId) { this.jogadorAlvoId = jogadorAlvoId; }
}