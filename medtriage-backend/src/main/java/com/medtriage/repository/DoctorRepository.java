package com.medtriage.repository;

import com.medtriage.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

// Acceso a datos de los doctores
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
}