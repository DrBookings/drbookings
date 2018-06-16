/*
 * DrBookings
 *
 * Copyright (C) 2016 - 2018 Alexander Kerner
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 */

package com.github.drbookings.excel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.kerner.utils.exception.ExceptionFileFormat;

public class FileFormatBookingXLS {

    private final static Logger logger = LoggerFactory.getLogger(FileFormatBookingXLS.class);

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static final String IDENTIFIER_BOOKING_CLIENT_NAMES = "Guest name(s)";

    public static final String IDENTIFIER_BOOKING_NUMBER = "Book number";

    public static final String IDENTIFIER_CHECK_IN = "Check-in";

    public static final String IDENTIFIER_CHECK_OUT = "Check-out";

    public static final String STATUS = "Status";

    public static final String NA_STRING = "n/a";

    public final static int NA_INT = -1;

    public static int getBookingNumber(final Cell c) {
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
	return NA_INT;
    }

    public static int getColumnIndexBookingNumber(final Row row) throws ExceptionFileFormat {
	return getColumnIndexForIdentifier(row, FileFormatBookingXLS.IDENTIFIER_BOOKING_NUMBER);
    }

    public static int getColumnIndexCheckIn(final Row row) throws ExceptionFileFormat {
	return getColumnIndexForIdentifier(row, FileFormatBookingXLS.IDENTIFIER_CHECK_IN);
    }

    public static int getColumnIndexCheckOut(final Row row) throws ExceptionFileFormat {
	return getColumnIndexForIdentifier(row, FileFormatBookingXLS.IDENTIFIER_CHECK_OUT);
    }

    public static int getColumnIndexClientName(final Row row) throws ExceptionFileFormat {
	return getColumnIndexForIdentifier(row, FileFormatBookingXLS.IDENTIFIER_BOOKING_CLIENT_NAMES);
    }

    public static int getColumnIndexForIdentifier(final Row row, final String identifierBookingNumber)
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

    public static int getColumnIndexStatus(final Row row) throws ExceptionFileFormat {
	return getColumnIndexForIdentifier(row, FileFormatBookingXLS.STATUS);
    }

    public static LocalDate getDate(final Cell cell) {
	if (cell != null) {
	    final String result = cell.getStringCellValue();
	    if ((result != null) && !StringUtils.isEmpty(result)) {
		return LocalDate.parse(result);
	    }
	}
	return null;
    }

    public static String getString(final Cell cell) {
	if (cell != null) {
	    final String result = cell.getStringCellValue();
	    if (!StringUtils.isEmpty(result)) {
		return result;
	    }
	}
	return null;
    }

}
