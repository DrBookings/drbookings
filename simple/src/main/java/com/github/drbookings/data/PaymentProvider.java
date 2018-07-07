package com.github.drbookings.data;

import java.util.List;

import com.github.drbookings.model.Payment;

public interface PaymentProvider {

    List<Payment> getPayments();

}
