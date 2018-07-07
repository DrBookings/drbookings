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

import java.util.Objects;
import java.util.UUID;

public class IDedImpl implements IDed {

    private final String id;

    public IDedImpl() {
	this(null);
    }

    public IDedImpl(final String id) {
	if (id == null) {
	    this.id = UUID.randomUUID().toString();
	} else {
	    this.id = id;
	}
    }

    @Override
    public boolean equals(final Object obj) {
	if (!(obj instanceof IDedImpl)) {
	    return false;
	}
	final IDed other = (IDed) obj;
	return Objects.equals(this.getId(), other.getId());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.drbookings.model.data.IDed#getId()
     */
    @Override
    public String getId() {
	return id;
    }

    @Override
    public int hashCode() {
	return Objects.hash(getId());
    }
}
