package com.github.drbookings.model.data;

import java.time.LocalDate;
import java.util.List;

import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.collections4.map.MultiKeyMap;

import com.github.drbookings.model.BookingEntry;
import com.github.drbookings.model.BookingEntryPair;
import com.github.drbookings.model.RoomEntry;
import com.github.drbookings.model.data.manager.BookingOriginProvider;
import com.github.drbookings.model.data.manager.GuestProvider;
import com.github.drbookings.model.data.manager.RoomProvider;
import com.github.drbookings.model.exception.OverbookingException;

public class DrBookingsDataCore {

    /**
     * Use provider to keep name unique.
     */
    private final BookingOriginProvider bookingOriginProvider;

    /**
     * Use provider to keep name unique.
     */
    private final GuestProvider guestProvider;

    /**
     * Use provider to keep name unique.
     */

    private final RoomProvider roomProvider;
    /**
     * (Room name, Date) -> Value
     */
    protected final MultiKeyMap<Object, BookingEntryPair> bookingEntries;

    /**
     * (Room name, Date) -> Value
     */
    protected final MultiKeyMap<Object, RoomEntry> roomEntries;

    public DrBookingsDataCore() {
	roomProvider = new RoomProvider();
	guestProvider = new GuestProvider();
	bookingOriginProvider = new BookingOriginProvider();
	roomEntries = MultiKeyMap.multiKeyMap(new LinkedMap<>());
	bookingEntries = MultiKeyMap.multiKeyMap(new LinkedMap<>());
    }

    public BookingBean createAndAddBooking(final LocalDate checkInDate, final LocalDate checkOutDate,
	    final String guestName, final String roomName, final String source) throws OverbookingException {
	return createAndAddBooking(null, checkInDate, checkOutDate, guestName, roomName, source);
    }

    public BookingBean createAndAddBooking(final String id, final LocalDate checkInDate, final LocalDate checkOutDate,
	    final String guestName, final String roomName, final String originName) throws OverbookingException {
	final BookingBean newBooking = createBooking(id, checkInDate, checkOutDate, guestName, roomName, originName);
	addBooking(newBooking);
	return newBooking;
    }

    BookingBean createBooking(final String bookingId, final LocalDate checkInDate, final LocalDate checkOutDate,
	    final String guestName, final String roomName, final String source) {
	final BookingBeanFactory f = new BookingBeanFactory(getGuestProvider(), getRoomProvider(),
		getBookingOriginProvider());
	return f.createBooking(bookingId, checkInDate, checkOutDate, guestName, roomName, source);
    }

    List<BookingEntry> addBooking(final BookingBean bb) throws OverbookingException {

	final List<BookingEntry> bes = Bookings.toEntries(bb);
	// none of those booking entries must be here already
	for (final BookingEntry be : bes) {
	    final Room room = be.getRoom();
	    final LocalDate date = be.getDate();
	    BookingEntryPair be2 = bookingEntries.get(getBookingEntryMultiKey(room.getName(), date));
	    if (be2 != null) {
		be2.addBooking(be);
	    } else {
		be2 = new BookingEntryPair(be);
		final BookingEntryPair oldVal = bookingEntries.put(getBookingEntryMultiKey(room.getName(), date), be2);
		if (oldVal != null) {
		    throw new RuntimeException();
		}
	    }

	}
	return bes;
    }

    protected RoomEntry createNewRoomEntry(final Room room, final LocalDate date) {

	return new RoomEntry(date, room);
    }

    protected MultiKey<Object> getBookingEntryMultiKey(final String roomName, final LocalDate date) {

	return new MultiKey<>(roomName, date);
    }

    BookingOriginProvider getBookingOriginProvider() {
	return bookingOriginProvider;
    }

    GuestProvider getGuestProvider() {
	return guestProvider;
    }

    Room getOrCreateRoom(final String name, final LocalDate date) {

	final Room room = roomProvider.getOrCreateElement(name);
	return room;
    }

    RoomEntry getOrCreateRoomEntry(final Room room, final LocalDate date) {

	return getOrCreateRoomEntry(room.getName(), date);
    }

    RoomEntry getOrCreateRoomEntry(final String roomName, final LocalDate date) {

	RoomEntry roomEntry = roomEntries.get(getRoomEntryMultiKey(roomName, date));
	if (roomEntry == null) {
	    roomEntry = createNewRoomEntry(getOrCreateRoom(roomName, date), date);
	    final RoomEntry oldVal = roomEntries.put(getRoomEntryMultiKey(roomName, date), roomEntry);
	    if (oldVal != null) {
		throw new RuntimeException();
	    }
	}
	return roomEntry;
    }

    protected MultiKey<Object> getRoomEntryMultiKey(final String roomName, final LocalDate date) {

	return new MultiKey<>(roomName, date);
    }

    RoomProvider getRoomProvider() {
	return roomProvider;
    }

}
