import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  name: string = '';
  email: string = '';
  password: string = '';
  role: string = 'USER';
  error: string = '';
  success: string = '';

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit() {
    const user = { name: this.name, email: this.email, password: this.password, role: this.role };
    this.authService.register(user).subscribe({
      next: (response) => {
        this.success = response.message || 'Registration successful. Please login.';
        // Clear form
        this.name = '';
        this.email = '';
        this.password = '';
        this.role = 'USER';
        this.error = '';
        // Redirect to login after 2 seconds
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 2000);
      },
      error: (err) => {
        this.error = err.error || 'Registration failed. Please try again.';
        this.success = '';
      }
    });
  }
}
