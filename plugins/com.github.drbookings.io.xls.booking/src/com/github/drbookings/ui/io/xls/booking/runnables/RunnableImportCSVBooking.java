package com.github.drbookings.ui.io.xls.booking.runnables;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.core.api.BookingManager;
import com.github.drbookings.core.datamodel.impl.BookingBean;

import net.sf.kerner.utils.exception.ExceptionFileFormat;
import net.sf.kerner.utils.rcp.RunnableProto;

public class RunnableImportCSVBooking extends RunnableProto<List<BookingBean>> {

	private final static Logger logger = LoggerFactory.getLogger(RunnableImportCSVBooking.class);

	public static final String NA_VALUE_BOOKING_NAME = "n/a";

	public final static Integer NA_VALUE_BOOKING_NUMBER = -1;

	private static final LocalDate NA_VALUE_DATE = LocalDate.parse("2000-01-01", FileFormatBookingXLS.DATE_FORMATTER);

	private final File file;

	private final BookingManager manager;

	public RunnableImportCSVBooking(final BookingManager manager, final File file) {
		this.manager = manager;
		this.file = file;
	}

	private Integer getBookingNumber(final Cell c) {
		if (c != null) {
			if (c.getCellType() == Cell.CELL_TYPE_NUMERIC) {
				return (int) c.getNumericCellValue();
			} else {
				final String cellContent = c.getStringCellValue();
				try {
					return Integer.parseInt(cellContent);
				} catch (final Exception e) {
					if (logger.isWarnEnabled()) {
						logger.warn("Failed to parse booking number from " + cellContent);
					}
				}
			}
		}
		return NA_VALUE_BOOKING_NUMBER;
	}

	private int getColumnIndexBookingNumber(final HSSFRow row) throws ExceptionFileFormat {
		return getColumnIndexForIdentifier(row, FileFormatBookingXLS.IDENTIFIER_BOOKING_NUMBER);
	}

	private int getColumnIndexCheckIn(final HSSFRow row) throws ExceptionFileFormat {
		return getColumnIndexForIdentifier(row, FileFormatBookingXLS.IDENTIFIER_CHECK_IN);
	}

	private int getColumnIndexCheckOut(final HSSFRow row) throws ExceptionFileFormat {
		return getColumnIndexForIdentifier(row, FileFormatBookingXLS.IDENTIFIER_CHECK_OUT);
	}

	private int getColumnIndexClientName(final HSSFRow row) throws ExceptionFileFormat {
		return getColumnIndexForIdentifier(row, FileFormatBookingXLS.IDENTIFIER_BOOKING_CLIENT_NAMES);
	}

	private int getColumnIndexForIdentifier(final HSSFRow row, final String identifierBookingNumber)
			throws ExceptionFileFormat {
		final Iterator<Cell> cellIterator = row.cellIterator();
		while (cellIterator.hasNext()) {
			final Cell nextCell = cellIterator.next();
			final String cellContent = nextCell.getStringCellValue();
			if (cellContent.equals(identifierBookingNumber)) {
				return nextCell.getColumnIndex();
			}
		}
		throw new ExceptionFileFormat("Failed to parse " + identifierBookingNumber);
	}

	private LocalDate getDate(final Cell cell) {
		if (cell != null) {
			final String result = cell.getStringCellValue();
			if (result != null && !StringUtils.isEmpty(result)) {
				return LocalDate.parse(result);
			}
		}
		return NA_VALUE_DATE;
	}

	private String getString(final Cell cell) {
		if (cell != null) {
			final String result = cell.getStringCellValue();
			if (result != null && !StringUtils.isEmpty(result)) {
				return result;
			}
		}
		return NA_VALUE_BOOKING_NAME;
	}

	@Override
	protected void onSuccess(final List<BookingBean> result) {

		super.onSuccess(result);
		manager.addAllBookings(result);

	}

	@Override
	protected List<BookingBean> process(final IProgressMonitor monitor) throws Exception {

		try {
			if (logger.isInfoEnabled()) {
				logger.info("Reading " + file);
			}

			final FileInputStream stream = new FileInputStream(file);

			final HSSFWorkbook workbook = new HSSFWorkbook(stream);
			final HSSFSheet sheet = workbook.getSheetAt(0);
			if (logger.isInfoEnabled()) {
				logger.info("Processing sheet " + sheet.getSheetName());
			}
			final int indexBookingNumber = getColumnIndexBookingNumber(sheet.getRow(0));
			final int indexClientName = getColumnIndexClientName(sheet.getRow(0));
			final int indexBookingCheckIn = getColumnIndexCheckIn(sheet.getRow(0));
			final int indexBookingCheckOut = getColumnIndexCheckOut(sheet.getRow(0));
			final List<Integer> bookingNumbers = new ArrayList<>();
			final List<String> guestNames = new ArrayList<>();
			final List<LocalDate> bookingCheckIn = new ArrayList<>();
			final List<LocalDate> bookingCheckOut = new ArrayList<>();
			for (final Row r : sheet) {
				// skip first row
				if (r.getRowNum() == 0) {
					continue;
				}
				bookingNumbers.add(getBookingNumber(r.getCell(indexBookingNumber)));
				guestNames.add(getString(r.getCell(indexClientName)));
				bookingCheckIn.add(getDate(r.getCell(indexBookingCheckIn)));
				bookingCheckOut.add(getDate(r.getCell(indexBookingCheckOut)));
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

			final List<BookingBean> bookings = new ArrayList<>();
			for (int i = 0; i < bookingNumbers.size(); i++) {
				final int number = bookingNumbers.get(i);
				final LocalDate checkIn = bookingCheckIn.get(i);
				final LocalDate checkOut = bookingCheckOut.get(i);
				final String names = guestNames.get(i);
				bookings.add(new BookingBean(Integer.toString(number), checkIn, checkOut, names));
			}
			return bookings;

		} finally {
			monitor.done();
		}

	}

}
