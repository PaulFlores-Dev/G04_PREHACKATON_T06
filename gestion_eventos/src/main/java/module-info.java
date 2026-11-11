module vallegrande.edu.pe.gestion_eventos {
        requires javafx.controls;
        requires javafx.fxml;
        requires java.sql;

        opens vallegrande.edu.pe.gestion_eventos to javafx.fxml;
        exports vallegrande.edu.pe.gestion_eventos;
        }