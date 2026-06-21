package com.Rayaine.ecommerce.service;

import com.rayaine.ecommerce.exception.InvalidOrderOperationException;
import com.rayaine.ecommerce.exception.ProductNotFoundException;
import com.rayaine.ecommerce.exception.UnavailableProductException;
import com.rayaine.ecommerce.model.Order;
import com.rayaine.ecommerce.model.Product;
import com.rayaine.ecommerce.model.User;
import com.rayaine.ecommerce.repository.OrderItemRepository;
import com.rayaine.ecommerce.repository.OrderRepository;
import com.rayaine.ecommerce.repository.ProductRepository;
import com.rayaine.ecommerce.repository.UserRepository;
import com.rayaine.ecommerce.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {


    @Mock
    private UserRepository userRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private OrderService orderService;






    @Test
    void placeOrder_noSelectedProducts_throwsInvalidOrderOperationException(){
        Map<Long,Integer> selectedProducts = new HashMap<>();
        String destination = "destination";
        InvalidOrderOperationException exception = assertThrows(InvalidOrderOperationException.class,
                ()-> {orderService.placeOrder(selectedProducts,destination);});
        assertEquals("Order must contain at least on item",exception.getMessage());
    }
    @Test
    void placeOrder_productNotFound_throwsProductNotFoundException(){
        Map<Long,Integer> selectedProducts = new HashMap<>();
        String destination = "destination";
        Long productId = 1L;
        selectedProducts.put(1L,1);
        when(productRepository.findById(productId)).thenReturn(Optional.empty());
        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class,
                ()-> {orderService.placeOrder(selectedProducts,destination);});
        assertEquals("Product not found",exception.getMessage());
    }

    @Test
    void placeOrder_productNotAvailable_throwsUnavailableProductException(){
        Map<Long,Integer> selectedProducts = new HashMap<>();
        String destination = "destination";
        Long productId = 1L;
        Product product = new Product();
        product.setStatus(Product.Status.OUT_OF_STOCK);
        product.setProductId(productId);
        product.setProductName("product");
        selectedProducts.put(1L,1);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        UnavailableProductException exception = assertThrows(UnavailableProductException.class,
                ()-> {orderService.placeOrder(selectedProducts,destination);});
        assertEquals("Product " + product.getProductName() + " is not available",exception.getMessage());
    }

    @Test
    void placeOrder_productAvailable_orderSaved(){
        User mockUser = new User();
        mockUser.setUsername("username");
        mockUser.setUserId(1L);
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(mockUser));
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("username");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Map<Long,Integer> selectedProducts = new HashMap<>();
        String destination = "destination";
        Long productId = 1L;
        Product product = new Product();
        product.setStatus(Product.Status.AVAILABLE);
        product.setProductId(productId);
        product.setProductName("product");
        selectedProducts.put(1L,1);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        orderService.placeOrder(selectedProducts,destination);
        verify(orderRepository).save(any(Order.class));
    }

}
