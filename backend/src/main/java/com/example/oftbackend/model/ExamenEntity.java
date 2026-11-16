package com.example.oftbackend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "examenes")
public class ExamenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long pacienteId;

    @Column(nullable = false)
    private String pacienteNombre;

    @Column(nullable = false)
    private String tipoExamen;

    private String ojoEvaluado;
    private String fechaExamen;
    private String estado;
    private String comentarios;
    private String sucursal;
    private Boolean sincronizado = true;

    public ExamenEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPacienteId() { return pacienteId; }
    public void setPacienteId(Long pacienteId) { this.pacienteId = pacienteId; }
    public String getPacienteNombre() { return pacienteNombre; }
    public void setPacienteNombre(String pacienteNombre) { this.pacienteNombre = pacienteNombre; }
    public String getTipoExamen() { return tipoExamen; }
    public void setTipoExamen(String tipoExamen) { this.tipoExamen = tipoExamen; }
    public String getOjoEvaluado() { return ojoEvaluado; }
    public void setOjoEvaluado(String ojoEvaluado) { this.ojoEvaluado = ojoEvaluado; }
    public String getFechaExamen() { return fechaExamen; }
    public void setFechaExamen(String fechaExamen) { this.fechaExamen = fechaExamen; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getComentarios() { return comentarios; }
    public void setComentarios(String comentarios) { this.comentarios = comentarios; }
    public String getSucursal() { return sucursal; }
    public void setSucursal(String sucursal) { this.sucursal = sucursal; }
    public Boolean getSincronizado() { return sincronizado; }
    public void setSincronizado(Boolean sincronizado) { this.sincronizado = sincronizado; }
}
