package ec.edu.epn.sara.view;

import ec.edu.epn.sara.domain.Student;
import ec.edu.epn.sara.domain.Enrollment;
import ec.edu.epn.sara.infrastructure.CSVDataPersistence;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel de control interactivo para el rol del Profesor.
 * Permite visualizar actas de notas, editarlas en caliente y cargar archivos CSV.
 */
public class ProfessorDashboard extends JFrame {

    private JTable tableGrades;
    private DefaultTableModel tableModel;
    private JButton btnImportCSV;
    private JButton btnSaveChanges;
    private JButton btnLogout;

    private CSVDataPersistence persistence;
    private JFrame loginFrame;
    private List<Student> systemStudents;
    private List<ec.edu.epn.sara.domain.Course> availableCourses;
    private String currentProfessorId; // Nueva variable de instancia para el filtro dinámico

    // Constructor único unificado que recibe el ID del profesor que inició sesión
    public ProfessorDashboard(JFrame loginWindow, String professorId) {
        this.loginFrame = loginWindow;
        this.currentProfessorId = professorId; // Guardamos el ID dinámico
        this.persistence = new CSVDataPersistence();
        this.availableCourses = new ArrayList<>();

        // 1. Cargar la información base del sistema de forma relacional conectando los archivos
        this.systemStudents = persistence.loadFullSystemData(this.availableCourses);

        // 2. Configurar Ventana Principal
        setTitle("SARA - Panel de Control del Docente");
        setSize(750, 480); // Un poco más ancho para que quepan holgadamente las columnas
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));

        // 3. Encabezado Institucional Elegante
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(new Color(75, 0, 130)); // Morado docente
        panelHeader.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        btnLogout = new JButton("← Cerrar Sesión");
        btnLogout.setBackground(Color.RED);
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFont(new Font("Arial", Font.BOLD, 11));
        panelHeader.add(btnLogout, BorderLayout.WEST);

        JLabel lblWelcome = new JLabel("MÓDULO DE GESTIÓN DOCENTE - ACTAS DE CALIFICACIONES", JLabel.CENTER);
        lblWelcome.setForeground(Color.WHITE);
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 13));
        panelHeader.add(lblWelcome, BorderLayout.CENTER);
        add(panelHeader, BorderLayout.NORTH);

        // 4. Zona Central: ¡La Nueva Tabla Visual e Interactiva!
        JPanel panelCenter = new JPanel(new BorderLayout(5, 5));
        panelCenter.setBorder(BorderFactory.createTitledBorder("Planilla de Calificaciones Actuales (Doble clic para editar notas)"));

        String[] columns = {"ID Estudiante", "Nombre Completo", "Asignatura", "Componente 1 (Bimestre A)", "Componente 2 (Bimestre B)"};

        // Bloqueo de seguridad: solo permitimos editar las columnas de notas (índices 3 y 4)
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3 || column == 4;
            }
        };

        tableGrades = new JTable(tableModel);
        tableGrades.getTableHeader().setReorderingAllowed(false);
        tableGrades.setRowHeight(24); // Filas más altas y legibles

        // Rellenar la tabla aplicando el filtro por profesor
        refreshTableData();

        panelCenter.add(new JScrollPane(tableGrades), BorderLayout.CENTER);
        add(panelCenter, BorderLayout.CENTER);

        // 5. Zona Inferior: Botonera de acciones rápidas
        JPanel panelFooter = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 10));

        btnImportCSV = new JButton("Subir Acta desde Excel (CSV)");
        btnImportCSV.setFont(new Font("Arial", Font.BOLD, 12));
        btnImportCSV.setBackground(new Color(30, 144, 255)); // Azul llamativo
        btnImportCSV.setForeground(Color.WHITE);

        btnSaveChanges = new JButton("Guardar Cambios Manuales");
        btnSaveChanges.setFont(new Font("Arial", Font.BOLD, 12));
        btnSaveChanges.setBackground(new Color(34, 139, 34)); // Verde de confirmación seguro
        btnSaveChanges.setForeground(Color.WHITE);

        panelFooter.add(btnImportCSV);
        panelFooter.add(btnSaveChanges);
        add(panelFooter, BorderLayout.SOUTH);

        // --- MANEJO DE CONTROLADORES DE EVENTOS ---

        // Botón para importar masivamente el CSV externo
        btnImportCSV.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ejecutarCargaMasiva();
            }
        });

        // Botón para procesar y escribir las ediciones manuales en el archivo grades.csv
        btnSaveChanges.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guardarCambiosManuales();
            }
        });

        // Botón para cerrar sesión de forma segura
        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                loginFrame.setVisible(true);
            }
        });
    }

    /**
     * Limpia la rejilla y vuelve a volcar los datos actualizados filtrando exclusivamente
     * por las materias que corresponden al profesor que inició sesión.
     */
    private void refreshTableData() {
        tableModel.setRowCount(0);
        for (Student s : systemStudents) {
            if (s.getEnrollments() != null) {
                for (Enrollment enc : s.getEnrollments()) {
                    ec.edu.epn.sara.domain.Course curso = enc.getCourse();

                    // FILTRO DINÁMICO: Solo muestra la fila si la materia le pertenece al profesor conectado
                    if (curso.getProfessor() != null && curso.getProfessor().getId().equals(this.currentProfessorId)) {
                        Object[] row = {
                                s.getId(),
                                s.getFirstName() + " " + s.getLastName(),
                                curso.getCode(),
                                enc.getComponent1(),
                                enc.getComponent2()
                        };
                        tableModel.addRow(row);
                    }
                }
            }
        }
    }

    /**
     * Selector de archivos nativo para absorber los datos de un archivo externo.
     */
    private void ejecutarCargaMasiva() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccione el archivo CSV generado en el Bloc de Notas o Excel");
        int selection = fileChooser.showOpenDialog(this);

        if (selection == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            // Absorber y asociar
            persistence.importGradesFromProfessorCSV(selectedFile.getAbsolutePath(), systemStudents, availableCourses);

            // Forzar recarga visual inmediata en la pantalla del profesor
            refreshTableData();

            JOptionPane.showMessageDialog(this, "Acta procesada y volcada con éxito al visor del profesor.", "Operación Exitosa", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Lee lo que el profesor editó con sus dedos en las celdas y lo consolida en el archivo físico.
     * Blindado contra ClassCastException mediante el uso de String.valueOf().
     */
    private void guardarCambiosManuales() {
        // Asegurar que si el docente dejó una celda a medio escribir, se cierre y capture el valor
        if (tableGrades.isEditing()) {
            tableGrades.getCellEditor().stopCellEditing();
        }

        try {
            // Recorrer la tabla fila por fila
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String studentId = tableModel.getValueAt(i, 0).toString();
                String courseCode = tableModel.getValueAt(i, 2).toString();

                // SOLUCIÓN CRÍTICA: String.valueOf() tolera tanto objetos String como Double de forma segura
                double c1 = Double.parseDouble(String.valueOf(tableModel.getValueAt(i, 3)));
                double c2 = Double.parseDouble(String.valueOf(tableModel.getValueAt(i, 4)));

                // Buscar el objeto en las listas internas e inyectarle las notas modificadas
                for (Student s : systemStudents) {
                    if (s.getId().equals(studentId)) {
                        for (Enrollment enc : s.getEnrollments()) {
                            if (enc.getCourse().getCode().equals(courseCode)) {
                                enc.updateGrades(c1, c2); // Ejecuta el control de NotaInvalidaException
                            }
                        }
                    }
                }
            }

            // Guardar permanentemente la matriz limpia en grades.csv
            persistence.saveAllGrades(systemStudents);
            JOptionPane.showMessageDialog(this, "¡Planilla de notas guardada correctamente en grades.csv!", "Éxito", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Error: Ingrese exclusivamente formatos numéricos válidos en las casillas.", "Formato Inválido", JOptionPane.ERROR_MESSAGE);
        } catch (ec.edu.epn.sara.domain.NotaInvalidaException nie) {
            JOptionPane.showMessageDialog(this, nie.getMessage(), "Calificación Rechazada", JOptionPane.ERROR_MESSAGE);
            refreshTableData(); // Revierte visualmente la nota errónea
        }
    }
}