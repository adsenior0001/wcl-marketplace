import { Routes } from '@angular/router';
import { LoginComponent } from './features/login/login.component';
import { DashboardComponent } from './features/dashboard/dashboard.component';
import { authGuard } from './core/guards/auth.guard'; // 1. Import the Bouncer

export const routes: Routes = [
  // If the user goes to the default URL, redirect them to login
  { path: '', redirectTo: 'login', pathMatch: 'full' },

  // If the URL is /login, show the LoginComponent
  { path: 'login', component: LoginComponent },

  // If the URL is /dashboard, show the DashboardComponent
  { 
    path: 'dashboard', 
    component: DashboardComponent,
    canActivate: [authGuard] 
  }
];
