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

import java.time.LocalDate;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.github.drbookings.model.data.BookingEntries;
import com.github.drbookings.model.settings.SettingsManager;
import com.github.drbookings.ui.BookingEntry;
import com.google.common.collect.Range;

import javafx.beans.binding.Bindings;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class StatisticsTableBean {

	private final StringProperty origin = new SimpleStringProperty();

	private final IntegerProperty numberOfNights = new SimpleIntegerProperty();

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

	private final IntegerProperty bookingCount = new SimpleIntegerProperty();

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
		result.setNumberOfNights((int) BookingEntries.countNights(origin, bookings));
		result.setBookingCount(bookings.stream().map(b -> b.getElement()).collect(Collectors.toSet()).size());
		result.setCleaningCount((int) BookingEntries.countCleanings(bookings));
		result.setCleaningCosts((float) BookingEntries.getCleaningCosts(bookings));
		result.setCleaningFees((float) BookingEntries.getCleaningFees(bookings));
		result.setGrossIncome((float) BookingEntries.getGrossEarnings(bookings));
		result.setNetIncome((float) BookingEntries.getNetEarnings(bookings));
		result.setServiceFees((float) BookingEntries.getServiceFees(bookings));
		return result;

	}

	public static StatisticsTableBean buildSum(final Collection<StatisticsTableBean> data) {
		final StatisticsTableBean result = new StatisticsTableBean(false);
		result.setOrigin("sum");
		result.setNumberOfNights(data.stream().mapToInt(b -> b.getNumberOfNights()).sum());
		result.setBookingCount(data.stream().mapToInt(b -> b.getBookingCount()).sum());
		result.setCleaningCount(data.stream().mapToInt(b -> b.getCleaningCount()).sum());
		result.setCleaningCosts((float) data.stream().mapToDouble(b -> b.getCleaningCosts()).sum());
		result.setCleaningFees((float) data.stream().mapToDouble(b -> b.getCleaningFees()).sum());
		result.setGrossIncome((float) data.stream().mapToDouble(b -> b.getGrossEarnings()).sum());
		result.setNetIncome((float) data.stream().mapToDouble(b -> b.getNetIncome()).sum());
		result.setServiceFees((float) data.stream().mapToDouble(b -> b.getServiceFees()).sum());
		result.setEarnings((float) data.stream().mapToDouble(b -> b.getEarnings()).sum());
		result.setEarningsPayout((float) data.stream().mapToDouble(b -> b.getEarningsPayout()).sum());
		result.setNetEarnings((float) data.stream().mapToDouble(b -> b.getNetEarnings()).sum());
		return result;

	}

	public final StringProperty originProperty() {
		return this.origin;
	}

	public final String getOrigin() {
		return this.originProperty().get();
	}

	public final void setOrigin(final String origin) {
		this.originProperty().set(origin);
	}

	public final IntegerProperty numberOfNightsProperty() {
		return this.numberOfNights;
	}

	public final int getNumberOfNights() {
		return this.numberOfNightsProperty().get();
	}

	public final void setNumberOfNights(final int numberOfNights) {
		this.numberOfNightsProperty().set(numberOfNights);
	}

	public final FloatProperty cleaningFeesProperty() {
		return this.cleaningFees;
	}

	public final float getCleaningFees() {
		return this.cleaningFeesProperty().get();
	}

	public final void setCleaningFees(final float cleaningFees) {
		this.cleaningFeesProperty().set(cleaningFees);
	}

	public final FloatProperty cleaningCostsProperty() {
		return this.cleaningCosts;
	}

	public final float getCleaningCosts() {
		return this.cleaningCostsProperty().get();
	}

	public final void setCleaningCosts(final float cleaningCosts) {
		this.cleaningCostsProperty().set(cleaningCosts);
	}

	public final IntegerProperty cleaningCountProperty() {
		return this.cleaningCount;
	}

	public final int getCleaningCount() {
		return this.cleaningCountProperty().get();
	}

	public final void setCleaningCount(final int cleaningCount) {
		this.cleaningCountProperty().set(cleaningCount);
	}

	public final ObjectProperty<Range<LocalDate>> dateRangeProperty() {
		return this.dateRange;
	}

	public final Range<LocalDate> getDateRange() {
		return this.dateRangeProperty().get();
	}

	public final void setDateRange(final Range<LocalDate> dateRange) {
		this.dateRangeProperty().set(dateRange);
	}

	public final IntegerProperty bookingCountProperty() {
		return this.bookingCount;
	}

	public final int getBookingCount() {
		return this.bookingCountProperty().get();
	}

	public final void setBookingCount(final int bookingCount) {
		this.bookingCountProperty().set(bookingCount);
	}

	public final FloatProperty nightsPercentProperty() {
		return this.nightsPercent;
	}

	public final float getNightsPercent() {
		return this.nightsPercentProperty().get();
	}

	public final void setNightsPercent(final float nightsPercent) {
		this.nightsPercentProperty().set(nightsPercent);
	}

	public final FloatProperty fixCostsProperty() {
		return this.fixCosts;
	}

	public final float getFixCosts() {
		return this.fixCostsProperty().get();
	}

	public final void setFixCosts(final float fixCosts) {
		this.fixCostsProperty().set(fixCosts);
	}

	public final FloatProperty earningsProperty() {
		return this.earnings;
	}

	public final float getEarnings() {
		return this.earningsProperty().get();
	}

	public final void setEarnings(final float earnings) {
		this.earningsProperty().set(earnings);
	}

	public final FloatProperty earningsPayoutProperty() {
		return this.earningsPayout;
	}

	public final float getEarningsPayout() {
		return this.earningsPayoutProperty().get();
	}

	public final void setEarningsPayout(final float earningsPayout) {
		this.earningsPayoutProperty().set(earningsPayout);
	}

	public final FloatProperty grossIncomeProperty() {
		return this.grossIncome;
	}

	public final float getGrossEarnings() {
		return this.grossIncomeProperty().get();
	}

	public final void setGrossIncome(final float grossIncome) {
		this.grossIncomeProperty().set(grossIncome);
	}

	public final FloatProperty netIncomeProperty() {
		return this.netIncome;
	}

	public final float getNetIncome() {
		return this.netIncomeProperty().get();
	}

	public final void setNetIncome(final float netEarnings) {
		this.netIncomeProperty().set(netEarnings);
	}

	public final FloatProperty serviceFeesProperty() {
		return this.serviceFees;
	}

	public final float getServiceFees() {
		return this.serviceFeesProperty().get();
	}

	public final void setServiceFees(final float serviceFees) {
		this.serviceFeesProperty().set(serviceFees);
	}

	public final FloatProperty netEarningsProperty() {
		return this.netEarnings;
	}

	public final float getNetEarnings() {
		return this.netEarningsProperty().get();
	}

	public final void setNetEarnings(final float netEarnings) {
		this.netEarningsProperty().set(netEarnings);
	}

}
