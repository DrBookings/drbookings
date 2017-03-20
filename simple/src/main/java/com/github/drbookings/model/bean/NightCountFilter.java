package com.github.drbookings.model.bean;

import java.util.function.Predicate;

public class NightCountFilter implements Predicate<BookingBean> {

    @Override
    public boolean test(final BookingBean t) {
	if (t.getRoom() == null || t.getRoom().getDateBean() == null
		|| t.getRoom().getDateBean().getDataModel() == null) {
	    return true;
	}
	switch (t.getRoom().getDateBean().getDataModel().getModelConfiguration().getNightCounting()) {
	case DAY_AFTER:
	    return !t.isCheckIn();

	case DAY_BEFORE:
	    return !t.isCheckOut();
	default:
	    return true;
	}
    }

}
