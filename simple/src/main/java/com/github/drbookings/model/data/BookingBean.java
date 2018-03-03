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

package com.github.drbookings.model.data;

import com.github.drbookings.Scripting;
import com.github.drbookings.TemporalQueries;
import com.github.drbookings.model.*;
import com.github.drbookings.model.settings.SettingsManager;
import com.github.drbookings.ui.BookingEntry;
import com.github.drbookings.ui.CleaningEntry;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.Callable;

public class BookingBean extends IDed
    implements Comparable<BookingBean>, NetEarningsProvider, GrossEarningsProvider, EarningsProvider, IBooking {

    public static final RoundingMode DEFAULT_ROUNDING_MODE = RoundingMode.HALF_UP;
    private static final Logger logger = LoggerFactory.getLogger(BookingBean.class);
    private final RoundingMode roundingMode = DEFAULT_ROUNDING_MODE;
    private final LocalDate checkIn;
    private final LocalDate checkOut;
    private final Guest guest;
    private final Room room;
    private final BookingOrigin bookingOrigin;
    private final FloatProperty serviceFees = new SimpleFloatProperty();
    private final FloatProperty serviceFeesPercent = new SimpleFloatProperty();
    private final FloatProperty grossEarnings = new SimpleFloatProperty();
    private final FloatProperty cleaningFees = new SimpleFloatProperty();
    private final FloatProperty netEarnings = new SimpleFloatProperty();
    private final StringProperty grossEarningsExpression = new SimpleStringProperty();
    private final StringProperty checkInNote = new SimpleStringProperty();
    private final StringProperty checkOutNote = new SimpleStringProperty();
    private final StringProperty specialRequestNote = new SimpleStringProperty();
    private final BooleanProperty welcomeMailSend = new SimpleBooleanProperty(false);
    private final BooleanProperty paymentDone = new SimpleBooleanProperty(false);
    private final DoubleProperty paymentSoFar = new SimpleDoubleProperty();
    @Deprecated
    private final BooleanProperty splitBooking = new SimpleBooleanProperty(false);
    private final ObjectProperty<LocalDate> dateOfPayment = new SimpleObjectProperty<>(null);
    private final ListProperty<Payment> payments = new SimpleListProperty<>(FXCollections.observableArrayList());
    /**
     * Set by the cleaning entry.
     */
    private final ObjectProperty<CleaningEntry> cleaning = new SimpleObjectProperty<>();
    private String externalId;
    private List<String> calendarIds = new ArrayList<>();

    public BookingBean(final Guest guest, final Room room, final BookingOrigin origin, final LocalDate checkIn,
                       final LocalDate checkOut) {
        this(null, guest, room, origin, checkIn, checkOut);

    }

    public BookingBean(final String id, final Guest guest, final Room room, final BookingOrigin origin,
                       final LocalDate checkIn, final LocalDate checkOut) {
        super(id);
        Objects.requireNonNull(guest);
        Objects.requireNonNull(room);
        Objects.requireNonNull(origin);
        Objects.requireNonNull(checkIn);
        Objects.requireNonNull(checkOut);

        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.guest = guest;
        this.room = room;
        bookingOrigin = origin;

        bindProperties();

    }

    public static Callback<BookingBean, Observable[]> extractor() {
        return param -> new Observable[]{param.serviceFeeProperty(), param.serviceFeesPercentProperty(),
            param.grossEarningsProperty(), param.cleaningProperty(), param.netEarningsProperty(),
            param.checkInNoteProperty(), param.checkOutNoteProperty(), param.specialRequestNoteProperty(),
            param.paymentDoneProperty(), param.welcomeMailSendProperty(), param.dateOfPaymentProperty()};
    }

    public ReadOnlyDoubleProperty paymentSoFarProperty() {
        return paymentSoFar;
    }

    public final double getPaymentSoFar() {
        return paymentSoFar.get();
    }

    public Callable<Number> calculatePaymentSoFar() {
        return () -> payments.stream().mapToDouble(p -> p.getAmount().getNumber().doubleValue()).sum();
    }

    public ListProperty<Payment> paymentsProperty() {
        return payments;
    }

    public final List<Payment> getPayments() {
        return payments.get();
    }

    public final void setPayments(Collection<? extends Payment> payments) {
        this.payments.setAll(payments);
    }

    public List<BookingEntry> getEntries() {
        return Bookings.toEntries(this);
    }

    public void addCalendarId(final String id) {
        if (id != null) {
            calendarIds.add(id);
        }

    }

    private void bindProperties() {
        // if (logger.isDebugEnabled()) {
        // logger.debug("Binding on " + Thread.currentThread().getName());
        // }
        grossEarningsProperty()
            .bind(Bindings.createObjectBinding(evaluateExpression(), grossEarningsExpressionProperty()));
        netEarningsProperty().bind(Bindings.createObjectBinding(calculateNetEarnings(), grossEarningsProperty(),
            cleaningFeesProperty(), serviceFeeProperty(), serviceFeesPercentProperty(), cleaningProperty(),
            SettingsManager.getInstance().showNetEarningsProperty()));
        paymentDoneProperty().addListener((c, o, n) -> {
            if (n && getDateOfPayment() == null) {
                setDateOfPayment(LocalDate.now());
            } else if (!n) {
                setDateOfPayment(null);
            }
        });

        dateOfPaymentProperty().addListener((c, o, n) -> {
            if (n != null) {
                setPaymentDone(true);
            } else {
                setPaymentDone(false);
            }
        });
        paymentSoFar.bind(Bindings.createObjectBinding(calculatePaymentSoFar(), payments));
        paymentSoFar.addListener((o, ov, nv) -> {
            if (nv != null && nv.floatValue() > 0) {
                setPaymentDone(true);
            }
        });


    }

    private Callable<Number> calculateNetEarnings() {

        return () -> {
            final NetEarningsCalculator c = new DefaultNetEarningsCalculator();
            final Number result = c.apply(this);
            return result;
        };
    }

    public StringProperty checkInNoteProperty() {
        return checkInNote;
    }

    public StringProperty checkOutNoteProperty() {
        return checkOutNote;
    }

    @Override
    public int compareTo(final BookingBean o) {
        return getCheckIn().compareTo(o.getCheckIn());
    }

    private Callable<Number> evaluateExpression() {
        return () -> {
            return Scripting.evaluateExpression(getGrossEarningsExpression());
        };
    }

    @Override
    public BookingOrigin getBookingOrigin() {
        return bookingOrigin;
    }

    public List<String> getCalendarIds() {
        return calendarIds;
    }

    public void setCalendarIds(final Collection<? extends String> calendarIds) {
        if (calendarIds != null) {
            this.calendarIds = new ArrayList<>(calendarIds);
        }
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public String getCheckInNote() {
        return checkInNoteProperty().get();
    }

    public void setCheckInNote(final String checkInNote) {
        checkInNoteProperty().set(checkInNote);
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public String getCheckOutNote() {
        return checkOutNoteProperty().get();
    }

    public void setCheckOutNote(final String checkOutNote) {
        checkOutNoteProperty().set(checkOutNote);
    }

    @Override
    public float getEarnings(final boolean netEarnings) {
        if (netEarnings) {
            return getNetEarnings();
        }
        return getGrossEarnings();
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(final String externalId) {
        this.externalId = externalId;
    }

    @Override
    public float getGrossEarnings() {
        return grossEarningsProperty().get();
    }

    public void setGrossEarnings(final float grossEarnings) {
        grossEarningsProperty().set(grossEarnings);
        // if (logger.isDebugEnabled()) {
        // logger.debug("Gross Earnings changed to " + getGrossEarnings());
        // }

    }

    public String getGrossEarningsExpression() {
        return grossEarningsExpressionProperty().get();
    }

    public void setGrossEarningsExpression(final String expression) {
        // System.err.println("set " + expression);
        grossEarningsExpressionProperty().set(expression);
    }

    public Guest getGuest() {
        return guest;
    }

    @Override
    public float getNetEarnings() {
        return netEarningsProperty().get();
    }

    public void setNetEarnings(final float netEarnings) {
        netEarningsProperty().set(netEarnings);
    }

    public int getNumberOfDays() {
        final int daysElapsed = (int) ChronoUnit.DAYS.between(getCheckIn(), getCheckOut());
        return daysElapsed + 1;
    }

    public int getNumberOfNights() {
        final int daysElapsed = getNumberOfDays();
        return daysElapsed - 1;
    }

    public Room getRoom() {
        return room;
    }

    public RoundingMode getRoundingMode() {
        return roundingMode;
    }

    public float getServiceFee() {
        return serviceFeeProperty().get();
    }

    public void setServiceFee(final float serviceFee) {
        serviceFeeProperty().set(serviceFee);
    }

    public String getSpecialRequestNote() {
        return specialRequestNoteProperty().get();
    }

    public void setSpecialRequestNote(final String specialRequestNote) {
        specialRequestNoteProperty().set(specialRequestNote);
    }

    public StringProperty grossEarningsExpressionProperty() {
        return grossEarningsExpression;
    }

    @Override
    public FloatProperty grossEarningsProperty() {
        return grossEarnings;
    }

    @Override
    public boolean isPaymentDone() {
        return paymentDoneProperty().get();
    }

    public void setPaymentDone(final boolean paymentDone) {
        paymentDoneProperty().set(paymentDone);
    }

    public boolean isWelcomeMailSend() {
        return welcomeMailSendProperty().get();
    }

    public void setWelcomeMailSend(final boolean welcomeMailSend) {
        welcomeMailSendProperty().set(welcomeMailSend);
    }

    @Override
    public FloatProperty netEarningsProperty() {
        return netEarnings;
    }

    public BooleanProperty paymentDoneProperty() {
        return paymentDone;
    }

    public FloatProperty serviceFeeProperty() {
        return serviceFees;
    }

    public StringProperty specialRequestNoteProperty() {
        return specialRequestNote;
    }

    @Override
    public String toString() {
        return "BookingBean{" +
            "checkIn=" + checkIn +
            ",\tcheckOut=" + checkOut +
            ",\tguest=" + guest +
            ",\troom=" + room +
            ",\tbookingOrigin=" + bookingOrigin +
            ",\tcleaningFees=" + cleaningFees +
            ",\tcleaning=" + cleaning +
            '}';
    }

    public BooleanProperty welcomeMailSendProperty() {
        return welcomeMailSend;
    }

    public final FloatProperty cleaningFeesProperty() {
        return cleaningFees;
    }

    public final float getCleaningFees() {
        return cleaningFeesProperty().get();
    }

    public final void setCleaningFees(final float cleaningFees) {
        cleaningFeesProperty().set(cleaningFees);
    }

    public final FloatProperty serviceFeesPercentProperty() {
        return serviceFeesPercent;
    }

    public final float getServiceFeesPercent() {
        return serviceFeesPercentProperty().get();
    }

    public final void setServiceFeesPercent(final float serviceFeesPercent) {
        serviceFeesPercentProperty().set(serviceFeesPercent);
    }

    public BookingEntry getEntry(final LocalDate date) {
        if (date.isBefore(getCheckIn()) && date.isAfter(getCheckOut())) {
            throw new NoSuchElementException(
                "For date " + date + "checkin:" + getCheckIn() + ",checkout:" + getCheckOut());
        }
        return new BookingEntry(date, this);
    }

    /**
     * Set by the cleaning entry.
     *
     * @return
     */
    public final ObjectProperty<CleaningEntry> cleaningProperty() {
        return cleaning;
    }

    public final CleaningEntry getCleaning() {
        return cleaningProperty().get();
    }

    /**
     * Set by the cleaning entry.
     *
     * @param cleaning
     */
    public final void setCleaning(final CleaningEntry cleaning) {
        cleaningProperty().set(cleaning);
    }

    public final ObjectProperty<LocalDate> dateOfPaymentProperty() {
        return dateOfPayment;
    }

    public final LocalDate getDateOfPayment() {
        return dateOfPaymentProperty().get();
    }

    public final void setDateOfPayment(final LocalDate dateOfPayment) {
        dateOfPaymentProperty().set(dateOfPayment);
    }

    public final BooleanProperty splitBookingProperty() {
        return splitBooking;
    }

    public final boolean isSplitBooking() {
        return splitBookingProperty().get();
    }

    public final void setSplitBooking(final boolean splitBooking) {
        splitBookingProperty().set(splitBooking);
    }

    @Override
    public boolean isPaymentOverdue() {
        final boolean lastMonth = getCheckIn().query(TemporalQueries::isPreviousMonthOrEarlier);

        return !isPaymentDone() && lastMonth;
    }

}
