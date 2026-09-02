package com.deveyk.jobmatch.testsupport;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public abstract class LogTrackerConfiguration {

    private static final String ROOT_PACKAGE = "com.deveyk.jobmatch";

    protected final LogTracker logTracker = new LogTracker();

    @BeforeEach
    void setUpLogTracker() {

        this.logTracker.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        this.logTracker.start();

        final Logger logger = (Logger) LoggerFactory.getLogger(ROOT_PACKAGE);
        logger.setLevel(Level.TRACE);
        logger.addAppender(this.logTracker);
    }

    @AfterEach
    void tearDownLogTracker() {

        final Logger logger = (Logger) LoggerFactory.getLogger(ROOT_PACKAGE);
        logger.detachAppender(this.logTracker);

        this.logTracker.stop();
    }

    protected static class LogTracker extends ListAppender<ILoggingEvent> {

        public Optional<String> findMessage(final Level level, final String messageFragment) {

            if (CollectionUtils.isEmpty(this.list)) {
                return Optional.empty();
            }

            return this.list.stream()
                    .filter(log -> log.getLevel().equals(level))
                    .map(ILoggingEvent::getFormattedMessage)
                    .filter(formattedMessage -> formattedMessage.contains(messageFragment))
                    .findFirst();
        }

    }

}
