package br.unicentro.lojaveiculos.controller;

import br.unicentro.lojaveiculos.main.GerenciadorTelas;
import br.unicentro.lojaveiculos.model.Veiculo;
import br.unicentro.lojaveiculos.model.VeiculoBuilder;
import br.unicentro.lojaveiculos.service.VeiculoService;
import br.unicentro.lojaveiculos.strategy.OrdenacaoAnoMaisRecenteStrategy;
import br.unicentro.lojaveiculos.strategy.OrdenacaoMaiorPrecoStrategy;
import br.unicentro.lojaveiculos.strategy.OrdenacaoMenorPrecoStrategy;
import br.unicentro.lojaveiculos.strategy.OrdenacaoPadraoStrategy;

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

    @FXML
    private ComboBox<String> cbOrdenacao;

    // Padrão Service Layer: Controller conversa apenas com o Service
    private final VeiculoService veiculoService = new VeiculoService();

    @FXML
    public void initialize() {
        cbStatus.setItems(FXCollections.observableArrayList("DISPONIVEL", "RESERVADO", "VENDIDO"));

        // Opções de ordenação do Padrão Strategy
        cbOrdenacao.setItems(FXCollections.observableArrayList(
                "Padrão (ID)",
                "Menor Preço",
                "Maior Preço",
                "Ano Mais Recente"
        ));
        cbOrdenacao.setValue("Padrão (ID)");

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
            if (selecionado != null) {
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

        txtBusca.textProperty().addListener((observable, antigo, novoValor) -> {
            carregarVeiculos();
        });

        // Listener que troca a estratégia de ordenação em tempo de execução (Padrão Strategy)
        cbOrdenacao.valueProperty().addListener((observable, antigo, criterioSelecionado) -> {
            atualizarEstrategiaOrdenacao(criterioSelecionado);
            carregarVeiculos();
        });
    }

    private void atualizarEstrategiaOrdenacao(String criterio) {
        if (criterio == null) {
            veiculoService.setEstrategiaOrdenacao(new OrdenacaoPadraoStrategy());
            return;
        }

        switch (criterio) {
            case "Menor Preço":
                veiculoService.setEstrategiaOrdenacao(new OrdenacaoMenorPrecoStrategy());
                break;
            case "Maior Preço":
                veiculoService.setEstrategiaOrdenacao(new OrdenacaoMaiorPrecoStrategy());
                break;
            case "Ano Mais Recente":
                veiculoService.setEstrategiaOrdenacao(new OrdenacaoAnoMaisRecenteStrategy());
                break;
            default:
                veiculoService.setEstrategiaOrdenacao(new OrdenacaoPadraoStrategy());
                break;
        }
    }

    @FXML
    private void cadastrar() {
        String marca = txtMarca.getText().trim();
        String modelo = txtModelo.getText().trim();
        String placa = txtPlaca.getText().trim().toUpperCase();
        String anoTexto = txtAno.getText().trim();
        String cor = txtCor.getText().trim();
        String tipo = txtTipo.getText().trim();
        String valorTexto = txtValor.getText().trim();
        String status = cbStatus.getValue();
        String especificacoes = txtEspecificacoes.getText().trim();

        if (marca.isEmpty() || modelo.isEmpty() || placa.isEmpty() || anoTexto.isEmpty() ||
                cor.isEmpty() || tipo.isEmpty() || valorTexto.isEmpty() || status == null) {
            new Alert(Alert.AlertType.WARNING, "Preencha todos os campos obrigatórios!", ButtonType.OK).showAndWait();
            return;
        }

        try {
            double valor = Double.parseDouble(valorTexto.replace(",", "."));
            int ano = Integer.parseInt(anoTexto);

            // Aplicação do Padrão GoF Builder para construir o objeto Veiculo
            Veiculo veiculo = VeiculoBuilder.builder()
                    .comMarca(marca)
                    .comModelo(modelo)
                    .comPlaca(placa)
                    .comAno(ano)
                    .comCor(cor)
                    .comTipo(tipo)
                    .comValor(valor)
                    .comStatus(status)
                    .comEspecificacoes(especificacoes)
                    .build();

            veiculoService.cadastrar(veiculo);

            new Alert(Alert.AlertType.INFORMATION, "Veículo cadastrado com sucesso!!", ButtonType.OK).showAndWait();

            limparCampos();
            carregarVeiculos();

        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.WARNING, "Informe valores válidos para ano e valor!", ButtonType.OK).showAndWait();
        } catch (IllegalArgumentException e) {
            new Alert(Alert.AlertType.WARNING, e.getMessage(), ButtonType.OK).showAndWait();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Erro ao cadastrar veículo!! Verifique se a placa já está cadastrada!!", ButtonType.OK).showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    private void alterar() {
        Veiculo veiculoSelecionado = tabelaVeiculos.getSelectionModel().getSelectedItem();

        if (veiculoSelecionado == null) {
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

        if (marca.isEmpty() || modelo.isEmpty() || placa.isEmpty() || anoTexto.isEmpty() ||
                cor.isEmpty() || tipo.isEmpty() || valorTexto.isEmpty() || status == null) {
            new Alert(Alert.AlertType.WARNING, "Preencha todos os campos obrigatórios!", ButtonType.OK).showAndWait();
            return;
        }

        try {
            double valor = Double.parseDouble(valorTexto.replace(",", "."));
            int ano = Integer.parseInt(anoTexto);

            // Aplicação do Padrão GoF Builder para reconstruir o objeto Veiculo na alteração
            Veiculo veiculo = VeiculoBuilder.builder()
                    .comIdVeiculo(veiculoSelecionado.getIdVeiculo())
                    .comMarca(marca)
                    .comModelo(modelo)
                    .comPlaca(placa)
                    .comAno(ano)
                    .comCor(cor)
                    .comTipo(tipo)
                    .comValor(valor)
                    .comStatus(status)
                    .comEspecificacoes(especificacoes)
                    .build();

            veiculoService.alterar(veiculo);

            new Alert(Alert.AlertType.INFORMATION, "Veículo alterado com sucesso!!", ButtonType.OK).showAndWait();

            limparCampos();
            carregarVeiculos();

        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.WARNING, "Informe valores válidos para ano e valor!!", ButtonType.OK).showAndWait();
        } catch (IllegalArgumentException e) {
            new Alert(Alert.AlertType.WARNING, e.getMessage(), ButtonType.OK).showAndWait();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Erro ao alterar veículo!!", ButtonType.OK).showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    private void excluir() {
        Veiculo veiculo = tabelaVeiculos.getSelectionModel().getSelectedItem();

        if (veiculo == null) {
            new Alert(Alert.AlertType.WARNING, "Selecione um veículo na tabela!!", ButtonType.OK).showAndWait();
            return;
        }

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION,
                "Deseja realmente excluir o veículo " + veiculo.getMarca() + " " + veiculo.getModelo() + "?",
                ButtonType.YES, ButtonType.NO);
        confirmacao.showAndWait();

        if (confirmacao.getResult() == ButtonType.YES) {
            try {
                veiculoService.excluir(veiculo.getIdVeiculo());

                new Alert(Alert.AlertType.INFORMATION, "Veículo excluído com sucesso!!", ButtonType.OK).showAndWait();

                limparCampos();
                carregarVeiculos();
            } catch (IllegalArgumentException e) {
                new Alert(Alert.AlertType.WARNING, e.getMessage(), ButtonType.OK).showAndWait();
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, "Erro ao excluir veículo!! Verifique se ele possui uma venda cadastrada.", ButtonType.OK).showAndWait();
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void limpar() {
        limparCampos();
    }

    private void limparCampos() {
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

    private void carregarVeiculos() {
        try {
            String termoBusca = txtBusca != null ? txtBusca.getText() : null;
            tabelaVeiculos.setItems(FXCollections.observableArrayList(
                    veiculoService.buscarPorMarcaOuPlaca(termoBusca)
            ));
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Erro ao consultar veículos!!", ButtonType.OK).showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    private void voltar() {
        try {
            GerenciadorTelas.trocarTela("/view/menu.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}