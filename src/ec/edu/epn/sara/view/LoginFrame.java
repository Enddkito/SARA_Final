package ec.edu.epn.sara.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {

    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnBack; // Botón para regresar

    private String selectedRole; // "Estudiante" o "Profesor"
    private JFrame previousFrame; // Referencia a la ventana anterior

    public LoginFrame(String role, JFrame previous) {
        this.selectedRole = role;
        this.previousFrame = previous;

        setTitle("SARA - Iniciar Sesión como " + role);
        setSize(400, 280);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // 1. Panel de Encabezado con botón de regreso incorporado
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(role.equals("Estudiante") ? new Color(30, 144, 255) : new Color(75, 0, 130));
        panelHeader.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Flecha/Botón para regresar de manera intuitiva
        btnBack = new JButton("← Volver");
        btnBack.setFont(new Font("Arial", Font.BOLD, 11));
        btnBack.setBackground(Color.DARK_GRAY);
        btnBack.setForeground(Color.WHITE);
        panelHeader.add(btnBack, BorderLayout.WEST);

        JLabel lblTitle = new JLabel("INGRESO - MÓDULO " + role.toUpperCase(), JLabel.CENTER);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));
        panelHeader.add(lblTitle, BorderLayout.CENTER);

        add(panelHeader, BorderLayout.NORTH);

        // 2. Panel Central: Formulario
        JPanel panelForm = new JPanel(new GridLayout(2, 2, 10, 10));
        panelForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblEmail = new JLabel("Correo Institucional:");
        txtEmail = new JTextField();
        JLabel lblPassword = new JLabel("Contraseña:");
        txtPassword = new JPasswordField();

        panelForm.add(lblEmail);
        panelForm.add(txtEmail);
        panelForm.add(lblPassword);
        panelForm.add(txtPassword);
        add(panelForm, BorderLayout.CENTER);

        // 3. Panel Inferior: Botón de login
        JPanel panelFooter = new JPanel();
        btnLogin = new JButton("Ingresar al Sistema");
        btnLogin.setFont(new Font("Arial", Font.BOLD, 12));
        btnLogin.setBackground(new Color(34, 139, 34));
        btnLogin.setForeground(Color.WHITE);
        panelFooter.add(btnLogin);
        add(panelFooter, BorderLayout.SOUTH);

        // --- MANEJO DE EVENTOS ---

        // Evento para regresar al módulo anterior sin reiniciar el programa
        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Cierra el Login actual
                previousFrame.setVisible(true); // Vuelve a mostrar la pantalla "Soy..."
            }
        });

        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                validarLoginPorRol();
            }
        });
    }

    private void validarLoginPorRol() {
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, llene todos los campos.", "Campos Vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // SEGURIDAD REQUERIDA: Restringir acceso según la zona elegida
        if (selectedRole.equals("Profesor")) {
            // Zona Profesores: Solo admite al profesor registrado
            if (email.equals("profesor@epn.edu.ec") && password.equals("profesor123")) {
                JOptionPane.showMessageDialog(this, "¡Acceso Concedido como Docente!");
                this.dispose();
                new ProfessorDashboard(this).setVisible(true); // Le pasamos este login por si desea cerrar sesión
            } else {
                JOptionPane.showMessageDialog(this, "Acceso Denegado. Credenciales de Profesor incorrectas o inexistentes en este módulo.", "Error de Autenticación", JOptionPane.ERROR_MESSAGE);
            }
        } else if (selectedRole.equals("Estudiante")) {
            // Zona Estudiantes: Busca en nuestro archivo real CSV de alumnos
            ec.edu.epn.sara.infrastructure.CSVDataPersistence persistence = new ec.edu.epn.sara.infrastructure.CSVDataPersistence();
            java.util.List<ec.edu.epn.sara.domain.Student> listaEstudiantes = persistence.loadStudents();

            boolean encontrado = false;
            for (ec.edu.epn.sara.domain.Student s : listaEstudiantes) {
                if (s.getEmail().equalsIgnoreCase(email) && s.getPassword().equals(password)) {
                    encontrado = true;
                    JOptionPane.showMessageDialog(this, "¡Autenticación Exitosa!\nBienvenido(a) al sistema SARA.", "Acceso Concedido", JOptionPane.INFORMATION_MESSAGE);

                    this.dispose(); // Oculta el login
                    // Abrimos el nuevo Dashboard pasándole el objeto estudiante 's' cargado del CSV y el Login actual
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