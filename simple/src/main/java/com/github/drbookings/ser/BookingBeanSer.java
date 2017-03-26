package com.github.drbookings.ser;

import java.time.LocalDate;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class BookingBeanSer {

    @XmlAttribute
    public String guestName;

    @XmlAttribute
    public String source;

    @XmlAttribute
    public double grossEarnings;

    @XmlAttribute
    public boolean welcomeMailSend;

    @XmlAttribute
    @XmlJavaTypeAdapter(value = LocalDateAdapter.class)
    public LocalDate date;

    @XmlAttribute
    public String roomName;

    @XmlAttribute
    public double serviceFee;
    @XmlAttribute
    public String checkInNote;
    @XmlAttribute
    public boolean paymentDone;

}
