package com.github.drbookings.model;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collection;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.model.settings.SettingsManager;
import com.github.drbookings.ui.beans.RoomBean;

public class MinimumPriceCalulcator implements Function<Collection<RoomBean>, Number> {

	private static final Logger logger = LoggerFactory.getLogger(MinimumPriceCalulcator.class);

	private final Number refIncome;

	private final OccupancyRateCalculator orc = new OccupancyRateCalculator();

	public MinimumPriceCalulcator(final Number refIncome) {

		this.refIncome = refIncome;

	}

	@Override
	public Number apply(final Collection<RoomBean> rooms) {

		if (rooms.isEmpty()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Nothing selected");
			}
			return Double.NaN;
		}
		final Set<LocalDate> refDays = rooms.stream().map(r -> r.getDate()).collect(Collectors.toSet());
		final Set<YearMonth> months = refDays.stream().map(d -> YearMonth.from(d)).collect(Collectors.toSet());
		final OptionalDouble avDays = months.stream().mapToDouble(ym -> ym.getMonth().maxLength()).average();
		final double or = orc.apply(rooms).doubleValue();
		final double ref2 = refIncome.doubleValue() / avDays.getAsDouble() * refDays.size();
		final double daysBusy = refDays.size() * or;
		final double result = ref2 / daysBusy / SettingsManager.getInstance().getNumberOfRooms();
		// if (logger.isDebugEnabled()) {
		// logger.debug("Days total: " + refDays.size());
		// logger.debug("Days busy: " + daysBusy);
		// logger.debug("OccupancyRate: " + or);
		// logger.debug("RefIncome: " + (ref2));
		// logger.debug("MinPrice: " + result);
		// }

		return result;
	}

}
