package com.rayaine.ecommerce.controller;



import com.rayaine.ecommerce.dto.PlaceOrderRequest;
import com.rayaine.ecommerce.model.Order;
import com.rayaine.ecommerce.service.OrderService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("api/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }


    @PostMapping("/placeOrder")
    public ResponseEntity<?> placeOrder(@RequestBody PlaceOrderRequest request ) {
        orderService.placeOrder(request.getSelectedProducts(),request.getDestination());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/cancelOrder/{orderId}")
    public ResponseEntity<?> cancelOrder(@PathVariable Long orderId ) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/getOrders")
    public ResponseEntity<?> getOrders(@RequestParam(required = false) Order.Status status , Pageable pageable ) {
        return ResponseEntity.ok(orderService.getOrders(status,pageable));
    }



    @GetMapping("/getOrderDetails/{orderId}")
    public ResponseEntity<?> getOrderDetails(@PathVariable Long orderId ) {
        return ResponseEntity.ok(orderService.getOrderDetails(orderId));
    }



}
