package com.github.drbookings.ui.selection;

import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.model.data.DateBeans;
import com.github.drbookings.ui.beans.DateBean;
import com.github.drbookings.ui.beans.RoomBean;
import com.google.common.collect.Range;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class DateBeanSelectionManager {

	private static class InstanceHolder {
		private static final DateBeanSelectionManager instance = new DateBeanSelectionManager();
	}

	private static final Logger logger = LoggerFactory.getLogger(DateBeanSelectionManager.class);

	public static DateBeanSelectionManager getInstance() {
		return InstanceHolder.instance;
	}

	private static ObservableList<DateBean> transform(final Collection<? extends RoomBean> rooms) {
		return rooms.stream().map(r -> r.getDateBean())
				.collect(Collectors.toCollection(() -> FXCollections.observableArrayList(DateBean.extractor())));
	}

	private final ListProperty<DateBean> selection = new SimpleListProperty<>(
			FXCollections.observableArrayList(DateBean.extractor()));

	private final ObjectProperty<Range<LocalDate>> selectedDateRange = new SimpleObjectProperty<>();

	private DateBeanSelectionManager() {
		selectedDateRangeProperty().bind(Bindings.createObjectBinding(calculateDateRange(), selectionProperty()));
		RoomBeanSelectionManager.getInstance().selectionProperty().addListener(new ListChangeListener<RoomBean>() {

			@Override
			public void onChanged(final javafx.collections.ListChangeListener.Change<? extends RoomBean> c) {
				while (c.next()) {
					// selectionProperty().removeAll(transform(c.getRemoved()));
					// selectionProperty().addAll(transform(c.getAddedSubList()));

				}
				selectionProperty().setAll(new LinkedHashSet<>(transform(c.getList())));
				if (logger.isDebugEnabled()) {
					logger.debug("Selection updated: " + selectionProperty().size());
				}
			}
		});
	}

	private Callable<Range<LocalDate>> calculateDateRange() {
		return () -> DateBeans.getDateRange(getSelection());
	}

	public final List<DateBean> getSelection() {
		return this.selectionProperty().get();
	}

	public final ListProperty<DateBean> selectionProperty() {
		return this.selection;
	}

	public final ObjectProperty<Range<LocalDate>> selectedDateRangeProperty() {
		return this.selectedDateRange;
	}

	public final Range<LocalDate> getSelectedDateRange() {
		return this.selectedDateRangeProperty().get();
	}

}
