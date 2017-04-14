package com.github.drbookings.model;

public interface NetEarningsCalculator {

    // @Deprecated
    // float calculateNetEarnings(MainManager manager, LocalDate localDate);

    float calculateNetEarnings(float grossEarnings, String bookingOrigin);

    float getFees();

    float getProvision();

    NetEarningsCalculator setFees(float fees);

    NetEarningsCalculator setProvision(float provision);

}
