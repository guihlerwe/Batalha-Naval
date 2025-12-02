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
                
                partida.getJogadores().size();
                
                partida.getJogadores().remove(jogador);
                jogador.setPartida(null);
                jogador.setProntoParaJogar(false);
                
                jogadorDAO.atualizar(jogador);
                
                if (!partida.getStatus().equals("FINALIZADA")) {
                    if (partida.getJogadores().isEmpty()) {
                        try {
                            partidaDAO.remover(partidaId);
                            System.out.println("Partida " + partidaId + " removida (ficou vazia)");
                        } catch (Exception e) {
                            System.err.println("Erro ao remover partida vazia: " + e.getMessage());
                        }
                    } else if (partida.getJogadores().size() == 1) {
                        partida.setStatus("CANCELADA");
                        partidaDAO.atualizar(partida);
                        System.out.println("Partida " + partidaId + " marcada como cancelada");
                    } else {
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
            System.out.println("=== ENTRANDO NA PARTIDA " + partidaId + " - JOGADOR " + jogadorId + " ===");
            
            Jogador jogador = jogadorDAO.buscar(jogadorId);
            Partida partida = partidaDAO.buscar(partidaId);
            
            if (partida == null) {
                throw new RemoteException("Partida não encontrada");
            }
            
            if (partida.estaCheia()) {
                throw new RemoteException("Partida já está cheia");
            }
            
            if (jogador.getPartida() != null && jogador.getPartida().getId().equals(partidaId) && jogador.getTabuleiro() != null) {
                System.out.println("Jogador já está na partida com tabuleiro, retornando...");
                return converterPartidaParaDTO(partida);
            }
            
            System.out.println("ETAPA 1: Limpeza de tabuleiros antigos");
            EntityManager emLimpeza = JPAUtil.getEntityManager();
            try {
                emLimpeza.getTransaction().begin();
                
                int naviosRemovidos = emLimpeza.createNativeQuery(
                    "DELETE FROM navio WHERE tabuleiro_id IN (SELECT id FROM tabuleiro WHERE jogador_id = ?)")
                    .setParameter(1, jogadorId)
                    .executeUpdate();
                
                int tabuleirosRemovidos = emLimpeza.createNativeQuery(
                    "DELETE FROM tabuleiro WHERE jogador_id = ?")
                    .setParameter(1, jogadorId)
                    .executeUpdate();
                
                emLimpeza.getTransaction().commit();
                System.out.println("  -> Limpeza concluída: " + naviosRemovidos + " navios, " + tabuleirosRemovidos + " tabuleiro(s)");
                
            } catch (Exception e) {
                if (emLimpeza.getTransaction().isActive()) {
                    emLimpeza.getTransaction().rollback();
                }
                System.err.println("Erro na limpeza: " + e.getMessage());
            } finally {
                if (emLimpeza.isOpen()) {
                    emLimpeza.close();
                }
            }
            
            Thread.sleep(100);
            
            System.out.println("ETAPA 2: Verificação de limpeza");
            EntityManager emVerif = JPAUtil.getEntityManager();
            try {
                Long count = (Long) emVerif.createQuery("SELECT COUNT(t) FROM Tabuleiro t WHERE t.jogador.id = :jogadorId")
                    .setParameter("jogadorId", jogadorId)
                    .getSingleResult();
                System.out.println("  -> Tabuleiros restantes: " + count);
                
                if (count > 0) {
                    System.err.println("  -> ERRO: Ainda existem " + count + " tabuleiro(s)!");
                    throw new RemoteException("Falha ao limpar tabuleiros antigos");
                }
            } finally {
                if (emVerif.isOpen()) {
                    emVerif.close();
                }
            }
            
            System.out.println("ETAPA 3: Recarregando entidades");
            jogador = jogadorDAO.buscar(jogadorId);
            partida = partidaDAO.buscar(partidaId);
            
            if (jogador.getPartida() == null || !jogador.getPartida().getId().equals(partidaId)) {
                System.out.println("  -> Adicionando jogador à partida");
                partida.adicionarJogador(jogador);
                partidaDAO.atualizar(partida);
            }
            
            System.out.println("ETAPA 4: Criando novo tabuleiro " + partida.getTamanhoTabuleiro() + "x" + partida.getTamanhoTabuleiro());
            
            jogador = jogadorDAO.buscar(jogadorId);
            
            Tabuleiro novoTabuleiro = new Tabuleiro();
            novoTabuleiro.setTamanho(partida.getTamanhoTabuleiro());
            novoTabuleiro.setJogador(jogador);
            jogador.setTabuleiro(novoTabuleiro);
            jogador.setProntoParaJogar(false);
            
            jogadorDAO.atualizar(jogador);
            
            System.out.println("SUCESSO: Jogador entrou na partida com novo tabuleiro");
            
            return converterPartidaParaDTO(partida);
            
        } catch (Exception e) {
            System.err.println("ERRO: " + e.getMessage());
            e.printStackTrace();
            throw new RemoteException("Erro ao entrar na partida: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<PartidaDTO> listarPartidasDisponiveis() throws RemoteException {
        try {
            limparPartidasAbandonadas();
            
            List<Partida> partidas = partidaDAO.buscarPartidasAguardando();
            List<PartidaDTO> partidasDTO = new ArrayList<>();
            
            System.out.println("=== LISTANDO PARTIDAS DISPONÍVEIS ===");
            
            for (Partida partida : partidas) {
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
    public void cancelarPartida(Long jogadorId, Long partidaId) throws RemoteException {
        try {
            System.out.println("=== CANCELANDO PARTIDA " + partidaId + " - JOGADOR " + jogadorId + " ===");
            
            Jogador jogador = jogadorDAO.buscar(jogadorId);
            Partida partida = partidaDAO.buscar(partidaId);
            
            if (partida == null) {
                System.out.println("Partida já foi removida");
                return;
            }
            
            for (Jogador j : new ArrayList<>(partida.getJogadores())) {
                j.setPartida(null);
                j.setProntoParaJogar(false);
                jogadorDAO.atualizar(j);
            }
            
            partidaDAO.remover(partidaId);
            
            System.out.println("Partida " + partidaId + " cancelada e removida com sucesso");
            
        } catch (Exception e) {
            System.err.println("Erro ao cancelar partida: " + e.getMessage());
            throw new RemoteException("Erro ao cancelar partida", e);
        }
    }
    
    @Override
    public boolean posicionarNavios(Long jogadorId, List<NavioDTO> naviosDTO) throws RemoteException {
        try {
            Jogador jogador = jogadorDAO.buscar(jogadorId);
            Tabuleiro tabuleiro = jogador.getTabuleiro();
            
            for (Navio navio : tabuleiro.getNavios()) {
                navioDAO.remover(navio.getId());
            }
            tabuleiro.getNavios().clear();
            
            for (NavioDTO navioDTO : naviosDTO) {
                Navio navio = new Navio(
                    navioDTO.getTipo(),
                    navioDTO.getLinhaInicial(),
                    navioDTO.getColunaInicial(),
                    navioDTO.getOrientacao()
                );
                tabuleiro.adicionarNavio(navio);
                
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
            
            if (!partida.getTurnoJogadorId().equals(jogadorId)) {
                return new ResultadoJogadaDTO("ERRO", false, null, "Não é seu turno!");
            }
            
            Jogador adversario = null;
            for (Jogador j : partida.getJogadores()) {
                if (!j.getId().equals(jogadorId)) {
                    adversario = j;
                    break;
                }
            }
            
            Tabuleiro tabuleiroAdversario = adversario.getTabuleiro();
            
            tabuleiroAdversario.getNavios().size();
            
            String celulaAtual = tabuleiroAdversario.obterCelula(linha, coluna);
            if (celulaAtual.equals("2") || celulaAtual.equals("3")) {
                return new ResultadoJogadaDTO("ERRO", false, null, "Posição já foi atacada!");
            }
            
            String resultado;
            boolean acertou = false;
            Navio navioAtingido = null;
            
            for (Navio navio : tabuleiroAdversario.getNavios()) {
                if (navio.ocupaPosicao(linha, coluna)) {
                    acertou = true;
                    navioAtingido = navio;
                    break;
                }
            }
            
            if (acertou) {
                tabuleiroAdversario.atualizarCelula(linha, coluna, "3"); 
                navioAtingido.receberAtaque();
                navioDAO.atualizar(navioAtingido);
                
                if (navioAtingido.estaAfundado()) {
                    resultado = "AFUNDOU";
                } else {
                    resultado = "ACERTO";
                }
            } else {
                tabuleiroAdversario.atualizarCelula(linha, coluna, "2"); 
                resultado = "AGUA";
            }
            
            tabuleiroDAO.atualizar(tabuleiroAdversario);
            
            Jogada jogada = new Jogada(linha, coluna, jogadorId, adversario.getId());
            jogada.setResultado(resultado);
            partida.adicionarJogada(jogada);
            jogadaDAO.salvar(jogada);
            
            boolean fimDeJogo = false;
            Long vencedorId = null;
            
            tabuleiroAdversario.getNavios().size();
            
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
                if (resultado.equals("AGUA")) {
                    partida.setTurnoJogadorId(adversario.getId());
                    partidaDAO.atualizar(partida);
                }
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
            
            if (tabuleiro.getNavios() != null) {
                tabuleiro.getNavios().size();
            }
            
            int tamanho = calcularTamanhoNavio(navioDTO.getTipo());
            
            System.out.println("=== VALIDANDO NAVIO ===");
            System.out.println("Tipo: " + navioDTO.getTipo() + " | Tamanho: " + tamanho);
            System.out.println("Posição: (" + navioDTO.getLinhaInicial() + "," + navioDTO.getColunaInicial() + ")");
            System.out.println("Orientação: " + navioDTO.getOrientacao());
            System.out.println("Navios já posicionados: " + (tabuleiro.getNavios() != null ? tabuleiro.getNavios().size() : 0));
            
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
    
    private void limparTabuleirosOrfaos() {
        try {
            EntityManager em = JPAUtil.getEntityManager();
            try {
                TypedQuery<Tabuleiro> query = em.createQuery(
                    "SELECT t FROM Tabuleiro t WHERE t.jogador IS NULL", Tabuleiro.class);
                List<Tabuleiro> tabuleirosOrfaos = query.getResultList();
                
                for (Tabuleiro tabuleiro : tabuleirosOrfaos) {
                    System.out.println("Removendo tabuleiro órfão ID: " + tabuleiro.getId());
                    try {
                        tabuleiro.getNavios().size();
                        for (Navio navio : new ArrayList<>(tabuleiro.getNavios())) {
                            navioDAO.remover(navio.getId());
                        }
                        tabuleiroDAO.remover(tabuleiro.getId());
                    } catch (Exception e) {
                        System.err.println("Erro ao remover tabuleiro órfão " + tabuleiro.getId() + ": " + e.getMessage());
                    }
                }
            } finally {
                if (em.isOpen()) {
                    em.close();
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao limpar tabuleiros órfãos: " + e.getMessage());
        }
    }
    
    private void limparPartidasAbandonadas() {
        try {
            limparTabuleirosOrfaos();
            
            List<Partida> partidas = partidaDAO.buscarPartidasAguardando();
            long tempoAtual = System.currentTimeMillis();
            long timeout = 30 * 60 * 1000; 
            
            for (Partida partida : partidas) {
                boolean deveLimpar = false;
                
                partida.getJogadores().size();
                
                if (partida.getJogadores().isEmpty()) {
                    deveLimpar = true;
                }
                
                if (partida.getDataInicio() != null) {
                    long tempoDecorrido = tempoAtual - partida.getDataInicio().getTime();
                    if (tempoDecorrido > timeout) {
                        deveLimpar = true;
                    }
                }
                
                if (deveLimpar) {
                    System.out.println("Removendo partida abandonada: ID " + partida.getId());
                    
                    for (Jogador jogador : new ArrayList<>(partida.getJogadores())) {
                        jogador.setPartida(null);
                        jogador.setProntoParaJogar(false);
                        jogadorDAO.atualizar(jogador);
                    }
                    
                    try {
                        partidaDAO.remover(partida.getId());
                    } catch (Exception e) {
                        System.err.println("Erro ao remover partida " + partida.getId() + ": " + e.getMessage());
                    }
                }
            }
            
            limparPartidasCanceladas();
            
        } catch (Exception e) {
            System.err.println("Erro ao limpar partidas abandonadas: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void limparPartidasCanceladas() {
        try {
            EntityManager em = JPAUtil.getEntityManager();
            try {
                TypedQuery<Partida> query = em.createQuery(
                    "SELECT p FROM Partida p WHERE p.status = 'CANCELADA'", Partida.class);
                List<Partida> partidasCanceladas = query.getResultList();
                
                long tempoAtual = System.currentTimeMillis();
                long timeout = 5 * 60 * 1000; 
                
                for (Partida partida : partidasCanceladas) {
                    if (partida.getDataInicio() != null) {
                        long tempoDecorrido = tempoAtual - partida.getDataInicio().getTime();
                        if (tempoDecorrido > timeout) {
                            System.out.println("Removendo partida cancelada: ID " + partida.getId());
                            
                            partida.getJogadores().size();
                            for (Jogador jogador : new ArrayList<>(partida.getJogadores())) {
                                jogador.setPartida(null);
                                jogador.setProntoParaJogar(false);
                                jogadorDAO.atualizar(jogador);
                            }
                            
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
