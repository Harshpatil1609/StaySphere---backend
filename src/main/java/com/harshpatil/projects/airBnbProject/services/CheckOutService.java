package com.harshpatil.projects.airBnbProject.services;

import com.harshpatil.projects.airBnbProject.entity.Booking;

public interface CheckOutService {

    String getCheckoutSession(Booking booking , String succesUrl, String failureUrl );
}
