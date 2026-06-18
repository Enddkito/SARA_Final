package ec.edu.epn.sara;

import ec.edu.epn.sara.domain.Student;
import ec.edu.epn.sara.infrastructure.CSVDataPersistence;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== INICIANDO PRUEBA DEL SISTEMA SARA ===");

        // 1. Instanciar el motor de persistencia
        CSVDataPersistence persistence = new CSVDataPersistence();

        // 2. Crear estudiantes de prueba
        List<Student> estudiantesNuevos = new ArrayList<>();
        estudiantesNuevos.add(new Student("1755555555", "Emily", "Lara", "emily.natsha.lara@gmail.com", "progresando123"));
        estudiantesNuevos.add(new Student("1766666666", "Alejandro", "Perez", "alejandro@epn.edu.ec", "segura456"));

        // 3. Guardar en el disco duro
        System.out.println("\nIntentando guardar estudiantes en CSV...");
        persistence.saveStudents(estudiantesNuevos);

        // 4. Intentar recuperarlos leyendo el archivo
        System.out.println("\nIntentando leer estudiantes desde el CSV...");
        List<Student> estudiantesCargados = persistence.loadStudents();

        // 5. Mostrar en consola lo que se leyó del archivo
        System.out.println("\n=== RESULTADOS DE LA LECTURA ===");
        for (Student s : estudiantesCargados) {
            System.out.println("Estudiante Detectado: " + s.getFirstName() + " " + s.getLastName() + " | Email: " + s.getEmail());
        }
    }
}