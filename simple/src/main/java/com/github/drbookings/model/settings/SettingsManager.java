package com.github.drbookings.model.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.prefs.Preferences;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.DrBookingsApplication;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import net.sf.kerner.utils.objects.Objects;

public class SettingsManager {

    private static class InstanceHolder {

	private static final SettingsManager instance = new SettingsManager();
    }

    private static final String cleaningPlanLookBehindKey = "cleaningPlanLookBehindKey";

    private static final String completePaymentKey = "completePaymentKey";

    private static final String upcomingLookAheadKey = "upcomingLookAheadKey";

    private static final String roomNamePrefixKey = "roomPrefixKey";

    private static final String cleaningFeeKey = "cleaningFeeKey";

    private static final String roomNameMapKey = "roomNameMapKey";

    public static final int DEFAULT_CLEANINGPLAN_LOOKBEHIND_DAYS = 7;

    public static final boolean DEFAULT_COMPLETE_PAYMENT = false;

    public static final int DEFAULT_UPCOMING_LOOK_AHEAD_DAYS = 3;

    public static final float DEFAULT_CLEANING_FEE = 0;

    private static final String fileKey = "data";

    public static final String DEFAULT_FILE_NAME = "booking-data.xml";

    public static final String DEFAULT_ROOM_NAME_PREFIX = "F";

    private static final Logger logger = LoggerFactory.getLogger(SettingsManager.class);

    public static SettingsManager getInstance() {
	return InstanceHolder.instance;
    }

    static Properties readProperties() {
	final Properties prop = new Properties();
	InputStream input = null;
	try {
	    input = new FileInputStream(DrBookingsApplication.CONFIG_FILE_PATH);
	    prop.load(input);
	    if (logger.isInfoEnabled()) {
		logger.info("Read settings from " + DrBookingsApplication.CONFIG_FILE_PATH);
	    }
	    return prop;
	} catch (final Exception ex) {
	    if (logger.isDebugEnabled()) {
		logger.debug("Failed to read properties file " + ex.toString());
	    }
	} finally {
	    IOUtils.closeQuietly(input);
	}
	return null;
    }

    static void saveNetEarningsToFile(final Boolean newValue) {
	final Properties prop = readProperties();
	OutputStream output = null;
	try {
	    output = new FileOutputStream(DrBookingsApplication.CONFIG_FILE_PATH);
	    // set the properties value
	    prop.setProperty(DrBookingsApplication.SHOW_NET_EARNINGS_KEY, newValue.toString());
	    // save properties to project root folder
	    prop.store(output, "Dr.Bookings Preferences");
	    if (logger.isDebugEnabled()) {
		logger.debug("Properties file updated");
	    }
	} catch (final IOException io) {
	    if (logger.isErrorEnabled()) {
		logger.debug("Failed to write properties file " + io.toString());
	    }
	} finally {
	    IOUtils.closeQuietly(output);
	}

    }

    private final BooleanProperty completePayment = new SimpleBooleanProperty(DEFAULT_COMPLETE_PAYMENT);

    private final FloatProperty fees = new SimpleFloatProperty(0f);

    private final FloatProperty referenceColdRentLongTerm = new SimpleFloatProperty();

    private final FloatProperty workHoursPerMonth = new SimpleFloatProperty();

    private final FloatProperty additionalCosts = new SimpleFloatProperty();

    private final BooleanProperty showNetEarnings = new SimpleBooleanProperty();

    private final IntegerProperty numberOfRooms = new SimpleIntegerProperty();

    private final Preferences cleaningFees = Preferences.userNodeForPackage(getClass());

