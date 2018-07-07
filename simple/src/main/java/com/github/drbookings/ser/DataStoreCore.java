package com.github.drbookings.ser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.github.drbookings.model.data.ser.CleaningExpenseSer;
import com.github.drbookings.model.data.ser.ExpenseSer;
import com.github.drbookings.model.ser.BookingBeanSer;
import com.github.drbookings.model.ser.CleaningBeanSer;

@XmlRootElement
public class DataStoreCore {

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (bookings == null ? 0 : bookings.hashCode());
	result = prime * result + (cleaningExpenses == null ? 0 : cleaningExpenses.hashCode());
	result = prime * result + (cleanings == null ? 0 : cleanings.hashCode());
	result = prime * result + (expenses == null ? 0 : expenses.hashCode());
	return result;
    }

    @Override
    public boolean equals(final Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (!(obj instanceof DataStoreCore)) {
	    return false;
	}
	final DataStoreCore other = (DataStoreCore) obj;
	if (bookings == null) {
	    if (other.bookings != null) {
		return false;
	    }
	} else if (!bookings.equals(other.bookings)) {
	    return false;
	}
	if (cleaningExpenses == null) {
	    if (other.cleaningExpenses != null) {
		return false;
	    }
	} else if (!cleaningExpenses.equals(other.cleaningExpenses)) {
	    return false;
	}
	if (cleanings == null) {
	    if (other.cleanings != null) {
		return false;
	    }
	} else if (!cleanings.equals(other.cleanings)) {
	    return false;
	}
	if (expenses == null) {
	    if (other.expenses != null) {
		return false;
	    }
	} else if (!expenses.equals(other.expenses)) {
	    return false;
	}
	return true;
    }

    private final List<BookingBeanSer> bookings = new ArrayList<>();

    private final List<CleaningBeanSer> cleanings = new ArrayList<>();

    private final List<ExpenseSer> expenses = new ArrayList<>();

    private final List<CleaningExpenseSer> cleaningExpenses = new ArrayList<>();

    public DataStoreCore setBookingSer(final Collection<? extends BookingBeanSer> bookings) {
	this.bookings.clear();
	this.bookings.addAll(bookings);
	return this;
    }

    public DataStoreCore setCleaningSer(final Collection<? extends CleaningBeanSer> cleanings) {
	this.cleanings.clear();
	this.cleanings.addAll(cleanings);
	return this;
    }

    public DataStoreCore setExpenses(final Collection<? extends ExpenseSer> expenses) {
	this.expenses.clear();
	this.expenses.addAll(expenses);
	return this;
    }

    public DataStoreCore setCleaningExpenses(final Collection<? extends CleaningExpenseSer> expenses) {
	this.cleaningExpenses.clear();
	this.cleaningExpenses.addAll(expenses);
	return this;
    }

    @XmlElementWrapper(name = "bookings")
    @XmlElement(name = "booking")
    public List<BookingBeanSer> getBookingsSer() {
	return bookings;
    }

    @XmlElementWrapper(name = "cleanings")
    @XmlElement(name = "cleaning")
    public List<CleaningBeanSer> getCleaningsSer() {
	return cleanings;
    }

    @XmlElementWrapper(name = "expenses")
    @XmlElement(name = "expense")
    public List<ExpenseSer> getExpenses() {
	return expenses;
    }

    @XmlElementWrapper(name = "cleaning-expenses")
    @XmlElement(name = "cleaning-expense")
    public List<CleaningExpenseSer> getCleaningExpenses() {
	return cleaningExpenses;
    }

}
