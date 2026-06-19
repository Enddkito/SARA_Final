package ec.edu.epn.sara;

import ec.edu.epn.sara.view.WelcomeFrame;
import ec.edu.epn.sara.infrastructure.CSVDataPersistence;
import ec.edu.epn.sara.domain.Student;
import ec.edu.epn.sara.domain.Professor;
import javax.swing.SwingUtilities;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

//        // --- BLOQUE DE SEGURIDAD Y SEMBRADO DE DATOS (SEEDING) ---
//        CSVDataPersistence persistence = new CSVDataPersistence();
//
//        // 1. Verificar y autogenerar students.csv si no existe
//        File studentFile = new File("students.csv");
//        if (!studentFile.exists()) {
//            List<Student> alumnosIniciales = new ArrayList<>();
//            // Formato: id, firstName, lastName, email, password
//            alumnosIniciales.add(new Student("1755555555", "Emily", "Lara", "emily.natsha.lara@gmail.com", "progresando123"));
//            alumnosIniciales.add(new Student("1766666666", "Alejandro", "Perez", "alejandro@epn.edu.ec", "segura456"));
//            persistence.saveStudents(alumnosIniciales);
//            System.out.println("Sistema: Archivo students.csv creado automáticamente con usuarios de prueba.");
//        }
//
//        // 2. Verificar y autogenerar professors.csv si no existe
//        File professorFile = new File("professors.csv");
//        if (!professorFile.exists()) {
//            List<Professor> profesoresIniciales = new ArrayList<>();
//            // Formato completo de 6 campos: id, firstName, lastName, email, password, specialty
//            profesoresIniciales.add(new Professor("1711111111", "Ing", "Martinez", "profesor@epn.edu.ec", "profesor123", "Sistemas"));
//            persistence.saveProfessors(profesoresIniciales);
//            System.out.println("Sistema: Archivo professors.csv creado automáticamente con usuarios de prueba.");
//        }
        // ---------------------------------------------------------

        // Hilo seguro para ejecutar la interfaz gráfica
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                WelcomeFrame bienvenida = new WelcomeFrame();
                bienvenida.setVisible(true); // Arranca con la ventana de bienvenida
            }
        });
    }
}