package com.team_04.controller;

import com.team_04.dao.InscripcionDAO;
import com.team_04.model.Inscripcion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class InscripcionesController {

    @FXML private TextField txtIdParticipante;
    @FXML private TextField txtCategoria;
    @FXML private TextField txtFecha;
    @FXML private TextField txtEstado;

    @FXML private TableView<Inscripcion> tblInscripciones;
    @FXML private TableColumn<Inscripcion, Integer> colId;
    @FXML private TableColumn<Inscripcion, Integer> colIdParticipante;
    @FXML private TableColumn<Inscripcion, String> colCategoria;
    @FXML private TableColumn<Inscripcion, String> colFecha;
    @FXML private TableColumn<Inscripcion, String> colEstado;

    private final InscripcionDAO dao = new InscripcionDAO();
    private final ObservableList<Inscripcion> listaInscripciones = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idInscripcion"));
        colIdParticipante.setCellValueFactory(new PropertyValueFactory<>("idParticipante"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaInscripcion"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        cargarDatos();
    }

    private void cargarDatos() {
        listaInscripciones.clear();
        listaInscripciones.addAll(dao.listar());
        tblInscripciones.setItems(listaInscripciones);
    }

    @FXML
    private void handleGuardar() {
        try {
            int idPart = Integer.parseInt(txtIdParticipante.getText());
            String cat = txtCategoria.getText();
            String fecha = txtFecha.getText();
            String estado = txtEstado.getText().isEmpty() ? "Confirmado" : txtEstado.getText();

            Inscripcion i = new Inscripcion(idPart, cat, fecha, estado);
            if (dao.guardar(i)) {
                cargarDatos();
                handleLimpiar();
            }
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "El ID del participante debe ser un número entero.");
            alert.show();
        }
    }

    @FXML
    private void handleEliminar() {
        Inscripcion seleccionada = tblInscripciones.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            if (dao.eliminar(seleccionada.getIdInscripcion())) {
                cargarDatos();
            }
        }
    }

    @FXML
    private void handleLimpiar() {
        txtIdParticipante.clear();
        txtCategoria.clear();
        txtFecha.clear();
        txtEstado.clear();
    }
}