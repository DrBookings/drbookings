package com.github.drbookings.ui.selection;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.model.data.Booking;
import com.github.drbookings.ui.BookingEntry;
import com.github.drbookings.ui.beans.RoomBean;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class BookingSelectionManager {

	private static class InstanceHolder {
		private static final BookingSelectionManager instance = new BookingSelectionManager();
	}

	private static final Logger logger = LoggerFactory.getLogger(BookingSelectionManager.class);

	public static BookingSelectionManager getInstance() {
		return InstanceHolder.instance;
	}

	private static ObservableList<BookingEntry> transform(final Collection<? extends RoomBean> rooms) {
		return rooms.stream().flatMap(r -> r.getFilteredBookingEntries().stream())
				.collect(Collectors.toCollection(() -> FXCollections.observableArrayList(BookingEntry.extractor())));
	}

	private final ListProperty<Booking> bookings = new SimpleListProperty<>(
			FXCollections.observableArrayList(Booking.extractor()));

	private final ListProperty<BookingEntry> selection = new SimpleListProperty<>(
			FXCollections.observableArrayList(BookingEntry.extractor()));

	private BookingSelectionManager() {
		// internally update bookings (of type Booking)
		bookings.bind(Bindings.createObjectBinding(collectBookings(), selectionProperty()));
		// register selection listener to update selection
		RoomBeanSelectionManager.getInstance().selectionProperty().addListener(new ListChangeListener<RoomBean>() {

			@Override
			public void onChanged(final javafx.collections.ListChangeListener.Change<? extends RoomBean> c) {
				while (c.next()) {
					selectionProperty().removeAll(transform(c.getRemoved()));
					selectionProperty().addAll(transform(c.getAddedSubList()));
					if (logger.isDebugEnabled()) {
						logger.debug("Selection updated: " + selectionProperty().size());
					}
				}
			}
		});
		// initially set selection
		selectionProperty().setAll(transform(RoomBeanSelectionManager.getInstance().selectionProperty()));
	}

	public final ListProperty<Booking> bookingsProperty() {
		return this.bookings;
	}

	private Callable<ObservableList<Booking>> collectBookings() {
		return () -> {
			final Set<Booking> set = selectionProperty().stream().map(e -> e.getElement()).collect(Collectors.toSet());
			final ObservableList<Booking> list = FXCollections.observableArrayList(Booking.extractor());
			list.addAll(set);
			return list;
		};
	}

	public final List<Booking> getBookings() {
		return this.bookingsProperty().get();
	}

	public final List<BookingEntry> getSelection() {
		return this.selectionProperty().get();
	}

	public final ListProperty<BookingEntry> selectionProperty() {
		return this.selection;
	}

}
