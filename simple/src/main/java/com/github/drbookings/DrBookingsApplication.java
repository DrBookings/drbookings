package com.github.drbookings;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.model.settings.SettingsManager;
import com.github.drbookings.ser.XMLStorage;
import com.github.drbookings.ui.controller.MainController;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class DrBookingsApplication extends Application {

	private class CloseRequestEventHandler implements EventHandler<WindowEvent> {

		@Override
		public void handle(final WindowEvent event) {

			final Alert alert = new Alert(AlertType.CONFIRMATION);
			final ButtonType buttonTypeOne = new ButtonType("Yes", ButtonData.OK_DONE);
			final ButtonType buttonTypeTwo = new ButtonType("No", ButtonData.NO);
			final ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
			alert.getButtonTypes().setAll(buttonTypeCancel, buttonTypeTwo, buttonTypeOne);
			alert.setTitle("Save changes?");
			alert.setHeaderText("Save changes?");

			final Optional<ButtonType> result = alert.showAndWait();

			if (result.get() == buttonTypeOne) {
				try {
					final FileChooser fileChooser = new FileChooser();
					final File file = SettingsManager.getInstance().getDataFile();
					fileChooser.setInitialDirectory(file.getParentFile());
					fileChooser.getExtensionFilters().addAll(
							new FileChooser.ExtensionFilter("Dr.Booking Booking Data", Arrays.asList("*.xml")),
							new FileChooser.ExtensionFilter("All Files", "*"));
					fileChooser.setTitle("Select File");
					fileChooser.setInitialFileName(file.getName());
					final File file2 = fileChooser.showSaveDialog(((Stage) event.getSource()));
					if (file2 != null) {
						SettingsManager.getInstance().setDataFile(file2);
						new XMLStorage().save(mainController.getManager(), file2);
					}
				} catch (final Exception e) {
					logger.error(e.getLocalizedMessage(), e);
				}
				// go ahead..
			} else if (result.get() == buttonTypeTwo) {
				// go ahead..
			} else {
				// cancel shutdown
				event.consume();
			}
		}
	}

	public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("E\tdd.MM.yyyy");

	private final static Logger logger = LoggerFactory.getLogger(DrBookingsApplication.class);

	public static final String CONFIG_FILE_PATH = "drbookings.properties";

	public static final String DATA_FILE_KEY = "data-file";

	public static final String ADDITIONAL_COSTS_KEY = "additional-costs-permonth-perroom";

	public static final String REFERENCE_COLD_RENT_LONGTERM_KEY = "reference-coldrent-longterm-permonth-perroom";

	public static final String WORK_HOURS_PER_MONTH_KEY = "work-hours-permonth";

	public static final String NUMBER_OF_ROOMS_KEY = "number-of-rooms";

	public static final String ROOM_NAME_PREFIX_KEY = "room-name-prefix";

	public static final String SHOW_NET_EARNINGS_KEY = "show-net-earnings";

	private static final String CLEANING_FEES_KEY = "default-cleaning-fees";

	private static final String SERVICE_FEES_KEY = "default-service-fees";

	private static final String SERVICE_FEES_PERCENT_KEY = "default-service-fees-percent";

	private static final String CLEANING_COSTS_KEY = "default-cleaning-costs";

	public static void main(final String[] args) {
		launch(args);
	}

	static void readProperties() {
		final Properties prop = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream(System.getProperty("user.home") + File.separator + CONFIG_FILE_PATH);
			if (logger.isInfoEnabled()) {
				logger.info("Reading properties from " + System.getProperty("user.home") + File.separator
						+ CONFIG_FILE_PATH);
			}
			prop.load(input);

			try {
				SettingsManager.getInstance().setDataFile(new File(prop.getProperty(DATA_FILE_KEY)));
			} catch (final Exception ex) {
				if (logger.isDebugEnabled()) {
					logger.debug("Failed to parse " + DATA_FILE_KEY + ", " + ex.toString());
				}
			}
			try {
				SettingsManager.getInstance()
						.setAdditionalCosts(Float.parseFloat(prop.getProperty(ADDITIONAL_COSTS_KEY)));
			} catch (final Exception ex) {
				if (logger.isDebugEnabled()) {
					logger.debug("Failed to parse " + ADDITIONAL_COSTS_KEY + ", " + ex.toString());
				}
			}
			try {
				SettingsManager.getInstance().setReferenceColdRentLongTerm(
						Float.parseFloat(prop.getProperty(REFERENCE_COLD_RENT_LONGTERM_KEY)));
			} catch (final Exception ex) {
				if (logger.isDebugEnabled()) {
					logger.debug("Failed to parse " + REFERENCE_COLD_RENT_LONGTERM_KEY + ", " + ex.toString());
				}
			}
			try {
				SettingsManager.getInstance().setNumberOfRooms(Integer.parseInt(prop.getProperty(NUMBER_OF_ROOMS_KEY)));
			} catch (final Exception ex) {
				if (logger.isDebugEnabled()) {
					logger.debug("Failed to parse " + NUMBER_OF_ROOMS_KEY + ", " + ex.toString());
				}
			}
			try {
				SettingsManager.getInstance()
						.setWorkHoursPerMonth(Float.parseFloat(prop.getProperty(WORK_HOURS_PER_MONTH_KEY)));
			} catch (final Exception ex) {
				if (logger.isDebugEnabled()) {
					logger.debug("Failed to parse " + WORK_HOURS_PER_MONTH_KEY + ", " + ex.toString());
				}
			}
			try {
				SettingsManager.getInstance().setRoomNamePrefix(prop.getProperty(ROOM_NAME_PREFIX_KEY));
			} catch (final Exception ex) {
				if (logger.isDebugEnabled()) {
					logger.debug("Failed to parse " + ROOM_NAME_PREFIX_KEY + ", " + ex.toString());
				}
			}
			try {
				SettingsManager.getInstance()
						.setShowNetEarnings(Boolean.parseBoolean(prop.getProperty(SHOW_NET_EARNINGS_KEY)));
			} catch (final Exception ex) {
				if (logger.isDebugEnabled()) {
					logger.debug("Failed to parse " + SHOW_NET_EARNINGS_KEY + ", " + ex.toString());
				}
			}
			try {
				SettingsManager.getInstance().setCleaningFees(Float.parseFloat(prop.getProperty(CLEANING_FEES_KEY)));
			} catch (final Exception ex) {
				if (logger.isDebugEnabled()) {
					logger.debug("Failed to parse " + CLEANING_FEES_KEY + ", " + ex.toString());
				}
			}
			try {
				SettingsManager.getInstance().setCleaningCosts(Float.parseFloat(prop.getProperty(CLEANING_COSTS_KEY)));
			} catch (final Exception ex) {
				if (logger.isDebugEnabled()) {
					logger.debug("Failed to parse " + CLEANING_COSTS_KEY + ", " + ex.toString());
				}
			}
			try {
				SettingsManager.getInstance().setServiceFees(Float.parseFloat(prop.getProperty(SERVICE_FEES_KEY)));
			} catch (final Exception ex) {
				if (logger.isDebugEnabled()) {
					logger.debug("Failed to parse " + SERVICE_FEES_KEY + ", " + ex.toString());
				}
			}
			try {
				SettingsManager.getInstance()
						.setServiceFeesPercent(Float.parseFloat(prop.getProperty(SERVICE_FEES_PERCENT_KEY)));
			} catch (final Exception ex) {
				if (logger.isDebugEnabled()) {
					logger.debug("Failed to parse " + SERVICE_FEES_PERCENT_KEY + ", " + ex.toString());
				}
			}
		} catch (final Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error(e.getLocalizedMessage(), e);
			}
		} finally {
			IOUtils.closeQuietly(input);
		}
	}

	private MainController mainController;

	@Override
	public void start(final Stage stage) throws Exception {
		if (logger.isInfoEnabled()) {
			logger.info("Application version " + getClass().getPackage().getImplementationVersion());
		}
		readProperties();
		startGUI(stage);
	}

	private void startGUI(final Stage stage) throws IOException {
		final FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
		final Parent root = loader.load();
		final Scene scene = new Scene(root, 900, 800);
		mainController = loader.getController();
		String s = getClass().getPackage().getImplementationVersion();
		if (s == null) {
			s = "dev version";
		}
		stage.setTitle("Dr.Bookings " + s);
		stage.setScene(scene);
		stage.setOnCloseRequest(new CloseRequestEventHandler());
		stage.show();
		mainController.readDataFile(SettingsManager.getInstance().getDataFile());

	}

	@Override
	public void stop() throws Exception {
		mainController.shutDown();
		super.stop();
	}
}
