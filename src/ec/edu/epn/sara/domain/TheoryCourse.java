package ec.edu.epn.sara.domain;

public class TheoryCourse extends Course {

    public TheoryCourse(String code, String name) {
        super(code, name);
    }

    /**
     * Implementación polimórfica para materias teóricas (70% Bimestre 1, 30% Bimestre 2)
     * o según la distribución paramétrica institucional.
     */
    @Override
    public double calculateFinalGrade(double component1, double component2) {
        return (component1 * 0.70) + (component2 * 0.30);
    }
}