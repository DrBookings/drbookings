package com.github.drbookings.model.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DateBeans {

    public static List<RoomBean> roomView(final String roomName, final List<DateBean> datesAfter) {
	final List<RoomBean> result = new ArrayList<>();
	for (final DateBean db : datesAfter) {
	    final Optional<RoomBean> rb = db.getRoom(roomName);
	    if (rb.isPresent()) {
		result.add(rb.get());
	    }
	}
	return result;
    }

}
