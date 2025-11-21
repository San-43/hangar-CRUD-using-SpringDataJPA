package com.example.hangar.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "piloto_naves")
public class PilotoNave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_piloto", nullable = false)
    private Piloto piloto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nave", nullable = false)
    private Nave nave;

    @Column(name = "fecha_certificacion")
    private LocalDate fechaCertificacion;

    @Column(name = "fecha_expiracion")
    private LocalDate fechaExpiracion;

    @Column(name = "horas_en_nave")
    private Integer horasEnNave;

    @Column(length = 20)
    private String estado; // ACTIVA, VENCIDA, SUSPENDIDA

    // Constructors
    public PilotoNave() {
        this.estado = "ACTIVA";
        this.horasEnNave = 0;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Piloto getPiloto() {
        return piloto;
    }

    public void setPiloto(Piloto piloto) {
        this.piloto = piloto;
    }

    public Nave getNave() {
        return nave;
    }

    public void setNave(Nave nave) {
        this.nave = nave;
    }

    public LocalDate getFechaCertificacion() {
        return fechaCertificacion;
    }

    public void setFechaCertificacion(LocalDate fechaCertificacion) {
        this.fechaCertificacion = fechaCertificacion;
    }

    public LocalDate getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(LocalDate fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }

    public Integer getHorasEnNave() {
        return horasEnNave;
    }

    public void setHorasEnNave(Integer horasEnNave) {
        this.horasEnNave = horasEnNave;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}

