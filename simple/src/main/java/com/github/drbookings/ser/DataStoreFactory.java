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

package com.github.drbookings.ser;

import com.github.drbookings.BookingBeanFactory;
import com.github.drbookings.CleaningEntryFactory;
import com.github.drbookings.DataStoreCore;
import com.github.drbookings.ExpenseFactory;

/**
 * 
 * @author Alexander Kerner
 * @date 2018-08-19
 *
 */
public class DataStoreFactory {

    public static DataStoreCore build(final DataStoreCoreSer data) {
	final SimpleDataStoreCore result = new SimpleDataStoreCore();
	result.setBookings(new BookingBeanFactory().build(data.getBookingsSer()));
	result.setCleanings(new CleaningEntryFactory(result.getBookings()).build(data.getCleaningsSer()));
	result.setExpenses(new ExpenseFactory().build(data.getExpenses()));
	return result;
    }

}
