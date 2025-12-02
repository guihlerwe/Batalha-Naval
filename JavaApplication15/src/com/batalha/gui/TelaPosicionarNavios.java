package com.batalha.gui;

import com.batalha.cliente.ClienteRMI;
import com.batalha.cliente.GerenciadorCliente;
import com.batalha.common.dto.NavioDTO;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class TelaPosicionarNavios extends JFrame {
    
    private int TAMANHO_TABULEIRO;
    private static final int TAMANHO_CELULA = 40;
    
    private ClienteRMI cliente;
    private JButton[][] botoesTabuleiro;
    private JPanel panelTabuleiro;
    private JButton btnConfirmar;
    private JComboBox<String> comboTipoNavio;
    private JComboBox<String> comboOrientacao;
    private JButton btnLimpar;
    
    private List<NavioDTO> naviosPosicionados;
    private String tipoNavioAtual = "PORTA_AVIOES";
    private String orientacaoAtual = "HORIZONTAL";
    
    // Quantidade de navios permitidos (será calculada dinamicamente)
    private int portaAvioesRestantes;
    private int cruzadoresRestantes;
    private int contratorpedeirosRestantes;
    private int submarinosRestantes;
    
    private JLabel lblStatus;
    
    public TelaPosicionarNavios() {
        this.cliente = GerenciadorCliente.getInstancia().getCliente();
        this.naviosPosicionados = new ArrayList<>();
        
        // Obter tamanho do tabuleiro da partida atual
        try {
            if (cliente.getPartidaAtual() != null) {
                this.TAMANHO_TABULEIRO = cliente.getPartidaAtual().getTamanhoTabuleiro();
                System.out.println("Tamanho do tabuleiro da partida: " + this.TAMANHO_TABULEIRO);
            } else {
                System.err.println("AVISO: Partida atual é null, usando tamanho padrão 10");
                this.TAMANHO_TABULEIRO = 10;
            }
        } catch (Exception e) {
            System.err.println("ERRO ao obter tamanho do tabuleiro: " + e.getMessage());
            this.TAMANHO_TABULEIRO = 10; // Padrão se houver erro
        }
        
        // Calcular quantidade de navios baseado no tamanho do tabuleiro
        calcularQuantidadeNavios();
        
        initComponents();
        setLocationRelativeTo(null);
    }
    
    private void calcularQuantidadeNavios() {
        // Fórmula baseada no tamanho do tabuleiro
        // Tabuleiros menores: menos navios
        // Tabuleiros maiores: mais navios
        if (TAMANHO_TABULEIRO <= 8) {
            // 8x8: poucos navios
            portaAvioesRestantes = 1;
            cruzadoresRestantes = 1;
            contratorpedeirosRestantes = 2;
            submarinosRestantes = 2;
        } else if (TAMANHO_TABULEIRO <= 10) {
            // 10x10: quantidade padrão
            portaAvioesRestantes = 1;
            cruzadoresRestantes = 2;
            contratorpedeirosRestantes = 3;
            submarinosRestantes = 4;
        } else if (TAMANHO_TABULEIRO <= 12) {
            // 12x12: mais navios
            portaAvioesRestantes = 2;
            cruzadoresRestantes = 3;
            contratorpedeirosRestantes = 4;
            submarinosRestantes = 5;
        } else {
            // 15x15 ou maior: muitos navios
            portaAvioesRestantes = 2;
            cruzadoresRestantes = 4;
            contratorpedeirosRestantes = 5;
            submarinosRestantes = 6;
        }
        
        System.out.println("Quantidade de navios para tabuleiro " + TAMANHO_TABULEIRO + "x" + TAMANHO_TABULEIRO + ":");
        System.out.println("  Porta-aviões: " + portaAvioesRestantes);
        System.out.println("  Cruzadores: " + cruzadoresRestantes);
        System.out.println("  Contratorpedeiros: " + contratorpedeirosRestantes);
        System.out.println("  Submarinos: " + submarinosRestantes);
    }
    
    private void initComponents() {
        setTitle("Batalha Naval - Posicionar Navios");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        // Ajustar tamanho da janela baseado no tamanho do tabuleiro
        // Adicionar espaço extra para coordenadas, margens e controles
        int larguraBase = TAMANHO_TABULEIRO * TAMANHO_CELULA;
        int alturaBase = TAMANHO_TABULEIRO * TAMANHO_CELULA;
        
        int largura = larguraBase + 200; // Espaço para coordenadas e margens
        int altura = alturaBase + 350; // Espaço para título, coordenadas e controles
        
        setSize(largura, altura);
        setResizable(true); // Permitir redimensionar para telas menores
        
        System.out.println("Tamanho da janela: " + largura + "x" + altura + " (Tabuleiro: " + TAMANHO_TABULEIRO + "x" + TAMANHO_TABULEIRO + ")");
        
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBackground(new Color(240, 248, 255)); // Azul muito claro
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Título
        JLabel lblTitulo = new JLabel("⚓ POSICIONE SEUS NAVIOS ⚓");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(new Color(25, 55, 109)); // Azul marinho
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);
        
        // Painel central com coordenadas e tabuleiro
        JPanel panelTabuleiroComCoordenadas = new JPanel(new BorderLayout(5, 5));
        panelTabuleiroComCoordenadas.setBackground(new Color(240, 248, 255));
        
        // Coordenadas superiores (números 0-9)
        JPanel panelCoordenadasSuperior = new JPanel(new GridLayout(1, TAMANHO_TABULEIRO + 1, 2, 0));
        panelCoordenadasSuperior.setBackground(new Color(240, 248, 255));
        panelCoordenadasSuperior.add(new JLabel("")); // Canto vazio
        for (int i = 0; i < TAMANHO_TABULEIRO; i++) {
            JLabel lbl = new JLabel(String.valueOf(i), SwingConstants.CENTER);
            lbl.setFont(new Font("Arial", Font.BOLD, 12));
            lbl.setForeground(new Color(25, 55, 109));
            panelCoordenadasSuperior.add(lbl);
        }
        panelTabuleiroComCoordenadas.add(panelCoordenadasSuperior, BorderLayout.NORTH);
        
        // Painel com coordenadas laterais (letras A-J) e tabuleiro
        JPanel panelCentral = new JPanel(new BorderLayout(5, 0));
        panelCentral.setBackground(new Color(240, 248, 255));
        
        // Coordenadas laterais (letras A-Z)
        JPanel panelCoordenadasLateral = new JPanel(new GridLayout(TAMANHO_TABULEIRO, 1, 0, 2));
        panelCoordenadasLateral.setBackground(new Color(240, 248, 255));
        for (int i = 0; i < TAMANHO_TABULEIRO; i++) {
            String letra = String.valueOf((char)('A' + i));
            JLabel lbl = new JLabel(letra, SwingConstants.CENTER);
            lbl.setFont(new Font("Arial", Font.BOLD, 12));
            lbl.setForeground(new Color(25, 55, 109));
            panelCoordenadasLateral.add(lbl);
        }
        panelCentral.add(panelCoordenadasLateral, BorderLayout.WEST);
        
        // Painel central com tabuleiro
        panelTabuleiro = new JPanel(new GridLayout(TAMANHO_TABULEIRO, TAMANHO_TABULEIRO, 1, 1));
        panelTabuleiro.setPreferredSize(new Dimension(
            TAMANHO_TABULEIRO * TAMANHO_CELULA, 
            TAMANHO_TABULEIRO * TAMANHO_CELULA));
        panelTabuleiro.setBackground(new Color(60, 90, 130)); // Cor da grade
        panelTabuleiro.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(25, 55, 109), 3),
            BorderFactory.createEmptyBorder(2, 2, 2, 2)
        ));
        
        botoesTabuleiro = new JButton[TAMANHO_TABULEIRO][TAMANHO_TABULEIRO];
        
        for (int i = 0; i < TAMANHO_TABULEIRO; i++) {
            for (int j = 0; j < TAMANHO_TABULEIRO; j++) {
                JButton btn = new JButton();
                btn.setBackground(new Color(135, 206, 250)); // Azul céu
                btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(25, 55, 109), 1),
                    BorderFactory.createLineBorder(new Color(100, 150, 200), 1)
                ));
                btn.setPreferredSize(new Dimension(TAMANHO_CELULA, TAMANHO_CELULA));
                btn.setFocusPainted(false);
                
                final int linha = i;
                final int coluna = j;
                
                btn.addActionListener(e -> cliqueNaCelula(linha, coluna));
                btn.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        destacarPosicaoNavio(linha, coluna, true);
                    }
                    
                    @Override
                    public void mouseExited(MouseEvent e) {
                        destacarPosicaoNavio(linha, coluna, false);
                    }
                });
                
                botoesTabuleiro[i][j] = btn;
                panelTabuleiro.add(btn);
            }
        }
        
        panelCentral.add(panelTabuleiro, BorderLayout.CENTER);
        panelTabuleiroComCoordenadas.add(panelCentral, BorderLayout.CENTER);
        
        JPanel panelTabuleiroContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelTabuleiroContainer.setBackground(new Color(240, 248, 255));
        panelTabuleiroContainer.add(panelTabuleiroComCoordenadas);
        panelPrincipal.add(panelTabuleiroContainer, BorderLayout.CENTER);
        
        // Panel inferior com controles
        JPanel panelControles = new JPanel(new GridLayout(5, 1, 5, 5));
        panelControles.setBackground(new Color(240, 248, 255));
        
        // Seleção de tipo de navio
        JPanel panelTipo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTipo.setBackground(new Color(240, 248, 255));
        JLabel lblTipoNavio = new JLabel("🚢 Tipo de Navio:");
        lblTipoNavio.setFont(new Font("Arial", Font.BOLD, 13));
        lblTipoNavio.setForeground(new Color(25, 55, 109));
        panelTipo.add(lblTipoNavio);
        comboTipoNavio = new JComboBox<>(new String[]{
            "PORTA_AVIOES (5)", "CRUZADOR (4)", "CONTRATORPEDEIRO (3)", "SUBMARINO (2)"
        });
        comboTipoNavio.addActionListener(e -> atualizarTipoNavio());
        panelTipo.add(comboTipoNavio);
        
        // Seleção de orientação
        JPanel panelOrientacao = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelOrientacao.setBackground(new Color(240, 248, 255));
        JLabel lblOrientacao = new JLabel("🧭 Orientação:");
        lblOrientacao.setFont(new Font("Arial", Font.BOLD, 13));
        lblOrientacao.setForeground(new Color(25, 55, 109));
        panelOrientacao.add(lblOrientacao);
        comboOrientacao = new JComboBox<>(new String[]{"HORIZONTAL", "VERTICAL"});
        comboOrientacao.addActionListener(e -> orientacaoAtual = (String) comboOrientacao.getSelectedItem());
        panelOrientacao.add(comboOrientacao);
        
        // Status
        lblStatus = new JLabel(getTextoStatus());
        lblStatus.setFont(new Font("Arial", Font.BOLD, 12));
        lblStatus.setForeground(new Color(25, 55, 109));
        
        // Botões
        JPanel panelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panelBotoes.setBackground(new Color(240, 248, 255));
        btnLimpar = new JButton("🗑️ Limpar Tudo");
        btnLimpar.setBackground(new Color(220, 20, 60));
        btnLimpar.setForeground(Color.BLACK);
        btnLimpar.setFont(new Font("Arial", Font.BOLD, 12));
        btnLimpar.setFocusPainted(false);
        btnLimpar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(139, 0, 0), 2),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)));
        btnLimpar.addActionListener(e -> limparTabuleiro());
        
        btnConfirmar = new JButton("✓ Confirmar Posições");
        btnConfirmar.setBackground(new Color(34, 139, 34));
        btnConfirmar.setForeground(Color.BLACK);
        btnConfirmar.setFont(new Font("Arial", Font.BOLD, 12));
        btnConfirmar.setFocusPainted(false);
        btnConfirmar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 100, 0), 2),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)));
        btnConfirmar.addActionListener(e -> confirmarPosicoes());
        btnConfirmar.setEnabled(false);
        
        panelBotoes.add(btnLimpar);
        panelBotoes.add(btnConfirmar);
        
        panelControles.add(panelTipo);
        panelControles.add(panelOrientacao);
        panelControles.add(lblStatus);
        panelControles.add(new JLabel("")); // Espaçador
        panelControles.add(panelBotoes);
        
        panelPrincipal.add(panelControles, BorderLayout.SOUTH);
        
        add(panelPrincipal);
    }
    
    private void atualizarTipoNavio() {
        String selecionado = (String) comboTipoNavio.getSelectedItem();
        tipoNavioAtual = selecionado.split(" ")[0];
    }
    
    private void cliqueNaCelula(int linha, int coluna) {
        // Verificar se ainda pode adicionar este tipo de navio
        if (!podeAdicionarNavio()) {
            JOptionPane.showMessageDialog(this,
                "Você já posicionou todos os navios deste tipo!",
                "Aviso",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Criar navio
        NavioDTO navio = new NavioDTO(tipoNavioAtual, linha, coluna, orientacaoAtual);
        
        // Validar posição localmente primeiro
        if (!validarPosicaoLocal(navio)) {
            JOptionPane.showMessageDialog(this,
                "Posição inválida! Há outro navio ocupando esse espaço.",
                "Erro",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validar posição no servidor
        try {
            if (!cliente.validarPosicaoNavio(navio)) {
                JOptionPane.showMessageDialog(this,
                    "Posição inválida! Verifique limites e sobreposição.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erro ao validar posição: " + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Adicionar navio
        naviosPosicionados.add(navio);
        decrementarContador();
        pintarNavio(navio);
        atualizarStatus();
        
        // Trocar automaticamente para o próximo tipo de navio se necessário
        trocarParaProximoTipoDisponivel();
    }
    
    private boolean podeAdicionarNavio() {
        switch (tipoNavioAtual) {
            case "PORTA_AVIOES": return portaAvioesRestantes > 0;
            case "CRUZADOR": return cruzadoresRestantes > 0;
            case "CONTRATORPEDEIRO": return contratorpedeirosRestantes > 0;
            case "SUBMARINO": return submarinosRestantes > 0;
            default: return false;
        }
    }
    
    private void decrementarContador() {
        switch (tipoNavioAtual) {
            case "PORTA_AVIOES": portaAvioesRestantes--; break;
            case "CRUZADOR": cruzadoresRestantes--; break;
            case "CONTRATORPEDEIRO": contratorpedeirosRestantes--; break;
            case "SUBMARINO": submarinosRestantes--; break;
        }
    }
    
    private void trocarParaProximoTipoDisponivel() {
        // Se o tipo atual ainda tem navios disponíveis, não trocar
        if (podeAdicionarNavio()) {
            return;
        }
        
        // Trocar para o próximo tipo disponível
        String[] tipos = {"PORTA_AVIOES", "CRUZADOR", "CONTRATORPEDEIRO", "SUBMARINO"};
        
        for (String tipo : tipos) {
            String tipoAntigo = tipoNavioAtual;
            tipoNavioAtual = tipo;
            
            if (podeAdicionarNavio()) {
                // Atualizar o combo box
                switch (tipo) {
                    case "PORTA_AVIOES":
                        comboTipoNavio.setSelectedIndex(0);
                        break;
                    case "CRUZADOR":
                        comboTipoNavio.setSelectedIndex(1);
                        break;
                    case "CONTRATORPEDEIRO":
                        comboTipoNavio.setSelectedIndex(2);
                        break;
                    case "SUBMARINO":
                        comboTipoNavio.setSelectedIndex(3);
                        break;
                }
                
                System.out.println("Trocado automaticamente de " + tipoAntigo + " para " + tipo);
                return;
            }
        }
        
        // Se chegou aqui, todos os navios foram posicionados
        System.out.println("Todos os navios foram posicionados!");
    }
    
    private boolean validarPosicaoLocal(NavioDTO novoNavio) {
        int tamanho = getTamanhoNavio(novoNavio.getTipo());
        
        // Verificar limites do tabuleiro
        if (novoNavio.getOrientacao().equals("HORIZONTAL")) {
            if (novoNavio.getColunaInicial() + tamanho > TAMANHO_TABULEIRO) {
                return false;
            }
        } else {
            if (novoNavio.getLinhaInicial() + tamanho > TAMANHO_TABULEIRO) {
                return false;
            }
        }
        
        // Verificar sobreposição com navios já posicionados
        for (NavioDTO navioExistente : naviosPosicionados) {
            if (naviosSeColidem(novoNavio, navioExistente)) {
                return false;
            }
        }
        
        return true;
    }
    
    private boolean naviosSeColidem(NavioDTO navio1, NavioDTO navio2) {
        // Obter todas as posições ocupadas pelo navio1
        List<int[]> posicoes1 = getPosicoes(navio1);
        List<int[]> posicoes2 = getPosicoes(navio2);
        
        // Verificar se alguma posição se sobrepõe
        for (int[] pos1 : posicoes1) {
            for (int[] pos2 : posicoes2) {
                if (pos1[0] == pos2[0] && pos1[1] == pos2[1]) {
                    return true; // Colisão encontrada
                }
            }
        }
        
        return false;
    }
    
    private List<int[]> getPosicoes(NavioDTO navio) {
        List<int[]> posicoes = new ArrayList<>();
        int tamanho = getTamanhoNavio(navio.getTipo());
        
        for (int i = 0; i < tamanho; i++) {
            int linha, coluna;
            if (navio.getOrientacao().equals("HORIZONTAL")) {
                linha = navio.getLinhaInicial();
                coluna = navio.getColunaInicial() + i;
            } else {
                linha = navio.getLinhaInicial() + i;
                coluna = navio.getColunaInicial();
            }
            posicoes.add(new int[]{linha, coluna});
        }
        
        return posicoes;
    }
    
    private void pintarNavio(NavioDTO navio) {
        int tamanho = getTamanhoNavio(navio.getTipo());
        for (int i = 0; i < tamanho; i++) {
            int linha, coluna;
            if (navio.getOrientacao().equals("HORIZONTAL")) {
                linha = navio.getLinhaInicial();
                coluna = navio.getColunaInicial() + i;
            } else {
                linha = navio.getLinhaInicial() + i;
                coluna = navio.getColunaInicial();
            }
            // Usar cinza escuro bem visível
            botoesTabuleiro[linha][coluna].setBackground(new Color(50, 50, 50));
            botoesTabuleiro[linha][coluna].setText("🚢");
            botoesTabuleiro[linha][coluna].setFont(new Font("Arial", Font.PLAIN, 24));
            botoesTabuleiro[linha][coluna].setForeground(Color.WHITE);
        }
    }
    
    private void destacarPosicaoNavio(int linha, int coluna, boolean destacar) {
        if (!podeAdicionarNavio()) return;
        
        int tamanho = getTamanhoNavio(tipoNavioAtual);
        Color cor = destacar ? new Color(255, 215, 0) : new Color(135, 206, 250); // Dourado ao destacar
        
        for (int i = 0; i < tamanho; i++) {
            int l, c;
            if (orientacaoAtual.equals("HORIZONTAL")) {
                l = linha;
                c = coluna + i;
            } else {
                l = linha + i;
                c = coluna;
            }
            
            if (l < TAMANHO_TABULEIRO && c < TAMANHO_TABULEIRO) {
                // Só destacar se a célula estiver vazia (não tem navio ainda)
                Color corAtual = botoesTabuleiro[l][c].getBackground();
                boolean estaVazia = corAtual.equals(new Color(135, 206, 250)) || 
                                    corAtual.equals(new Color(255, 215, 0));
                if (estaVazia) {
                    botoesTabuleiro[l][c].setBackground(cor);
                }
            }
        }
    }
    
    private int getTamanhoNavio(String tipo) {
        switch (tipo) {
            case "PORTA_AVIOES": return 5;
            case "CRUZADOR": return 4;
            case "CONTRATORPEDEIRO": return 3;
            case "SUBMARINO": return 2;
            default: return 1;
        }
    }
    
    private void limparTabuleiro() {
        for (int i = 0; i < TAMANHO_TABULEIRO; i++) {
            for (int j = 0; j < TAMANHO_TABULEIRO; j++) {
                botoesTabuleiro[i][j].setBackground(new Color(135, 206, 250));
                botoesTabuleiro[i][j].setText(""); // Limpar texto também
            }
        }
        naviosPosicionados.clear();
        
        // Recalcular quantidade de navios
        calcularQuantidadeNavios();
        
        atualizarStatus();
    }
    
    private void atualizarStatus() {
        lblStatus.setText(getTextoStatus());
        
        // Verificar se todos os navios foram posicionados
        if (portaAvioesRestantes == 0 && cruzadoresRestantes == 0 &&
            contratorpedeirosRestantes == 0 && submarinosRestantes == 0) {
            btnConfirmar.setEnabled(true);
        } else {
            btnConfirmar.setEnabled(false);
        }
    }
    
    private String getTextoStatus() {
        return String.format("Restantes - Porta-aviões: %d | Cruzadores: %d | Contratorpedeiros: %d | Submarinos: %d",
            portaAvioesRestantes, cruzadoresRestantes, contratorpedeirosRestantes, submarinosRestantes);
    }
    
    private void confirmarPosicoes() {
        try {
            cliente.posicionarNavios(naviosPosicionados);
            cliente.confirmarPronto();
            
            JOptionPane.showMessageDialog(this,
                "Navios posicionados com sucesso!\nAguardando o adversário...",
                "Sucesso",
                JOptionPane.INFORMATION_MESSAGE);
            
            // Abrir tela do jogo
            new TelaJogo().setVisible(true);
            this.dispose();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erro ao confirmar posições: " + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
