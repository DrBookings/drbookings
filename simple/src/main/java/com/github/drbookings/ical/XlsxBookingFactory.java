package com.github.drbookings.ical;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.model.ser.BookingBeanSer;

public class XlsxBookingFactory implements BookingFactory {

    private final static Logger logger = LoggerFactory.getLogger(XlsxBookingFactory.class);

    private final File file;

    public XlsxBookingFactory(final File file) {
	super();
	this.file = file;
    }

    @Override
    public Collection<BookingBeanSer> build() throws Exception {
	final List<BookingBeanSer> bookings = new ArrayList<>();
	FileInputStream stream = null;
	Workbook workbook = null;
	try {
	    stream = new FileInputStream(file);
	    workbook = new HSSFWorkbook(stream);
	    final Sheet sheet = workbook.getSheetAt(0);
	    if (logger.isInfoEnabled()) {
		logger.info("Processing sheet " + sheet.getSheetName());
	    }
	    final int indexBookingNumber = FileFormatBookingXLS.getColumnIndexBookingNumber(sheet.getRow(0));
	    final int indexClientName = FileFormatBookingXLS.getColumnIndexClientName(sheet.getRow(0));
	    final int indexBookingCheckIn = FileFormatBookingXLS.getColumnIndexCheckIn(sheet.getRow(0));
	    final int indexBookingCheckOut = FileFormatBookingXLS.getColumnIndexCheckOut(sheet.getRow(0));
	    final int indexStatus = FileFormatBookingXLS.getColumnIndexStatus(sheet.getRow(0));
	    final List<Integer> bookingNumbers = new ArrayList<>();
	    final List<String> guestNames = new ArrayList<>();
	    final List<String> stati = new ArrayList<>();
	    final List<LocalDate> bookingCheckIn = new ArrayList<>();
	    final List<LocalDate> bookingCheckOut = new ArrayList<>();
	    for (final Row r : sheet) {
		// skip first row
		if (r.getRowNum() == 0) {
		    continue;
		}
		bookingNumbers.add(FileFormatBookingXLS.getBookingNumber(r.getCell(indexBookingNumber)));
		guestNames.add(FileFormatBookingXLS.getString(r.getCell(indexClientName)));
		bookingCheckIn.add(FileFormatBookingXLS.getDate(r.getCell(indexBookingCheckIn)));
		bookingCheckOut.add(FileFormatBookingXLS.getDate(r.getCell(indexBookingCheckOut)));
		stati.add(FileFormatBookingXLS.getString(r.getCell(indexStatus)));
	    }
	    if (logger.isDebugEnabled()) {
		logger.debug("Booking numbers: " + bookingNumbers);
		logger.debug("Guest names: " + guestNames);
		logger.debug("Check-in dates: " + bookingCheckIn);
		logger.debug("Check-out dates: " + bookingCheckOut);
	    }
	    if (logger.isInfoEnabled()) {
		logger.info("Building bookings.. ");
	    }

	    for (int i = 0; i < bookingNumbers.size(); i++) {
		final int number = bookingNumbers.get(i);
		final LocalDate checkIn = bookingCheckIn.get(i);
		final LocalDate checkOut = bookingCheckOut.get(i);
		final String names = guestNames.get(i);
		final String status = stati.get(i);
		if (status.equals("ok")) {
		    final BookingBeanSer bb = new BookingBeanSer();
		    bb.checkInDate = checkIn;
		    bb.checkOutDate = checkOut;
		    bb.guestName = names;
		    bb.externalId = Integer.toString(number);
		    bookings.add(bb);
		} else {
		    if (logger.isDebugEnabled()) {
			logger.debug("Skipping status " + status);
		    }
		}
	    }
	} catch (final Exception e) {
	    if (logger.isErrorEnabled()) {
		logger.error(e.getLocalizedMessage(), e);
	    }
	} finally {
	    if (workbook != null) {
		IOUtils.closeQuietly(workbook);
	    }
	    if (stream != null) {
		IOUtils.closeQuietly(stream);
	    }
	}
	return bookings;
    }

}
