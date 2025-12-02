package com.batalha.gui;

import com.batalha.cliente.ClienteRMI;
import com.batalha.cliente.GerenciadorCliente;
import com.batalha.common.dto.PartidaDTO;
import java.awt.*;
import java.util.List;
import javax.swing.*;

public class TelaLobby extends JFrame {
    
    private ClienteRMI cliente;
    private JPanel panelPartidas;
    private JButton btnCriarPartida;
    private JButton btnAtualizar;
    private Timer timerAtualizacao;
    private List<PartidaDTO> partidasDisponiveis;
    
    public TelaLobby() {
        this.cliente = GerenciadorCliente.getInstancia().getCliente();
        initComponents();
        setLocationRelativeTo(null);
        atualizarListaPartidas();
        iniciarAtualizacaoAutomatica();
    }
    
    private void initComponents() {
        setTitle("Batalha Naval - Lobby");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setResizable(false);
        
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panelPrincipal.setBackground(new Color(240, 248, 255));
        
        JLabel lblTitulo = new JLabel("SALAS DISPONÍVEIS");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(25, 55, 109)); 
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);
        
        panelPartidas = new JPanel();
        panelPartidas.setLayout(new BoxLayout(panelPartidas, BoxLayout.Y_AXIS));
        panelPartidas.setBackground(new Color(240, 248, 255));
        
        JScrollPane scrollPane = new JScrollPane(panelPartidas);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(25, 55, 109), 2));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panelPrincipal.add(scrollPane, BorderLayout.CENTER);
        
        JPanel panelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBotoes.setBackground(new Color(240, 248, 255));
        
        btnCriarPartida = new JButton("Criar Nova Partida");
        btnCriarPartida.setPreferredSize(new Dimension(200, 40));
        btnCriarPartida.setFont(new Font("Arial", Font.BOLD, 13));
        btnCriarPartida.setBackground(new Color(34, 139, 34)); 
        btnCriarPartida.setForeground(Color.BLACK);
        btnCriarPartida.setFocusPainted(false);
        btnCriarPartida.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 100, 0), 3),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)));
        btnCriarPartida.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCriarPartida.addActionListener(e -> btnCriarPartidaActionPerformed());
        
        btnAtualizar = new JButton("Atualizar");
        btnAtualizar.setPreferredSize(new Dimension(150, 40));
        btnAtualizar.setFont(new Font("Arial", Font.BOLD, 13));
        btnAtualizar.setBackground(new Color(100, 100, 100)); 
        btnAtualizar.setForeground(Color.BLACK);
        btnAtualizar.setFocusPainted(false);
        btnAtualizar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 50, 50), 3),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)));
        btnAtualizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAtualizar.addActionListener(e -> atualizarListaPartidas());
        
        panelBotoes.add(btnCriarPartida);
        panelBotoes.add(btnAtualizar);
        
        panelPrincipal.add(panelBotoes, BorderLayout.SOUTH);
        
        add(panelPrincipal);
    }
    
    private void btnCriarPartidaActionPerformed() {
        try {
            String[] opcoesTamanho = {"8x8", "10x10", "12x12", "15x15"};
            String selecao = (String) JOptionPane.showInputDialog(
                this,
                "Selecione o tamanho do tabuleiro:",
                "Criar Nova Partida",
                JOptionPane.QUESTION_MESSAGE,
                null,
                opcoesTamanho,
                opcoesTamanho[1] 
            );
            
            if (selecao == null) {
                return;
            }
            
            int tamanho = Integer.parseInt(selecao.split("x")[0]);
            
            PartidaDTO partida = cliente.criarPartida(tamanho);
            
            System.out.println("Partida retornada: ID=" + partida.getId() + ", Tamanho=" + partida.getTamanhoTabuleiro());
            
            JOptionPane.showMessageDialog(this,
                "Partida criada com sucesso!\nID: " + partida.getId() + "\nTabuleiro: " + partida.getTamanhoTabuleiro() + "x" + partida.getTamanhoTabuleiro(),
                "Sucesso",
                JOptionPane.INFORMATION_MESSAGE);
            
            atualizarListaPartidas();
            
            System.out.println("Abrindo tela de posicionamento...");
            new TelaPosicionarNavios().setVisible(true);
            pararAtualizacaoAutomatica();
            this.dispose();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erro ao criar partida: " + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void atualizarListaPartidas() {
        try {
            partidasDisponiveis = cliente.listarPartidasDisponiveis();
            panelPartidas.removeAll();
            
            if (partidasDisponiveis.isEmpty()) {
                JLabel lblVazio = new JLabel("Nenhuma partida disponível no momento");
                lblVazio.setFont(new Font("Arial", Font.ITALIC, 14));
                lblVazio.setForeground(Color.GRAY);
                lblVazio.setAlignmentX(Component.CENTER_ALIGNMENT);
                lblVazio.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
                panelPartidas.add(lblVazio);
            } else {
                for (PartidaDTO partida : partidasDisponiveis) {
                    JPanel cardPartida = criarCardPartida(partida);
                    panelPartidas.add(cardPartida);
                    panelPartidas.add(Box.createRigidArea(new Dimension(0, 10))); 
                }
            }
            
            panelPartidas.revalidate();
            panelPartidas.repaint();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erro ao atualizar lista: " + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private JPanel criarCardPartida(PartidaDTO partida) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(10, 5));
        card.setMaximumSize(new Dimension(750, 80));
        card.setPreferredSize(new Dimension(750, 80));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(25, 55, 109), 2),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        JPanel panelInfo = new JPanel();
        panelInfo.setLayout(new GridLayout(2, 2, 5, 2));
        panelInfo.setBackground(Color.WHITE);
        
        JLabel lblId = new JLabel("Sala #" + partida.getId());
        lblId.setFont(new Font("Arial", Font.BOLD, 14));
        lblId.setForeground(new Color(25, 55, 109));
        
        JLabel lblStatus = new JLabel("Status: " + partida.getStatus());
        lblStatus.setFont(new Font("Arial", Font.PLAIN, 12));
        
        JLabel lblJogadores = new JLabel("Jogadores: " + partida.getJogadores().size() + "/2");
        lblJogadores.setFont(new Font("Arial", Font.PLAIN, 12));
        
        JLabel lblTamanho = new JLabel("Tabuleiro: " + partida.getTamanhoTabuleiro() + "x" + partida.getTamanhoTabuleiro());
        lblTamanho.setFont(new Font("Arial", Font.PLAIN, 12));
        
        panelInfo.add(lblId);
        panelInfo.add(lblStatus);
        panelInfo.add(lblJogadores);
        panelInfo.add(lblTamanho);
        
        JButton btnSelecionar = new JButton("SELECIONAR");
        btnSelecionar.setPreferredSize(new Dimension(150, 60));
        btnSelecionar.setFont(new Font("Arial", Font.BOLD, 12));
        btnSelecionar.setBackground(new Color(25, 55, 109));
        btnSelecionar.setForeground(Color.BLACK);
        btnSelecionar.setFocusPainted(false);
        btnSelecionar.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 80), 3));
        btnSelecionar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnSelecionar.addActionListener(e -> entrarNaPartida(partida));
        
        card.add(panelInfo, BorderLayout.CENTER);
        card.add(btnSelecionar, BorderLayout.EAST);
        
        return card;
    }
    
    private void entrarNaPartida(PartidaDTO partida) {
        try {
            cliente.entrarNaPartida(partida.getId());
            
            JOptionPane.showMessageDialog(this,
                "Você entrou na partida!",
                "Sucesso",
                JOptionPane.INFORMATION_MESSAGE);
            
            new TelaPosicionarNavios().setVisible(true);
            pararAtualizacaoAutomatica();
            this.dispose();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erro ao entrar na partida: " + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void iniciarAtualizacaoAutomatica() {
        timerAtualizacao = new Timer(3000, e -> atualizarListaPartidas());
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
