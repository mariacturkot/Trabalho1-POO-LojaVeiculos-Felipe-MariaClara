package br.unicentro.lojaveiculos.controller;

import br.unicentro.lojaveiculos.main.GerenciadorTelas;
import javafx.fxml.FXML;

import java.io.IOException;

public class MenuController {

    @FXML
    private void abrirFuncionarios() throws IOException {
        GerenciadorTelas.trocarTela("/view/funcionario.fxml");
    }

    @FXML
    private void abrirVeiculos() throws IOException {
        GerenciadorTelas.trocarTela("/view/veiculo.fxml");
    }

    @FXML
    private void abrirVendas() throws IOException{
        GerenciadorTelas.trocarTela("/view/venda.fxml");
    }



}
