import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

import { ShelterService } from '../../services/shelter.service';

@Component({
  selector: 'app-shelters',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './shelters.component.html',
  styleUrls: ['./shelters.component.css']
})
export class SheltersComponent implements OnInit {
  shelters: any[] = [];
  loading = false;
  error = '';

  constructor(
    private shelterService: ShelterService,
    private router: Router
  ) {}

  ngOnInit() {
    this.loadShelters();
  }

  loadShelters() {
    this.loading = true;
    this.error = '';
    this.shelterService.getAllShelters().subscribe({
      next: (shelters) => {
        this.shelters = shelters;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading shelters:', error);
        this.error = 'Failed to load shelters. Please try again.';
        this.loading = false;
      }
    });
  }

  viewShelterDetails(id: string) {
    this.router.navigate(['/shelter', id]);
  }

  contactShelter(shelter: any) {
    // Navigate to inquiry form with shelter pre-selected
    this.router.navigate(['/inquiries'], { queryParams: { shelterId: shelter.id } });
  }

  donateToShelter(id: string) {
    // Navigate to donation page with shelter pre-selected
    this.router.navigate(['/donate'], { queryParams: { shelterId: id } });
  }
}
