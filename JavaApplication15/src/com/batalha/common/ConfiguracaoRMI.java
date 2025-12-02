package com.batalha.common;

import java.net.InetAddress;

public class ConfiguracaoRMI {
    public static final int PORTA = 1099;
    public static final String NOME_SERVICO = "BatalhaNavalServidor";
    
    private static String HOST = "localhost";
    
    public static String getHost() {
        return HOST;
    }
    
    public static void setHost(String host) {
        HOST = host;
    }
    
    public static String getLocalIP() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "localhost";
        }
    }
}
