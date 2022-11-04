package de.craftsblock.craftscore.buildin.network;

import de.craftsblock.craftscore.api.network.AbstractNetworker;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.util.Objects;

public class Networker extends AbstractNetworker {

    public Networker(String endpoint) {
        super(endpoint);
    }

    @Override
    public void shutdown() throws IOException {
        Objects.requireNonNull(client().cache()).close();
    }

    @Override
    public ResponseBody send(Request request) throws IOException {
        return client().newCall(request).execute().body();
    }

    @Override
    public Request get(String path) {
        return new Request.Builder()
                .url(prepareURL(path))
                .get()
                .build();
    }

    @Override
    public Request post(String path, String data) throws IOException {
        return new Request.Builder()
                .url(prepareURL(path))
                .post(prepareData(data))
                .build();
    }

    @Override
    public Request post(String path, FormBody formBody) throws IOException {
        return new Request.Builder()
                .url(prepareURL(path))
                .post(formBody)
                .build();
    }

    @Override
    public Request put(String path, String data) throws IOException {
        return new Request.Builder()
                .url(prepareURL(path))
                .put(prepareData(data))
                .build();
    }

    @Override
    public Request put(String path, FormBody formBody) throws IOException {
        return new Request.Builder()
                .url(prepareURL(path))
                .put(formBody)
                .build();
    }

    @Override
    public Request delete(String path) {
        return new Request.Builder()
                .url(prepareURL(path))
                .delete()
                .build();
    }

    @Override
    public Request delete(String path, String data) throws IOException {
        return new Request.Builder()
                .url(prepareURL(path))
                .delete(prepareData(data))
                .build();
    }

    @Override
    public Request delete(String path, FormBody formBody) throws IOException {
        return new Request.Builder()
                .url(prepareURL(path))
                .delete(formBody)
                .build();
    }

    @Override
    public Request patch(String path, String data) throws IOException {
        return new Request.Builder()
                .url(prepareURL(path))
                .patch(prepareData(data))
                .build();
    }

    @Override
    public Request patch(String path, FormBody formBody) throws IOException {
        return new Request.Builder()
                .url(prepareURL(path))
                .patch(formBody)
                .build();
    }
}
