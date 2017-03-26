package com.github.drbookings.model.manager;

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
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.DateRange;
import com.github.drbookings.OverbookingException;
import com.github.drbookings.model.BookingSourceFilter;
import com.github.drbookings.model.ModelConfiguration;
import com.github.drbookings.model.ModelConfiguration.NightCounting;
import com.github.drbookings.model.bean.BookingBean;
import com.github.drbookings.model.bean.DateBean;
import com.github.drbookings.model.bean.RoomBean;
import com.github.drbookings.ser.DataStore;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

public class DataModel {

    private static final Logger logger = LoggerFactory.getLogger(DataModel.class);

    private static Predicate<BookingBean> getBookingSourceRegexFilter(final String bookingSourceRegex) {
	return b -> b.getSource().matches(bookingSourceRegex);
    }

    private static Predicate<BookingBean> getInclusiveAfterDateFilter(final LocalDate date) {
	return b -> b.getDate().plusDays(1).isAfter(date);
    }

    private static Predicate<BookingBean> getInclusiveBeforeDateFilter(final LocalDate date) {
	return b -> b.getDate().minusDays(1).isBefore(date);
    }

    private final ObjectProperty<ModelConfiguration> modelConfiguration = new SimpleObjectProperty<>(
	    new ModelConfiguration());

    private final ObservableList<DateBean> data = FXCollections.observableArrayList(DateBean.extractor());

    private final ObservableMap<LocalDate, DateBean> dataMap = FXCollections.observableMap(new LinkedHashMap<>());

    private String id;

    public DataModel() {
	setId(UUID.randomUUID().toString());
    }

    public synchronized DateBean add(final BookingBean booking) throws OverbookingException {

	final RoomBean rb = booking.getRoom();
	if (rb == null) {
	    throw new IllegalArgumentException("Booking room not set");
	}
	return add(rb.getDateBean());
    }

    public synchronized void add(final Collection<? extends DateBean> dates) throws OverbookingException {
	for (final DateBean b : dates) {
	    add(b);
	}
	fillMissing();
    }

    public synchronized DateBean add(final DateBean db) throws OverbookingException {
	Objects.requireNonNull(db);
	Objects.requireNonNull(db.getDate());
	DateBean db2 = dataMap.get(db.getDate());
	if (db2 == null) {
	    db2 = addNoCheck(db);
	} else {
	    db2.merge(db);
	}
	fillMissing();
	return db2;
    }

    public void addAll(final Collection<? extends BookingBean> bookings) throws OverbookingException {
	for (final BookingBean bb : bookings) {
	    add(bb);
	}

    }

    private synchronized DateBean addNoCheck(final DateBean db) {
	Objects.requireNonNull(db);
	data.add(db);
	dataMap.put(db.getDate(), db);
	db.setDataModel(this);
	Collections.sort(data);
	// if (logger.isDebugEnabled()) {
	// logger.debug("Added " + db);
	// }
	return db;
    }

    private void addNoCheck(final LocalDate d) {
	addNoCheck(new DateBean(d));

    }

    public synchronized void applyFilter() {

    }

    public void applyGuestNameFilter(final String guestNameFilterString) {
	// if (logger.isDebugEnabled()) {
	// logger.debug("Applying filter");
	// }
	for (final DateBean db : data) {
	    db.applyGuestNameFilter(guestNameFilterString);
	}
    }

    public synchronized double calculateBruttoEarningsPerNight(final BookingBean booking) {
	final NightCounting nightCounting = getModelConfiguration().getNightCounting();
	switch (nightCounting) {
	case DAY_AFTER:
	    if (isCheckIn(booking)) {
		return 0;
	    }
	    break;
	case DAY_BEFORE:
	    if (isCheckOut(booking)) {
		return 0;
	    }
	    break;
	default:
	    break;
	}
	final int nightCount = getNightCount(booking);
	return booking.getGrossEarnings() / nightCount;
    }

