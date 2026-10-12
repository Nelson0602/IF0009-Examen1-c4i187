package com.medtriage.repository;

import com.medtriage.model.CitaMedica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Acceso a datos de las citas, con consultas por prioridad y orden por fecha
@Repository
public interface CitaMedicaRepository extends JpaRepository<CitaMedica, Long> {

    List<CitaMedica> findByNivelPrioridadOrderByFechaCitaDesc(String nivelPrioridad);

    List<CitaMedica> findAllByOrderByFechaCitaDesc();

    boolean existsByCodigoCita(String codigoCita);
}
