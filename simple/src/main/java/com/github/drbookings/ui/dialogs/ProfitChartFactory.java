package com.github.drbookings.ui.dialogs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.ui.controller.ProfitChartController;

public class ProfitChartFactory extends AbstractViewFactory implements ViewFactory {

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(ProfitChartFactory.class);

	public ProfitChartFactory() {
		setFxml("/fxml/OverviewChartView.fxml");
		setTitle("Performance");
		setHeight(400);
		setWidth(600);

	}

	@Override
	protected void visitController(final Object controller) {
		super.visitController(controller);
		final ProfitChartController c = (ProfitChartController) controller;
		// c.setMainManager(manager);
	}
}
