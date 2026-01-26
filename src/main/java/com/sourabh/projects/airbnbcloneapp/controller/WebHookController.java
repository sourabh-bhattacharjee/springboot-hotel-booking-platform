package com.sourabh.projects.airbnbcloneapp.controller;

import com.sourabh.projects.airbnbcloneapp.service.BookingService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class WebHookController {

    private final BookingService bookingService;

    @Value("${stripe.webhook.secret}")
    private String endPointSecret;

    @PostMapping("/payment")
    public ResponseEntity<Void> capturePayment(HttpServletRequest request, @RequestHeader("Stripe-Signature") String sigHeader){
        log.info("Webhook received. Signature: {}", sigHeader);

        String payload;
        try{
            payload = request.getReader()
                    .lines()
                    .collect(Collectors.joining("\n"));
        } catch (IOException e) {
            log.error("Failed to read Stripe webhook payload", e);
            return ResponseEntity.badRequest().build();
        }
        try{
            Event event = Webhook.constructEvent(payload,sigHeader,endPointSecret);
            bookingService.capturePayment(event);
            return ResponseEntity.noContent().build();
        } catch (SignatureVerificationException e){
            throw  new RuntimeException(e);
        }
    }

}
