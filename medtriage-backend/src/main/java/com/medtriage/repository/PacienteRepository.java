package com.medtriage.repository;

import com.medtriage.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

// Acceso a datos de los pacientes
public interface PacienteRepository extends JpaRepository<Paciente, Long> {
}