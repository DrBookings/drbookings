package com.github.drbookings.model;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.github.drbookings.ui.beans.RoomBean;

public class OccupancyRateCalculator implements Function<Collection<? extends RoomBean>, Number> {

    @Override
    public Number apply(final Collection<? extends RoomBean> rooms) {
	final double cntRooms = rooms.size();
	final double cntBusyRooms = rooms.stream().filter(r -> !r.getFilteredBookingEntries().stream()
		.filter(b -> !b.isCheckOut()).collect(Collectors.toList()).isEmpty()).count();
	return (float) (cntBusyRooms / cntRooms);
    }

}
