package com.github.drbookings.model;

public class DefaultNetEarningsCalculator implements NetEarningsCalculator {

    @Override
    public float getFees() {
	return fees;
    }

    @Override
    public DefaultNetEarningsCalculator setFees(final float fees) {
	this.fees = fees;
	return this;
    }

    @Override
    public float getProvision() {
	return provision;
    }

    @Override
    public DefaultNetEarningsCalculator setProvision(final float provision) {
	this.provision = provision;
	return this;
    }

    public static final float DEFAULT_PROVISION_BOOKING = 0.12f;

    private float provision = DEFAULT_PROVISION_BOOKING;

    private float fees = 0;

    public DefaultNetEarningsCalculator() {

    }

    // @Override
    // public float calculateNetEarnings(final MainManager manager, final
    // LocalDate date) {
    //
    // final Collection<BookingEntry> bookingEntries =
    // manager.getBookingEntries(date);
    //
    // final Collection<Booking> bookingBookings = new ArrayList<>();
    // final Collection<Booking> otherBookings = new ArrayList<>();
    //
    // // TODO hard coded check-out filter
    // bookingEntries.stream().filter(e -> !e.isCheckOut()).map(e ->
    // e.getElement()).forEach(
    // (b) -> (b.getBookingOrigin().getName().equalsIgnoreCase("booking") ?
    // bookingBookings : otherBookings)
    // .add(b));
    //
    // final double sumFees = Stream.concat(bookingBookings.stream(),
    // otherBookings.stream())
    // .mapToDouble(b -> b.getServiceFee()).sum() / (bookingBookings.size() +
    // otherBookings.size());
    //
    // final double sumBookingEarnings = bookingBookings.stream().mapToDouble(b
    // -> b.getGrossEarnings()).sum()
    // / bookingBookings.size();
    //
    // final double sumOtherEarnings = otherBookings.stream().mapToDouble(b ->
    // b.getGrossEarnings()).sum()
    // / otherBookings.size();
    //
    // final double provision = sumBookingEarnings * 0.12;
    //
    // final double sumCosts = sumFees + provision;
    //
    // final double result = sumBookingEarnings + sumOtherEarnings - sumCosts;
    //
    // System.err.println(result);
    //
    // return (float) result;
    // }

    @Override
    public float calculateNetEarnings(final float grossEarnings, final String bookingOrigin) {
	double result = (double) grossEarnings - getFees();
	if ("booking".equalsIgnoreCase(bookingOrigin)) {
	    result -= grossEarnings * getProvision();
	}
	// System.err.println("result: " + result);
	return (float) result;
    }

}
