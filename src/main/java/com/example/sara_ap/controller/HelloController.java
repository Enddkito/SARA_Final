package com.example.sara_ap.controller;
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
        cambiarPantalla("/com/example/sara_ap/hello-view.fxml"); // Nos regresa volando a la pantalla de perfiles
    }

    // 🔓 Botón "Iniciar Sesión" (Valida las claves según el rol elegido)
    // 🔓 Botón "Iniciar Sesión" (Valida las claves según el rol elegido)
    @FXML
    protected void onAutenticarClick() {
        String emailInput = txtEmail.getText().trim();
        String passwordInput = txtPassword.getText().trim();

        if (emailInput.isEmpty() || passwordInput.isEmpty()) {
            mostrarAlerta("Campos Vacíos", "Por favor, llene todos los campos del formulario.", Alert.AlertType.WARNING);
            return;
        }

        if (rolSeleccionado.equals("PROFESOR")) {
            List<Professor> profesores = persistencia.loadProfessors();
            for (Professor p : profesores) {
                if (p.getEmail().trim().equalsIgnoreCase(emailInput) && p.getPassword().equals(passwordInput)) {
                    try {
                        Stage stage = (Stage) txtEmail.getScene().getWindow();
                        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("/com/example/sara_ap/teacher-view.fxml"));
                        Scene scene = new Scene(loader.load(), 650, 500);

                        TeacherController controller = loader.getController();
                        controller.initData(p);

                        stage.setScene(scene);
                        stage.centerOnScreen();
                    } catch (IOException e) {
                        System.err.println("Error al cargar el panel del profesor: " + e.getMessage());
                        e.printStackTrace();
                    }
                    return;
                }
            }
        } else if (rolSeleccionado.equals("ESTUDIANTE")) {
            List<Student> estudiantes = persistencia.loadFullSystemData(cursosDisponibles);
            for (Student s : estudiantes) {
                if (s.getEmail().trim().equalsIgnoreCase(emailInput) && s.getPassword().equals(passwordInput)) {
                    try {
                        Stage stage = (Stage) txtEmail.getScene().getWindow();
                        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("/com/example/sara_ap/student-view.fxml"));
                        Scene scene = new Scene(loader.load(), 650, 500);

                        stage.setScene(scene);
                        stage.setTitle("SARA - Panel del Estudiante");
                        stage.centerOnScreen();
                    } catch (IOException e) {
                        System.err.println("Error al cargar el panel del estudiante: " + e.getMessage());
                        e.printStackTrace();
                    }
                    return;
                }
            }
        }

        // ❌ Si el bucle termina y no retornó, las credenciales están mal: ¡Mostramos la alerta visual!
        mostrarAlerta("Error de Autenticación", "El correo electrónico o la contraseña son incorrectos para el perfil " + rolSeleccionado.toLowerCase() + ".", Alert.AlertType.ERROR);
    }

    // 🔥 Método auxiliar para crear alertas visuales rápidamente
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    // 🔄 Método utilitario para alternar los diseños FXML dentro de la misma ventana
    private void cambiarPantalla(String fxmlFile) {
        try {
            Stage stage = (Stage) Stage.getWindows().filtered(w -> w.isShowing()).get(0);
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(fxmlFile));
            Scene newScene = new Scene(fxmlLoader.load(), 450, 400);
            stage.setScene(newScene);
        } catch (IOException e) {
            System.err.println("Error al cambiar a la pantalla " + fxmlFile + ": " + e.getMessage());
        }
    }

}