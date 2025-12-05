import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PaymentService } from '../../services/payment.service';
import { DonationService } from '../../services/donation.service';
import { Payment } from './payment.model'; // Create a simple model if needed

@Component({
  selector: 'app-payment-history',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './payment-history.component.html',
  styleUrls: ['./payment-history.component.css']
})
export class PaymentHistoryComponent implements OnInit {
  payments: Payment[] = [];
  donations: any[] = [];
  combinedHistory: any[] = [];
  userId: string | null = null;
  loading = true;
  error: string | null = null;

  constructor(
    private paymentService: PaymentService,
    private donationService: DonationService
  ) {}

  ngOnInit(): void {
    this.userId = localStorage.getItem('userId');
    if (this.userId) {
      this.loadHistory();
    } else {
      this.error = 'User not authenticated';
      this.loading = false;
    }
  }

  loadHistory(): void {
    // Load both payments and donations
    Promise.all([
      this.paymentService.getUserPayments(this.userId!).toPromise(),
      this.donationService.getUserDonations(this.userId!).toPromise()
    ]).then(([payments, donations]) => {
      this.payments = payments || [];
      this.donations = donations || [];

      // Combine and sort by date (most recent first)
      this.combinedHistory = [
        ...this.payments.map(p => ({ ...p, type: 'payment', date: p.createdAt || new Date() })),
        ...this.donations.map(d => ({ ...d, type: 'donation', date: d.createdAt || new Date() }))
      ].sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime());

      this.loading = false;
    }).catch((err) => {
      this.error = 'Failed to load history: ' + err.message;
      this.loading = false;
    });
  }
}
