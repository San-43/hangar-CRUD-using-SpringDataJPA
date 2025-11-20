package com.example.hangar.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "modelo")
public class Modelo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_modelo")
    private Integer id;

    private Integer peso;

    @Column(length = 100)
    private String pais_fabricacion;

    @OneToMany(mappedBy = "modelo")
    private Set<Nave> naves = new LinkedHashSet<>();

    @OneToMany(mappedBy = "modelo")
    private Set<Piloto> pilotos = new LinkedHashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPeso() {
        return peso;
    }

    public void setPeso(Integer peso) {
        this.peso = peso;
    }

    public String getPais_fabricacion() {
        return pais_fabricacion;
    }

    public void setPais_fabricacion(String pais_fabricacion) {
        this.pais_fabricacion = pais_fabricacion;
    }

    public Set<Nave> getNaves() {
        return naves;
    }

    public void setNaves(Set<Nave> naves) {
        this.naves = naves;
    }

    public Set<Piloto> getPilotos() {
        return pilotos;
    }

    public void setPilotos(Set<Piloto> pilotos) {
        this.pilotos = pilotos;
    }
}
