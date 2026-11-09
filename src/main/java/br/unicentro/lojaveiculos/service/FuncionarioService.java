package br.unicentro.lojaveiculos.service;

import br.unicentro.lojaveiculos.dao.FuncionarioDAO;
import br.unicentro.lojaveiculos.model.Funcionario;

import java.sql.SQLException;
import java.util.List;

public class FuncionarioService {

    private final FuncionarioDAO funcionarioDAO = new FuncionarioDAO();

    public void validarFuncionario(Funcionario funcionario) {
        if (funcionario == null) {
            throw new IllegalArgumentException("Informe os dados do funcionário!");
        }

        if (funcionario.getNome() == null || funcionario.getNome().isBlank() ||
                funcionario.getCpf() == null || funcionario.getCpf().isBlank() ||
                funcionario.getCargo() == null || funcionario.getCargo().isBlank() ||
                funcionario.getTurno() == null || funcionario.getTurno().isBlank()) {
            throw new IllegalArgumentException("Preencha todos os campos obrigatórios!");
        }

        if (funcionario.getCpf().length() != 11) {
            throw new IllegalArgumentException("O CPF deve possuir 11 números!");
        }

        if (!Double.isFinite(funcionario.getSalario()) || funcionario.getSalario() <= 0) {
            throw new IllegalArgumentException("O salário deve ser maior que zero!");
        }
    }

    public void cadastrar(Funcionario funcionario) throws SQLException {
        validarFuncionario(funcionario);
        funcionarioDAO.inserir(funcionario);
    }

    public void alterar(Funcionario funcionario) throws SQLException {
        validarFuncionario(funcionario);

        if (funcionario.getIdFuncionario() <= 0) {
            throw new IllegalArgumentException("Selecione um funcionário válido!");
        }

        funcionarioDAO.alterar(funcionario);
    }

    public void excluir(int idFuncionario) throws SQLException {
        if (idFuncionario <= 0) {
            throw new IllegalArgumentException("Selecione um funcionário válido!");
        }

        funcionarioDAO.excluir(idFuncionario);
    }

    public List<Funcionario> consultar() throws SQLException {
        return funcionarioDAO.consultar();
    }
}