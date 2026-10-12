// Interfaces que copian los DTOs del backend

export interface Doctor {
  id?: number;
  nombre: string;
  especialidad: string;
  disponible: boolean;
}

export interface Paciente {
  id?: number;
  identificacion: string;
  nombreCompleto: string;
  correo: string;
  telefono: string;
}

// Igual que CitaMedicaDTO (salida del backend)
export interface CitaMedica {
  id?: number;
  codigoCita: string;
  nombrePaciente: string;
  nombreDoctor: string;
  nivelPrioridad: 'ALTA' | 'MEDIA' | 'BAJA';
  motivoConsulta: string;
  estado: 'PENDIENTE' | 'EN_ATENCION' | 'ATENDIDA' | 'CANCELADA';
  montoConsulta: number;
  fechaCita: string;
}

// Igual que CrearCitaDTO (entrada al backend)
export interface CrearCitaPayload {
  pacienteId: number;
  doctorId: number;
  nivelPrioridad: 'ALTA' | 'MEDIA' | 'BAJA';
  motivoConsulta: string;
  montoConsulta: number;
}