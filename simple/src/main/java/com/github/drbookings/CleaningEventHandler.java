package com.github.drbookings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.model.bean.DateBean;
import com.github.drbookings.model.bean.RoomBean;

import javafx.event.EventHandler;
import javafx.scene.control.TableColumn.CellEditEvent;

public class CleaningEventHandler implements EventHandler<CellEditEvent<DateBean, String>> {

    private static final Logger logger = LoggerFactory.getLogger(CleaningEventHandler.class);
    private final String id;

    public CleaningEventHandler(final String id) {
	super();
	this.id = id;
    }

    @Override
    public void handle(final CellEditEvent<DateBean, String> event) {
	final DateBean bean = event.getTableView().getItems().get(event.getTablePosition().getRow());

	final RoomBean room = bean.getRoom(id);

	if (logger.isDebugEnabled()) {
	    logger.debug(bean + " and " + room);
	}
    }

}
