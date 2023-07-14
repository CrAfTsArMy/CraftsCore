package de.craftsblock.craftscore.web.fetcher;

import okhttp3.Request;

/**
 * @author CraftsBlock
 * @since  3.5.4-SNAPSHOT
 * @version 1.0
 * @see Result
 * @see WebFetcher
 */
public interface Callback {

    /**
     * Represents the callback wich is called after the {@link WebFetcher#send(Request, Callback)} method completed the fetch
     *
     * @param result The Result as {@link Result} that the fetch returned
     */
    void callback(Result result);

}
