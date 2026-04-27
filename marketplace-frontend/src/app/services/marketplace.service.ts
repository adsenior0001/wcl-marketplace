import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { EventSourcePolyfill } from 'event-source-polyfill';
import { environment } from '../../environments/environment'; // <-- Imported environment

export interface Product {
  id?: string;
  name: string;
  sku: string;
  price: number;
  stockQuantity: number;
}

@Injectable({
  providedIn: 'root',
})
export class MarketplaceService {
  // Pointing dynamically to our Azure Gateway via the environment file
  private readonly GATEWAY_URL = environment.apiUrl; // <-- The Localhost Ghost is banished

  constructor(private http: HttpClient) {}

  /**
   * Fetches the current inventory from the Product Service.
   * (Auth headers are automatically attached by jwt.interceptor.ts)
   */
  getProducts(): Observable<Product[]> {
    return this.http
      .get<Product[]>(`${this.GATEWAY_URL}/products`)
      .pipe(catchError(this.handleError));
  }

  /**
   * Submits a new product to the Product Service.
   */
  createProduct(productData: any): Observable<any> {
    return this.http
      .post(`${this.GATEWAY_URL}/products`, productData)
      .pipe(catchError(this.handleError));
  }

  /**
   * Submits a new order to the Order Service.
   */
  placeOrder(sku: string, quantity: number): Observable<string> {
    const payload = { sku, quantity };
    return this.http
      .post(`${this.GATEWAY_URL}/orders`, payload, {
        responseType: 'text', // Expecting a UUID string, not a JSON object
      })
      .pipe(catchError(this.handleError));
  }

  /**
   * Connects to the Server-Sent Events (SSE) stream for real-time inventory updates.
   * Uses EventSourcePolyfill to ensure the JWT token is sent with the connection.
   */
  listenToInventoryUpdates(): Observable<Product> {
    return new Observable((observer) => {
      // Grab the token manually for the stream
      const token = localStorage.getItem('jwt_token');

      // Use the Polyfill to attach the Authorization header
      const eventSource = new EventSourcePolyfill(
        `${this.GATEWAY_URL}/products/stream`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        },
      );

      // Listen for our specific "inventory-update" event emitted by the backend
      eventSource.addEventListener('inventory-update', (event: any) => {
        const updatedProduct = JSON.parse(event.data);
        observer.next(updatedProduct);
      });

      eventSource.onerror = (error) => {
        console.error('SSE Error:', error);
      };

      // Cleanup when the component unmounts or unsubscribes
      return () => eventSource.close();
    });
  }

  private handleError(error: any) {
    console.error('API Error:', error);
    return throwError(
      () => new Error(error.message || 'Server error occurred'),
    );
  }
}
