/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.batalha.servidor;

import com.batalha.common.InterfaceServidor;
import com.batalha.common.dto.*;
import com.batalha.dao.*;
import com.batalha.model.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

public class ServidorRMI extends UnicastRemoteObject implements InterfaceServidor {
    
    // DAOs
    private JogadorDAO jogadorDAO;
    private PartidaDAO partidaDAO;
    private TabuleiroDAO tabuleiroDAO;
    private NavioDAO navioDAO;
    private JogadaDAO jogadaDAO;
    
    public ServidorRMI() throws RemoteException {
        super();
        this.jogadorDAO = new JogadorDAO();
        this.partidaDAO = new PartidaDAO();
        this.tabuleiroDAO = new TabuleiroDAO();
        this.navioDAO = new NavioDAO();
        this.jogadaDAO = new JogadaDAO();
    }
    
    @Override
    public JogadorDTO conectarJogador(String nome) throws RemoteException {
        try {
            Jogador jogador = new Jogador(nome);
            jogadorDAO.salvar(jogador);
            
            return converterJogadorParaDTO(jogador);
        } catch (Exception e) {
            throw new RemoteException("Erro ao conectar jogador", e);
        }
    }
    
    @Override
    public void desconectarJogador(Long jogadorId) throws RemoteException {
        try {
            Jogador jogador = jogadorDAO.buscar(jogadorId);
            if (jogador != null && jogador.getPartida() != null) {
                Partida partida = jogador.getPartida();
                Long partidaId = partida.getId();
                
                // Forçar carregamento dos jogadores
                partida.getJogadores().size();
                
                // Remover jogador da partida
                partida.getJogadores().remove(jogador);
                jogador.setPartida(null);
                jogador.setProntoParaJogar(false);
                
                // Atualizar jogador primeiro
                jogadorDAO.atualizar(jogador);
                
                // Se a partida não está finalizada
                if (!partida.getStatus().equals("FINALIZADA")) {
                    if (partida.getJogadores().isEmpty()) {
                        // Partida vazia, remover
                        try {
                            partidaDAO.remover(partidaId);
                            System.out.println("Partida " + partidaId + " removida (ficou vazia)");
                        } catch (Exception e) {
                            System.err.println("Erro ao remover partida vazia: " + e.getMessage());
                        }
                    } else if (partida.getJogadores().size() == 1) {
                        // Avisar o outro jogador e marcar como cancelada
                        partida.setStatus("CANCELADA");
                        partidaDAO.atualizar(partida);
                        System.out.println("Partida " + partidaId + " marcada como cancelada");
                    } else {
                        // Atualizar partida normalmente
                        partidaDAO.atualizar(partida);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao desconectar jogador: " + e.getMessage());
            throw new RemoteException("Erro ao desconectar jogador", e);
        }
    }
    
    @Override
    public PartidaDTO criarPartida(int tamanhoTabuleiro) throws RemoteException {
        try {
            // Limpar partidas antigas vazias ou incompletas
            limparPartidasAbandonadas();
            
            Partida partida = new Partida();
            partida.setTamanhoTabuleiro(tamanhoTabuleiro);
            partidaDAO.salvar(partida);
            
            System.out.println("Partida criada: ID " + partida.getId() + " | Tamanho: " + tamanhoTabuleiro + "x" + tamanhoTabuleiro);
            
            return converterPartidaParaDTO(partida);
        } catch (Exception e) {
            throw new RemoteException("Erro ao criar partida", e);
        }
    }
    
    @Override
    public PartidaDTO entrarNaPartida(Long jogadorId, Long partidaId) throws RemoteException {
        try {
            Jogador jogador = jogadorDAO.buscar(jogadorId);
            Partida partida = partidaDAO.buscar(partidaId);
            
            if (partida == null) {
                throw new RemoteException("Partida não encontrada");
            }
            
            if (partida.estaCheia()) {
                throw new RemoteException("Partida já está cheia");
            }
            
            // Verificar se o jogador já está nesta partida
            if (jogador.getPartida() != null && jogador.getPartida().getId().equals(partidaId)) {
                System.out.println("Jogador " + jogadorId + " já está na partida " + partidaId);
                return converterPartidaParaDTO(partida);
            }
            
            // Remover jogador de partida anterior se existir
            if (jogador.getPartida() != null) {
                Partida partidaAnterior = jogador.getPartida();
                System.out.println("Removendo jogador " + jogadorId + " da partida anterior " + partidaAnterior.getId());
                
                partidaAnterior.getJogadores().remove(jogador);
                jogador.setPartida(null);
                jogador.setProntoParaJogar(false);
                
                // Atualizar jogador primeiro para remover a referência
                jogadorDAO.atualizar(jogador);
                partidaDAO.atualizar(partidaAnterior);
                
                // Se a partida anterior ficou vazia, remover
                if (partidaAnterior.getJogadores().isEmpty() && !partidaAnterior.getStatus().equals("FINALIZADA")) {
                    try {
                        System.out.println("Removendo partida vazia " + partidaAnterior.getId());
                        partidaDAO.remover(partidaAnterior.getId());
                    } catch (Exception e) {
                        System.err.println("Não foi possível remover partida vazia ID " + partidaAnterior.getId() + ": " + e.getMessage());
                    }
                }
            }
            
            System.out.println("Adicionando jogador " + jogadorId + " à partida " + partidaId);
            
            // Adicionar jogador à partida
            partida.adicionarJogador(jogador);
            
            // Verificar se o jogador já tem um tabuleiro e removê-lo
            Tabuleiro tabuleiroAntigo = jogador.getTabuleiro();
            if (tabuleiroAntigo != null) {
                System.out.println("Removendo tabuleiro antigo ID " + tabuleiroAntigo.getId() + " do jogador " + jogadorId);
                
                // Forçar carregamento dos navios
                tabuleiroAntigo.getNavios().size();
                
                // Remover navios antigos
                for (Navio navio : new ArrayList<>(tabuleiroAntigo.getNavios())) {
                    try {
                        navioDAO.remover(navio.getId());
                    } catch (Exception e) {
                        System.err.println("Erro ao remover navio: " + e.getMessage());
                    }
                }
                
                // Desassociar e remover o tabuleiro antigo
                Long tabuleiroAntigoId = tabuleiroAntigo.getId();
                jogador.setTabuleiro(null);
                jogadorDAO.atualizar(jogador);
                
                try {
                    tabuleiroDAO.remover(tabuleiroAntigoId);
                    System.out.println("Tabuleiro antigo " + tabuleiroAntigoId + " removido com sucesso");
                } catch (Exception e) {
                    System.err.println("Erro ao remover tabuleiro antigo: " + e.getMessage());
                }
            }
            
            // Criar novo tabuleiro
            System.out.println("Criando novo tabuleiro para jogador " + jogadorId + " com tamanho " + partida.getTamanhoTabuleiro());
            Tabuleiro novoTabuleiro = new Tabuleiro();
            novoTabuleiro.setTamanho(partida.getTamanhoTabuleiro());
            novoTabuleiro.setJogador(jogador);
            jogador.setTabuleiro(novoTabuleiro);
            
            // Resetar status do jogador
            jogador.setProntoParaJogar(false);
            
            // Atualizar jogador e partida
            jogadorDAO.atualizar(jogador);
            partidaDAO.atualizar(partida);
            
            return converterPartidaParaDTO(partida);
        } catch (Exception e) {
            throw new RemoteException("Erro ao entrar na partida", e);
        }
    }
    
    @Override
    public List<PartidaDTO> listarPartidasDisponiveis() throws RemoteException {
        try {
            // Limpar partidas abandonadas antes de listar
            limparPartidasAbandonadas();
            
            List<Partida> partidas = partidaDAO.buscarPartidasAguardando();
            List<PartidaDTO> partidasDTO = new ArrayList<>();
            
            System.out.println("=== LISTANDO PARTIDAS DISPONÍVEIS ===");
            
            for (Partida partida : partidas) {
                // Forçar carregamento dos jogadores
                partida.getJogadores().size();
                
                System.out.println("Partida ID: " + partida.getId() + 
                                 " | Jogadores: " + partida.getJogadores().size() + 
                                 " | Status: " + partida.getStatus());
                
                if (!partida.estaCheia()) {
                    partidasDTO.add(converterPartidaParaDTO(partida));
                }
            }
            
            System.out.println("Total de partidas disponíveis: " + partidasDTO.size());
            
            return partidasDTO;
        } catch (Exception e) {
            throw new RemoteException("Erro ao listar partidas", e);
        }
    }
    
    @Override
    public PartidaDTO obterEstadoPartida(Long partidaId) throws RemoteException {
        try {
            Partida partida = partidaDAO.buscar(partidaId);
            return converterPartidaParaDTO(partida);
        } catch (Exception e) {
            throw new RemoteException("Erro ao obter estado da partida", e);
        }
    }
    
    @Override
    public boolean posicionarNavios(Long jogadorId, List<NavioDTO> naviosDTO) throws RemoteException {
        try {
            Jogador jogador = jogadorDAO.buscar(jogadorId);
            Tabuleiro tabuleiro = jogador.getTabuleiro();
            
            // Limpar navios anteriores
            for (Navio navio : tabuleiro.getNavios()) {
                navioDAO.remover(navio.getId());
            }
            tabuleiro.getNavios().clear();
            
            // Adicionar novos navios
            for (NavioDTO navioDTO : naviosDTO) {
                Navio navio = new Navio(
                    navioDTO.getTipo(),
                    navioDTO.getLinhaInicial(),
                    navioDTO.getColunaInicial(),
                    navioDTO.getOrientacao()
                );
                tabuleiro.adicionarNavio(navio);
                
                // Atualizar matriz do tabuleiro
                atualizarMatrizComNavio(tabuleiro, navio);
            }
            
            tabuleiroDAO.atualizar(tabuleiro);
            return true;
        } catch (Exception e) {
            throw new RemoteException("Erro ao posicionar navios", e);
        }
    }
    
    @Override
    public void confirmarPronto(Long jogadorId) throws RemoteException {
        try {
            Jogador jogador = jogadorDAO.buscar(jogadorId);
            jogador.setProntoParaJogar(true);
            jogadorDAO.atualizar(jogador);
            
            // Verificar se ambos os jogadores estão prontos
            Partida partida = jogador.getPartida();
            if (partida.estaCheia()) {
                boolean todosProntos = true;
                for (Jogador j : partida.getJogadores()) {
                    if (!j.isProntoParaJogar()) {
                        todosProntos = false;
                        break;
                    }
                }
                
                if (todosProntos) {
                    partida.iniciar();
                    partidaDAO.atualizar(partida);
                }
            }
        } catch (Exception e) {
            throw new RemoteException("Erro ao confirmar pronto", e);
        }
    }
    
    @Override
    public ResultadoJogadaDTO realizarJogada(Long jogadorId, int linha, int coluna) throws RemoteException {
        try {
            Jogador jogador = jogadorDAO.buscar(jogadorId);
            Partida partida = jogador.getPartida();
            
            // Verificar se é o turno do jogador
            if (!partida.getTurnoJogadorId().equals(jogadorId)) {
                return new ResultadoJogadaDTO("ERRO", false, null, "Não é seu turno!");
            }
            
            // Encontrar o adversário
            Jogador adversario = null;
            for (Jogador j : partida.getJogadores()) {
                if (!j.getId().equals(jogadorId)) {
                    adversario = j;
                    break;
                }
            }
            
            Tabuleiro tabuleiroAdversario = adversario.getTabuleiro();
            
            // Forçar carregamento dos navios (lazy loading)
            tabuleiroAdversario.getNavios().size();
            
            // Verificar se já foi atacado
            String celulaAtual = tabuleiroAdversario.obterCelula(linha, coluna);
            if (celulaAtual.equals("2") || celulaAtual.equals("3")) {
                return new ResultadoJogadaDTO("ERRO", false, null, "Posição já foi atacada!");
            }
            
            // Realizar ataque
            String resultado;
            boolean acertou = false;
            Navio navioAtingido = null;
            
            // Verificar se acertou um navio
            for (Navio navio : tabuleiroAdversario.getNavios()) {
                if (navio.ocupaPosicao(linha, coluna)) {
                    acertou = true;
                    navioAtingido = navio;
                    break;
                }
            }
            
            if (acertou) {
                tabuleiroAdversario.atualizarCelula(linha, coluna, "3"); // Navio atingido
                navioAtingido.receberAtaque();
                navioDAO.atualizar(navioAtingido);
                
                if (navioAtingido.estaAfundado()) {
                    resultado = "AFUNDOU";
                } else {
                    resultado = "ACERTO";
                }
            } else {
                tabuleiroAdversario.atualizarCelula(linha, coluna, "2"); // Água atingida
                resultado = "AGUA";
            }
            
            tabuleiroDAO.atualizar(tabuleiroAdversario);
            
            // Registrar jogada
            Jogada jogada = new Jogada(linha, coluna, jogadorId, adversario.getId());
            jogada.setResultado(resultado);
            partida.adicionarJogada(jogada);
            jogadaDAO.salvar(jogada);
            
            // Verificar vitória
            boolean fimDeJogo = false;
            Long vencedorId = null;
            
            // Forçar carregamento dos navios antes de verificar
            tabuleiroAdversario.getNavios().size();
            
            // Debug: imprimir informações dos navios
            System.out.println("=== VERIFICANDO VITÓRIA ===");
            System.out.println("Total de navios: " + tabuleiroAdversario.getNavios().size());
            for (Navio navio : tabuleiroAdversario.getNavios()) {
                System.out.println("Navio " + navio.getTipo() + ": " + navio.getPartesAtingidas() + "/" + navio.getTamanho() + " - Afundado: " + navio.estaAfundado());
            }
            
            if (tabuleiroAdversario.todosNaviosAfundados()) {
                System.out.println("TODOS OS NAVIOS AFUNDADOS! FIM DE JOGO!");
                fimDeJogo = true;
                vencedorId = jogadorId;
                partida.finalizar(vencedorId);
                partidaDAO.atualizar(partida);
            } else {
                // Alternar turno APENAS se errou (água)
                // Se acertou ou afundou, o jogador joga novamente
                if (resultado.equals("AGUA")) {
                    partida.setTurnoJogadorId(adversario.getId());
                    partidaDAO.atualizar(partida);
                }
                // Se acertou, mantém o turno do jogador atual (não faz nada)
            }
            
            String mensagem = resultado.equals("AGUA") ? "Água! Vez do adversário." : 
                             resultado.equals("ACERTO") ? "Acertou! Jogue novamente!" : "Afundou o navio! Jogue novamente!";
            
            return new ResultadoJogadaDTO(resultado, fimDeJogo, vencedorId, mensagem);
        } catch (Exception e) {
            throw new RemoteException("Erro ao realizar jogada", e);
        }
    }
    
    @Override
    public boolean ehMeuTurno(Long jogadorId) throws RemoteException {
        try {
            Jogador jogador = jogadorDAO.buscar(jogadorId);
            Partida partida = jogador.getPartida();
            return partida != null && partida.getTurnoJogadorId().equals(jogadorId);
        } catch (Exception e) {
            throw new RemoteException("Erro ao verificar turno", e);
        }
    }
    
    @Override
    public String obterMatrizAdversario(Long jogadorId) throws RemoteException {
        try {
            Jogador jogador = jogadorDAO.buscar(jogadorId);
            Partida partida = jogador.getPartida();
            
            // Encontrar adversário
            for (Jogador j : partida.getJogadores()) {
                if (!j.getId().equals(jogadorId)) {
                    return j.getTabuleiro().getMatriz();
                }
            }
            
            return "";
        } catch (Exception e) {
            throw new RemoteException("Erro ao obter matriz do adversário", e);
        }
    }
    
    @Override
    public String obterMinhaMatriz(Long jogadorId) throws RemoteException {
        try {
            Jogador jogador = jogadorDAO.buscar(jogadorId);
            return jogador.getTabuleiro().getMatriz();
        } catch (Exception e) {
            throw new RemoteException("Erro ao obter sua matriz", e);
        }
    }
    
    @Override
    public boolean validarPosicaoNavio(Long jogadorId, NavioDTO navioDTO) throws RemoteException {
        try {
            Jogador jogador = jogadorDAO.buscar(jogadorId);
            Tabuleiro tabuleiro = jogador.getTabuleiro();
            
            // Forçar carregamento dos navios
            if (tabuleiro.getNavios() != null) {
                tabuleiro.getNavios().size();
            }
            
            int tamanho = calcularTamanhoNavio(navioDTO.getTipo());
            
            System.out.println("=== VALIDANDO NAVIO ===");
            System.out.println("Tipo: " + navioDTO.getTipo() + " | Tamanho: " + tamanho);
            System.out.println("Posição: (" + navioDTO.getLinhaInicial() + "," + navioDTO.getColunaInicial() + ")");
            System.out.println("Orientação: " + navioDTO.getOrientacao());
            System.out.println("Navios já posicionados: " + (tabuleiro.getNavios() != null ? tabuleiro.getNavios().size() : 0));
            
            // Verificar limites do tabuleiro
            if (navioDTO.getOrientacao().equals("HORIZONTAL")) {
                if (navioDTO.getColunaInicial() + tamanho > tabuleiro.getTamanho()) {
                    System.out.println("FALHA: Ultrapassa limite horizontal");
                    return false;
                }
            } else {
                if (navioDTO.getLinhaInicial() + tamanho > tabuleiro.getTamanho()) {
                    System.out.println("FALHA: Ultrapassa limite vertical");
                    return false;
                }
            }
            
            // Verificar sobreposição com outros navios
            if (tabuleiro.getNavios() != null) {
                for (Navio navio : tabuleiro.getNavios()) {
                    if (naviosSeColidem(navioDTO, navio, tamanho)) {
                        System.out.println("FALHA: Colide com navio " + navio.getTipo());
                        return false;
                    }
                }
            }
            
            System.out.println("SUCESSO: Posição válida!");
            return true;
        } catch (Exception e) {
            System.err.println("ERRO ao validar: " + e.getMessage());
            e.printStackTrace();
            throw new RemoteException("Erro ao validar posição do navio", e);
        }
    }
    
    private JogadorDTO converterJogadorParaDTO(Jogador jogador) {
        return new JogadorDTO(jogador.getId(), jogador.getNome(), jogador.isProntoParaJogar());
    }
    
    private PartidaDTO converterPartidaParaDTO(Partida partida) {
        PartidaDTO dto = new PartidaDTO();
        dto.setId(partida.getId());
        dto.setStatus(partida.getStatus());
        dto.setTamanhoTabuleiro(partida.getTamanhoTabuleiro());
        dto.setTurnoJogadorId(partida.getTurnoJogadorId());
        dto.setVencedorId(partida.getVencedorId());
        
        List<JogadorDTO> jogadoresDTO = new ArrayList<>();
        for (Jogador jogador : partida.getJogadores()) {
            jogadoresDTO.add(converterJogadorParaDTO(jogador));
        }
        dto.setJogadores(jogadoresDTO);
        
        return dto;
    }
    
    private void atualizarMatrizComNavio(Tabuleiro tabuleiro, Navio navio) {
        int tamanho = navio.getTamanho();
        for (int i = 0; i < tamanho; i++) {
            if (navio.getOrientacao().equals("HORIZONTAL")) {
                tabuleiro.atualizarCelula(navio.getLinhaInicial(), navio.getColunaInicial() + i, "1");
            } else {
                tabuleiro.atualizarCelula(navio.getLinhaInicial() + i, navio.getColunaInicial(), "1");
            }
        }
    }
    
    private int calcularTamanhoNavio(String tipo) {
        switch (tipo) {
            case "PORTA_AVIOES": return 5;
            case "CRUZADOR": return 4;
            case "CONTRATORPEDEIRO": return 3;
            case "SUBMARINO": return 2;
            default: return 1;
        }
    }
    
    private boolean naviosSeColidem(NavioDTO novoNavio, Navio navioExistente, int tamanhoNovo) {
        for (int i = 0; i < tamanhoNovo; i++) {
            int linha, coluna;
            if (novoNavio.getOrientacao().equals("HORIZONTAL")) {
                linha = novoNavio.getLinhaInicial();
                coluna = novoNavio.getColunaInicial() + i;
            } else {
                linha = novoNavio.getLinhaInicial() + i;
                coluna = novoNavio.getColunaInicial();
            }
            
            if (navioExistente.ocupaPosicao(linha, coluna)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Remove partidas que estão aguardando há muito tempo ou que estão vazias
     */
    private void limparPartidasAbandonadas() {
        try {
            List<Partida> partidas = partidaDAO.buscarPartidasAguardando();
            long tempoAtual = System.currentTimeMillis();
            long timeout = 30 * 60 * 1000; // 30 minutos
            
            for (Partida partida : partidas) {
                boolean deveLimpar = false;
                
                // Forçar carregamento dos jogadores
                partida.getJogadores().size();
                
                // Remover partidas vazias
                if (partida.getJogadores().isEmpty()) {
                    deveLimpar = true;
                }
                
                // Remover partidas antigas (mais de 30 minutos aguardando)
                if (partida.getDataInicio() != null) {
                    long tempoDecorrido = tempoAtual - partida.getDataInicio().getTime();
                    if (tempoDecorrido > timeout) {
                        deveLimpar = true;
                    }
                }
                
                if (deveLimpar) {
                    System.out.println("Removendo partida abandonada: ID " + partida.getId());
                    
                    // Limpar referências dos jogadores ANTES de remover a partida
                    for (Jogador jogador : new ArrayList<>(partida.getJogadores())) {
                        jogador.setPartida(null);
                        jogador.setProntoParaJogar(false);
                        jogadorDAO.atualizar(jogador);
                    }
                    
                    // Agora sim, remover a partida
                    try {
                        partidaDAO.remover(partida.getId());
                    } catch (Exception e) {
                        System.err.println("Erro ao remover partida " + partida.getId() + ": " + e.getMessage());
                    }
                }
            }
            
            // Também limpar partidas canceladas antigas
            limparPartidasCanceladas();
            
        } catch (Exception e) {
            System.err.println("Erro ao limpar partidas abandonadas: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Remove partidas canceladas que estão no banco há mais de 5 minutos
     */
    private void limparPartidasCanceladas() {
        try {
            EntityManager em = JPAUtil.getEntityManager();
            try {
                TypedQuery<Partida> query = em.createQuery(
                    "SELECT p FROM Partida p WHERE p.status = 'CANCELADA'", Partida.class);
                List<Partida> partidasCanceladas = query.getResultList();
                
                long tempoAtual = System.currentTimeMillis();
                long timeout = 5 * 60 * 1000; // 5 minutos
                
                for (Partida partida : partidasCanceladas) {
                    if (partida.getDataInicio() != null) {
                        long tempoDecorrido = tempoAtual - partida.getDataInicio().getTime();
                        if (tempoDecorrido > timeout) {
                            System.out.println("Removendo partida cancelada: ID " + partida.getId());
                            
                            // Forçar carregamento e limpar jogadores ANTES de remover partida
                            partida.getJogadores().size();
                            for (Jogador jogador : new ArrayList<>(partida.getJogadores())) {
                                jogador.setPartida(null);
                                jogador.setProntoParaJogar(false);
                                jogadorDAO.atualizar(jogador);
                            }
                            
                            // Agora remover a partida
                            try {
                                partidaDAO.remover(partida.getId());
                            } catch (Exception e) {
                                System.err.println("Erro ao remover partida cancelada " + partida.getId() + ": " + e.getMessage());
                            }
                        }
                    }
                }
            } finally {
                em.close();
            }
        } catch (Exception e) {
            System.err.println("Erro ao limpar partidas canceladas: " + e.getMessage());
        }
    }
}
