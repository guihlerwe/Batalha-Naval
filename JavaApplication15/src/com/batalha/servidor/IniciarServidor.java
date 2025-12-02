package com.batalha.servidor;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class IniciarServidor {
    public static void main(String[] args) {
        try {
            // Criar o servidor RMI
            ServidorRMI servidor = new ServidorRMI();
            
            // Criar o registro na porta 1099
            Registry registry = LocateRegistry.createRegistry(1099);
            
            // Registrar o servidor com o nome "BatalhaNavalServidor"
            registry.rebind("BatalhaNavalServidor", servidor);
            
            System.out.println("Servidor Batalha Naval iniciado na porta 1099");
            System.out.println("Aguardando conexões...");
            
        } catch (Exception e) {
            System.err.println("Erro ao iniciar servidor:");
            e.printStackTrace();
        }
    }
}
