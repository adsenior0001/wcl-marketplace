import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule], // FormsModule allows us to read input fields
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  // Inject required services
  authService = inject(AuthService);
  router = inject(Router);

  // --- UI STATE ---
  isLoginMode = true; // Determines if we show the Login or Register form
  errorMessage = '';

  // --- FORM DATA ---
  userData = {
    email: '',
    password: '',
    companyName: '',
    role: 'DISTRIBUTOR'
  };

  // --- METHODS ---

  // Toggles the UI between Login and Register modes
  toggleMode() {
    this.isLoginMode = !this.isLoginMode;
    this.errorMessage = ''; // Clear any existing errors when switching
  }

  // Executes when the user clicks the submit button
  onSubmit() {
    // 1. Determine which backend call to make based on the current mode
    const authObservable = this.isLoginMode 
      ? this.authService.login({ email: this.userData.email, password: this.userData.password })
      : this.authService.register(this.userData);

    // 2. Execute the call and wait for the response
    authObservable.subscribe({
      next: () => {
        // If successful, the token is already saved by the AuthService's tap() operator
        console.log('>>> SUCCESS! Authenticated and Token stored.');
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        console.error('>>> ERROR: Authentication failed', err);
        // Display a user-friendly error message on the screen
        this.errorMessage = this.isLoginMode 
          ? 'Login failed. Please check your email and password.' 
          : 'Registration failed. That email might already be in use.';
      }
    });
  }
}