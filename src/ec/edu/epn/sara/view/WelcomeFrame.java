package ec.edu.epn.sara.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Ventana inicial de selección de rol para el sistema SARA.
 */
public class WelcomeFrame extends JFrame {

    private JButton btnStudent;
    private JButton btnProfessor;

    public WelcomeFrame() {
        setTitle("SARA - Selección de Módulo");
        setSize(450, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));

        // 1. Panel de Encabezado
        JPanel panelHeader = new JPanel();
        panelHeader.setBackground(new Color(24, 43, 73)); // Azul obscuro institucional de la EPN
        JLabel lblTitle = new JLabel("SISTEMA SARA - CONTROL ACADÉMICO");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));
        panelHeader.add(lblTitle);
        add(panelHeader, BorderLayout.NORTH);

        // 2. Panel Central: Pregunta de Selección
        JPanel panelCenter = new JPanel(new BorderLayout());
        JLabel lblQuestion = new JLabel("Por favor, seleccione su rol para ingresar al sistema:", JLabel.CENTER);
        lblQuestion.setFont(new Font("Arial", Font.PLAIN, 13));
        lblQuestion.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        panelCenter.add(lblQuestion, BorderLayout.NORTH);

        // Grid para los dos botones grandes de roles
        JPanel panelButtons = new JPanel(new GridLayout(1, 2, 20, 0));
        panelButtons.setBorder(BorderFactory.createEmptyBorder(0, 30, 20, 30));

        btnStudent = new JButton("SOY ESTUDIANTE");
        btnStudent.setFont(new Font("Arial", Font.BOLD, 12));
        btnStudent.setBackground(new Color(30, 144, 255)); // Azul brillante
        btnStudent.setForeground(Color.WHITE);

        btnProfessor = new JButton("SOY PROFESOR");
        btnProfessor.setFont(new Font("Arial", Font.BOLD, 12));
        btnProfessor.setBackground(new Color(75, 0, 130)); // Morado elegante
        btnProfessor.setForeground(Color.WHITE);

        panelButtons.add(btnStudent);
        panelButtons.add(btnProfessor);
        panelCenter.add(panelButtons, BorderLayout.CENTER);
        add(panelCenter, BorderLayout.CENTER);

        // 3. Manejo de Eventos: Abrir el Login con el rol preseleccionado
        btnStudent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirLoginConRol("Estudiante");
            }
        });

        btnProfessor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirLoginConRol("Profesor");
            }
        });
    }

    private void abrirLoginConRol(String rol) {
        this.setVisible(false); // Oculta la pantalla de bienvenida temporalmente

        // Instancia el nuevo login pasando el rol elegido y esta ventana como padre
        LoginFrame loginForm = new LoginFrame(rol, this);
        loginForm.setVisible(true);
    }
}