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

    /**
     * Carga un archivo CSV externo provisto por un profesor y actualiza las notas de los estudiantes.
     * Formato esperado del CSV: id_estudiante,codigo_materia,nota_componente1,nota_componente2
     */
    public void importGradesFromProfessorCSV(String filePath, java.util.List<Student> systemStudents, java.util.List<ec.edu.epn.sara.domain.Course> availableCourses) {
        File file = new File(filePath);
        if (!file.exists()) {
            System.err.println("Error: El archivo de calificaciones no existe en la ruta especificada.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int lineCount = 0;

            while ((line = br.readLine()) != null) {
                lineCount++;
                // Omitir la cabecera si el archivo Excel la incluye (ej: "ID,Materia,C1,C2")
                if (lineCount == 1 && line.toLowerCase().contains("id")) {
                    continue;
                }

                String[] data = line.split(",");
                if (data.length == 4) {
                    String studentId = data[0].trim();
                    String courseCode = data[1].trim();

                    try {
                        double c1 = Double.parseDouble(data[2].trim());
                        double c2 = Double.parseDouble(data[3].trim());

                        // 1. Buscar al estudiante en los registros del sistema
                        Student targetStudent = null;
                        for (Student s : systemStudents) {
                            if (s.getId().equals(studentId)) {
                                targetStudent = s;
                                break;
                            }
                        }

                        if (targetStudent != null) {
                            // 2. Buscar si el estudiante ya tiene esa materia asignada (Enrollment)
                            ec.edu.epn.sara.domain.Enrollment targetEnrollment = null;
                            for (ec.edu.epn.sara.domain.Enrollment e : targetStudent.getEnrollments()) {
                                if (e.getCourse().getCode().equals(courseCode)) {
                                    targetEnrollment = e;
                                    break;
                                }
                            }

                            // 3. Si no está inscrito, buscamos el curso y lo inscribimos dinámicamente
                            if (targetEnrollment == null) {
                                for (ec.edu.epn.sara.domain.Course c : availableCourses) {
                                    if (c.getCode().equals(courseCode)) {
                                        targetEnrollment = new ec.edu.epn.sara.domain.Enrollment(c);
                                        targetStudent.addEnrollment(targetEnrollment);
                                        break;
                                    }
                                }
                            }

                            // 4. Actualizar las calificaciones usando tu excepción personalizada
                            if (targetEnrollment != null) {
                                targetEnrollment.updateGrades(c1, c2);
                                System.out.println("Línea " + lineCount + ": Notas actualizadas para " + targetStudent.getFirstName() + " en " + courseCode);
                            }
                        } else {
                            System.out.println("Línea " + lineCount + ": Advertencia - Estudiante con ID " + studentId + " no existe en el sistema.");
                        }

                    } catch (NumberFormatException nfe) {
                        System.err.println("Línea " + lineCount + ": Error de formato numérico en las notas.");
                    } catch (ec.edu.epn.sara.domain.NotaInvalidaException nie) {
                        // ¡Aquí atrapamos tu excepción si el Excel viene con notas como 11.5 o -1!
                        System.err.println("Línea " + lineCount + ": " + nie.getMessage());
                    }
                }
            }

            // Al finalizar la carga, guardamos el estado maestro actualizado de los estudiantes
            saveStudents(systemStudents);
            System.out.println("\n=== Carga masiva finalizada. Base de datos actualizada ===");

        } catch (IOException e) {
            System.err.println("Error crítico al procesar la carga masiva: " + e.getMessage());
        }
    }

}