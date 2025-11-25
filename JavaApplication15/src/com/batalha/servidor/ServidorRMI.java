/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.batalha.servidor;

import com.batalha.common.InterfaceServidor;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;

public class ServidorRMI extends UnicastRemoteObject implements InterfaceServidor {
    public ServidorRMI() throws RemoteException { super(); }
    public String conectarJogador(String nome) throws RemoteException {
        // Implemente aqui a lógica de adicionar o jogador e retornar um ID
        return "jogador_" + nome;
    }
}
