package com.github.drbookings.ui.dialogs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.ui.controller.EarningsChartViewController;

public class EarningsChartFactory extends AbstractViewFactory implements ViewFactory {

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(EarningsChartFactory.class);

	public EarningsChartFactory() {
		setFxml("/fxml/EarningsChartView.fxml");
		setTitle("Earnings per Day");
		setHeight(400);
		setWidth(400);

	}

	@Override
	protected void visitController(final Object controller) {
		super.visitController(controller);
		final EarningsChartViewController c = (EarningsChartViewController) controller;
		// c.setMainManager(manager);
	}
}
