package br.unicentro.lojaveiculos.dao;

import br.unicentro.lojaveiculos.model.Veiculo;

import java.sql.SQLException;
import java.util.List;

public interface IVeiculo {

    void inserir(Veiculo veiculo) throws SQLException;

    List<Veiculo> consultar() throws SQLException;

    void alterar(Veiculo veiculo) throws SQLException;

    void excluir (int idVeiculo) throws SQLException;
}
