package vallegrande.edu.pe.gestion_eventos;

import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public class HelloController {

    @FXML private TableView<Participant> participantsTable;
    @FXML private TableColumn<Participant, Number> idColumn;
    @FXML private TableColumn<Participant, String> namesColumn;
    @FXML private TableColumn<Participant, String> lastNamesColumn;
    @FXML private TableColumn<Participant, String> dniColumn;
    @FXML private TableColumn<Participant, String> emailColumn;
    @FXML private TableColumn<Participant, String> phoneColumn;
    @FXML private TableColumn<Participant, String> institutionColumn;

    @FXML private TextField namesField;
    @FXML private TextField lastNamesField;
    @FXML private TextField dniField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private ComboBox<String> institutionField;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> institutionFilter;

    @FXML private VBox formBox;
    @FXML private Button refreshButton;
    @FXML private Button saveButton;
    @FXML private Button deleteButton;
    @FXML private Button reportButton;
    @FXML private Label statusLabel;
    @FXML private Label formTitle;

    private final ParticipantDao participantDao = new ParticipantDao();
    private List<Participant> allParticipants = new ArrayList<>();

    private boolean busy;
    private Integer editingId;

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(cell ->
                new ReadOnlyIntegerWrapper(cell.getValue().getId()));
        namesColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getNombres()));
        lastNamesColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getApellidos()));
        dniColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getDni()));
        emailColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getCorreo()));
        phoneColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getTelefono()));
        institutionColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(cell.getValue().getInstitucion()));

        participantsTable.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, previous, selected) -> {
                    if (selected != null && !busy) {
                        fillForm(selected);
                    }
                });

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!busy) {
                applyFilters();
            }
        });

        institutionFilter.valueProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (!busy) {
                        applyFilters();
                    }
                });

        clearForm();
        loadParticipants();
    }

    private void fillForm(Participant participant) {
        editingId = participant.getId();

        namesField.setText(participant.getNombres());
        lastNamesField.setText(participant.getApellidos());
        dniField.setText(participant.getDni());
        emailField.setText(participant.getCorreo());
        phoneField.setText(participant.getTelefono() == null
                ? "" : participant.getTelefono());

        institutionField.setValue(participant.getInstitucion());
        institutionField.getEditor().setText(participant.getInstitucion());

        formTitle.setText("Editar participante #" + editingId);
        saveButton.setText("Guardar cambios");
        deleteButton.setDisable(false);
    }

    @FXML
    private void loadParticipants() {
        if (busy) {
            return;
        }

        execute(
                "Consultando MySQL...",
                participantDao::findAll,
                participants -> {
                    allParticipants = participants;
                    refreshInstitutions();
                    applyFilters();
                }
        );
    }

    private void refreshInstitutions() {
        String selectedFilter = institutionFilter.getValue();

        List<String> institutions = allParticipants.stream()
                .map(Participant::getInstitucion)
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();

        institutionField.getItems().setAll(institutions);
        institutionFilter.getItems().setAll(institutions);

        institutionFilter.setValue(
                institutions.contains(selectedFilter) ? selectedFilter : null
        );
    }

    private void applyFilters() {
        String query = normalize(searchField.getText());
        String institution = institutionFilter.getValue();

        List<Participant> filtered = allParticipants.stream()
                .filter(p -> matches(p, query, institution))
                .toList();

        clearForm();
        participantsTable.getItems().setAll(filtered);

        statusLabel.setText(
                "Mostrando " + filtered.size()
                        + " de " + allParticipants.size() + " participantes"
        );
    }

    private boolean matches(
            Participant participant,
            String query,
            String institution
    ) {
        boolean sameInstitution = institution == null
                || institution.equals(participant.getInstitucion());

        String searchable = (
                participant.getDni() + " "
                        + participant.getNombres() + " "
                        + participant.getApellidos() + " "
                        + participant.getCorreo()
        ).toLowerCase(Locale.ROOT);

        return sameInstitution
                && searchable.contains(query.toLowerCase(Locale.ROOT));
    }

    @FXML
    private void clearFilters() {
        if (busy) {
            return;
        }

        searchField.clear();
        institutionFilter.setValue(null);
        applyFilters();
    }

    @FXML
    private void saveParticipant() {
        if (busy) {
            return;
        }

        String names = normalize(namesField.getText());
        String lastNames = normalize(lastNamesField.getText());
        String dni = normalize(dniField.getText());
        String email = normalize(emailField.getText());
        String phone = normalize(phoneField.getText());

        String enteredInstitution =
                normalize(institutionField.getEditor().getText());

        // Reutiliza la escritura de una institución existente.
        String institution = institutionField.getItems().stream()
                .filter(item -> item.equalsIgnoreCase(enteredInstitution))
                .findFirst()
                .orElse(enteredInstitution);

        if (names.isBlank() || lastNames.isBlank() || dni.isBlank()
                || email.isBlank() || institution.isBlank()) {
            showMessage(Alert.AlertType.WARNING, "Datos incompletos",
                    "Completa todos los campos obligatorios.");
            return;
        }

        if (!dni.matches("[0-9]{8}")) {
            showMessage(Alert.AlertType.WARNING, "DNI incorrecto",
                    "El DNI debe contener exactamente 8 dígitos.");
            return;
        }

        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            showMessage(Alert.AlertType.WARNING, "Correo incorrecto",
                    "Escribe un correo como nombre@ejemplo.com.");
            return;
        }

        if (names.length() > 100 || lastNames.length() > 100
                || email.length() > 150 || institution.length() > 150
                || phone.length() > 20) {
            showMessage(Alert.AlertType.WARNING, "Texto demasiado largo",
                    "Máximos: nombres y apellidos 100 caracteres; "
                            + "correo e institución 150; teléfono 20.");
            return;
        }

        final boolean editing = editingId != null;

        Participant participant = new Participant(
                editing ? editingId : 0,
                names, lastNames, dni, email,
                phone.isBlank() ? null : phone,
                institution
        );

        execute(
                "Guardando participante...",
                () -> {
                    if (editing) {
                        participantDao.update(participant);
                    } else {
                        participantDao.insert(participant);
                    }
                    return true;
                },
                result -> {
                    clearForm();
                    showMessage(
                            Alert.AlertType.INFORMATION,
                            editing ? "Actualización correcta" : "Registro correcto",
                            editing
                                    ? "Los cambios se guardaron correctamente."
                                    : "El participante se registró correctamente."
                    );
                },
                this::loadParticipants
        );
    }

    @FXML
    private void deleteParticipant() {
        if (busy || editingId == null) {
            return;
        }

        Participant selected = allParticipants.stream()
                .filter(p -> p.getId() == editingId)
                .findFirst()
                .orElse(null);

        if (selected == null) {
            showMessage(Alert.AlertType.WARNING, "Selecciona un participante",
                    "Actualiza el listado y selecciona nuevamente la persona.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Eliminar participante");
        confirmation.setHeaderText(
                selected.getNombres() + " " + selected.getApellidos()
        );
        confirmation.setContentText(
                "DNI: " + selected.getDni()
                        + "\nSe eliminará el registro de forma permanente."
                        + "\n¿Deseas continuar?"
        );

        if (confirmation.showAndWait().orElse(ButtonType.CANCEL)
                != ButtonType.OK) {
            return;
        }

        final int id = selected.getId();

        execute(
                "Eliminando participante...",
                () -> {
                    participantDao.delete(id);
                    return true;
                },
                result -> {
                    clearForm();
                    showMessage(Alert.AlertType.INFORMATION,
                            "Eliminación correcta",
                            "El participante fue eliminado.");
                },
                this::loadParticipants
        );
    }

    @FXML
    private void generateReport() {
        if (busy) {
            return;
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Guardar reporte de participantes");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Reporte HTML", "*.html")
        );

        String timestamp = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
        );

        chooser.setInitialFileName("participantes_" + timestamp + ".html");

        File destination = chooser.showSaveDialog(
                participantsTable.getScene().getWindow()
        );

        if (destination == null) {
            return;
        }

        final String query = normalize(searchField.getText());
        final String institution = institutionFilter.getValue();

        execute(
                "Consultando datos actuales y generando reporte...",
                () -> {
                    // Se vuelve a consultar MySQL al emitir cada reporte.
                    List<Participant> current = participantDao.findAll();

                    List<Participant> filtered = current.stream()
                            .filter(p -> matches(p, query, institution))
                            .toList();

                    ParticipantReport.write(
                            destination.toPath(), filtered, institution, query
                    );

                    return filtered.size();
                },
                total -> {
                    statusLabel.setText(
                            "Reporte generado: " + total + " participantes."
                    );

                    showMessage(
                            Alert.AlertType.INFORMATION,
                            "Reporte generado",
                            "Archivo guardado en:\n"
                                    + destination.getAbsolutePath()
                                    + "\n\nÁbrelo con tu navegador y pulsa "
                                    + "«Imprimir / Guardar PDF»."
                    );
                }
        );
    }

    @FXML
    private void clearForm() {
        editingId = null;
        participantsTable.getSelectionModel().clearSelection();

        namesField.clear();
        lastNamesField.clear();
        dniField.clear();
        emailField.clear();
        phoneField.clear();
        institutionField.setValue(null);
        institutionField.getEditor().clear();

        formTitle.setText("Nuevo participante");
        saveButton.setText("Guardar participante");
        deleteButton.setDisable(true);
    }

    private String normalize(String value) {
        return value == null ? "" : value.strip().replaceAll("\\s+", " ");
    }

    private void setBusy(boolean value) {
        busy = value;
        formBox.setDisable(value);
        refreshButton.setDisable(value);
        reportButton.setDisable(value);
        searchField.setDisable(value);
        institutionFilter.setDisable(value);
        participantsTable.setDisable(value);
        deleteButton.setDisable(value || editingId == null);
    }

    private <T> void execute(
            String message,
            Callable<T> operation,
            Consumer<T> onSuccess
    ) {
        execute(message, operation, onSuccess, () -> {});
    }

    private <T> void execute(
            String message,
            Callable<T> operation,
            Consumer<T> onSuccess,
            Runnable afterSuccess
    ) {
        setBusy(true);
        statusLabel.setText(message);

        Task<T> task = new Task<>() {
            @Override
            protected T call() throws Exception {
                return operation.call();
            }
        };

        task.setOnSucceeded(event -> {
            try {
                onSuccess.accept(task.getValue());
            } finally {
                setBusy(false);
            }
            afterSuccess.run();
        });

        task.setOnFailed(event -> {
            setBusy(false);
            statusLabel.setText("La operación no se completó.");

            Throwable error = task.getException();
            String errorMessage = error.getMessage();

            if (error instanceof SQLException sqlError) {
                if (sqlError.getErrorCode() == 1062) {
                    errorMessage = "Ya existe un participante con ese DNI.";
                } else if (sqlError.getErrorCode() == 1451) {
                    errorMessage = "No se puede eliminar este participante "
                            + "porque tiene registros relacionados, "
                            + "como inscripciones.";
                }
            }

            showMessage(Alert.AlertType.ERROR, "Error", errorMessage);
        });

        Thread thread = new Thread(task, "participants-database");
        thread.setDaemon(true);
        thread.start();
    }

    private void showMessage(
            Alert.AlertType type,
            String title,
            String message
    ) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message == null ? "Error sin detalle." : message);
        alert.showAndWait();
    }
}