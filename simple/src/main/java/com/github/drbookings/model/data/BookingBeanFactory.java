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

package com.github.drbookings.model.data;

import com.github.drbookings.model.data.manager.BookingOriginProvider;
import com.github.drbookings.model.data.manager.GuestProvider;
import com.github.drbookings.model.data.manager.RoomProvider;
import java.time.LocalDate;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

public class BookingBeanFactory {

    /**
     * Use provider to keep name unique.
     */
    private final GuestProvider guestProvider;

    /**
     * Use provider to keep name unique.
     */
    private final RoomProvider roomProvider;

    /**
     * Use provider to keep name unique.
     */
    private final BookingOriginProvider bookingOriginProvider;

    public BookingBeanFactory(GuestProvider guestProvider,
        RoomProvider roomProvider,
        BookingOriginProvider bookingOriginProvider) {
        this.guestProvider = guestProvider;
        this.roomProvider = roomProvider;
        this.bookingOriginProvider = bookingOriginProvider;
    }

    public BookingBeanFactory() {
        this(new GuestProvider(), new RoomProvider(), new BookingOriginProvider());
    }

    /**
     * @return the newly created {@link BookingBean}
     */
    public synchronized BookingBean createBooking(final String id, final LocalDate checkInDate,
        final LocalDate checkOutDate, final String guestName, final String
        roomName, final String originName) {
        Objects.requireNonNull(checkInDate);
        Objects.requireNonNull(checkOutDate);
        if (StringUtils.isBlank(guestName)) {
            throw new IllegalArgumentException("No guest name given");
        }
        if (StringUtils.isBlank(roomName)) {
            throw new IllegalArgumentException("No room name given");
        }
        final Guest guest = guestProvider.getOrCreateElement(guestName);
        final Room room = roomProvider.getOrCreateElement(roomName);
        final BookingOrigin bookingOrigin = bookingOriginProvider.getOrCreateElement(originName);
        final BookingBean booking = new BookingBean(id, guest, room, bookingOrigin, checkInDate,
            checkOutDate);
        return booking;
    }
}
