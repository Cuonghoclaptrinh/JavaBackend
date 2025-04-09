package com.example.PRJWEB.Configure;

import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.Data;

import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
public class VNPayConfig {
    @Value("${vnpay.tmnCode}")
    private String tmnCode;

    @Value("${vnpay.hashSecret}")
    private String hashSecret;

    @Value("${vnpay.payUrl}")
    private String payUrl;

    @Value("${vnpay.returnUrl}")
    private String returnUrl;

    @PostConstruct
    public void checkConfig() {
        System.out.println("tmnCode: " + tmnCode);
        System.out.println("hashSecret: " + hashSecret);
        System.out.println("payUrl: " + payUrl);
        System.out.println("returnUrl: " + returnUrl);
    }
}

