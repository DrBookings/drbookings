package com.github.drbookings.model;

import java.time.LocalDate;
import java.util.Currency;

import javax.money.MonetaryAmount;

public interface Payment {

    MonetaryAmount getAmount();

    Currency getCurrency();

    LocalDate getDate();

}