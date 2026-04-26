import { Component, OnInit, OnDestroy, inject, NgZone } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subscription } from 'rxjs';
import { MarketplaceService } from '../../services/marketplace.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
})
export class DashboardComponent implements OnInit, OnDestroy {
  // Modern dependency injection
  private marketplaceService = inject(MarketplaceService);

  // FIX: Explicitly declare ngZone as a private class property so TypeScript recognizes it
  private ngZone = inject(NgZone);
  private subscriptions: Subscription = new Subscription();

  // State Management
  products: any[] = [];
  loading: boolean = true;
  isProcessing: boolean = false;
  uiMessage: string | null = null;
  isError: boolean = false;

  // Data model for the "Add Product" form
  newProduct = {
    sku: '',
    name: '',
    description: '',
    basePrice: 0,
    stockQuantity: 0,
    technicalSpecifications: {},
  };

  ngOnInit(): void {
    // 1. Initial synchronous load
    this.loadProducts();

    // 2. Open the real-time SSE connection
    this.subscriptions.add(
      this.marketplaceService.listenToInventoryUpdates().subscribe({
        next: (updatedProduct: any) => {
          // Force Angular to wake up and apply the HTML updates
          this.ngZone.run(() => {
            // Look for the product in our UI array
            const index = this.products.findIndex(
              (p) => p.sku === updatedProduct.sku,
            );

            if (index !== -1) {
              // SCENARIO A: It exists! Update the stock.
              this.products[index].stockQuantity = updatedProduct.stockQuantity;
              console.log(
                `Live Update: ${updatedProduct.sku} stock is now ${updatedProduct.stockQuantity}`,
              );
            } else {
              // SCENARIO B: It doesn't exist! Someone in another window just created it.
              this.products.push(updatedProduct);
              console.log(
                `Live Add: New product ${updatedProduct.sku} appeared!`,
              );
            }
          });
        },
        error: (err: any) => {
          console.error('SSE Stream Error:', err);
        },
      }),
    );
  }

  ngOnDestroy(): void {
    // Prevent memory leaks when navigating away from the dashboard
    this.subscriptions.unsubscribe();
  }

  loadProducts(): void {
    this.loading = true;
    this.subscriptions.add(
      this.marketplaceService.getProducts().subscribe({
        next: (data: any) => {
          this.products = data;
          this.loading = false;
        },
        error: (err: any) => {
          this.showMessage(
            'Failed to load products. Check Gateway connection.',
            true,
          );
          this.loading = false;
          console.error(err);
        },
      }),
    );
  }

  onSubmit(): void {
    this.isProcessing = true;
    this.subscriptions.add(
      this.marketplaceService.createProduct(this.newProduct).subscribe({
        next: (createdProduct: any) => {
          // Add it immediately for the user who clicked submit
          this.products.push(createdProduct);
          this.showMessage(
            `Successfully created product: ${createdProduct.sku}`,
            false,
          );
          this.isProcessing = false;

          // Reset the form
          this.newProduct = {
            sku: '',
            name: '',
            description: '',
            basePrice: 0,
            stockQuantity: 0,
            technicalSpecifications: {},
          };
        },
        error: (err: any) => {
          this.showMessage('Failed to create product.', true);
          this.isProcessing = false;
          console.error(err);
        },
      }),
    );
  }

  buyProduct(sku: string): void {
    this.isProcessing = true;
    this.showMessage(`Processing order for ${sku}...`, false);

    this.subscriptions.add(
      this.marketplaceService.placeOrder(sku, 1).subscribe({
        next: (orderId: any) => {
          this.showMessage(`Success! Order placed. ID: ${orderId}`, false);
          this.isProcessing = false;
        },
        error: (err: any) => {
          this.showMessage(`Order failed. Verify authentication token.`, true);
          this.isProcessing = false;
          console.error(err);
        },
      }),
    );
  }

  // Unified message handler for UI feedback
  private showMessage(msg: string, isError: boolean): void {
    this.uiMessage = msg;
    this.isError = isError;

    if (!isError) {
      setTimeout(() => (this.uiMessage = null), 5000);
    }
  }
}
