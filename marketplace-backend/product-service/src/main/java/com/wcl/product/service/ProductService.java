package com.wcl.product.service;

import com.wcl.product.dto.ProductRequestDTO;
import com.wcl.product.dto.ProductResponseDTO;
import com.wcl.product.entity.Product;
import com.wcl.product.repository.ProductRepository;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    // The Reactive Sink acts as our radio tower to broadcast inventory updates.
    // We multicast the DTO so the frontend gets exactly the format it expects.
    private final Sinks.Many<ProductResponseDTO> inventorySink = Sinks.many().multicast().onBackpressureBuffer();

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductResponseDTO createProduct(ProductRequestDTO request) {
        // 1. Map DTO to Entity
        Product product = Product.builder()
                .sku(request.sku())
                .name(request.name())
                .description(request.description())
                .basePrice(request.basePrice())
                .stockQuantity(request.stockQuantity())
                .technicalSpecifications(request.technicalSpecifications())
                .build();

        // 2. Save to MongoDB
        Product savedProduct = productRepository.save(product);

        // 3. Map Entity back to Response DTO
        ProductResponseDTO dto = mapToDTO(savedProduct);

        // 4. NEW: Broadcast the newly created product to the live stream!
        inventorySink.tryEmitNext(dto);

        return dto;
    }

    public List<ProductResponseDTO> getAllProducts() {
        // Fetch all and convert each to a DTO
        return productRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ProductResponseDTO getProductById(String id) {
        // Find the product, or throw an error if the ID doesn't exist
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));

        return mapToDTO(product);
    }

    public void decrementInventory(String sku, Integer quantityToDeduct) {
        // 1. Find the product
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new RuntimeException("Product not found for SKU: " + sku));

        // 2. Check if we have enough stock
        if (product.getStockQuantity() < quantityToDeduct) {
            // Note: In a massive production system, we would fire a "Failed" event back to Kafka here
            throw new RuntimeException("Insufficient stock for SKU: " + sku);
        }

        // 3. Deduct the stock and save
        product.setStockQuantity(product.getStockQuantity() - quantityToDeduct);
        Product savedProduct = productRepository.save(product);

        System.out.println(">>> SUCCESS: Inventory updated. New stock for " + sku + ": " + savedProduct.getStockQuantity());

        // 4. Broadcast the new inventory level to the SSE Sink
        inventorySink.tryEmitNext(mapToDTO(savedProduct));
    }

    // Exposes the live stream for the Controller to consume
    public Flux<ServerSentEvent<ProductResponseDTO>> getInventoryStream() {
        return inventorySink.asFlux()
                .map(dto -> ServerSentEvent.<ProductResponseDTO>builder()
                        .event("inventory-update")
                        .data(dto)
                        .build());
    }

    // Helper method to keep code DRY (Don't Repeat Yourself)
    private ProductResponseDTO mapToDTO(Product product) {
        return new ProductResponseDTO(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getBasePrice(),
                product.getStockQuantity(),
                product.getTechnicalSpecifications()
        );
    }
}