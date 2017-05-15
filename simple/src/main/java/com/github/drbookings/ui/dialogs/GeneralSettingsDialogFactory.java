package com.github.drbookings.ui.dialogs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeneralSettingsDialogFactory extends AbstractDialogFactory implements DialogFactory {

    private static final Logger logger = LoggerFactory.getLogger(GeneralSettingsDialogFactory.class);

    public GeneralSettingsDialogFactory() {
	setFxml("/fxml/GeneralSettingsView.fxml");
	setTitle("Settings");
    }
}
