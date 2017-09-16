package com.github.drbookings;

import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.junit.*;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.testfx.framework.junit.ApplicationTest;

import java.io.File;
import java.nio.file.Files;

import static org.hamcrest.Matchers.not;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.hasText;


@Category(UITests.class)
public class DrBookingsApplicationUITest extends ApplicationTest {

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

    @Override
    public void start(final Stage stage) throws Exception {
        new DrBookingsApplication().start(stage);
    }

    @Test
    public void test01() throws Exception {
        // wait for reading to complete
        Thread.sleep(2000);
        verifyThat("#status-label", not(hasText("*Exception*")));
        Thread.sleep(4000);

    }
}