package com.example.hangar.config;

import com.example.hangar.model.Encargado;
import com.example.hangar.model.Empresa;
import com.example.hangar.model.Hangar;
import com.example.hangar.model.Modelo;
import com.example.hangar.model.Nave;
import com.example.hangar.model.Persona;
import com.example.hangar.model.Piloto;
import com.example.hangar.model.PilotoNave;
import com.example.hangar.model.Reporte;
import com.example.hangar.model.Taller;
import com.example.hangar.model.Tripulacion;
import com.example.hangar.model.Vuelo;
import com.example.hangar.repository.EncargadoRepository;
import com.example.hangar.repository.EmpresaRepository;
import com.example.hangar.repository.HangarRepository;
import com.example.hangar.repository.ModeloRepository;
import com.example.hangar.repository.NaveRepository;
import com.example.hangar.repository.PersonaRepository;
import com.example.hangar.repository.PilotoNaveRepository;
import com.example.hangar.repository.PilotoRepository;
import com.example.hangar.repository.ReporteRepository;
import com.example.hangar.repository.TallerRepository;
import com.example.hangar.repository.TripulacionRepository;
import com.example.hangar.repository.VueloRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final PersonaRepository personaRepository;
    private final PilotoRepository pilotoRepository;
    private final PilotoNaveRepository pilotoNaveRepository;
    private final EmpresaRepository empresaRepository;
    private final ModeloRepository modeloRepository;
    private final HangarRepository hangarRepository;
    private final EncargadoRepository encargadoRepository;
    private final TallerRepository tallerRepository;
    private final NaveRepository naveRepository;
    private final TripulacionRepository tripulacionRepository;
    private final VueloRepository vueloRepository;
    private final ReporteRepository reporteRepository;

    public DataSeeder(PersonaRepository personaRepository,
                      PilotoRepository pilotoRepository,
                      PilotoNaveRepository pilotoNaveRepository,
                      EmpresaRepository empresaRepository,
                      ModeloRepository modeloRepository,
                      HangarRepository hangarRepository,
                      EncargadoRepository encargadoRepository,
                      TallerRepository tallerRepository,
                      NaveRepository naveRepository,
                      TripulacionRepository tripulacionRepository,
                      VueloRepository vueloRepository,
                      ReporteRepository reporteRepository) {
        this.personaRepository = personaRepository;
        this.pilotoRepository = pilotoRepository;
        this.pilotoNaveRepository = pilotoNaveRepository;
        this.empresaRepository = empresaRepository;
        this.modeloRepository = modeloRepository;
        this.hangarRepository = hangarRepository;
        this.encargadoRepository = encargadoRepository;
        this.tallerRepository = tallerRepository;
        this.naveRepository = naveRepository;
        this.tripulacionRepository = tripulacionRepository;
        this.vueloRepository = vueloRepository;
        this.reporteRepository = reporteRepository;
    }

    @Override
    public void run(String... args) {
        if (empresaRepository.count() > 0) {
            // Si ya existen datos asumimos que la base se encuentra poblada.
            return;
        }

        // Orden correcto según dependencias
        List<Empresa> empresas = seedEmpresas();
        List<Encargado> encargados = seedEncargados();
        List<Hangar> hangares = seedHangares();
        List<Taller> talleres = seedTalleres(hangares, encargados);
        List<Modelo> modelos = seedModelos();
        List<Persona> personas = seedPersonas();
        List<Piloto> pilotos = seedPilotos(personas);
        List<Nave> naves = seedNaves(empresas, hangares, modelos);
        seedPilotoNaves(pilotos, naves);
        List<Vuelo> vuelos = seedVuelos(naves);
        seedTripulaciones(vuelos, personas);
        seedReportes(naves, talleres, encargados);
    }

    private List<Empresa> seedEmpresas() {
        Empresa e1 = new Empresa();
        e1.setNombre("AeroMéxico");
        e1.setContacto("contacto@aeromexico.com");
        e1.setUbicacion("Ciudad de México");
        e1.setRfc("AMX960530AB1");

        Empresa e2 = new Empresa();
        e2.setNombre("Avianca");
        e2.setContacto("info@avianca.com");
        e2.setUbicacion("Bogotá, Colombia");
        e2.setRfc("AVI850215CD2");

        Empresa e3 = new Empresa();
        e3.setNombre("LATAM Airlines");
        e3.setContacto("soporte@latam.com");
        e3.setUbicacion("Santiago, Chile");
        e3.setRfc("LAT920810EF3");

        return empresaRepository.saveAll(Arrays.asList(e1, e2, e3));
    }

    private List<Encargado> seedEncargados() {
        Encargado enc1 = new Encargado();
        enc1.setNombre("Juan Pérez García");

        Encargado enc2 = new Encargado();
        enc2.setNombre("María López Hernández");

        Encargado enc3 = new Encargado();
        enc3.setNombre("Carlos Ramírez Torres");

        return encargadoRepository.saveAll(Arrays.asList(enc1, enc2, enc3));
    }

    private List<Hangar> seedHangares() {
        Hangar h1 = new Hangar();
        h1.setDescripcion("Hangar principal de mantenimiento");
        h1.setCapacidad(20);
        h1.setArea("Norte");
        h1.setNum(1);

        Hangar h2 = new Hangar();
        h2.setDescripcion("Hangar de almacenamiento temporal");
        h2.setCapacidad(15);
        h2.setArea("Sur");
        h2.setNum(2);

        Hangar h3 = new Hangar();
        h3.setDescripcion("Hangar de reparaciones mayores");
        h3.setCapacidad(10);
        h3.setArea("Este");
        h3.setNum(3);

        return hangarRepository.saveAll(Arrays.asList(h1, h2, h3));
    }

    private List<Taller> seedTalleres(List<Hangar> hangares, List<Encargado> encargados) {
        Taller t1 = new Taller();
        t1.setHangar(hangares.get(0));
        t1.setEncargado(encargados.get(0));

        Taller t2 = new Taller();
        t2.setHangar(hangares.get(1));
        t2.setEncargado(encargados.get(1));

        Taller t3 = new Taller();
        t3.setHangar(hangares.get(2));
        t3.setEncargado(encargados.get(2));

        return tallerRepository.saveAll(Arrays.asList(t1, t2, t3));
    }

    private List<Modelo> seedModelos() {
        Modelo m1 = new Modelo();
        m1.setNombreModelo("Boeing 737-800");
        m1.setPeso(75000);
        m1.setCapacidad(180);
        m1.setPaisFabricacion("Estados Unidos");

        Modelo m2 = new Modelo();
        m2.setNombreModelo("Airbus A320neo");
        m2.setPeso(68000);
        m2.setCapacidad(150);
        m2.setPaisFabricacion("Francia");

        Modelo m3 = new Modelo();
        m3.setNombreModelo("Embraer E195-E2");
        m3.setPeso(82000);
        m3.setCapacidad(200);
        m3.setPaisFabricacion("Brasil");

        return modeloRepository.saveAll(Arrays.asList(m1, m2, m3));
    }

    private List<Persona> seedPersonas() {
        Persona p1 = new Persona();
        p1.setNombre("Ana María González");
        p1.setCurp("GOMA900315MDFNRN01");
        p1.setEdad(34);
        p1.setCelular("5551234567");
        p1.setHrsVuelo(1500);
        p1.setLicencia("PIL-COM-2020-001");

        Persona p2 = new Persona();
        p2.setNombre("Roberto Carlos Silva");
        p2.setCurp("SICR850720HDFLRB02");
        p2.setEdad(39);
        p2.setCelular("5559876543");
        p2.setHrsVuelo(2300);
        p2.setLicencia("PIL-COM-2018-042");

        Persona p3 = new Persona();
        p3.setNombre("Laura Patricia Méndez");
        p3.setCurp("MEPL920510MDFNDR03");
        p3.setEdad(32);
        p3.setCelular("5555551234");
        p3.setHrsVuelo(800);
        p3.setLicencia("ING-VUELO-2021-015");

        // Auxiliares de vuelo
        Persona p4 = new Persona();
        p4.setNombre("María Fernanda Torres");
        p4.setCurp("TOMF950825MDFRRL04");
        p4.setEdad(29);
        p4.setCelular("5551112233");
        p4.setHrsVuelo(200);
        p4.setLicencia("TCP-2022-101");

        Persona p5 = new Persona();
        p5.setNombre("José Luis Ramírez");
        p5.setCurp("RALJ880630HDFMSS05");
        p5.setEdad(37);
        p5.setCelular("5554445566");
        p5.setHrsVuelo(350);
        p5.setLicencia("TCP-2020-089");

        return personaRepository.saveAll(Arrays.asList(p1, p2, p3, p4, p5));
    }

    private List<Piloto> seedPilotos(List<Persona> personas) {
        Piloto pi1 = new Piloto();
        pi1.setPersona(personas.get(0));
        pi1.setLicenciaTipo("Piloto Comercial");
        pi1.setCertificaciones("Boeing 737, Airbus A320");

        Piloto pi2 = new Piloto();
        pi2.setPersona(personas.get(1));
        pi2.setLicenciaTipo("Piloto Comercial");
        pi2.setCertificaciones("Boeing 777, Airbus A330");

        return pilotoRepository.saveAll(Arrays.asList(pi1, pi2));
    }

    private void seedPilotoNaves(List<Piloto> pilotos, List<Nave> naves) {
        if (pilotos.isEmpty() || naves.isEmpty()) {
            return;
        }

        // Piloto 1 certificado en nave 1
        PilotoNave pn1 = new PilotoNave();
        pn1.setPiloto(pilotos.get(0));
        pn1.setNave(naves.get(0));
        pn1.setFechaCertificacion(java.time.LocalDate.now().minusYears(2));
        pn1.setFechaExpiracion(java.time.LocalDate.now().plusYears(1));
        pn1.setHorasEnNave(1500);
        pn1.setEstado("ACTIVA");

        // Piloto 2 certificado en nave 2
        PilotoNave pn2 = new PilotoNave();
        pn2.setPiloto(pilotos.get(1));
        pn2.setNave(naves.size() > 1 ? naves.get(1) : naves.get(0));
        pn2.setFechaCertificacion(java.time.LocalDate.now().minusYears(3));
        pn2.setFechaExpiracion(java.time.LocalDate.now().plusYears(2));
        pn2.setHorasEnNave(2300);
        pn2.setEstado("ACTIVA");

        pilotoNaveRepository.saveAll(Arrays.asList(pn1, pn2));
    }

    private List<Nave> seedNaves(List<Empresa> empresas, List<Hangar> hangares, List<Modelo> modelos) {
        Nave n1 = new Nave();
        n1.setEmpresa(empresas.get(0));
        n1.setHangar(hangares.get(0));
        n1.setModelo(modelos.get(0));
        n1.setEstado("Operativa");

        Nave n2 = new Nave();
        n2.setEmpresa(empresas.get(1));
        n2.setHangar(hangares.get(1));
        n2.setModelo(modelos.get(1));
        n2.setEstado("En mantenimiento");

        Nave n3 = new Nave();
        n3.setEmpresa(empresas.get(2));
        n3.setHangar(hangares.get(2));
        n3.setModelo(modelos.get(2));
        n3.setEstado("Operativa");

        return naveRepository.saveAll(Arrays.asList(n1, n2, n3));
    }

    private List<Vuelo> seedVuelos(List<Nave> naves) {
        Vuelo v1 = new Vuelo();
        v1.setNave(naves.get(0));
        v1.setOrigen("Ciudad de México");
        v1.setDestino("Guadalajara");
        v1.setFechaSalida(LocalDateTime.now().plusDays(1));
        v1.setFechaLlegada(LocalDateTime.now().plusDays(1).plusHours(2));
        v1.setPasajeros(150);
        v1.setDistancia(550);

        Vuelo v2 = new Vuelo();
        v2.setNave(naves.get(1));
        v2.setOrigen("Bogotá");
        v2.setDestino("Lima");
        v2.setFechaSalida(LocalDateTime.now().plusDays(2));
        v2.setFechaLlegada(LocalDateTime.now().plusDays(2).plusHours(3));
        v2.setPasajeros(120);
        v2.setDistancia(1850);

        Vuelo v3 = new Vuelo();
        v3.setNave(naves.get(2));
        v3.setOrigen("Santiago");
        v3.setDestino("Buenos Aires");
        v3.setFechaSalida(LocalDateTime.now().plusDays(3));
        v3.setFechaLlegada(LocalDateTime.now().plusDays(3).plusHours(2).plusMinutes(30));
        v3.setPasajeros(180);
        v3.setDistancia(1400);

        return vueloRepository.saveAll(Arrays.asList(v1, v2, v3));
    }

    private List<Tripulacion> seedTripulaciones(List<Vuelo> vuelos, List<Persona> personas) {
        // Vuelo 1 - Tripulación completa y válida
        Tripulacion t1_capitan = new Tripulacion();
        t1_capitan.setVuelo(vuelos.get(0));
        t1_capitan.setPersona(personas.get(0)); // Ana María - Capitán
        t1_capitan.setRolTripulacion("Capitán");

        Tripulacion t1_copiloto = new Tripulacion();
        t1_copiloto.setVuelo(vuelos.get(0));
        t1_copiloto.setPersona(personas.get(1)); // Roberto - Copiloto
        t1_copiloto.setRolTripulacion("Copiloto");

        Tripulacion t1_ingeniero = new Tripulacion();
        t1_ingeniero.setVuelo(vuelos.get(0));
        t1_ingeniero.setPersona(personas.get(2)); // Laura - Ingeniero
        t1_ingeniero.setRolTripulacion("Ingeniero de Vuelo");

        Tripulacion t1_aux1 = new Tripulacion();
        t1_aux1.setVuelo(vuelos.get(0));
        t1_aux1.setPersona(personas.get(3)); // María Fernanda - Auxiliar 1
        t1_aux1.setRolTripulacion("Auxiliar de Vuelo");

        Tripulacion t1_aux2 = new Tripulacion();
        t1_aux2.setVuelo(vuelos.get(0));
        t1_aux2.setPersona(personas.get(4)); // José Luis - Auxiliar 2
        t1_aux2.setRolTripulacion("Auxiliar de Vuelo");

        // Vuelo 2 - Tripulación completa (reutilizando personas)
        Tripulacion t2_capitan = new Tripulacion();
        t2_capitan.setVuelo(vuelos.get(1));
        t2_capitan.setPersona(personas.get(1)); // Roberto - Capitán
        t2_capitan.setRolTripulacion("Capitán");

        Tripulacion t2_copiloto = new Tripulacion();
        t2_copiloto.setVuelo(vuelos.get(1));
        t2_copiloto.setPersona(personas.get(0)); // Ana María - Copiloto
        t2_copiloto.setRolTripulacion("Copiloto");

        Tripulacion t2_ingeniero = new Tripulacion();
        t2_ingeniero.setVuelo(vuelos.get(1));
        t2_ingeniero.setPersona(personas.get(2)); // Laura - Ingeniero
        t2_ingeniero.setRolTripulacion("Ingeniero de Vuelo");

        Tripulacion t2_aux1 = new Tripulacion();
        t2_aux1.setVuelo(vuelos.get(1));
        t2_aux1.setPersona(personas.get(3)); // María Fernanda - Auxiliar 1
        t2_aux1.setRolTripulacion("Auxiliar de Vuelo");

        Tripulacion t2_aux2 = new Tripulacion();
        t2_aux2.setVuelo(vuelos.get(1));
        t2_aux2.setPersona(personas.get(4)); // José Luis - Auxiliar 2
        t2_aux2.setRolTripulacion("Auxiliar de Vuelo");

        // Vuelo 3 - Tripulación completa
        Tripulacion t3_capitan = new Tripulacion();
        t3_capitan.setVuelo(vuelos.get(2));
        t3_capitan.setPersona(personas.get(0)); // Ana María - Capitán
        t3_capitan.setRolTripulacion("Capitán");

        Tripulacion t3_copiloto = new Tripulacion();
        t3_copiloto.setVuelo(vuelos.get(2));
        t3_copiloto.setPersona(personas.get(1)); // Roberto - Copiloto
        t3_copiloto.setRolTripulacion("Copiloto");

        Tripulacion t3_ingeniero = new Tripulacion();
        t3_ingeniero.setVuelo(vuelos.get(2));
        t3_ingeniero.setPersona(personas.get(2)); // Laura - Ingeniero
        t3_ingeniero.setRolTripulacion("Ingeniero de Vuelo");

        Tripulacion t3_aux1 = new Tripulacion();
        t3_aux1.setVuelo(vuelos.get(2));
        t3_aux1.setPersona(personas.get(3)); // María Fernanda - Auxiliar 1
        t3_aux1.setRolTripulacion("Auxiliar de Vuelo");

        Tripulacion t3_aux2 = new Tripulacion();
        t3_aux2.setVuelo(vuelos.get(2));
        t3_aux2.setPersona(personas.get(4)); // José Luis - Auxiliar 2
        t3_aux2.setRolTripulacion("Auxiliar de Vuelo");

        return tripulacionRepository.saveAll(Arrays.asList(
            t1_capitan, t1_copiloto, t1_ingeniero, t1_aux1, t1_aux2,
            t2_capitan, t2_copiloto, t2_ingeniero, t2_aux1, t2_aux2,
            t3_capitan, t3_copiloto, t3_ingeniero, t3_aux1, t3_aux2
        ));
    }

    private void seedReportes(List<Nave> naves, List<Taller> talleres, List<Encargado> encargados) {
        Reporte rep1 = new Reporte();
        rep1.setNave(naves.get(0));
        rep1.setTaller(talleres.get(0));
        rep1.setEncargado(encargados.get(0));
        rep1.setDiagnostico("Revisión de motores - desgaste normal");
        rep1.setAccionesRealizadas("Cambio de aceite y filtros");
        rep1.setFecha(LocalDateTime.now().minusDays(5));
        rep1.setCosto(new java.math.BigDecimal("15000.50"));

        Reporte rep2 = new Reporte();
        rep2.setNave(naves.get(1));
        rep2.setTaller(talleres.get(1));
        rep2.setEncargado(encargados.get(1));
        rep2.setDiagnostico("Falla en sistema hidráulico");
        rep2.setAccionesRealizadas("Reemplazo de bomba hidráulica");
        rep2.setFecha(LocalDateTime.now().minusDays(2));
        rep2.setCosto(new java.math.BigDecimal("28500.00"));

        Reporte rep3 = new Reporte();
        rep3.setNave(naves.get(2));
        rep3.setTaller(talleres.get(2));
        rep3.setEncargado(encargados.get(2));
        rep3.setDiagnostico("Inspección rutinaria - estado óptimo");
        rep3.setAccionesRealizadas("Verificación de sistemas de seguridad");
        rep3.setFecha(LocalDateTime.now().minusDays(1));
        rep3.setCosto(new java.math.BigDecimal("8500.75"));

        reporteRepository.saveAll(Arrays.asList(rep1, rep2, rep3));
    }
}
