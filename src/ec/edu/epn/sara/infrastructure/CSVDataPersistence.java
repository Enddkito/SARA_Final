package ec.edu.epn.sara.infrastructure;

import ec.edu.epn.sara.domain.Student;
import ec.edu.epn.sara.domain.Professor;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase responsable de la persistencia de datos del sistema SARA mediante archivos CSV.
 * Aplica el Principio de Responsabilidad Única (SRP).
 */
public class CSVDataPersistence {

    private static final String STUDENT_FILE = "students.csv";
    private static final String PROFESSOR_FILE = "professors.csv";

    /**
     * Guarda la lista de estudiantes en un archivo CSV.
     */
    public void saveStudents(List<Student> students) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(STUDENT_FILE))) {
            for (Student s : students) {
                // Formato: id,firstName,lastName,email,password
                bw.write(s.getId() + "," + s.getFirstName() + "," + s.getLastName() + "," +
                        s.getEmail() + "," + s.getPassword());
                bw.newLine();
            }
            System.out.println("Sistema: Datos de estudiantes respaldados con éxito en CSV.");
        } catch (IOException e) {
            System.err.println("Error crítico de Infraestructura: No se pudieron guardar los estudiantes. " + e.getMessage());
        }
    }

    /**
     * Lee y carga los estudiantes desde el archivo CSV.
     */
    public List<Student> loadStudents() {
        List<Student> students = new ArrayList<>();
        File file = new File(STUDENT_FILE);

        if (!file.exists()) return students; // Si el archivo no existe, retorna una lista vacía

        try (BufferedReader br = new BufferedReader(new FileReader(STUDENT_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 5) {
                    Student student = new Student(data[0], data[1], data[2], data[3], data[4]);
                    students.add(student);
                }
            }
        } catch (IOException e) {
            System.err.println("Error crítico de Infraestructura: Fallo al leer el archivo de estudiantes. " + e.getMessage());
        }
        return students;
    }

    /**
     * Guarda la lista de profesores en un archivo CSV.
     */
    public void saveProfessors(List<Professor> professors) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PROFESSOR_FILE))) {
            for (Professor p : professors) {
                // Formato: id,firstName,lastName,email,password,specialty
                bw.write(p.getId() + "," + p.getFirstName() + "," + p.getLastName() + "," +
                        p.getEmail() + "," + p.getPassword() + "," + p.getSpecialty());
                bw.newLine();
            }
            System.out.println("Sistema: Datos de profesores respaldados con éxito en CSV.");
        } catch (IOException e) {
            System.err.println("Error crítico de Infraestructura: No se pudieron guardar los profesores. " + e.getMessage());
        }
    }
}