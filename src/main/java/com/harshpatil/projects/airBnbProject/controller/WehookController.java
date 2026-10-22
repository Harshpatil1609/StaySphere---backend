package com.harshpatil.projects.airBnbProject.controller;

import com.harshpatil.projects.airBnbProject.services.BookingServie;
import com.stripe.net.Webhook;
import com.stripe.model.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/webhook")
@Slf4j
public class WehookController {

    private final BookingServie bookingServie;

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @PostMapping("/payment")
    public ResponseEntity<Void> capturePayments(@RequestBody String payload, @RequestHeader("Stripe-Signature")String sigHeader)
    {
        log.info("Stripe webhook received");
        try {
            Event event= Webhook.constructEvent(payload,sigHeader,endpointSecret);

            log.info("Stripe event type: {}", event.getType());
            log.info("Stripe event ID: {}", event.getId());

            bookingServie.capturePayment(event);

            log.info("Stripe webhook processed successfully");

            return ResponseEntity.noContent().build();

        }
        catch (Exception e)
        {
            log.error("Failed to process Stripe webhook", e);

            return ResponseEntity.internalServerError().build();
        }
    }
}
