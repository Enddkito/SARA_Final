package com.example.sara_ap.infrastructure;

import com.example.sara_ap.domain.Student;
import com.example.sara_ap.domain.Professor;
import com.example.sara_ap.domain.Course;
import com.example.sara_ap.domain.TheoryCourse;
import com.example.sara_ap.domain.LaboratoryCourse;
import com.example.sara_ap.domain.Enrollment;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVDataPersistence {

    private static final String PATH_PREFIX = "/com/example/sara_ap/";
    private static final String STUDENTS_FILE = "students.csv";
    private static final String PROFESSORS_FILE = "professors.csv";
    private static final String COURSES_FILE = "courses.csv";
    private static final String GRADES_FILE = "grades.csv";

    // 🔑 MÉTODO INTELIGENTE DE CONTINGENCIA PARA CLASSPATH
    private InputStream getCorrectStream(String fileName) {
        // Intento 1: Buscar directamente en la raíz (Como lo empaqueta tu pom.xml actual)
        InputStream is = getClass().getResourceAsStream("/" + fileName);
        if (is == null) {
            // Intento 2: Buscar con el prefijo del paquete clásico
            is = getClass().getResourceAsStream(PATH_PREFIX + fileName);
        }
        return is;
    }

    // Mapeador dinámico corregido para encontrar las rutas físicas de escritura
    private File getWriteableFile(String fileName) {
        URL resource = getClass().getResource("/" + fileName);
        if (resource != null) {
            return new File(resource.getFile());
        }
        resource = getClass().getResource(PATH_PREFIX + fileName);
        if (resource != null) {
            return new File(resource.getFile());
        }
        return new File("src/main/resources/" + fileName);
    }

    // 1️⃣ CARGAR ESTUDIANTES
    public List<Student> loadStudents() {
        List<Student> students = new ArrayList<>();
        try (InputStream is = getCorrectStream(STUDENTS_FILE)) {
            if (is == null) return students;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    String[] data = line.split(",");
                    if (data.length >= 5) {
                        students.add(new Student(
                                data[0].trim(), data[1].trim(), data[2].trim(), data[3].trim(), data[4].trim()
                        ));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar estudiantes: " + e.getMessage());
        }
        return students;
    }

    public void saveStudents(List<Student> students) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(getWriteableFile(STUDENTS_FILE)))) {
            for (Student s : students) {
                bw.write(s.getId() + "," + s.getFirstName() + "," + s.getLastName() + "," + s.getEmail() + "," + s.getPassword());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error al guardar estudiantes: " + e.getMessage());
        }
    }

    // 2️⃣ CARGAR PROFESORES
    public List<Professor> loadProfessors() {
        List<Professor> professors = new ArrayList<>();
        try (InputStream is = getCorrectStream(PROFESSORS_FILE)) {
            if (is == null) return professors;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    String[] data = line.split(",");
                    if (data.length >= 6) {
                        professors.add(new Professor(
                                data[0].trim(), data[1].trim(), data[2].trim(), data[3].trim(), data[4].trim(), data[5].trim()
                        ));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar profesores: " + e.getMessage());
        }
        return professors;
    }

    public void saveProfessors(List<Professor> professors) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(getWriteableFile(PROFESSORS_FILE)))) {
            for (Professor p : professors) {
                bw.write(p.getId() + "," + p.getFirstName() + "," + p.getLastName() + "," + p.getEmail() + "," + p.getPassword() + "," + p.getSpecialty());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error al guardar profesores: " + e.getMessage());
        }
    }

    // 3️⃣ CARGAR CURSOS
    public List<Course> loadCourses(List<Professor> professorsList) {
        List<Course> courses = new ArrayList<>();
        Map<String, Professor> professorMap = new HashMap<>();
        for (Professor p : professorsList) {
            professorMap.put(p.getId().trim(), p);
        }

        try (InputStream is = getCorrectStream(COURSES_FILE)) {
            if (is == null) {
                System.err.println("❌ Error crítico: No se encontró el archivo de cursos interno (" + COURSES_FILE + ")");
                return courses;
            }
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    String[] data = line.split(",");
                    if (data.length >= 3) {
                        String courseCode = data[0].trim();
                        String courseName = data[1].trim();
                        String professorId = data[2].trim();

                        Professor assignedProfessor = professorMap.get(professorId);

                        Course course;
                        if (courseCode.startsWith("POO") || courseCode.startsWith("ED")) {
                            course = new LaboratoryCourse(courseCode, courseName, assignedProfessor);
                        } else {
                            course = new TheoryCourse(courseCode, courseName, assignedProfessor);
                        }
                        courses.add(course);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar cursos: " + e.getMessage());
        }
        return courses;
    }

// 4️⃣ CARGAR CALIFICACIONES BIMESTRALES (Versión Adaptativa y Blindada)
    public void loadGradesIntoStudents(List<Student> students, List<Course> availableCourses) {
        try (InputStream is = getCorrectStream(GRADES_FILE)) {
            if (is == null) return;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                String line;
                int lineCount = 0;
                while ((line = br.readLine()) != null) {
                    lineCount++;
                    if (lineCount == 1 && line.toLowerCase().contains("id")) continue;
                    if (line.trim().isEmpty()) continue;

                    String[] data = line.split(",");
                    if (data.length >= 12) {
                        String studentId = data[0].trim();
                        String courseCode = data[1].trim();

                        double[] b1 = new double[]{
                                Double.parseDouble(data[2].trim()), Double.parseDouble(data[3].trim()),
                                Double.parseDouble(data[4].trim()), Double.parseDouble(data[5].trim()),
                                Double.parseDouble(data[6].trim())
                        };

                        double[] b2 = new double[]{
                                Double.parseDouble(data[7].trim()), Double.parseDouble(data[8].trim()),
                                Double.parseDouble(data[9].trim()), Double.parseDouble(data[10].trim()),
                                Double.parseDouble(data[11].trim())
                        };

                        // 🔑 BUSCAMOS AL ESTUDIANTE EN LA LISTA ACTUAL
                        Student targetStudent = null;
                        for (Student s : students) {
                            if (s.getId().trim().equals(studentId)) {
                                targetStudent = s;
                                break;
                            }
                        }

                        // 🔍 CONTINGENCIA: Si el alumno no estaba cargado en students.csv, lo creamos dinámicamente
                        if (targetStudent == null) {
                            targetStudent = new Student(studentId, "Estudiante", studentId, "estudiante@epn.edu.ec", "clave123");
                            students.add(targetStudent);
                        }

                        // Buscamos o creamos la inscripción (Enrollment) de la materia para este alumno
                        Enrollment targetEnc = null;
                        if (targetStudent.getEnrollments() != null) {
                            for (Enrollment e : targetStudent.getEnrollments()) {
                                if (e.getCourse().getCode().trim().equalsIgnoreCase(courseCode)) {
                                    targetEnc = e;
                                    break;
                                }
                            }
                        }

                        if (targetEnc == null) {
                            Course masterCourse = null;
                            for (Course c : availableCourses) {
                                if (c.getCode().trim().equalsIgnoreCase(courseCode)) {
                                    masterCourse = c;
                                    break;
                                }
                            }
                            // Si por alguna razón la materia no está en courses.csv, la creamos para que no explote
                            if (masterCourse == null) {
                                masterCourse = new TheoryCourse(courseCode, "Materia " + courseCode, null);
                                availableCourses.add(masterCourse);
                            }
                            targetEnc = new Enrollment(masterCourse);
                            targetStudent.addEnrollment(targetEnc);
                        }

                        // Inyectamos los dos arreglos desglosados de 5 aportes de forma limpia
                        targetEnc.updateBimestralGrades(b1, b2);
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error al procesar notas: " + e.getMessage());
        }
    }

    public void importGradesFromProfessorCSV(String filePath, List<Student> students, List<Course> availableCourses) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineCount = 0;
            while ((line = br.readLine()) != null) {
                lineCount++;
                if (lineCount == 1 && line.toLowerCase().contains("id")) continue;
                if (line.trim().isEmpty()) continue;

                String[] data = line.split(",");
                if (data.length >= 12) {
                    String studentId = data[0].trim();
                    String courseCode = data[1].trim();

                    double[] b1 = new double[]{
                            Double.parseDouble(data[2].trim()), Double.parseDouble(data[3].trim()),
                            Double.parseDouble(data[4].trim()), Double.parseDouble(data[5].trim()),
                            Double.parseDouble(data[6].trim())
                    };

                    double[] b2 = new double[]{
                            Double.parseDouble(data[7].trim()), Double.parseDouble(data[8].trim()),
                            Double.parseDouble(data[9].trim()), Double.parseDouble(data[10].trim()),
                            Double.parseDouble(data[11].trim())
                    };

                    for (Student s : students) {
                        if (s.getId().trim().equals(studentId)) {
                            Enrollment targetEnc = null;
                            if (s.getEnrollments() != null) {
                                for (Enrollment e : s.getEnrollments()) {
                                    if (e.getCourse().getCode().trim().equalsIgnoreCase(courseCode)) {
                                        targetEnc = e;
                                        break;
                                    }
                                }
                            }

                            if (targetEnc == null) {
                                Course masterCourse = null;
                                for (Course c : availableCourses) {
                                    if (c.getCode().trim().equalsIgnoreCase(courseCode)) {
                                        masterCourse = c;
                                        break;
                                    }
                                }
                                if (masterCourse == null) {
                                    masterCourse = new TheoryCourse(courseCode, "Materia " + courseCode, null);
                                    availableCourses.add(masterCourse);
                                }
                                targetEnc = new Enrollment(masterCourse);
                                s.addEnrollment(targetEnc);
                            }
                            targetEnc.updateBimestralGrades(b1, b2);
                        }
                    }
                }
            }
            saveAllGrades(students);
        } catch (Exception e) {
            System.err.println("Error en importación manual: " + e.getMessage());
        }
    }

    public void saveAllGrades(List<Student> students) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(getWriteableFile(GRADES_FILE)))) {
            bw.write("id_estudiante,codigo_materia,b1_virtual,b1_presencial,b1_examen,b1_talleres,b1_deberes,b2_virtual,b2_presencial,b2_examen,b2_talleres,b2_deberes");
            bw.newLine();
            for (Student s : students) {
                if (s.getEnrollments() != null) {
                    for (Enrollment e : s.getEnrollments()) {
                        double[] b1 = e.getNotasBimestre1();
                        double[] b2 = e.getNotasBimestre2();

                        bw.write(s.getId() + "," + e.getCourse().getCode() + "," +
                                b1[0] + "," + b1[1] + "," + b1[2] + "," + b1[3] + "," + b1[4] + "," +
                                b2[0] + "," + b2[1] + "," + b2[2] + "," + b2[3] + "," + b2[4]);
                        bw.newLine();
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al guardar calificaciones: " + e.getMessage());
        }
    }

    public List<Student> loadFullSystemData(List<Course> outAvailableCourses) {
        List<Professor> professors = loadProfessors();
        List<Course> loadedCourses = loadCourses(professors);
        outAvailableCourses.clear();
        outAvailableCourses.addAll(loadedCourses);

        List<Student> students = loadStudents();
        loadGradesIntoStudents(students, outAvailableCourses);
        return students;
    }
}