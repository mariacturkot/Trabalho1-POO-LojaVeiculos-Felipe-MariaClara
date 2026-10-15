package br.unicentro.lojaveiculos.dao;

import br.unicentro.lojaveiculos.model.Venda;

import java.sql.SQLException;
import java.util.List;

public interface IVenda {

    void inserir(Venda venda) throws SQLException;

    List<Venda> consultar() throws SQLException;

    void alterar(Venda venda) throws SQLException;

    void excluir(int idVenda) throws SQLException;
}
