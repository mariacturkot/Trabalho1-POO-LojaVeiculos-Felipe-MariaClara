package br.unicentro.lojaveiculos.controller;

import br.unicentro.lojaveiculos.dao.VeiculoDAO;
import br.unicentro.lojaveiculos.main.GerenciadorTelas;
import br.unicentro.lojaveiculos.model.Veiculo;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.SQLException;

public class VeiculoController {

    @FXML
    private TextField txtMarca;

    @FXML
    private TextField txtModelo;

    @FXML
    private TextField txtPlaca;

    @FXML
    private TextField txtAno;

    @FXML
    private TextField txtCor;

    @FXML
    private TextField txtTipo;

    @FXML
    private TextField txtValor;

    @FXML
    private ComboBox<String> cbStatus;

    @FXML
    private TextArea txtEspecificacoes;

    @FXML
    private TableView<Veiculo> tabelaVeiculos;

    @FXML
    private TableColumn<Veiculo, Integer> colId;

    @FXML
    private TableColumn<Veiculo, String> colMarca;

    @FXML
    private TableColumn<Veiculo, String> colModelo;

    @FXML
    private TableColumn<Veiculo, String> colPlaca;

    @FXML
    private TableColumn<Veiculo, String> colCor;

    @FXML
    private TableColumn<Veiculo, Double> colValor;

    @FXML
    private TableColumn<Veiculo, String> colStatus;

    @FXML
    private TableColumn<Veiculo, String> colTipo;

    @FXML
    private TableColumn<Veiculo, Integer> colAno;

    @FXML
    private TableColumn<Veiculo, String> colEspecificacoes;

    @FXML
    private TextField txtBusca;

    private VeiculoDAO veiculoDAO = new VeiculoDAO();

