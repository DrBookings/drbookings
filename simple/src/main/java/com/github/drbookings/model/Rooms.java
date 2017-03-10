package com.github.drbookings.model;

import java.util.List;

import com.github.drbookings.model.bean.BookingBean;
import com.github.drbookings.model.bean.RoomBean;

public class Rooms {

    public static boolean hasCheckOut(final RoomBean room) {
	if (room.getAfter().isPresent()) {
	    for (final BookingBean bb : room.getBookings()) {
		if (bb.isCheckOut()) {
		    return true;
		}
	    }
	    return false;
	} else {
	    return true;
	}
    }

    public static boolean hasCleaningAfter(final RoomBean room) {
	final List<RoomBean> nextRooms = DataModel.getInstance().getAllAfter(room);
	for (final RoomBean rb : nextRooms) {
	    if (rb.hasCleaning()) {
		return true;
	    }
	    if (rb.hasCheckIn()) {
		return false;
	    }
	}
	return false;
    }

}
