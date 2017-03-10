package com.github.drbookings;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.model.DataModel;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DrBookingsApplication extends Application {

    private final static Logger logger = LoggerFactory.getLogger(DrBookingsApplication.class);

    public static void main(final String[] args) {
	launch(args);
    }

    static void restoreState() {
	final File file = new File(System.getProperty("user.home"), "booking-data.xml");
	if (logger.isInfoEnabled()) {
	    logger.info("Loading state from " + file.getAbsolutePath());
	}
	try {
	    final JAXBContext jc = JAXBContext.newInstance(DataModel.class);
	    final Unmarshaller jaxbMarshaller = jc.createUnmarshaller();
	    DataModel.getInstance().setAll(((DataModel) jaxbMarshaller.unmarshal(file)).getData());
	} catch (final Exception e1) {
	    logger.error(e1.getLocalizedMessage(), e1);
	}
    }

    void saveState() {
	if (logger.isDebugEnabled()) {
	    logger.debug("Saving state..");
	}
	try {
	    final JAXBContext jc = JAXBContext.newInstance(DataModel.class);
	    final Marshaller jaxbMarshaller = jc.createMarshaller();
	    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	    jaxbMarshaller.marshal(DataModel.getInstance(),
		    new File(System.getProperty("user.home"), "booking-data.xml"));
	} catch (final Exception e1) {
	    logger.error(e1.getLocalizedMessage(), e1);
	}
	if (logger.isDebugEnabled()) {
	    logger.debug("Saving state done");
	}

    }

    @Override
    public void start(final Stage stage) throws Exception {
	stage.setOnCloseRequest(event -> new Thread(() -> saveState()).start());
	final Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainView.fxml"));
	final Scene scene = new Scene(root, 800, 800);
	stage.setTitle("Dr.Bookings");
	stage.setScene(scene);
	stage.show();
	restoreState();

    }

}
