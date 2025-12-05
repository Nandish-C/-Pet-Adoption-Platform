import { Component, OnInit, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';

import { ShelterService } from '../../services/shelter.service';
import { DonationService } from '../../services/donation.service';
import { AuthService } from '../../services/auth.service';

declare var Stripe: any;

@Component({
  selector: 'app-donate',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './donate.component.html',
  styleUrls: ['./donate.component.css']
})
export class DonateComponent implements OnInit, AfterViewInit {
  shelters: any[] = [];
  selectedShelterId = '';
  donationAmount = 0;
  donationFrequency = 'one-time';
  donationMessage = '';
  isAnonymous = false;
  processing = false;
  selectedPaymentMethod = 'card';

  stripe: any;
  card: any;
  clientSecret: string = '';
  paymentId: string = '';
  errorMessage: string = '';

  recentDonations: any[] = [];
  upiApps: string[] = ['PhonePe', 'Google Pay', 'Paytm', 'BHIM UPI'];

  constructor(
    private shelterService: ShelterService,
    private donationService: DonationService,
    private authService: AuthService,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.loadShelters();
    this.checkForShelterParam();
    this.loadRecentDonations();
  }

  ngAfterViewInit() {
    // Initialize Stripe after view is rendered
    this.initializeStripe();
  }

  loadShelters() {
    this.shelterService.getAllShelters().subscribe({
      next: (shelters) => {
        this.shelters = shelters;
      },
      error: (error) => {
        console.error('Error loading shelters:', error);
      }
    });
  }

  loadRecentDonations() {
    // Load recent donations from the backend
    this.donationService.getUserDonations('all').subscribe({
      next: (donations) => {
        // Show last 3 donations, anonymized
        this.recentDonations = donations
          .slice(-3)
          .reverse()
          .map(donation => ({
            amount: donation.amount,
            frequency: donation.frequency,
            donor: donation.isAnonymous ? 'Anonymous' : 'Anonymous' // Always show as anonymous for privacy
          }));
      },
      error: (error) => {
        console.error('Error loading recent donations:', error);
        // Fallback to sample data if API fails
        this.recentDonations = [
          { amount: 1000, frequency: 'one-time', donor: 'Anonymous' },
          { amount: 500, frequency: 'monthly', donor: 'Anonymous' },
          { amount: 2000, frequency: 'one-time', donor: 'Anonymous' }
        ];
      }
    });
  }

  checkForShelterParam() {
    this.route.queryParams.subscribe(params => {
      if (params['shelterId']) {
        this.selectedShelterId = params['shelterId'];
      }
    });
  }

  initializeStripe() {
    // Initialize Stripe (you'll need to add your publishable key)
    this.stripe = Stripe('pk_test_your_stripe_publishable_key_here'); // Replace with actual key

    const elements = this.stripe.elements();
    this.card = elements.create('card');
    this.card.mount('#card-element');

    this.card.on('change', (event: any) => {
      const displayError = document.getElementById('card-errors');
      if (displayError) {
        if (event.error) {
          displayError.textContent = event.error.message;
        } else {
          displayError.textContent = '';
        }
      }
    });
  }

  async processDonation() {
    if (!this.donationAmount || this.donationAmount <= 0 || this.processing) {
      return;
    }

    this.processing = true;
    this.errorMessage = '';

    try {
      const userId = localStorage.getItem('userId');
      if (!userId) {
        this.errorMessage = 'Please login to make a donation.';
        this.processing = false;
        return;
      }

      // Create donation payment intent with selected payment method
      let paymentIntentResponse;
      try {
        paymentIntentResponse = await this.donationService.createDonationIntent({
          amount: this.donationAmount,
          shelterId: this.selectedShelterId || null,
          frequency: this.donationFrequency,
          message: this.donationMessage,
          isAnonymous: this.isAnonymous,
          userId: userId,
          paymentMethod: this.selectedPaymentMethod
        }).toPromise();
      } catch (error: any) {
        console.error('Error creating donation intent:', error);
        let msg = 'Failed to initialize donation. Please try again.';
        if (error instanceof HttpErrorResponse) {
          if (error.status === 0) {
            msg = 'Network error. Please check your internet connection and try again.';
          } else if (error.status === 401) {
            msg = 'Session expired. Please login again.';
          } else if (error.status >= 500) {
            msg = 'Server error. Please try again later.';
          } else {
            msg = error.error?.message || `Error ${error.status}`;
          }
        }
        this.errorMessage = msg;
        this.processing = false;
        return;
      }

      if (this.selectedPaymentMethod === 'upi') {
        // For UPI, directly confirm payment
        this.paymentId = paymentIntentResponse.paymentId;
        try {
          await this.donationService.confirmDonation(this.paymentId).toPromise();
          alert('Thank you for your donation! Your support helps animals find loving homes.');
          this.resetForm();
        } catch (backendError: any) {
          console.error('Backend confirmation error:', backendError);
          this.errorMessage = 'UPI donation request failed. Please try again.';
          this.processing = false;
        }
        return;
      }

      // For card payment, continue with Stripe flow
      this.clientSecret = paymentIntentResponse.clientSecret;
      this.paymentId = paymentIntentResponse.paymentId;

      // Confirm payment with Stripe
      let result;
      try {
        result = await this.stripe.confirmCardPayment(this.clientSecret, {
          payment_method: {
            card: this.card,
          }
        });
      } catch (stripeError: any) {
        console.error('Stripe error:', stripeError);
        this.errorMessage = 'Payment processing failed. Please check your card details and try again.';
        this.processing = false;
        return;
      }

      if (result.error) {
        console.error('Stripe payment error:', result.error);
        this.errorMessage = result.error.message || 'Payment failed. Please try again.';
        this.processing = false;
      } else if (result.paymentIntent.status === 'succeeded') {
        // Payment successful, confirm with backend
        try {
          await this.donationService.confirmDonation(this.paymentId).toPromise();
          alert('Thank you for your donation! Your support helps animals find loving homes.');
          this.resetForm();
        } catch (backendError: any) {
          console.error('Backend confirmation error:', backendError);
          let msg = 'Payment succeeded but donation processing failed. Please contact support.';
          if (backendError instanceof HttpErrorResponse) {
            if (backendError.status === 0) {
              msg = 'Network error during confirmation. Please contact support with payment details.';
            } else if (backendError.status >= 500) {
              msg = 'Server error during donation. Please contact support.';
            } else {
              msg = backendError.error?.message || `Confirmation error ${backendError.status}`;
            }
          }
          this.errorMessage = msg;
          this.processing = false;
        }
      } else {
        this.errorMessage = 'Payment was not completed. Please try again.';
        this.processing = false;
      }
    } catch (error: any) {
      console.error('Unexpected donation error:', error);
      this.errorMessage = 'An unexpected error occurred. Please try again.';
      this.processing = false;
    }
  }

  resetForm() {
    this.donationAmount = 0;
    this.donationMessage = '';
    this.isAnonymous = false;
    this.processing = false;
    this.errorMessage = '';
    this.selectedPaymentMethod = 'card';
  }
}