    public synchronized double calculateNettoEarningsPerNight(final BookingBean booking) {
	final NightCounting nightCounting = getModelConfiguration().getNightCounting();
	switch (nightCounting) {
	case DAY_AFTER:
	    if (isCheckIn(booking)) {
		return 0;
	    }
	    break;
	case DAY_BEFORE:
	    if (isCheckOut(booking)) {
		return 0;
	    }
	    break;
	default:
	    break;
	}
	final int nightCount = getNightCount(booking);
	return booking.getNettoEarnings() / nightCount;
    }

    public synchronized void fillMissing() {
	final List<LocalDate> keyList = new ArrayList<>(dataMap.keySet());
	final Collection<LocalDate> toAdd = new HashSet<>();
	Collections.sort(keyList);
	LocalDate last = null;
	for (final LocalDate d : keyList) {
	    if (last != null) {
		if (d.equals(last.plusDays(1))) {
		    // ok
		} else {
		    toAdd.addAll(new DateRange(last.plusDays(1), d.minusDays(1)).toList());
		}
	    }
	    last = d;
	}
	for (final LocalDate d : toAdd) {
	    addNoCheck(d);
	}
    }

    public synchronized List<DateBean> getAfter(final LocalDate date) {

	final List<DateBean> result = data.stream().filter(db -> db.getDate().isAfter(date))
		.collect(Collectors.toList());
	return result;
    }

    public List<BookingBean> getAllBookings() {
	final List<BookingBean> result = new ArrayList<>();
	for (final DateBean db : data) {
	    result.addAll(db.getBookings());
	}
	return result;
    }

    public Stream<BookingBean> getAllBookingsStream() {
	final List<Stream<BookingBean>> bookingStreams = getBookingStreams();
	if (bookingStreams.isEmpty()) {
	    return Stream.empty();
	}
	Stream<BookingBean> result = bookingStreams.get(0);
	for (int i = 1; i < bookingStreams.size(); i++) {
	    result = Stream.concat(result, bookingStreams.get(i));
	}
	return result;
    }

    public synchronized Stream<BookingBean> getAllBookingsStreamBetween(final LocalDate startDate,
	    final LocalDate endDate) {
	return getAllBookingsStream()
		.filter(getInclusiveAfterDateFilter(startDate).and(getInclusiveBeforeDateFilter(endDate)));
    }

