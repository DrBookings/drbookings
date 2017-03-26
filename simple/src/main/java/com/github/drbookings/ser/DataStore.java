package com.github.drbookings.ser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.github.drbookings.model.bean.BookingBean;

@XmlRootElement
public class DataStore {

    public DataStore(final Collection<? extends BookingBean> data) {
	for (final BookingBean bb : data) {
	    this.data.add(transform(bb));
	}
    }

    public static BookingBeanSer transform(final BookingBean bb) {
	final BookingBeanSer result = new BookingBeanSer();
	Objects.requireNonNull(bb.getRoom());
	Objects.requireNonNull(bb.getDate());
	result.date = bb.getDate();
	result.grossEarnings = bb.getGrossEarnings();
	result.guestName = bb.getGuestName();
	result.roomName = bb.getRoom().getName();
	result.source = bb.getSource();
	result.welcomeMailSend = bb.isWelcomeMailSend();
	result.serviceFee = bb.getServiceFee();
	result.checkInNote = bb.getCheckInNote();
	result.paymentDone = bb.isMoneyReceived();
	return result;
    }

    public static BookingBean transform(final BookingBeanSer bb) {
	final BookingBean result = BookingBean.create(bb.guestName, bb.roomName, bb.date);
	result.setBruttoEarnings(bb.grossEarnings);
	result.setServiceFee(bb.serviceFee);
	result.setSource(bb.source);
	result.setWelcomeMailSend(bb.welcomeMailSend);
	result.setCheckInNote(bb.checkInNote);
	result.setPaymentDone(bb.paymentDone);
	return result;
    }

    public DataStore() {

    }

    @XmlElementWrapper(name = "bookings")
    @XmlElement(name = "booking")
    public List<BookingBeanSer> getData() {
	return data;
    }

    private final List<BookingBeanSer> data = new ArrayList<>();

    public List<BookingBean> getBookings() {
	final List<BookingBean> result = new ArrayList<>();
	for (final BookingBeanSer bbs : data) {
	    result.add(transform(bbs));
	}
	return result;
    }

}
