import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// Define Pet interface
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
}

// Define Matching Criteria interface
interface MatchingCriteria {
  species?: string;
  breed?: string;
  ageRange?: { min: number; max: number };
  size?: string;
  energyLevel?: string;
  goodWithKids?: boolean;
  goodWithPets?: boolean;
  location?: string;
  budget?: number;
}

@Injectable({
  providedIn: 'root'
})
export class PetService {
  private apiUrl = 'http://localhost:8080/api/pets';

  constructor(private http: HttpClient) {}

  getPets(): Observable<Pet[]> {
    return this.http.get<Pet[]>(this.apiUrl);
  }

  addPet(pet: Pet): Observable<Pet> {
    return this.http.post<Pet>(this.apiUrl, pet);
  }

  updatePetStatus(id: string, status: string): Observable<Pet> {
    return this.http.put<Pet>(`${this.apiUrl}/${id}/status`, status, { headers: { 'Content-Type': 'application/json' } });
  }

  matchPets(species: string, age: number, location: string): Observable<Pet[]> {
    return this.http.get<Pet[]>(`${this.apiUrl}/match?species=${species}&age=${age}&location=${location}`);
  }

  // Advanced matching with multiple criteria
  advancedMatch(criteria: MatchingCriteria): Observable<Pet[]> {
    const params = new URLSearchParams();
    if (criteria.species) params.append('species', criteria.species);
    if (criteria.breed) params.append('breed', criteria.breed);
    if (criteria.ageRange) {
      params.append('minAge', criteria.ageRange.min.toString());
      params.append('maxAge', criteria.ageRange.max.toString());
    }
    if (criteria.size) params.append('size', criteria.size);
    if (criteria.energyLevel) params.append('energyLevel', criteria.energyLevel);
    if (criteria.goodWithKids !== undefined) params.append('goodWithKids', criteria.goodWithKids.toString());
    if (criteria.goodWithPets !== undefined) params.append('goodWithPets', criteria.goodWithPets.toString());
    if (criteria.location) params.append('location', criteria.location);
    if (criteria.budget) params.append('budget', criteria.budget.toString());

    return this.http.get<Pet[]>(`${this.apiUrl}/advanced-match?${params.toString()}`);
  }

  // Get pet compatibility score
  getCompatibilityScore(petId: string, userPreferences: MatchingCriteria): Observable<{ score: number; reasons: string[] }> {
    return this.http.post<{ score: number; reasons: string[] }>(`${this.apiUrl}/${petId}/compatibility`, userPreferences);
  }

  // Get recommended pets based on user profile
  getRecommendations(userId: string): Observable<Pet[]> {
    return this.http.get<Pet[]>(`${this.apiUrl}/recommendations/${userId}`);
  }

  // Search pets with filters
  searchPets(filters: {
    query?: string;
    species?: string;
    breed?: string;
    ageMin?: number;
    ageMax?: number;
    size?: string;
    location?: string;
    status?: string;
  }): Observable<Pet[]> {
    const params = new URLSearchParams();
    Object.entries(filters).forEach(([key, value]) => {
      if (value !== undefined && value !== null && value !== '') {
        params.append(key, value.toString());
      }
    });
    return this.http.get<Pet[]>(`${this.apiUrl}/search?${params.toString()}`);
  }

  // Get pets by shelter
  getPetsByShelter(shelterId: string): Observable<Pet[]> {
    return this.http.get<Pet[]>(`${this.apiUrl}/shelter/${shelterId}`);
  }

  // Get pet details with full information
  getPetDetails(id: string): Observable<Pet> {
    return this.http.get<Pet>(`${this.apiUrl}/${id}/details`);
  }
}
