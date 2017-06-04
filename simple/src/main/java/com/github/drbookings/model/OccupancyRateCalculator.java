package com.github.drbookings.model;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.github.drbookings.ui.beans.RoomBean;

public class OccupancyRateCalculator implements Function<Collection<? extends RoomBean>, Number> {

    @Override
    public Number apply(final Collection<? extends RoomBean> rooms) {
	if (rooms.isEmpty()) {
	    return Double.valueOf(0);
	}
	final int cntRooms = rooms.size();
	final float cntBusyRooms = rooms.stream().filter(r -> !r.getFilteredBookingEntries().stream()
		.filter(b -> !b.isCheckOut()).collect(Collectors.toList()).isEmpty()).count();
	return cntBusyRooms / cntRooms;
    }

}
