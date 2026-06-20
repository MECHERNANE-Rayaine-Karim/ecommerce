package com.Rayaine.ecommerce.service;

import com.rayaine.ecommerce.exception.ProductNotFoundException;
import com.rayaine.ecommerce.model.Product;
import com.rayaine.ecommerce.repository.ProductRepository;
import com.rayaine.ecommerce.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;


    @InjectMocks
    private ProductService productService;

    @Test
    void deleteProduct_productNotFound_throwsException(){
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());
        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class,() -> {
            productService.deleteProduct(productId);
        });
        assertEquals( "product not found",exception.getMessage());
    }


    @Test
    void deleteProduct_productFound_productDeleted() {
        Long productId = 1L;
        Product product = new Product();
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        productService.deleteProduct(productId);
        verify(productRepository).delete(product);
    }





    @Test
    void changePrice_productNotFound_throwsException(){
        Long productId = 1L;
        double price = 12345;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());
        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class,() -> {
            productService.changePrice(productId,price);
        });
        assertEquals( "product not found",exception.getMessage());
    }


    @Test
    void changePrice_productFound_priceChanged(){
        Long productId = 1L;
        double price = 12345;
        Product product = new Product();
        product.setProductPrice(999.99);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        productService.changePrice(productId,price);
        assertEquals(price,product.getProductPrice());
        verify(productRepository).save(product);
    }





    @Test
    void changeStatus_productNotFound_throwsException(){
        Long productId = 1L;
        Product.Status status = Product.Status.AVAILABLE;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());
        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class,() -> {
            productService.changeStatus(productId,status);
        });
        assertEquals( "product not found",exception.getMessage());
    }

    @Test
    void changeStatus_productFound_statusChanged(){
        Long productId = 1L;
        Product.Status status = Product.Status.AVAILABLE;
        Product product = new Product();
        product.setStatus(Product.Status.DISCONTINUED);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        productService.changeStatus(productId,status);
        assertEquals(status,product.getStatus());
        verify(productRepository).save(product);
    }



}
