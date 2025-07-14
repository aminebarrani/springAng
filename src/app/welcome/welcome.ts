import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

function parseJwt(token: string): any {
  try {
    return JSON.parse(atob(token.split('.')[1]));
  } catch {
    return null;
  }
}

@Component({
  selector: 'app-welcome',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './welcome.html',
  styleUrls: ['./welcome.css']
})
export class Welcome {
  isAdmin = false;
  username = '';
  router = inject(Router);

  constructor() {
    const token = localStorage.getItem('auth_token');
    if (token) {
      const payload = parseJwt(token);
      console.log('JWT payload:', payload); // Debug: See the payload
      this.isAdmin = payload?.role === 'ADMIN' || (Array.isArray(payload?.roles) && payload.roles.includes('ADMIN'));
      this.username = payload?.username || payload?.sub || '';
      console.log('isAdmin:', this.isAdmin); // Debug: See the computed isAdmin
    }
  }

  goToAdminPanel() {
    this.router.navigate(['/admin-panel']);
  }
}
