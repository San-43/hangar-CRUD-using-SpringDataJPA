package com.example.hangar.model;

import jakarta.persistence.CascadeType;
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
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pilotos")
public class Piloto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_piloto")
    private Integer idPiloto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_persona")
    private Persona persona;

    @Column(name = "licencia_tipo", length = 50)
    private String licenciaTipo;

    @Column(columnDefinition = "TEXT")
    private String certificaciones;

    @OneToMany(mappedBy = "piloto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PilotoNave> navesCertificadas = new ArrayList<>();

    public Integer getIdPiloto() {
        return idPiloto;
    }

    public void setIdPiloto(Integer idPiloto) {
        this.idPiloto = idPiloto;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public String getLicenciaTipo() {
        return licenciaTipo;
    }

    public void setLicenciaTipo(String licenciaTipo) {
        this.licenciaTipo = licenciaTipo;
    }

    public String getCertificaciones() {
        return certificaciones;
    }

    public void setCertificaciones(String certificaciones) {
        this.certificaciones = certificaciones;
    }

    public List<PilotoNave> getNavesCertificadas() {
        return navesCertificadas;
    }

    public void setNavesCertificadas(List<PilotoNave> navesCertificadas) {
        this.navesCertificadas = navesCertificadas;
    }

    // Helper methods
    public void addNave(PilotoNave pilotoNave) {
        navesCertificadas.add(pilotoNave);
        pilotoNave.setPiloto(this);
    }

    public void removeNave(PilotoNave pilotoNave) {
        navesCertificadas.remove(pilotoNave);
        pilotoNave.setPiloto(null);
    }
}
