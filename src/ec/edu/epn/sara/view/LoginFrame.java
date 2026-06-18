package ec.edu.epn.sara.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Ventana de inicio de sesión (Login) para el sistema SARA utilizando Swing.
 * Aplica el Principio de Responsabilidad Única para la capa de presentación.
 */
public class LoginFrame extends JFrame {

    // Componentes visuales de la interfaz
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public LoginFrame() {
        // Configuración básica de la ventana (JFrame)
        setTitle("SARA - Sistema de Análisis de Rendimiento Académico");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centra la ventana en la pantalla
        setLayout(new BorderLayout(10, 10));

        // 1. Panel de Encabezado (Título Superior)
        JPanel panelHeader = new JPanel();
        panelHeader.setBackground(new Color(30, 144, 255)); // Azul brillante académico
        JLabel lblTitle = new JLabel("BIENVENIDO A SARA");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        panelHeader.add(lblTitle);
        add(panelHeader, BorderLayout.NORTH);

        // 2. Panel Central (Formulario de campos)
        JPanel panelForm = new JPanel(new GridLayout(2, 2, 10, 10));
        panelForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblEmail = new JLabel("Correo Electrónico:");
        lblEmail.setFont(new Font("Arial", Font.PLAIN, 12));
        txtEmail = new JTextField();

        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setFont(new Font("Arial", Font.PLAIN, 12));
        txtPassword = new JPasswordField();

        panelForm.add(lblEmail);
        panelForm.add(txtEmail);
        panelForm.add(lblPassword);
        panelForm.add(txtPassword);
        add(panelForm, BorderLayout.CENTER);

        // 3. Panel Inferior (Botón de Acción)
        JPanel panelFooter = new JPanel();
        btnLogin = new JButton("Iniciar Sesión");
        btnLogin.setFont(new Font("Arial", Font.BOLD, 12));
        btnLogin.setBackground(new Color(34, 139, 34)); // Verde bosque funcional
        btnLogin.setForeground(Color.WHITE);
        panelFooter.add(btnLogin);
        add(panelFooter, BorderLayout.SOUTH);

        // 4. Manejo de Eventos (Controlador del botón)
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                validarLogin();
            }
        });
    }

    /**
     * Captura las entradas del usuario y realiza una verificación inicial.
     * Aquí se conectará con el motor lógico en el futuro.
     */
    /**
     * Captura las entradas del usuario y valida las credenciales
     * buscando directamente en el archivo de persistencia CSV.
     */
    private void validarLogin() {
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        // 1. Validación de campos vacíos
        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, llene todos los campos del formulario.",
                    "Campos Vacíos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Instanciar el motor de persistencia y cargar la lista de alumnos reales
        ec.edu.epn.sara.infrastructure.CSVDataPersistence persistence = new ec.edu.epn.sara.infrastructure.CSVDataPersistence();
        java.util.List<ec.edu.epn.sara.domain.Student> listaEstudiantes = persistence.loadStudents();

        // 3. Buscar al usuario en la lista cargada desde el CSV
        boolean usuarioEncontrado = false;
        for (ec.edu.epn.sara.domain.Student estudiante : listaEstudiantes) {
            if (estudiante.getEmail().equalsIgnoreCase(email) && estudiante.getPassword().equals(password)) {
                usuarioEncontrado = true;
                JOptionPane.showMessageDialog(this,
                        "¡Autenticación Exitosa!\nBienvenido(a) " + estudiante.getFirstName() + " " + estudiante.getLastName() + " al sistema SARA.",
                        "Acceso Concedido",
                        JOptionPane.INFORMATION_MESSAGE);
                break;
            }
        }

        // 4. Si no coinciden las credenciales
        if (!usuarioEncontrado) {
            JOptionPane.showMessageDialog(this,
                    "Credenciales incorrectas o usuario no registrado en el sistema.",
                    "Acceso Denegado",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}