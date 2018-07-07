package com.github.drbookings.model.data.ser;

import java.time.LocalDate;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.github.drbookings.ser.LocalDateAdapter;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class ExpenseSer {

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + Float.floatToIntBits(amount);
	result = prime * result + (date == null ? 0 : date.hashCode());
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
	if (!(obj instanceof ExpenseSer)) {
	    return false;
	}
	final ExpenseSer other = (ExpenseSer) obj;
	if (Float.floatToIntBits(amount) != Float.floatToIntBits(other.amount)) {
	    return false;
	}
	if (date == null) {
	    if (other.date != null) {
		return false;
	    }
	} else if (!date.equals(other.date)) {
	    return false;
	}
	return true;
    }

    @XmlAttribute
    @XmlJavaTypeAdapter(value = LocalDateAdapter.class)
    public LocalDate date;

    @XmlAttribute
    public float amount;

}
