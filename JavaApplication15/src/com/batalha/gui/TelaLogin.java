package com.batalha.gui;

import com.batalha.cliente.ClienteRMI;
import com.batalha.cliente.GerenciadorCliente;
import com.batalha.common.dto.JogadorDTO;
import java.awt.*;
import javax.swing.*;

public class TelaLogin extends JFrame {
    
    private JTextField txtNome;
    private JButton btnEntrar;
    private JLabel lblTitulo;
    private JLabel lblNome;
    
    public TelaLogin() {
        initComponents();
        setLocationRelativeTo(null);
    }
    
    private void initComponents() {
        setTitle("Batalha Naval - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(450, 300);
        setResizable(false);
        
        // Panel principal com cor de fundo
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        panelPrincipal.setBackground(new Color(240, 248, 255)); // Azul claro
        
        // Título com cor
        lblTitulo = new JLabel("⚓ BATALHA NAVAL ⚓");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitulo.setForeground(new Color(25, 55, 109)); // Azul marinho
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Espaçamento
        panelPrincipal.add(lblTitulo);
        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Label nome com cor
        lblNome = new JLabel("👤 Digite seu nome:");
        lblNome.setFont(new Font("Arial", Font.BOLD, 15));
        lblNome.setForeground(new Color(25, 55, 109));
        lblNome.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Campo de texto estilizado
        txtNome = new JTextField(20);
        txtNome.setMaximumSize(new Dimension(280, 35));
        txtNome.setFont(new Font("Arial", Font.PLAIN, 14));
        txtNome.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(25, 55, 109), 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        txtNome.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Botão estilizado
        btnEntrar = new JButton("🚢 Entrar no Jogo");
        btnEntrar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnEntrar.setPreferredSize(new Dimension(200, 45));
        btnEntrar.setMaximumSize(new Dimension(200, 45));
        btnEntrar.setFont(new Font("Arial", Font.BOLD, 15));
        btnEntrar.setBackground(new Color(25, 55, 109)); // Azul marinho
        btnEntrar.setForeground(Color.BLACK);
        btnEntrar.setFocusPainted(false);
        btnEntrar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 0, 80), 3),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)));
        btnEntrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEntrar.addActionListener(e -> btnEntrarActionPerformed());
        
        // Adicionar componentes
        panelPrincipal.add(lblNome);
        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 10)));
        panelPrincipal.add(txtNome);
        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 20)));
        panelPrincipal.add(btnEntrar);
        
        add(panelPrincipal);
    }
    
    private void btnEntrarActionPerformed() {
        String nome = txtNome.getText().trim();
        
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Por favor, digite seu nome!", 
                "Aviso", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            ClienteRMI cliente = GerenciadorCliente.getInstancia().getCliente();
            
            // Conectar ao servidor
            if (!cliente.conectar()) {
                JOptionPane.showMessageDialog(this,
                    "Não foi possível conectar ao servidor.\nVerifique se o servidor está rodando.",
                    "Erro de Conexão",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Conectar jogador
            JogadorDTO jogador = cliente.conectarJogador(nome);
            
            JOptionPane.showMessageDialog(this,
                "Conectado com sucesso!\nBem-vindo, " + jogador.getNome() + "!",
                "Sucesso",
                JOptionPane.INFORMATION_MESSAGE);
            
            // Abrir lobby
            new TelaLobby().setVisible(true);
            this.dispose();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Erro ao conectar: " + ex.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> new TelaLogin().setVisible(true));
    }
}
