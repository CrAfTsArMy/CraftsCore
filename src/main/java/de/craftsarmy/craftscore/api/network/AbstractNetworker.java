package de.craftsarmy.craftscore.api.network;

import de.craftsarmy.craftscore.utils.Validator;
import okhttp3.*;

import java.io.IOException;

public abstract class AbstractNetworker {

    private final String endpoint;
    private final OkHttpClient client;

    public AbstractNetworker(String endpoint) {
        this.endpoint = endpoint;
        client = new OkHttpClient();
    }

    public abstract void shutdown() throws IOException;
    public abstract ResponseBody send(Request request) throws IOException;
    public abstract Request get(String path);
    public abstract Request post(String path, String data) throws IOException;
    public abstract Request put(String path, String data) throws IOException;
    public abstract Request delete(String path);
    public abstract Request delete(String path, String data) throws IOException;
    public abstract Request patch(String path, String data) throws IOException;
    public final OkHttpClient client() {
        return client;
    }

    public final String prepareURL(String path) {
        return this.endpoint.trim() + (this.endpoint.trim().endsWith("/") && !path.trim().startsWith("/") ? "" : "/") + path.trim();
    }

    public final RequestBody prepareData(String data) throws IOException {
        if (Validator.isJsonValid(data))
            return RequestBody.create(data, MediaType.parse("application/json"));
        throw new IOException("You could only post valid json data.");
    }

}
