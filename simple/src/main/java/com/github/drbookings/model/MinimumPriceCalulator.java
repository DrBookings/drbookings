package com.github.drbookings.model;

import java.util.function.Function;

public class MinimumPriceCalulator implements Function<Number, Number> {

    public static final float DEFAULT_NUMBER_OF_DAYS_PER_MONTH = 30;

    public static final float DEFAULT_REFERENCE_INCOME = 800;

    private float numberOfDaysPerMonth = DEFAULT_NUMBER_OF_DAYS_PER_MONTH;

    private float referenceIncome = DEFAULT_REFERENCE_INCOME;

    @Override
    public Number apply(final Number occupancyRate) {
	final float daysBusy = numberOfDaysPerMonth * occupancyRate.floatValue();
	return referenceIncome / daysBusy;
    }

    public float getNumberOfDaysPerMonth() {
	return numberOfDaysPerMonth;
    }

    public float getReferenceIncome() {
	return referenceIncome;
    }

    public void setNumberOfDaysPerMonth(final float numberOfDaysPerMonth) {
	this.numberOfDaysPerMonth = numberOfDaysPerMonth;
    }

    public void setReferenceIncome(final float referenceIncome) {
	this.referenceIncome = referenceIncome;
    }

}
