package com.github.drbookings.model.data;

import java.time.LocalDate;
import java.util.Currency;

import javax.money.MonetaryAmount;

import com.github.drbookings.model.Payment;
import com.github.drbookings.model.PaymentImpl;

public class Expense implements Named, Payment {

    private final Named named;

    private final Payment payment;

    public Expense(final String name, final LocalDate date, final float amount) {
	this.named = new NamedImpl(name);
	this.payment = new PaymentImpl(date, amount);

    }

    @Override
    public MonetaryAmount getAmount() {
	return payment.getAmount();
    }

    @Override
    public Currency getCurrency() {
	return payment.getCurrency();
    }

    @Override
    public LocalDate getDate() {
	return payment.getDate();
    }

    @Override
    public String getName() {
	return named.getName();
    }

}
