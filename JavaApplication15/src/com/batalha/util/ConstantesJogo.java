package com.batalha.util;

public class ConstantesJogo {
    
    // Tamanhos dos navios
    public static final int TAMANHO_PORTA_AVIOES = 5;
    public static final int TAMANHO_CRUZADOR = 4;
    public static final int TAMANHO_CONTRATORPEDEIRO = 3;
    public static final int TAMANHO_SUBMARINO = 2;
    
    // Quantidade de navios
    public static final int QTD_PORTA_AVIOES = 1;
    public static final int QTD_CRUZADORES = 2;
    public static final int QTD_CONTRATORPEDEIROS = 3;
    public static final int QTD_SUBMARINOS = 4;
    
    // Tamanho padrão do tabuleiro
    public static final int TAMANHO_TABULEIRO_PADRAO = 10;
    
    // Status da partida
    public static final String STATUS_AGUARDANDO = "AGUARDANDO";
    public static final String STATUS_EM_ANDAMENTO = "EM_ANDAMENTO";
    public static final String STATUS_FINALIZADA = "FINALIZADA";
    
    // Tipos de navios
    public static final String TIPO_PORTA_AVIOES = "PORTA_AVIOES";
    public static final String TIPO_CRUZADOR = "CRUZADOR";
    public static final String TIPO_CONTRATORPEDEIRO = "CONTRATORPEDEIRO";
    public static final String TIPO_SUBMARINO = "SUBMARINO";
    
    // Orientações
    public static final String ORIENTACAO_HORIZONTAL = "HORIZONTAL";
    public static final String ORIENTACAO_VERTICAL = "VERTICAL";
    
    // Resultados de jogada
    public static final String RESULTADO_AGUA = "AGUA";
    public static final String RESULTADO_ACERTO = "ACERTO";
    public static final String RESULTADO_AFUNDOU = "AFUNDOU";
    public static final String RESULTADO_ERRO = "ERRO";
    
    // Células do tabuleiro
    public static final String CELULA_AGUA = "0";
    public static final String CELULA_NAVIO = "1";
    public static final String CELULA_AGUA_ATINGIDA = "2";
    public static final String CELULA_NAVIO_ATINGIDO = "3";
}
