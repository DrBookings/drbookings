package com.github.drbookings.ser;

import java.time.LocalDate;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class LocalDateAdapter extends XmlAdapter<String, LocalDate> {
    @Override
    public String marshal(final LocalDate v) throws Exception {
	if (v == null) {
	    return null;
	}
	return v.toString();
    }

    @Override
    public LocalDate unmarshal(final String v) throws Exception {
	return LocalDate.parse(v);
    }
}
