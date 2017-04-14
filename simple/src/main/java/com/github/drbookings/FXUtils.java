package com.github.drbookings;

import java.util.concurrent.CountDownLatch;

import javax.swing.SwingUtilities;

import javafx.embed.swing.JFXPanel;

public class FXUtils {
    public static void setupJavaFX() throws RuntimeException {
	final CountDownLatch latch = new CountDownLatch(1);
	SwingUtilities.invokeLater(() -> {
	    new JFXPanel(); // initializes JavaFX environment
	    latch.countDown();
	});
	try {
	    latch.await(); // wait for SwingUI thread to return (FX environment
			   // ready)
	} catch (final InterruptedException e) {
	    throw new RuntimeException(e);
	}
    }

}
