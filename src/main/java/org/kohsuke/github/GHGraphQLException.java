package org.kohsuke.github;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A GraphQL request that GitHub answered with errors.
 */
public class GHGraphQLException extends GHIOException {

    /** The Constant serialVersionUID. */
    static final long serialVersionUID = 1L;

    /** The messages of the GraphQL errors. */
    private final List<String> errorMessages;

    GHGraphQLException(List<String> errorMessages) {
        super("GraphQL request failed by:" + errorMessages);
        this.errorMessages = Collections.unmodifiableList(new ArrayList<>(errorMessages));
    }

    /**
     * Gets the messages of the GraphQL errors.
     *
     * @return the error messages
     */
    public List<String> getErrorMessages() {
        return errorMessages;
    }
}
