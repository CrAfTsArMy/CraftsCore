package de.craftsblock.craftscore.web;

import okhttp3.Request;

public interface Callback {

    /**
     * Represents the callback wich is called after the {@link WebFetcher#send(Request, Callback)} method completed the fetch
     *
     * @param result The Result as {@link Result} that the fetch returned
     */
    void callback(Result result);

}
