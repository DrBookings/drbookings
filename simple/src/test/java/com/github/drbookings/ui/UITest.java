/*
 * DrBookings
 *
 * Copyright (C) 2016 - 2018 Alexander Kerner
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 */

package com.github.drbookings.ui;

import com.github.drbookings.DrBookingsApplication;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.testfx.framework.junit.ApplicationTest;

import java.io.File;
import java.nio.file.Files;

public class UITest extends ApplicationTest {

    public static final File USER_HOME = new File("src/test/resources/tmp/user-home");

    @BeforeClass
    public static void setUpClass() throws Exception {

	Files.createDirectories(USER_HOME.toPath());
	System.setProperty("user.home", USER_HOME.getAbsolutePath());
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
	FileUtils.deleteDirectory(USER_HOME);
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    protected DrBookingsApplication instance;

    @Override
    public void start(final Stage stage) throws Exception {
	instance = new DrBookingsApplication();
	instance.start(stage);
    }



}
