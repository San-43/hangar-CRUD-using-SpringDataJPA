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
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "tripulaciones",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_vuelo_persona",
        columnNames = {"id_vuelo", "id_persona"}
    )
)
public class Tripulacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tripulacion")
    private Integer idTripulacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vuelo")
    private Vuelo vuelo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_persona")
    private Persona persona;

    @Column(name = "rol_tripulacion", length = 50, nullable = false)
    private String rolTripulacion;  // "Capitán", "Copiloto", "Ingeniero de Vuelo", "Auxiliar de Vuelo"

    public Integer getIdTripulacion() {
        return idTripulacion;
    }

    public void setIdTripulacion(Integer idTripulacion) {
        this.idTripulacion = idTripulacion;
    }

    public Vuelo getVuelo() {
        return vuelo;
    }

    public void setVuelo(Vuelo vuelo) {
        this.vuelo = vuelo;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public String getRolTripulacion() {
        return rolTripulacion;
    }

    public void setRolTripulacion(String rolTripulacion) {
        this.rolTripulacion = rolTripulacion;
    }
}
