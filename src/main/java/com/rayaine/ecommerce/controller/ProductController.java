package com.rayaine.ecommerce.controller;


import com.rayaine.ecommerce.model.Product;
import com.rayaine.ecommerce.service.ProductService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }


    @PostMapping("/addProduct")
    public ResponseEntity<?> addProduct(@RequestBody Map<String, String> request) {
        productService.addProduct(request.get("productName"), request.get("productDescription"), Double.valueOf(request.get("productPrice")), Product.Status.valueOf(request.get("status")));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/deleteProduct/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }


    @PatchMapping("/changePrice/{productId}")
    public ResponseEntity<?> changePrice(@PathVariable Long productId,@RequestParam double newPrice) {
        productService.changePrice(productId, newPrice);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/changeStatus/{productId}")
    public ResponseEntity<?> changeStatus(@PathVariable Long productId,@RequestParam Product.Status newStatus) {
        productService.changeStatus(productId, newStatus);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/getCatalogue")
    public ResponseEntity<?> getCatalogue(@RequestParam(required = false) Product.Status status, @RequestParam(required = false) Double minPrice, @RequestParam(required = false) Double maxPrice ,Pageable pageable) {
        return ResponseEntity.ok(productService.getCatalogue(status,minPrice,maxPrice,pageable));
    }
}