    @FXML
    public void initialize(){
        cbStatus.setItems(FXCollections.observableArrayList("DISPONIVEL", "RESERVADO", "VENDIDO"));
        colId.setCellValueFactory(dado -> new SimpleIntegerProperty(dado.getValue().getIdVeiculo()).asObject());
        colMarca.setCellValueFactory(dado -> new SimpleStringProperty(dado.getValue().getMarca()));
        colModelo.setCellValueFactory(dado -> new SimpleStringProperty(dado.getValue().getModelo()));
        colPlaca.setCellValueFactory(dado -> new SimpleStringProperty(dado.getValue().getPlaca()));
        colCor.setCellValueFactory(dado -> new SimpleStringProperty(dado.getValue().getCor()));
        colValor.setCellValueFactory(dado -> new SimpleDoubleProperty(dado.getValue().getValor()).asObject());
        colStatus.setCellValueFactory(dado -> new SimpleStringProperty(dado.getValue().getStatus()));
        colTipo.setCellValueFactory(dado -> new SimpleStringProperty(dado.getValue().getTipo()));
        colAno.setCellValueFactory(dado -> new SimpleIntegerProperty(dado.getValue().getAno()).asObject());
        colEspecificacoes.setCellValueFactory(dado -> new SimpleStringProperty(dado.getValue().getEspecificacoes()));

        carregarVeiculos();

        tabelaVeiculos.getSelectionModel().selectedItemProperty().addListener((observable, antigo, selecionado) -> {

            if(selecionado != null){
                txtMarca.setText(selecionado.getMarca());
                txtModelo.setText(selecionado.getModelo());
                txtPlaca.setText(selecionado.getPlaca());
                txtAno.setText(String.valueOf(selecionado.getAno()));
                txtCor.setText(selecionado.getCor());
                txtTipo.setText(selecionado.getTipo());
                txtValor.setText(String.valueOf(selecionado.getValor()));
                cbStatus.setValue(selecionado.getStatus());
                txtEspecificacoes.setText(selecionado.getEspecificacoes());
            }
        });

        // --- INÍCIO DO LISTENER DO DIFERENCIAL 1 ---
        txtBusca.textProperty().addListener((observable, antigo, novoValor) -> {
            try {
                if (novoValor == null || novoValor.trim().isEmpty()) {
                    carregarVeiculos(); // Se a busca estiver vazia, carrega todos
                } else {
                    // Atualiza a tabela chamando o método novo do DAO
                    tabelaVeiculos.setItems(FXCollections.observableArrayList(veiculoDAO.buscarPorMarcaOuPlaca(novoValor)));
                }
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, "Erro ao buscar veículos!!", ButtonType.OK).showAndWait();
                e.printStackTrace();
            }
        });

    }

    @FXML
    private void cadastrar(){
        String marca = txtMarca.getText().trim();
        String modelo = txtModelo.getText().trim();
        String placa = txtPlaca.getText().trim().toUpperCase();
        String anoTexto = txtAno.getText().trim();
        String cor = txtCor.getText().trim();
        String tipo = txtTipo.getText().trim();
        String valorTexto = txtValor.getText().trim();
        String status = cbStatus.getValue();
        String especificacoes = txtEspecificacoes.getText().trim();

        if(marca.isEmpty() || modelo.isEmpty() || placa.isEmpty() || anoTexto.isEmpty() || cor.isEmpty() || tipo.isEmpty() || valorTexto.isEmpty() || status == null){
            new Alert(Alert.AlertType.WARNING, "Preencha todos os campos obrigatórios!!", ButtonType.OK).showAndWait();
            return;
        }

        if(placa.length() != 7){
            new Alert(Alert.AlertType.WARNING, "A placa deve possuir 7 caracteres!!", ButtonType.OK).showAndWait();
            return;
        }

        try{
            double valor = Double.parseDouble(valorTexto.replace(",", "."));
            int ano = Integer.parseInt((anoTexto));

            if(valor <= 0){
                new Alert(Alert.AlertType.WARNING, "O valor deve ser maior que zero!!", ButtonType.OK).showAndWait();
                return;
            }

            if(ano < 1900){
                new Alert(Alert.AlertType.WARNING, "Informe um ano válido!", ButtonType.OK).showAndWait();
                return;
            }

            Veiculo veiculo = new Veiculo();

            veiculo.setMarca(marca);
            veiculo.setModelo(modelo);
            veiculo.setPlaca(placa);
            veiculo.setAno(ano);
            veiculo.setCor(cor);
            veiculo.setTipo(tipo);
            veiculo.setValor(valor);
            veiculo.setStatus(status);
            veiculo.setEspecificacoes(especificacoes);

            veiculoDAO.inserir(veiculo);

            new Alert(Alert.AlertType.INFORMATION, "Veículo cadastrado com sucesso!!", ButtonType.OK).showAndWait();

            limparCampos();
            carregarVeiculos();

        } catch(NumberFormatException e){
            new Alert(Alert.AlertType.WARNING, "Informe valores válidos para ano e valor!", ButtonType.OK).showAndWait();
        } catch (SQLException e){
            new Alert(Alert.AlertType.ERROR, "Erro ao cadastrar veículo!! Verifique se a placa já está cadastrada!!", ButtonType.OK).showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    private void alterar(){
        Veiculo veiculo = tabelaVeiculos.getSelectionModel().getSelectedItem();

        if(veiculo == null){
            new Alert(Alert.AlertType.WARNING, "Selecione um veículo na tabela!", ButtonType.OK).showAndWait();
            return;
        }

        String marca = txtMarca.getText().trim();
        String modelo = txtModelo.getText().trim();
        String placa = txtPlaca.getText().trim().toUpperCase();
        String anoTexto = txtAno.getText().trim();
        String cor = txtCor.getText().trim();
        String tipo = txtTipo.getText().trim();
        String valorTexto = txtValor.getText().trim();
        String status = cbStatus.getValue();
        String especificacoes = txtEspecificacoes.getText().trim();

        if(marca.isEmpty() || modelo.isEmpty() || placa.isEmpty() || anoTexto.isEmpty() || cor.isEmpty() || tipo.isEmpty() || valorTexto.isEmpty() || status == null){
            new Alert(Alert.AlertType.WARNING, "Preencha todos os campos obrigatórios!!", ButtonType.OK).showAndWait();
            return;
        }

        if(placa.length() != 7){
            new Alert(Alert.AlertType.WARNING, "A placa deve possuir 7 caracteres!!", ButtonType.OK).showAndWait();
            return;
        }

        try {
            double valor = Double.parseDouble(valorTexto.replace(",", "."));
            int ano = Integer.parseInt((anoTexto));

            if (valor <= 0) {
                new Alert(Alert.AlertType.WARNING, "O valor deve ser maior que zero!!", ButtonType.OK).showAndWait();
                return;
            }

            if (ano < 1900) {
                new Alert(Alert.AlertType.WARNING, "Informe um ano válido!", ButtonType.OK).showAndWait();
                return;
            }

            veiculo.setMarca(marca);
            veiculo.setModelo(modelo);
            veiculo.setPlaca(placa);
            veiculo.setAno(ano);
            veiculo.setCor(cor);
            veiculo.setTipo(tipo);
            veiculo.setValor(valor);
            veiculo.setStatus(status);
            veiculo.setEspecificacoes(especificacoes);

            veiculoDAO.alterar(veiculo);

            new Alert(Alert.AlertType.INFORMATION, "Veículo alterado com sucesso!!", ButtonType.OK).showAndWait();

            limparCampos();
            carregarVeiculos();

        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.WARNING, "Informe valores válidos para ano e valor!!", ButtonType.OK).showAndWait();
        } catch (SQLException e){
            new Alert(Alert.AlertType.ERROR, "Erro ao alterar veículo!!", ButtonType.OK).showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    private void excluir(){
        Veiculo veiculo = tabelaVeiculos.getSelectionModel().getSelectedItem();

        if(veiculo == null){
            new Alert(Alert.AlertType.WARNING, "Selecione um veículo na tabela!!", ButtonType.OK).showAndWait();
            return;
        }

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION, "Deseja realmente excluir o veículo " + veiculo.getMarca() + " " + veiculo.getModelo() + "?", ButtonType.YES, ButtonType.NO);
        confirmacao.showAndWait();

        if(confirmacao.getResult() == ButtonType.YES){

            try{
                veiculoDAO.excluir(veiculo.getIdVeiculo());

                new Alert(Alert.AlertType.INFORMATION, "Veículo excluído com sucesso!!", ButtonType.OK).showAndWait();

                limparCampos();
                carregarVeiculos();
            } catch (SQLException e){
                new Alert(Alert.AlertType.ERROR, "Erro ao excluir veículo!! Verifique se ele possui uma venda cadastrada.", ButtonType.OK).showAndWait();
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void limpar(){
        limparCampos();
    }

    private void limparCampos(){
        txtMarca.clear();
        txtModelo.clear();
        txtPlaca.clear();
        txtAno.clear();
        txtCor.clear();
        txtTipo.clear();
        txtValor.clear();
        txtEspecificacoes.clear();
        cbStatus.setValue(null);

        tabelaVeiculos.getSelectionModel().clearSelection();

        txtMarca.requestFocus();
    }

    private void carregarVeiculos(){
        try {
            tabelaVeiculos.setItems(FXCollections.observableArrayList(veiculoDAO.consultar()));
        } catch (SQLException e){
            new Alert(Alert.AlertType.ERROR, "Erro ao consultar veículos!!", ButtonType.OK).showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    private void voltar(){
        try {
            GerenciadorTelas.trocarTela("/view/menu.fxml");
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
