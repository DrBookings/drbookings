package com.github.drbookings;

import java.time.LocalDate;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class LocalDateAdapter extends XmlAdapter<String, LocalDate> {
    public String marshal(final LocalDate v) throws Exception {
	return v.toString();
    }

    public LocalDate unmarshal(final String v) throws Exception {
	return LocalDate.parse(v);
    }
}
