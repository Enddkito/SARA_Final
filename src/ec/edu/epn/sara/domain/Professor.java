package ec.edu.epn.sara.domain;

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
     * Sobreescritura polimórfica del método abstracto de la clase padre.
     */
    @Override
    public String getRole() {
        return "Professor";
    }
}