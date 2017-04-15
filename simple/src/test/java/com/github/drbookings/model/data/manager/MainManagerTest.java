package com.github.drbookings.model.data.manager;

import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.drbookings.model.data.Cleaning;
import com.github.drbookings.model.data.Room;
import com.github.drbookings.ui.controller.CleaningEntry;

public class MainManagerTest {

    private MainManager mm;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testAddCleaning01() {
	mm = new MainManager();
	final CleaningEntry ce = new CleaningEntry(LocalDate.now(), new Room("testRoom"), new Cleaning("testCleaning"));
	mm.addCleaning(LocalDate.now(), "testCleaning", "testRoom");
	assertTrue(mm.getCleaningEntries().contains(ce));
    }

}
