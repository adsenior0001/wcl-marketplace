import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import { provideHttpClient, withInterceptors } from '@angular/common/http';

// 1. ADD THIS LINE: This tells Angular exactly where to find the interceptor
import { jwtInterceptor } from './core/interceptors/jwt.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),

    // 2. USE IT HERE: Now Angular knows what 'jwtInterceptor' is
    provideHttpClient(withInterceptors([jwtInterceptor])),
  ],
};
