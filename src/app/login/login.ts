import { Component } from '@angular/core';
import { inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  imports: [FormsModule, CommonModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {
  username = '';
  password = '';
  errorMessage = '';
  showPassword = false;
  loading = false;

  http = inject(HttpClient);
  router = inject(Router);

  onSubmit() {
    console.log('Login form submitted:', this.username, this.password);
    // TODO: Call AuthService to authenticate
    // this.authService.login(this.username, this.password).subscribe(...)
    // For now, just a placeholder
    if (!this.username || !this.password) {
      this.errorMessage = 'Username and password are required.';
      return;
    }
    this.errorMessage = '';
    this.loading = true;
    // Example: Replace with your backend URL
    const loginUrl = 'http://localhost:8080/api/auth/login';
    this.http.post<{token: string}>(loginUrl, { username: this.username, password: this.password })
      .subscribe({
        next: (res) => {
          localStorage.setItem('auth_token', res.token); // <--- THIS LINE IS CRITICAL
          console.log('Login success:', res);
          this.loading = false;
          this.router.navigate(['/welcome']);
        },
        error: (err) => {
          console.error('Login error:', err);
          this.loading = false;
          this.errorMessage = 'Invalid username or password.';
        }
      });
  }

  goToRegister() {
    this.router.navigate(['/register']);
  }
}
