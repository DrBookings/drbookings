package com.github.drbookings.ui.dialogs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.model.data.manager.MainManager;
import com.github.drbookings.ui.controller.StatsViewController;

public class StatisticsFactory extends AbstractViewFactory implements ViewFactory {

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(StatisticsFactory.class);
	private final MainManager manager;

	public StatisticsFactory(final MainManager manager) {
		setFxml("/fxml/StatisticsView.fxml");
		setTitle("Statistics for Selection");
		setHeight(240);
		setWidth(400);
		this.manager = manager;
	}

	@Override
	protected void visitController(final Object controller) {
		super.visitController(controller);
		final StatsViewController c = (StatsViewController) controller;
		c.setMainManager(manager);
	}
}
