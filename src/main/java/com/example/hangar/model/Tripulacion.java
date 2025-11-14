package com.example.hangar.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "tripulaciones")
public class Tripulacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String nombre;

    @ManyToMany
    @JoinTable(name = "tripulacion_persona",
            joinColumns = @JoinColumn(name = "tripulacion_id"),
            inverseJoinColumns = @JoinColumn(name = "persona_id"))
    private Set<Persona> integrantes = new LinkedHashSet<>();

    @OneToMany(mappedBy = "tripulacion", fetch = FetchType.LAZY)
    private Set<Vuelo> vuelos = new LinkedHashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Set<Persona> getIntegrantes() {
        return integrantes;
    }

    public void setIntegrantes(Set<Persona> integrantes) {
        this.integrantes = integrantes;
    }

    public Set<Vuelo> getVuelos() {
        return vuelos;
    }

    public void setVuelos(Set<Vuelo> vuelos) {
        this.vuelos = vuelos;
    }
}
