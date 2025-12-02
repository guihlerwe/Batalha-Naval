package com.batalha.gui;

import com.batalha.cliente.ClienteRMI;
import com.batalha.cliente.GerenciadorCliente;
import com.batalha.common.dto.PartidaDTO;
import com.batalha.common.dto.ResultadoJogadaDTO;
import java.awt.*;
import javax.swing.*;

public class TelaJogo extends JFrame {
    
    private int TAMANHO_TABULEIRO;
    private static final int TAMANHO_CELULA = 40;
    
    private ClienteRMI cliente;
    private JButton[][] botoesMeuTabuleiro;
    private JButton[][] botoesTabuleiroInimigo;
    private JPanel panelMeuTabuleiro;
    private JPanel panelTabuleiroInimigo;
    private JLabel lblStatus;
    private JLabel lblTurno;
    private Timer timerAtualizacao;
    
    public TelaJogo() {
        this.cliente = GerenciadorCliente.getInstancia().getCliente();
        
        try {
            this.TAMANHO_TABULEIRO = cliente.getPartidaAtual().getTamanhoTabuleiro();
        } catch (Exception e) {
            this.TAMANHO_TABULEIRO = 10; 
        }
        
        initComponents();
        setLocationRelativeTo(null);
        iniciarAtualizacaoAutomatica();
        atualizarMeuTabuleiro();
    }
    
