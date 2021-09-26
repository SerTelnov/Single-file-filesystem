package org.test_project.jetbrains.commands;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class CommandParams {

    private final Map<String, String> properties;

    public CommandParams(Map<String, String> properties) {
        this.properties = Collections.unmodifiableMap(properties);
    }

    public static CommandParams emptyParams() {
        return new CommandParams(Collections.emptyMap());
    }

    public static CommandParams of(String key, String value) {
        return new CommandParams(Map.of(key, value));
    }

    public String getParam(String paramName) {
        return properties.get(paramName);
    }

    public Optional<String> getParamSafe(String paramName) {
        if (properties.containsKey(paramName)) {
            return Optional.of(properties.get(paramName));
        }
        return Optional.empty();
    }

    public boolean containsParam(String paramName) {
        return properties.containsKey(paramName);
    }

    public boolean hasAnyParam() {
        return !properties.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CommandParams that = (CommandParams) o;
        return Objects.equals(properties, that.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(properties);
    }
}
