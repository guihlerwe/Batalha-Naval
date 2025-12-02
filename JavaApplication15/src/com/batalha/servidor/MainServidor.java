package com.batalha.servidor;

import com.batalha.common.ConfiguracaoRMI;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class MainServidor {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.createRegistry(ConfiguracaoRMI.PORTA);
            ServidorRMI servidor = new ServidorRMI();
            registry.rebind(ConfiguracaoRMI.NOME_SERVICO, servidor);
            System.out.println("Servidor iniciado com sucesso!");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
