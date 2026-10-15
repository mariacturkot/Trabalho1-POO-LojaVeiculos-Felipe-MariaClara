package br.unicentro.lojaveiculos.controller;

import br.unicentro.lojaveiculos.dao.FuncionarioDAO;
import br.unicentro.lojaveiculos.dao.VeiculoDAO;
import br.unicentro.lojaveiculos.dao.VendaDAO;
import br.unicentro.lojaveiculos.main.GerenciadorTelas;
import br.unicentro.lojaveiculos.model.Funcionario;
import br.unicentro.lojaveiculos.model.Veiculo;
import br.unicentro.lojaveiculos.model.Venda;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

public class VendaController {

    @FXML
    private ComboBox<Funcionario> cbFuncionario;

    @FXML
    private ComboBox<Veiculo> cbVeiculo;

    @FXML
    private DatePicker dpDataVenda;

    @FXML
    private DatePicker dpDataPagamento;

    @FXML
    private TextField txtValorTotal;

    @FXML
    private TableView<Venda> tabelaVendas;

    @FXML
    private TableColumn<Venda, Integer> colId;

    @FXML
    private TableColumn<Venda, String> colFuncionario;

    @FXML
    private TableColumn<Venda, String> colVeiculo;

    @FXML
    private TableColumn<Venda, String> colDataVenda;

    @FXML
    private TableColumn<Venda, Double> colValorTotal;

    @FXML
    private TableColumn<Venda, String> colDataPagamento;

    private VendaDAO vendaDAO = new VendaDAO();
    private FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
    private VeiculoDAO veiculoDAO = new VeiculoDAO();

    @FXML
    public void initialize(){

        carregarCombos();

        colId.setCellValueFactory(dado -> new SimpleIntegerProperty(dado.getValue().getIdVenda()).asObject());

        colFuncionario.setCellValueFactory(dado -> {
            Funcionario funcionario = buscarFuncionarioPorId(dado.getValue().getIdFuncionario());
            if(funcionario != null){
                return new SimpleStringProperty(funcionario.getNome());
            }
            return new SimpleStringProperty(String.valueOf(dado.getValue().getIdFuncionario()));
        });

        colVeiculo.setCellValueFactory(dado -> {
            Veiculo veiculo = buscarVeiculoPorId(dado.getValue().getIdVeiculo());
            if(veiculo != null){
                return new SimpleStringProperty(veiculo.getMarca() + " " + veiculo.getModelo() + " " + veiculo.getPlaca());
            }
            return new SimpleStringProperty(String.valueOf(dado.getValue().getIdVeiculo()));
        });

        colDataVenda.setCellValueFactory(dado -> new SimpleStringProperty(String.valueOf(dado.getValue().getDataVenda())));
        colValorTotal.setCellValueFactory(dado -> new SimpleDoubleProperty(dado.getValue().getValorTotal()).asObject());

        colDataPagamento.setCellValueFactory(dado -> {
            if(dado.getValue().getDataPagamento() != null){
                return new SimpleStringProperty(String.valueOf(dado.getValue().getDataPagamento()));
            } else {
                return new SimpleStringProperty("-");
            }
        });

        carregarVendas();
        dpDataVenda.setValue(LocalDate.now());

        tabelaVendas.getSelectionModel().selectedItemProperty().addListener((observable, antigo, selecionado) -> {

            if(selecionado != null){
                cbFuncionario.setValue(buscarFuncionarioPorId(selecionado.getIdFuncionario()));
                cbVeiculo.setValue(buscarVeiculoPorId(selecionado.getIdVeiculo()));
                dpDataVenda.setValue(selecionado.getDataVenda());
                txtValorTotal.setText(String.valueOf(selecionado.getValorTotal()));
                dpDataPagamento.setValue(selecionado.getDataPagamento());
            }
        });
    }

