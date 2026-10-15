module br.unicentro.lojaveiculos {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens br.unicentro.lojaveiculos.controller to javafx.fxml;
    exports br.unicentro.lojaveiculos.main;
}