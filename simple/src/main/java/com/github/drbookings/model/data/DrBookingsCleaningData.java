package com.github.drbookings.model.data;

import java.time.LocalDate;

import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.collections4.map.MultiKeyMap;

import com.github.drbookings.model.data.manager.CleaningProvider;
import com.github.drbookings.model.exception.AlreadyBusyException;
import com.github.drbookings.ui.CleaningEntry;

public class DrBookingsCleaningData {
    /**
     * (Room name, Date) -> Value
     */
    protected final MultiKeyMap<Object, CleaningEntry> cleaningEntries;

    /**
     * Use provider to keep name unique.
     */
    private final CleaningProvider cleaningProvider;

    public DrBookingsCleaningData() {
	cleaningProvider = new CleaningProvider();
	cleaningEntries = MultiKeyMap.multiKeyMap(new LinkedMap<>());
    }

    public CleaningEntry createAndAddCleaning(final String id, final String cleaningName, final LocalDate date,
	    final Room room) throws AlreadyBusyException {

	CleaningEntry cleaningEntry = cleaningEntries.get(getCleaningEntryMultiKey(room.getName(), date));
	if (cleaningEntry == null) {
	    cleaningEntry = createNewCleaningEntry(id, getOrCreateCleaning(cleaningName), date, room);
	    cleaningEntries.put(getCleaningEntryMultiKey(room.getName(), date), cleaningEntry);
	} else {
	    throw new AlreadyBusyException(
		    "There is already a cleaning at " + date + " for " + room.getName() + ": " + cleaningEntry);
	}
	return cleaningEntry;
    }

    protected CleaningEntry createNewCleaningEntry(final String id, final Cleaning cleaning, final LocalDate date,
	    final Room room) {

	return new CleaningEntry(id, date, room, cleaning);
    }

    protected MultiKey<Object> getCleaningEntryMultiKey(final String roomName, final LocalDate date) {

	return new MultiKey<>(roomName, date);
    }

    CleaningProvider getCleaningProvider() {
	return cleaningProvider;
    }

    Cleaning getOrCreateCleaning(final String name) {

	final Cleaning room = cleaningProvider.getOrCreateElement(name);
	return room;
    }
}
