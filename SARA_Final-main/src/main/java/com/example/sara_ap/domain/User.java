package com.example.sara_ap.domain;

/**
 * Clase abstracta que representa la base de cualquier usuario en el sistema SARA.
 * Implementa el encapsulamiento de los atributos de identidad y credenciales.
 */
public abstract class User {
    // Atributos privados para cumplir con el pilar de Encapsulamiento
    private String id; // Puede ser la cédula o el código único
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    // Constructor completo
    public User(String id, String firstName, String lastName, String email, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    // Métodos Getters y Setters obligatorios
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    /**
     * Método abstracto para obtener el rol del usuario en tiempo de ejecución.
     * Obliga a las subclases a identificarse, clave para el Polimorfismo.
     */
    public abstract String getRole();
}