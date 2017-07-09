package com.github.drbookings.model.data;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.model.EarningsProvider;
import com.github.drbookings.model.GrossEarningsProvider;
import com.github.drbookings.ui.BookingEntry;
import com.github.drbookings.ui.BookingsByOrigin;

public class BookingEntries {

	private final static Logger logger = LoggerFactory.getLogger(BookingEntries.class);

	public static Predicate<BookingEntry> HAS_CLEANING = b -> b.getElement().getCleaning() != null;

	public static Predicate<BookingEntry> PAYMENT_DONE = b -> b.getElement().isPaymentDone();

	public static long countCleanings(final Collection<? extends BookingEntry> bookings) {
		return bookings.stream().filter(HAS_CLEANING).map(b -> b.getElement().getCleaning()).collect(Collectors.toSet())
				.size();
	}

	public static long countNights(final BookingsByOrigin<? extends BookingEntry> bo, final boolean all) {
		long result = 0;
		result += countNightsAirbnb(bo.getAirbnbBookings());
		result += countNightsBookings(bo.getBookingBookings());
		if (all) {
			result += countNights(bo.getOtherBookings());
		}
		return result;

	}

	private static long countNightsBookings(final Collection<? extends BookingEntry> bookingBookings) {
		return bookingBookings.stream().map(b -> b.getElement()).collect(Collectors.toSet()).stream()
				.mapToLong(b -> b.getNumberOfNights()).sum();
	}

	private static long countNightsAirbnb(final Collection<? extends BookingEntry> airbnbBookings) {
		return countNights(airbnbBookings);
	}

	public static long countNights(final Collection<? extends BookingEntry> bookings) {
		return bookings.stream().filter(b -> !b.isCheckOut()).count();

	}

	public static double getCleaningCosts(final Collection<? extends BookingEntry> bookings) {
		return bookings.stream().filter(HAS_CLEANING).map(b -> b.getElement()).collect(Collectors.toSet()).stream()
				.mapToDouble(b -> b.getCleaning().getCleaningCosts()).sum();
	}

	public static double getCleaningFees(final Collection<? extends BookingEntry> bookings) {
		return bookings.stream().filter(HAS_CLEANING).map(b -> b.getElement()).collect(Collectors.toSet()).stream()
				.mapToDouble(b -> b.getCleaningFees()).sum();
	}

	public static double getGrossEarningsAirbnb(final Collection<? extends BookingEntry> bookings) {
		return getEarningsAirbnb(bookings, b -> b.getEarnings(false));
	}

	public static double getEarningsBooking(final Collection<? extends BookingEntry> bookings,
			final Function<EarningsProvider, Number> earningsProvider) {
		return bookings.stream().map(b -> b.getElement()).collect(Collectors.toSet()).stream()
				.filter(b -> b.isPaymentDone()).mapToDouble(b -> earningsProvider.apply(b).doubleValue()).sum();
	}

	public static double getEarningsGeneral(final Collection<? extends EarningsProvider> bookings,
			final Function<EarningsProvider, Number> earningsProvider) {
		return bookings.stream().filter(b -> b.isPaymentDone())
				.mapToDouble(b -> earningsProvider.apply(b).doubleValue()).sum();
	}

	public static double getGrossEarnings(final Collection<? extends BookingEntry> bookings) {
		double result = 0;
		final BookingsByOrigin<BookingEntry> bo = new BookingsByOrigin<>(bookings);
		result += getGrossEarningsCompleteBookings(bo.getBookingBookings());
		result += getGrossEarningsAirbnb(bo.getAirbnbBookings());
		result += getGrossEarningsSingleEntries(bo.getOtherBookings());
		return result;
	}

	public static double getEarningsAirbnb(final Collection<? extends BookingEntry> airbnbBookings,
			final Function<EarningsProvider, Number> earningsProvider) {

		double result = 0;

		final Map<Booking, Collection<BookingEntry>> map = new LinkedHashMap<>();
		for (final BookingEntry be : airbnbBookings) {
			final Collection<BookingEntry> value = map.getOrDefault(be.getElement(), new ArrayList<>());
			value.add(be);
			map.put(be.getElement(), value);
		}

		for (final Entry<Booking, Collection<BookingEntry>> en : map.entrySet()) {
			final LocalDate ci = en.getKey().getCheckIn();
			final int daysCurrentMonth = YearMonth.from(ci).lengthOfMonth();
			if (en.getValue().size() >= daysCurrentMonth) {
				if (logger.isInfoEnabled()) {
					logger.info("Split-payment for " + en.getKey() + ", days of month " + daysCurrentMonth);
				}
				result += getEarningsGeneral(en.getValue(), earningsProvider);
			} else {
				result += getEarningsBooking(en.getValue(), earningsProvider);
			}
		}

		return result;
	}

	public static double getGrossEarningsCompleteBookings(final Collection<? extends BookingEntry> bookingBookings) {
		return getEarningsBooking(bookingBookings, b -> b.getEarnings(false));
	}

	public static double getGrossEarningsSingleEntries(final Collection<? extends GrossEarningsProvider> bookings) {
		return getEarningsGeneral(bookings, b -> b.getEarnings(false));
	}

	public static double getNetEarnings(final Collection<? extends BookingEntry> bookings) {
		double result = 0;
		final BookingsByOrigin<BookingEntry> bo = new BookingsByOrigin<>(bookings);
		result += getNetEarningsBooking(bo.getBookingBookings());
		result += getNetEarningsAirbnb(bo.getAirbnbBookings());
		result += getNetEarningsGeneral(bo.getOtherBookings());
		return result;
	}

	public static double getNetEarningsAirbnb(final Collection<? extends BookingEntry> bookings) {
		return getEarningsAirbnb(bookings, b -> b.getEarnings(true));
	}

	public static double getNetEarningsBooking(final Collection<? extends BookingEntry> bookings) {
		return getEarningsBooking(bookings, b -> b.getEarnings(true));
	}

	public static double getNetEarningsGeneral(final Collection<? extends BookingEntry> bookings) {
		return getEarningsGeneral(bookings, b -> b.getEarnings(true));
	}

	public static double getServiceFees(final Collection<? extends BookingEntry> bookings) {
		return bookings.stream().filter(PAYMENT_DONE).map(b -> b.getElement()).collect(Collectors.toSet()).stream()
				.mapToDouble(b -> b.getServiceFee() + Bookings.getServiceFeePercentAmount(b)).sum();
	}

	public static long countNights(final String origin, final Collection<? extends BookingEntry> bookings) {
		if ("booking".equalsIgnoreCase(origin)) {
			return countNightsBookings(bookings);
		} else if ("airbnb".equalsIgnoreCase(origin)) {
			return countNightsAirbnb(bookings);
		}
		return countNights(bookings);
	}

}
