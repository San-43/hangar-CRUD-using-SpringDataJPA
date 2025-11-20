package com.example.hangar.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "vuelo")
public class Vuelo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vuelo")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nave")
    private Nave nave;

    @Column(length = 100)
    private String origen;

    @Column(length = 100)
    private String destino;

    @Column(name = "fecha_salida")
    private LocalDateTime fecha_salida;

    @Column(name = "fecha_llegada")
    private LocalDateTime fecha_llegada;

    private Integer pasajeros;

    private Integer distancia;

    @OneToMany(mappedBy = "vuelo")
    private Set<Tripulacion> tripulaciones = new LinkedHashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Nave getNave() {
        return nave;
    }

    public void setNave(Nave nave) {
        this.nave = nave;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public LocalDateTime getFecha_salida() {
        return fecha_salida;
    }

    public void setFecha_salida(LocalDateTime fecha_salida) {
        this.fecha_salida = fecha_salida;
    }

    public LocalDateTime getFecha_llegada() {
        return fecha_llegada;
    }

    public void setFecha_llegada(LocalDateTime fecha_llegada) {
        this.fecha_llegada = fecha_llegada;
    }

    public Integer getPasajeros() {
        return pasajeros;
    }

    public void setPasajeros(Integer pasajeros) {
        this.pasajeros = pasajeros;
    }

    public Integer getDistancia() {
        return distancia;
    }

    public void setDistancia(Integer distancia) {
        this.distancia = distancia;
    }

    public Set<Tripulacion> getTripulaciones() {
        return tripulaciones;
    }

    public void setTripulaciones(Set<Tripulacion> tripulaciones) {
        this.tripulaciones = tripulaciones;
    }
}
