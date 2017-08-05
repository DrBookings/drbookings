package com.github.drbookings.ui;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.OptionalDouble;

import com.github.drbookings.model.BookingEntryToBooking;
import com.github.drbookings.model.EarningsProvider;
import com.github.drbookings.model.data.BookingEntries;
import com.github.drbookings.model.settings.SettingsManager;
import com.github.drbookings.ui.provider.MinimumPriceProvider;
import com.github.drbookings.ui.provider.OccupancyRateProvider;

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
		if (bookings.isEmpty()) {
			return "";
		}
		final boolean completePayment = SettingsManager.getInstance().isCompletePayment();
		final boolean netEarnings = SettingsManager.getInstance().isShowNetEarnings();
		return build(completePayment, netEarnings);

	}

	private String build(final boolean completePayment, final boolean netEarnings) {
		final StringBuilder sb = new StringBuilder();
		sb.append(BookingEntries.getMinDate(bookings.getAllBookings()).get());
		sb.append(" ▶ ");
		sb.append(BookingEntries.getMaxDate(bookings.getAllBookings()).get());
		sb.append("\tEarnings:");
		sb.append(DECIMAL_FORMAT.format(bookings.getAllBookings(false).stream().filter(b -> !b.isCheckOut())
				.mapToDouble(b -> b.getEarnings(netEarnings)).sum()));
		sb.append("\tAv.Earnings/Night/Room:");
		final OptionalDouble av = bookings.getAllBookings(false).stream().filter(b -> !b.isCheckOut())
				.mapToDouble(b -> b.getEarnings(netEarnings)).average();
		if (av.isPresent()) {
			sb.append(DECIMAL_FORMAT.format(av.getAsDouble()));
		} else {
			sb.append(DECIMAL_FORMAT.format(0));
		}
		sb.append("\tOccupancyRate:");
		sb.append(StatusLabelStringFactory.DECIMAL_FORMAT.format(new OccupancyRateProvider().getOccupancyRate() * 100));
		sb.append("\tMinPriceAtRate:");
		sb.append(StatusLabelStringFactory.DECIMAL_FORMAT.format(new MinimumPriceProvider().getMinimumPrice()));
		return sb.toString();
	}

}
