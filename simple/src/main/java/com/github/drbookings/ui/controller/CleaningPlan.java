package com.github.drbookings.ui.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

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
	    entries.stream().sorted().filter(e -> e.getDate().isAfter(LocalDate.now().minusWeeks(1))).forEach(e -> {
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
