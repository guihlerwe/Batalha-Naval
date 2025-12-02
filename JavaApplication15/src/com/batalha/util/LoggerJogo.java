package com.batalha.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LoggerJogo {
    
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    public static void log(String mensagem) {
        System.out.println("[" + sdf.format(new Date()) + "] " + mensagem);
    }
    
    public static void logErro(String mensagem, Exception e) {
        System.err.println("[" + sdf.format(new Date()) + "] ERRO: " + mensagem);
        if (e != null) {
            e.printStackTrace();
        }
    }
    
    public static void logJogada(Long jogadorId, int linha, int coluna, String resultado) {
        log(String.format("Jogador %d atacou [%d,%d] - Resultado: %s", 
            jogadorId, linha, coluna, resultado));
    }
    
    public static void logPartida(Long partidaId, String acao) {
        log(String.format("Partida %d: %s", partidaId, acao));
    }
    
    public static void logJogador(Long jogadorId, String acao) {
        log(String.format("Jogador %d: %s", jogadorId, acao));
    }
}
