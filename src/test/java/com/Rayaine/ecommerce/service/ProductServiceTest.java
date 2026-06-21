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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
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




    @Test
    void getCatalogue_allFiltersProvided_getFilteredProducts(){
        Pageable pageable = PageRequest.of(0,10);
        Product.Status status = Product.Status.AVAILABLE;
        Double maxPrice = 30.0;
        Double minPrice = 15.0;
        Product product1 = new Product();
        product1.setStatus(Product.Status.AVAILABLE);
        product1.setProductPrice(20);
        Product product2 = new Product();
        product2.setProductPrice(22);
        product2.setStatus(Product.Status.AVAILABLE);
        Page<Product> expectedPage = new PageImpl<>(List.of(product1,product2));
        when(productRepository.findAll(any(Specification.class),eq(pageable))).thenReturn(expectedPage);
        Page<Product> returnedPage = productService.getCatalogue(status,minPrice,maxPrice,pageable);
        assertEquals(expectedPage,returnedPage);
    }


    @Test
    void getCatalogue_noProvidedFilters_getFilteredProducts(){
        Pageable pageable = PageRequest.of(0,10);
        Product product1 = new Product();
        Product product2 = new Product();
        Page<Product> expectedPage = new PageImpl<>(List.of(product1,product2));
        when(productRepository.findAll(any(Specification.class),eq(pageable))).thenReturn(expectedPage);
        Page<Product> returnedPage = productService.getCatalogue(null,null,null,pageable);
        assertEquals(expectedPage,returnedPage);
    }

    @Test
    void getCatalogue_statusProvided_getFilteredProducts(){
        Pageable pageable = PageRequest.of(0,10);
        Product.Status status = Product.Status.AVAILABLE;
        Product product1 = new Product();
        product1.setStatus(Product.Status.AVAILABLE);
        Product product2 = new Product();
        product2.setStatus(Product.Status.AVAILABLE);
        Page<Product> expectedPage = new PageImpl<>(List.of(product1,product2));
        when(productRepository.findAll(any(Specification.class),eq(pageable))).thenReturn(expectedPage);
        Page<Product> returnedPage = productService.getCatalogue(status,null,null,pageable);
        assertEquals(expectedPage,returnedPage);
    }

    @Test
    void getCatalogue_minPriceAndMaxPriceProvided_getFilteredProducts(){
        Pageable pageable = PageRequest.of(0,10);
        Double maxPrice = 30.0;
        Double minPrice = 15.0;
        Product product1 = new Product();
        product1.setProductPrice(20);
        Product product2 = new Product();
        product2.setProductPrice(22);
        Page<Product> expectedPage = new PageImpl<>(List.of(product1,product2));
        when(productRepository.findAll(any(Specification.class),eq(pageable))).thenReturn(expectedPage);
        Page<Product> returnedPage = productService.getCatalogue(null,minPrice,maxPrice,pageable);
        assertEquals(expectedPage,returnedPage);
    }


    @Test
    void getCatalogue_minPriceProvided_getFilteredProducts(){
        Pageable pageable = PageRequest.of(0,10);
        Double minPrice = 15.0;
        Product product1 = new Product();
        product1.setProductPrice(20);
        Product product2 = new Product();
        product2.setProductPrice(22);
        Page<Product> expectedPage = new PageImpl<>(List.of(product1,product2));
        when(productRepository.findAll(any(Specification.class),eq(pageable))).thenReturn(expectedPage);
        Page<Product> returnedPage = productService.getCatalogue(null,minPrice,null,pageable);
        assertEquals(expectedPage,returnedPage);
    }

    @Test
    void getCatalogue_maxPriceAndStatusProvided_getFilteredProducts(){
        Pageable pageable = PageRequest.of(0,10);
        Double maxPrice = 30.0;
        Product.Status status = Product.Status.AVAILABLE;
        Product product1 = new Product();
        product1.setStatus(Product.Status.AVAILABLE);
        product1.setProductPrice(20);
        Product product2 = new Product();
        product2.setStatus(Product.Status.AVAILABLE);
        product2.setProductPrice(22);
        Page<Product> expectedPage = new PageImpl<>(List.of(product1,product2));
        when(productRepository.findAll(any(Specification.class),eq(pageable))).thenReturn(expectedPage);
        Page<Product> returnedPage = productService.getCatalogue(status,null,maxPrice,pageable);
        assertEquals(expectedPage,returnedPage);
    }


    @Test
    void getCatalogue_maxPriceProvided_getFilteredProducts(){
        Pageable pageable = PageRequest.of(0,10);
        Double maxPrice = 30.0;
        Product product1 = new Product();
        product1.setProductPrice(20);
        Product product2 = new Product();
        product2.setProductPrice(22);
        Page<Product> expectedPage = new PageImpl<>(List.of(product1,product2));
        when(productRepository.findAll(any(Specification.class),eq(pageable))).thenReturn(expectedPage);
        Page<Product> returnedPage = productService.getCatalogue(null,null,maxPrice,pageable);
        assertEquals(expectedPage,returnedPage);
    }

    @Test
    void getCatalogue_minPriceAndStatusProvided_getFilteredProducts(){
        Pageable pageable = PageRequest.of(0,10);
        Double minPrice = 15.0;
        Product.Status status = Product.Status.AVAILABLE;
        Product product1 = new Product();
        product1.setStatus(Product.Status.AVAILABLE);
        product1.setProductPrice(20);
        Product product2 = new Product();
        product2.setStatus(Product.Status.AVAILABLE);
        product2.setProductPrice(22);
        Page<Product> expectedPage = new PageImpl<>(List.of(product1,product2));
        when(productRepository.findAll(any(Specification.class),eq(pageable))).thenReturn(expectedPage);
        Page<Product> returnedPage = productService.getCatalogue(status,minPrice,null,pageable);
        assertEquals(expectedPage,returnedPage);
    }

}
