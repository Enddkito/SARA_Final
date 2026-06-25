package ec.edu.epn.sara.infrastructure;

import ec.edu.epn.sara.domain.Student;
import ec.edu.epn.sara.domain.Professor;
import ec.edu.epn.sara.domain.Course;
import ec.edu.epn.sara.domain.TheoryCourse;
import ec.edu.epn.sara.domain.LaboratoryCourse;
import ec.edu.epn.sara.domain.Enrollment;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVDataPersistence {

    private static final String STUDENTS_FILE = "students.csv";
    private static final String PROFESSORS_FILE = "professors.csv";
    private static final String COURSES_FILE = "courses.csv";
    private static final String GRADES_FILE = "grades.csv";

    public List<Student> loadStudents() {
        List<Student> students = new ArrayList<>();
        File file = new File(STUDENTS_FILE);
        if (!file.exists()) return students;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = line.split(",");
                if (data.length >= 5) {
                    students.add(new Student(
                            data[0].trim(),
                            data[1].trim(),
                            data[2].trim(),
                            data[3].trim(),
                            data[4].trim()
                    ));
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar estudiantes: " + e.getMessage());
        }
        return students;
    }

    public void saveStudents(List<Student> students) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(STUDENTS_FILE))) {
            for (Student s : students) {
                bw.write(s.getId() + "," + s.getFirstName() + "," + s.getLastName() + "," + s.getEmail() + "," + s.getPassword());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error al guardar estudiantes: " + e.getMessage());
        }
    }

    public List<Professor> loadProfessors() {
        List<Professor> professors = new ArrayList<>();
        File file = new File(PROFESSORS_FILE);
        if (!file.exists()) return professors;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = line.split(",");
                if (data.length >= 6) {
                    professors.add(new Professor(
                            data[0].trim(),
                            data[1].trim(),
                            data[2].trim(),
                            data[3].trim(),
                            data[4].trim(),
                            data[5].trim()
                    ));
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar profesores: " + e.getMessage());
        }
        return professors;
    }

    public void saveProfessors(List<Professor> professors) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PROFESSORS_FILE))) {
            for (Professor p : professors) {
                bw.write(p.getId() + "," + p.getFirstName() + "," + p.getLastName() + "," + p.getEmail() + "," + p.getPassword() + "," + p.getSpecialty());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error al guardar profesores: " + e.getMessage());
        }
    }

    public List<Course> loadCourses(List<Professor> professorsList) {
        List<Course> courses = new ArrayList<>();
        File file = new File(COURSES_FILE);
        if (!file.exists()) return courses;

        Map<String, Professor> professorMap = new HashMap<>();
        for (Professor p : professorsList) {
            professorMap.put(p.getId().trim(), p);
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
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
        } catch (IOException e) {
            System.err.println("Error al cargar cursos: " + e.getMessage());
        }
        return courses;
    }

    public void loadGradesIntoStudents(List<Student> students, List<Course> availableCourses) {
        File file = new File(GRADES_FILE);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int lineCount = 0;
            while ((line = br.readLine()) != null) {
                lineCount++;
                if (lineCount == 1 && line.toLowerCase().contains("id")) continue;
                if (line.trim().isEmpty()) continue;

                String[] data = line.split(",");
                if (data.length >= 4) {
                    String studentId = data[0].trim();
                    String courseCode = data[1].trim();
                    double c1 = Double.parseDouble(data[2].trim());
                    double c2 = Double.parseDouble(data[3].trim());

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

                            try {
                                targetEnc.updateGrades(c1, c2);
                            } catch (Exception ex) {
                                // Ignorar registros con notas fuera de rango en la carga inicial
                            }
                        }
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
                if (data.length >= 4) {
                    String studentId = data[0].trim();
                    String courseCode = data[1].trim();
                    double c1 = Double.parseDouble(data[2].trim());
                    double c2 = Double.parseDouble(data[3].trim());

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
                            targetEnc.updateGrades(c1, c2);
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
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(GRADES_FILE))) {
            bw.write("id_estudiante,codigo_materia,nota_componente1,nota_componente2");
            bw.newLine();
            for (Student s : students) {
                if (s.getEnrollments() != null) {
                    for (Enrollment e : s.getEnrollments()) {
                        bw.write(s.getId() + "," + e.getCourse().getCode() + "," + e.getComponent1() + "," + e.getComponent2());
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