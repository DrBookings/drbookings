package com.github.drbookings.model;

import com.github.drbookings.model.data.BookingOrigin;
import com.github.drbookings.model.settings.SettingsManager;
import com.github.drbookings.ui.BookingEntry;

import java.time.LocalDate;
import java.util.*;

public class NightlyRateView {

    final Map<BookingOrigin, Map<LocalDate, Collection<Number>>> data = new LinkedHashMap<>();
    private double binSize = 1;

    public NightlyRateView(final Collection<? extends BookingEntry> bookingEntries) {
        addAll(bookingEntries);
    }

    public NightlyRateView() {

    }

    @Override
    public String toString() {
        return "NightlyRateView{" +
                "data=" + data +
                '}';
    }

    public Map<BookingOrigin, Map<LocalDate, Collection<Number>>> getData() {
        return Collections.unmodifiableMap(data);
    }

    public NightlyRateView addAll(final Collection<? extends BookingEntry> bookingEntries) {
        bookingEntries.forEach(c -> add(c));
        return this;
    }

    public NightlyRateView add(final BookingEntry b) {
        Map<LocalDate, Collection<Number>> innerMap = data.get(b.getBookingOrigin());
        if (innerMap == null) {
            innerMap = new LinkedHashMap<>();
            data.put(b.getBookingOrigin(), innerMap);
        }
        Collection<Number> values = innerMap.get(b.getDate());
        if (values == null) {
            values = new ArrayList<>();
        }
        values.add(b.getEarnings(SettingsManager.getInstance().isShowNetEarnings()));
        innerMap.put(b.getDate(), values);
        return this;
    }

    public void setBinSize(final double binSize) {
        this.binSize = binSize;
    }

    private BinType binType;

    public void setBinType(final BinType selectedItem) {
        binType = binType;
    }
}
