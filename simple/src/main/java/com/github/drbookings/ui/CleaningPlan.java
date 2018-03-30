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

package com.github.drbookings.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import com.github.drbookings.model.settings.SettingsManager;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class CleaningPlan {

    private static final DateTimeFormatter myDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, E");

    private final Multimap<String, CleaningEntry> nameToCleaningEntry;

    public CleaningPlan(final Collection<CleaningEntry> cleaningEntries) {
	this();
	for (final CleaningEntry e : cleaningEntries) {
	    nameToCleaningEntry.put(e.getElement().getName(), e);
	}
    }

    public CleaningPlan() {
	this.nameToCleaningEntry = ArrayListMultimap.create();
    }

    @Override
    public String toString() {
	final StringBuilder sb = new StringBuilder();
	for (final Entry<String, Collection<CleaningEntry>> entry : nameToCleaningEntry.asMap().entrySet()) {
	    sb.append(entry.getKey());
	    sb.append("\n");
	    final List<CleaningEntry> entries = new ArrayList<>(entry.getValue());
	    entries.stream().sorted()
		    .filter(e -> e.getDate()
			    .isAfter(LocalDate.now()
				    .minusDays(SettingsManager.getInstance().getCleaningPlanLookBehind())))
		    .forEach(e -> {
			sb.append(myDateFormatter.format(e.getDate()));
			sb.append("\t");
			sb.append(e.getRoom());
			sb.append("\n");
		    });
	    // Collections.sort(entries);
	    // for (final CleaningEntry e : entries) {
	    // sb.append(myDateFormatter.format(e.getDate()));
	    // sb.append("\t");
	    // sb.append(e.getRoom());
	    // sb.append("\n");
	    // }
	}
	return sb.toString();
    }

}
