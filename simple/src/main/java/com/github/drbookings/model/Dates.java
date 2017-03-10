package com.github.drbookings.model;

import java.util.ArrayList;
import java.util.List;

import com.github.drbookings.model.bean.DateBean;
import com.github.drbookings.model.bean.RoomBean;

public class Dates {

    public static List<RoomBean> roomView(final String roomName, final List<DateBean> datesAfter) {
	final List<RoomBean> result = new ArrayList<>();
	for (final DateBean db : datesAfter) {
	    result.add(db.getRoom(roomName));
	}
	return result;
    }

}
