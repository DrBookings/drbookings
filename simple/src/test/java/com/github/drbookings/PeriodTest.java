package com.github.drbookings;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;

import org.junit.Test;

public class PeriodTest {

    @Test
    public void testOneMonthPeriodDays01() {
	final Period p = Period.between(LocalDate.of(2017, 06, 01), LocalDate.of(2017, 07, 01));
	assertEquals(0, p.getDays());
    }

    @Test
    public void testOneMonthPeriodDays02() {
	final long daysElapsed = ChronoUnit.DAYS.between(LocalDate.of(2017, 06, 01), LocalDate.of(2017, 07, 01));
	assertEquals(30, daysElapsed);
    }

}
