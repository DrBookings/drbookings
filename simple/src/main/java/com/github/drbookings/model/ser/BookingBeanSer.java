package com.github.drbookings.model.ser;

import java.time.LocalDate;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.github.drbookings.ser.LocalDateAdapter;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class BookingBeanSer {

    @XmlAttribute
    public String guestName;

    @XmlAttribute
    public String source;

    public LocalDate getCheckInDate() {
	return checkInDate;
    }

    // @XmlAttribute
    // public double grossEarnings;

    @XmlAttribute
    public boolean welcomeMailSend;

    @XmlAttribute
    @XmlJavaTypeAdapter(value = LocalDateAdapter.class)
    public LocalDate checkInDate;

    @XmlAttribute
    @XmlJavaTypeAdapter(value = LocalDateAdapter.class)
    public LocalDate checkOutDate;

    @XmlAttribute
    public String roomName;

    @XmlAttribute
    public double serviceFee;
    @XmlAttribute
    public String checkInNote;
    @XmlAttribute
    public boolean paymentDone;
    @XmlAttribute
    public String bookingId;
    @XmlAttribute
    public String grossEarningsExpression;

    @XmlAttribute
    public String externalId;

    @XmlAttribute
    public String specialRequestNote;

    @XmlAttribute
    public String checkOutNote;

}
