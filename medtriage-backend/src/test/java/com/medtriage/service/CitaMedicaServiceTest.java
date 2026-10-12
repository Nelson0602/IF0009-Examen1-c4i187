package com.medtriage.service;

import com.medtriage.dto.CitaMedicaDTO;
import com.medtriage.dto.CrearCitaDTO;
import com.medtriage.model.CitaMedica;
import com.medtriage.model.Doctor;
import com.medtriage.model.Paciente;
import com.medtriage.repository.CitaMedicaRepository;
import com.medtriage.repository.DoctorRepository;
import com.medtriage.repository.PacienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Pruebas unitarias de crearCita 
@ExtendWith(MockitoExtension.class)
class CitaMedicaServiceTest {

    @Mock
    private CitaMedicaRepository citaRepository;

    @Mock
    private PacienteRepository pacienteRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private CitaMedicaService service;

    private CrearCitaDTO dto;
    private Paciente patient;
    private Doctor doctor;

    // Datos
    @BeforeEach
    void setUp() {
        dto = new CrearCitaDTO();
        dto.setPacienteId(1L);
        dto.setDoctorId(1L);
        dto.setNivelPrioridad("ALTA");
        dto.setMotivoConsulta("Dolor de pecho");
        dto.setMontoConsulta(new BigDecimal("30000.00"));

        patient = new Paciente();
        patient.setId(1L);
        patient.setNombreCompleto("Elena Madrigal Castro");

        doctor = new Doctor();
        doctor.setId(1L);
        doctor.setNombre("Dr. Carlos Alvarado");
    }

    @Test
    void crearCita_Exito() {
        doctor.setDisponible(true);
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(citaRepository.count()).thenReturn(2L);
        when(citaRepository.save(any(CitaMedica.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CitaMedicaDTO result = service.crearCita(dto);

        assertNotNull(result);
        assertEquals("CIT-2026-0003", result.getCodigoCita());
        assertEquals("PENDIENTE", result.getEstado());
        assertEquals("Elena Madrigal Castro", result.getNombrePaciente());
        assertEquals("Dr. Carlos Alvarado", result.getNombreDoctor());
        verify(citaRepository, times(1)).save(any(CitaMedica.class));
    }

    @Test
    void crearCita_DoctorNoDisponible_LanzaExcepcion() {
        doctor.setDisponible(false);
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.crearCita(dto));

        assertEquals("El doctor seleccionado no está disponible.", ex.getMessage());
        verify(citaRepository, never()).save(any());
    }

    @Test
    void crearCita_PacienteNoExiste_LanzaExcepcion() {
        when(pacienteRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.crearCita(dto));

        assertEquals("Paciente no encontrado", ex.getMessage());
        verify(citaRepository, never()).save(any());
    }
}