package com.example.demo;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.config.NamingConvention;
import io.micrometer.core.lang.Nullable;

import java.util.regex.Pattern;

public class CustomPrometheusNamingConvention implements NamingConvention {

    private static final
    private static final Pattern nameChars = Pattern.compile("[^a-zA-Z0-9_:]");
    private static final Pattern tagKeyChars = Pattern.compile("[^a-zA-Z0-9_]");
    private final String timerSuffix;

    public CustomPrometheusNamingConvention() {
        this("");
    }

    public CustomPrometheusNamingConvention(String timerSuffix) {
        this.timerSuffix = timerSuffix;
    }

    /**
     * Names are snake-cased. They contain a base unit suffix when applicable.
     * <p>
     * Names may contain ASCII letters and digits, as well as underscores and colons. They must match the regex
     * [a-zA-Z_:][a-zA-Z0-9_:]*
     */
    @Override
    public String name(String name, Meter.Type type, @Nullable String baseUnit) {
        String conventionName = NamingConvention.snakeCase.name("actuator_"+name, type, baseUnit);

        switch (type) {
            case COUNTER:
            case DISTRIBUTION_SUMMARY:
            case GAUGE:
                if (baseUnit != null && !conventionName.endsWith("_" + baseUnit))
                    conventionName += "_" + baseUnit;
                break;
        }

        switch (type) {
            case COUNTER:
                if (!conventionName.endsWith("_total"))
                    conventionName += "_total";
                break;
            case TIMER:
            case LONG_TASK_TIMER:
                if (conventionName.endsWith(timerSuffix)) {
                    conventionName += "_seconds";
                } else if (!conventionName.endsWith("_seconds"))
                    conventionName += timerSuffix + "_seconds";
                break;
        }

        String sanitized = nameChars.matcher(conventionName).replaceAll("_");
        if (!Character.isLetter(sanitized.charAt(0))) {
            sanitized = "m_" + sanitized;
        }
        return sanitized;
    }

    /**
     * Label names may contain ASCII letters, numbers, as well as underscores. They must match the regex
     * [a-zA-Z_][a-zA-Z0-9_]*. Label names beginning with __ are reserved for internal use.
     */
    @Override
    public String tagKey(String key) {
        String conventionKey = NamingConvention.snakeCase.tagKey(key);

        String sanitized = tagKeyChars.matcher(conventionKey).replaceAll("_");
        if (!Character.isLetter(sanitized.charAt(0))) {
            sanitized = "m_" + sanitized;
        }
        return sanitized;
    }
}

