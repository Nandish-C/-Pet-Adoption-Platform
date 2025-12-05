import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  email: string = '';
  password: string = '';
  error: string = '';

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit() {
    const user = { email: this.email, password: this.password };
    this.authService.login(user).subscribe({
      next: (response) => {
        const currentUser = this.authService.getCurrentUser();
        if (currentUser) {
          if (currentUser.role === 'ADMIN') {
            this.router.navigate(['/admin']);
          } else {
            this.router.navigate(['/']);
          }
        } else {
          this.error = 'Login failed: No user data received';
        }
      },
      error: (err) => {
        this.error = 'Login failed: ' + (err.error?.message || err.message);
      }
    });
  }
}
