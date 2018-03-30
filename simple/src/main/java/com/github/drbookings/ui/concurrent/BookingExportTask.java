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

package com.github.drbookings.ui.concurrent;

import com.github.drbookings.concurrent.BookingExportCallable;
import com.github.drbookings.model.data.BookingBean;
import javafx.concurrent.Task;

import java.util.Collection;

public class BookingExportTask extends Task<Void> {
    private final Collection<? extends BookingBean> bookings;

    public BookingExportTask(Collection<? extends BookingBean> bookings) {
        this.bookings = bookings;
    }

    @Override
    protected Void call() throws Exception {
        return new BookingExportCallable(bookings).call();
    }
}
