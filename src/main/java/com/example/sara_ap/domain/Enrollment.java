package com.example.sara_ap.domain;

public class Enrollment {
    private Student student;
    private Course course;

    // Arreglos de 5 posiciones: [0]=P. Virtual, [1]=P. Presencial, [2]=Examen, [3]=Talleres, [4]=Deberes
    private double[] notasBimestre1 = new double[5];
    private double[] notasBimestre2 = new double[5];

    public Enrollment(Course course) {
        this.course = course;
    }

    public Enrollment(Student student, Course course) {
        this.student = student;
        this.course = course;
    }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    // Métodos para asignar y obtener arreglos completos
    public double[] getNotasBimestre1() { return notasBimestre1; }
    public void setNotasBimestre1(double[] notas) { this.notasBimestre1 = notas; }

    public double[] getNotasBimestre2() { return notasBimestre2; }
    public void setNotasBimestre2(double[] notas) { this.notasBimestre2 = notas; }

// 🔑 Método centralizado para actualizar los 10 aportes de golpe desde la persistencia
    public void updateBimestralGrades(double[] notasB1, double[] notasB2) {
        this.notasBimestre1 = notasB1;
        this.notasBimestre2 = notasB2;

    }
    // Calcula la nota total del Bimestre 1 sumando sus 5 aportes
    public double getComponent1() {
        double sum = 0;
        for (double n : notasBimestre1) sum += n;
        return sum;
    }

    // Calcula la nota total del Bimestre 2 sumando sus 5 aportes
    public double getComponent2() {
        double sum = 0;
        for (double n : notasBimestre2) sum += n;
        return sum;
    }
}