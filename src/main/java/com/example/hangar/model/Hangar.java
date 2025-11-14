package com.example.hangar.model;

import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "hangares")
public class Hangar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String codigo;

    @Column(nullable = false)
    private Integer capacidad;

    @Column(length = 120)
    private String ubicacion;

    @ManyToOne(fetch = FetchType.LAZY)
    private Empresa empresa;

    @OneToOne(mappedBy = "hangar", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Encargado encargado;

    @OneToMany(mappedBy = "hangar")
    private Set<Taller> talleres = new LinkedHashSet<>();

    @OneToMany(mappedBy = "hangar")
    private Set<Nave> naves = new LinkedHashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Integer getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public Encargado getEncargado() {
        return encargado;
    }

    public void setEncargado(Encargado encargado) {
        this.encargado = encargado;
    }

    public Set<Taller> getTalleres() {
        return talleres;
    }

    public void setTalleres(Set<Taller> talleres) {
        this.talleres = talleres;
    }

    public Set<Nave> getNaves() {
        return naves;
    }

    public void setNaves(Set<Nave> naves) {
        this.naves = naves;
    }
}
