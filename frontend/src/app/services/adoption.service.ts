import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AdoptionService {
  private apiUrl = 'http://localhost:8080/api/adoptions';

  constructor(private http: HttpClient) {}

  getAllAdoptions(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  getUserAdoptions(userId: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/user/${userId}`);
  }

  submitAdoption(adoption: any): Observable<any> {
    return this.http.post(this.apiUrl, adoption);
  }
}
