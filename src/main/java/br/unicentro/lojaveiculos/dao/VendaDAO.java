package br.unicentro.lojaveiculos.dao;

import br.unicentro.lojaveiculos.model.Venda;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import java.util.ArrayList;
import java.util.List;

public class VendaDAO implements IVenda{

    @Override
    public void inserir(Venda venda) throws SQLException{
        try(Connection conexao = Conexao.getConexao()){
            inserir(venda, conexao);
        }
    }

    public void inserir(Venda venda, Connection conexao) throws SQLException{
        String sql= "INSERT INTO Venda " +
                    "(idFuncionario, idVeiculo, dataVenda, valorTotal, dataPagamento) " +
                    "VALUES (?, ?, ?, ?, ?) " +
                    "RETURNING idVenda";

        try(PreparedStatement stmt = conexao.prepareStatement(sql)){

            stmt.setInt(1, venda.getIdFuncionario());
            stmt.setInt(2, venda.getIdVeiculo());
            stmt.setDate(3, java.sql.Date.valueOf(venda.getDataVenda()));
            stmt.setDouble(4, venda.getValorTotal());

            if(venda.getDataPagamento() != null){
                stmt.setDate(5, java.sql.Date.valueOf(venda.getDataPagamento()));
            } else {
                stmt.setNull(5, Types.DATE);
            }

            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    venda.setIdVenda(rs.getInt("idVenda"));
                } else {
                    throw new SQLException("Não foi possível registrar a venda!");
                }
            }

        }
    }

    @Override
    public List<Venda> consultar() throws SQLException{

        List<Venda> vendas = new ArrayList<>();

        String sql= "SELECT * FROM Venda " +
                    "ORDER BY idVenda";

        try(Connection conexao = Conexao.getConexao(); PreparedStatement stmt = conexao.prepareStatement(sql); ResultSet rs = stmt.executeQuery()){

            while(rs.next()){

                Venda venda = new Venda();

                venda.setIdVenda(rs.getInt("idVenda"));
                venda.setIdFuncionario(rs.getInt("idFuncionario"));
                venda.setIdVeiculo(rs.getInt("idVeiculo"));
                venda.setDataVenda(rs.getDate("dataVenda").toLocalDate());
                venda.setValorTotal(rs.getDouble("valorTotal"));
                java.sql.Date dataPagamento = rs.getDate("dataPagamento");

                if(dataPagamento != null){
                    venda.setDataPagamento(dataPagamento.toLocalDate());
                } else {
                    venda.setDataPagamento(null);
                }
                vendas.add(venda);
            }
        }
        return vendas;
    }

    @Override
    public void alterar(Venda venda) throws SQLException{
        try(Connection conexao = Conexao.getConexao()){
            alterar(venda, conexao);
        }
    }

    public void alterar(Venda venda, Connection conexao) throws SQLException{

        String sql= "UPDATE Venda SET " +
                    "idFuncionario= ?, " +
                    "idVeiculo = ?, " +
                    "dataVenda = ?, " +
                    "valorTotal = ?, " +
                    "dataPagamento = ? " +
                    "WHERE idVenda = ?";

        try(PreparedStatement stmt = conexao.prepareStatement(sql)){

            stmt.setInt(1, venda.getIdFuncionario());
            stmt.setInt(2, venda.getIdVeiculo());
            stmt.setDate(3, java.sql.Date.valueOf(venda.getDataVenda()));
            stmt.setDouble(4, venda.getValorTotal());

            if(venda.getDataPagamento() != null){
                stmt.setDate(5, java.sql.Date.valueOf(venda.getDataPagamento()));
            } else {
                stmt.setNull(5, Types.DATE);
            }

            stmt.setInt(6, venda.getIdVenda());
            int linhasAlteradas = stmt.executeUpdate();

            if(linhasAlteradas == 0){
                throw new SQLException("Venda não encontrada para alteração!");
            }
        }
    }

    @Override
    public void excluir(int idVenda) throws SQLException{
        try(Connection conexao = Conexao.getConexao()){
            excluir(idVenda,conexao);
        }
    }

    public void excluir(int idVenda, Connection conexao) throws SQLException{

        String sql = "DELETE FROM Venda Where idVenda = ?";

        try(PreparedStatement stmt = conexao.prepareStatement(sql)){

            stmt.setInt(1, idVenda);
            int linhasExcluidas = stmt.executeUpdate();

            if(linhasExcluidas == 0){
                throw new SQLException("Venda não encontrada para exclusão!");
            }
        }
    }

    public int buscarIdVeiculoParaExclusao(int idVenda, Connection conexao) throws SQLException{

        String sql= "SELECT idVeiculo FROM Venda " +
                    "WHERE idVenda = ? FOR UPDATE";

        try(PreparedStatement stmt = conexao.prepareStatement(sql)){

            stmt.setInt(1, idVenda);

            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    return rs.getInt("idVeiculo");
                }
                throw new SQLException("Venda não encontrada para exclusão!");
            }

        }

    }
}
