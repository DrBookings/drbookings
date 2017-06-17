package com.github.drbookings.model.ser;

import java.time.LocalDate;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.github.drbookings.ser.LocalDateAdapter;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class CleaningBeanSer {

	@Override
	public String toString() {
		return "CleaningBeanSer date=" + date + ", name=" + name + ", room=" + room + "]";
	}

	@XmlAttribute
	public List<String> calendarIds;

	@XmlAttribute
	public float cleaningCosts;

	@XmlAttribute
	@XmlJavaTypeAdapter(value = LocalDateAdapter.class)
	public LocalDate date;

	@XmlAttribute
	public String name;

	@XmlAttribute
	public String bookingId;

	@XmlAttribute
	public String room;

}
