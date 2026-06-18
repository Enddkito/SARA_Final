package ec.edu.epn.sara.view;

import ec.edu.epn.sara.domain.Student;
import ec.edu.epn.sara.infrastructure.CSVDataPersistence;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel de control principal para el rol del Profesor.
 * Permite realizar la carga masiva de calificaciones mediante archivos CSV externos.
 */
public class ProfessorDashboard extends JFrame {

    private JButton btnImportCSV;
    private JTextArea txtLog;
    private CSVDataPersistence persistence;

    // Atributos agregados para la navegación de regreso
    private JFrame loginFrame;
    private JButton btnLogout;

    /**
     * Constructor modificado que recibe la ventana de Login para permitir la reversibilidad.
     */
    public ProfessorDashboard(JFrame loginWindow) {
        this.loginFrame = loginWindow;
        this.persistence = new CSVDataPersistence();

        setTitle("SARA - Panel de Control del Docente");
        setSize(550, 420); // Ajustado a 420 de alto para dar espacio cómodo a los componentes
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));

        // 1. Encabezado estructurado con BorderLayout para alinear la flecha y el título
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(new Color(75, 0, 130)); // Morado obscuro elegante para profesores
        panelHeader.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Botón de regreso / cierre de sesión intuitivo
        btnLogout = new JButton("← Cerrar Sesión");
        btnLogout.setBackground(Color.RED);
        btnLogout.setForeground(Color.WHITE);
        panelHeader.add(btnLogout, BorderLayout.WEST);

        JLabel lblWelcome = new JLabel("MÓDULO DE GESTIÓN DOCENTE - SARA", JLabel.CENTER);
        lblWelcome.setForeground(Color.WHITE);
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 14));
        panelHeader.add(lblWelcome, BorderLayout.CENTER);

        add(panelHeader, BorderLayout.NORTH);

        // 2. Zona central: Consola de reportes (Bitácora)
        JPanel panelCenter = new JPanel(new BorderLayout(5, 5));
        panelCenter.setBorder(BorderFactory.createTitledBorder("Bitácora de Operaciones Académicas"));
        txtLog = new JTextArea();
        txtLog.setEditable(false);
        txtLog.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtLog.setBackground(new Color(245, 245, 245));
        panelCenter.add(new JScrollPane(txtLog), BorderLayout.CENTER);
        add(panelCenter, BorderLayout.CENTER);

        // 3. Zona inferior: Botón de importación CSV
        JPanel panelFooter = new JPanel();
        btnImportCSV = new JButton("Subir Acta de Calificaciones (CSV)");
        btnImportCSV.setFont(new Font("Arial", Font.BOLD, 12));
        btnImportCSV.setBackground(new Color(30, 144, 255));
        btnImportCSV.setForeground(Color.WHITE);
        panelFooter.add(btnImportCSV);
        add(panelFooter, BorderLayout.SOUTH);

        // 4. Controladores de Eventos (ActionListeners)

        // Evento para procesar la carga masiva de Excel
        btnImportCSV.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ejecutarCargaMasiva();
            }
        });

        // Evento para salir y regresar al Login limpio sin romper el hilo del programa
        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Cierra el Dashboard actual liberando memoria
                loginFrame.setVisible(true); // Redirige al Login original que permanecía oculto
            }
        });
    }

    /**
     * Abre el selector nativo de archivos para cargar el CSV generado por el docente.
     */
    private void ejecutarCargaMasiva() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccione el archivo CSV exportado desde Excel");

        int selection = fileChooser.showOpenDialog(this);
        if (selection == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            txtLog.setText("Iniciando procesamiento de: " + selectedFile.getName() + "\n");
            txtLog.append("--------------------------------------------------\n");

            // Cargar estudiantes actuales del sistema
            List<Student> alumnosSistema = persistence.loadStudents();

            // Invocar al motor de infraestructura con los parámetros correspondientes
            persistence.importGradesFromProfessorCSV(selectedFile.getAbsolutePath(), alumnosSistema, new ArrayList<>());

            txtLog.append("Proceso finalizado. Registros actualizados en students.csv.\n");
            JOptionPane.showMessageDialog(this,
                    "Archivo CSV procesado. Revise la bitácora para verificar si hubo notas inválidas rebotadas.",
                    "Carga Completa",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
}