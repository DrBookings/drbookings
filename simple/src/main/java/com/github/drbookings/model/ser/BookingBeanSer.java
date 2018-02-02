/*
 * DrBookings
 *
 * Copyright (C) 2016 - 2017 Alexander Kerner
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

import com.github.drbookings.ser.LocalDateAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;
import java.util.List;

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
