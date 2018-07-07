package com.github.drbookings.data.payments;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collection;

import javax.money.MonetaryAmount;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.DateRangeHandler;
import com.github.drbookings.LocalDates;
import com.github.drbookings.data.numbers.nights.NumberOfNightsCounter;
import com.github.drbookings.model.data.BookingBean;
import com.github.drbookings.model.data.BookingOrigin;
import com.github.drbookings.model.data.Expense;
import com.github.drbookings.model.settings.SettingsManager;
import com.github.drbookings.ui.BookingsByOrigin;
import com.google.common.collect.Range;

public class PayoutCalculator extends DateRangeHandler {

    public static final float DEFAULT_PAYOUT_FACTOR = 1f;

    private static final Logger logger = LoggerFactory.getLogger(PayoutCalculator.class);

    private final Collection<? extends BookingBean> bookings;

    private final Collection<? extends Expense> expenses;

    private final float payoutFactor;

    public PayoutCalculator(final Range<LocalDate> dates, final Collection<? extends BookingBean> bookings,
	    final Collection<? extends Expense> expenses) {
	this(dates, bookings, expenses, DEFAULT_PAYOUT_FACTOR);
    }

    public PayoutCalculator(final Range<LocalDate> dates, final Collection<? extends BookingBean> bookings,
	    final Collection<? extends Expense> expenses, final float payoutFactor) {
	super(dates);
	this.bookings = bookings;
	this.expenses = expenses;
	this.payoutFactor = payoutFactor;
    }

    public PayoutCalculator(final YearMonth month, final Collection<? extends BookingBean> bookings,
	    final Collection<? extends Expense> expenses) {
	this(month, bookings, expenses, DEFAULT_PAYOUT_FACTOR);
    }

    public PayoutCalculator(final YearMonth month, final Collection<? extends BookingBean> bookings,
	    final Collection<? extends Expense> expenses, final float payoutFactor) {
	super(month);
	this.bookings = bookings;
	this.expenses = expenses;
	this.payoutFactor = payoutFactor;
    }

    Collection<? extends BookingBean> getBookings() {
	return bookings;
    }

    public long getNumberOfMonths() {
	final long months = LocalDates.getNumberOfMonth(getDateRange());
	return months;
    }

    public MonetaryAmount getPayout(final BookingOrigin origin, final boolean cheat) {

	final short numRooms = (short) SettingsManager.getInstance().getNumberOfRooms();
	final MonetaryAmount fixCostsOneRoom = Payments
		.createMondary(SettingsManager.getInstance().getAdditionalCosts());
	final MonetaryAmount fixCostsAllRooms = fixCostsOneRoom.multiply(numRooms);
	return getPayout(origin, fixCostsAllRooms, cheat);
    }

    /**
     *
     * Note that {@code additionalCostsPerRoom} is the total amount of fix costs,
     * not the monthly amount.
     *
     * @param origin
     *            the booking origin to calculate the payout for
     *
     * @param additionalCostsAllRooms
     *            the fix costs per room for the whole time
     * @param cheat
     *            if {@code true}, consider only non-empty booking origins
     * @return
     */
    public MonetaryAmount getPayout(final BookingOrigin origin, final MonetaryAmount additionalCostsAllRooms,
	    final boolean cheat) {

	final BookingsByOrigin<BookingBean> bbo = new BookingsByOrigin<>(getBookings());

	final MonetaryAmount thisBookingsPaymentSum = Payments.getSum(bbo.getByOrigin(origin), getDateRange());

	final MonetaryAmount thisAdditionalCostsAllRooms = getFixkostenAnteil(origin, cheat, additionalCostsAllRooms);

	final MonetaryAmount result = thisBookingsPaymentSum.subtract(thisAdditionalCostsAllRooms);

	return result;
    }

    private MonetaryAmount getFixkostenAnteil(final BookingOrigin origin, final boolean cheat,
	    final MonetaryAmount additionalCostsAllRooms) {
	final double percentageThisBookings = getPercentageThisBookings(origin, cheat);
	final MonetaryAmount result = additionalCostsAllRooms.multiply(percentageThisBookings);
	return result;
    }

    private double getPercentageThisBookings(final BookingOrigin origin, final boolean cheat) {
	final BookingsByOrigin<BookingBean> bbo = new BookingsByOrigin<>(getBookings());
	final double allBookings = NumberOfNightsCounter.countNights(bbo.getAllBookings(cheat), getDateRange());
	final double thisBookings = NumberOfNightsCounter.countNights(bbo.getByOrigin(origin), getDateRange());
	final double result = thisBookings / allBookings;
	return result;
    }

    public MonetaryAmount getPayout(final String originName, final boolean cheat) {
	return getPayout(new BookingOrigin(originName), cheat);
    }

    public MonetaryAmount getPayout(final String originName, final MonetaryAmount totalFixCostsAllRooms,
	    final boolean cheat) {
	return getPayout(new BookingOrigin(originName), totalFixCostsAllRooms, cheat);
    }

    float getPayoutFactor() {
	return payoutFactor;
    }

}
