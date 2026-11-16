package com.example.oftbackend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "pacientes")
public class PacienteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String rut;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellidos;

    private Integer edad;
    private String prevision;
    private String telefono;
    private String correo;

    public PacienteEntity() {}

    public PacienteEntity(Long id, String rut, String nombre, String apellidos, Integer edad, String prevision, String telefono, String correo) {
        this.id = id;
        this.rut = rut;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.edad = edad;
        this.prevision = prevision;
        this.telefono = telefono;
        this.correo = correo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRut() { return rut; }
    public void setRut(String rut) { this.rut = rut; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public Integer getEdad() { return edad; }
    public void setEdad(Integer edad) { this.edad = edad; }
    public String getPrevision() { return prevision; }
    public void setPrevision(String prevision) { this.prevision = prevision; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
}
