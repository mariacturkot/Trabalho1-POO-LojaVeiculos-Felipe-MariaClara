package br.unicentro.lojaveiculos.dao;

import br.unicentro.lojaveiculos.model.Funcionario;

import java.sql.SQLException;
import java.util.List;

public interface IFuncionario {
    void inserir(Funcionario funcionario) throws SQLException;

    List<Funcionario> consultar() throws SQLException;

    void alterar(Funcionario funcionario) throws SQLException;

    void excluir(int idFuncionario) throws SQLException;
}
