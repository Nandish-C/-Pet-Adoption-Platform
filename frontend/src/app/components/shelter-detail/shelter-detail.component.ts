import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ShelterService } from '../../services/shelter.service';

@Component({
  selector: 'app-shelter-detail',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './shelter-detail.component.html',
  styleUrls: ['./shelter-detail.component.css']
})
export class ShelterDetailComponent implements OnInit {
  shelter: any = null;
  loading = false;
  error = '';
  currentShelterId = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private shelterService: ShelterService
  ) {}

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.currentShelterId = id;
      this.loadShelter(id);
    } else {
      this.router.navigate(['/shelters']);
    }
  }

  loadShelter(id: string) {
    this.loading = true;
    this.error = '';
    this.shelterService.getShelterById(id).subscribe({
      next: (shelter: any) => {
        this.shelter = shelter;
        this.loading = false;
      },
      error: (error: any) => {
        console.error('Error loading shelter:', error);
        this.error = 'Failed to load shelter details. Please try again.';
        this.loading = false;
      }
    });
  }

  retryLoad() {
    if (this.currentShelterId) {
      this.loadShelter(this.currentShelterId);
    }
  }

  contactShelter() {
    if (this.shelter) {
      this.router.navigate(['/inquiries'], { queryParams: { shelterId: this.shelter.id } });
    }
  }

  donateToShelter() {
    if (this.shelter) {
      this.router.navigate(['/donate'], { queryParams: { shelterId: this.shelter.id } });
    }
  }

  goBack() {
    this.router.navigate(['/shelters']);
  }
}
