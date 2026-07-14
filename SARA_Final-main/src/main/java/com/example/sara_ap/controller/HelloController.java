package com.example.sara_ap.controller;

import javafx.stage.Window;
import javafx.scene.control.Alert;
import com.example.sara_ap.HelloApplication;
import com.example.sara_ap.domain.Student;
import com.example.sara_ap.domain.Professor;
import com.example.sara_ap.infrastructure.CSVDataPersistence;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HelloController {

    @FXML
    private TextField txtEmail;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private Label lblTipoAcceso;

    private static String rolSeleccionado = ""; // Guarda temporalmente si es Profesor o Estudiante
    private CSVDataPersistence persistencia = new CSVDataPersistence();
    private List<com.example.sara_ap.domain.Course> cursosDisponibles = new ArrayList<>();

    // 👨‍🏫 Acción al pulsar "Soy Profesor"
    @FXML
    protected void onProfesorClick() {
        rolSeleccionado = "PROFESOR";
        cambiarPantalla("/com/example/sara_ap/login-view.fxml");
    }

    // 🎓 Acción al pulsar "Soy Estudiante"
    @FXML
    protected void onEstudianteClick() {
        rolSeleccionado = "ESTUDIANTE";
        cambiarPantalla("/com/example/sara_ap/login-view.fxml");
    }

    // 🔙 Acción al pulsar el botón "Volver"
    @FXML
    protected void onVolverClick() {
        rolSeleccionado = ""; // Reinicia el rol seleccionado
        cambiarPantalla("/com/example/sara_ap/hello-view.fxml"); // Nos regresa a la pantalla de perfiles
    }

    // 🔓 Botón "Iniciar Sesión" (Valida las claves según el rol elegido)
    @FXML
    protected void onAutenticarClick() {
        try {
            String emailInput = txtEmail.getText().trim();
            String passwordInput = txtPassword.getText().trim();

            if (emailInput.isEmpty() || passwordInput.isEmpty()) {
                mostrarAlerta("Campos Vacíos", "Por favor, llene todos los campos obligatorios.", Alert.AlertType.WARNING);
                return;
            }

            if (rolSeleccionado.equals("PROFESOR")) {
                List<Professor> profesores = persistencia.loadProfessors();
                for (Professor p : profesores) {
                    // 🔑 Sanitización para evitar desfasados por retornos de carro (\r) en el CSV
                    String csvEmail = p.getEmail() != null ? p.getEmail().replaceAll("[\\r\\n]", "").trim() : "";
                    String csvPassword = p.getPassword() != null ? p.getPassword().replaceAll("[\\r\\n]", "").trim() : "";

                    if (csvEmail.equalsIgnoreCase(emailInput) && csvPassword.equals(passwordInput)) {
                        try {
                            Stage stage = (Stage) txtEmail.getScene().getWindow();
                            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("/com/example/sara_ap/teacher-view.fxml"));
                            Scene scene = new Scene(loader.load());

                            // Pasamos el profesor autenticado para llenar el ComboBox por ID textual
                            TeacherController controller = loader.getController();
                            controller.initData(p);

                            stage.setScene(scene);
                            stage.centerOnScreen();
                        } catch (IOException e) {
                            mostrarAlerta("Error de Interfaz", "Fallo al cargar el FXML del Profesor: " + e.getMessage(), Alert.AlertType.ERROR);
                            e.printStackTrace();
                        }
                        return;
                    }
                }
            } else if (rolSeleccionado.equals("ESTUDIANTE")) {
                List<Student> estudiantes = persistencia.loadFullSystemData(cursosDisponibles);
                for (Student s : estudiantes) {
                    // 🔑 Sanitización homóloga para el módulo de estudiantes
                    String csvEmail = s.getEmail() != null ? s.getEmail().replaceAll("[\\r\\n]", "").trim() : "";
                    String csvPassword = s.getPassword() != null ? s.getPassword().replaceAll("[\\r\\n]", "").trim() : "";

                    if (csvEmail.equalsIgnoreCase(emailInput) && csvPassword.equals(passwordInput)) {
                        try {
                            Stage stage = (Stage) txtEmail.getScene().getWindow();
                            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("/com/example/sara_ap/student-view.fxml"));
                            Scene scene = new Scene(loader.load());

                            // 🔑 🚀 CONEXIÓN ADAPTATIVA: Conseguimos el controlador inyectado y le pasamos el estudiante dinámico
                            StudentController studentController = loader.getController();
                            studentController.initData(s);

                            stage.setScene(scene);
                            stage.setTitle("SARA - Panel del Estudiante");
                            stage.centerOnScreen();
                        } catch (IOException e) {
                            mostrarAlerta("Error de Interfaz", "Fallo al cargar el FXML del Estudiante: " + e.getMessage(), Alert.AlertType.ERROR);
                            e.printStackTrace();
                        }
                        return;
                    }
                }
            }

            mostrarAlerta("Acceso Denegado", "El correo electrónico o la contraseña son incorrectos.", Alert.AlertType.ERROR);

        } catch (Exception ex) {
            mostrarAlerta("Error Crítico", ex.toString(), Alert.AlertType.ERROR);
            ex.printStackTrace();
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void cambiarPantalla(String fxmlFile) {
        try {
            Stage stage = (Stage) Stage.getWindows().filtered(Window::isShowing).get(0);
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(fxmlFile));

            Scene newScene = new Scene(fxmlLoader.load());
            stage.setScene(newScene);
            stage.centerOnScreen();
        } catch (IOException e) {
            System.err.println("Error al cambiar a la pantalla " + fxmlFile + ": " + e.getMessage());
        }
    }
}