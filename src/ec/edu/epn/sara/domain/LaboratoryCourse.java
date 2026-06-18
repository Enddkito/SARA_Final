package ec.edu.epn.sara.domain;

public class LaboratoryCourse extends Course {

    public LaboratoryCourse(String code, String name) {
        super(code, name);
    }

    /**
     * Implementación polimórfica para laboratorios prácticos (60% / 40%).
     */
    @Override
    public double calculateFinalGrade(double component1, double component2) {
        return (component1 * 0.60) + (component2 * 0.40);
    }
}