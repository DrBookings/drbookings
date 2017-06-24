package com.github.drbookings.model.ser;

import java.time.LocalDate;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.github.drbookings.ser.LocalDateAdapter;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class BookingBeanSer {

	@XmlAttribute
	public String bookingId;

	@XmlAttribute
	public List<String> calendarIds;

	@XmlAttribute
	@XmlJavaTypeAdapter(value = LocalDateAdapter.class)
	public LocalDate checkInDate;

	@XmlAttribute
	public String checkInNote;

	@XmlAttribute
	@XmlJavaTypeAdapter(value = LocalDateAdapter.class)
	public LocalDate checkOutDate;

	@XmlAttribute
	public String checkOutNote;

	@XmlAttribute
	public float cleaningFees;

	@XmlAttribute
	@XmlJavaTypeAdapter(value = LocalDateAdapter.class)
	public LocalDate dateOfPayment;
	@XmlAttribute
	public String externalId;
	@XmlAttribute
	public String grossEarningsExpression;
	@XmlAttribute
	public String guestName;
	@XmlAttribute
	public boolean paymentDone;
	@XmlAttribute
	public String roomName;

	/**
	 * E.g. Airbnb fees. Absolute value, such as 12€.
	 */
	@XmlAttribute
	public float serviceFee;

	/**
	 * E.g. Booking fees. Relative value, such as 0.12 (12%).
	 */
	@XmlAttribute
	public float serviceFeePercent;

	@XmlAttribute
	public String source;

	@XmlAttribute
	public String specialRequestNote;
	@XmlAttribute
	public boolean welcomeMailSend;

}
