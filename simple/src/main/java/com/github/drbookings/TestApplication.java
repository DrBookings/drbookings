package com.github.drbookings;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class TestApplication extends Application {

    public static void main(final String[] args) {
	launch(args);
    }

    private final TableView table = new TableView();

    @Override
    public void start(final Stage stage) throws Exception {
	final Scene scene = new Scene(new Group(), 800, 800);
	stage.setTitle("Test");
	final TableColumn c1 = new TableColumn("C1");
	final TableColumn c2 = new TableColumn("C2");
	table.getColumns().addAll(c1, c2);
	((Group) scene.getRoot()).getChildren().add(table);
	stage.setScene(scene);
	stage.show();

    }

}
