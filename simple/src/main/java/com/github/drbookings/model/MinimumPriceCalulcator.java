/*
 * DrBookings
 *
 * Copyright (C) 2016 - 2018 Alexander Kerner
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 */

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
