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
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.github.drbookings.ser.LocalDateAdapter;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class CleaningBeanSer {

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (date == null ? 0 : date.hashCode());
	result = prime * result + (name == null ? 0 : name.hashCode());
	result = prime * result + (room == null ? 0 : room.hashCode());
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
	if (!(obj instanceof CleaningBeanSer)) {
	    return false;
	}
	final CleaningBeanSer other = (CleaningBeanSer) obj;
	if (date == null) {
	    if (other.date != null) {
		return false;
	    }
	} else if (!date.equals(other.date)) {
	    return false;
	}
	if (name == null) {
	    if (other.name != null) {
		return false;
	    }
	} else if (!name.equals(other.name)) {
	    return false;
	}
	if (room == null) {
	    if (other.room != null) {
		return false;
	    }
	} else if (!room.equals(other.room)) {
	    return false;
	}
	return true;
    }

    @XmlAttribute
    public List<String> calendarIds;

    @XmlAttribute
    public float cleaningCosts;

    @XmlAttribute
    @XmlJavaTypeAdapter(value = LocalDateAdapter.class)
    public LocalDate date;

    @XmlAttribute
    public String name;

    @XmlAttribute
    public String id;

    /**
     * Optional
     */
    @XmlAttribute
    public String bookingId;

    @XmlAttribute
    public String room;

    @Override
    public String toString() {
	return "CleaningBeanSer date=" + date + ", name=" + name + ", room=" + room + "]";
    }

}
