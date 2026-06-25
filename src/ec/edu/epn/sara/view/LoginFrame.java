package ec.edu.epn.sara.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Formulario de Autenticación (Login) del Sistema SARA.
 * Implementa control de roles y validación dinámica contra archivos CSV.
 */
public class LoginFrame extends JFrame {

    private String selectedRole; // "Estudiante" o "Profesor"
    private JFrame parentWindow; // Guarda la referencia de la ventana de bienvenida

    // Componentes gráficos del formulario
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnBack;

    public LoginFrame(String role, JFrame welcomeWindow) {
        this.selectedRole = role;
        this.parentWindow = welcomeWindow;

        // Configuración básica de la ventana de Login
        setTitle("SARA - Inicio de Sesión: " + role);
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // 1. Panel Superior: Título descriptivo según el Rol elegido
        JPanel panelHeader = new JPanel();
        panelHeader.setBackground(role.equals("Profesor") ? new Color(75, 0, 130) : new Color(30, 144, 255));
        JLabel lblTitle = new JLabel("MÓDULO DE ACCESO: " + role.toUpperCase());
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 13));
        panelHeader.add(lblTitle);
        add(panelHeader, BorderLayout.NORTH);

        // 2. Panel Central: Formulario de credenciales (Grid ordenado)
        JPanel panelForm = new JPanel(new GridLayout(2, 2, 5, 15));
        panelForm.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        panelForm.add(new JLabel("Correo Institucional:"));
        txtEmail = new JTextField();
        panelForm.add(txtEmail);

        panelForm.add(new JLabel("Contraseña de Acceso:"));
        txtPassword = new JPasswordField();
        panelForm.add(txtPassword);

        add(panelForm, BorderLayout.CENTER);

        // 3. Panel Inferior: Botonera de acciones (Ingresar / Regresar)
        JPanel panelFooter = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        btnBack = new JButton("← Volver");
        btnBack.setBackground(Color.LIGHT_GRAY);
        panelFooter.add(btnBack);

        btnLogin = new JButton("Ingresar al Sistema");
        btnLogin.setBackground(new Color(34, 139, 34)); // Verde seguro institucional
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Arial", Font.BOLD, 12));
        panelFooter.add(btnLogin);

        add(panelFooter, BorderLayout.SOUTH);

        // --- CONTROLADORES DE EVENTOS (LISTENERS) ---

        // Evento para procesar la autenticación de credenciales
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                validarLoginPorRol();
            }
        });

        // Evento para regresar a la pantalla de selección de rol (WelcomeFrame)
        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Cierra el LoginFrame actual liberando memoria
                parentWindow.setVisible(true); // Hace visible la ventana padre
            }
        });
    }

    /**
     * Valida de manera dinámica los datos ingresados contra la persistencia en CSV
     * según el rol preseleccionado en la pantalla de bienvenida.
     */
    private void validarLoginPorRol() {
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, llene todos los campos.", "Campos Vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Instancia de persistencia para cargar los archivos CSV
        ec.edu.epn.sara.infrastructure.CSVDataPersistence persistence = new ec.edu.epn.sara.infrastructure.CSVDataPersistence();

        // ================= ZONA DE PROFESORES =================
        if (selectedRole.equals("Profesor")) {
            // Cargamos la lista dinámica desde professors.csv de forma relacional
            java.util.List<ec.edu.epn.sara.domain.Professor> listaProfesores = persistence.loadProfessors();
            boolean encontrado = false;

            // Buscamos si coinciden las credenciales ingresadas con algún registro del CSV
            for (ec.edu.epn.sara.domain.Professor p : listaProfesores) {
                if (p.getEmail().equalsIgnoreCase(email) && p.getPassword().equals(password)) {
                    encontrado = true;
                    JOptionPane.showMessageDialog(this, "¡Autenticación Exitosa!\nBienvenido(a) Dr(a). " + p.getLastName() + " al sistema SARA.", "Acceso Concedido", JOptionPane.INFORMATION_MESSAGE);

                    this.dispose(); // Ocultamos el login

                    // Conexión Relacional Correcta: Enviamos la ventana padre y el ID del docente conectado
                    new ProfessorDashboard(this, p.getId()).setVisible(true);
                    break;
                }
            }

            if (!encontrado) {
                JOptionPane.showMessageDialog(this, "Acceso Denegado. Credenciales de Profesor incorrectas.", "Error de Autenticación", JOptionPane.ERROR_MESSAGE);
            }

            // ================= ZONA DE ESTUDIANTES =================
        } else if (selectedRole.equals("Estudiante")) {
            java.util.List<ec.edu.epn.sara.domain.Student> listaEstudiantes = persistence.loadStudents();
            boolean encontrado = false;

            for (ec.edu.epn.sara.domain.Student s : listaEstudiantes) {
                if (s.getEmail().equalsIgnoreCase(email) && s.getPassword().equals(password)) {
                    encontrado = true;
                    JOptionPane.showMessageDialog(this, "¡Autenticación Exitosa!\nBienvenido(a) al sistema SARA.", "Acceso Concedido", JOptionPane.INFORMATION_MESSAGE);

                    this.dispose();
                    new StudentDashboard(s, this).setVisible(true);
                    break;
                }
            }

            if (!encontrado) {
                JOptionPane.showMessageDialog(this, "Acceso Denegado. Si usted es Profesor, por favor use el módulo correspondiente.", "Error de Autenticación", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}