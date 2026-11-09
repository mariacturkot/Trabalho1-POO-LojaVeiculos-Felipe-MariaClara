package br.unicentro.lojaveiculos.dao;

import br.unicentro.lojaveiculos.model.Veiculo;
import br.unicentro.lojaveiculos.model.VeiculoBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

public class VeiculoDAO implements IVeiculo {

    // Método auxiliar que aplica o Padrão Builder para mapear o ResultSet em um objeto Veiculo
    private Veiculo construirVeiculo(ResultSet rs) throws SQLException {
        return VeiculoBuilder.builder()
                .comIdVeiculo(rs.getInt("idVeiculo"))
                .comMarca(rs.getString("marca"))
                .comPlaca(rs.getString("placa"))
                .comCor(rs.getString("cor"))
                .comValor(rs.getDouble("valor"))
                .comStatus(rs.getString("status"))
                .comTipo(rs.getString("tipo"))
                .comModelo(rs.getString("modelo"))
                .comAno(rs.getInt("ano"))
                .comEspecificacoes(rs.getString("especificacoes"))
                .build();
    }

    @Override
    public void inserir(Veiculo veiculo) throws SQLException {

        String sql = "INSERT INTO Veiculo " +
                "(marca, placa, cor, valor, status, tipo, modelo, ano, especificacoes) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "RETURNING idVeiculo";

        try (Connection conexao = Conexao.getConexao(); PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, veiculo.getMarca());
            stmt.setString(2, veiculo.getPlaca());
            stmt.setString(3, veiculo.getCor());
            stmt.setDouble(4, veiculo.getValor());
            stmt.setString(5, veiculo.getStatus());
            stmt.setString(6, veiculo.getTipo());
            stmt.setString(7, veiculo.getModelo());
            stmt.setInt(8, veiculo.getAno());
            stmt.setString(9, veiculo.getEspecificacoes());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    veiculo.setIdVeiculo(rs.getInt("idVeiculo"));
                }
            }
        }
    }

    @Override
    public List<Veiculo> consultar() throws SQLException {
        List<Veiculo> veiculos = new ArrayList<>();

        String sql = "SELECT * FROM Veiculo ORDER BY idVeiculo";

        try (Connection conexao = Conexao.getConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                veiculos.add(construirVeiculo(rs));
            }
        }
        return veiculos;
    }

    public Veiculo buscarPorId(int idVeiculo) throws SQLException {
        String sql = "SELECT * FROM Veiculo WHERE idVeiculo = ?";

        try (Connection conexao = Conexao.getConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, idVeiculo);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return construirVeiculo(rs);
                }
            }
        }
        return null;
    }

    @Override
    public void alterar(Veiculo veiculo) throws SQLException {

        String sql = "UPDATE veiculo SET" +
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

        try (Connection conexao = Conexao.getConexao(); PreparedStatement stmt = conexao.prepareStatement(sql)) {

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

    public void marcarComoVendido(int idVeiculo, Connection conexao) throws SQLException {

        String sql = "UPDATE Veiculo SET status = ? " +
                "WHERE idVeiculo = ? AND status = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, "VENDIDO");
            stmt.setInt(2, idVeiculo);
            stmt.setString(3, "DISPONIVEL");

            int linhasAlteradas = stmt.executeUpdate();

            if (linhasAlteradas == 0) {
                throw new SQLException("Veiculo não encontrado ou indisponível para venda!");
            }
        }
    }

    public void marcarComoDisponivel(int idVeiculo, Connection conexao) throws SQLException {

        String sql = "UPDATE Veiculo SET status = ? " +
                "WHERE idVeiculo = ? AND status = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, "DISPONIVEL");
            stmt.setInt(2, idVeiculo);
            stmt.setString(3, "VENDIDO");

            int linhasAlteradas = stmt.executeUpdate();
            if (linhasAlteradas == 0) {
                throw new SQLException("Veículo não encontrado ou não está vendido!");
            }
        }
    }

    @Override
    public void excluir(int idVeiculo) throws SQLException {

        String sql = "DELETE FROM Veiculo WHERE idVeiculo = ?";

        try (Connection conexao = Conexao.getConexao(); PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, idVeiculo);
            stmt.executeUpdate();
        }
    }

    public List<Veiculo> buscarPorMarcaOuPlaca(String termoBusca) throws SQLException {
        List<Veiculo> veiculos = new ArrayList<>();

        String sql = "SELECT * FROM Veiculo WHERE marca ILIKE ? OR placa ILIKE ? ORDER BY idVeiculo";

        try (Connection conexao = Conexao.getConexao(); PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, "%" + termoBusca + "%");
            stmt.setString(2, "%" + termoBusca + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    veiculos.add(construirVeiculo(rs));
                }
            }
        }
        return veiculos;
    }
}