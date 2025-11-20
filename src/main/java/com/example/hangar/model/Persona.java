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
@Table(name = "persona")
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_persona")
    private Integer id;

    @Column(length = 100)
    private String nombre;

    @Column(length = 18)
    private String curp;

    private Integer edad;

    @Column(length = 20)
    private String celular;

    private Integer hrs_vuelo;

    @OneToMany(mappedBy = "persona")
    private Set<Rol> roles = new LinkedHashSet<>();

    @OneToMany(mappedBy = "persona")
    private Set<Piloto> pilotos = new LinkedHashSet<>();

    @OneToMany(mappedBy = "persona")
    private Set<Tripulacion> tripulaciones = new LinkedHashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCurp() {
        return curp;
    }

    public void setCurp(String curp) {
        this.curp = curp;
    }

    public Integer getEdad() {
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public Integer getHrs_vuelo() {
        return hrs_vuelo;
    }

    public void setHrs_vuelo(Integer hrs_vuelo) {
        this.hrs_vuelo = hrs_vuelo;
    }

    public Set<Rol> getRoles() {
        return roles;
    }

    public void setRoles(Set<Rol> roles) {
        this.roles = roles;
    }

    public Set<Piloto> getPilotos() {
        return pilotos;
    }

    public void setPilotos(Set<Piloto> pilotos) {
        this.pilotos = pilotos;
    }

    public Set<Tripulacion> getTripulaciones() {
        return tripulaciones;
    }

    public void setTripulaciones(Set<Tripulacion> tripulaciones) {
        this.tripulaciones = tripulaciones;
    }
}
