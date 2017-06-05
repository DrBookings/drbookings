package com.github.drbookings.model;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.model.settings.SettingsManager;
import com.github.drbookings.ui.CellSelectionManager;

import javafx.beans.binding.Bindings;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;

public class ProfitProvider {

    private static final Logger logger = LoggerFactory.getLogger(ProfitProvider.class);

    private static double getCostsToCover() {
	double result = 0;
	result += SettingsManager.getInstance().getAdditionalCosts();
	result *= SettingsManager.getInstance().getNumberOfRooms();
	return result;
    }

    private static double getEarnings() {

	return MoneyMonitor.getInstance().getTotalNetEarnings();
    }

    private static double getHours() {
	return SettingsManager.getInstance().getWorkHoursPerMonth();
    }

    private static double getRefColdRentLongTerm() {
	return SettingsManager.getInstance().getReferenceColdRentLongTerm()
		* SettingsManager.getInstance().getNumberOfRooms();
    }

    private final FloatProperty profit = new SimpleFloatProperty();

    private final FloatProperty profitPerHour = new SimpleFloatProperty();

    public ProfitProvider() {
	bindProperties();
    }

    private void bindProperties() {
	profit.bind(
		Bindings.createFloatBinding(calculcateProfit(), SettingsManager.getInstance().additionalCostsProperty(),
			CellSelectionManager.getInstance().getSelection()));
	profitPerHour.bind(Bindings.createFloatBinding(calculateProfitPerHour(), profit));
    }

    private Callable<Float> calculateProfitPerHour() {
	return () -> {
	    final Set<LocalDate> dates = CellSelectionManager.getInstance().getSelection().stream()
		    .map(r -> r.getDate()).collect(Collectors.toSet());
	    // hours is a per-month-value, therefore calculate for selected
	    // period
	    final double hours = getHours() / 30d * dates.size();
	    return (float) (profit.get() / hours);
	};
    }

    private Callable<Float> calculcateProfit() {
	return () -> {
	    final Set<LocalDate> dates = CellSelectionManager.getInstance().getSelection().stream()
		    .map(r -> r.getDate()).collect(Collectors.toSet());
	    final Set<YearMonth> months = dates.stream().map(d -> YearMonth.from(d)).collect(Collectors.toSet());
	    final OptionalDouble avDays = months.stream().mapToDouble(ym -> ym.getMonth().maxLength()).average();
	    // costs to cover is a per-month-value, therefore calculate for
	    // selected
	    // period
	    if (!avDays.isPresent()) {
		return 0f;
	    }
	    final double costsToCover = getCostsToCover() / avDays.getAsDouble() * dates.size();
	    // reference cold rent is a per-month-value, therefore calculate for
	    // selected period
	    final double refColdRentLongTerm = getRefColdRentLongTerm() / avDays.getAsDouble() * dates.size();
	    final double netEarnings = getEarnings();
	    final double payment = netEarnings - costsToCover - refColdRentLongTerm;

	    if (logger.isDebugEnabled()) {
		logger.debug(String.format("AvDaysMonth %4.2f", avDays.getAsDouble()));
		logger.debug(String.format("CostsToCover %8.2f", costsToCover));
		logger.debug(String.format("CostsToCover plus RefRent %8.2f", (costsToCover + refColdRentLongTerm)));
		logger.debug(String.format("TotalNetProfit %8.2f", netEarnings));
		logger.debug(String.format("Profit %8.2f", payment));
	    }
	    return (float) payment;
	};
    }

    public final float getProfit() {
	return this.profitProperty().get();
    }

    public final float getProfitPerHour() {
	return this.profitPerHourProperty().get();
    }

    public final FloatProperty profitPerHourProperty() {
	return this.profitPerHour;
    }

    public final FloatProperty profitProperty() {
	return this.profit;
    }

    public final void setProfit(final float profit) {
	this.profitProperty().set(profit);
    }

    public final void setProfitPerHour(final float profitPerHour) {
	this.profitPerHourProperty().set(profitPerHour);
    }

}
