package br.unicentro.lojaveiculos.main;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application{

    @Override
    public void start(Stage stage) throws Exception{

        GerenciadorTelas.setStage(stage);

        stage.setTitle("Loja de Veículos");

        GerenciadorTelas.trocarTela("/view/menu.fxml");

    }

    public static void main(String[] args){
        launch();
    }

}
