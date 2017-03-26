package com.github.drbookings.ui;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.model.bean.DateBean;
import com.github.drbookings.model.bean.RoomBean;
import com.github.drbookings.ui.controller.CellContentController;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class StudioCellFactory implements Callback<TableColumn<DateBean, DateBean>, TableCell<DateBean, DateBean>> {

    private static final Logger logger = LoggerFactory.getLogger(StudioCellFactory.class);

    private final String id;

    public StudioCellFactory(final String id) {
	this.id = id;
    }

    private Node buildCellContent(final RoomBean roomBean) {
	try {
	    final FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CellContentView.fxml"));
	    final VBox cellContent = loader.load();
	    final CellContentController c = loader.getController();
	    c.setData(roomBean);
	    return cellContent;
	} catch (final IOException e) {
	    logger.error(e.getLocalizedMessage(), e);

	}
	return null;
    }

    private Node buildCellContentFast(final RoomBean roomBean) {
	final CellContentController c = new CellContentController();
	final VBox parent = new VBox(0);
	c.setCellContainer(parent);

	VBox vbox = new VBox();
	c.setGuestNamesCheckIn(vbox);
	parent.getChildren().add(vbox);

	vbox = new VBox();
	c.setGuestNamesCheckOut(vbox);
	parent.getChildren().add(vbox);

	vbox = new VBox();
	c.setGuestNamesStay(vbox);
	parent.getChildren().add(vbox);

	vbox = new VBox();
	c.setCleaning(vbox);
	parent.getChildren().add(vbox);

	c.setData(roomBean);

	parent.setAlignment(Pos.CENTER);

	return parent;
    }

    @Override
    public TableCell<DateBean, DateBean> call(final TableColumn<DateBean, DateBean> param) {
	return new TableCell<DateBean, DateBean>() {

	    @Override
	    protected void updateItem(final DateBean item, final boolean empty) {

		super.updateItem(item, empty);
		if (empty || item == null) {
		    setText("");
		    setStyle("");
		    setGraphic(null);
		} else {
		    setGraphic(buildCellContentFast(item.getRoom(id).get()));
		    setStyle("-fx-padding: 0 0 0 0;");
		}
	    }
	};
    }

}
