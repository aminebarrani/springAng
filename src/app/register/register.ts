import { Component, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-register',
  imports: [FormsModule, CommonModule],
  templateUrl: './register.html',
  styleUrl: './register.css'
})
export class Register {
  username = '';
  password = '';
  errorMessage = '';
  successMessage = '';
  email = '';
  showPassword = false;
  loading = false;
  role = '';

  http = inject(HttpClient);
  router = inject(Router);

  onRegister() {
    if (!this.email || !this.username || !this.password || !this.role) {
      this.errorMessage = 'Email, username, password, and role are required.';
      this.successMessage = '';
      return;
    }
    this.errorMessage = '';
    this.successMessage = '';
    this.loading = true;
    const registerUrl = 'http://localhost:8080/api/auth/register';
    this.http.post(registerUrl, { email: this.email, username: this.username, password: this.password, role: this.role })
      .subscribe({
        next: (res) => {
          this.successMessage = 'Registration successful! You can now log in.';
          this.errorMessage = '';
          this.loading = false;
          setTimeout(() => this.router.navigate(['/']), 1500);
        },
        error: (err) => {
          this.errorMessage = err.error?.message || 'Registration failed.';
          this.successMessage = '';
          this.loading = false;
        }
      });
  }

  goToLogin() {
    this.router.navigate(['/']);
  }
}
