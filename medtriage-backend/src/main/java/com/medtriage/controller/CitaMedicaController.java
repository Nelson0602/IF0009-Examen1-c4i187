package com.medtriage.controller;

import com.medtriage.dto.CitaMedicaDTO;
import com.medtriage.dto.CrearCitaDTO;
import com.medtriage.service.CitaMedicaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Controlador REST de las citas médicas, con CORS habilitado para Angular
@RestController
@RequestMapping("/api/v1/citas")
@CrossOrigin(origins = "http://localhost:4200")
public class CitaMedicaController {

    private final CitaMedicaService service;

    public CitaMedicaController(CitaMedicaService service) {
        this.service = service;
    }

    // GET /api/v1/citas
    @GetMapping
    public ResponseEntity<List<CitaMedicaDTO>> obtenerTodas() {
        return ResponseEntity.ok(service.obtenerTodas());
    }

    // GET /api/v1/citas/prioridad/ALTA
    @GetMapping("/prioridad/{prioridad}")
    public ResponseEntity<List<CitaMedicaDTO>> obtenerPorPrioridad(@PathVariable String prioridad) {
        return ResponseEntity.ok(service.obtenerPorPrioridad(prioridad));
    }

    // POST /api/v1/citas responde 201 cuando se crea
    @PostMapping
    public ResponseEntity<CitaMedicaDTO> crearCita(@RequestBody CrearCitaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearCita(dto));
    }

    // PATCH /api/v1/citas/1/estado?nuevoEstado=EN_ATENCION
    @PatchMapping("/{id}/estado")
    public ResponseEntity<CitaMedicaDTO> actualizarEstado(@PathVariable Long id,
            @RequestParam String nuevoEstado) {
        return ResponseEntity.ok(service.actualizarEstado(id, nuevoEstado));
    }
}