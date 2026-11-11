package com.team_04.model;

public class Inscripcion {
    private int idInscripcion;
    private int idParticipante;
    private String categoria;
    private String fechaInscripcion;
    private String estado;

    public Inscripcion() {}

    public Inscripcion(int idInscripcion, int idParticipante, String categoria, String fechaInscripcion, String estado) {
        this.idInscripcion = idInscripcion;
        this.idParticipante = idParticipante;
        this.categoria = categoria;
        this.fechaInscripcion = fechaInscripcion;
        this.estado = estado;
    }

    public Inscripcion(int idParticipante, String categoria, String fechaInscripcion, String estado) {
        this.idParticipante = idParticipante;
        this.categoria = categoria;
        this.fechaInscripcion = fechaInscripcion;
        this.estado = estado;
    }

    public int getIdInscripcion() { return idInscripcion; }
    public void setIdInscripcion(int idInscripcion) { this.idInscripcion = idInscripcion; }

    public int getIdParticipante() { return idParticipante; }
    public void setIdParticipante(int idParticipante) { this.idParticipante = idParticipante; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getFechaInscripcion() { return fechaInscripcion; }
    public void setFechaInscripcion(String fechaInscripcion) { this.fechaInscripcion = fechaInscripcion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}