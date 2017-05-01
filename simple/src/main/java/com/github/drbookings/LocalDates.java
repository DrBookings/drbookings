package com.github.drbookings;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class LocalDates {

    public static Pair<LocalDate, LocalDate> getFirstAndLastDayOfMonth(final int month) {
	final int year = LocalDate.now().getYear();
	final LocalDate firstDay = LocalDate.of(year, month, 1);
	final LocalDate lastDay = LocalDate.of(year, month,
		firstDay.with(java.time.temporal.TemporalAdjusters.lastDayOfMonth()).getDayOfMonth());
	return new ImmutablePair<>(firstDay, lastDay);
    }

    public static TextFlow getDateText(final LocalDate date) {
	final TextFlow tf = new TextFlow();
	final Text t0 = new Text(getDateString(date));
	tf.getChildren().addAll(t0);
	return tf;
    }

    public static String getDateString(final LocalDate date) {
	return date.format(DateTimeFormatter.ofPattern("EE, dd MMM"));
    }

    public static TextFlow getYearText(final LocalDate date) {
	final TextFlow tf = new TextFlow();
	final Text t0 = new Text(getYearString(date));
	t0.getStyleClass().add("center-text");
	tf.getChildren().addAll(t0);
	return tf;
    }

    public static String getYearString(final LocalDate date) {
	return date.format(DateTimeFormatter.ofPattern("yyyy"));
    }

}
