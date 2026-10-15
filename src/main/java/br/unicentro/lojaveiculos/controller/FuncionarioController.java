package br.unicentro.lojaveiculos.controller;

import br.unicentro.lojaveiculos.dao.FuncionarioDAO;
import br.unicentro.lojaveiculos.main.GerenciadorTelas;
import br.unicentro.lojaveiculos.model.Funcionario;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.SQLException;
public class FuncionarioController {

    @FXML
    private TextField txtNome;

    @FXML
    private TextField txtCpf;

    @FXML
    private TextField txtCargo;

    @FXML
    private TextField txtTurno;

    @FXML
    private TextField txtSalario;

    @FXML
    private TableView<Funcionario> tabelaFuncionarios;

    @FXML
    private TableColumn<Funcionario, Integer> colId;

    @FXML
    private TableColumn<Funcionario, String> colNome;

    @FXML
    private TableColumn<Funcionario, String> colCpf;

    @FXML
    private TableColumn<Funcionario, String> colCargo;

    @FXML
    private TableColumn<Funcionario, String> colTurno;

    @FXML
    private TableColumn<Funcionario, Double> colSalario;

    private FuncionarioDAO funcionarioDAO = new FuncionarioDAO();

    @FXML
    public void initialize(){
        colId.setCellValueFactory(dado -> new SimpleIntegerProperty(dado.getValue().getIdFuncionario()).asObject());
        colNome.setCellValueFactory(dado -> new SimpleStringProperty(dado.getValue().getNome()));
        colCpf.setCellValueFactory(dado -> new SimpleStringProperty(dado.getValue().getCpf()));
        colCargo.setCellValueFactory(dado -> new SimpleStringProperty(dado.getValue().getCargo()));
        colTurno.setCellValueFactory(dado -> new SimpleStringProperty(dado.getValue().getTurno()));
        colSalario.setCellValueFactory(dado -> new SimpleDoubleProperty(dado.getValue().getSalario()).asObject());

        carregarFuncionarios();

        tabelaFuncionarios.getSelectionModel().selectedItemProperty().addListener((observable, antigo, selecionado) ->{

           if(selecionado != null){
               txtNome.setText(selecionado.getNome());
               txtCpf.setText(selecionado.getCpf());
               txtCargo.setText(selecionado.getCargo());
               txtTurno.setText(selecionado.getTurno());
               txtSalario.setText(String.valueOf(selecionado.getSalario()));
           }
        });
    }

    @FXML
    private void cadastrar(){
        String nome = txtNome.getText().trim();
        String cpf = txtCpf.getText().trim();
        String cargo = txtCargo.getText().trim();
        String turno = txtTurno.getText().trim();
        String salarioTexto = txtSalario.getText().trim();

        if(nome.isEmpty() || cpf.isEmpty() || cargo.isEmpty() || turno.isEmpty() || salarioTexto.isEmpty()){
            new Alert(Alert.AlertType.WARNING, "Preencha todos os campos!!", ButtonType.OK).showAndWait();
            return;
        }

        if (cpf.length() != 11){
            new Alert(Alert.AlertType.WARNING, "O CPF deve possuir 11 números!!", ButtonType.OK).showAndWait();
            return;
        }

        try{
            double salario = Double.parseDouble(salarioTexto.replace(",", "."));

            if(salario <= 0){
                new Alert(Alert.AlertType.WARNING, "O salário deve ser maior que zero!!", ButtonType.OK).showAndWait();
                return;
            }

            Funcionario funcionario = new Funcionario();
            funcionario.setNome(nome);
            funcionario.setCpf(cpf);
            funcionario.setCargo(cargo);
            funcionario.setTurno(turno);
            funcionario.setSalario(salario);

            funcionarioDAO.inserir(funcionario);

            new Alert(Alert.AlertType.INFORMATION, "Funcionário cadastrado com sucesso!!", ButtonType.OK).showAndWait();

            limparCampos();
            carregarFuncionarios();

        } catch (NumberFormatException e){
            new Alert(Alert.AlertType.WARNING, "Informe um salário válido!!", ButtonType.OK).showAndWait();
            txtSalario.requestFocus();
        } catch (SQLException e){
            new Alert(Alert.AlertType.ERROR, "Erro ao cadastrar funcionário!!", ButtonType.OK).showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    private void alterar(){
        Funcionario funcionario = tabelaFuncionarios.getSelectionModel().getSelectedItem();
        if (funcionario == null){
            new Alert(Alert.AlertType.WARNING, "Selecione um funcionário na tabela!!", ButtonType.OK).showAndWait();
            return;
        }

        String nome = txtNome.getText().trim();
        String cpf = txtCpf.getText().trim();
        String cargo = txtCargo.getText().trim();
        String turno = txtTurno.getText().trim();
        String salarioTexto = txtSalario.getText().trim();

        if(nome.isEmpty() || cpf.isEmpty() || cargo.isEmpty() || turno.isEmpty() || salarioTexto.isEmpty()){
            new Alert(Alert.AlertType.WARNING, "Preencha todos os campos!!", ButtonType.OK).showAndWait();
            return;
        }

        try{
            double salario = Double.parseDouble(salarioTexto.replace(",", "."));

            funcionario.setNome(nome);
            funcionario.setCpf(cpf);
            funcionario.setCargo(cargo);
            funcionario.setTurno(turno);
            funcionario.setSalario(salario);
            funcionarioDAO.alterar(funcionario);

            new Alert(Alert.AlertType.INFORMATION, "Funcionário alterado com sucesso!!", ButtonType.OK).showAndWait();

            limparCampos();
            carregarFuncionarios();

        } catch (NumberFormatException e){
            new Alert(Alert.AlertType.WARNING, "Informe um salário válido!!", ButtonType.OK).showAndWait();
        } catch (SQLException e){
            new Alert(Alert.AlertType.ERROR, "Erro ao alterar funcionário!!", ButtonType.OK).showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    private void excluir(){
        Funcionario funcionario = tabelaFuncionarios.getSelectionModel().getSelectedItem();

        if(funcionario == null){
            new Alert(Alert.AlertType.WARNING, "Selecione um funcionário na tabela!", ButtonType.OK).showAndWait();
            return;
        }

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION, "Deseja realmente excluir o funcionário " + funcionario.getNome() + "?", ButtonType.YES, ButtonType.NO);
        confirmacao.showAndWait();

        if(confirmacao.getResult() == ButtonType.YES){

            try {
                funcionarioDAO.excluir(funcionario.getIdFuncionario());

                new Alert(Alert.AlertType.INFORMATION, "Funcionário excluído com sucesso!!", ButtonType.OK).showAndWait();

                limparCampos();
                carregarFuncionarios();

            } catch (SQLException e){
                new Alert(Alert.AlertType.ERROR, "Erro ao excluir funcionário! Verifique se ele possui vendas cadastradas!!", ButtonType.OK).showAndWait();
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void limpar(){
        limparCampos();
    }

    private void limparCampos(){
        txtNome.clear();
        txtCpf.clear();
        txtCargo.clear();
        txtTurno.clear();
        txtSalario.clear();

        tabelaFuncionarios.getSelectionModel().clearSelection();
        txtNome.requestFocus();
    }

    private void carregarFuncionarios(){

        try{
            tabelaFuncionarios.setItems(FXCollections.observableArrayList(funcionarioDAO.consultar()));
        } catch (SQLException e){
            new Alert(Alert.AlertType.ERROR, "Erro ao consultar funcionários!!", ButtonType.OK).showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    private void voltar(){

        try{
            GerenciadorTelas.trocarTela("/view/menu.fxml");
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
