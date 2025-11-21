package com.example.hangar.model;

import jakarta.persistence.*;

@Entity
@Table(name = "naves")
public class Nave {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_nave")
    private Integer idNave;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_empresa")
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_hangar")
    private Hangar hangar;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_modelo")
    private Modelo modelo;

    @Column(length = 50)
    private String estado;

    public Integer getIdNave() { return idNave; }
    public void setIdNave(Integer idNave) { this.idNave = idNave; }
    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }
    public Hangar getHangar() { return hangar; }
    public void setHangar(Hangar hangar) { this.hangar = hangar; }
    public Modelo getModelo() { return modelo; }
    public void setModelo(Modelo modelo) { this.modelo = modelo; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    // Compatibilidad: delegar capacidad al modelo
    public Integer getCapacidad() { return modelo != null ? modelo.getCapacidad() : null; }
    public void setCapacidad(Integer capacidad) { /* deprecated: capacidad gestionada en Modelo */ }
}

