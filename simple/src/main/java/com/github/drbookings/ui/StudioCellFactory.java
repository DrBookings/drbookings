package com.github.drbookings.ui;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.model.bean.DateBean;
import com.github.drbookings.model.bean.RoomBean;
import com.github.drbookings.ui.controller.CellContentController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class StudioCellFactory implements Callback<TableColumn<DateBean, DateBean>, TableCell<DateBean, DateBean>> {

    private static final Logger logger = LoggerFactory.getLogger(StudioCellFactory.class);

    private static Node buildCellContent(final RoomBean roomBean) {
	try {
	    final FXMLLoader loader = new FXMLLoader(StudioCellFactory.class.getResource("/fxml/CellContentView.fxml"));
	    final Parent cellContent = loader.load();
	    final CellContentController c = loader.getController();
	    if (c.setData(roomBean)) {
		return cellContent;
	    }
	} catch (final IOException e) {
	    logger.error(e.getLocalizedMessage(), e);
	}
	return null;
    }

    private final String id;

    public StudioCellFactory(final String id) {
	this.id = id;
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
		    setGraphic(buildCellContent(item.getRoom(id)));
		}
	    }
	};
    }

}
