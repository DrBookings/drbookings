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

package com.github.drbookings.ui.dialogs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.ui.controller.EarningsPerDayChartViewController;

public class EarningsChartFactory extends AbstractViewFactory implements ViewFactory {

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(EarningsChartFactory.class);

    public EarningsChartFactory() {
	setFxml("/fxml/EarningsPerDayChartView.fxml");
	setTitle("Earnings per Day");
	setHeight(400);
	setWidth(400);

    }

    @Override
    protected void visitController(final Object controller) {
	super.visitController(controller);
	final EarningsPerDayChartViewController c = (EarningsPerDayChartViewController) controller;
	// c.setMainManager(manager);
    }
}