    public synchronized Stream<BookingBean> getAllBookingsStreamBetween(final LocalDate startDate,
	    final LocalDate endDate, final String regexSource) {
	return getAllBookingsStreamBetween(startDate, endDate).filter(getBookingSourceRegexFilter(regexSource));
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

    public List<BookingBean> getBookings() {
	return getAllBookingsStream().collect(Collectors.toList());
    }

    public List<Stream<BookingBean>> getBookingStreams() {
	return getData().stream().map(d -> d.getBookings().stream()).collect(Collectors.toList());
    }

    public synchronized double getBruttoEarnings(final LocalDate startDate, final LocalDate endDate,
	    final String regexSource) {
	return getAllBookingsStreamBetween(startDate, endDate, regexSource)
		.mapToDouble(b -> b.getNettoEarningsPerNight()).sum();
    }

    public double getBruttoEarnings(final String bookingSource) {
	return getAllBookingsStream().filter(new BookingSourceFilter(bookingSource))
		.mapToDouble(b -> b.getNettoEarningsPerNight()).sum();
    }

    public synchronized Map<String, List<BookingBean>> getByGuestName() {
	final Map<String, List<BookingBean>> map = new LinkedHashMap<>();
	for (final Iterator<BookingBean> bookingsIterator = getAllBookingsStream().iterator(); bookingsIterator
		.hasNext();) {
	    final BookingBean nextBooking = bookingsIterator.next();
	    List<BookingBean> bookings = map.get(nextBooking.getGuestName());
	    if (bookings == null) {
		bookings = new ArrayList<>();
		map.put(nextBooking.getGuestName(), bookings);
	    }
	    bookings.add(nextBooking);
	}
	return map;
    }

    public synchronized Map<String, List<BookingBean>> getByGuestName(final LocalDate startDate,
	    final LocalDate endDate) {
	final Map<String, List<BookingBean>> map = new LinkedHashMap<>();
	for (final Iterator<BookingBean> bookingsIterator = getAllBookingsStreamBetween(startDate, endDate)
		.iterator(); bookingsIterator.hasNext();) {
	    final BookingBean nextBooking = bookingsIterator.next();
	    List<BookingBean> bookings = map.get(nextBooking.getGuestName());
	    if (bookings == null) {
		bookings = new ArrayList<>();
		map.put(nextBooking.getGuestName(), bookings);
	    }
	    bookings.add(nextBooking);
	}
	return map;
    }

    public synchronized Map<String, List<BookingBean>> getByGuestName(final String bookingSource) {
	final Map<String, List<BookingBean>> map = new LinkedHashMap<>();
	for (final Iterator<BookingBean> bookingsIterator = getAllBookingsStream()
		.filter(new BookingSourceFilter(bookingSource)).iterator(); bookingsIterator.hasNext();) {
	    final BookingBean nextBooking = bookingsIterator.next();
	    List<BookingBean> bookings = map.get(nextBooking.getGuestName());
	    if (bookings == null) {
		bookings = new ArrayList<>();
		map.put(nextBooking.getGuestName(), bookings);
	    }
	    bookings.add(nextBooking);
	}
	return map;
    }

    /**
     * @return the first booking that is check-in
     */
    public synchronized Optional<BookingBean> getCheckIn(final RoomBean room) {
	for (final BookingBean bb : room.getFilteredBookings()) {
	    if (isCheckIn(bb)) {
		return Optional.of(bb);
	    }
	}
	return Optional.empty();
    }

    /**
     * @return the first booking that is check-out
     */
    public synchronized Optional<BookingBean> getCheckOut(final RoomBean room) {
	for (final BookingBean bb : room.getFilteredBookings()) {
	    if (isCheckOut(bb)) {
		return Optional.of(bb);
	    }
	}
	return Optional.empty();
    }

    public synchronized Optional<BookingBean> getConnectedNext(final BookingBean bookingBean) {
	final Optional<RoomBean> room = getConnectedNext(bookingBean.getRoom());
	if (room.isPresent()) {
	    return room.get().getConnectedBooking(bookingBean);
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
	    return db.get().getRoom(room.getName());
	}
	return Optional.empty();
    }

    public synchronized Optional<BookingBean> getConnectedPrevious(final BookingBean bookingBean) {
	final Optional<RoomBean> room = getConnectedPrevious(bookingBean.getRoom());
	if (room.isPresent()) {
	    return room.get().getConnectedBooking(bookingBean);
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
	    return db.get().getRoom(room.getName());
	}
	return Optional.empty();
    }

    public synchronized ObservableList<DateBean> getData() {
	return data;
    }

    public Optional<DateBean> getData(final LocalDate date) {
	final DateBean db = dataMap.get(date);
	if (db == null) {
	    return Optional.empty();
	}
	return Optional.of(db);
    }

    protected synchronized ObservableList<DateBean> getDataRaw() {
	return data;
    }

    public String getId() {
	return id;
    }

    public synchronized List<BookingBean> getLaterBookingsForRoom(final RoomBean rb) {
	final List<BookingBean> result = new ArrayList<>();
	for (final DateBean db : data) {
	    if (db.getDate().isAfter(rb.getDateBean().getDate())) {
		final Optional<RoomBean> rb2 = db.getRoom(rb.getName());
		if (rb2.isPresent()) {
		    final List<BookingBean> bb2 = rb2.get().getAllBookings();
		    result.addAll(bb2);
		}
	    }
	}
	return result;
    }

    public ModelConfiguration getModelConfiguration() {
	return this.modelConfigurationProperty().get();
    }

    public int getNightCount(final BookingBean bb) {
	final int allSize = getAllSame(bb).size();
	return allSize - 1;
    }

    public synchronized long getNumberOfBookingDays(final LocalDate startDate, final LocalDate endDate) {
	return getAllBookingsStreamBetween(startDate, endDate).count();
    }

    public synchronized long getNumberOfBookingDays(final LocalDate startDate, final LocalDate endDate,
	    final String source) {
	return getAllBookingsStreamBetween(startDate, endDate, source).count();
    }

    public synchronized long getNumberOfBookingDays(final String bookingSource) {
	return getAllBookingsStream().filter(new BookingSourceFilter(bookingSource)).count();
    }

    public synchronized long getNumberOfBookingNights(final LocalDate startDate, final LocalDate endDate) {
	final long days = getNumberOfBookingDays(startDate, endDate);
	final long numberOfDistinctBookings = getNumberOfDistinctBookings(startDate, endDate);
	return days - numberOfDistinctBookings;
    }

    public synchronized long getNumberOfBookingNights(final LocalDate startDate, final LocalDate endDate,
	    final String source) {
	final long days = getNumberOfBookingDays(startDate, endDate, source);
	final long numberOfDistinctBookings = getNumberOfDistinctBookings(startDate, endDate, source);
	final long result = days - numberOfDistinctBookings;
	return result;
    }

    public synchronized long getNumberOfBookingNights(final String bookingSource) {
	final long result = getAllBookingsStream().filter(new BookingSourceFilter(bookingSource))
		.filter(b -> !b.isCheckOut()).count();
	return result;

    }

    public synchronized int getNumberOfDistinctBookings() {
	final Map<String, List<BookingBean>> map = getByGuestName();
	if (map.isEmpty()) {
	    return 0;
	}
	int cnt = 0;
	for (final Entry<String, List<BookingBean>> e : map.entrySet()) {
	    boolean foundMatch = false;
	    final List<BookingBean> bookings = e.getValue();
	    if (bookings != null) {
		Collections.sort(bookings);
		BookingBean last = null;
		for (final BookingBean b : bookings) {
		    foundMatch = true;
		    if (last != null) {
			if (!last.isConnected(b)) {
			    cnt++;
			}
		    }
		    last = b;
		}
		if (foundMatch) {
		    cnt++;
		}
	    }
	}
	return cnt;
    }

    public synchronized int getNumberOfDistinctBookings(final LocalDate startDate, final LocalDate endDate) {
	final Map<String, List<BookingBean>> map = getByGuestName(startDate, endDate);
	if (map.isEmpty()) {
	    return 0;
	}
	int cnt = 0;
	for (final Entry<String, List<BookingBean>> e : map.entrySet()) {
	    boolean foundMatch = false;
	    final List<BookingBean> bookings = e.getValue();
	    if (bookings != null) {
		Collections.sort(bookings);
		BookingBean last = null;
		for (final BookingBean b : bookings) {
		    foundMatch = true;
		    if (last != null) {
			if (!last.isConnected(b)) {
			    cnt++;
			}
		    }
		    last = b;
		}
		if (foundMatch) {
		    cnt++;
		}
	    }
	}
	return cnt;
    }

    public synchronized int getNumberOfDistinctBookings(final LocalDate startDate, final LocalDate endDate,
	    final String source) {
	final Map<String, List<BookingBean>> map = getByGuestName(startDate, endDate);
	if (map.isEmpty()) {
	    return 0;
	}
	int cnt = 0;
	for (final Entry<String, List<BookingBean>> e : map.entrySet()) {
	    boolean foundMatch = false;
	    final List<BookingBean> bookings = e.getValue();
	    if (bookings != null) {
		Collections.sort(bookings);
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
	}
	return cnt;
    }

    public synchronized int getNumberOfDistinctBookings(final String source) {
	final Map<String, List<BookingBean>> map = getByGuestName(source);
	if (map.isEmpty()) {
	    return 0;
	}
	int cnt = 0;
	for (final Entry<String, List<BookingBean>> e : map.entrySet()) {
	    boolean foundMatch = false;
	    final List<BookingBean> bookings = e.getValue();
	    if (bookings != null) {
		Collections.sort(bookings);
		BookingBean last = null;
		for (final BookingBean b : bookings) {
		    foundMatch = true;
		    if (last != null) {
			if (!last.isConnected(b)) {
			    cnt++;
			}
		    }
		    last = b;
		}
		if (foundMatch) {
		    cnt++;
		}
	    }
	}
	return cnt;
    }

    public Optional<RoomBean> getRoom(final LocalDate date, final String string) {
	final Optional<DateBean> db = getData(date);
	if (db.isPresent()) {
	    return db.get().getRoom(string);
	}
	return Optional.empty();
    }

    public boolean hasCheckIn(final RoomBean room) {
	return getCheckIn(room).isPresent();
    }

    public boolean hasCheckOut(final RoomBean room) {
	return getCheckOut(room).isPresent();
    }

    public synchronized boolean isCheckIn(final BookingBean booking) {
	final Optional<BookingBean> bb = getConnectedPrevious(booking);
	return !bb.isPresent();
    }

    public synchronized boolean isCheckOut(final BookingBean booking) {
	final Optional<BookingBean> bb = getConnectedNext(booking);
	return !bb.isPresent();
    }

    public ObjectProperty<ModelConfiguration> modelConfigurationProperty() {
	return this.modelConfiguration;
    }

    public synchronized void removeAll(final BookingBean... bookings) {
	final List<BookingBean> allall = new ArrayList<>();
	for (final BookingBean bbb : bookings) {
	    allall.addAll(getAllSame(bbb));
	}
	for (final Iterator<DateBean> it = data.listIterator(); it.hasNext();) {
	    final DateBean next = it.next();
	    for (final RoomBean nextRoom : next) {
		for (final Iterator<BookingBean> itBooking = nextRoom.allBookingsProperty().iterator(); itBooking
			.hasNext();) {
		    final BookingBean bb = itBooking.next();
		    for (final BookingBean bbb : allall) {
			if (bb.equals(bbb)) {
			    itBooking.remove();
			    break;
			}
		    }
		}
	    }
	}
    }

    public void removeAll(final Collection<? extends BookingBean> bookings) {
	removeAll(bookings.toArray(new BookingBean[bookings.size()]));
    }

    public void setAllBruttoEarnings(final BookingBean booking, final double earnings) {
	final List<BookingBean> bookings = getAllSame(booking);
	for (final BookingBean bb : bookings) {
	    if (bb.getGrossEarnings() == 0 || earnings == 0) {
		bb.setBruttoEarnings(earnings);
	    }
	}
    }

    public void setAllCheckInNote(final BookingBean booking, final String checkInNote) {
	final List<BookingBean> bookings = getAllSame(booking);
	for (final BookingBean bb : bookings) {
	    if (!bb.hasCheckInNote() || checkInNote == null || checkInNote.length() < 1) {
		bb.setCheckInNote(checkInNote);
	    }
	}

    }

    public void setAllPaymentDone(final BookingBean booking, final boolean moneyReceived) {
	final List<BookingBean> bookings = getAllSame(booking);
	for (final BookingBean bb : bookings) {
	    bb.setPaymentDone(moneyReceived);
	}
    }

    public void setAllWelcomeMailSend(final BookingBean booking, final boolean sent) {
	final List<BookingBean> bookings = getAllSame(booking);
	for (final BookingBean bb : bookings) {
	    bb.setWelcomeMailSend(sent);
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

    public void setModelConfiguration(final ModelConfiguration modelConfiguration) {
	this.modelConfigurationProperty().set(modelConfiguration);
    }

    public DataStore toDataStore() {
	return new DataStore(getBookings());
    }

    @Override
    public String toString() {
	final int maxLen = 10;
	return "DataModel [data=" + (data != null ? data.subList(0, Math.min(data.size(), maxLen)) : null) + "]";
    }

    public synchronized void setAll(final Collection<? extends BookingBean> data) throws OverbookingException {
	this.data.clear();
	dataMap.clear();
	addAll(data);
    }

}
