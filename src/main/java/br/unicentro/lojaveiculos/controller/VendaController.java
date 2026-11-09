package br.unicentro.lojaveiculos.controller;

import br.unicentro.lojaveiculos.main.GerenciadorTelas;
import br.unicentro.lojaveiculos.model.Funcionario;
import br.unicentro.lojaveiculos.model.Veiculo;
import br.unicentro.lojaveiculos.model.Venda;
import br.unicentro.lojaveiculos.observer.AtualizacaoTelaVendaObserver;
import br.unicentro.lojaveiculos.observer.AuditoriaVendaObserver;
import br.unicentro.lojaveiculos.service.FuncionarioService;
import br.unicentro.lojaveiculos.service.VeiculoService;
import br.unicentro.lojaveiculos.service.VendaService;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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

    // Padrão Service Layer: nenhuma referência direta a classes DAO no Controller
    private final VendaService vendaService = new VendaService();
    private final FuncionarioService funcionarioService = new FuncionarioService();
    private final VeiculoService veiculoService = new VeiculoService();

    @FXML
    public void initialize() {

        // Padrão Observer: registro dos observadores concretos no Subject (VendaService)
        vendaService.adicionarObserver(new AuditoriaVendaObserver());
        vendaService.adicionarObserver(new AtualizacaoTelaVendaObserver(() -> {
            limparCampos();
            carregarCombos();
            carregarVendas();
        }));

        carregarCombos();

        colId.setCellValueFactory(dado -> new SimpleIntegerProperty(dado.getValue().getIdVenda()).asObject());

        colFuncionario.setCellValueFactory(dado -> {
            Funcionario funcionario = buscarFuncionarioPorId(dado.getValue().getIdFuncionario());
            if (funcionario != null) {
                return new SimpleStringProperty(funcionario.getNome());
            }
            return new SimpleStringProperty(String.valueOf(dado.getValue().getIdFuncionario()));
        });

        colVeiculo.setCellValueFactory(dado -> {
            Veiculo veiculo = buscarVeiculoPorId(dado.getValue().getIdVeiculo());
            if (veiculo != null) {
                return new SimpleStringProperty(veiculo.getMarca() + " " + veiculo.getModelo() + " " + veiculo.getPlaca());
            }
            return new SimpleStringProperty(String.valueOf(dado.getValue().getIdVeiculo()));
        });

        colDataVenda.setCellValueFactory(dado -> new SimpleStringProperty(String.valueOf(dado.getValue().getDataVenda())));
        colValorTotal.setCellValueFactory(dado -> new SimpleDoubleProperty(dado.getValue().getValorTotal()).asObject());

        colDataPagamento.setCellValueFactory(dado -> {
            if (dado.getValue().getDataPagamento() != null) {
                return new SimpleStringProperty(String.valueOf(dado.getValue().getDataPagamento()));
            } else {
                return new SimpleStringProperty("-");
            }
        });

        carregarVendas();
        dpDataVenda.setValue(LocalDate.now());

        tabelaVendas.getSelectionModel().selectedItemProperty().addListener((observable, antigo, selecionado) -> {
            if (selecionado != null) {
                cbFuncionario.setValue(buscarFuncionarioPorId(selecionado.getIdFuncionario()));
                cbVeiculo.setValue(buscarVeiculoPorId(selecionado.getIdVeiculo()));
                dpDataVenda.setValue(selecionado.getDataVenda());
                txtValorTotal.setText(String.valueOf(selecionado.getValorTotal()));
                dpDataPagamento.setValue(selecionado.getDataPagamento());
            }
        });
    }

    @FXML
    private void cadastrar() {
        Funcionario funcionario = cbFuncionario.getValue();
        Veiculo veiculo = cbVeiculo.getValue();
        LocalDate dataVenda = dpDataVenda.getValue();
        LocalDate dataPagamento = dpDataPagamento.getValue();
        String valorTexto = txtValorTotal.getText().trim();

        if (funcionario == null || veiculo == null || dataVenda == null || valorTexto.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Preencha todos os campos obrigatórios!!", ButtonType.OK).showAndWait();
            return;
        }

        try {
            double valorTotal = Double.parseDouble(valorTexto.replace(",", "."));

            Venda venda = new Venda();
            venda.setIdFuncionario(funcionario.getIdFuncionario());
            venda.setIdVeiculo(veiculo.getIdVeiculo());
            venda.setDataVenda(dataVenda);
            venda.setValorTotal(valorTotal);
            venda.setDataPagamento(dataPagamento);

            // Ao confirmar no banco, o VendaService notifica automaticamente os Observers (Auditoria e Tela)
            vendaService.cadastrar(venda, veiculo);

            new Alert(Alert.AlertType.INFORMATION, "Venda cadastrada com sucesso!!", ButtonType.OK).showAndWait();

        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.WARNING, "Informe um valor válido!!", ButtonType.OK).showAndWait();
        } catch (IllegalArgumentException e) {
            new Alert(Alert.AlertType.WARNING, e.getMessage(), ButtonType.OK).showAndWait();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Erro ao cadastrar a venda!\n\n" + e.getMessage(), ButtonType.OK).showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    public void alterar() {

        Venda vendaSelecionada = tabelaVendas.getSelectionModel().getSelectedItem();

        if (vendaSelecionada == null) {
            new Alert(Alert.AlertType.WARNING, "Selecione uma venda na tabela!", ButtonType.OK).showAndWait();
            return;
        }

        Funcionario funcionario = cbFuncionario.getValue();
        Veiculo veiculo = cbVeiculo.getValue();
        LocalDate dataVenda = dpDataVenda.getValue();
        LocalDate dataPagamento = dpDataPagamento.getValue();
        String valorTexto = txtValorTotal.getText().trim();

        if (funcionario == null || veiculo == null || dataVenda == null || valorTexto.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Preencha todos os campos obrigatórios!", ButtonType.OK).showAndWait();
            return;
        }

        try {
            double valorTotal = Double.parseDouble(valorTexto.replace(",", "."));

            Venda venda = new Venda();
            venda.setIdVenda(vendaSelecionada.getIdVenda());
            venda.setIdFuncionario(funcionario.getIdFuncionario());
            venda.setIdVeiculo(veiculo.getIdVeiculo());
            venda.setDataVenda(dataVenda);
            venda.setValorTotal(valorTotal);
            venda.setDataPagamento(dataPagamento);

            // Ao confirmar no banco, o VendaService notifica automaticamente os Observers (Auditoria e Tela)
            vendaService.alterar(venda, veiculo);

            new Alert(Alert.AlertType.INFORMATION, "Venda alterada com sucesso!", ButtonType.OK).showAndWait();

        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.WARNING, "Informe um valor válido!", ButtonType.OK).showAndWait();
        } catch (IllegalArgumentException e) {
            new Alert(Alert.AlertType.WARNING, e.getMessage(), ButtonType.OK).showAndWait();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Erro ao alterar a venda!\n\n" + e.getMessage(), ButtonType.OK).showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    private void excluir() {
        Venda venda = tabelaVendas.getSelectionModel().getSelectedItem();

        if (venda == null) {
            new Alert(Alert.AlertType.WARNING, "Selecione uma venda na tabela!!", ButtonType.OK).showAndWait();
            return;
        }

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION, "Deseja realmente excluir esta venda?", ButtonType.YES, ButtonType.NO);
        confirmacao.showAndWait();

        if (confirmacao.getResult() == ButtonType.YES) {
            try {
                // Ao confirmar no banco, o VendaService notifica automaticamente os Observers (Auditoria e Tela)
                vendaService.excluir(venda.getIdVenda());

                new Alert(Alert.AlertType.INFORMATION, "Venda excluída com sucesso!!", ButtonType.NO).showAndWait();

            } catch (IllegalArgumentException e) {
                new Alert(Alert.AlertType.WARNING, e.getMessage(), ButtonType.OK).showAndWait();
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, "Erro ao excluir a venda!!", ButtonType.NO).showAndWait();
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void limpar() {
        limparCampos();
    }

    private void limparCampos() {
        cbFuncionario.setValue(null);
        cbVeiculo.setValue(null);
        dpDataVenda.setValue(LocalDate.now());
        dpDataPagamento.setValue(null);
        txtValorTotal.clear();

        tabelaVendas.getSelectionModel().clearSelection();
    }

    private void carregarCombos() {
        try {
            cbFuncionario.setItems(FXCollections.observableArrayList(funcionarioService.consultar()));
            cbVeiculo.setItems(FXCollections.observableArrayList(veiculoService.consultar()));
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Erro ao carregar funcionários ou veículos!!", ButtonType.NO).showAndWait();
            e.printStackTrace();
        }
    }

    private void carregarVendas() {
        try {
            tabelaVendas.setItems(FXCollections.observableArrayList(vendaService.consultar()));
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Erro ao consultar vendas!!", ButtonType.NO).showAndWait();
            e.printStackTrace();
        }
    }

    private Funcionario buscarFuncionarioPorId(int idFuncionario) {
        for (Funcionario funcionario : cbFuncionario.getItems()) {
            if (funcionario.getIdFuncionario() == idFuncionario) {
                return funcionario;
            }
        }
        return null;
    }

    private Veiculo buscarVeiculoPorId(int idVeiculo) {
        for (Veiculo veiculo : cbVeiculo.getItems()) {
            if (veiculo.getIdVeiculo() == idVeiculo) {
                return veiculo;
            }
        }
        return null;
    }

    @FXML
    private void voltar() {
        try {
            GerenciadorTelas.trocarTela("/view/menu.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void gerarRecibo() {
        Venda venda = tabelaVendas.getSelectionModel().getSelectedItem();

        if (venda == null) {
            new Alert(Alert.AlertType.WARNING, "Selecione uma venda na tabela para gerar o recibo!", ButtonType.OK).showAndWait();
            return;
        }

        Funcionario funcionario = buscarFuncionarioPorId(venda.getIdFuncionario());
        Veiculo veiculo = buscarVeiculoPorId(venda.getIdVeiculo());

        if (funcionario == null || veiculo == null) {
            new Alert(Alert.AlertType.ERROR, "Erro ao carregar dados complementares para o recibo.", ButtonType.OK).showAndWait();
            return;
        }

        DateTimeFormatter formatadorData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dataVendaFormatada = venda.getDataVenda().format(formatadorData);
        String dataPagamentoFormatada = venda.getDataPagamento() != null ? venda.getDataPagamento().format(formatadorData) : "Pendente";

        String conteudoRecibo = "=========================================\n" +
                "        RECIBO DE VENDA DE VEÍCULO       \n" +
                "=========================================\n\n" +
                "DADOS DA TRANSAÇÃO:\n" +
                "ID da Venda: " + venda.getIdVenda() + "\n" +
                "Data da Venda: " + dataVendaFormatada + "\n" +
                "Data de Pagamento: " + dataPagamentoFormatada + "\n" +
                "Valor Total: R$ " + String.format("%.2f", venda.getValorTotal()) + "\n\n" +
                "DADOS DO VEÍCULO:\n" +
                "Marca: " + veiculo.getMarca() + "\n" +
                "Modelo: " + veiculo.getModelo() + "\n" +
                "Ano: " + veiculo.getAno() + "\n" +
                "Placa: " + veiculo.getPlaca() + "\n" +
                "Cor: " + veiculo.getCor() + "\n" +
                "Chassi/Especificações: " + veiculo.getEspecificacoes() + "\n\n" +
                "DADOS DO ATENDIMENTO:\n" +
                "Vendedor(a): " + funcionario.getNome() + "\n" +
                "Cargo: " + funcionario.getCargo() + "\n\n" +
                "=========================================\n" +
                "        OBRIGADO PELA PREFERÊNCIA!       \n" +
                "=========================================";

        try {
            File diretorio = new File("Recibos");
            if (!diretorio.exists()) {
                diretorio.mkdir();
            }

            String nomeArquivo = "Recibos/Recibo_Venda_ID" + venda.getIdVenda() + "_Placa" + veiculo.getPlaca() + ".txt";
            File arquivo = new File(nomeArquivo);

            FileWriter escritor = new FileWriter(arquivo);
            escritor.write(conteudoRecibo);
            escritor.close();

            Alert sucesso = new Alert(Alert.AlertType.INFORMATION, "Recibo gerado com sucesso!\nSalvo em: " + arquivo.getAbsolutePath(), ButtonType.OK);
            sucesso.setHeaderText("Comprovante Emitido");
            sucesso.showAndWait();

        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Erro ao gerar o arquivo de recibo:\n" + e.getMessage(), ButtonType.OK).showAndWait();
            e.printStackTrace();
        }
    }
}