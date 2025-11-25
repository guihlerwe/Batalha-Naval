/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.batalha.cliente;

import com.batalha.common.InterfaceServidor;
import com.batalha.common.ConfiguracaoRMI;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClienteRMI {
    public InterfaceServidor conectar() throws Exception {
        Registry registry = LocateRegistry.getRegistry(ConfiguracaoRMI.HOST, ConfiguracaoRMI.PORTA);
        return (InterfaceServidor) registry.lookup(ConfiguracaoRMI.NOME_SERVICO);
    }
}
