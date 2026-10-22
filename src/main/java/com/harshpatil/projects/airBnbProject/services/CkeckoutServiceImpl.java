package com.harshpatil.projects.airBnbProject.services;

import com.harshpatil.projects.airBnbProject.entity.Booking;
import com.harshpatil.projects.airBnbProject.entity.User;
import com.harshpatil.projects.airBnbProject.repository.BookingRepository;
import com.stripe.model.Customer;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class CkeckoutServiceImpl implements CheckOutService  {

    private final BookingRepository bookingRepository;


    @Override
    public String getCheckoutSession(Booking booking, String succesUrl, String failureUrl) {
        log.info("creating session for booking with id : {} ",booking.getId());
        User user=(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {

            CustomerCreateParams customerCreateParams= CustomerCreateParams.builder()
                    .setName(user.getName())
                    .setEmail(user.getEmail())
                    .build();

            Customer customer=Customer.create(customerCreateParams);

            SessionCreateParams sessionParams=SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setBillingAddressCollection(SessionCreateParams.BillingAddressCollection.REQUIRED)
                    .setCustomer(customer.getId())
                    .setSuccessUrl(succesUrl)
                    .setCancelUrl(failureUrl)
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("inr")
                                                    .setUnitAmount(booking.getAmount().multiply(BigDecimal.valueOf(100)).longValue())
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName(booking.getHotel().getName()+" "+booking.getRoom().getType())
                                                                    .setDescription("Booking id : "+booking.getId())
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .build()
                    )
                    .build();

            Session session=Session.create(sessionParams);

            booking.setPaymentSessiond(session.getId());
            bookingRepository.save(booking);

            log.info("booking created successfully for booking with id : {} ",booking.getId());
            return session.getUrl();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

    }
}
