package com.github.drbookings;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class ExampleController implements Initializable {

    @FXML
    private VBox box;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
	final TextFlow tf = new TextFlow();
	final Text t0 = new Text("First part");
	final Text t1 = new Text(", second");
	final Text t2 = new Text(" and third");
	tf.getChildren().addAll(t0, t1, t2);
	this.box.getChildren().add(tf);
    }
}
