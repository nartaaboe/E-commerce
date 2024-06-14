package com.example.ecommerce.dto.request;

import com.example.ecommerce.entity.PaymentMethod;
import com.example.ecommerce.entity.PaymentStatus;
import lombok.Data;

@Data
public class PaymentDetailsRequest {
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
}
