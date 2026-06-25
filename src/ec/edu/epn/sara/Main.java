package ec.edu.epn.sara;

import ec.edu.epn.sara.view.WelcomeFrame;
import ec.edu.epn.sara.infrastructure.CSVDataPersistence;
import ec.edu.epn.sara.domain.Student;
import ec.edu.epn.sara.domain.Professor;
import ec.edu.epn.sara.domain.Course;
import javax.swing.SwingUtilities;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static List<Student> sistemaEstudiantes = new ArrayList<>();
    public static List<Course> sistemaCursos = new ArrayList<>();

    public static void main(String[] args) {

        // --- BLOQUE DE SEGURIDAD Y SEMBRADO DE DATOS (SEEDING) ---
        CSVDataPersistence persistence = new CSVDataPersistence();

        File studentFile = new File("students.csv");
        if (!studentFile.exists()) {
            List<Student> alumnosIniciales = new ArrayList<>();
            alumnosIniciales.add(new Student("1755555555", "Emily", "Lara", "emily.lara@epn.edu.ec", "progresando123"));
            alumnosIniciales.add(new Student("1766666666", "Alejandro", "Perez", "alejandro@epn.edu.ec", "segura456"));
            persistence.saveStudents(alumnosIniciales);
        }

        File professorFile = new File("professors.csv");
        if (!professorFile.exists()) {
            List<Professor> profesoresIniciales = new ArrayList<>();
            profesoresIniciales.add(new Professor("1711111111", "Ing", "Martinez", "profesor@epn.edu.ec", "profesor123", "Sistemas"));
            profesoresIniciales.add(new Professor("1755555555", "Alejandro", "Larrea", "alarrea@epn.edu.ec", "clave123", "Sistemas"));
            profesoresIniciales.add(new Professor("1744444444", "Carmen", "Viteri", "cviteri@epn.edu.ec", "clave456", "Física"));
            persistence.saveProfessors(profesoresIniciales);
        }

        File courseFile = new File("courses.csv");
        if (!courseFile.exists()) {
            try (java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.FileWriter(courseFile))) {
                bw.write("POO-01,Programación Orientada a Objetos,1755555555"); bw.newLine();
                bw.write("ED-01,Estructuras de Datos,1755555555"); bw.newLine();
                bw.write("FIS-01,Física Clásica,1711111111"); bw.newLine();
            } catch (java.io.IOException e) {
                // Manejo silencioso
            }
        }
        // ---------------------------------------------------------

        // Ejecutar carga relacional estructurada de los tres archivos CSV
        sistemaEstudiantes = persistence.loadFullSystemData(sistemaCursos);

        // Hilo seguro para arrancar la GUI de SARA
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                WelcomeFrame bienvenida = new WelcomeFrame();
                bienvenida.setVisible(true);
            }
        });
    }
}