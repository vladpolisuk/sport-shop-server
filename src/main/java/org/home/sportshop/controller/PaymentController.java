package org.home.sportshop.controller;

import java.math.BigDecimal;
import java.util.Map;

import org.home.sportshop.logging.LoggingService;
import org.home.sportshop.payment.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    
    private final PaymentService paymentService;
    private final LoggingService logger = LoggingService.getInstance();
    
    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
    
    @GetMapping("/methods")
    public ResponseEntity<Map<String, String>> getPaymentMethods() {
        return ResponseEntity.ok(paymentService.getAvailablePaymentMethods());
    }
    
    @GetMapping("/methods/ids")
    public ResponseEntity<Map<Long, String>> getPaymentMethodsWithIds() {
        return ResponseEntity.ok(paymentService.getAvailablePaymentMethodsWithIds());
    }
    
    @PostMapping("/process")
    public ResponseEntity<Map<String, Object>> processPayment(@RequestBody PaymentRequest request) {
        logger.logInfo("Получен запрос на проведение платежа: " + request);
        
        boolean success = paymentService.processPayment(
            request.getPaymentMethod(),
            request.getOrderId(),
            request.getAmount()
        );
        
        Map<String, Object> response = Map.of(
            "success", success,
            "orderId", request.getOrderId(),
            "paymentMethod", request.getPaymentMethod(),
            "message", success ? "Платеж успешно обработан" : "Ошибка обработки платежа"
        );
        
        return success 
            ? ResponseEntity.ok(response)
            : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    @PostMapping("/process/by-id")
    public ResponseEntity<Map<String, Object>> processPaymentById(@RequestBody PaymentRequestWithId request) {
        logger.logInfo("Получен запрос на проведение платежа по ID: " + request);
        
        boolean success = paymentService.processPaymentById(
            request.getPaymentMethodId(),
            request.getOrderId(),
            request.getAmount()
        );
        
        Map<String, Object> response = Map.of(
            "success", success,
            "orderId", request.getOrderId(),
            "paymentMethodId", request.getPaymentMethodId(),
            "message", success ? "Платеж успешно обработан" : "Ошибка обработки платежа"
        );
        
        return success 
            ? ResponseEntity.ok(response)
            : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * Класс для запроса на проведение платежа по коду метода
     */
    public static class PaymentRequest {
        private String orderId;
        private String paymentMethod;
        private BigDecimal amount;
        
        public String getOrderId() { return orderId; }
        public void setOrderId(String orderId) { this.orderId = orderId; }
        
        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
        
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        
        @Override
        public String toString() {
            return "PaymentRequest [orderId=" + orderId + 
                ", paymentMethod=" + paymentMethod + 
                ", amount=" + amount + "]";
        }
    }
    
    /**
     * Класс для запроса на проведение платежа по ID метода
     */
    public static class PaymentRequestWithId {
        private String orderId;
        private Long paymentMethodId;
        private BigDecimal amount;
        
        public String getOrderId() { return orderId; }
        public void setOrderId(String orderId) { this.orderId = orderId; }
        
        public Long getPaymentMethodId() { return paymentMethodId; }
        public void setPaymentMethodId(Long paymentMethodId) { this.paymentMethodId = paymentMethodId; }
        
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        
        @Override
        public String toString() {
            return "PaymentRequestWithId [orderId=" + orderId + 
                ", paymentMethodId=" + paymentMethodId + 
                ", amount=" + amount + "]";
        }
    }
} 