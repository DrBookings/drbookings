package com.github.drbookings.model.settings;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.prefs.Preferences;

import org.apache.commons.lang3.tuple.ImmutablePair;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.value.ChangeListener;
import net.sf.kerner.utils.objects.Objects;

public class SettingsManager {

    private static class InstanceHolder {

	private static final SettingsManager instance = new SettingsManager();
    }

    private final BooleanProperty completePayment = new SimpleBooleanProperty(DEFAULT_COMPLETE_PAYMENT);

    private final FloatProperty fees = new SimpleFloatProperty(0f);

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

    public static SettingsManager getInstance() {
	return InstanceHolder.instance;
    }

    private final Preferences cleaningFees = Preferences.userNodeForPackage(getClass());

    private SettingsManager() {
	completePayment.set(cleaningFees.getBoolean(completePaymentKey, false));
	completePayment.addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> cleaningFees
		.putBoolean(completePaymentKey, newValue));
	cleaningFeesProperty().set(cleaningFees.getFloat(cleaningFeeKey, DEFAULT_CLEANING_FEE));
	cleaningFeesProperty().addListener((ChangeListener<Number>) (observable, oldValue, newValue) -> {
	    cleaningFees.putFloat(cleaningFeeKey, newValue.floatValue());
	});
    }

    public void putRoomNameMapping(final String vendorName, final String ourName)
	    throws ClassNotFoundException, IOException {
	putRoomNameMapping(new ImmutablePair<>(vendorName, ourName));

    }

    public void putRoomNameMapping(final Map.Entry<String, String> pair) throws ClassNotFoundException, IOException {
	final Map<String, String> map = getRoomNameMappings();
	map.put(pair.getKey(), pair.getValue());
	setRoomNameMapping(map);
    }

    public void setRoomNameMapping(final Map<String, String> map) throws IOException {
	cleaningFees.putByteArray(roomNameMapKey, Objects.toBytes(map));
    }

    public int getCleaningPlanLookBehind() {
	return cleaningFees.getInt(SettingsManager.cleaningPlanLookBehindKey, DEFAULT_CLEANINGPLAN_LOOKBEHIND_DAYS);
    }

    public int getUpcomingLookAhead() {
	return cleaningFees.getInt(SettingsManager.upcomingLookAheadKey, DEFAULT_UPCOMING_LOOK_AHEAD_DAYS);
    }

    public File getDataFile() {
	final String fileString = cleaningFees.get(fileKey,
		System.getProperty("user.home") + File.separator + DEFAULT_FILE_NAME);
	return new File(fileString);
    }

    public String getRoomNamePrefix() {
	return cleaningFees.get(roomNamePrefixKey, DEFAULT_ROOM_NAME_PREFIX);
    }

    public void setRoomNamePrefix(final String prefix) {
	cleaningFees.put(roomNamePrefixKey, prefix);
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

    public void setCleaningPlanLookBehind(final int value) {
	cleaningFees.putInt(cleaningPlanLookBehindKey, value);

    }

    public void setUpcomingLookAhead(final int value) {
	cleaningFees.putInt(upcomingLookAheadKey, value);

    }

    public void setDataFile(final File file) {
	cleaningFees.put(fileKey, file.getAbsolutePath());
    }

    public BooleanProperty completePaymentProperty() {
	return this.completePayment;
    }

    public boolean isCompletePayment() {
	return this.completePaymentProperty().get();
    }

    public void setCompletePayment(final boolean completePayment) {
	this.completePaymentProperty().set(completePayment);
    }

    public FloatProperty cleaningFeesProperty() {
	return this.fees;
    }

    public float getCleaningFees() {
	return this.cleaningFeesProperty().get();
    }

    public void setCleaningFees(final float fees) {
	System.err.println("CLeaning fees now " + fees);
	this.cleaningFeesProperty().set(fees);
    }

}
