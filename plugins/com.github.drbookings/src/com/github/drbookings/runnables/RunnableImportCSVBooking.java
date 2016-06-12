package com.github.drbookings.runnables;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;

import net.sf.kerner.utils.rcp.RunnableProto;

public class RunnableImportCSVBooking extends RunnableProto<Object> {

	private final File file;

	public RunnableImportCSVBooking(final File file) {
		this.file = file;
	}

	@Override
	protected void onSuccess(final Object result) {

		super.onSuccess(result);

	}

	@Override
	protected Object process(final IProgressMonitor monitor) {

		try {

			return null;

		} finally {
			monitor.done();
		}

	}

}
