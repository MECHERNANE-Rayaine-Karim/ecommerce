package com.Rayaine.ecommerce.service;

import com.rayaine.ecommerce.dto.OrderDto;
import com.rayaine.ecommerce.dto.OrderItemDto;
import com.rayaine.ecommerce.exception.*;
import com.rayaine.ecommerce.model.Order;
import com.rayaine.ecommerce.model.OrderItem;
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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
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


    @BeforeEach
    void securitySetUp(){
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("username");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }



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

    @Test
    void cancelOrder_orderNotFound_throwsOrderNotFoundException(){
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
        OrderNotFoundException exception = assertThrows(OrderNotFoundException.class , () ->{
            orderService.cancelOrder(orderId);
        });
        assertEquals("order not found",exception.getMessage());
    }

    @Test
    void cancelOrder_UserDoesntOwnTheOrder_throwsUnauthorizedOrderAccessException() {
        User wrongUser = new User();
        wrongUser.setUsername("username");
        wrongUser.setUserId(1L);
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(wrongUser));
        User orderOwner = new User();
        orderOwner.setUsername("the owner");
        orderOwner.setUserId(2L);
        Long orderId = 1L;
        Order orderToCancel = new Order();
        orderToCancel.setUser(orderOwner);
        orderToCancel.setOrderId(orderId);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(orderToCancel));
        UnauthorizedOrderAccessException exception = assertThrows(UnauthorizedOrderAccessException.class , () ->{
            orderService.cancelOrder(orderId);
        });
        assertEquals("invalid operation",exception.getMessage());
    }


    @Test
    void cancelOrder_InvalidOrderStatus_throwsInvalidOrderOperationException(){
        User orderOwner = new User();
        orderOwner.setUsername("username");
        orderOwner.setUserId(2L);
        Long orderId = 1L;
        Order orderToCancel = new Order();
        orderToCancel.setUser(orderOwner);
        orderToCancel.setOrderId(orderId);
        orderToCancel.setStatus(Order.Status.DELIVERED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(orderToCancel));
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(orderOwner));
        InvalidOrderOperationException exception = assertThrows(InvalidOrderOperationException.class , () ->{
            orderService.cancelOrder(orderId);
        });
        assertEquals("invalid operation, order is already " + String.valueOf(orderToCancel.getStatus()),exception.getMessage());
    }

    @Test
    void cancelOrder_cancelationWindowExpired_throwsInvalidOrderOperationException(){
        User orderOwner = new User();
        orderOwner.setUsername("username");
        orderOwner.setUserId(2L);
        Long orderId = 1L;
        Order orderToCancel = new Order();
        orderToCancel.setUser(orderOwner);
        orderToCancel.setOrderId(orderId);
        orderToCancel.setStatus(Order.Status.PENDING);
        orderToCancel.setCreatedAt(LocalDateTime.now().minusDays(4));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(orderToCancel));
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(orderOwner));
        InvalidOrderOperationException exception = assertThrows(InvalidOrderOperationException.class , () ->{
            orderService.cancelOrder(orderId);
        });
        assertEquals("invalid operation, time allowed for cancelation is expire ",exception.getMessage());
    }



    @Test
    void cancelOrder_validConditions_orderStatusUpdatedToCanceled(){
        User orderOwner = new User();
        orderOwner.setUsername("username");
        orderOwner.setUserId(2L);
        Long orderId = 1L;
        Order orderToCancel = new Order();
        orderToCancel.setUser(orderOwner);
        orderToCancel.setOrderId(orderId);
        orderToCancel.setStatus(Order.Status.PENDING);
        orderToCancel.setCreatedAt(LocalDateTime.now().minusDays(1));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(orderToCancel));
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(orderOwner));
        orderService.cancelOrder(orderId);
        assertEquals( Order.Status.CANCELLED,orderToCancel.getStatus());
        verify(orderRepository).save(orderToCancel);
    }


    @Test
    void getOrderDetails_orderNotFound_throwsOrderNotFoundException(){
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
        OrderNotFoundException exception = assertThrows(OrderNotFoundException.class , () ->{
            orderService.getOrderDetails(orderId);
        });
        assertEquals("order not found",exception.getMessage());
    }

    @Test
    void getOrderDetails_UserDoesntOwnTheOrder_throwsUnauthorizedOrderAccessException() {
        User wrongUser = new User();
        wrongUser.setUsername("username");
        wrongUser.setUserId(1L);
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(wrongUser));
        User orderOwner = new User();
        orderOwner.setUsername("the owner");
        orderOwner.setUserId(2L);
        Long orderId = 1L;
        Order orderToCancel = new Order();
        orderToCancel.setUser(orderOwner);
        orderToCancel.setOrderId(orderId);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(orderToCancel));
        UnauthorizedOrderAccessException exception = assertThrows(UnauthorizedOrderAccessException.class , () ->{
            orderService.getOrderDetails(orderId);
        });
        assertEquals("invalid operation",exception.getMessage());
    }


    @Test
    void getOrderDetails_validConditions_returnsCorrectOrderDto(){
        User orderOwner = new User();
        orderOwner.setUsername("username");
        orderOwner.setUserId(2L);
        Long orderId = 1L;
        Order order= new Order();
        order.setUser(orderOwner);
        order.setOrderId(orderId);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(orderOwner));
        List<OrderItem> orderItemList = new ArrayList<>();
        Product product1 = new Product();
        product1.setProductId(1L);
        product1.setProductName("product1");
        product1.setProductPrice(100.0);
        OrderItem orderItem1 = new OrderItem();
        orderItem1.setItemId(1L);
        orderItem1.setProduct(product1);
        orderItem1.setPriceAtPurchase(product1.getProductPrice());
        orderItem1.setQuantity(3);
        orderItem1.setOrder(order);
        orderItemList.add(orderItem1);
        Product product2 = new Product();
        product2.setProductId(2L);
        product2.setProductName("product2");
        product2.setProductPrice(99.99);
        OrderItem orderItem2 = new OrderItem();
        orderItem2.setItemId(2L);
        orderItem2.setProduct(product2);
        orderItem2.setPriceAtPurchase(product2.getProductPrice());
        orderItem2.setQuantity(2);
        orderItem2.setOrder(order);
        orderItemList.add(orderItem2);
        when(orderItemRepository.findByOrder(order)).thenReturn(orderItemList);
        List<OrderItemDto> orderItemDtoList = new ArrayList<>();
        for( OrderItem orderItem : orderItemList){
            orderItemDtoList.add(new OrderItemDto(orderItem));
        }
        OrderDto expectedOrderDto = new OrderDto(order,orderItemDtoList);
        OrderDto returnedOrderDto = orderService.getOrderDetails(orderId);
        assertEquals( expectedOrderDto,returnedOrderDto);

    }







    @Test
    void getOrders_statusProvided_getFilteredOrders(){
        User orderOwner = new User();
        orderOwner.setUsername("username");
        orderOwner.setUserId(2L);
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(orderOwner));
        Pageable pageable = PageRequest.of(0,10);
        Order.Status status = Order.Status.DELIVERED;
        Order order1 = new Order();
        order1.setStatus(Order.Status.DELIVERED);
        OrderDto orderDto1 = new OrderDto(order1);
        Order order2 = new Order();
        order2.setStatus(Order.Status.DELIVERED);
        OrderDto orderDto2 = new OrderDto(order2);
        Page<OrderDto> expectedPage = new PageImpl<>(List.of(orderDto1,orderDto2),PageRequest.of(0,10),2);
        Page<Order> expectedOrdersPage = new PageImpl<>(List.of(order1,order2),PageRequest.of(0,10),2);
        when(orderRepository.findAll(any(Specification.class),eq(pageable))).thenReturn(expectedOrdersPage);
        Page<OrderDto> returnedPage = orderService.getOrders(status,pageable);
        assertEquals(expectedPage,returnedPage);
    }

    @Test
    void getorders_noFilterProvided_getAllOrders(){
        User orderOwner = new User();
        orderOwner.setUsername("username");
        orderOwner.setUserId(2L);
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(orderOwner));
        Pageable pageable = PageRequest.of(0,10);
        Order order1 = new Order();
        order1.setStatus(Order.Status.DELIVERED);
        OrderDto orderDto1 = new OrderDto(order1);
        Order order2 = new Order();
        order2.setStatus(Order.Status.DELIVERED);
        OrderDto orderDto2 = new OrderDto(order2);
        Page<OrderDto> expectedPage = new PageImpl<>(List.of(orderDto1,orderDto2),PageRequest.of(0,10),2);
        Page<Order> expectedOrdersPage = new PageImpl<>(List.of(order1,order2),PageRequest.of(0,10),2);
        when(orderRepository.findAll(any(Specification.class),eq(pageable))).thenReturn(expectedOrdersPage);
        Page<OrderDto> returnedPage = orderService.getOrders(null,pageable);
        assertEquals(expectedPage,returnedPage);
    }


}