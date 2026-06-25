package ec.edu.epn.sara.domain;

public class LaboratoryCourse extends Course {

    public LaboratoryCourse(String code, String name, Professor professor) {
        super(code, name, professor); // Pasamos los 3 datos obligatorios a Course
    }

    /**
     * Implementación polimórfica para laboratorios prácticos (60% / 40%)
     */
    @Override
    public double calculateFinalGrade(double component1, double component2) {
        return (component1 * 0.60) + (component2 * 0.40);
    }
}