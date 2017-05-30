package com.github.drbookings.ui.controller;

import java.text.DecimalFormat;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.drbookings.model.data.Booking;
import com.github.drbookings.model.settings.SettingsManager;
import com.github.drbookings.ui.BookingsByOrigin;

public class StatusLabelStringFactory {

    private static final DecimalFormat decimalFormat = new DecimalFormat("#,###,###,##0");
    private final BookingsByOrigin bookings;

    public StatusLabelStringFactory(final BookingsByOrigin bookings) {
	this.bookings = bookings;
    }

    public String build() {
	final StringBuilder sb = new StringBuilder();

	final boolean completePayment = SettingsManager.getInstance().isCompletePayment();

	// we want total payment, since payment is done once
	final Set<Booking> airbnbBookings2 = bookings.getAirbnbBookings().stream().map(b -> b.getElement())
		.collect(Collectors.toSet());
	// we want total payment, since payment is done once
	final Set<Booking> bookingBookings2 = bookings.getBookingBookings().stream().map(b -> b.getElement())
		.collect(Collectors.toSet());
	// we want total payment, since payment is done once
	final Set<Booking> otherBookings2 = bookings.getOtherBookings().stream().map(b -> b.getElement())
		.collect(Collectors.toSet());

	final double sumAirbnb;
	final double sumBooking;
	final double sumOther;
	final double sumTotal;
	if (completePayment) {
	    sumAirbnb = airbnbBookings2.stream().mapToDouble(b -> b.getNetEarnings()).sum();
	    sumBooking = bookingBookings2.stream().mapToDouble(b -> b.getNetEarnings()).sum();
	    sumOther = otherBookings2.stream().mapToDouble(b -> b.getNetEarnings()).sum();
	    sumTotal = sumAirbnb + sumBooking + sumOther;
	} else {
	    sumAirbnb = bookings.getAirbnbBookings().stream().mapToDouble(b -> b.getNetEarnings()).sum();
	    // System.err.println(bookings.getAirbnbBookings());
	    sumBooking = bookings.getBookingBookings().stream().mapToDouble(b -> b.getNetEarnings()).sum();
	    sumOther = bookings.getOtherBookings().stream().mapToDouble(b -> b.getNetEarnings()).sum();
	    sumTotal = sumAirbnb + sumBooking + sumOther;
	}

	sb.append("Airbnb:");

	if (completePayment) {
	    sb.append(airbnbBookings2.stream().mapToLong(b -> b.getNumberOfNights()).sum());
	} else {
	    sb.append(bookings.getAirbnbBookings().stream().filter(b -> !b.isCheckOut()).count());
	}

	sb.append("(");

	sb.append(decimalFormat.format(sumAirbnb));
	sb.append(")");
	sb.append("\t Booking:");
	if (completePayment) {
	    sb.append(bookingBookings2.stream().mapToLong(b -> b.getNumberOfNights()).sum());
	} else {
	    sb.append(bookings.getBookingBookings().stream().filter(b -> !b.isCheckOut()).count());
	}
	sb.append("(");
	sb.append(decimalFormat.format(sumBooking));
	sb.append(")");
	sb.append("\t Other:");
	if (completePayment) {
	    sb.append(otherBookings2.stream().mapToLong(b -> b.getNumberOfNights()).sum());
	} else {
	    sb.append(bookings.getOtherBookings().stream().filter(b -> !b.isCheckOut()).count());
	}
	sb.append("(");
	sb.append(decimalFormat.format(sumOther));
	sb.append(")");
	sb.append("\t Total:");

	if (completePayment) {
	    sb.append(Stream
		    .concat(bookingBookings2.stream(), Stream.concat(airbnbBookings2.stream(), otherBookings2.stream()))
		    .mapToLong(b -> b.getNumberOfNights()).sum());
	} else {
	    sb.append(Stream
		    .concat(bookings.getBookingBookings().stream(),
			    Stream.concat(bookings.getAirbnbBookings().stream(), bookings.getOtherBookings().stream()))
		    .filter(b -> !b.isCheckOut()).count());
	}

	sb.append("(");
	sb.append(decimalFormat.format(sumTotal));
	sb.append(")");
	sb.append("\t Av.NetEarnings/Night:");
	final OptionalDouble av = Stream
		.concat(bookings.getBookingBookings().stream(),
			Stream.concat(bookings.getAirbnbBookings().stream(), bookings.getOtherBookings().stream()))
		.filter(b -> !b.isCheckOut()).mapToDouble(b -> b.getNetEarnings()).average();
	if (av.isPresent()) {
	    sb.append(decimalFormat.format(av.getAsDouble()));
	} else {
	    sb.append(decimalFormat.format(0.0));
	}
	return sb.toString();
    }

}
