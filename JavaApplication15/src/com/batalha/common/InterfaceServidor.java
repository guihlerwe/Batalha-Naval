/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.batalha.common;

/**
 *
 * @author aluno
 */
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InterfaceServidor extends Remote {
    String conectarJogador(String nome) throws RemoteException;
    // Adicione outros métodos conforme necessário para o jogo
}
