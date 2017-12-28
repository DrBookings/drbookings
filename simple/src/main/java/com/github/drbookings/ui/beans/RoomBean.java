/*
 * DrBookings
 *
 * Copyright (C) 2016 - 2017 Alexander Kerner
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

package com.github.drbookings.ui.beans;

import com.github.drbookings.TemporalQueries;
import com.github.drbookings.model.data.MatchException;
import com.github.drbookings.model.data.manager.MainManager;
import com.github.drbookings.ui.BookingEntry;
import com.github.drbookings.ui.BookingFilter;
import com.github.drbookings.ui.CleaningEntry;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RoomBean extends WarnableBean {

    private static final Logger logger = LoggerFactory.getLogger(RoomBean.class);
    private final ObjectProperty<CleaningEntry> cleaningEntries = new SimpleObjectProperty<>();
    private final ListProperty<BookingEntry> bookingEntries = new SimpleListProperty<>(
            FXCollections.observableArrayList(BookingEntry.extractor()));
    private final ListProperty<BookingEntry> filteredBookingEntries = new SimpleListProperty<>(
            FXCollections.observableArrayList(BookingEntry.extractor()));
    private final String name;
    private final MainManager manager;
    private final DateBean dateBean;
    private final StringProperty bookingFilterString = new SimpleStringProperty();
    private final BooleanProperty needsCleaning = new SimpleBooleanProperty();

    public RoomBean(final String name, final DateBean date, final MainManager manager) {
        this.name = name;
        this.manager = manager;
        this.dateBean = date;
        bindProperties();
    }

    public static Callback<RoomBean, Observable[]> extractor() {
        return param -> new Observable[]{param.bookingFilterStringProperty(), param.bookingEntriesProperty(),
                param.filteredBookingEntriesProperty(), param.cleaningEntryProperty()};
    }

    public void addBookingEntry(final BookingEntry e) {
        Objects.requireNonNull(e);
        bookingEntriesProperty().add(e);
        // if (logger.isDebugEnabled()) {
        // logger.debug(this + " booking entries now " + getBookingEntries());
        // }
        // if (logger.isDebugEnabled()) {
        // logger.debug("filtered booking entries now " +
        // getFilteredBookingEntries());
        // }
    }

    @Override
    protected void bindProperties() {
        filteredBookingEntriesProperty().bind(Bindings.createObjectBinding(filterBookings(),
                bookingFilterStringProperty(), bookingEntriesProperty()));
        needsCleaningProperty().bind(Bindings.createObjectBinding(calulateNeedsCleaning(), manager.getUIData()));
        super.bindProperties();

    }

    public ListProperty<BookingEntry> bookingEntriesProperty() {
        return this.bookingEntries;
    }

    public StringProperty bookingFilterStringProperty() {
        return this.bookingFilterString;
    }

    @Override
    protected Callable<Boolean> calculateWarningProperty() {

        return () -> {

            final boolean lastMonth = getDate().query(TemporalQueries::isPreviousMonthOrEarlier);

            // if (getDate().isAfter(LocalDate.now()) && needsCleaning()) {
            // return true;
            // }

            final boolean payment = filteredBookingEntriesProperty().stream()
                    .filter(b -> b.getElement().isPaymentDone()).count() == filteredBookingEntriesProperty().size();

            if (!payment && lastMonth) {
                return true;
            }
            final boolean welcomeMail = filteredBookingEntriesProperty().stream()
                    .filter(b -> b.getElement().isWelcomeMailSend()).count() == filteredBookingEntriesProperty().size();
            return !lastMonth && !welcomeMail;

        };
    }

    public Callable<Boolean> calulateNeedsCleaning() {
        return () -> {

            if (getDate().isBefore(LocalDate.now())) {
                // time out
                return false;
            }
            // if (getGuestNames().size() > 1) {
            // System.err.println(getGuestNames());
            // }
            if (getFilteredBookingEntries().size() > 1 && hasCheckIn() && hasCheckOut() && !hasCleaning()) {
                return getGuestNames().size() >= 2;
            }
            return hasCheckOut() && getFilteredBookingEntries().stream().filter(be -> !be.isCheckIn())
                    .anyMatch(b -> b.getElement().getCleaning() == null);
        };
    }

    public ObjectProperty<CleaningEntry> cleaningEntryProperty() {
        return this.cleaningEntries;
    }

    private Callable<ObservableList<BookingEntry>> filterBookings() {

        return () -> {
            // if (logger.isDebugEnabled()) {
            // logger.debug(this + " Filtering on " + getBookingEntries());
            // }
            final ObservableList<BookingEntry> result = FXCollections.observableArrayList();
            result.addAll(bookingEntriesProperty().stream().filter(new BookingFilter(getBookingFilterString()))
                    .collect(Collectors.toList()));
            // if (logger.isDebugEnabled()) {
            // logger.debug("filtered booking entries now " + result);
            // }
            return result;
        };
    }

    public ListProperty<BookingEntry> filteredBookingEntriesProperty() {
        return this.filteredBookingEntries;
    }

    public List<BookingEntry> getBookingEntries() {
        return this.bookingEntriesProperty().get();
    }

    public void setBookingEntries(final Collection<? extends BookingEntry> bookingEntries) {
        this.bookingEntriesProperty().setAll(bookingEntries);
    }

    public BookingEntry getBookingEntryCheckIn() {
        return getSingleBooking(e -> e.isCheckIn());
    }

    public BookingEntry getBookingEntryCheckOut() {
        return getSingleBooking(e -> e.isCheckOut());
    }

    public String getBookingFilterString() {
        return this.bookingFilterStringProperty().get();
    }

    public void setBookingFilterString(final String bookingFilterString) {
        this.bookingFilterStringProperty().set(bookingFilterString);
    }

    public CleaningEntry getCleaningEntry() {
        CleaningEntry result = this.cleaningEntryProperty().get();
        return result;
    }

    public void setCleaningEntry(final CleaningEntry cleaningEntry) {
        this.cleaningEntryProperty().set(cleaningEntry);
    }

    public LocalDate getDate() {
        return dateBean.getDate();
    }

    public DateBean getDateBean() {
        return dateBean;
    }

    public List<BookingEntry> getFilteredBookingEntries() {
        return this.filteredBookingEntriesProperty().get();
    }

    public void setFilteredBookingEntries(final Collection<? extends BookingEntry> filteredBookingEntries) {
        this.filteredBookingEntriesProperty().setAll(filteredBookingEntries);
    }

    public Set<String> getGuestNames() {
        return BookingEntry.guestNameView(getFilteredBookingEntries());
    }

    public String getName() {
        return name;
    }

    protected BookingEntry getSingleBooking(final Predicate<BookingEntry> filter) {
        final List<BookingEntry> bookings = getBookingEntries().stream().filter(filter).collect(Collectors.toList());
        if (bookings.isEmpty()) {
            return null;
        } else if (bookings.size() > 1) {
            if (logger.isWarnEnabled()) {
                logger.warn("Too many bookings" + bookings);
            }
        }
        return bookings.get(0);
    }

    @Override
    protected Observable[] getWarnableObservables() {
        return new Observable[]{filteredBookingEntriesProperty(), needsCleaningProperty()};
    }

    public boolean hasCheckIn() {
        final List<BookingEntry> filteredBookingEntries = getFilteredBookingEntries();
        if (filteredBookingEntries.size() == 1 && filteredBookingEntries.get(0).isCheckIn()) {
            return true;
        }
        boolean check = false;
        boolean split = false;
        for (final BookingEntry be : filteredBookingEntries) {
            if (be.isCheckIn()) {
                check = true;
            }
            if (be.getElement().isSplitBooking()) {
                split = true;
            }
        }
        // do not count, if prev. check-out is split booking
        // any booking is check-in and any booking is split booking
        return check && !split;
    }

    public boolean hasCheckOut() {
        final List<BookingEntry> filteredBookingEntries = getFilteredBookingEntries();
        if (filteredBookingEntries.size() == 1 && filteredBookingEntries.get(0).isCheckOut()) {
            return true;
        }
        boolean check = false;
        boolean split = false;
        for (final BookingEntry be : filteredBookingEntries) {
            if (be.isCheckOut()) {
                check = true;
            }
            if (be.getElement().isSplitBooking()) {
                split = true;
            }
        }
        // do not count, if next check-in is split booking
        // any booking is check-out and any booking is split booking
        return check && !split;
    }

    public boolean hasCleaning() {
        return getCleaningEntry() != null;
    }

    public boolean isEmpty() {
        return getFilteredBookingEntries().isEmpty();
    }

    public boolean needsCleaning() {
        return this.needsCleaningProperty().get();
    }

    public BooleanProperty needsCleaningProperty() {
        return this.needsCleaning;
    }

    public void removeCleaningEntry() {
        if (getCleaningEntry() != null) {
            manager.removeCleaning(getCleaningEntry());
            // setCleaningEntry(null);
        }

    }

    public void setCleaning(final String cleaningName) {
        if (getCleaningEntry() != null) {
            manager.removeCleaning(getCleaningEntry());
            setCleaningEntry(null);
        }
        try {
            manager.addCleaning(getDate(), cleaningName, getName());
        } catch (final MatchException e) {
            if (logger.isErrorEnabled()) {
                logger.error("Failed to add cleaning " + cleaningName + ", " + e.getLocalizedMessage(), e);
            }
        }

    }

    public void setNeedsCleaning(final boolean needsCleaning) {
        this.needsCleaningProperty().set(needsCleaning);
    }

    @Override
    public String toString() {
        return "roomBean:" + getDate() + ",name:" + getName() + ",filteredBookings:"
                + getFilteredBookingEntries().size() + " " + getBookingEntries().size();
    }

}
