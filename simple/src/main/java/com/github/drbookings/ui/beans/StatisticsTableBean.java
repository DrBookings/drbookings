package com.github.drbookings.ui.beans;

/*-
 * #%L
 * DrBookings
 * %%
 * Copyright (C) 2016 - 2017 Alexander Kerner
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

import com.github.drbookings.model.data.BookingEntries;
import com.github.drbookings.model.settings.SettingsManager;
import com.github.drbookings.ui.BookingEntry;
import com.google.common.collect.Range;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Collection;
import java.util.concurrent.Callable;

public class StatisticsTableBean {

    private static final Logger logger = LoggerFactory.getLogger(StatisticsTableBean.class);

	private final StringProperty origin = new SimpleStringProperty();

    private final IntegerProperty numberOfPayedNights = new SimpleIntegerProperty();

    private final IntegerProperty numberOfAllNights = new SimpleIntegerProperty();

	private final FloatProperty cleaningFees = new SimpleFloatProperty();

	private final FloatProperty cleaningCosts = new SimpleFloatProperty();

	private final FloatProperty fixCosts = new SimpleFloatProperty();

	private final FloatProperty earnings = new SimpleFloatProperty();

	private final FloatProperty netEarnings = new SimpleFloatProperty();

	private final FloatProperty grossIncome = new SimpleFloatProperty();

	private final FloatProperty netIncome = new SimpleFloatProperty();

	private final FloatProperty earningsPayout = new SimpleFloatProperty();

	private final FloatProperty nightsPercent = new SimpleFloatProperty();

	private final FloatProperty serviceFees = new SimpleFloatProperty();

	private final IntegerProperty cleaningCount = new SimpleIntegerProperty();

	private final IntegerProperty numberOfPayedBookings = new SimpleIntegerProperty();

    private final IntegerProperty numberOfAllBookings = new SimpleIntegerProperty();

	private final ObjectProperty<Range<LocalDate>> dateRange = new SimpleObjectProperty<>();

	public StatisticsTableBean() {
		this(true);
	}

	public StatisticsTableBean(final boolean bind) {
		if (bind) {
			earningsPayoutProperty().bind(Bindings.createObjectBinding(calculateEarningsPayout(), earningsProperty()));
			netEarningsProperty().bind(Bindings.createObjectBinding(calculateNetEarningsPayout(), earningsProperty()));
			earningsProperty().bind(
					Bindings.createObjectBinding(calculateEarnings(), fixCostsProperty(), cleaningCostsProperty()));
		}
	}

	private Callable<Number> calculateNetEarningsPayout() {
		return () -> getEarnings() - getCleaningCosts();
	}

	private Callable<Number> calculatePerformance() {
		return () -> {
			final double earnings = getEarnings();
			final double cleaningCosts = getCleaningCosts();
			final double referenceIncome = SettingsManager.getInstance().getReferenceColdRentLongTerm();
			final double referenceIncomeTotal = referenceIncome * SettingsManager.getInstance().getNumberOfRooms();
			final double percentage = getNightsPercent();
			final double referenceIncomeRelative = referenceIncomeTotal * percentage / 100;
			final double result = earnings - cleaningCosts - referenceIncomeRelative;
			return result;
		};
	}

	private Callable<Number> calculateEarningsPayout() {
		return () -> {
			if (StringUtils.isBlank(getOrigin())) {
				return getEarnings();
			}

			// not earnings, since here cleaning is already included. But
			// cleaning costs go cash, so its net income times payout percent
			return getEarnings() * SettingsManager.getInstance().getEarningsPayoutPercent();
		};
	}

	private Callable<Number> calculateEarnings() {
		return () -> {
			return (double) getNetIncome() - getFixCosts();
		};
	}

	public static StatisticsTableBean build(final String origin, final Collection<? extends BookingEntry> bookings) {
		final StatisticsTableBean result = new StatisticsTableBean();
		result.setOrigin(origin);
        result.setGrossIncome((float) BookingEntries.getGrossEarnings(bookings));
        result.setNetIncome((float) BookingEntries.getNetEarnings(bookings));
        result.setServiceFees((float) BookingEntries.getServiceFees(bookings));
        return result;

	}

    public static StatisticsTableBean applyCleaningStuff(final StatisticsTableBean bean, final Collection<? extends
            BookingEntry> allBookingsInRange) {
        bean.setCleaningCount((int) BookingEntries.countCleanings(allBookingsInRange));
        bean.setCleaningCosts((float) BookingEntries.getCleaningCosts(allBookingsInRange));
        bean.setCleaningFees((float) BookingEntries.getCleaningFees(allBookingsInRange));
        return bean;
    }

	public static StatisticsTableBean buildSum(final Collection<StatisticsTableBean> data) {
		final StatisticsTableBean result = new StatisticsTableBean(false);
		result.setOrigin("sum");
        result.setNumberOfPayedNights(data.stream().mapToInt(StatisticsTableBean::getNumberOfPayedNights).sum());
        result.setNumberOfPayedBookings(data.stream().mapToInt(StatisticsTableBean::getNumberOfPayedBookings).sum());
        result.setCleaningCount(data.stream().mapToInt(StatisticsTableBean::getCleaningCount).sum());
        result.setCleaningCosts((float) data.stream().mapToDouble(StatisticsTableBean::getCleaningCosts).sum());
        result.setCleaningFees((float) data.stream().mapToDouble(StatisticsTableBean::getCleaningFees).sum());
        result.setGrossIncome((float) data.stream().mapToDouble(StatisticsTableBean::getGrossEarnings).sum());
        result.setNetIncome((float) data.stream().mapToDouble(StatisticsTableBean::getNetIncome).sum());
        result.setServiceFees((float) data.stream().mapToDouble(StatisticsTableBean::getServiceFees).sum());
        result.setEarnings((float) data.stream().mapToDouble(StatisticsTableBean::getEarnings).sum());
        result.setEarningsPayout((float) data.stream().mapToDouble(StatisticsTableBean::getEarningsPayout).sum());
        result.setNetEarnings((float) data.stream().mapToDouble(StatisticsTableBean::getNetEarnings).sum());
        return result;

	}

	public final StringProperty originProperty() {
        return origin;
    }

	public final String getOrigin() {
        return originProperty().get();
    }

	public final void setOrigin(final String origin) {
        originProperty().set(origin);
    }

    public final IntegerProperty numberOfPayedNightsProperty() {
        return numberOfPayedNights;
    }

    public final IntegerProperty numberOfAllNightsProperty() {
        return numberOfAllNights;
    }

    public final int getNumberOfPayedNights() {
        return numberOfPayedNightsProperty().get();
    }

    public final int getNumberOfAllNights() {
        return numberOfAllNightsProperty().get();
    }

    public final void setNumberOfPayedNights(final int numberOfPayedNights) {
        numberOfPayedNightsProperty().set(numberOfPayedNights);
    }

    public final void setNumberOfAllNights(final int numberOfAllNights) {
        numberOfAllNightsProperty().set(numberOfAllNights);
    }

	public final FloatProperty cleaningFeesProperty() {
        return cleaningFees;
    }

	public final float getCleaningFees() {
        return cleaningFeesProperty().get();
    }

	public final void setCleaningFees(final float cleaningFees) {
        cleaningFeesProperty().set(cleaningFees);
    }

	public final FloatProperty cleaningCostsProperty() {
        return cleaningCosts;
    }

	public final float getCleaningCosts() {
        return cleaningCostsProperty().get();
    }

	public final void setCleaningCosts(final float cleaningCosts) {
        cleaningCostsProperty().set(cleaningCosts);
    }

	public final IntegerProperty cleaningCountProperty() {
        return cleaningCount;
    }

	public final int getCleaningCount() {
        return cleaningCountProperty().get();
    }

	public final void setCleaningCount(final int cleaningCount) {
        cleaningCountProperty().set(cleaningCount);
    }

	public final ObjectProperty<Range<LocalDate>> dateRangeProperty() {
        return dateRange;
    }

	public final Range<LocalDate> getDateRange() {
        return dateRangeProperty().get();
    }

	public final void setDateRange(final Range<LocalDate> dateRange) {
        dateRangeProperty().set(dateRange);
    }

	public final IntegerProperty numberOfPayedBookingsProperty() {
        return numberOfPayedBookings;
    }

    public final IntegerProperty numberOfAllBookingsProperty() {
        return numberOfAllBookings;
    }

	public final int getNumberOfPayedBookings() {
        return numberOfPayedBookingsProperty().get();
    }

    public final int getNumberOfAllBookings() {
        return numberOfAllBookingsProperty().get();
    }

	public final void setNumberOfPayedBookings(final int numberOfPayedBookings) {
        numberOfPayedBookingsProperty().set(numberOfPayedBookings);
    }

    public final void setNumberOfAllBookings(final int numberOfAllBookings) {
        numberOfAllBookingsProperty().set(numberOfAllBookings);
    }

	public final FloatProperty nightsPercentProperty() {
        return nightsPercent;
    }

	public final float getNightsPercent() {
        return nightsPercentProperty().get();
    }

	public final void setNightsPercent(final float nightsPercent) {
        nightsPercentProperty().set(nightsPercent);
    }

	public final FloatProperty fixCostsProperty() {
        return fixCosts;
    }

	public final float getFixCosts() {
        return fixCostsProperty().get();
    }

	public final void setFixCosts(final float fixCosts) {
        fixCostsProperty().set(fixCosts);
    }

	public final FloatProperty earningsProperty() {
        return earnings;
    }

	public final float getEarnings() {
        return earningsProperty().get();
    }

	public final void setEarnings(final float earnings) {
        earningsProperty().set(earnings);
    }

	public final FloatProperty earningsPayoutProperty() {
        return earningsPayout;
    }

	public final float getEarningsPayout() {
        return earningsPayoutProperty().get();
    }

	public final void setEarningsPayout(final float earningsPayout) {
        earningsPayoutProperty().set(earningsPayout);
    }

	public final FloatProperty grossIncomeProperty() {
        return grossIncome;
    }

	public final float getGrossEarnings() {
        return grossIncomeProperty().get();
    }

	public final void setGrossIncome(final float grossIncome) {
        grossIncomeProperty().set(grossIncome);
    }

	public final FloatProperty netIncomeProperty() {
        return netIncome;
    }

	public final float getNetIncome() {
        return netIncomeProperty().get();
    }

	public final void setNetIncome(final float netEarnings) {
        netIncomeProperty().set(netEarnings);
    }

	public final FloatProperty serviceFeesProperty() {
        return serviceFees;
    }

	public final float getServiceFees() {
        return serviceFeesProperty().get();
    }

	public final void setServiceFees(final float serviceFees) {
        serviceFeesProperty().set(serviceFees);
    }

	public final FloatProperty netEarningsProperty() {
        return netEarnings;
    }

	public final float getNetEarnings() {
        return netEarningsProperty().get();
    }

	public final void setNetEarnings(final float netEarnings) {
        netEarningsProperty().set(netEarnings);
    }

}
