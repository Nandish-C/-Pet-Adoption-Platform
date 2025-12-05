import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DonationService {
  private apiUrl = 'http://localhost:8080/api/donations';

  constructor(private http: HttpClient) {}

  createDonationIntent(donationRequest: {
    amount: number;
    shelterId: string | null;
    frequency: string;
    message: string;
    isAnonymous: boolean;
    userId: string;
    paymentMethod?: string;
  }): Observable<any> {
    return this.http.post(`${this.apiUrl}/create-intent`, donationRequest);
  }

  confirmDonation(paymentId: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/confirm/${paymentId}`, {});
  }

  processDonation(paymentRequest: any): Observable<any> {
    return this.http.post(this.apiUrl, paymentRequest);
  }

  getUserDonations(userId: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/user/${userId}`);
  }
}
