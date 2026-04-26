import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private http = inject(HttpClient);
  private readonly API_URL = `${environment.apiUrl}/products`;

  // Fetch all products from the backend
  getProducts(): Observable<any[]> {
    return this.http.get<any[]>(this.API_URL);
  }

  // Send a new product to the backend
  createProduct(productData: any): Observable<any> {
    return this.http.post<any>(this.API_URL, productData);
  }
}