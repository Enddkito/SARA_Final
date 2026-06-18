package ec.edu.epn.sara.view;

import ec.edu.epn.sara.domain.Student;
import ec.edu.epn.sara.domain.Enrollment;
import ec.edu.epn.sara.infrastructure.CSVDataPersistence; // IMPORTACIÓN AGREGADA
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList; // IMPORTACIÓN AGREGADA

/**
 * Panel de control principal para el rol del Estudiante.
 * Muestra el récord académico y las calificaciones calculadas del alumno.
 */
public class StudentDashboard extends JFrame {

    private JTable tableGrades;
    private DefaultTableModel tableModel;
    private JButton btnLogout;
    private JFrame loginFrame;
    private Student currentStudent;

    public StudentDashboard(Student student, JFrame loginWindow) {
        this.loginFrame = loginWindow;

        // === PRIMERO: CARGAR LAS NOTAS ACTUALIZADAS DESDE EL ARCHIVO INDEPENDIENTE ===
        CSVDataPersistence persistence = new CSVDataPersistence();
        java.util.List<Student> systemStudents = persistence.loadStudents();
        persistence.loadGradesIntoStudents(systemStudents, new ArrayList<>());

        // Buscar tu instancia actualizada con los enrollments cargados en memoria
        this.currentStudent = student; // Respaldo por defecto
        for (Student s : systemStudents) {
            if (s.getId().equals(student.getId())) {
                this.currentStudent = s;
                break;
            }
        }

        // === SEGUNDO: CONFIGURACIÓN DE LA INTERFAZ GRÁFICA ===
        setTitle("SARA - Panel de Control del Estudiante");
        setSize(600, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));

        // 1. Encabezado institucional con botón de Cerrar Sesión
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(new Color(30, 144, 255)); // Azul brillante académico
        panelHeader.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        btnLogout = new JButton("← Cerrar Sesión");
        btnLogout.setBackground(Color.DARK_GRAY);
        btnLogout.setForeground(Color.WHITE);
        panelHeader.add(btnLogout, BorderLayout.WEST);

        // Mensaje de bienvenida personalizado con el nombre real del alumno
        JLabel lblWelcome = new JLabel("ESTUDIANTE: " + currentStudent.getFirstName().toUpperCase() + " " + currentStudent.getLastName().toUpperCase(), JLabel.CENTER);
        lblWelcome.setForeground(Color.WHITE);
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 13));
        panelHeader.add(lblWelcome, BorderLayout.CENTER);
        add(panelHeader, BorderLayout.NORTH);

        // 2. Panel Central: Tabla de Calificaciones
        JPanel panelCenter = new JPanel(new BorderLayout(5, 5));
        panelCenter.setBorder(BorderFactory.createTitledBorder("Mis Asignaturas Inscritas y Calificaciones"));

        // Definir las columnas de la tabla académica
        String[] columns = {"Código Materia", "Componente 1", "Componente 2", "Nota Final", "Estado"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Evita que el estudiante altere sus notas de forma visual
            }
        };

        // === TERCERO: LLENAR LA TABLA (Ahora que currentStudent ya tiene las notas cargadas) ===
        tableTableGrades();

        tableGrades = new JTable(tableModel);
        tableGrades.getTableHeader().setReorderingAllowed(false); // Bloquea el arrastre de columnas
        panelCenter.add(new JScrollPane(tableGrades), BorderLayout.CENTER);
        add(panelCenter, BorderLayout.CENTER);

        // 3. Controlador para regresar al Login original
        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Cierra el dashboard liberando memoria
                loginFrame.setVisible(true); // Regresa al formulario de login
            }
        });
    }

    /**
     * Llena la tabla con los datos del alumno cargados desde el CSV
     */
    private void tableTableGrades() {
        // Protección de seguridad contra nulos
        if (currentStudent == null || currentStudent.getEnrollments() == null) {
            return;
        }

        for (Enrollment e : currentStudent.getEnrollments()) {
            // Si las notas no están asignadas aún, se muestra un texto o el número
            String c1 = (e.getComponent1() == 0) ? "N/A" : String.valueOf(e.getComponent1());
            String c2 = (e.getComponent2() == 0) ? "N/A" : String.valueOf(e.getComponent2());
            String finalGrade = (e.getComponent1() == 0 && e.getComponent2() == 0) ? "N/A" : String.valueOf(e.getFinalGrade());

            // Estado predictivo inicial basado en la nota
            String status = (e.getFinalGrade() >= 7.0) ? "APROBADO" : "EN RIESGO / EN CURSO";

            Object[] rowData = {
                    e.getCourse().getCode(),
                    c1,
                    c2,
                    finalGrade,
                    status
            };
            tableModel.addRow(rowData);
        }
    }
}