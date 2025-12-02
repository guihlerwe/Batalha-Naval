/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.batalha.cliente;

import com.batalha.common.ConfiguracaoRMI;
import com.batalha.common.InterfaceServidor;
import com.batalha.common.dto.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class ClienteRMI {
    
    private InterfaceServidor servidor;
    private JogadorDTO jogadorAtual;
    private PartidaDTO partidaAtual;
    
    public boolean conectar() {
        try {
            Registry registry = LocateRegistry.getRegistry(ConfiguracaoRMI.HOST, ConfiguracaoRMI.PORTA);
            servidor = (InterfaceServidor) registry.lookup(ConfiguracaoRMI.NOME_SERVICO);
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao conectar ao servidor: " + e.getMessage());
            return false;
        }
    }
    
    public JogadorDTO conectarJogador(String nome) throws Exception {
        if (servidor == null) {
            throw new Exception("Não conectado ao servidor");
        }
        jogadorAtual = servidor.conectarJogador(nome);
        return jogadorAtual;
    }
    
    public void desconectar() {
        try {
            if (servidor != null && jogadorAtual != null) {
                servidor.desconectarJogador(jogadorAtual.getId());
            }
        } catch (Exception e) {
            System.err.println("Erro ao desconectar: " + e.getMessage());
        }
    }
    
    public PartidaDTO criarPartida(int tamanhoTabuleiro) throws Exception {
        if (servidor == null) {
            throw new Exception("Não conectado ao servidor");
        }
        System.out.println("Criando partida com tamanho: " + tamanhoTabuleiro);
        PartidaDTO partida = servidor.criarPartida(tamanhoTabuleiro);
        System.out.println("Partida criada com ID: " + partida.getId() + ", entrando na partida...");
        PartidaDTO partidaAtualizada = entrarNaPartida(partida.getId());
        System.out.println("Entrou na partida. Tamanho do tabuleiro: " + partidaAtualizada.getTamanhoTabuleiro());
        return partidaAtualizada;
    }
    
    public PartidaDTO entrarNaPartida(Long partidaId) throws Exception {
        if (servidor == null || jogadorAtual == null) {
            throw new Exception("Jogador não conectado");
        }
        System.out.println("Entrando na partida ID: " + partidaId);
        partidaAtual = servidor.entrarNaPartida(jogadorAtual.getId(), partidaId);
        System.out.println("partidaAtual atualizada: ID=" + partidaAtual.getId() + ", Tamanho=" + partidaAtual.getTamanhoTabuleiro());
        return partidaAtual;
    }
    
    public List<PartidaDTO> listarPartidasDisponiveis() throws Exception {
        if (servidor == null) {
            throw new Exception("Não conectado ao servidor");
        }
        return servidor.listarPartidasDisponiveis();
    }
    
    public PartidaDTO obterEstadoPartida() throws Exception {
        if (servidor == null || partidaAtual == null) {
            throw new Exception("Partida não iniciada");
        }
        partidaAtual = servidor.obterEstadoPartida(partidaAtual.getId());
        return partidaAtual;
    }
    
    public boolean posicionarNavios(List<NavioDTO> navios) throws Exception {
        if (servidor == null || jogadorAtual == null) {
            throw new Exception("Jogador não conectado");
        }
        return servidor.posicionarNavios(jogadorAtual.getId(), navios);
    }
    
    public void confirmarPronto() throws Exception {
        if (servidor == null || jogadorAtual == null) {
            throw new Exception("Jogador não conectado");
        }
        servidor.confirmarPronto(jogadorAtual.getId());
    }
    
    public boolean validarPosicaoNavio(NavioDTO navio) throws Exception {
        if (servidor == null || jogadorAtual == null) {
            throw new Exception("Jogador não conectado");
        }
        return servidor.validarPosicaoNavio(jogadorAtual.getId(), navio);
    }
    
    public ResultadoJogadaDTO realizarJogada(int linha, int coluna) throws Exception {
        if (servidor == null || jogadorAtual == null) {
            throw new Exception("Jogador não conectado");
        }
        ResultadoJogadaDTO resultado = servidor.realizarJogada(jogadorAtual.getId(), linha, coluna);
        return resultado;
    }
    
    public boolean ehMeuTurno() throws Exception {
        if (servidor == null || jogadorAtual == null) {
            throw new Exception("Jogador não conectado");
        }
        return servidor.ehMeuTurno(jogadorAtual.getId());
    }
    
    public ResultadoJogadaDTO atacar(int linha, int coluna) throws Exception {
        return realizarJogada(linha, coluna);
    }
    
    public boolean[][] obterPosicoesNavios() throws Exception {
        if (servidor == null || jogadorAtual == null) {
            throw new Exception("Jogador não conectado");
        }
        
        // Obter tamanho do tabuleiro da partida
        int tamanho = partidaAtual != null ? partidaAtual.getTamanhoTabuleiro() : 10;
        
        // Obter matriz do servidor
        String matriz = servidor.obterMinhaMatriz(jogadorAtual.getId());
        boolean[][] posicoes = new boolean[tamanho][tamanho];
        
        if (matriz != null && !matriz.isEmpty()) {
            String[] linhas = matriz.split("\\|");
            for (int i = 0; i < Math.min(linhas.length, tamanho); i++) {
                for (int j = 0; j < Math.min(linhas[i].length(), tamanho); j++) {
                    // '1' indica que tem navio
                    posicoes[i][j] = linhas[i].charAt(j) == '1' || linhas[i].charAt(j) == '3';
                }
            }
        }
        
        return posicoes;
    }
    
    public String[][] obterAtaquesRecebidos() throws Exception {
        if (servidor == null || jogadorAtual == null) {
            throw new Exception("Jogador não conectado");
        }
        
        // Obter tamanho do tabuleiro da partida
        int tamanho = partidaAtual != null ? partidaAtual.getTamanhoTabuleiro() : 10;
        
        // Obter matriz do servidor
        String matriz = servidor.obterMinhaMatriz(jogadorAtual.getId());
        String[][] ataques = new String[tamanho][tamanho];
        
        if (matriz != null && !matriz.isEmpty()) {
            String[] linhas = matriz.split("\\|");
            for (int i = 0; i < Math.min(linhas.length, tamanho); i++) {
                for (int j = 0; j < Math.min(linhas[i].length(), tamanho); j++) {
                    char celula = linhas[i].charAt(j);
                    if (celula == '2') {
                        ataques[i][j] = "AGUA";
                    } else if (celula == '3') {
                        ataques[i][j] = "ACERTO";
                    }
                }
            }
        }
        
        return ataques;
    }
    
    public String obterMatrizAdversario() throws Exception {
        if (servidor == null || jogadorAtual == null) {
            throw new Exception("Jogador não conectado");
        }
        return servidor.obterMatrizAdversario(jogadorAtual.getId());
    }
    
    public String obterMinhaMatriz() throws Exception {
        if (servidor == null || jogadorAtual == null) {
            throw new Exception("Jogador não conectado");
        }
        return servidor.obterMinhaMatriz(jogadorAtual.getId());
    }
    
    public JogadorDTO getJogadorAtual() {
        return jogadorAtual;
    }
    
    public PartidaDTO getPartidaAtual() {
        return partidaAtual;
    }
    
    public InterfaceServidor getServidor() {
        return servidor;
    }
    
    public boolean isConectado() {
        return servidor != null && jogadorAtual != null;
    }
    
    public boolean temPartida() {
        return partidaAtual != null;
    }
}
