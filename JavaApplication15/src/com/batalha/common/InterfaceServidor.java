/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.batalha.common;

import com.batalha.common.dto.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface InterfaceServidor extends Remote {
    
    
    // Gerenciamento de Jogadores
    JogadorDTO conectarJogador(String nome) throws RemoteException;
    void desconectarJogador(Long jogadorId) throws RemoteException;
    
    // Gerenciamento de Partidas
    PartidaDTO criarPartida(int tamanhoTabuleiro) throws RemoteException;
    PartidaDTO entrarNaPartida(Long jogadorId, Long partidaId) throws RemoteException;
    List<PartidaDTO> listarPartidasDisponiveis() throws RemoteException;
    PartidaDTO obterEstadoPartida(Long partidaId) throws RemoteException;
    
    // Posicionamento de Navios
    boolean posicionarNavios(Long jogadorId, List<NavioDTO> navios) throws RemoteException;
    void confirmarPronto(Long jogadorId) throws RemoteException;
    
    // Jogadas
    ResultadoJogadaDTO realizarJogada(Long jogadorId, int linha, int coluna) throws RemoteException;
    boolean ehMeuTurno(Long jogadorId) throws RemoteException;
    
    // Sincronização
    String obterMatrizAdversario(Long jogadorId) throws RemoteException;
    String obterMinhaMatriz(Long jogadorId) throws RemoteException;
    
    // Utilitários
    boolean validarPosicaoNavio(Long jogadorId, NavioDTO navio) throws RemoteException;
}
