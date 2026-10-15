package br.unicentro.lojaveiculos.main;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class GerenciadorTelas {

    private static Stage stage;

    public static void setStage(Stage stagePrincipal){
        stage = stagePrincipal;
    }

    public static void trocarTela(String arquivoFXML) throws IOException{

        FXMLLoader loader = new FXMLLoader(GerenciadorTelas.class.getResource(arquivoFXML));

        Scene scene = new Scene(loader.load());

        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();

    }

}
