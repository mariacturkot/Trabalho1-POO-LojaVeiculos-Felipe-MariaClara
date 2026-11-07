package br.unicentro.lojaveiculos.service;

import br.unicentro.lojaveiculos.dao.VeiculoDAO;
import br.unicentro.lojaveiculos.model.Veiculo;

import java.sql.SQLException;
import java.util.List;

public class VeiculoService {

    private final VeiculoDAO veiculoDAO = new VeiculoDAO();
    public void validarVeiculo(Veiculo veiculo) {

        if (veiculo == null) {
            throw new IllegalArgumentException(
                    "Informe os dados do veículo!");
        }

        if (veiculo.getMarca() == null ||
                veiculo.getMarca().isBlank() ||
                veiculo.getModelo() == null ||
                veiculo.getModelo().isBlank() ||
                veiculo.getPlaca() == null ||
                veiculo.getPlaca().isBlank() ||
                veiculo.getCor() == null ||
                veiculo.getCor().isBlank() ||
                veiculo.getTipo() == null ||
                veiculo.getTipo().isBlank() ||
                veiculo.getStatus() == null) {

            throw new IllegalArgumentException("Preencha todos os campos obrigatórios!");
        }

        if (veiculo.getPlaca().length() != 7) {
            throw new IllegalArgumentException("A placa deve possuir 7 caracteres!");
        }

        if (!Double.isFinite(veiculo.getValor()) ||
                veiculo.getValor() <= 0) {
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
        veiculoDAO.inserir(veiculo);
    }

    public void alterar(Veiculo veiculo) throws SQLException {
        validarVeiculo(veiculo);

        if (veiculo.getIdVeiculo() <= 0) {
            throw new IllegalArgumentException(
                    "Selecione um veículo válido!");
        }

        veiculoDAO.alterar(veiculo);
    }

    public void excluir(int idVeiculo) throws SQLException {
        if (idVeiculo <= 0) {
            throw new IllegalArgumentException(
                    "Selecione um veículo válido!");
        }

        veiculoDAO.excluir(idVeiculo);
    }

    public List<Veiculo> consultar() throws SQLException {
        return veiculoDAO.consultar();
    }

    public List<Veiculo> buscarPorMarcaOuPlaca(String termo)
            throws SQLException {

        if (termo == null || termo.isBlank()) {
            return consultar();
        }

        return veiculoDAO.buscarPorMarcaOuPlaca(termo.trim());
    }
}
