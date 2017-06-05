package com.github.drbookings.model;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.model.settings.SettingsManager;

import javafx.beans.binding.Bindings;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;

public class MinimumPriceProvider {

    private static final Logger logger = LoggerFactory.getLogger(MinimumPriceProvider.class);

    public static final float DEFAULT_NUMBER_OF_DAYS_PER_MONTH = 30;

    private final FloatProperty numberOfDaysPerMonth = new SimpleFloatProperty(DEFAULT_NUMBER_OF_DAYS_PER_MONTH);

    private final FloatProperty referenceIncome = new SimpleFloatProperty();

    private final FloatProperty minimumPrice = new SimpleFloatProperty();

    private final OccupancyRateProvider occupancyRateProvider = new OccupancyRateProvider();

    public MinimumPriceProvider() {
	bindProperties();
    }

    private void bindProperties() {
	referenceIncome.bind(Bindings.createObjectBinding(getRefIncome(),
		SettingsManager.getInstance().referenceColdRentLongTermProperty(),
		SettingsManager.getInstance().additionalCostsProperty(),
		SettingsManager.getInstance().numberOfRoomsProperty()));
	minimumPrice.bind(Bindings.createObjectBinding(calculateMinimumPrice(), referenceIncome,
		occupancyRateProvider.occupancyRateProperty()));
    }

    private Callable<Number> calculateMinimumPrice() {
	return () -> {
	    final double daysBusy = (double) numberOfDaysPerMonth.get() * occupancyRateProvider.getOccupancyRate();
	    if (logger.isDebugEnabled()) {
		logger.debug("OccupancyRate: " + occupancyRateProvider.getOccupancyRate());
	    }
	    if (logger.isDebugEnabled()) {
		logger.debug("Days busy: " + daysBusy);
	    }
	    if (logger.isDebugEnabled()) {
		logger.debug("RefIncome: " + referenceIncome.get());
	    }
	    final double result = referenceIncome.get() / daysBusy / SettingsManager.getInstance().getNumberOfRooms();
	    if (logger.isDebugEnabled()) {
		logger.debug("MinPrice: " + result);
	    }
	    return result;
	};
    }

    public final float getMinimumPrice() {
	return this.minimumPriceProperty().get();
    }

    public final float getNumberOfDaysPerMonth() {
	return this.numberOfDaysPerMonthProperty().get();
    }

    public final float getReferenceIncome() {
	return this.referenceIncomeProperty().get();
    }

    private Callable<Number> getRefIncome() {
	return () -> {
	    final double refColdRent = SettingsManager.getInstance().getReferenceColdRentLongTerm();
	    final double addCosts = SettingsManager.getInstance().getAdditionalCosts();
	    final double roomCnt = SettingsManager.getInstance().getNumberOfRooms();
	    double result = 0;
	    result += refColdRent;
	    result += addCosts;
	    result *= roomCnt;
	    if (logger.isDebugEnabled()) {
		logger.debug("RefIncome: " + result);
	    }

	    return result;
	};
    }

    public final FloatProperty minimumPriceProperty() {
	return this.minimumPrice;
    }

    public final FloatProperty numberOfDaysPerMonthProperty() {
	return this.numberOfDaysPerMonth;
    }

    public final FloatProperty referenceIncomeProperty() {
	return this.referenceIncome;
    }

    public final void setMinimumPrice(final float minimumPrice) {
	this.minimumPriceProperty().set(minimumPrice);
    }

    public final void setNumberOfDaysPerMonth(final float numberOfDaysPerMonth) {
	this.numberOfDaysPerMonthProperty().set(numberOfDaysPerMonth);
    }

    public final void setReferenceIncome(final float referenceIncome) {
	this.referenceIncomeProperty().set(referenceIncome);
    }

}
