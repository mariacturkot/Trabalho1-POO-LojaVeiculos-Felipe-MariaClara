package br.unicentro.lojaveiculos.dao;

import br.unicentro.lojaveiculos.model.Veiculo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

public class VeiculoDAO implements IVeiculo{

    @Override
    public void inserir(Veiculo veiculo) throws SQLException {

        String sql= "INSERT INTO Veiculo " +
                    "(marca, placa, cor, valor, status, tipo, modelo, ano, especificacoes) " +
                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                    "RETURNING idVeiculo";

        try(Connection conexao = Conexao.getConexao(); PreparedStatement stmt = conexao.prepareStatement(sql)){

            stmt.setString(1, veiculo.getMarca());
            stmt.setString(2, veiculo.getPlaca());
            stmt.setString(3, veiculo.getCor());
            stmt.setDouble(4, veiculo.getValor());
            stmt.setString(5, veiculo.getStatus());
            stmt.setString(6, veiculo.getTipo());
            stmt.setString(7, veiculo.getModelo());
            stmt.setInt(8, veiculo.getAno());
            stmt.setString(9, veiculo.getEspecificacoes());

            try(ResultSet rs = stmt.executeQuery()){

                if (rs.next()){
                    veiculo.setIdVeiculo(rs.getInt("idVeiculo"));
                }
            }
        }
    }

    @Override
    public List<Veiculo> consultar() throws SQLException{
        List<Veiculo> veiculos = new ArrayList<>();

        String sql= "SELECT * FROM Veiculo " +
                    "ORDER BY idVeiculo";

        try(Connection conexao = Conexao.getConexao(); PreparedStatement stmt = conexao.prepareStatement(sql); ResultSet rs = stmt.executeQuery()){

            while(rs.next()){

                Veiculo veiculo = new Veiculo();

                veiculo.setIdVeiculo(rs.getInt("idVeiculo"));
                veiculo.setMarca(rs.getString("marca"));
                veiculo.setPlaca(rs.getString("placa"));
                veiculo.setCor(rs.getString("cor"));
                veiculo.setValor(rs.getDouble("valor"));
                veiculo.setStatus(rs.getString("status"));
                veiculo.setTipo(rs.getString("tipo"));
                veiculo.setModelo(rs.getString("modelo"));
                veiculo.setAno(rs.getInt("ano"));
                veiculo.setEspecificacoes(rs.getString("especificacoes"));

                veiculos.add(veiculo);
            }
        }
        return veiculos;
    }

    @Override
    public void alterar(Veiculo veiculo) throws SQLException{

        String sql= "UPDATE veiculo SET" +
                    " marca = ?, " +
                    "placa = ?, " +
                    "cor = ?, " +
                    "valor = ?, " +
                    "status = ?, " +
                    "tipo = ?, " +
                    "modelo = ?, " +
                    "ano = ?, " +
                    "especificacoes = ? " +
                    "WHERE idVeiculo = ?";

        try(Connection conexao = Conexao.getConexao(); PreparedStatement stmt = conexao.prepareStatement(sql)){

            stmt.setString(1, veiculo.getMarca());
            stmt.setString(2, veiculo.getPlaca());
            stmt.setString(3, veiculo.getCor());
            stmt.setDouble(4, veiculo.getValor());
            stmt.setString(5, veiculo.getStatus());
            stmt.setString(6, veiculo.getTipo());
            stmt.setString(7, veiculo.getModelo());
            stmt.setInt(8, veiculo.getAno());
            stmt.setString(9, veiculo.getEspecificacoes());
            stmt.setInt(10, veiculo.getIdVeiculo());

            stmt.executeUpdate();
        }
    }

    @Override
    public void excluir(int idVeiculo) throws SQLException{

        String sql= "DELETE FROM Veiculo WHERE idVeiculo = ?";

        try(Connection conexao = Conexao.getConexao(); PreparedStatement stmt = conexao.prepareStatement(sql)){
            stmt.setInt(1, idVeiculo);
            stmt.executeUpdate();
        }
    }
}
