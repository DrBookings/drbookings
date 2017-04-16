package com.github.drbookings.model.settings;

import java.io.File;
import java.util.prefs.Preferences;

public class SettingsManager {

    private static class InstanceHolder {

	private static final SettingsManager instance = new SettingsManager();
    }

    private static final String cleaningPlanLookBehindKey = "cleaningPlanLookBehindDays";

    public static final int DEFAULT_CLEANINGPLAN_LOOKBEHIND_DAYS = 7;

    private static final String fileKey = "data";

    public static final String DEFAULT_FILE_NAME = "booking-data.xml";

    public static SettingsManager getInstance() {
	return InstanceHolder.instance;
    }

    private final Preferences prefs = Preferences.userNodeForPackage(getClass());

    private SettingsManager() {

    }

    public void setDataFile(final File file) {
	prefs.put(fileKey, file.getAbsolutePath());
    }

    public File getDataFile() {
	final String fileString = prefs.get(fileKey,
		System.getProperty("user.home") + File.separator + DEFAULT_FILE_NAME);
	return new File(fileString);
    }

    public int getCleaningPlanLookBehind() {
	return prefs.getInt(SettingsManager.cleaningPlanLookBehindKey, DEFAULT_CLEANINGPLAN_LOOKBEHIND_DAYS);
    }

    public void setCleaningPlanLookBehind(final int value) {
	prefs.putInt(cleaningPlanLookBehindKey, value);

    }

}
