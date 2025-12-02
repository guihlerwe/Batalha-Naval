package com.batalha.util;

import com.batalha.model.Navio;
import com.batalha.model.Tabuleiro;
import java.util.List;

public class ValidadorJogo {
    
    /**
     * Valida se um navio pode ser posicionado em uma determinada posição
     */
    public static boolean validarPosicaoNavio(Tabuleiro tabuleiro, int linhaInicial, 
                                              int colunaInicial, int tamanho, 
                                              String orientacao) {
        // Verificar limites do tabuleiro
        if (orientacao.equals("HORIZONTAL")) {
            if (colunaInicial + tamanho > tabuleiro.getTamanho()) {
                return false;
            }
            if (linhaInicial < 0 || linhaInicial >= tabuleiro.getTamanho()) {
                return false;
            }
        } else {
            if (linhaInicial + tamanho > tabuleiro.getTamanho()) {
                return false;
            }
            if (colunaInicial < 0 || colunaInicial >= tabuleiro.getTamanho()) {
                return false;
            }
        }
        
        // Verificar sobreposição com navios existentes
        List<Navio> naviosExistentes = tabuleiro.getNavios();
        for (Navio navioExistente : naviosExistentes) {
            if (verificarColisao(linhaInicial, colunaInicial, tamanho, orientacao, navioExistente)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Verifica se há colisão entre dois navios
     */
    private static boolean verificarColisao(int linhaInicial, int colunaInicial, 
                                           int tamanho, String orientacao, 
                                           Navio navioExistente) {
        for (int i = 0; i < tamanho; i++) {
            int linha, coluna;
            if (orientacao.equals("HORIZONTAL")) {
                linha = linhaInicial;
                coluna = colunaInicial + i;
            } else {
                linha = linhaInicial + i;
                coluna = colunaInicial;
            }
            
            if (navioExistente.ocupaPosicao(linha, coluna)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Valida se todas as peças necessárias foram posicionadas
     */
    public static boolean validarQuantidadeNavios(List<Navio> navios) {
        int portaAvioes = 0;
        int cruzadores = 0;
        int contratorpedeiros = 0;
        int submarinos = 0;
        
        for (Navio navio : navios) {
            switch (navio.getTipo()) {
                case "PORTA_AVIOES":
                    portaAvioes++;
                    break;
                case "CRUZADOR":
                    cruzadores++;
                    break;
                case "CONTRATORPEDEIRO":
                    contratorpedeiros++;
                    break;
                case "SUBMARINO":
                    submarinos++;
                    break;
            }
        }
        
        return portaAvioes == 1 && cruzadores == 2 && 
               contratorpedeiros == 3 && submarinos == 4;
    }
    
    /**
     * Verifica se todos os navios de um tabuleiro foram afundados
     */
    public static boolean todosNaviosAfundados(Tabuleiro tabuleiro) {
        List<Navio> navios = tabuleiro.getNavios();
        
        if (navios.isEmpty()) {
            return false;
        }
        
        for (Navio navio : navios) {
            if (!navio.estaAfundado()) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Calcula o tamanho de um navio baseado no tipo
     */
    public static int calcularTamanhoNavio(String tipo) {
        switch (tipo) {
            case "PORTA_AVIOES": return 5;
            case "CRUZADOR": return 4;
            case "CONTRATORPEDEIRO": return 3;
            case "SUBMARINO": return 2;
            default: return 1;
        }
    }
}
