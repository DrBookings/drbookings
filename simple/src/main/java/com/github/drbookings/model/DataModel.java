package com.github.drbookings.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.DateRange;
import com.github.drbookings.OverbookingException;
import com.github.drbookings.model.bean.BookingBean;
import com.github.drbookings.model.bean.DateBean;
import com.github.drbookings.model.bean.RoomBean;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@XmlRootElement(name = "data")
public class DataModel {

    private static class InstanceHolder {
	private static final DataModel instance = new DataModel();
    }

    private static final Logger logger = LoggerFactory.getLogger(DataModel.class);

    public static DataModel getInstance() {
	return InstanceHolder.instance;
    }

    private final ObservableList<DateBean> data = FXCollections.observableArrayList(DateBean.extractor());

    private DataModel() {
    }

    public void add(final BookingDates bookings) throws OverbookingException {
	for (final DateBean b : bookings) {
	    add(b);
	}
	fillMissing();
    }

    public void add(final DateBean db) throws OverbookingException {
	if (data.contains(db)) {
	    final DateBean bb = data.get(data.indexOf(db));
	    bb.merge(db);
	    update(bb);
	} else {
	    data.add(db);
	}
    }

    public void fillMissing() {
	Collections.sort(data);
	for (final LocalDate d : new DateRange(data.get(0).getDate(), data.get(data.size() - 1).getDate())) {
	    try {
		add(new DateBean(d));
	    } catch (final OverbookingException e) {
		// cannot happen
		throw new RuntimeException(e);
	    }
	}
    }

    public Optional<BookingBean> getAfter(final BookingBean bb) {
	final Optional<DateBean> db2 = getAfter(bb.getRoomBean().getDateBean());
	if (db2.isPresent()) {
	    final DateBean db3 = db2.get();
	    final RoomBean rb3 = db3.getRoom(bb.getRoomBean().getName());
	    final Optional<BookingBean> bb3 = rb3.getBooking(bb.getGuestName());
	    return bb3;

	}
	return Optional.empty();
    }

    public Optional<DateBean> getAfter(final DateBean db) {
	final int index = data.indexOf(db) + 1;
	if (index < data.size()) {
	    final DateBean result = data.get(index);
	    if (result != null) {
		return Optional.of(result);
	    }
	}
	return Optional.empty();
    }

    public List<DateBean> getAfter(final LocalDate date) {
	final List<DateBean> result = data.stream().filter(db -> db.getDate().isAfter(date))
		.collect(Collectors.toList());
	return result;
    }

    public Optional<RoomBean> getAfter(final RoomBean roomBean) {
	final DateBean db = roomBean.getDateBean();
	final Optional<DateBean> dbAfter = getAfter(db);
	if (dbAfter.isPresent()) {
	    return Optional.of(dbAfter.get().getRoom(roomBean.getName()));
	}
	return Optional.empty();
    }

    public List<RoomBean> getAllAfter(final RoomBean room) {
	// Collections.sort(data);
	final List<DateBean> datesAfter = data.subList(data.indexOf(room.getDateBean()), data.size() - 1);
	final List<RoomBean> rooms = DateBeans.roomView(room.getName(), datesAfter);
	return rooms;
    }

    public Optional<BookingBean> getBefore(final BookingBean bb) {
	final RoomBean rb = bb.getRoomBean();
	final DateBean db = rb.getDateBean();
	final Optional<DateBean> db2 = getBefore(db);
	if (db2.isPresent()) {
	    final DateBean db3 = db2.get();
	    final RoomBean rb3 = db3.getRoom(rb.getName());
	    final List<BookingBean> bb3 = rb3.getBookings();
	    if (!bb3.isEmpty()) {
		return Optional.of(bb3.get(bb3.size() - 1));
	    }
	}
	return Optional.empty();
    }

    public Optional<DateBean> getBefore(final DateBean db) {
	final int index = data.indexOf(db) - 1;
	if (index >= 0) {
	    final DateBean result = data.get(index);
	    if (result != null) {
		return Optional.of(result);
	    }
	}
	return Optional.empty();
    }

    @XmlElementWrapper(name = "dates")
    @XmlElement(name = "date")
    public ObservableList<DateBean> getData() {
	return data;
    }

    public List<BookingBean> getLaterBookingsForRoom(final RoomBean rb) {
	final List<BookingBean> result = new ArrayList<>();
	for (final DateBean db : data) {
	    if (db.getDate().isAfter(rb.getDateBean().getDate())) {
		final RoomBean rb2 = db.getRoom(rb.getName());
		final List<BookingBean> bb2 = rb2.getBookings();
		result.addAll(bb2);
	    }
	}
	return result;
    }

    public int getNumberOfBookingDays(final LocalDate startDate, final LocalDate endDate, final String string) {
	int result = 0;
	for (final DateBean db : data) {
	    if (db.getDate().isAfter(startDate) && db.getDate().isBefore(endDate)) {
		for (final RoomBean rb : db) {
		    for (final BookingBean bb : rb.getBookings()) {
			if (bb.getSource().equalsIgnoreCase(string)) {
			    result++;
			}
		    }
		}
	    }
	}
	return result;
    }

    public void removeAll(final BookingBean... bookings) {
	final List<Integer> changed = new ArrayList<>();
	for (final ListIterator<DateBean> it = data.listIterator(); it.hasNext();) {
	    final int index = it.nextIndex();
	    final DateBean next = it.next();
	    for (final RoomBean nextRoom : next) {
		for (final Iterator<BookingBean> itBooking = nextRoom.bookingsProperty().iterator(); itBooking
			.hasNext();) {
		    final BookingBean bb = itBooking.next();
		    for (final BookingBean bbb : bookings) {
			if (bb.equals(bbb)) {
			    itBooking.remove();
			    changed.add(index);
			}
		    }
		}
	    }
	}
	for (final Integer i : changed) {
	    data.set(i, data.get(i));
	}
    }

    public void removeAll(final Collection<? extends BookingBean> bookings) {
	removeAll(bookings.toArray(new BookingBean[bookings.size()]));
    }

    public void setAll(final Collection<? extends DateBean> data) {
	this.data.setAll(data);

    }

    private void update(final DateBean dateBean) {
	final int index = data.indexOf(dateBean);
	data.set(index, dateBean);
    }

    public void update(final RoomBean roomBean) {
	update(roomBean.getDateBean());
    }
}
