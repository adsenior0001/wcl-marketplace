import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // 1. Ask the AuthService if the user has a VIP pass
  if (authService.isLoggedIn()) {
    return true; // Let them through!
  } else {
    // 2. If they don't, kick them back to the login page
    console.warn('>>> BOUNCER: Access Denied. Redirecting to Login.');
    router.navigate(['/login']);
    return false; // Block the route!
  }
};