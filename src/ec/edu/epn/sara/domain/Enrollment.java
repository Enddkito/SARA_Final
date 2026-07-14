package ec.edu.epn.sara.domain;

public class Enrollment {
    private Course course;
    private double component1;
    private double component2;
    private double finalGrade;
    private String predictionStatus; // "Aprobado", "En Riesgo", "Reprobado"

    public Enrollment(Course course) {
        this.course = course;
        this.component1 = 0.0;
        this.component2 = 0.0;
        this.finalGrade = 0.0;
        this.predictionStatus = "Sin Registrar";
    }

    public Course getCourse() { return course; }

    public double getComponent1() { return component1; }
    public double getComponent2() { return component2; }
    public double getFinalGrade() { return finalGrade; }
    public String getPredictionStatus() { return predictionStatus; }

    /**
     * Modifica las notas blindando el sistema contra datos inválidos.
     * Aquí nacerá el manejo de excepciones personalizado.
     */
    /**
     * Modifica las notas blindando el sistema contra datos inválidos mediante excepciones personalizadas.
     */
    public void updateGrades(double c1, double c2) throws NotaInvalidaException {
        if (c1 < 0 || c1 > 10) {
            throw new NotaInvalidaException(c1);
        }
        if (c2 < 0 || c2 > 10) {
            throw new NotaInvalidaException(c2);
        }

        this.component1 = c1;
        this.component2 = c2;

        // Uso del polimorfismo en tiempo de ejecución:
        this.finalGrade = this.course.calculateFinalGrade(c1, c2);
    }

    public void setPredictionStatus(String status) {
        this.predictionStatus = status;
    }
}