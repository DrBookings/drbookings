package com.github.drbookings.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.github.drbookings.model.bean.BookingBean;
import com.github.drbookings.model.bean.DateBean;
import com.github.drbookings.model.bean.RoomBean;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Rooms {

    public static List<BookingBean> bookingsView(final Collection<? extends RoomBean> data) {
	final List<BookingBean> result = new ArrayList<>();
	for (final RoomBean rb : data) {
	    result.addAll(rb.getBookings());
	}
	return result;
    }

    public static List<String> cleaningView(final Collection<? extends RoomBean> c) {
	final List<String> result = new ArrayList<>();
	for (final RoomBean rb : c) {
	    result.add(rb.getCleaning());
	}
	return result;
    }

    public static ObservableList<RoomBean> newObservableList(final DateBean date) {
	if (date.getRooms().isEmpty()) {
	    throw new IllegalArgumentException();
	}
	final ObservableList<RoomBean> result = FXCollections.observableArrayList(RoomBean.extractor());
	result.addAll(date.getRooms());
	return result;
    }

}
