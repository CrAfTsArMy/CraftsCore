package de.craftsblock.craftscore.utils;

import okhttp3.*;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class provides a simple way to fetch web APIs
 *
 * @author CraftsBlock
 * @version 3.5.4-SNAPSHOT
 */
public final class WebFetcher {

    private final String endpoint;
    private final OkHttpClient client;

    /**
     * @param endpoint Represents the endpoint that will be fetched
     */
    public WebFetcher(String endpoint) {
        this.endpoint = endpoint;
        client = new OkHttpClient();
    }

    /**
     * Flushes the Cache of the OkHttpClient
     *
     * @throws IOException Thrown if the {@link OkHttpClient} fails
     */
    public void flushCache() throws IOException {
        assert client.cache() != null;
        client.cache().flush();
    }

    /**
     * Send the request produced by {@link Builder}
     *
     * @param request  The Request produced by {@link Builder}
     * @param callback The {@link Callback} wich should be called after the fetch is done
     * @throws IOException Thrown if the {@link OkHttpClient} fails
     */
    public void send(Request request, Callback callback) throws IOException {
        int code;
        String result;
        try (Response response = client.newCall(request).execute()) {
            try (ResponseBody body = response.body()) {
                assert body != null;
                code = response.code();
                result = body.string();
            }
        } catch (Exception e) {
            callback.callback(500, e.getMessage());
            throw e;
        }
        callback.callback(code, result);
    }

    /**
     * The Builder for building {@link Request.Builder}
     */
    public static class Builder {

        private final WebFetcher networker;
        private final ConcurrentHashMap<String, String> headers = new ConcurrentHashMap<>();
        private Method method;
        private RequestBody body;
        private String path;

        /**
         * @param networker
         */
        public Builder(WebFetcher networker) {
            this.networker = networker;
        }

        /**
         * Uses {@link Method} to set the Method of the request
         *
         * @param method The {@link Method} the request should perform
         * @return {@link Builder}
         */
        public Builder method(Method method) {
            this.method = method;
            return this;
        }

        /**
         * Uses {@link RequestBody} to set the body of the request
         *
         * @param body The body to send along with the request
         * @return {@link Builder}
         */
        public Builder data(RequestBody body) {
            this.body = body;
            return this;
        }

        /**
         * Set the path of the Request. It will be attached to the {@link WebFetcher#endpoint} of the Request.
         * If this is not set, nothing will be attached to the end of {@link WebFetcher#endpoint}.
         *
         * @param path Optionally sets the path of the request
         * @return {@link Builder}
         */
        public Builder path(String path) {
            this.path = path;
            return this;
        }

        /**
         * Adds a custom Header to the Request
         *
         * @param name  The name of the Header to add
         * @param value The value of the Header to add
         * @return {@link Builder}
         */
        public Builder addHeader(String name, String value) {
            headers.put(name, value);
            return this;
        }

        /**
         * Adds multiple custom Headers to the Request
         *
         * @param headers A Map of Headers that should be added to the Request
         * @return {@link Builder}
         */
        public Builder addHeaders(Map<String, String> headers) {
            this.headers.putAll(headers);
            return this;
        }

        /**
         * Removes custom Headers added by {@link Builder#addHeader(String, String)} or {@link Builder#addHeaders(Map)}
         *
         * @param name The name of the Header to remove
         * @return {@link Builder}
         */
        public Builder removeHeader(String name) {
            headers.remove(name);
            return this;
        }

        /**
         * Clears the list of custom Headers
         *
         * @return {@link Builder}
         */
        public Builder clearHeaders() {
            headers.clear();
            return this;
        }

        /**
         * Building the Request with {@link Builder#method}, {@link Builder#body} & if set {@link Builder#path}
         *
         * @return {@link Request}
         */
        public Request build() {
            // Loading the request builder from the method
            Request.Builder builder = method.builder(body);
            String endpoint = networker.endpoint;

            // Loading the path into the builder
            if (path != null) {
                String separator = "";
                if (!endpoint.trim().endsWith("/") && !path.trim().startsWith("/"))
                    separator = "/";
                builder.url(endpoint.trim() + separator + path.trim());
            } else builder.url(endpoint.trim());

            // Appending all custom Headers
            for(Map.Entry<String, String> header : headers.entrySet())
                builder.addHeader(header.getKey(), header.getValue());

            return builder.build();
        }

    }

    /**
     * The Method the {@link Builder} requires in {@link Builder#method(Method)}
     */
    public enum Method {

        /**
         * All HTTP Methods that are currently supported
         */
        GET, POST, PUT, PATCH, DELETE, HEAD;

        /**
         * Get a {@link Request.Builder} from your Method without setting a {@link RequestBody}
         *
         * @return {@link Request.Builder}
         */
        public Request.Builder builder() {
            return builder(null);
        }

        /**
         * Get a {@link Request.Builder} from your Method with optional set {@link RequestBody}
         *
         * @param body Optional sets the body for the request if not needed it can be set to null
         * @return {@link Request.Builder}
         */
        public Request.Builder builder(@Nullable RequestBody body) {
            Request.Builder builder = new Request.Builder();
            switch (this) {
                case GET:
                    return builder.get();
                case POST:
                    if (body == null) throw new NullPointerException("\"body\" may not be null!");
                    else return builder.post(body);
                case PUT:
                    if (body == null) throw new NullPointerException("\"body\" may not be null!");
                    else return builder.put(body);
                case PATCH:
                    if (body == null) throw new NullPointerException("\"body\" may not be null!");
                    else return builder.patch(body);
                case DELETE:
                    if (body == null) return builder.delete();
                    else return builder.delete(body);
                case HEAD:
                    return builder.head();
            }
            return builder;
        }

    }

    public interface Callback {

        /**
         * Represents the callback wich is called after the {@link WebFetcher#send(Request, Callback)} method completed the fetch
         *
         * @param code   The HTTP code that was returned by the fetch
         * @param result The Result body as {@link String} that the fetch returned
         */
        void callback(int code, String result);

    }

    /**
     * Return the current instance of the {@link OkHttpClient} used by this class
     *
     * @return {@link OkHttpClient}
     */
    public final OkHttpClient client() {
        return client;
    }

    /**
     * Transform the provided data to a {@link RequestBody}
     *
     * @param data The data to transform
     * @return {@link RequestBody}
     * @throws IOException Throws this Exception if the data is not a valid json String
     */
    public final RequestBody prepareData(String data) throws IOException {
        if (Validator.isJsonValid(data)) return RequestBody.create(data, MediaType.parse("application/json"));
        throw new IOException("You could only post valid json data.");
    }

    /**
     * Creates a new {@link FormBody.Builder} object
     *
     * @return {@link FormBody.Builder}
     */
    public final FormBody.Builder create() {
        return new FormBody.Builder();
    }

}
