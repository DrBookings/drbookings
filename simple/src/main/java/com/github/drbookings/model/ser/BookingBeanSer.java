/*
 * DrBookings
 *
 * Copyright (C) 2016 - 2018 Alexander Kerner
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 */

package com.github.drbookings.model.ser;

import java.time.LocalDate;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.github.drbookings.model.data.ser.PaymentSer;
import com.github.drbookings.ser.LocalDateAdapter;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class BookingBeanSer {

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (checkInDate == null ? 0 : checkInDate.hashCode());
	result = prime * result + (checkOutDate == null ? 0 : checkOutDate.hashCode());
	result = prime * result + (guestName == null ? 0 : guestName.hashCode());
	result = prime * result + (roomName == null ? 0 : roomName.hashCode());
	result = prime * result + (source == null ? 0 : source.hashCode());
	return result;
    }

    @Override
    public boolean equals(final Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (!(obj instanceof BookingBeanSer)) {
	    return false;
	}
	final BookingBeanSer other = (BookingBeanSer) obj;
	if (checkInDate == null) {
	    if (other.checkInDate != null) {
		return false;
	    }
	} else if (!checkInDate.equals(other.checkInDate)) {
	    return false;
	}
	if (checkOutDate == null) {
	    if (other.checkOutDate != null) {
		return false;
	    }
	} else if (!checkOutDate.equals(other.checkOutDate)) {
	    return false;
	}
	if (guestName == null) {
	    if (other.guestName != null) {
		return false;
	    }
	} else if (!guestName.equals(other.guestName)) {
	    return false;
	}
	if (roomName == null) {
	    if (other.roomName != null) {
		return false;
	    }
	} else if (!roomName.equals(other.roomName)) {
	    return false;
	}
	if (source == null) {
	    if (other.source != null) {
		return false;
	    }
	} else if (!source.equals(other.source)) {
	    return false;
	}
	return true;
    }

    @XmlAttribute
    public String bookingId;

    @XmlAttribute
    public List<String> calendarIds;

    @XmlElementWrapper(name = "Payments")
    public List<PaymentSer> paymentsSoFar;

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
    public boolean splitBooking;
    @XmlAttribute
    public String roomName;

    /**
     * E.g. Airbnb fees. Absolute value, such as 12€.
     */
    @XmlAttribute
    public float serviceFee;

    /**
     * E.g. BookingBean fees. Relative value, such as 0.12 (12%).
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
