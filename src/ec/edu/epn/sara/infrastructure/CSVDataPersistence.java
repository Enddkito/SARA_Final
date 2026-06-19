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
    private static final String GRADES_FILE = "grades.csv";

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
                // Formato completo de 6 campos requerido por tu dominio: id,firstName,lastName,email,password,specialty
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
     * Carga la lista de profesores autorizados desde el archivo professors.csv.
     */
    public List<Professor> loadProfessors() {
        List<Professor> professors = new ArrayList<>();
        File file = new File(PROFESSOR_FILE);

        if (!file.exists()) {
            System.err.println("Advertencia: El archivo professors.csv no existe. Creando uno vacío.");
            return professors;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 5) {
                    String id = data[0].trim();
                    String firstName = data[1].trim();
                    String lastName = data[2].trim();
                    String email = data[3].trim();
                    String password = data[4].trim();

                    // Si el archivo trae el sexto campo lo lee, si no, le pone "Docente EPN" por defecto
                    String specialty = (data.length >= 6) ? data[5].trim() : "Docente EPN";

                    // SOLUCIÓN: Inyectamos los 6 argumentos exactos que espera tu modelo
                    Professor prof = new Professor(id, firstName, lastName, email, password, specialty);
                    professors.add(prof);
                }
            }
        } catch (IOException e) {
            System.err.println("Error de lectura en professors.csv: " + e.getMessage());
        }
        return professors;
    }

    /**
     * Carga un archivo CSV externo provisto por un profesor y actualiza las notas de los estudiantes.
     * Formato esperado del CSV: id_estudiante,codigo_materia,nota_componente1,nota_componente2
     */
    public void importGradesFromProfessorCSV(String filePath, List<Student> systemStudents, List<ec.edu.epn.sara.domain.Course> availableCourses) {
        File file = new File(filePath);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int lineCount = 0;

            while ((line = br.readLine()) != null) {
                lineCount++;
                if (lineCount == 1 && (line.toLowerCase().contains("id") || line.toLowerCase().contains("estudiante"))) {
                    continue;
                }

                // SOPORTE DOBLE: Divide por coma o por punto y coma según cómo guardó Excel
                String[] data = line.contains(";") ? line.split(";") : line.split(",");

                if (data.length >= 4) {
                    String studentId = data[0].trim();
                    String courseCode = data[1].trim();
                    double c1 = Double.parseDouble(data[2].trim());
                    double c2 = Double.parseDouble(data[3].trim());

                    // 1. Buscar al estudiante
                    Student targetStudent = null;
                    for (Student s : systemStudents) {
                        if (s.getId().equals(studentId)) {
                            targetStudent = s;
                            break;
                        }
                    }

                    if (targetStudent != null) {
                        // 2. Buscar si ya tiene la matrícula hecha
                        ec.edu.epn.sara.domain.Enrollment targetEnrollment = null;
                        for (ec.edu.epn.sara.domain.Enrollment e : targetStudent.getEnrollments()) {
                            if (e.getCourse().getCode().equals(courseCode)) {
                                targetEnrollment = e;
                                break;
                            }
                        }

                        // 3. SOLUCIÓN: Si no está matriculado, creamos el curso y lo inscribimos en caliente
                        if (targetEnrollment == null) {
                            ec.edu.epn.sara.domain.Course newCourse = null;
                            for (ec.edu.epn.sara.domain.Course c : availableCourses) {
                                if (c.getCode().equals(courseCode)) {
                                    newCourse = c;
                                    break;
                                }
                            }
                            // Si el curso no existe en el catálogo global, lo instanciamos como un curso teórico
                            if (newCourse == null) {
                                newCourse = new ec.edu.epn.sara.domain.TheoryCourse(courseCode, "Materia " + courseCode);
                                availableCourses.add(newCourse);
                            }

                            targetEnrollment = new ec.edu.epn.sara.domain.Enrollment(newCourse);
                            targetStudent.addEnrollment(targetEnrollment);
                        }

                        // 4. Inyectar las calificaciones
                        targetEnrollment.updateGrades(c1, c2);
                    }
                }
            }

            // Guardar el estado consolidado de las notas en el archivo independiente
            saveAllGrades(systemStudents);

        } catch (Exception e) {
            System.err.println("Error procesando CSV: " + e.getMessage());
        }
    }

    /**
     * Guarda todas las calificaciones de la aplicación en el archivo maestro grades.csv.
     */
    public void saveAllGrades(List<Student> students) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(GRADES_FILE))) {
            // Cabecera informativa para el CSV
            bw.write("id_estudiante,codigo_materia,nota_componente1,nota_componente2");
            bw.newLine();

            for (Student s : students) {
                if (s.getEnrollments() != null) {
                    for (ec.edu.epn.sara.domain.Enrollment e : s.getEnrollments()) {
                        bw.write(s.getId() + "," + e.getCourse().getCode() + "," +
                                e.getComponent1() + "," + e.getComponent2());
                        bw.newLine();
                    }
                }
            }
            System.out.println("Sistema: Archivo maestro grades.csv actualizado con éxito.");
        } catch (IOException e) {
            System.err.println("Error de Infraestructura al guardar grades.csv: " + e.getMessage());
        }
    }

    /**
     * Carga las notas desde grades.csv y las vincula a los estudiantes en memoria.
     */
    public void loadGradesIntoStudents(List<Student> students, List<ec.edu.epn.sara.domain.Course> availableCourses) {
        File file = new File(GRADES_FILE);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(GRADES_FILE))) {
            String line;
            int lineCount = 0;
            while ((line = br.readLine()) != null) {
                lineCount++;
                if (lineCount == 1 && line.toLowerCase().contains("id")) continue; // Omitir cabecera

                String[] data = line.split(",");
                if (data.length == 4) {
                    String studentId = data[0].trim();
                    String courseCode = data[1].trim();
                    double c1 = Double.parseDouble(data[2].trim());
                    double c2 = Double.parseDouble(data[3].trim());

                    // Buscar estudiante y vincular la nota
                    for (Student s : students) {
                        if (s.getId().equals(studentId)) {
                            // Buscar si ya tiene el enrollment
                            ec.edu.epn.sara.domain.Enrollment targetEnc = null;
                            for (ec.edu.epn.sara.domain.Enrollment e : s.getEnrollments()) {
                                if (e.getCourse().getCode().equals(courseCode)) {
                                    targetEnc = e;
                                    break;
                                }
                            }
                            // Si no existe, crear el curso y el enrollment dinámicamente
                            if (targetEnc == null) {
                                ec.edu.epn.sara.domain.Course course = null;
                                for (ec.edu.epn.sara.domain.Course c : availableCourses) {
                                    if (c.getCode().equals(courseCode)) {
                                        course = c;
                                        break;
                                    }
                                }
                                if (course == null) {
                                    course = new ec.edu.epn.sara.domain.TheoryCourse(courseCode, "Materia " + courseCode);
                                    availableCourses.add(course);
                                }
                                targetEnc = new ec.edu.epn.sara.domain.Enrollment(course);
                                s.addEnrollment(targetEnc);
                            }
                            try {
                                targetEnc.updateGrades(c1, c2);
                            } catch (Exception ex) {
                                // Ignorar registros corruptos en la carga inicial
                            }
                        }
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error al cargar las notas iniciales: " + e.getMessage());
        }
    }
}