    private void initComponents() {
        setTitle("Batalha Naval - Jogo");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        int larguraBase = TAMANHO_TABULEIRO * TAMANHO_CELULA * 2;
        int alturaBase = TAMANHO_TABULEIRO * TAMANHO_CELULA;
        
        int largura = larguraBase + 250; 
        int altura = alturaBase + 250; 
        
        setSize(largura, altura);
        setResizable(true); 
        
        System.out.println("Tamanho da janela de jogo: " + largura + "x" + altura + " (Tabuleiro: " + TAMANHO_TABULEIRO + "x" + TAMANHO_TABULEIRO + ")");
        
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBackground(new Color(240, 248, 255));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel lblTitulo = new JLabel("⚓ BATALHA NAVAL ⚓");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(25, 55, 109));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);
        
        JPanel panelTabuleiros = new JPanel(new GridLayout(1, 2, 20, 0));
        panelTabuleiros.setBackground(new Color(240, 248, 255));
        
        JPanel panelMeuTabuleiroContainer = new JPanel(new BorderLayout(5, 5));
        panelMeuTabuleiroContainer.setBackground(new Color(240, 248, 255));
        JLabel lblMeuTabuleiro = new JLabel("🚢 MEU TABULEIRO");
        lblMeuTabuleiro.setHorizontalAlignment(SwingConstants.CENTER);
        lblMeuTabuleiro.setFont(new Font("Arial", Font.BOLD, 14));
        lblMeuTabuleiro.setForeground(new Color(25, 55, 109));
        panelMeuTabuleiroContainer.add(lblMeuTabuleiro, BorderLayout.NORTH);
        
        JPanel panelMeuTabComCoord = new JPanel(new BorderLayout(3, 3));
        panelMeuTabComCoord.setBackground(new Color(240, 248, 255));
        
        JPanel panelCoordSup1 = new JPanel(new GridLayout(1, TAMANHO_TABULEIRO + 1, 1, 0));
        panelCoordSup1.setBackground(new Color(240, 248, 255));
        panelCoordSup1.add(new JLabel(""));
        for (int i = 0; i < TAMANHO_TABULEIRO; i++) {
            JLabel lbl = new JLabel(String.valueOf(i), SwingConstants.CENTER);
            lbl.setFont(new Font("Arial", Font.BOLD, 10));
            lbl.setForeground(new Color(25, 55, 109));
            panelCoordSup1.add(lbl);
        }
        panelMeuTabComCoord.add(panelCoordSup1, BorderLayout.NORTH);
        
        JPanel panelCentral1 = new JPanel(new BorderLayout(3, 0));
        panelCentral1.setBackground(new Color(240, 248, 255));
        
        JPanel panelLetras1 = new JPanel(new GridLayout(TAMANHO_TABULEIRO, 1, 0, 1));
        panelLetras1.setBackground(new Color(240, 248, 255));
        for (int i = 0; i < TAMANHO_TABULEIRO; i++) {
            String letra = "ⒶⒷⒸⒹⒺⒻⒼⒽⒾⒿⓀⓁⓂⓃⓄ".charAt(Math.min(i, 14)) + "";
            if (i < 10) {
                letra = "ⒶⒷⒸⒹⒺⒻⒼⒽⒾⒿ".charAt(i) + "";
            } else {
                letra = String.valueOf((char)('A' + i));
            }
            JLabel lbl = new JLabel(letra, SwingConstants.CENTER);
            lbl.setFont(new Font("Arial", Font.BOLD, 10));
            lbl.setForeground(new Color(25, 55, 109));
            panelLetras1.add(lbl);
        }
        panelCentral1.add(panelLetras1, BorderLayout.WEST);
        
        panelMeuTabuleiro = new JPanel(new GridLayout(TAMANHO_TABULEIRO, TAMANHO_TABULEIRO, 1, 1));
        panelMeuTabuleiro.setBackground(new Color(60, 90, 130)); 
        panelMeuTabuleiro.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(25, 55, 109), 3),
            BorderFactory.createEmptyBorder(1, 1, 1, 1)
        ));
        botoesMeuTabuleiro = new JButton[TAMANHO_TABULEIRO][TAMANHO_TABULEIRO];
        
        for (int i = 0; i < TAMANHO_TABULEIRO; i++) {
            for (int j = 0; j < TAMANHO_TABULEIRO; j++) {
                JButton btn = new JButton();
                btn.setBackground(new Color(135, 206, 250)); 
                btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(25, 55, 109), 1),
                    BorderFactory.createLineBorder(new Color(100, 150, 200), 1)
                ));
                btn.setPreferredSize(new Dimension(TAMANHO_CELULA, TAMANHO_CELULA));
                btn.setFocusPainted(false);
                btn.setEnabled(false);
                botoesMeuTabuleiro[i][j] = btn;
                panelMeuTabuleiro.add(btn);
            }
        }
        panelCentral1.add(panelMeuTabuleiro, BorderLayout.CENTER);
        panelMeuTabComCoord.add(panelCentral1, BorderLayout.CENTER);
        panelMeuTabuleiroContainer.add(panelMeuTabComCoord, BorderLayout.CENTER);
        
        JPanel panelTabuleiroInimigoContainer = new JPanel(new BorderLayout(5, 5));
        panelTabuleiroInimigoContainer.setBackground(new Color(240, 248, 255));
        JLabel lblTabuleiroInimigo = new JLabel("🎯 TABULEIRO INIMIGO");
        lblTabuleiroInimigo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTabuleiroInimigo.setFont(new Font("Arial", Font.BOLD, 14));
        lblTabuleiroInimigo.setForeground(new Color(25, 55, 109));
        panelTabuleiroInimigoContainer.add(lblTabuleiroInimigo, BorderLayout.NORTH);
        
        JPanel panelTabInimigoComCoord = new JPanel(new BorderLayout(3, 3));
        panelTabInimigoComCoord.setBackground(new Color(240, 248, 255));
        
        JPanel panelCoordSup2 = new JPanel(new GridLayout(1, TAMANHO_TABULEIRO + 1, 1, 0));
        panelCoordSup2.setBackground(new Color(240, 248, 255));
        panelCoordSup2.add(new JLabel(""));
        for (int i = 0; i < TAMANHO_TABULEIRO; i++) {
            JLabel lbl = new JLabel(String.valueOf(i), SwingConstants.CENTER);
            lbl.setFont(new Font("Arial", Font.BOLD, 10));
            lbl.setForeground(new Color(25, 55, 109));
            panelCoordSup2.add(lbl);
        }
        panelTabInimigoComCoord.add(panelCoordSup2, BorderLayout.NORTH);
        
        JPanel panelCentral2 = new JPanel(new BorderLayout(3, 0));
        panelCentral2.setBackground(new Color(240, 248, 255));
        
        JPanel panelLetras2 = new JPanel(new GridLayout(TAMANHO_TABULEIRO, 1, 0, 1));
        panelLetras2.setBackground(new Color(240, 248, 255));
        for (int i = 0; i < TAMANHO_TABULEIRO; i++) {
            String letra = "ⒶⒷⒸⒹⒺⒻⒼⒽⒾⒿⓀⓁⓂⓃⓄ".charAt(Math.min(i, 14)) + "";
            if (i < 10) {
                letra = "ⒶⒷⒸⒹⒺⒻⒼⒽⒾⒿ".charAt(i) + "";
            } else {
                letra = String.valueOf((char)('A' + i));
            }
            JLabel lbl = new JLabel(letra, SwingConstants.CENTER);
            lbl.setFont(new Font("Arial", Font.BOLD, 10));
            lbl.setForeground(new Color(25, 55, 109));
            panelLetras2.add(lbl);
        }
        panelCentral2.add(panelLetras2, BorderLayout.WEST);
        
        panelTabuleiroInimigo = new JPanel(new GridLayout(TAMANHO_TABULEIRO, TAMANHO_TABULEIRO, 1, 1));
        panelTabuleiroInimigo.setBackground(new Color(60, 90, 130)); 
        panelTabuleiroInimigo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(25, 55, 109), 3),
            BorderFactory.createEmptyBorder(1, 1, 1, 1)
        ));
        botoesTabuleiroInimigo = new JButton[TAMANHO_TABULEIRO][TAMANHO_TABULEIRO];
        
        for (int i = 0; i < TAMANHO_TABULEIRO; i++) {
            for (int j = 0; j < TAMANHO_TABULEIRO; j++) {
                JButton btn = new JButton();
                btn.setBackground(new Color(135, 206, 250)); 
                btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(25, 55, 109), 1),
                    BorderFactory.createLineBorder(new Color(100, 150, 200), 1)
                ));
                btn.setPreferredSize(new Dimension(TAMANHO_CELULA, TAMANHO_CELULA));
                btn.setFocusPainted(false);
                
                final int linha = i;
                final int coluna = j;
                
                btn.addActionListener(e -> atacarPosicao(linha, coluna));
                
                botoesTabuleiroInimigo[i][j] = btn;
                panelTabuleiroInimigo.add(btn);
            }
        }
        panelCentral2.add(panelTabuleiroInimigo, BorderLayout.CENTER);
        panelTabInimigoComCoord.add(panelCentral2, BorderLayout.CENTER);
        panelTabuleiroInimigoContainer.add(panelTabInimigoComCoord, BorderLayout.CENTER);
        
        panelTabuleiros.add(panelMeuTabuleiroContainer);
        panelTabuleiros.add(panelTabuleiroInimigoContainer);
        panelPrincipal.add(panelTabuleiros, BorderLayout.CENTER);
        
        JPanel panelStatus = new JPanel(new GridLayout(2, 1, 5, 5));
        panelStatus.setBackground(new Color(240, 248, 255));
        
        lblTurno = new JLabel("Turno: Aguardando...");
        lblTurno.setFont(new Font("Arial", Font.BOLD, 14));
        lblTurno.setForeground(new Color(25, 55, 109));
        lblTurno.setHorizontalAlignment(SwingConstants.CENTER);
        
        lblStatus = new JLabel("Status: Aguardando início da partida...");
        lblStatus.setFont(new Font("Arial", Font.PLAIN, 12));
        lblStatus.setForeground(new Color(25, 55, 109));
        lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
        
        panelStatus.add(lblTurno);
        panelStatus.add(lblStatus);
        
        panelPrincipal.add(panelStatus, BorderLayout.SOUTH);
        
        add(panelPrincipal);
    }
    
    private void atacarPosicao(int linha, int coluna) {
        try {
            if (!cliente.ehMeuTurno()) {
                JOptionPane.showMessageDialog(this,
                    "Não é o seu turno!",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            JButton btn = botoesTabuleiroInimigo[linha][coluna];
            if (!btn.isEnabled()) {
                JOptionPane.showMessageDialog(this,
                    "Você já atacou esta posição!",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            ResultadoJogadaDTO resultado = cliente.atacar(linha, coluna);
            
            btn.setEnabled(false);
            
            switch (resultado.getResultado()) {
                case "AGUA":
                    btn.setBackground(Color.CYAN);
                    btn.setText("○");
                    lblStatus.setText("Status: Água!");
                    break;
                case "ACERTO":
                    btn.setBackground(Color.ORANGE);
                    btn.setText("💥");
                    btn.setFont(new Font("Arial", Font.PLAIN, 20));
                    lblStatus.setText("Status: Acertou!");
                    break;
                case "AFUNDOU":
                    btn.setBackground(Color.RED);
                    btn.setText("💥");
                    btn.setFont(new Font("Arial", Font.PLAIN, 20));
                    lblStatus.setText("Status: Afundou um navio!");
                    break;
            }
            
            if (resultado.isFimDeJogo()) {
                pararAtualizacaoAutomatica();
                
                String mensagemVitoria;
                if (resultado.getVencedorId().equals(cliente.getJogadorAtual().getId())) {
                    mensagemVitoria = "🎉 PARABÉNS! VOCÊ VENCEU! 🎉\n\nVocê afundou todos os navios do adversário!";
                } else {
                    mensagemVitoria = "😢 VOCÊ PERDEU!\n\nTodos os seus navios foram afundados.";
                }
                
                JOptionPane.showMessageDialog(this,
                    mensagemVitoria,
                    "Fim de Jogo",
                    JOptionPane.INFORMATION_MESSAGE);
                
                new TelaLobby().setVisible(true);
                this.dispose();
            }
            
            atualizarEstadoJogo();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erro ao atacar: " + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void atualizarMeuTabuleiro() {
        try {
            boolean[][] posicoes = cliente.obterPosicoesNavios();
            
            for (int i = 0; i < TAMANHO_TABULEIRO; i++) {
                for (int j = 0; j < TAMANHO_TABULEIRO; j++) {
                    if (posicoes[i][j]) {
                        botoesMeuTabuleiro[i][j].setBackground(new Color(60, 60, 60)); 
                        botoesMeuTabuleiro[i][j].setText("■");
                        botoesMeuTabuleiro[i][j].setForeground(Color.WHITE);
                        botoesMeuTabuleiro[i][j].setFont(new Font("Arial", Font.PLAIN, 16));
                    }
                }
            }
            
            String[][] ataquesRecebidos = cliente.obterAtaquesRecebidos();
            
            for (int i = 0; i < TAMANHO_TABULEIRO; i++) {
                for (int j = 0; j < TAMANHO_TABULEIRO; j++) {
                    String ataque = ataquesRecebidos[i][j];
                    if (ataque != null) {
                        if (ataque.equals("ACERTO") || ataque.equals("AFUNDOU")) {
                            botoesMeuTabuleiro[i][j].setBackground(Color.RED);
                            botoesMeuTabuleiro[i][j].setText("💥");
                            botoesMeuTabuleiro[i][j].setFont(new Font("Arial", Font.PLAIN, 20));
                        } else if (ataque.equals("AGUA")) {
                            botoesMeuTabuleiro[i][j].setBackground(Color.CYAN);
                            botoesMeuTabuleiro[i][j].setText("○");
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            System.err.println("Erro ao atualizar meu tabuleiro: " + e.getMessage());
        }
    }
    
    private void atualizarEstadoJogo() {
        try {
            PartidaDTO partida = cliente.obterEstadoPartida();
            
            if (partida != null && "FINALIZADA".equals(partida.getStatus())) {
                pararAtualizacaoAutomatica();
                
                String mensagemFinal;
                if (partida.getVencedorId().equals(cliente.getJogadorAtual().getId())) {
                    mensagemFinal = "🎉 PARABÉNS! VOCÊ VENCEU! 🎉\n\nVocê afundou todos os navios do adversário!";
                } else {
                    mensagemFinal = "😢 VOCÊ PERDEU!\n\nTodos os seus navios foram afundados.";
                }
                
                JOptionPane.showMessageDialog(this,
                    mensagemFinal,
                    "Fim de Jogo",
                    JOptionPane.INFORMATION_MESSAGE);
                
                new TelaLobby().setVisible(true);
                this.dispose();
                return;
            }
            
            boolean meuTurno = cliente.ehMeuTurno();
            
            if (meuTurno) {
                lblTurno.setText("Turno: SEU TURNO - Ataque!");
                lblTurno.setForeground(Color.GREEN.darker());
            } else {
                lblTurno.setText("Turno: Adversário");
                lblTurno.setForeground(Color.RED);
                lblStatus.setText("Status: Aguardando jogada do adversário...");
            }
            
            atualizarMeuTabuleiro();
            
        } catch (Exception e) {
            System.err.println("Erro ao atualizar estado: " + e.getMessage());
        }
    }
    
    private void iniciarAtualizacaoAutomatica() {
        timerAtualizacao = new Timer(500, e -> atualizarEstadoJogo());
        timerAtualizacao.start();
    }
    
    private void pararAtualizacaoAutomatica() {
        if (timerAtualizacao != null) {
            timerAtualizacao.stop();
        }
    }
    
    @Override
    public void dispose() {
        pararAtualizacaoAutomatica();
        super.dispose();
    }
}
