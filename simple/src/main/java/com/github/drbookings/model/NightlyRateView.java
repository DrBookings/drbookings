package com.github.drbookings.model;

import com.github.drbookings.model.data.BookingOrigin;
import com.github.drbookings.model.settings.SettingsManager;
import com.github.drbookings.ui.BookingEntry;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class NightlyRateView {

    final Map<BookingOrigin, Map<LocalDate, Number>> data = new LinkedHashMap<>();
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

    public Map<BookingOrigin, Map<LocalDate, Number>> getData() {
        return Collections.unmodifiableMap(data);
    }

    public NightlyRateView addAll(final Collection<? extends BookingEntry> bookingEntries) {
        bookingEntries.forEach(c -> add(c));
        return this;
    }

    public NightlyRateView add(final BookingEntry b) {
        Map<LocalDate, Number> innerMap = data.get(b.getBookingOrigin());
        if (innerMap == null) {
            innerMap = new LinkedHashMap<>();
            data.put(b.getBookingOrigin(), innerMap);
        }
        Number value = innerMap.get(b.getDate());
        if (value == null) {
            value = Double.valueOf(0);
        }
        value = (value.doubleValue() + b.getEarnings(SettingsManager.getInstance().isShowNetEarnings())) / 2;
        innerMap.put(b.getDate(), value);
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
