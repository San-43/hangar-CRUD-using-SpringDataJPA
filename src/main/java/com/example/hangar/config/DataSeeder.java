package com.example.hangar.config;

import com.example.hangar.model.*;
import com.example.hangar.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final PersonaRepository personaRepository;
    private final RolRepository rolRepository;
    private final EmpresaRepository empresaRepository;
    private final ModeloRepository modeloRepository;
    private final PilotoRepository pilotoRepository;
    private final HangarRepository hangarRepository;
    private final EncargadoRepository encargadoRepository;
    private final TallerRepository tallerRepository;
    private final NaveRepository naveRepository;
    private final VueloRepository vueloRepository;
    private final TripulacionRepository tripulacionRepository;
    private final ReporteRepository reporteRepository;

    public DataSeeder(PersonaRepository personaRepository,
                      RolRepository rolRepository,
                      EmpresaRepository empresaRepository,
                      ModeloRepository modeloRepository,
                      PilotoRepository pilotoRepository,
                      HangarRepository hangarRepository,
                      EncargadoRepository encargadoRepository,
                      TallerRepository tallerRepository,
                      NaveRepository naveRepository,
                      VueloRepository vueloRepository,
                      TripulacionRepository tripulacionRepository,
                      ReporteRepository reporteRepository) {
        this.personaRepository = personaRepository;
        this.rolRepository = rolRepository;
        this.empresaRepository = empresaRepository;
        this.modeloRepository = modeloRepository;
        this.pilotoRepository = pilotoRepository;
        this.hangarRepository = hangarRepository;
        this.encargadoRepository = encargadoRepository;
        this.tallerRepository = tallerRepository;
        this.naveRepository = naveRepository;
        this.vueloRepository = vueloRepository;
        this.tripulacionRepository = tripulacionRepository;
        this.reporteRepository = reporteRepository;
    }

    @Override
    public void run(String... args) {
        if (personaRepository.count() > 0) {
            return; // Data already seeded
        }

        // Seed in order of dependencies
        List<Persona> personas = seedPersonas();
        List<Rol> roles = seedRoles(personas);
        List<Empresa> empresas = seedEmpresas();
        List<Modelo> modelos = seedModelos();
        List<Piloto> pilotos = seedPilotos(personas, modelos);
        List<Hangar> hangares = seedHangares();
        List<Encargado> encargados = seedEncargados();
        List<Taller> talleres = seedTalleres(hangares, encargados);
        List<Nave> naves = seedNaves(empresas, hangares, modelos);
        List<Vuelo> vuelos = seedVuelos(naves);
        List<Tripulacion> tripulaciones = seedTripulaciones(vuelos, personas, roles);
        seedReportes(naves, talleres, encargados);
    }

    private List<Persona> seedPersonas() {
        Persona p1 = new Persona();
        p1.setNombre("Ana Ramírez");
        p1.setCurp("RAMA850315MDFMRN09");
        p1.setEdad(39);
        p1.setCelular("5551234567");
        p1.setHrs_vuelo(1500);

        Persona p2 = new Persona();
        p2.setNombre("Carlos Pérez");
        p2.setCurp("PERC900520HDFRRL03");
        p2.setEdad(34);
        p2.setCelular("5559876543");
        p2.setHrs_vuelo(800);

        Persona p3 = new Persona();
        p3.setNombre("Lucía González");
        p3.setCurp("GONL920710MDFRRC08");
        p3.setEdad(32);
        p3.setCelular("5555555555");
        p3.setHrs_vuelo(200);

        return personaRepository.saveAll(Arrays.asList(p1, p2, p3));
    }

    private List<Rol> seedRoles(List<Persona> personas) {
        Rol r1 = new Rol();
        r1.setPersona(personas.get(0));
        r1.setRol("Piloto");

        Rol r2 = new Rol();
        r2.setPersona(personas.get(1));
        r2.setRol("Mecánico");

        Rol r3 = new Rol();
        r3.setPersona(personas.get(2));
        r3.setRol("Copiloto");

        return rolRepository.saveAll(Arrays.asList(r1, r2, r3));
    }

    private List<Empresa> seedEmpresas() {
        Empresa e1 = new Empresa();
        e1.setNombre("AeroStar");
        e1.setContacto("contacto@aerostar.com");
        e1.setUbicacion("Ciudad de México");
        e1.setRfc("AER850315ABC");

        Empresa e2 = new Empresa();
        e2.setNombre("SkyWays");
        e2.setContacto("info@skyways.com");
        e2.setUbicacion("Bogotá, Colombia");
        e2.setRfc("SKY900520DEF");

        Empresa e3 = new Empresa();
        e3.setNombre("AndesAir");
        e3.setContacto("soporte@andesair.com");
        e3.setUbicacion("Lima, Perú");
        e3.setRfc("AND920710GHI");

        return empresaRepository.saveAll(Arrays.asList(e1, e2, e3));
    }

    private List<Modelo> seedModelos() {
        Modelo m1 = new Modelo();
        m1.setPeso(50000);
        m1.setPais_fabricacion("Estados Unidos");

        Modelo m2 = new Modelo();
        m2.setPeso(35000);
        m2.setPais_fabricacion("Francia");

        Modelo m3 = new Modelo();
        m3.setPeso(28000);
        m3.setPais_fabricacion("Brasil");

        return modeloRepository.saveAll(Arrays.asList(m1, m2, m3));
    }

    private List<Piloto> seedPilotos(List<Persona> personas, List<Modelo> modelos) {
        Piloto p1 = new Piloto();
        p1.setPersona(personas.get(0));
        p1.setModelo(modelos.get(0));
        p1.setHrs_voladas(1200);

        Piloto p2 = new Piloto();
        p2.setPersona(personas.get(1));
        p2.setModelo(modelos.get(1));
        p2.setHrs_voladas(750);

        Piloto p3 = new Piloto();
        p3.setPersona(personas.get(2));
        p3.setModelo(modelos.get(2));
        p3.setHrs_voladas(180);

        return pilotoRepository.saveAll(Arrays.asList(p1, p2, p3));
    }

    private List<Hangar> seedHangares() {
        Hangar h1 = new Hangar();
        h1.setDescripcion("Hangar principal para mantenimiento");
        h1.setCapacidad(10);
        h1.setArea("Norte");
        h1.setNum(1);

        Hangar h2 = new Hangar();
        h2.setDescripcion("Hangar secundario para almacenamiento");
        h2.setCapacidad(8);
        h2.setArea("Sur");
        h2.setNum(2);

        Hangar h3 = new Hangar();
        h3.setDescripcion("Hangar de reparaciones mayores");
        h3.setCapacidad(12);
        h3.setArea("Este");
        h3.setNum(3);

        return hangarRepository.saveAll(Arrays.asList(h1, h2, h3));
    }

    private List<Encargado> seedEncargados() {
        Encargado e1 = new Encargado();
        e1.setNombre("Jorge Martínez");

        Encargado e2 = new Encargado();
        e2.setNombre("Patricia Silva");

        Encargado e3 = new Encargado();
        e3.setNombre("Roberto Díaz");

        return encargadoRepository.saveAll(Arrays.asList(e1, e2, e3));
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

    private List<Nave> seedNaves(List<Empresa> empresas, List<Hangar> hangares, List<Modelo> modelos) {
        Nave n1 = new Nave();
        n1.setEmpresa(empresas.get(0));
        n1.setHangar(hangares.get(0));
        n1.setModelo(modelos.get(0));
        n1.setPeso(48000);
        n1.setCapacidad(200);
        n1.setEstado("Operativa");

        Nave n2 = new Nave();
        n2.setEmpresa(empresas.get(1));
        n2.setHangar(hangares.get(1));
        n2.setModelo(modelos.get(1));
        n2.setPeso(33000);
        n2.setCapacidad(150);
        n2.setEstado("En mantenimiento");

        Nave n3 = new Nave();
        n3.setEmpresa(empresas.get(2));
        n3.setHangar(hangares.get(2));
        n3.setModelo(modelos.get(2));
        n3.setPeso(27000);
        n3.setCapacidad(100);
        n3.setEstado("Listo para despegar");

        return naveRepository.saveAll(Arrays.asList(n1, n2, n3));
    }

    private List<Vuelo> seedVuelos(List<Nave> naves) {
        Vuelo v1 = new Vuelo();
        v1.setNave(naves.get(0));
        v1.setOrigen("Ciudad de México");
        v1.setDestino("Bogotá");
        v1.setFecha_salida(LocalDateTime.now().plusDays(1));
        v1.setFecha_llegada(LocalDateTime.now().plusDays(1).plusHours(3));
        v1.setPasajeros(180);
        v1.setDistancia(2500);

        Vuelo v2 = new Vuelo();
        v2.setNave(naves.get(1));
        v2.setOrigen("Bogotá");
        v2.setDestino("Lima");
        v2.setFecha_salida(LocalDateTime.now().plusDays(2));
        v2.setFecha_llegada(LocalDateTime.now().plusDays(2).plusHours(2));
        v2.setPasajeros(120);
        v2.setDistancia(1800);

        Vuelo v3 = new Vuelo();
        v3.setNave(naves.get(2));
        v3.setOrigen("Lima");
        v3.setDestino("Ciudad de México");
        v3.setFecha_salida(LocalDateTime.now().plusDays(3));
        v3.setFecha_llegada(LocalDateTime.now().plusDays(3).plusHours(4));
        v3.setPasajeros(90);
        v3.setDistancia(3200);

        return vueloRepository.saveAll(Arrays.asList(v1, v2, v3));
    }

    private List<Tripulacion> seedTripulaciones(List<Vuelo> vuelos, List<Persona> personas, List<Rol> roles) {
        Tripulacion t1 = new Tripulacion();
        t1.setVuelo(vuelos.get(0));
        t1.setPersona(personas.get(0));
        t1.setRol(roles.get(0));

        Tripulacion t2 = new Tripulacion();
        t2.setVuelo(vuelos.get(1));
        t2.setPersona(personas.get(1));
        t2.setRol(roles.get(1));

        Tripulacion t3 = new Tripulacion();
        t3.setVuelo(vuelos.get(2));
        t3.setPersona(personas.get(2));
        t3.setRol(roles.get(2));

        return tripulacionRepository.saveAll(Arrays.asList(t1, t2, t3));
    }

    private void seedReportes(List<Nave> naves, List<Taller> talleres, List<Encargado> encargados) {
        Reporte r1 = new Reporte();
        r1.setNave(naves.get(0));
        r1.setTaller(talleres.get(0));
        r1.setEncargado(encargados.get(0));
        r1.setDiagnostico("Revisión general de motores");
        r1.setAcciones_realizadas("Cambio de aceite y filtros");
        r1.setFecha(LocalDateTime.now().minusDays(5));
        r1.setCosto(new BigDecimal("5000.00"));

        Reporte r2 = new Reporte();
        r2.setNave(naves.get(1));
        r2.setTaller(talleres.get(1));
        r2.setEncargado(encargados.get(1));
        r2.setDiagnostico("Actualización de sistema aviónico");
        r2.setAcciones_realizadas("Instalación de nuevos sensores");
        r2.setFecha(LocalDateTime.now().minusDays(3));
        r2.setCosto(new BigDecimal("12000.00"));

        Reporte r3 = new Reporte();
        r3.setNave(naves.get(2));
        r3.setTaller(talleres.get(2));
        r3.setEncargado(encargados.get(2));
        r3.setDiagnostico("Reparación de fuselaje");
        r3.setAcciones_realizadas("Soldadura y refuerzo estructural");
        r3.setFecha(LocalDateTime.now().minusDays(1));
        r3.setCosto(new BigDecimal("8500.00"));

        reporteRepository.saveAll(Arrays.asList(r1, r2, r3));
    }
}
