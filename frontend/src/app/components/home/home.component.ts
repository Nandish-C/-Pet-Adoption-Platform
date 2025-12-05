import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { PetService } from '../../services/pet.service';
import { AdoptionService } from '../../services/adoption.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  pets: any[] = [];
  loading = true;
  error: string | null = null;
  selectedPetForModal: any = null;

  constructor(
    private petService: PetService,
    private adoptionService: AdoptionService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    this.petService.getPets().subscribe({
      next: (pets) => {
        // Filter to only available pets and remove duplicates by id
        this.pets = pets
          .filter(pet => pet.status && pet.status.toLowerCase() === 'available')
          .filter((pet, index, self) => self.findIndex(p => p.id === pet.id) === index);
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load pets: ' + err.message;
        this.loading = false;
      }
    });
  }

  adoptPet(petId: string) {
    const userId = this.authService.getCurrentUser()?.id;
    if (!userId) {
      this.router.navigate(['/login']);
      return;
    }

    // Navigate directly to payment page; adoption handled after successful payment
    this.router.navigate(['/payment', petId]);
  }

  buyPet(petId: string) {
    // Same as adoptPet but for buying
    this.adoptPet(petId);
  }

  openImageModal(pet: any) {
    this.selectedPetForModal = pet;
  }

  closeImageModal() {
    this.selectedPetForModal = null;
  }
}
