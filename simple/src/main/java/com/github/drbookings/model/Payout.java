package com.github.drbookings.model;

import java.time.LocalDate;

import com.github.drbookings.model.data.Booking;
import com.google.common.collect.Range;

public class Payout {

	@Override
	public String toString() {
		return "Payout [payout=" + payout + ", payoutUnkown=" + payoutUnkown + ", dateRange=" + dateRange + "]";
	}

	private final double payout;

	private final double payoutUnkown;

	private final Range<LocalDate> dateRange;

	public Payout(final Range<LocalDate> dateRange, final double payout, final double payoutUnkown) {
		super();
		this.payout = payout;
		this.payoutUnkown = payoutUnkown;
		this.dateRange = dateRange;
	}

	public Payout(final LocalDate date, final double payout, final double payoutUnkown) {
		this(Range.singleton(date), payout, payoutUnkown);
	}

	public Payout(final double payout, final double payoutUnkown) {
		this((Range<LocalDate>) null, payout, payoutUnkown);
	}

	public double getPayout() {
		return payout;
	}

	public double getPayoutUnkown() {
		return payoutUnkown;
	}

	public static Payout build(final Booking booking) {
		final LocalDate date = booking.getDateOfPayment();
		final double grossEarnings = booking.getGrossEarnings();
		final double cleaningFees = booking.getCleaningFees();
		final double comissonable = grossEarnings - cleaningFees;
		final double comisson = comissonable * booking.getServiceFeesPercent() / 100;
		final double result = grossEarnings - comisson;
		if (date == null) {
			return buildUnknown(result);
		}
		return build(date, result);
	}

	public Range<LocalDate> getDateRange() {
		return dateRange;
	}

	public static Payout buildUnknown(final double payoutUnknown) {
		return new Payout(0, payoutUnknown);
	}

	public static Payout build(final LocalDate date, final double payout) {
		return new Payout(date, payout, 0);
	}

	public Payout merge(final Payout anotherPayout) {
		final double newPayout = payout + anotherPayout.getPayout();
		final double newPayoutUnknown = payoutUnkown + anotherPayout.getPayoutUnkown();
		final Range<LocalDate> newRange;
		if (getDateRange() != null && anotherPayout.getDateRange() != null) {
			newRange = getDateRange().span(anotherPayout.getDateRange());
		} else if (getDateRange() == null) {
			newRange = anotherPayout.getDateRange();
		} else if (anotherPayout.getDateRange() == null) {
			newRange = getDateRange();
		} else {
			newRange = null;
		}
		return new Payout(newRange, newPayout, newPayoutUnknown);
	}

}
