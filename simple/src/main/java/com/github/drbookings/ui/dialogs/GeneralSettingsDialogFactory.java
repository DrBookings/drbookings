package com.github.drbookings.ui.dialogs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeneralSettingsDialogFactory extends AbstractViewFactory implements ViewFactory {

    private static final Logger logger = LoggerFactory.getLogger(GeneralSettingsDialogFactory.class);

    public GeneralSettingsDialogFactory() {
	setFxml("/fxml/GeneralSettingsView.fxml");
	setTitle("Settings");
	setHeight(400);
	setWidth(400);
    }
}
