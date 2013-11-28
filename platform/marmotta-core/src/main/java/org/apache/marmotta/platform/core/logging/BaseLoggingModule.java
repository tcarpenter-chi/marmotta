package org.apache.marmotta.platform.core.logging;

import ch.qos.logback.classic.Level;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.apache.marmotta.platform.core.api.config.ConfigurationService;
import org.apache.marmotta.platform.core.api.logging.LoggingModule;
import org.apache.marmotta.platform.core.api.logging.LoggingService;
import org.apache.marmotta.platform.core.model.logging.LoggingOutput;

import javax.inject.Inject;
import java.util.List;

/**
 * Add file description here!
 *
 * @author Sebastian Schaffert (sschaffert@apache.org)
 */
public abstract class BaseLoggingModule implements LoggingModule {

    @Inject
    protected ConfigurationService configurationService;

    @Inject
    protected LoggingService loggingService;

    /**
     * Update the currently active (logback) level used by this logging module. This method directly updates the
     * configuration file.
     *
     * @param level
     */
    @Override
    public void setCurrentLevel(Level level) {
        configurationService.setConfiguration(String.format("logging.module.%s.level", getId()), level.toString());
    }

    /**
     * Return the currently configured (logback) level used by this logging module. This field is read from the
     * configuration file and defaults to getDefaultLevel()
     *
     * @return
     */
    @Override
    public Level getCurrentLevel() {
        return Level.toLevel(configurationService.getStringConfiguration(String.format("logging.module.%s.level", getId())), getDefaultLevel());
    }


    /**
     * Return the identifiers of all logging outputs configured for this module
     *
     * @return
     */
    @Override
    public List<String> getLoggingOutputIds() {
        return configurationService.getListConfiguration(String.format("logging.module.%s.appenders", getId()), Lists.newArrayList("console","main"));
    }

    /**
     * Return the logging outputs configured for this module (resolved using the LoggingService).
     *
     * @return
     */
    @Override
    public List<LoggingOutput> getLoggingOutputs() {
        return Lists.transform(getLoggingOutputIds(), new Function<String, LoggingOutput>() {
            @Override
            public LoggingOutput apply(String input) {
                return loggingService.getOutputConfiguration(input);
            }
        });
    }

    /**
     * Set the identifiers of all logging outputs for this module
     *
     * @param ids
     */
    @Override
    public void setLoggingOutputIds(List<String> ids) {
        configurationService.setListConfiguration(String.format("logging.module.%s.appenders", getId()), ids);
    }

    /**
     * Set the logging outputs configured for this module (internally calls setLoggingOutputIds).
     *
     * @param outputs
     */
    @Override
    public void setLoggingOutputs(List<LoggingOutput> outputs) {
        setLoggingOutputIds(Lists.transform(outputs, new Function<LoggingOutput, String>() {
            @Override
            public String apply(LoggingOutput input) {
                return input.getId();
            }
        }));
    }
}
