package com.github.drbookings;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class JavaFXTest extends Application {

    @Override
    public void start(final Stage primaryStage) {

	final StackPane root = new StackPane();
	final TextFlow tf = new TextFlow();
	final Text t0 = new Text("First part");
	final Text t1 = new Text(", second");
	final TextField t2 = new TextField(" and very, very, very long third");
	t2.setEditable(false);
	t2.setBackground(Background.EMPTY);
	t2.setFocusTraversable(false);

	tf.getChildren().addAll(t0, t1, t2);
	root.getChildren().add(tf);

	final Scene scene = new Scene(root, 300, 250);

	primaryStage.setTitle("Hello World!");
	primaryStage.setScene(scene);
	primaryStage.show();
    }

    public static void main(final String[] args) {
	launch(args);
    }
}