package com.github.drbookings.model;

public interface EarningsProvider extends PaymentProvider {

	float getEarnings(boolean netEarnings);

}
