package com.github.drbookings.ui.beans;

import java.time.LocalDate;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import com.github.drbookings.model.Payout;
import com.github.drbookings.model.PayoutCalculator;
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

	private final FloatProperty totalPayout = new SimpleFloatProperty();

	private final FloatProperty cleaningFees = new SimpleFloatProperty();

	private final FloatProperty cleaningCosts = new SimpleFloatProperty();

	private final FloatProperty fixCosts = new SimpleFloatProperty();

	private final FloatProperty earnings = new SimpleFloatProperty();

	private final FloatProperty grossEarnings = new SimpleFloatProperty();

	private final FloatProperty netEarnings = new SimpleFloatProperty();

	private final FloatProperty earningsPayout = new SimpleFloatProperty();

	private final FloatProperty nightsPercent = new SimpleFloatProperty();

	private final FloatProperty performance = new SimpleFloatProperty();

	private final IntegerProperty cleaningCount = new SimpleIntegerProperty();

	private final IntegerProperty bookingCount = new SimpleIntegerProperty();

	private final FloatProperty unknownPayout = new SimpleFloatProperty();

	private final ObjectProperty<Range<LocalDate>> dateRange = new SimpleObjectProperty<>();

	private static final PayoutCalculator pc = new PayoutCalculator();

	public StatisticsTableBean() {
		earningsPayoutProperty().bind(Bindings.createObjectBinding(calculateEarningsPayout(), earningsProperty()));
		earningsProperty().bind(Bindings.createObjectBinding(calculateEarnings(), totalPayoutProperty(),
				unknownPayoutProperty(), fixCostsProperty()));
		performanceProperty()
				.bind(Bindings.createObjectBinding(calculatePerformance(), earningsProperty(), cleaningCostsProperty(),
						nightsPercentProperty(), SettingsManager.getInstance().referenceColdRentLongTermProperty(),
						SettingsManager.getInstance().numberOfRoomsProperty()));

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
			return getEarnings() * SettingsManager.getInstance().getEarningsPayoutPercent();
		};
	}

	private Callable<Number> calculateEarnings() {
		return () -> {
			return getTotalPayout() - getFixCosts();
		};
	}

	public static StatisticsTableBean build(final String origin, final Collection<? extends BookingEntry> bookings) {
		final StatisticsTableBean result = new StatisticsTableBean();
		result.setOrigin(origin);
		result.setNumberOfNights((int) BookingEntries.countNights(bookings));
		result.setBookingCount(bookings.stream().map(b -> b.getElement()).collect(Collectors.toSet()).size());
		final Payout p = pc.apply(bookings);
		result.setTotalPayout((float) p.getPayout());
		result.setUnknownPayout((float) p.getPayoutUnkown());
		result.setDateRange(p.getDateRange());
		result.setCleaningCount((int) BookingEntries.countCleanings(bookings));
		result.setCleaningCosts((float) BookingEntries.getCleaningCosts(bookings));
		result.setCleaningFees((float) BookingEntries.getCleaningFees(bookings));
		result.setGrossEarnings((float) bookings.stream().mapToDouble(b -> b.getGrossEarnings()).sum());
		result.setNetEarnings((float) bookings.stream().mapToDouble(b -> b.getNetEarnings()).sum());
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

	public final FloatProperty totalPayoutProperty() {
		return this.totalPayout;
	}

	public final float getTotalPayout() {
		return this.totalPayoutProperty().get();
	}

	public final void setTotalPayout(final float totalPayout) {
		this.totalPayoutProperty().set(totalPayout);
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

	public final FloatProperty unknownPayoutProperty() {
		return this.unknownPayout;
	}

	public final float getUnknownPayout() {
		return this.unknownPayoutProperty().get();
	}

	public final void setUnknownPayout(final float unknownPayout) {
		this.unknownPayoutProperty().set(unknownPayout);
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

	public final FloatProperty grossEarningsProperty() {
		return this.grossEarnings;
	}

	public final float getGrossEarnings() {
		return this.grossEarningsProperty().get();
	}

	public final void setGrossEarnings(final float grossEarnings) {
		this.grossEarningsProperty().set(grossEarnings);
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

	public final FloatProperty performanceProperty() {
		return this.performance;
	}

	public final float getPerformance() {
		return this.performanceProperty().get();
	}

	public final void setPerformance(final float performance) {
		this.performanceProperty().set(performance);
	}

}
