package br.unicentro.lojaveiculos.dao;

import br.unicentro.lojaveiculos.model.Funcionario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

public class FuncionarioDAO implements IFuncionario{
    @Override
    public void inserir(Funcionario funcionario) throws SQLException{
        String sql= "INSERT INTO Funcionario " +
                    "(nome, cpf, cargo, turno, salario) " +
                    "VALUES (?, ?, ?, ?, ?) " +
                    "RETURNING idFuncionario";

        try(Connection conexao = Conexao.getConexao(); PreparedStatement stmt = conexao.prepareStatement(sql)){

            stmt.setString(1, funcionario.getNome());
            stmt.setString(2, funcionario.getCpf());
            stmt.setString(3, funcionario.getCargo());
            stmt.setString(4, funcionario.getTurno());
            stmt.setDouble(5, funcionario.getSalario());

            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    funcionario.setIdFuncionario(rs.getInt("idFuncionario"));
                }
            }
        }
    }

    @Override
    public List<Funcionario> consultar() throws SQLException{

        List<Funcionario> funcionarios = new ArrayList<>();

        String sql= "SELECT * FROM Funcionario" +
                    " ORDER BY idFuncionario";
        try (Connection conexao = Conexao.getConexao(); PreparedStatement stmt = conexao.prepareStatement(sql); ResultSet rs = stmt.executeQuery()){

            while(rs.next()){

                Funcionario funcionario = new Funcionario();

                funcionario.setIdFuncionario(rs.getInt("idFuncionario"));
                funcionario.setNome(rs.getString("nome"));
                funcionario.setCpf(rs.getString("cpf"));
                funcionario.setCargo(rs.getString("cargo"));
                funcionario.setTurno(rs.getString("turno"));
                funcionario.setSalario(rs.getDouble("salario"));

                funcionarios.add(funcionario);
            }
        }
        return funcionarios;
    }

    @Override
    public void alterar(Funcionario funcionario) throws SQLException{

        String sql= "UPDATE Funcionario SET " +
                    "nome = ?, " +
                    "cpf = ?, " +
                    "cargo = ?, " +
                    "turno = ?, " +
                    "salario = ? " +
                    "WHERE idFuncionario = ?";

        try(Connection conexao = Conexao.getConexao(); PreparedStatement stmt = conexao.prepareStatement(sql)){

            stmt.setString(1, funcionario.getNome());
            stmt.setString(2, funcionario.getCpf());
            stmt.setString(3, funcionario.getCargo());
            stmt.setString(4, funcionario.getTurno());
            stmt.setDouble(5, funcionario.getSalario());

            stmt.setInt(6, funcionario.getIdFuncionario());

            stmt.executeUpdate();

        }
    }

    @Override
    public void excluir(int idFuncionario) throws SQLException{

        String sql= "DELETE FROM Funcionario " +
                    "WHERE idFuncionario = ?";

        try (Connection conexao = Conexao.getConexao(); PreparedStatement stmt = conexao.prepareStatement(sql)){
            stmt.setInt(1, idFuncionario);
            stmt.executeUpdate();
        }
    }
}
