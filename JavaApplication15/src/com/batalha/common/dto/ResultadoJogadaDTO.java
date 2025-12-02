package com.batalha.common.dto;

import java.io.Serializable;

public class ResultadoJogadaDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String resultado; 
    private boolean fimDeJogo;
    private Long vencedorId;
    private String mensagem;
    
    public ResultadoJogadaDTO() {}
    
    public ResultadoJogadaDTO(String resultado, boolean fimDeJogo, Long vencedorId, String mensagem) {
        this.resultado = resultado;
        this.fimDeJogo = fimDeJogo;
        this.vencedorId = vencedorId;
        this.mensagem = mensagem;
    }
    
    public String getResultado() { return resultado; }
    public void setResultado(String resultado) { this.resultado = resultado; }
    
    public boolean isFimDeJogo() { return fimDeJogo; }
    public void setFimDeJogo(boolean fimDeJogo) { this.fimDeJogo = fimDeJogo; }
    
    public Long getVencedorId() { return vencedorId; }
    public void setVencedorId(Long vencedorId) { this.vencedorId = vencedorId; }
    
    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }
}