    private SettingsManager() {
	completePayment.set(cleaningFees.getBoolean(completePaymentKey, false));
	completePayment.addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> cleaningFees
		.putBoolean(completePaymentKey, newValue));
	cleaningFeesProperty().set(cleaningFees.getFloat(cleaningFeeKey, DEFAULT_CLEANING_FEE));
	cleaningFeesProperty().addListener((ChangeListener<Number>) (observable, oldValue, newValue) -> {
	    cleaningFees.putFloat(cleaningFeeKey, newValue.floatValue());
	});
	showNetEarnings.addListener(new ChangeListener<Boolean>() {

	    @Override
	    public void changed(final ObservableValue<? extends Boolean> observable, final Boolean oldValue,
		    final Boolean newValue) {
		saveNetEarningsToFile(newValue);
	    }
	});
    }

    public final FloatProperty additionalCostsProperty() {
	return this.additionalCosts;
    }

    public FloatProperty cleaningFeesProperty() {
	return this.fees;
    }

    public BooleanProperty completePaymentProperty() {
	return this.completePayment;
    }

    public final float getAdditionalCosts() {
	return this.additionalCostsProperty().get();
    }

    public float getCleaningFees() {
	return this.cleaningFeesProperty().get();
    }

    public int getCleaningPlanLookBehind() {
	return cleaningFees.getInt(SettingsManager.cleaningPlanLookBehindKey, DEFAULT_CLEANINGPLAN_LOOKBEHIND_DAYS);
    }

    public File getDataFile() {
	final String fileString = cleaningFees.get(fileKey,
		System.getProperty("user.home") + File.separator + DEFAULT_FILE_NAME);
	return new File(fileString);
    }

    public final int getNumberOfRooms() {
	return this.numberOfRoomsProperty().get();
    }

    public final float getReferenceColdRentLongTerm() {
	return this.referenceColdRentLongTermProperty().get();
    }

    public Map<String, String> getRoomNameMappings() throws ClassNotFoundException, IOException {
	final byte[] bytes = cleaningFees.getByteArray(roomNameMapKey, null);
	if (bytes == null) {
	    return new LinkedHashMap<>();
	}
	@SuppressWarnings("unchecked")
	final Map<String, String> map = (Map<String, String>) Objects.fromBytes(bytes);
	return map;
    }

    public String getRoomNamePrefix() {
	return cleaningFees.get(roomNamePrefixKey, DEFAULT_ROOM_NAME_PREFIX);
    }

    public int getUpcomingLookAhead() {
	return cleaningFees.getInt(SettingsManager.upcomingLookAheadKey, DEFAULT_UPCOMING_LOOK_AHEAD_DAYS);
    }

    public final float getWorkHoursPerMonth() {
	return this.workHoursPerMonthProperty().get();
    }

    public boolean isCompletePayment() {
	return this.completePaymentProperty().get();
    }

    public final boolean isShowNetEarnings() {
	return this.showNetEarningsProperty().get();
    }

    public final IntegerProperty numberOfRoomsProperty() {
	return this.numberOfRooms;
    }

    public void putRoomNameMapping(final Map.Entry<String, String> pair) throws ClassNotFoundException, IOException {
	final Map<String, String> map = getRoomNameMappings();
	map.put(pair.getKey(), pair.getValue());
	setRoomNameMapping(map);
    }

    public void putRoomNameMapping(final String vendorName, final String ourName)
	    throws ClassNotFoundException, IOException {
	putRoomNameMapping(new ImmutablePair<>(vendorName, ourName));

    }

    public final FloatProperty referenceColdRentLongTermProperty() {
	return this.referenceColdRentLongTerm;
    }

    public final void setAdditionalCosts(final float additionalCosts) {
	this.additionalCostsProperty().set(additionalCosts);
    }

    public void setCleaningFees(final float fees) {
	this.cleaningFeesProperty().set(fees);
    }

    public void setCleaningPlanLookBehind(final int value) {
	cleaningFees.putInt(cleaningPlanLookBehindKey, value);

    }

    public void setCompletePayment(final boolean completePayment) {
	this.completePaymentProperty().set(completePayment);
    }

    public void setDataFile(final File file) {
	cleaningFees.put(fileKey, file.getAbsolutePath());
    }

    public final void setNumberOfRooms(final int numberOfRooms) {
	this.numberOfRoomsProperty().set(numberOfRooms);
    }

    public final void setReferenceColdRentLongTerm(final float referenceColdRentLongTerm) {
	this.referenceColdRentLongTermProperty().set(referenceColdRentLongTerm);
    }

    public void setRoomNameMapping(final Map<String, String> map) throws IOException {
	cleaningFees.putByteArray(roomNameMapKey, Objects.toBytes(map));
    }

    public void setRoomNamePrefix(final String prefix) {
	cleaningFees.put(roomNamePrefixKey, prefix);
    }

    public final void setShowNetEarnings(final boolean showNetEarnings) {
	this.showNetEarningsProperty().set(showNetEarnings);
    }

    public void setUpcomingLookAhead(final int value) {
	cleaningFees.putInt(upcomingLookAheadKey, value);

    }

    public final void setWorkHoursPerMonth(final float workHoursPerMonth) {
	this.workHoursPerMonthProperty().set(workHoursPerMonth);
    }

    public final BooleanProperty showNetEarningsProperty() {
	return this.showNetEarnings;
    }

    public final FloatProperty workHoursPerMonthProperty() {
	return this.workHoursPerMonth;
    }

}
