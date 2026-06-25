package com.example.sara_ap.domain;

/**
 * Clase que representa a la entidad Profesor dentro del sistema SARA.
 * Especializa a la clase base User con atributos específicos de docencia.
 */
public class Professor extends User {
    private String specialty; // Área académica del docente

    public Professor(String id, String firstName, String lastName, String email, String password, String specialty) {
        super(id, firstName, lastName, email, password);
        this.specialty = specialty;
    }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }

    /**
     * Métodos de conveniencia para obtener el ID y el nombre completo
     * necesarios para la vinculación con los cursos y persistencia CSV.
     */
    public String getId() {
        return super.getId(); // Retorna el ID heredado de la clase User
    }

    public String getName() {
        return super.getFirstName() + " " + super.getLastName(); // Construye el nombre completo desde User
    }

    /**
     * Sobreescritura polimórfica del método abstracto de la clase padre.
     */
    @Override
    public String getRole() {
        return "Professor";
    }

    /**
     * Representación en formato de texto del profesor.
     */
    @Override
    public String toString() {
        return getName() + " (ID: " + getId() + ")";
    }
}