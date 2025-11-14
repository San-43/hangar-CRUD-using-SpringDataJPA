package com.example.hangar.config;

import com.example.hangar.model.Encargado;
import com.example.hangar.model.Empresa;
import com.example.hangar.model.Hangar;
import com.example.hangar.model.Modelo;
import com.example.hangar.model.Nave;
import com.example.hangar.model.Persona;
import com.example.hangar.model.Piloto;
import com.example.hangar.model.Reporte;
import com.example.hangar.model.Rol;
import com.example.hangar.model.Taller;
import com.example.hangar.model.Tripulacion;
import com.example.hangar.model.Vuelo;
import com.example.hangar.repository.EncargadoRepository;
import com.example.hangar.repository.EmpresaRepository;
import com.example.hangar.repository.HangarRepository;
import com.example.hangar.repository.ModeloRepository;
import com.example.hangar.repository.NaveRepository;
import com.example.hangar.repository.PersonaRepository;
import com.example.hangar.repository.PilotoRepository;
import com.example.hangar.repository.ReporteRepository;
import com.example.hangar.repository.RolRepository;
import com.example.hangar.repository.TallerRepository;
import com.example.hangar.repository.TripulacionRepository;
import com.example.hangar.repository.VueloRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Component
public class DataSeeder implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final PersonaRepository personaRepository;
    private final PilotoRepository pilotoRepository;
    private final EmpresaRepository empresaRepository;
    private final ModeloRepository modeloRepository;
    private final HangarRepository hangarRepository;
    private final EncargadoRepository encargadoRepository;
    private final TallerRepository tallerRepository;
    private final NaveRepository naveRepository;
    private final TripulacionRepository tripulacionRepository;
    private final VueloRepository vueloRepository;
    private final ReporteRepository reporteRepository;

    public DataSeeder(RolRepository rolRepository,
                      PersonaRepository personaRepository,
                      PilotoRepository pilotoRepository,
                      EmpresaRepository empresaRepository,
                      ModeloRepository modeloRepository,
                      HangarRepository hangarRepository,
                      EncargadoRepository encargadoRepository,
                      TallerRepository tallerRepository,
                      NaveRepository naveRepository,
                      TripulacionRepository tripulacionRepository,
                      VueloRepository vueloRepository,
                      ReporteRepository reporteRepository) {
        this.rolRepository = rolRepository;
        this.personaRepository = personaRepository;
        this.pilotoRepository = pilotoRepository;
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
        if (rolRepository.count() > 0) {
            // Si ya existen datos asumimos que la base se encuentra poblada.
            return;
        }

        List<Rol> roles = seedRoles();
        List<Persona> personas = seedPersonas(roles);
        List<Piloto> pilotos = seedPilotos(roles);
        List<Empresa> empresas = seedEmpresas();
        List<Modelo> modelos = seedModelos();
        List<Hangar> hangares = seedHangares(empresas);
        seedEncargados(hangares, personas);
        List<Taller> talleres = seedTalleres(hangares);
        List<Nave> naves = seedNaves(empresas, hangares, modelos);
        List<Tripulacion> tripulaciones = seedTripulaciones(personas, pilotos);
        seedVuelos(naves, tripulaciones);
        seedReportes(talleres, naves);
    }

    private List<Rol> seedRoles() {
        Rol admin = createRol("Administrador");
        Rol mecanico = createRol("Mecánico");
        Rol operador = createRol("Operador");
        return rolRepository.saveAll(Arrays.asList(admin, mecanico, operador));
    }

    private Rol createRol(String nombre) {
        Rol rol = new Rol();
        rol.setNombre(nombre);
        return rol;
    }

    private List<Persona> seedPersonas(List<Rol> roles) {
        Persona ana = createPersona("Ana", "Ramírez", "DOC-001", roles.get(0));
        Persona carlos = createPersona("Carlos", "Pérez", "DOC-002", roles.get(1));
        Persona lucia = createPersona("Lucía", "González", "DOC-003", roles.get(2));
        return personaRepository.saveAll(Arrays.asList(ana, carlos, lucia));
    }

    private Persona createPersona(String nombres, String apellidos, String documento, Rol rol) {
        Persona persona = new Persona();
        persona.setNombres(nombres);
        persona.setApellidos(apellidos);
        persona.setDocumento(documento);
        persona.setRol(rol);
        return persona;
    }

    private List<Piloto> seedPilotos(List<Rol> roles) {
        Piloto piloto1 = createPiloto("Mario", "López", "PIL-100", roles.get(0), "LIC-01", "10 años");
        Piloto piloto2 = createPiloto("Sara", "Quintero", "PIL-101", roles.get(0), "LIC-02", "7 años");
        Piloto piloto3 = createPiloto("Iván", "Núñez", "PIL-102", roles.get(0), "LIC-03", "5 años");
        return pilotoRepository.saveAll(Arrays.asList(piloto1, piloto2, piloto3));
    }

    private Piloto createPiloto(String nombres, String apellidos, String documento, Rol rol, String licencia, String experiencia) {
        Piloto piloto = new Piloto();
        piloto.setNombres(nombres);
        piloto.setApellidos(apellidos);
        piloto.setDocumento(documento);
        piloto.setRol(rol);
        piloto.setLicencia(licencia);
        piloto.setExperiencia(experiencia);
        return piloto;
    }

    private List<Empresa> seedEmpresas() {
        Empresa aeroStar = createEmpresa("AeroStar", "México");
        Empresa skyWays = createEmpresa("SkyWays", "Colombia");
        Empresa andesAir = createEmpresa("AndesAir", "Perú");
        return empresaRepository.saveAll(Arrays.asList(aeroStar, skyWays, andesAir));
    }

    private Empresa createEmpresa(String nombre, String pais) {
        Empresa empresa = new Empresa();
        empresa.setNombre(nombre);
        empresa.setPais(pais);
        return empresa;
    }

    private List<Modelo> seedModelos() {
        Modelo carguero = createModelo("Carguero X", "Boeing", 12);
        Modelo explorador = createModelo("Explorador 9", "Airbus", 8);
        Modelo escolta = createModelo("Escolta 3", "Embraer", 6);
        return modeloRepository.saveAll(Arrays.asList(carguero, explorador, escolta));
    }

    private Modelo createModelo(String nombre, String fabricante, Integer capacidad) {
        Modelo modelo = new Modelo();
        modelo.setNombre(nombre);
        modelo.setFabricante(fabricante);
        modelo.setCapacidad(capacidad);
        return modelo;
    }

    private List<Hangar> seedHangares(List<Empresa> empresas) {
        Hangar alfa = createHangar("HN-ALF", 10, "Zona Norte", empresas.get(0));
        Hangar beta = createHangar("HN-BET", 8, "Zona Central", empresas.get(1));
        Hangar gamma = createHangar("HN-GAM", 12, "Zona Sur", empresas.get(2));
        return hangarRepository.saveAll(Arrays.asList(alfa, beta, gamma));
    }

    private Hangar createHangar(String codigo, int capacidad, String ubicacion, Empresa empresa) {
        Hangar hangar = new Hangar();
        hangar.setCodigo(codigo);
        hangar.setCapacidad(capacidad);
        hangar.setUbicacion(ubicacion);
        hangar.setEmpresa(empresa);
        return hangar;
    }

    private void seedEncargados(List<Hangar> hangares, List<Persona> personas) {
        Encargado enc1 = createEncargado(personas.get(0), hangares.get(0));
        Encargado enc2 = createEncargado(personas.get(1), hangares.get(1));
        Encargado enc3 = createEncargado(personas.get(2), hangares.get(2));
        encargadoRepository.saveAll(Arrays.asList(enc1, enc2, enc3));
    }

    private Encargado createEncargado(Persona persona, Hangar hangar) {
        Encargado encargado = new Encargado();
        encargado.setPersona(persona);
        encargado.setHangar(hangar);
        return encargado;
    }

    private List<Taller> seedTalleres(List<Hangar> hangares) {
        Taller motor = createTaller("Motores", "Motores y turbinas", hangares.get(0));
        Taller avionica = createTaller("Aviónica", "Sensores", hangares.get(1));
        Taller fuselaje = createTaller("Fuselaje", "Reparaciones estructurales", hangares.get(2));
        return tallerRepository.saveAll(Arrays.asList(motor, avionica, fuselaje));
    }

    private Taller createTaller(String nombre, String especialidad, Hangar hangar) {
        Taller taller = new Taller();
        taller.setNombre(nombre);
        taller.setEspecialidad(especialidad);
        taller.setHangar(hangar);
        return taller;
    }

    private List<Nave> seedNaves(List<Empresa> empresas, List<Hangar> hangares, List<Modelo> modelos) {
        Nave nave1 = createNave("AV-100", "Operativa", modelos.get(0), empresas.get(0), hangares.get(0));
        Nave nave2 = createNave("AV-200", "En mantenimiento", modelos.get(1), empresas.get(1), hangares.get(1));
        Nave nave3 = createNave("AV-300", "Listo para despegar", modelos.get(2), empresas.get(2), hangares.get(2));
        return naveRepository.saveAll(Arrays.asList(nave1, nave2, nave3));
    }

    private Nave createNave(String matricula, String estado, Modelo modelo, Empresa empresa, Hangar hangar) {
        Nave nave = new Nave();
        nave.setMatricula(matricula);
        nave.setEstado(estado);
        nave.setModelo(modelo);
        nave.setEmpresa(empresa);
        nave.setHangar(hangar);
        return nave;
    }

    private List<Tripulacion> seedTripulaciones(List<Persona> personas, List<Piloto> pilotos) {
        Tripulacion alfa = createTripulacion("Tripulación Alfa", Set.of(pilotos.get(0), personas.get(0)));
        Tripulacion beta = createTripulacion("Tripulación Beta", Set.of(pilotos.get(1), personas.get(1)));
        Tripulacion gamma = createTripulacion("Tripulación Gamma", Set.of(pilotos.get(2), personas.get(2)));
        return tripulacionRepository.saveAll(Arrays.asList(alfa, beta, gamma));
    }

    private Tripulacion createTripulacion(String nombre, Set<Persona> integrantes) {
        Tripulacion tripulacion = new Tripulacion();
        tripulacion.setNombre(nombre);
        tripulacion.getIntegrantes().addAll(integrantes);
        return tripulacion;
    }

    private void seedVuelos(List<Nave> naves, List<Tripulacion> tripulaciones) {
        Vuelo vuelo1 = createVuelo("FL-001", "Bogotá", LocalDateTime.now().plusDays(1), naves.get(0), tripulaciones.get(0));
        Vuelo vuelo2 = createVuelo("FL-002", "Quito", LocalDateTime.now().plusDays(2), naves.get(1), tripulaciones.get(1));
        Vuelo vuelo3 = createVuelo("FL-003", "Lima", LocalDateTime.now().plusDays(3), naves.get(2), tripulaciones.get(2));
        vueloRepository.saveAll(Arrays.asList(vuelo1, vuelo2, vuelo3));
    }

    private Vuelo createVuelo(String codigo, String destino, LocalDateTime fecha, Nave nave, Tripulacion tripulacion) {
        Vuelo vuelo = new Vuelo();
        vuelo.setCodigo(codigo);
        vuelo.setDestino(destino);
        vuelo.setFechaSalida(fecha);
        vuelo.setNave(nave);
        vuelo.setTripulacion(tripulacion);
        return vuelo;
    }

    private void seedReportes(List<Taller> talleres, List<Nave> naves) {
        Reporte reporte1 = createReporte("Revisión general", "Chequeo completo de sistemas.", talleres.get(0), naves.get(0));
        Reporte reporte2 = createReporte("Cambio de sensores", "Actualización del paquete aviónico.", talleres.get(1), naves.get(1));
        Reporte reporte3 = createReporte("Refuerzo de fuselaje", "Corrección de microfracturas.", talleres.get(2), naves.get(2));
        reporteRepository.saveAll(Arrays.asList(reporte1, reporte2, reporte3));
    }

    private Reporte createReporte(String titulo, String descripcion, Taller taller, Nave nave) {
        Reporte reporte = new Reporte();
        reporte.setTitulo(titulo);
        reporte.setDescripcion(descripcion);
        reporte.setTaller(taller);
        reporte.setNave(nave);
        return reporte;
    }
}

