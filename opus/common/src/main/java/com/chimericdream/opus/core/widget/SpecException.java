package com.chimericdream.opus.core.widget;

/** A widget body that is well-formed YAML but does not describe a valid widget. */
public final class SpecException extends Exception {
    private static final long serialVersionUID = 1L;

    public SpecException(String message) {
        super(message);
    }
}
