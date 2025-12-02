package com.batalha.common.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PartidaDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String status;
    private int tamanhoTabuleiro;
    private Long turnoJogadorId;
    private List<JogadorDTO> jogadores = new ArrayList<>();
    private Long vencedorId;
    
    public PartidaDTO() {}
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public int getTamanhoTabuleiro() { return tamanhoTabuleiro; }
    public void setTamanhoTabuleiro(int tamanhoTabuleiro) { this.tamanhoTabuleiro = tamanhoTabuleiro; }
    
    public Long getTurnoJogadorId() { return turnoJogadorId; }
    public void setTurnoJogadorId(Long turnoJogadorId) { this.turnoJogadorId = turnoJogadorId; }
    
    public List<JogadorDTO> getJogadores() { return jogadores; }
    public void setJogadores(List<JogadorDTO> jogadores) { this.jogadores = jogadores; }
    
    public Long getVencedorId() { return vencedorId; }
    public void setVencedorId(Long vencedorId) { this.vencedorId = vencedorId; }
}
