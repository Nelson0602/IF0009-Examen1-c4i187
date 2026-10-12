package com.medtriage.service;

import com.medtriage.dto.CitaMedicaDTO;
import com.medtriage.dto.CrearCitaDTO;
import com.medtriage.model.CitaMedica;
import com.medtriage.model.Doctor;
import com.medtriage.model.Paciente;
import com.medtriage.repository.CitaMedicaRepository;
import com.medtriage.repository.DoctorRepository;
import com.medtriage.repository.PacienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// Reglas de negocio para gestionar las citas médicas de emergencia
@Service
public class CitaMedicaService {

    private static final List<String> PRIORITIES = List.of("ALTA", "MEDIA", "BAJA");
    private static final List<String> NEW_STATES = List.of("EN_ATENCION", "ATENDIDA", "CANCELADA");
    private static final List<String> CLOSED_STATES = List.of("ATENDIDA", "CANCELADA");

    private final CitaMedicaRepository citaRepository;
    private final PacienteRepository pacienteRepository;
    private final DoctorRepository doctorRepository;

    public CitaMedicaService(CitaMedicaRepository citaRepository,
            PacienteRepository pacienteRepository,
            DoctorRepository doctorRepository) {
        this.citaRepository = citaRepository;
        this.pacienteRepository = pacienteRepository;
        this.doctorRepository = doctorRepository;
    }

    // Retorna todas las citas, de la más reciente a la más antigua
    @Transactional(readOnly = true)
    public List<CitaMedicaDTO> obtenerTodas() {
        return citaRepository.findAllByOrderByFechaCitaDesc()
                .stream().map(this::toDTO).toList();
    }

    // Retorna las citas de una prioridad (ALTA, MEDIA o BAJA)
    @Transactional(readOnly = true)
    public List<CitaMedicaDTO> obtenerPorPrioridad(String prioridad) {
        String priority = normalize(prioridad);
        if (!PRIORITIES.contains(priority)) {
            throw new IllegalArgumentException("Prioridad no válida: " + prioridad);
        }
        return citaRepository.findByNivelPrioridadOrderByFechaCitaDesc(priority)
                .stream().map(this::toDTO).toList();
    }

    // Crea una cita nueva: busca, valida, arma, guarda y convierte a DTO
    @Transactional
    public CitaMedicaDTO crearCita(CrearCitaDTO dto) {
        if (dto.getPacienteId() == null || dto.getDoctorId() == null) {
            throw new IllegalArgumentException("Debe indicar el paciente y el doctor.");
        }

        // 1. Buscar paciente y doctor
        Paciente patient = pacienteRepository.findById(dto.getPacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor no encontrado"));

        // 2. Validar las reglas de negocio
        if (!Boolean.TRUE.equals(doctor.getDisponible())) {
            throw new IllegalArgumentException("El doctor seleccionado no está disponible.");
        }
        String priority = normalize(dto.getNivelPrioridad());
        if (!PRIORITIES.contains(priority)) {
            throw new IllegalArgumentException("La prioridad debe ser ALTA, MEDIA o BAJA.");
        }
        if (dto.getMotivoConsulta() == null || dto.getMotivoConsulta().isBlank()) {
            throw new IllegalArgumentException("El motivo de la consulta es obligatorio.");
        }
        if (dto.getMontoConsulta() == null || dto.getMontoConsulta().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto de la consulta debe ser mayor que cero.");
        }

        // 3. Armar la cita con lo que genera el servidor y lo que vino en el DTO
        CitaMedica appointment = new CitaMedica();
        appointment.setCodigoCita(generateCode());
        appointment.setPaciente(patient);
        appointment.setDoctor(doctor);
        appointment.setNivelPrioridad(priority);
        appointment.setMotivoConsulta(dto.getMotivoConsulta().trim());
        appointment.setMontoConsulta(dto.getMontoConsulta());
        appointment.setEstado("PENDIENTE");
        appointment.setFechaCita(LocalDateTime.now());

        // 4. Guardar y 5. Convertir
        return toDTO(citaRepository.save(appointment));
    }

    // Cambia el estado de una cita que todavía no está cerrada
    @Transactional
    public CitaMedicaDTO actualizarEstado(Long id, String nuevoEstado) {
        String newState = normalize(nuevoEstado);
        if (!NEW_STATES.contains(newState)) {
            throw new IllegalArgumentException("Estado no válido: " + nuevoEstado);
        }

        CitaMedica appointment = citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        if (CLOSED_STATES.contains(appointment.getEstado())) {
            throw new IllegalArgumentException("La cita ya está cerrada y no se puede modificar.");
        }

        appointment.setEstado(newState);
        return toDTO(citaRepository.save(appointment));
    }

    // Genera un código con formato CIT-2026-XXXX que no exista todavía
    private String generateCode() {
        long next = citaRepository.count() + 1;
        String code = String.format("CIT-2026-%04d", next);
        while (citaRepository.existsByCodigoCita(code)) {
            next++;
            code = String.format("CIT-2026-%04d", next);
        }
        return code;
    }

    // Quita espacios y pasa a mayúsculas; si viene null devuelve texto vacío
    private String normalize(String value) {
        return value == null ? "" : value.trim().toUpperCase();
    }

    // Convierte la entidad en DTO aplanando paciente y doctor a sus nombres
    private CitaMedicaDTO toDTO(CitaMedica appointment) {
        CitaMedicaDTO result = new CitaMedicaDTO();
        result.setId(appointment.getId());
        result.setCodigoCita(appointment.getCodigoCita());
        result.setNombrePaciente(appointment.getPaciente().getNombreCompleto());
        result.setNombreDoctor(appointment.getDoctor().getNombre());
        result.setNivelPrioridad(appointment.getNivelPrioridad());
        result.setMotivoConsulta(appointment.getMotivoConsulta());
        result.setEstado(appointment.getEstado());
        result.setMontoConsulta(appointment.getMontoConsulta());
        result.setFechaCita(appointment.getFechaCita());
        return result;
    }
}