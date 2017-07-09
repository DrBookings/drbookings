package com.github.drbookings.model.data;

import java.time.LocalDate;
import java.util.Collection;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.github.drbookings.LocalDates;
import com.github.drbookings.ui.beans.DateBean;
import com.google.common.collect.Range;

public class DateBeans {

	public static Range<LocalDate> getDateRange(final Collection<? extends DateBean> dateBeans) {
		return LocalDates
				.getDateRange(dateBeans.stream().map(b -> b.getDate()).collect(Collectors.toCollection(TreeSet::new)));
	}

}
