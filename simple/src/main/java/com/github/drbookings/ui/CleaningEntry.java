package com.github.drbookings.ui;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.github.drbookings.model.data.Cleaning;
import com.github.drbookings.model.data.Room;
import com.github.drbookings.model.data.manager.MainManager;

public class CleaningEntry extends DateRoomEntry<Cleaning> {

    public static List<String> roomNameView(final Collection<CleaningEntry> e) {
	return e.stream().map(c -> c.getRoom().getName()).collect(Collectors.toList());

    }

    private List<String> calendarIds = new ArrayList<>();

    private final MainManager mainManager;

    public CleaningEntry(final LocalDate date, final Room room, final Cleaning element, final MainManager mainManager) {
	super(date, room, element);
	this.mainManager = mainManager;
    }

    public void addCalendarId(final String id) {
	calendarIds.add(id);
    }

    public List<String> getCalendarIds() {
	return calendarIds;
    }

    public boolean isShortTime() {
	return mainManager.hasCheckIn(getDate(), getRoom().getName());
    }

    public void setCalendarIds(final Collection<? extends String> calendarIds) {
	if (calendarIds != null) {
	    this.calendarIds = new ArrayList<>(calendarIds);
	}
    }

}
