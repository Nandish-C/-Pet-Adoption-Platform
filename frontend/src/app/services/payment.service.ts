import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PaymentService {
  private apiUrl = 'http://localhost:8080/api/payments';

  constructor(private http: HttpClient) {}

  createPaymentIntent(request: { petId: string; amount: number; userId: string; paymentMethod?: string }): Observable<any> {
    return this.http.post(`${this.apiUrl}/create-intent`, request);
  }

  confirmPayment(paymentId: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/confirm/${paymentId}`, {});
  }

  getUserPayments(userId: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/user/${userId}`);
  }
}
