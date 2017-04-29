package com.github.drbookings.model.settings;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.prefs.Preferences;

import org.apache.commons.lang3.tuple.ImmutablePair;

import net.sf.kerner.utils.objects.Objects;

public class SettingsManager {

    private static class InstanceHolder {

	private static final SettingsManager instance = new SettingsManager();
    }

    private static final String cleaningPlanLookBehindKey = "cleaningPlanLookBehindKey";

    private static final String cleaningFeeKey = "cleaningFeeKey";

    private static final String roomNameMapKey = "roomNameMapKey";

    public static final int DEFAULT_CLEANINGPLAN_LOOKBEHIND_DAYS = 7;

    public static final float DEFAULT_CLEANING_FEE = 0;

    private static final String fileKey = "data";

    public static final String DEFAULT_FILE_NAME = "booking-data.xml";

    public static SettingsManager getInstance() {
	return InstanceHolder.instance;
    }

    private final Preferences prefs = Preferences.userNodeForPackage(getClass());

    private SettingsManager() {

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
	prefs.putByteArray(roomNameMapKey, Objects.toBytes(map));
    }

    public int getCleaningPlanLookBehind() {
	return prefs.getInt(SettingsManager.cleaningPlanLookBehindKey, DEFAULT_CLEANINGPLAN_LOOKBEHIND_DAYS);
    }

    public File getDataFile() {
	final String fileString = prefs.get(fileKey,
		System.getProperty("user.home") + File.separator + DEFAULT_FILE_NAME);
	return new File(fileString);
    }

    public Map<String, String> getRoomNameMappings() throws ClassNotFoundException, IOException {
	final byte[] bytes = prefs.getByteArray(roomNameMapKey, null);
	if (bytes == null) {
	    return new LinkedHashMap<>();
	}
	@SuppressWarnings("unchecked")
	final Map<String, String> map = (Map<String, String>) Objects.fromBytes(bytes);
	return map;
    }

    public void setCleaningPlanLookBehind(final int value) {
	prefs.putInt(cleaningPlanLookBehindKey, value);

    }

    public void setCleaningFee(final float value) {
	prefs.putFloat(cleaningFeeKey, value);

    }

    public float getCleaningFee() {
	return prefs.getFloat(cleaningFeeKey, DEFAULT_CLEANING_FEE);

    }

    public void setDataFile(final File file) {
	prefs.put(fileKey, file.getAbsolutePath());
    }

}
