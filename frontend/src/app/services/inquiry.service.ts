import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class InquiryService {
  private apiUrl = 'http://localhost:8080/api/inquiries';

  constructor(private http: HttpClient) {}

  getAllInquiries(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  submitInquiry(inquiry: any): Observable<any> {
    return this.http.post(this.apiUrl, inquiry);
  }

  getInquiriesByPet(petId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/pet/${petId}`);
  }

  addInquiry(inquiry: any): Observable<any> {
    return this.http.post(this.apiUrl, inquiry);
  }
}
