package com.github.drbookings.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlID;
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

    private static final Logger logger = LoggerFactory.getLogger(DataModel.class);

    private final ObservableList<DateBean> data = FXCollections.observableArrayList(DateBean.extractor());

    private final Map<LocalDate, DateBean> dataMap = new LinkedHashMap<>();

    private String id;

    public DataModel() {
	setId(UUID.randomUUID().toString());
    }

    public synchronized void add(final Collection<? extends DateBean> dates) throws OverbookingException {
	for (final DateBean b : dates) {
	    add(b);
	}
	fillMissing();
    }

    public synchronized void add(final DateBean db) throws OverbookingException {
	final DateBean db2 = dataMap.get(db.getDate());
	if (db2 == null) {
	    data.add(db);
	    dataMap.put(db.getDate(), db);
	    db.setDataModel(this);
	} else {
	    db2.merge(db);
	}
    }

    synchronized void add(final LocalDate date) {
	try {
	    add(new DateBean(date));
	} catch (final OverbookingException e) {
	    // cannot happen
	    e.printStackTrace();
	}
    }

    public synchronized float calculateBruttoEarningsPerNight(final BookingBean booking) {
	if (isCheckOut(booking)) {
	    return 0;
	}
	final int nightCount = getNightCount(booking);
	return booking.getBruttoEarnings() / nightCount;
    }

    public synchronized void fillMissing() {
	final List<LocalDate> keyList = new ArrayList<>(dataMap.keySet());
	final Collection<LocalDate> toAdd = new HashSet<>();
	Collections.sort(keyList);
	final LocalDate last = null;
	for (final LocalDate d : keyList) {
	    if (last != null) {
		if (d.equals(last.plusDays(1))) {
		    // ok
		} else {
		    toAdd.addAll(new DateRange(last, d).toList());
		}
	    }
	}
	for (final LocalDate d : toAdd) {
	    add(d);
	}
    }

    public synchronized List<DateBean> getAfter(final LocalDate date) {

	final List<DateBean> result = data.stream().filter(db -> db.getDate().isAfter(date.minusDays(1)))
		.collect(Collectors.toList());
	return result;
    }

    public synchronized List<BookingBean> getAllSame(final BookingBean bookingBean) {
	final List<BookingBean> result = new ArrayList<>();
	result.add(bookingBean);
	Optional<BookingBean> bb = getConnectedPrevious(bookingBean);
	while (bb.isPresent()) {
	    result.add(bb.get());
	    bb = getConnectedPrevious(bb.get());
	}
	bb = getConnectedNext(bookingBean);
	while (bb.isPresent()) {
	    result.add(bb.get());
	    bb = getConnectedNext(bb.get());
	}
	return result;
    }

    public synchronized double getBruttoEarnings(final LocalDate startDate, final LocalDate endDate,
	    final String source) {

	double result = 0;
	for (final DateBean db : data) {
	    if (db.getDate().isAfter(startDate.minusDays(1)) && db.getDate().isBefore(endDate)) {
		for (final RoomBean rb : db) {
		    for (final BookingBean bb : rb.getBookings()) {
			if (bb.getSource().matches(source)) {
			    result += bb.getBruttoEarningsPerNight();
			}
		    }
		}
	    }
	}
	return result;
    }

    public synchronized Map<String, List<BookingBean>> getByGuestName(final LocalDate startDate,
	    final LocalDate endDate) {
	final Map<String, List<BookingBean>> map = new LinkedHashMap<>();
	for (final DateBean db : data) {
	    if (db.getDate().isAfter(startDate) && db.getDate().isBefore(endDate.plusDays(1))) {
		for (final RoomBean rb : db) {
		    for (final BookingBean bb : rb.getBookings()) {
			List<BookingBean> bookings = map.get(bb.getGuestName());
			if (bookings == null) {
			    bookings = new ArrayList<>();
			    map.put(bb.getGuestName(), bookings);
			}
			bookings.add(bb);
		    }
		}
	    }
	}
	return map;
    }

    /**
     * @return the first booking that is check-in
     */
    public Optional<BookingBean> getCheckIn(final RoomBean room) {
	for (final BookingBean bb : room.getBookings()) {
	    if (isCheckIn(bb)) {
		return Optional.of(bb);
	    }
	}
	return Optional.empty();
    }

    /**
     * @return the first booking that is check-out
     */
    public Optional<BookingBean> getCheckOut(final RoomBean room) {
	for (final BookingBean bb : room.getBookings()) {
	    if (isCheckOut(bb)) {
		return Optional.of(bb);
	    }
	}
	return Optional.empty();
    }

    public synchronized Optional<BookingBean> getConnectedNext(final BookingBean bookingBean) {
	final Optional<RoomBean> room = getConnectedNext(bookingBean.getRoom());
	if (room.isPresent()) {
	    return room.get().getBooking(bookingBean.getGuestName());
	}
	return Optional.empty();
    }

    public synchronized Optional<DateBean> getConnectedNext(final DateBean db) {
	final DateBean result = dataMap.get(db.getDate().plusDays(1));
	if (result != null) {
	    return Optional.of(result);
	}
	return Optional.empty();
    }

    public synchronized Optional<RoomBean> getConnectedNext(final RoomBean room) {
	final Optional<DateBean> db = getConnectedNext(room.getDateBean());
	if (db.isPresent()) {
	    return Optional.of(db.get().getRoom(room.getName()));
	}
	return Optional.empty();
    }

    public synchronized Optional<BookingBean> getConnectedPrevious(final BookingBean bookingBean) {
	final Optional<RoomBean> room = getConnectedPrevious(bookingBean.getRoom());
	if (room.isPresent()) {
	    return room.get().getBooking(bookingBean.getGuestName());
	}
	return Optional.empty();
    }

    public synchronized Optional<DateBean> getConnectedPrevious(final DateBean db) {
	final DateBean result = dataMap.get(db.getDate().minusDays(1));
	if (result != null) {
	    return Optional.of(result);
	}
	return Optional.empty();
    }

    public synchronized Optional<RoomBean> getConnectedPrevious(final RoomBean room) {
	final Optional<DateBean> db = getConnectedPrevious(room.getDateBean());
	if (db.isPresent()) {
	    return Optional.of(db.get().getRoom(room.getName()));
	}
	return Optional.empty();
    }

    @XmlElementWrapper(name = "dates")
    @XmlElement(name = "date")
    public synchronized ObservableList<DateBean> getData() {
	return data;
    }

    @XmlID
    @XmlAttribute
    public String getId() {
	return id;
    }

    public synchronized List<BookingBean> getLaterBookingsForRoom(final RoomBean rb) {
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

    public int getNightCount(final BookingBean bb) {
	return getAllSame(bb).size() - 1;
    }

    public synchronized int getNumberOfBookingDays(final LocalDate startDate, final LocalDate endDate,
	    final String source) {
	int result = 0;
	for (final DateBean db : data) {
	    if (db.getDate().isAfter(startDate) && db.getDate().isBefore(endDate.plusDays(1))) {
		for (final RoomBean rb : db) {
		    for (final BookingBean bb : rb.getBookings()) {
			if (bb.getSource().matches(source)) {
			    result++;
			}
		    }
		}
	    }
	}
	return result;
    }

    public synchronized int getNumberOfBookingNights(final LocalDate startDate, final LocalDate endDate,
	    final String source) throws IllegalStateException {
	final int days = getNumberOfBookingDays(startDate, endDate, source);
	final int numberOfDistinctBookings = getNumberOfDistinctBookings(startDate, endDate, source);
	if (logger.isDebugEnabled()) {
	    logger.debug("Distinct bookings for " + source + ": " + numberOfDistinctBookings);
	}
	return days - numberOfDistinctBookings;
    }

    public synchronized int getNumberOfDistinctBookings(final LocalDate startDate, final LocalDate endDate,
	    final String source) throws IllegalStateException {
	final Map<String, List<BookingBean>> map = getByGuestName(startDate, endDate);
	if (map.isEmpty()) {
	    return 0;
	}
	int cnt = 0;
	for (final Entry<String, List<BookingBean>> e : map.entrySet()) {
	    boolean foundMatch = false;
	    final List<BookingBean> bookings = e.getValue();
	    BookingBean last = null;
	    for (final BookingBean b : bookings) {
		if (b.getSource().matches(source)) {
		    foundMatch = true;
		    if (last != null) {
			if (!last.isConnected(b)) {
			    cnt++;
			}
		    }
		    last = b;
		}
	    }
	    if (foundMatch) {
		cnt++;
	    }
	}
	return cnt;
    }

    public boolean hasCheckIn(final RoomBean room) {
	return getCheckIn(room).isPresent();
    }

    public boolean hasCheckOut(final RoomBean room) {
	return getCheckOut(room).isPresent();
    }

    public synchronized boolean isCheckIn(final BookingBean booking) {
	final Optional<BookingBean> bb = getConnectedPrevious(booking);
	if (bb.isPresent()) {
	    if (bb.get().hasGuest() || bb.get().getGuestName().equals(booking.getGuestName())) {
		return false;
	    }
	}
	return true;
    }

    public synchronized boolean isCheckOut(final BookingBean booking) {
	final Optional<BookingBean> bb = getConnectedNext(booking);
	if (bb.isPresent()) {
	    if (booking.getGuestName().equals(bb.get().getGuestName())) {
		return false;
	    }
	}
	return true;
    }

    public synchronized void removeAll(final BookingBean... bookings) {
	final List<BookingBean> allall = new ArrayList<>();
	for (final BookingBean bbb : bookings) {
	    allall.addAll(getAllSame(bbb));
	}
	for (final Iterator<DateBean> it = data.listIterator(); it.hasNext();) {
	    final DateBean next = it.next();
	    for (final RoomBean nextRoom : next) {
		for (final Iterator<BookingBean> itBooking = nextRoom.bookingsProperty().iterator(); itBooking
			.hasNext();) {
		    final BookingBean bb = itBooking.next();
		    for (final BookingBean bbb : allall) {
			if (bb.equals(bbb)) {
			    itBooking.remove();
			}
		    }
		}
	    }
	}
    }

    public void removeAll(final Collection<? extends BookingBean> bookings) {
	removeAll(bookings.toArray(new BookingBean[bookings.size()]));
    }

    public void setAllBruttoEarnings(final BookingBean booking, final float earnings) {
	final List<BookingBean> bookings = getAllSame(booking);
	for (final BookingBean bb : bookings) {
	    bb.setBruttoEarnings(earnings);
	}
    }

    public synchronized void setData(final Collection<? extends DateBean> data) throws OverbookingException {
	this.data.clear();
	for (final DateBean db : data) {
	    add(db);
	}
    }

    private void setId(final String id) {
	this.id = id;
    }
}
