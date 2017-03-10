package com.github.drbookings.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.github.drbookings.DateRange;
import com.github.drbookings.model.bean.BookingBean;
import com.github.drbookings.model.bean.DateBean;
import com.github.drbookings.model.bean.RoomBean;

public class BookingDates implements List<DateBean> {

    private static List<DateBean> allDates(final LocalDate checkIn, final LocalDate checkOut, final String source,
	    final String room, final String guestName) {
	final List<DateBean> result = new ArrayList<>();
	for (final LocalDate d : new DateRange(checkIn, checkOut)) {
	    final DateBean b = new DateBean(d);
	    if (room == null || room.isEmpty()) {
		for (final RoomBean rb : b.getRooms()) {
		    rb.addBooking(new BookingBean().setGuestName(guestName).setSource(source));
		}
	    } else {
		b.getRoom(room).addBooking(new BookingBean().setGuestName(guestName).setSource(source));
	    }

	    result.add(b);
	}
	// a few extra
	result.add(new DateBean(checkOut.plusDays(3)));
	return result;
    }

    public static BookingDates buildBookingDate(final LocalDate checkIn, final LocalDate checkOut, final String source,
	    final String room, final String guestName) {
	final BookingDates b = new BookingDates(allDates(checkIn, checkOut, source, room, guestName));
	return b;
    }

    private final List<DateBean> dates;

    public BookingDates(final List<DateBean> dates) {
	super();
	this.dates = dates;
    }

    @Override
    public boolean add(final DateBean e) {
	return dates.add(e);
    }

    @Override
    public void add(final int index, final DateBean element) {
	dates.add(index, element);
    }

    @Override
    public boolean addAll(final Collection<? extends DateBean> c) {
	return dates.addAll(c);
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends DateBean> c) {
	return dates.addAll(index, c);
    }

    @Override
    public void clear() {
	dates.clear();
    }

    @Override
    public boolean contains(final Object o) {
	return dates.contains(o);
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
	return dates.containsAll(c);
    }

    @Override
    public boolean equals(final Object o) {
	return dates.equals(o);
    }

    @Override
    public DateBean get(final int index) {
	return dates.get(index);
    }

    @Override
    public int hashCode() {
	return dates.hashCode();
    }

    @Override
    public int indexOf(final Object o) {
	return dates.indexOf(o);
    }

    @Override
    public boolean isEmpty() {
	return dates.isEmpty();
    }

    @Override
    public Iterator<DateBean> iterator() {
	return dates.iterator();
    }

    @Override
    public int lastIndexOf(final Object o) {
	return dates.lastIndexOf(o);
    }

    @Override
    public ListIterator<DateBean> listIterator() {
	return dates.listIterator();
    }

    @Override
    public ListIterator<DateBean> listIterator(final int index) {
	return dates.listIterator(index);
    }

    @Override
    public DateBean remove(final int index) {
	return dates.remove(index);
    }

    @Override
    public boolean remove(final Object o) {
	return dates.remove(o);
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
	return dates.removeAll(c);
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
	return dates.retainAll(c);
    }

    @Override
    public DateBean set(final int index, final DateBean element) {
	return dates.set(index, element);
    }

    @Override
    public int size() {
	return dates.size();
    }

    @Override
    public List<DateBean> subList(final int fromIndex, final int toIndex) {
	return dates.subList(fromIndex, toIndex);
    }

    @Override
    public Object[] toArray() {
	return dates.toArray();
    }

    @Override
    public <T> T[] toArray(final T[] a) {
	return dates.toArray(a);
    }

    @Override
    public String toString() {
	return dates.toString();
    }

}
