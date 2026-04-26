import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable, tap } from 'rxjs';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  
  // Base URL for user operations
  private readonly API_URL = `${environment.apiUrl}/users`;

  // -------------------------------------------------------------------
  // 1. REGISTRATION METHOD (POST /api/v1/users/register)
  // -------------------------------------------------------------------
  register(userData: any): Observable<any> {
    return this.http.post<any>(`${this.API_URL}/register`, userData).pipe(
      tap((response) => {
        if (response && response.token) {
          localStorage.setItem('jwt_token', response.token);
        }
      })
    );
  }

  // -------------------------------------------------------------------
  // 2. LOGIN METHOD (POST /api/v1/users/login)
  // -------------------------------------------------------------------
  login(credentials: any): Observable<any> {
    return this.http.post<any>(`${this.API_URL}/login`, credentials).pipe(
      tap((response) => {
        if (response && response.token) {
          localStorage.setItem('jwt_token', response.token);
        }
      })
    );
  }

  // -------------------------------------------------------------------
  // SESSION MANAGEMENT
  // -------------------------------------------------------------------
  getToken(): string | null {
    return localStorage.getItem('jwt_token');
  }

  // Checks if a token exists in the browser
  isLoggedIn(): boolean {
    return !!this.getToken(); // Converts the string (or null) into a boolean
  }

  // Destroys the token and sends the user back to the login screen
  logout(): void {
    localStorage.removeItem('jwt_token');
    this.router.navigate(['/login']);
  }
}