package br.unicentro.lojaveiculos.service;

import br.unicentro.lojaveiculos.dao.Conexao;
import br.unicentro.lojaveiculos.dao.VeiculoDAO;
import br.unicentro.lojaveiculos.dao.VendaDAO;
import br.unicentro.lojaveiculos.model.Veiculo;
import br.unicentro.lojaveiculos.model.Venda;
import br.unicentro.lojaveiculos.observer.VendaObserver;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VendaService {

    private final VendaDAO vendaDAO = new VendaDAO();
    private final VeiculoDAO veiculoDAO = new VeiculoDAO();

    // Padrão Observer: VendaService atua como Subject mantendo a lista de observadores
    private final List<VendaObserver> observers = new ArrayList<>();

    public void adicionarObserver(VendaObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void removerObserver(VendaObserver observer) {
        observers.remove(observer);
    }

    private void notificarObservers(String tipoOperacao, Venda venda, String detalhes) {
        for (VendaObserver observer : observers) {
            observer.aoConfirmarOperacaoVenda(tipoOperacao, venda, detalhes);
        }
    }

    public void validarCadastro(Venda venda, Veiculo veiculo) {

        if (venda == null || veiculo == null || venda.getIdFuncionario() <= 0 || venda.getIdVeiculo() <= 0 || venda.getDataVenda() == null) {
            throw new IllegalArgumentException("Preencha todos os campos obrigatórios!!");
        }

        if (!"DISPONIVEL".equals(veiculo.getStatus())) {
            throw new IllegalArgumentException("O veículo selecionado não está disponível!");
        }

        if (!Double.isFinite(venda.getValorTotal()) || venda.getValorTotal() <= 0) {
            throw new IllegalArgumentException("O valor da venda deve ser maior que zero!");
        }

        if (venda.getDataPagamento() != null && venda.getDataPagamento().isBefore(venda.getDataVenda())) {
            throw new IllegalArgumentException("A data de pagamento não pode ser anterior à data da venda!");
        }
    }

    public void cadastrar(Venda venda, Veiculo veiculo) throws SQLException {

        validarCadastro(venda, veiculo);

        try (Connection conexao = Conexao.getConexao()) {
            conexao.setAutoCommit(false);

            try {
                veiculoDAO.marcarComoVendido(veiculo.getIdVeiculo(), conexao);
                vendaDAO.inserir(venda, conexao);
                conexao.commit();
                veiculo.setStatus("VENDIDO");

                notificarObservers(
                        "CADASTRO",
                        venda,
                        "Veículo vendido: " + veiculo.getMarca() + " " + veiculo.getModelo() + " (Placa: " + veiculo.getPlaca() + ")"
                );

            } catch (SQLException | RuntimeException e) {
                try {
                    conexao.rollback();
                } catch (SQLException erroRollBack) {
                    e.addSuppressed(erroRollBack);
                }
                throw e;
            }
        }
    }

    public void excluir(int idVenda) throws SQLException {

        if (idVenda <= 0) {
            throw new IllegalArgumentException("Selecione uma venda válida!");
        }

        try (Connection conexao = Conexao.getConexao()) {
            conexao.setAutoCommit(false);
            try {
                int idVeiculo = vendaDAO.buscarIdVeiculoParaExclusao(idVenda, conexao);
                vendaDAO.excluir(idVenda, conexao);
                veiculoDAO.marcarComoDisponivel(idVeiculo, conexao);
                conexao.commit();

                Venda vendaExcluida = new Venda();
                vendaExcluida.setIdVenda(idVenda);
                vendaExcluida.setIdVeiculo(idVeiculo);

                notificarObservers(
                        "EXCLUSAO",
                        vendaExcluida,
                        "Venda removida e Veículo ID " + idVeiculo + " retornado para DISPONIVEL"
                );

            } catch (SQLException | RuntimeException e) {
                try {
                    conexao.rollback();
                } catch (SQLException erroRollBack) {
                    e.addSuppressed(erroRollBack);
                }
                throw e;
            }
        }
    }

    public void alterar(Venda venda, Veiculo veiculo) throws SQLException {

        if (venda == null || veiculo == null || venda.getIdVenda() <= 0 ||
                venda.getIdFuncionario() <= 0 || venda.getIdVeiculo() <= 0 || venda.getDataVenda() == null) {
            throw new IllegalArgumentException("Preencha todos os campos obrigatórios da venda!");
        }

        if (venda.getIdVeiculo() != veiculo.getIdVeiculo()) {
            throw new IllegalArgumentException("O veículo informado não corresponde à venda!");
        }

        if (!Double.isFinite(venda.getValorTotal()) || venda.getValorTotal() <= 0) {
            throw new IllegalArgumentException("O valor da venda deve ser maior que zero!");
        }

        if (venda.getDataPagamento() != null && venda.getDataPagamento().isBefore(venda.getDataVenda())) {
            throw new IllegalArgumentException("A data de pagamento não pode ser anterior à data da venda!");
        }

        try (Connection conexao = Conexao.getConexao()) {
            conexao.setAutoCommit(false);

            try {
                int idVeiculoAntigo = vendaDAO.buscarIdVeiculoParaExclusao(venda.getIdVenda(), conexao);
                int idVeiculoNovo = venda.getIdVeiculo();

                if (idVeiculoAntigo != idVeiculoNovo) {
                    veiculoDAO.marcarComoVendido(idVeiculoNovo, conexao);
                }

                vendaDAO.alterar(venda, conexao);

                if (idVeiculoAntigo != idVeiculoNovo) {
                    veiculoDAO.marcarComoDisponivel(idVeiculoAntigo, conexao);
                }

                conexao.commit();

                if (idVeiculoAntigo != idVeiculoNovo) {
                    veiculo.setStatus("VENDIDO");
                }

                notificarObservers(
                        "ALTERACAO",
                        venda,
                        "Veículo vinculado: " + veiculo.getMarca() + " " + veiculo.getModelo() + " (Placa: " + veiculo.getPlaca() + ")"
                );

            } catch (SQLException | RuntimeException e) {
                try {
                    conexao.rollback();
                } catch (SQLException erroRollBack) {
                    e.addSuppressed(erroRollBack);
                }
                throw e;
            }
        }
    }

    public List<Venda> consultar() throws SQLException {
        return vendaDAO.consultar();
    }
}