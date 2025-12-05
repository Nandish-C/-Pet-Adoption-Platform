import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ShelterService {
  private apiUrl = 'http://localhost:8080/api/shelters';

  constructor(private http: HttpClient) {}

  getAllShelters(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  getShelterById(id: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }

  addShelter(shelter: any): Observable<any> {
    return this.http.post(this.apiUrl, shelter);
  }
}
