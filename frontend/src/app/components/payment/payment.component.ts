import { Component, OnInit, AfterViewInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { PaymentService } from '../../services/payment.service';
import { AdoptionService } from '../../services/adoption.service';
import { PetService } from '../../services/pet.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

declare var Stripe: any;

interface Pet {
  id: string;
  name: string;
  species: string;
  breed: string;
  age: number;
  description: string;
  status: string;
  shelterId?: string;
  videoUrl?: string;
  imageUrl?: string;
  size?: string;
  energyLevel?: string;
  goodWithKids?: boolean;
  goodWithPets?: boolean;
  specialNeeds?: boolean;
  adoptionFee?: number;
  price?: number;
  displayPrice?: string;
  currency?: string;
}

@Component({
  selector: 'app-payment',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './payment.component.html',
  styleUrls: ['./payment.component.css']
})
export class PaymentComponent implements OnInit, AfterViewInit {
  petId: string = '';
  pet: Pet | null = null;
  stripe: any;
  card: any;
  clientSecret: string = '';
  paymentId: string = '';
  isProcessing: boolean = false;
  errorMessage: string = '';
  selectedPaymentMethod: string = 'card';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private paymentService: PaymentService,
    private adoptionService: AdoptionService,
    private petService: PetService
  ) {}

  ngOnInit() {
    this.petId = this.route.snapshot.paramMap.get('petId') || '';
    if (!this.petId) {
      this.router.navigate(['/']);
      return;
    }

    const userId = localStorage.getItem('userId');
    if (!userId) {
      this.router.navigate(['/login']);
      return;
    }

    // Load pet details
    this.petService.getPetDetails(this.petId).subscribe({
      next: (pet: Pet) => {
        this.pet = pet;
        if (pet.status === 'ADOPTED') {
          alert('This pet has already been adopted.');
          this.router.navigate(['/']);
          return;
        }
      },
      error: (err: any) => {
        console.error('Error loading pet:', err);
        this.router.navigate(['/']);
      }
    });
  }

  ngAfterViewInit() {
    // Initialize Stripe after view is rendered
    if (this.pet) {
      this.initializeStripe();
    }
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

  onPaymentMethodChange() {
    // Reset error message when payment method changes
    this.errorMessage = '';
  }

  async processPayment() {
    if (!this.pet || this.isProcessing) return;

    this.isProcessing = true;
    this.errorMessage = '';

    try {
      const userId = localStorage.getItem('userId');
      if (!userId) {
        this.errorMessage = 'Please login to adopt a pet.';
        this.isProcessing = false;
        return;
      }

      // Create payment intent with selected payment method
      let paymentIntentResponse;
      try {
        paymentIntentResponse = await this.paymentService.createPaymentIntent({
          petId: this.petId,
          amount: this.pet.price || this.pet.adoptionFee || 0,
          userId: userId,
          paymentMethod: this.selectedPaymentMethod
        }).toPromise();
      } catch (error: any) {
        console.error('Error creating payment intent:', error);
        let msg = 'Failed to initialize payment. Please try again.';
        if (error instanceof HttpErrorResponse) {
          if (error.status === 0) {
            msg = 'Network error. Please check your internet connection and try again.';
          } else if (error.status === 401) {
            msg = 'Session expired. Please login again.';
            this.router.navigate(['/login']);
          } else if (error.status >= 500) {
            msg = 'Server error. Please try again later.';
          } else {
            msg = error.error?.message || `Error ${error.status}`;
          }
        }
        this.errorMessage = msg;
        this.isProcessing = false;
        return;
      }

      if (this.selectedPaymentMethod === 'cod') {
        // For COD, directly confirm payment
        this.paymentId = paymentIntentResponse.paymentId;
        try {
          await this.paymentService.confirmPayment(this.paymentId).toPromise();
          alert('Adoption request submitted! You will pay cash on delivery.');
          this.router.navigate(['/adoptions']);
        } catch (backendError: any) {
          console.error('Backend confirmation error:', backendError);
          this.errorMessage = 'COD adoption request failed. Please try again.';
          this.isProcessing = false;
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
        this.isProcessing = false;
        return;
      }

      if (result.error) {
        console.error('Stripe payment error:', result.error);
        this.errorMessage = result.error.message || 'Payment failed. Please try again.';
        this.isProcessing = false;
      } else if (result.paymentIntent.status === 'succeeded') {
        // Payment successful, confirm with backend (which handles adoption and pet status update)
        try {
          await this.paymentService.confirmPayment(this.paymentId).toPromise();
          // Redirect to success page or my adoptions
          alert('Adoption successful! The pet is now yours.');
          this.router.navigate(['/adoptions']); // Assuming /adoptions is the my adoptions page
        } catch (backendError: any) {
          console.error('Backend confirmation error:', backendError);
          let msg = 'Payment succeeded but adoption processing failed. Please contact support.';
          if (backendError instanceof HttpErrorResponse) {
            if (backendError.status === 0) {
              msg = 'Network error during confirmation. Please contact support with payment details.';
            } else if (backendError.status === 409) {
              msg = 'Pet may have been adopted by someone else. Please check and try another pet.';
            } else if (backendError.status >= 500) {
              msg = 'Server error during adoption. Please contact support.';
            } else {
              msg = backendError.error?.message || `Confirmation error ${backendError.status}`;
            }
          }
          this.errorMessage = msg;
          this.isProcessing = false;
        }
      } else {
        this.errorMessage = 'Payment was not completed. Please try again.';
        this.isProcessing = false;
      }
    } catch (error: any) {
      console.error('Unexpected payment error:', error);
      this.errorMessage = 'An unexpected error occurred. Please try again.';
      this.isProcessing = false;
    }
  }

  cancelPayment() {
    this.router.navigate(['/']);
  }
}
