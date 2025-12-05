import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { PetService } from '../../services/pet.service';

@Component({
  selector: 'app-match',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './match.component.html',
  styleUrls: ['./match.component.css']
})
export class MatchComponent {
  matchingCriteria: any = {
    species: '',
    breed: '',
    ageRange: { min: 0, max: 20 },
    size: '',
    energyLevel: '',
    goodWithKids: false,
    goodWithPets: false,
    budget: 0
  };
  matchedPets: any[] = [];
  isLoading = false;
  selectedPetForModal: any = null;

  constructor(
    private petService: PetService,
    private router: Router
  ) {}

  findMatches() {
    this.isLoading = true;
    this.petService.advancedMatch(this.matchingCriteria).subscribe({
      next: (pets) => {
        this.matchedPets = pets;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error finding matches:', error);
        this.isLoading = false;
        // Handle error (show message to user)
      }
    });
  }

  viewPetDetails(id: string) {
    // Navigate to home page where pet details are shown
    this.router.navigate(['/home']);
  }

  adoptPet(id: string) {
    // Navigate to payment page for adoption
    this.router.navigate(['/payment', id]);
  }

  openImageModal(pet: any) {
    this.selectedPetForModal = pet;
  }

  closeImageModal() {
    this.selectedPetForModal = null;
  }
}
