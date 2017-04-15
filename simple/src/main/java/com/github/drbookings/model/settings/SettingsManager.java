package com.github.drbookings.model.settings;

import java.util.prefs.Preferences;

public class SettingsManager {

    private static class InstanceHolder {

	private static final SettingsManager instance = new SettingsManager();
    }

    private static final String CLEANINGPLAN_LOOKBEHIND_KEY = "cleaningPlanLookBehindDays";

    public static final int DEFAULT_CLEANINGPLAN_LOOKBEHIND_DAYS = 7;

    public static SettingsManager getInstance() {
	return InstanceHolder.instance;
    }

    private final Preferences prefs = Preferences.userNodeForPackage(getClass());

    private SettingsManager() {

    }

    public int getCleaningPlanLookBehind() {
	return prefs.getInt(SettingsManager.CLEANINGPLAN_LOOKBEHIND_KEY, DEFAULT_CLEANINGPLAN_LOOKBEHIND_DAYS);
    }

    public void setCleaningPlanLookBehind(final int value) {
	prefs.putInt(CLEANINGPLAN_LOOKBEHIND_KEY, value);

    }

}
