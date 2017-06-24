package com.github.drbookings.ui;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.OptionalDouble;

import com.github.drbookings.model.BookingEntryToBooking;
import com.github.drbookings.model.EarningsProvider;
import com.github.drbookings.model.settings.SettingsManager;

public class StatusLabelStringFactory {

	public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,###,###,##0");

	private static final BookingEntryToBooking bookingEntryToBooking = new BookingEntryToBooking();

	private static String buildAirbnbStringNightCount(final BookingsByOrigin<BookingEntry> bookings,
			final boolean complete) {
		return buildStringNightCount("Airbnb", bookings.getAirbnbBookings(), complete);
	}

	private static String buildBookingStringNightCount(final BookingsByOrigin<BookingEntry> bookings,
			final boolean complete) {
		return buildStringNightCount("Booking", bookings.getBookingBookings(), complete);
	}

	private static String buildOtherStringNightCount(final BookingsByOrigin<BookingEntry> bookings,
			final boolean complete) {
		return buildStringNightCount("Other", bookings.getOtherBookings(), complete);
	}

	private static String buildStringEarnings(final Collection<? extends BookingEntry> bookings,
			final boolean completePayment, final boolean netEarnings) {
		return DECIMAL_FORMAT.format(getEarningsSum(bookings, netEarnings, completePayment));
	}

	private static String buildStringNightCount(final String prefix, final Collection<? extends BookingEntry> bookings,
			final boolean complete) {
		if (complete) {
			return prefix + ":" + bookingEntryToBooking.apply(bookings).stream().count();
		}
		return prefix + ":" + bookings.stream().filter(new NightCountFilter()).count();
	}

	private static Collection<? extends EarningsProvider> getEarningsProvider(
			final Collection<? extends BookingEntry> bookings, final boolean completePayment) {
		Collection<? extends EarningsProvider> result;
		if (completePayment) {
			result = bookingEntryToBooking.apply(bookings);
		} else {
			result = bookings;
		}
		return result;
	}

	private static double getEarningsSum(final Collection<? extends BookingEntry> bookings, final boolean netEarnings,
			final boolean completePayment) {

		return getEarningsProvider(bookings, completePayment).stream().mapToDouble(b -> b.getEarnings(netEarnings))
				.sum();
	}

	private final BookingsByOrigin<BookingEntry> bookings;

	public StatusLabelStringFactory(final BookingsByOrigin<BookingEntry> bookings) {
		this.bookings = bookings;
	}

	public String build() {

		final boolean completePayment = SettingsManager.getInstance().isCompletePayment();
		final boolean netEarnings = SettingsManager.getInstance().isShowNetEarnings();

		return build(completePayment, netEarnings);

	}

	private String build(final boolean completePayment, final boolean netEarnings) {
		final StringBuilder sb = new StringBuilder();
		// sb.append(buildAirbnbStringNightCount(bookings, completePayment));
		// sb.append("(");
		// sb.append(buildStringEarnings(bookings.getAirbnbBookings(),
		// completePayment, netEarnings));
		// sb.append(")\t");
		// sb.append(buildBookingStringNightCount(bookings, completePayment));
		// sb.append("(");
		// sb.append(buildStringEarnings(bookings.getBookingBookings(),
		// completePayment, netEarnings));
		// sb.append(")\t");
		// sb.append(buildOtherStringNightCount(bookings, completePayment));
		// sb.append("(");
		// sb.append(buildStringEarnings(bookings.getOtherBookings(),
		// completePayment, netEarnings));
		// sb.append(")\t");
		// sb.append(buildStringNightCount("Total", bookings.getAllBookings(),
		// completePayment));
		// sb.append("(");
		// sb.append(buildStringEarnings(bookings.getAllBookings(),
		// completePayment, netEarnings));
		// sb.append(")");
		sb.append("\tAv.Earnings/Night/Room:");
		final OptionalDouble av = bookings.getAllBookings().stream().filter(b -> !b.isCheckOut())
				.mapToDouble(b -> b.getEarnings(netEarnings)).average();
		if (av.isPresent()) {
			sb.append(DECIMAL_FORMAT.format(av.getAsDouble()));
		} else {
			sb.append(DECIMAL_FORMAT.format(0.0));
		}

		return sb.toString();
	}

}
