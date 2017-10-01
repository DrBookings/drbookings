package com.github.drbookings.io;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class Backup {

    private static final Logger logger = LoggerFactory.getLogger(Backup.class);

    public static void make(final File file) {
        if (file.exists() && file.length() != 0) {
            try {
                final File backupFile = new File(file.getParentFile(), file.getName() + ".bak");
                FileUtils.copyFile(file, backupFile);
                if (logger.isInfoEnabled()) {
                    logger.info("Backup created as " + backupFile);
                }
            } catch (final IOException e) {
                if (logger.isErrorEnabled()) {
                    logger.error(e.getLocalizedMessage(), e);
                }
            }
        }
    }
}
