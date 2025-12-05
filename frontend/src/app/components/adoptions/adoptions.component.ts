import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AdoptionService } from '../../services/adoption.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-adoptions',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './adoptions.component.html',
  styleUrls: ['./adoptions.component.css']
})
export class AdoptionsComponent implements OnInit {
  adoptions: any[] = [];
  loading = true;
  error: string | null = null;

  constructor(
    private adoptionService: AdoptionService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const userId = this.authService.getCurrentUser()?.id;
    if (!userId) {
      this.router.navigate(['/login']);
      return;
    }

    this.adoptionService.getUserAdoptions(userId).subscribe({
      next: (adoptions) => {
        this.adoptions = adoptions;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load adoptions: ' + err.message;
        this.loading = false;
      }
    });
  }
}