    @FXML
    private void cadastrar(){
        Funcionario funcionario = cbFuncionario.getValue();
        Veiculo veiculo = cbVeiculo.getValue();
        LocalDate dataVenda = dpDataVenda.getValue();
        LocalDate dataPagamento = dpDataPagamento.getValue();
        String valorTexto = txtValorTotal.getText().trim();

        if(funcionario == null || veiculo == null || dataVenda == null || valorTexto.isEmpty()){
            new Alert(Alert.AlertType.WARNING, "Preencha todos os campos obrigatórios!!", ButtonType.OK).showAndWait();
            return;
        }

        if(!veiculo.getStatus().equals("DISPONIVEL")){
            new Alert(Alert.AlertType.WARNING, "O veículo selecionado não está para venda!!", ButtonType.OK).showAndWait();
            return;
        }

        if(dataPagamento != null && dataPagamento.isBefore(dataVenda)){
            new Alert(Alert.AlertType.WARNING, "A data de pagamento não pode ser anterior à data da venda!!", ButtonType.OK).showAndWait();
            return;
        }

        try{
            double valorTotal = Double.parseDouble(valorTexto.replace(",", "."));

            if(valorTotal <= 0){
                new Alert(Alert.AlertType.WARNING, "O valor da venda deve ser maior que zero!!", ButtonType.OK).showAndWait();
                return;
            }

            Venda venda = new Venda();

            venda.setIdFuncionario(funcionario.getIdFuncionario());
            venda.setIdVeiculo(veiculo.getIdVeiculo());
            venda.setDataVenda(dataVenda);
            venda.setValorTotal(valorTotal);
            venda.setDataPagamento(dataPagamento);

            vendaDAO.inserir(venda);

            veiculo.setStatus("VENDIDO");
            veiculoDAO.alterar(veiculo);

            new Alert(Alert.AlertType.INFORMATION, "Venda cadastrada com sucesso!!", ButtonType.OK).showAndWait();

            limparCampos();
            carregarCombos();
            carregarVendas();

        } catch (NumberFormatException e){
            new Alert(Alert.AlertType.WARNING, "Informe um valor válido!!", ButtonType.OK).showAndWait();
        } catch (SQLException e){
            new Alert(Alert.AlertType.ERROR, "Erro ao cadastrar a venda!\n\n" + e.getMessage(), ButtonType.OK).showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    private void alterar(){

        Venda venda = tabelaVendas.getSelectionModel().getSelectedItem();

        if(venda ==  null){
            new Alert(Alert.AlertType.WARNING, "Selecione uma venda na tabela!!", ButtonType.OK).showAndWait();
            return;
        }

        Funcionario funcionario = cbFuncionario.getValue();
        Veiculo veiculo = cbVeiculo.getValue();
        LocalDate dataVenda = dpDataVenda.getValue();
        LocalDate dataPagamento = dpDataPagamento.getValue();
        String valorTexto = txtValorTotal.getText().trim();

        if(funcionario == null || veiculo == null || dataVenda == null || valorTexto.isEmpty()){
            new Alert(Alert.AlertType.WARNING, "Preencha todos os campos obrigatórios!!", ButtonType.OK).showAndWait();
            return;
        }

        if(dataPagamento != null && dataPagamento.isBefore(dataVenda)){
            new Alert(Alert.AlertType.WARNING, "A data de pagamento não pode ser anterior à data da venda!!", ButtonType.OK).showAndWait();
            return;
        }

        int idVeiculoAntigo = venda.getIdVeiculo();

        if(veiculo.getIdVeiculo() != idVeiculoAntigo && !veiculo.getStatus().equals("DISPONIVEL")){
            new Alert(Alert.AlertType.WARNING, "O novo veículo selecionado não está disponível!!", ButtonType.OK).showAndWait();
            return;
        }

        try{
            double valorTotal = Double.parseDouble(valorTexto.replace(",", "."));

            if(valorTotal <= 0){
                new Alert(Alert.AlertType.WARNING, "O valor da venda deve ser maior que zero!!", ButtonType.OK).showAndWait();
                return;
            }

            venda.setIdFuncionario(funcionario.getIdFuncionario());
            venda.setIdVeiculo(veiculo.getIdVeiculo());
            venda.setDataVenda(dataVenda);
            venda.setValorTotal(valorTotal);
            venda.setDataPagamento(dataPagamento);

            vendaDAO.alterar(venda);

            if(idVeiculoAntigo != veiculo.getIdVeiculo()){
                Veiculo veiculoAntigo = buscarVeiculoPorId(idVeiculoAntigo);

                if(veiculoAntigo != null){
                    veiculoAntigo.setStatus("DISPONIVEL");
                    veiculoDAO.alterar(veiculoAntigo);
                }

                veiculo.setStatus("VENDIDO");
                veiculoDAO.alterar(veiculo);
            }

            new Alert(Alert.AlertType.INFORMATION, "Venda alterada com sucesso!!", ButtonType.OK).showAndWait();

            limparCampos();
            carregarCombos();
            carregarVendas();

        } catch (NumberFormatException e){
            new Alert(Alert.AlertType.WARNING, "Informe um valor válido!!", ButtonType.OK).showAndWait();
        } catch (SQLException e){
            new Alert(Alert.AlertType.ERROR, "Erro ao alterar venda!!", ButtonType.OK).showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    private void excluir(){
        Venda venda = tabelaVendas.getSelectionModel().getSelectedItem();

        if(venda == null){
            new Alert(Alert.AlertType.WARNING,"Selecione uma venda na tabela!!", ButtonType.OK).showAndWait();
            return;
        }

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION, "Deseja realmente excluir esta venda?", ButtonType.YES, ButtonType.NO);
        confirmacao.showAndWait();

        if(confirmacao.getResult() == ButtonType.YES){
            try{
                Veiculo veiculo = buscarVeiculoPorId(venda.getIdVeiculo());
                vendaDAO.excluir(venda.getIdVenda());

                if(veiculo != null){
                    veiculo.setStatus("DISPONIVEL");
                    veiculoDAO.alterar(veiculo);
                }

                new Alert(Alert.AlertType.INFORMATION, "Venda excluída com sucesso!!", ButtonType.NO).showAndWait();

                limparCampos();
                carregarCombos();
                carregarVendas();

            } catch (SQLException e){
                new Alert(Alert.AlertType.ERROR, "Erro ao excluir a venda!!", ButtonType.NO).showAndWait();
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void limpar(){
        limparCampos();
    }

    private void limparCampos(){
        cbFuncionario.setValue(null);
        cbVeiculo.setValue(null);
        dpDataVenda.setValue(LocalDate.now());
        dpDataPagamento.setValue(null);
        txtValorTotal.clear();

        tabelaVendas.getSelectionModel().clearSelection();
    }

    private void carregarCombos(){
        try {
            cbFuncionario.setItems(FXCollections.observableArrayList(funcionarioDAO.consultar()));
            cbVeiculo.setItems(FXCollections.observableArrayList(veiculoDAO.consultar()));
        } catch (SQLException e){
            new Alert(Alert.AlertType.ERROR, "Erro ao carregar funcionários ou veículos!!", ButtonType.NO).showAndWait();
            e.printStackTrace();
        }
    }

    private void carregarVendas(){
        try{
            tabelaVendas.setItems(FXCollections.observableArrayList(vendaDAO.consultar()));
        } catch (SQLException e){
            new Alert(Alert.AlertType.ERROR, "Erro ao consultar vendas!!", ButtonType.NO).showAndWait();
            e.printStackTrace();
        }
    }

    private Funcionario buscarFuncionarioPorId(int idFuncionario){
        for (Funcionario funcionario : cbFuncionario.getItems()){
            if (funcionario.getIdFuncionario() == idFuncionario){
                return funcionario;
            }
        }
        return null;
    }

    private Veiculo buscarVeiculoPorId(int idVeiculo){
        for (Veiculo veiculo : cbVeiculo.getItems()){
            if (veiculo.getIdVeiculo() == idVeiculo){
                return veiculo;
            }
        }
        return null;
    }

    @FXML private void voltar(){
        try{
            GerenciadorTelas.trocarTela("/view/menu.fxml");
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
