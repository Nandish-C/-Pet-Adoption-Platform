import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AdminGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(): boolean {
    const currentUser = this.authService.getCurrentUser();
    const token = localStorage.getItem('authToken');
    const userRole = localStorage.getItem('userRole');
    console.log('AdminGuard check - currentUser:', currentUser);
    console.log('AdminGuard check - token exists:', !!token);
    console.log('AdminGuard check - userRole from localStorage:', userRole);

    if (currentUser && currentUser.role === 'ADMIN') {
      console.log('Admin access granted');
      return true;
    } else if (userRole === 'ADMIN') {
      console.log('Admin access granted via localStorage');
      return true;
    } else {
      console.log('Admin access denied, redirecting to login');
      this.router.navigate(['/login']);
      return false;
    }
  }
}