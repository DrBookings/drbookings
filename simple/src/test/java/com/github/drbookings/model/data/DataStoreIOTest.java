package com.github.drbookings.model.data;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.drbookings.TestUtils;
import com.github.drbookings.model.data.ser.CleaningExpenseSer;
import com.github.drbookings.model.data.ser.CleaningExpenseSerBuilder;
import com.github.drbookings.ser.DataStoreCore;

public class DataStoreIOTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
	io = new DataStoreIO();
    }

    @After
    public void tearDown() throws Exception {
	io = null;
    }

    private static File testFile = new File("src" + File.separator + "test" + File.separator + "resources"
	    + File.separator + DataStoreIOTest.class.getSimpleName() + ".xml");

    private DataStoreIO io;

    @Test
    public void test01() throws Exception {

	final List<CleaningExpense> expenses = CleaningExpense.build(TestUtils.getCleanings2018Mai());
	final List<CleaningExpenseSer> expensesSer = CleaningExpenseSerBuilder.build(expenses);
	final DataStoreCore ds = new DataStoreCore();
	ds.setCleaningExpenses(expensesSer);
	io.writeToFile(ds, testFile);

	final DataStoreCore store = io.readFromFile(testFile);
	assertThat(store, is(ds));

    }

}
