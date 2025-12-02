package com.batalha.util;

import com.batalha.dao.*;
import com.batalha.model.*;

public class GerenciadorPartida {
    
    private PartidaDAO partidaDAO;
    private JogadorDAO jogadorDAO;
    private TabuleiroDAO tabuleiroDAO;
    private NavioDAO navioDAO;
    private JogadaDAO jogadaDAO;
    
    public GerenciadorPartida() {
        this.partidaDAO = new PartidaDAO();
        this.jogadorDAO = new JogadorDAO();
        this.tabuleiroDAO = new TabuleiroDAO();
        this.navioDAO = new NavioDAO();
        this.jogadaDAO = new JogadaDAO();
    }
    
    /**
     * Processa uma jogada e retorna o resultado
     */
    public String processarJogada(Partida partida, Jogador jogadorAtacante, 
                                   int linha, int coluna) {
        // Encontrar o jogador adversário
        Jogador adversario = null;
        for (Jogador j : partida.getJogadores()) {
            if (!j.getId().equals(jogadorAtacante.getId())) {
                adversario = j;
                break;
            }
        }
        
        if (adversario == null) {
            return "ERRO";
        }
        
        Tabuleiro tabuleiroAdversario = adversario.getTabuleiro();
        
        // Verificar se acertou algum navio
        Navio navioAtingido = null;
        for (Navio navio : tabuleiroAdversario.getNavios()) {
            if (navio.ocupaPosicao(linha, coluna)) {
                navioAtingido = navio;
                break;
            }
        }
        
        String resultado;
        if (navioAtingido != null) {
            // Acertou um navio
            navioAtingido.receberAtaque();
            navioDAO.atualizar(navioAtingido);
            
            tabuleiroAdversario.atualizarCelula(linha, coluna, "3");
            
            if (navioAtingido.estaAfundado()) {
                resultado = "AFUNDOU";
            } else {
                resultado = "ACERTO";
            }
        } else {
            // Errou - água
            tabuleiroAdversario.atualizarCelula(linha, coluna, "2");
            resultado = "AGUA";
        }
        
        tabuleiroDAO.atualizar(tabuleiroAdversario);
        
        // Registrar jogada
        Jogada jogada = new Jogada(linha, coluna, jogadorAtacante.getId(), adversario.getId());
        jogada.setResultado(resultado);
        partida.adicionarJogada(jogada);
        jogadaDAO.salvar(jogada);
        
        return resultado;
    }
    
    /**
     * Verifica se a partida terminou
     */
    public boolean verificarFimDeJogo(Partida partida) {
        for (Jogador jogador : partida.getJogadores()) {
            if (ValidadorJogo.todosNaviosAfundados(jogador.getTabuleiro())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Determina o vencedor da partida
     */
    public Long determinarVencedor(Partida partida) {
        for (Jogador jogador : partida.getJogadores()) {
            if (!ValidadorJogo.todosNaviosAfundados(jogador.getTabuleiro())) {
                return jogador.getId();
            }
        }
        return null;
    }
    
    /**
     * Alterna o turno entre os jogadores
     */
    public void alternarTurno(Partida partida) {
        Long turnoAtual = partida.getTurnoJogadorId();
        
        for (Jogador jogador : partida.getJogadores()) {
            if (!jogador.getId().equals(turnoAtual)) {
                partida.setTurnoJogadorId(jogador.getId());
                break;
            }
        }
        
        partidaDAO.atualizar(partida);
    }
    
    /**
     * Verifica se é válida a jogada na posição
     */
    public boolean posicaoJaAtacada(Tabuleiro tabuleiro, int linha, int coluna) {
        String celula = tabuleiro.obterCelula(linha, coluna);
        return celula.equals("2") || celula.equals("3");
    }
}
