package com.batalha.gui;

import com.batalha.cliente.ClienteRMI;
import com.batalha.cliente.GerenciadorCliente;
import com.batalha.common.dto.PartidaDTO;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class TelaLobby extends JFrame {
    
    private ClienteRMI cliente;
    private JTable tabelaPartidas;
    private DefaultTableModel modeloTabela;
    private JButton btnCriarPartida;
    private JButton btnAtualizar;
    private JButton btnEntrarPartida;
    private Timer timerAtualizacao;
    
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
        setSize(750, 550);
        setResizable(false);
        
        // Panel principal com cor de fundo
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panelPrincipal.setBackground(new Color(240, 248, 255));
        
        // Título estilizado
        JLabel lblTitulo = new JLabel("⚓ SALAS DISPONÍVEIS ⚓");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(25, 55, 109)); // Azul marinho
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);
        
        // Tabela de partidas
        String[] colunas = {"ID", "Status", "Jogadores", "Tamanho"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tabelaPartidas = new JTable(modeloTabela);
        tabelaPartidas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaPartidas.getTableHeader().setReorderingAllowed(false);
        tabelaPartidas.setBackground(new Color(255, 255, 255));
        tabelaPartidas.setFont(new Font("Arial", Font.PLAIN, 12));
        tabelaPartidas.setRowHeight(25);
        tabelaPartidas.getTableHeader().setBackground(new Color(25, 55, 109));
        tabelaPartidas.getTableHeader().setForeground(Color.WHITE);
        tabelaPartidas.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        JScrollPane scrollPane = new JScrollPane(tabelaPartidas);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(25, 55, 109), 2));
        panelPrincipal.add(scrollPane, BorderLayout.CENTER);
        
        // Panel de botões
        JPanel panelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBotoes.setBackground(new Color(240, 248, 255));
        
        btnCriarPartida = new JButton("➕ Criar Nova Partida");
        btnCriarPartida.setPreferredSize(new Dimension(180, 40));
        btnCriarPartida.setFont(new Font("Arial", Font.BOLD, 13));
        btnCriarPartida.setBackground(new Color(34, 139, 34)); // Verde
        btnCriarPartida.setForeground(Color.BLACK);
        btnCriarPartida.setFocusPainted(false);
        btnCriarPartida.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 100, 0), 3),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)));
        btnCriarPartida.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCriarPartida.addActionListener(e -> btnCriarPartidaActionPerformed());
        
        btnEntrarPartida = new JButton("🚢 Entrar na Partida");
        btnEntrarPartida.setPreferredSize(new Dimension(180, 40));
        btnEntrarPartida.setFont(new Font("Arial", Font.BOLD, 13));
        btnEntrarPartida.setBackground(new Color(25, 55, 109)); // Azul marinho
        btnEntrarPartida.setForeground(Color.BLACK);
        btnEntrarPartida.setFocusPainted(false);
        btnEntrarPartida.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 0, 80), 3),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)));
        btnEntrarPartida.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEntrarPartida.addActionListener(e -> btnEntrarPartidaActionPerformed());
        
        btnAtualizar = new JButton("🔄 Atualizar");
        btnAtualizar.setPreferredSize(new Dimension(130, 40));
        btnAtualizar.setFont(new Font("Arial", Font.BOLD, 13));
        btnAtualizar.setBackground(new Color(100, 100, 100)); // Cinza escuro
        btnAtualizar.setForeground(Color.BLACK);
        btnAtualizar.setFocusPainted(false);
        btnAtualizar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 50, 50), 3),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)));
        btnAtualizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAtualizar.addActionListener(e -> atualizarListaPartidas());
        
        panelBotoes.add(btnCriarPartida);
        panelBotoes.add(btnEntrarPartida);
        panelBotoes.add(btnAtualizar);
        
        panelPrincipal.add(panelBotoes, BorderLayout.SOUTH);
        
        add(panelPrincipal);
    }
    
    private void btnCriarPartidaActionPerformed() {
        try {
            // Diálogo para selecionar tamanho do tabuleiro
            String[] opcoesTamanho = {"8x8", "10x10", "12x12", "15x15"};
            String selecao = (String) JOptionPane.showInputDialog(
                this,
                "Selecione o tamanho do tabuleiro:",
                "Criar Nova Partida",
                JOptionPane.QUESTION_MESSAGE,
                null,
                opcoesTamanho,
                opcoesTamanho[1] // 10x10 como padrão
            );
            
            // Se o usuário cancelou
            if (selecao == null) {
                return;
            }
            
            // Extrair o número do tamanho (ex: "10x10" -> 10)
            int tamanho = Integer.parseInt(selecao.split("x")[0]);
            
            PartidaDTO partida = cliente.criarPartida(tamanho);
            
            System.out.println("Partida retornada: ID=" + partida.getId() + ", Tamanho=" + partida.getTamanhoTabuleiro());
            
            JOptionPane.showMessageDialog(this,
                "Partida criada com sucesso!\nID: " + partida.getId() + "\nTabuleiro: " + partida.getTamanhoTabuleiro() + "x" + partida.getTamanhoTabuleiro(),
                "Sucesso",
                JOptionPane.INFORMATION_MESSAGE);
            
            atualizarListaPartidas();
            
            System.out.println("Abrindo tela de posicionamento...");
            // Abrir tela de posicionamento
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
    
    private void btnEntrarPartidaActionPerformed() {
        int linhaSelecionada = tabelaPartidas.getSelectedRow();
        
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Selecione uma partida para entrar!",
                "Aviso",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            Long idPartida = (Long) modeloTabela.getValueAt(linhaSelecionada, 0);
            PartidaDTO partida = cliente.entrarNaPartida(idPartida);
            
            JOptionPane.showMessageDialog(this,
                "Você entrou na partida!",
                "Sucesso",
                JOptionPane.INFORMATION_MESSAGE);
            
            // Abrir tela de posicionamento
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
    
    private void atualizarListaPartidas() {
        try {
            List<PartidaDTO> partidas = cliente.listarPartidasDisponiveis();
            
            // Limpar tabela
            modeloTabela.setRowCount(0);
            
            // Adicionar partidas
            for (PartidaDTO partida : partidas) {
                Object[] linha = {
                    partida.getId(),
                    partida.getStatus(),
                    partida.getJogadores().size() + "/2",
                    partida.getTamanhoTabuleiro() + "x" + partida.getTamanhoTabuleiro()
                };
                modeloTabela.addRow(linha);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erro ao atualizar lista: " + e.getMessage(),
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
