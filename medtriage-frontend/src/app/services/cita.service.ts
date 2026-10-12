import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import { CitaMedica, CrearCitaPayload } from '../models/cita.model';

// Servicio que consume la API REST de citas médicas
@Injectable({ providedIn: 'root' })
export class CitaService {
  private http = inject(HttpClient);
  private baseUrl = environment.API_URL + 'citas';

  getCitas(): Observable<CitaMedica[]> {
    return this.http.get<CitaMedica[]>(this.baseUrl);
  }

  getCitasPorPrioridad(prioridad: string): Observable<CitaMedica[]> {
    return this.http.get<CitaMedica[]>(this.baseUrl + '/prioridad/' + prioridad);
  }

  crearCita(payload: CrearCitaPayload): Observable<CitaMedica> {
    return this.http.post<CitaMedica>(this.baseUrl, payload);
  }

  // El nuevo estado viaja después del ? como RequestParam
  actualizarEstado(id: number, estado: string): Observable<CitaMedica> {
    return this.http.patch<CitaMedica>(
      this.baseUrl + '/' + id + '/estado?nuevoEstado=' + estado, {});
  }
}