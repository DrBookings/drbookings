package com.github.drbookings;

import java.util.concurrent.CountDownLatch;

import javax.swing.SwingUtilities;

import javafx.embed.swing.JFXPanel;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextAlignment;

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

	public static void makeHeaderWrappable(final TableColumn<?, ?> col) {
		final Label label = new Label(col.getText());
		label.setStyle("-fx-padding: 2px;");
		label.setWrapText(true);
		label.setAlignment(Pos.CENTER);
		label.setTextAlignment(TextAlignment.CENTER);
		final StackPane stack = new StackPane();
		stack.getChildren().add(label);
		stack.prefWidthProperty().bind(col.widthProperty().subtract(5));
		label.prefWidthProperty().bind(stack.prefWidthProperty());
		col.setGraphic(stack);
	}
}
