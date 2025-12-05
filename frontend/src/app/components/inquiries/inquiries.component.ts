import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { InquiryService } from '../../services/inquiry.service';
import { PetService } from '../../services/pet.service';
import { ShelterService } from '../../services/shelter.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-inquiries',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './inquiries.component.html',
  styleUrls: ['./inquiries.component.css']
})
export class InquiriesComponent implements OnInit {
  inquiries: any[] = [];
  availablePets: any[] = [];
  shelters: any[] = [];
  loading = false;
  error = '';

  // Form fields
  selectedPetId = '';
  selectedShelterId = '';
  inquiryMessage = '';
  submitting = false;

  constructor(
    private inquiryService: InquiryService,
    private petService: PetService,
    private shelterService: ShelterService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    this.loadInquiries();
    this.loadAvailablePets();
    this.loadShelters();
  }

  loadInquiries() {
    this.loading = true;
    this.error = '';
    this.inquiryService.getAllInquiries().subscribe({
      next: (inquiries) => {
        this.inquiries = inquiries;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading inquiries:', error);
        this.error = 'Failed to load inquiries. Please try again.';
        this.loading = false;
      }
    });
  }

  loadAvailablePets() {
    this.petService.getPets().subscribe({
      next: (pets: any[]) => {
        this.availablePets = pets.filter((pet: any) => pet.status === 'available');
      },
      error: (error: any) => {
        console.error('Error loading pets:', error);
      }
    });
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

  submitInquiry() {
    if (!this.selectedPetId || !this.selectedShelterId || !this.inquiryMessage) {
      alert('Please fill in all required fields.');
      return;
    }

    this.submitting = true;

    const inquiryData = {
      petId: this.selectedPetId,
      shelterId: this.selectedShelterId,
      message: this.inquiryMessage,
      userId: this.authService.getCurrentUser()?.id
    };

    this.inquiryService.submitInquiry(inquiryData).subscribe({
      next: (response) => {
        this.submitting = false;
        alert('Inquiry submitted successfully!');
        this.resetForm();
        this.loadInquiries(); // Refresh the list
      },
      error: (error) => {
        this.submitting = false;
        console.error('Error submitting inquiry:', error);
        alert('Failed to submit inquiry. Please try again.');
      }
    });
  }

  resetForm() {
    this.selectedPetId = '';
    this.selectedShelterId = '';
    this.inquiryMessage = '';
  }

  getStatusBadgeClass(status: string): string {
    if (!status) return 'bg-secondary';
    switch (status.toLowerCase()) {
      case 'pending':
        return 'bg-warning text-dark';
      case 'responded':
        return 'bg-info';
      case 'accepted':
        return 'bg-success';
      case 'rejected':
        return 'bg-danger';
      default:
        return 'bg-secondary';
    }
  }

  viewInquiryDetails(inquiryId: string) {
    // Navigate to inquiry details page
    this.router.navigate(['/inquiry', inquiryId]);
  }

  startChat(inquiryId: string) {
    // Navigate to chat with this inquiry
    this.router.navigate(['/chat'], { queryParams: { inquiryId } });
  }
}
