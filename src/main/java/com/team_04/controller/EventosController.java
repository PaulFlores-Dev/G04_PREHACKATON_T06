package com.team_04.controller;

import com.team_04.dao.EventoDAO;
import com.team_04.model.Evento;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;
import java.io.InputStream;
import java.sql.Connection;
import com.team_04.util.ConexionBD;

public class EventosController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtDescripcion;
    @FXML private DatePicker dpFecha;
    @FXML private TextField txtLugar;
    @FXML private ComboBox<String> cbModalidad;
    @FXML private ComboBox<String> cbEstado;

    @FXML private TableView<Evento> tablaEventos;
    @FXML private TableColumn<Evento, Integer> colId;
    @FXML private TableColumn<Evento, String> colNombre;
    @FXML private TableColumn<Evento, LocalDate> colFecha;
    @FXML private TableColumn<Evento, String> colLugar;
    @FXML private TableColumn<Evento, String> colModalidad;
    @FXML private TableColumn<Evento, String> colEstado;

    private final EventoDAO eventoDAO = new EventoDAO();
    private final ObservableList<Evento> listaEventos = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        cbModalidad.setItems(FXCollections.observableArrayList("Presencial", "Virtual", "Híbrido"));
        cbEstado.setItems(FXCollections.observableArrayList("Programado", "En Curso", "Finalizado", "Cancelado"));

        colId.setCellValueFactory(new PropertyValueFactory<>("idEvento"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colLugar.setCellValueFactory(new PropertyValueFactory<>("lugar"));
        colModalidad.setCellValueFactory(new PropertyValueFactory<>("modalidad"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        cargarDatos();

        tablaEventos.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                txtNombre.setText(newVal.getNombre());
                txtDescripcion.setText(newVal.getDescripcion());
                dpFecha.setValue(newVal.getFecha());
                txtLugar.setText(newVal.getLugar());
                cbModalidad.setValue(newVal.getModalidad());
                cbEstado.setValue(newVal.getEstado());
            }
        });
    }

    private void cargarDatos() {
        listaEventos.clear();
        listaEventos.addAll(eventoDAO.listarTodos());
        tablaEventos.setItems(listaEventos);
    }

    @FXML
    private void guardarEvento() {
        if (txtNombre.getText().isEmpty() || dpFecha.getValue() == null) {
            mostrarAlerta("Error de Validación", "Por favor complete los campos obligatorios.");
            return;
        }

        Evento e = new Evento(
                txtNombre.getText(),
                txtDescripcion.getText(),
                dpFecha.getValue(),
                txtLugar.getText(),
                cbModalidad.getValue() != null ? cbModalidad.getValue() : "Presencial",
                cbEstado.getValue() != null ? cbEstado.getValue() : "Programado"
        );

        if (eventoDAO.insertar(e)) {
            mostrarAlerta("Éxito", "Evento guardado correctamente.");
            limpiarCampos();
            cargarDatos();
        } else {
            mostrarAlerta("Error", "No se pudo guardar el evento.");
        }
    }

    @FXML
    private void eliminarEvento() {
        Evento seleccionado = tablaEventos.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            if (eventoDAO.eliminar(seleccionado.getIdEvento())) {
                mostrarAlerta("Éxito", "Evento eliminado correctamente.");
                cargarDatos();
            } else {
                mostrarAlerta("Error", "No se pudo eliminar el evento.");
            }
        } else {
            mostrarAlerta("Advertencia", "Seleccione un evento de la tabla.");
        }
    }

    @FXML
    private void limpiarCampos() {
        txtNombre.clear();
        txtDescripcion.clear();
        dpFecha.setValue(null);
        txtLugar.clear();
        cbModalidad.getSelectionModel().clearSelection();
        cbEstado.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    @FXML
    private void actualizarEvento() {
        Evento seleccionado = tablaEventos.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            seleccionado.setNombre(txtNombre.getText());
            seleccionado.setDescripcion(txtDescripcion.getText());
            seleccionado.setFecha(dpFecha.getValue());
            seleccionado.setLugar(txtLugar.getText());
            seleccionado.setModalidad(cbModalidad.getValue());
            seleccionado.setEstado(cbEstado.getValue());

            if (eventoDAO.actualizar(seleccionado)) {
                mostrarAlerta("Éxito", "Evento actualizado correctamente.");
                cargarDatos();
                limpiarCampos();
            } else {
                mostrarAlerta("Error", "No se pudo actualizar el evento.");
            }
        } else {
            mostrarAlerta("Advertencia", "Seleccione un evento de la tabla para editar.");
        }
    }

    @FXML
    private void generarReporte() {
        try {
            InputStream is = getClass().getResourceAsStream("/reports/eventos_reporte.jrxml");
            if (is == null) {
                mostrarAlerta("Error", "No se encontró la plantilla del reporte.");
                return;
            }
            JasperReport jr = JasperCompileManager.compileReport(is);
            Connection con = ConexionBD.getConexion();
            JasperPrint jp = JasperFillManager.fillReport(jr, null, con);
            JasperViewer.viewReport(jp, false);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Ocurrió un problema al generar el reporte PDF: " + e.getMessage());
        }
    }
}