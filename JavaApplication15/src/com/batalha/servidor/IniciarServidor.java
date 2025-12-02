package com.batalha.servidor;

import com.batalha.common.ConfiguracaoRMI;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class IniciarServidor {
    public static void main(String[] args) {
        try {
            String localIP = ConfiguracaoRMI.getLocalIP();
            System.setProperty("java.rmi.server.hostname", localIP);
            
            ServidorRMI servidor = new ServidorRMI();
            
            Registry registry = LocateRegistry.createRegistry(ConfiguracaoRMI.PORTA);
            
            registry.rebind(ConfiguracaoRMI.NOME_SERVICO, servidor);
            
            System.out.println("========================================");
            System.out.println("  SERVIDOR BATALHA NAVAL INICIADO");
            System.out.println("========================================");
            System.out.println("Porta: " + ConfiguracaoRMI.PORTA);
            System.out.println("IP Local: " + localIP);
            System.out.println();
            System.out.println("Para conectar de outro computador:");
            System.out.println("  1. Use o IP: " + localIP);
            System.out.println("  2. Certifique-se de estar na mesma rede Wi-Fi");
            System.out.println("========================================");
            System.out.println("Aguardando conexões...");
            System.out.println();
            
        } catch (Exception e) {
            System.err.println("Erro ao iniciar servidor:");
            e.printStackTrace();
        }
    }
}
