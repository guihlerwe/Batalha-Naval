package com.batalha.common;

import com.batalha.common.dto.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface InterfaceServidor extends Remote {
    
    
    JogadorDTO conectarJogador(String nome) throws RemoteException;
    void desconectarJogador(Long jogadorId) throws RemoteException;
    
    PartidaDTO criarPartida(int tamanhoTabuleiro) throws RemoteException;
    PartidaDTO entrarNaPartida(Long jogadorId, Long partidaId) throws RemoteException;
    List<PartidaDTO> listarPartidasDisponiveis() throws RemoteException;
    PartidaDTO obterEstadoPartida(Long partidaId) throws RemoteException;
    void cancelarPartida(Long jogadorId, Long partidaId) throws RemoteException;
    
    boolean posicionarNavios(Long jogadorId, List<NavioDTO> navios) throws RemoteException;
    void confirmarPronto(Long jogadorId) throws RemoteException;
    
    ResultadoJogadaDTO realizarJogada(Long jogadorId, int linha, int coluna) throws RemoteException;
    boolean ehMeuTurno(Long jogadorId) throws RemoteException;
    
    String obterMatrizAdversario(Long jogadorId) throws RemoteException;
    String obterMinhaMatriz(Long jogadorId) throws RemoteException;
    
    boolean validarPosicaoNavio(Long jogadorId, NavioDTO navio) throws RemoteException;
}
