package br.unicentro.lojaveiculos.service;

import br.unicentro.lojaveiculos.dao.VeiculoDAO;
import br.unicentro.lojaveiculos.model.Veiculo;
import br.unicentro.lojaveiculos.strategy.OrdenacaoPadraoStrategy;
import br.unicentro.lojaveiculos.strategy.OrdenacaoVeiculoStrategy;

import java.sql.SQLException;
import java.util.List;

public class VeiculoService {

    private final VeiculoDAO veiculoDAO = new VeiculoDAO();

    // Padrão Strategy: VeiculoService atua como Context mantendo a estratégia ativa
    private OrdenacaoVeiculoStrategy estrategiaOrdenacao = new OrdenacaoPadraoStrategy();

    public void setEstrategiaOrdenacao(OrdenacaoVeiculoStrategy estrategiaOrdenacao) {
        if (estrategiaOrdenacao != null) {
            this.estrategiaOrdenacao = estrategiaOrdenacao;
        }
    }

    public void validarVeiculo(Veiculo veiculo) {

        if (veiculo == null) {
            throw new IllegalArgumentException("Informe os dados do veículo!");
        }

        if (veiculo.getMarca() == null || veiculo.getMarca().isBlank() ||
                veiculo.getModelo() == null || veiculo.getModelo().isBlank() ||
                veiculo.getPlaca() == null || veiculo.getPlaca().isBlank() ||
                veiculo.getCor() == null || veiculo.getCor().isBlank() ||
                veiculo.getTipo() == null || veiculo.getTipo().isBlank() ||
                veiculo.getStatus() == null) {

            throw new IllegalArgumentException("Preencha todos os campos obrigatórios!");
        }

        if (veiculo.getPlaca().length() != 7) {
            throw new IllegalArgumentException("A placa deve possuir 7 caracteres!");
        }

        if (!Double.isFinite(veiculo.getValor()) || veiculo.getValor() <= 0) {
            throw new IllegalArgumentException("O valor deve ser maior que zero!");
        }

        if (veiculo.getAno() < 1900) {
            throw new IllegalArgumentException("Informe um ano válido!");
        }

        if (!List.of("DISPONIVEL", "RESERVADO", "VENDIDO").contains(veiculo.getStatus())) {
            throw new IllegalArgumentException("Informe um status válido!");
        }
    }

    public void cadastrar(Veiculo veiculo) throws SQLException {
        validarVeiculo(veiculo);

        if ("VENDIDO".equals(veiculo.getStatus())) {
            throw new IllegalArgumentException("Não é permitido cadastrar um veículo diretamente como VENDIDO! Cadastre-o como DISPONIVEL e registre a venda na tela de Vendas.");
        }

        veiculoDAO.inserir(veiculo);
    }

    public void alterar(Veiculo veiculo) throws SQLException {
        validarVeiculo(veiculo);

        if (veiculo.getIdVeiculo() <= 0) {
            throw new IllegalArgumentException("Selecione um veículo válido!");
        }

        Veiculo veiculoAtual = veiculoDAO.buscarPorId(veiculo.getIdVeiculo());
        if (veiculoAtual == null) {
            throw new IllegalArgumentException("Veículo não encontrado no banco de dados!");
        }

        if ("VENDIDO".equals(veiculoAtual.getStatus()) && !"VENDIDO".equals(veiculo.getStatus())) {
            throw new IllegalArgumentException("Não é permitido alterar manualmente o status de um veículo VENDIDO! Exclua ou altere a venda correspondente na tela de Vendas.");
        }

        if (!"VENDIDO".equals(veiculoAtual.getStatus()) && "VENDIDO".equals(veiculo.getStatus())) {
            throw new IllegalArgumentException("Para marcar um veículo como VENDIDO, registre a venda na tela de Vendas!");
        }

        veiculoDAO.alterar(veiculo);
    }

    public void excluir(int idVeiculo) throws SQLException {
        if (idVeiculo <= 0) {
            throw new IllegalArgumentException("Selecione um veículo válido!");
        }

        veiculoDAO.excluir(idVeiculo);
    }

    public List<Veiculo> consultar() throws SQLException {
        return estrategiaOrdenacao.ordenar(veiculoDAO.consultar());
    }

    public List<Veiculo> buscarPorMarcaOuPlaca(String termo) throws SQLException {
        if (termo == null || termo.isBlank()) {
            return consultar();
        }

        return estrategiaOrdenacao.ordenar(veiculoDAO.buscarPorMarcaOuPlaca(termo.trim()));
    }
}