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

package com.github.drbookings;

import java.util.function.Predicate;

public class BookingFilter2 implements Predicate<BookingBean> {

    private String filterString;

    public BookingFilter2(final String filterString) {
	this.filterString = filterString;
    }

    public String getFilterString() {
	return filterString;
    }

    public void setFilterString(final String filterString) {
	this.filterString = filterString;
    }

    @Override
    public boolean test(final BookingBean t) {
	if ((filterString == null) || (filterString.length() < 1)) {
	    return true;
	}
	boolean result = t.getGuest().getName().toLowerCase().contains(filterString.toLowerCase());
	if (!result) {
	    result = t.getCheckIn().getMonth().toString().toLowerCase().contains(filterString.toLowerCase());
	}
	if (!result) {
	    result = t.getBookingOrigin().getName().toLowerCase().contains(filterString.toLowerCase());
	}
	return result;
    }

}
