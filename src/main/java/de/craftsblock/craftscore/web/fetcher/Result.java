package de.craftsblock.craftscore.web.fetcher;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.util.Objects;

public class Result {

    private int code;

    private String body;
    private long bodyLength;
    private String mediaType;

    private Headers headers;
    private String protocol;
    private String message;

    private boolean success;
    private boolean redirected;

    /**
     * Converts an {@link Response} of {@link Call#execute()} to {@link Result}
     *
     * @param response The {@link Response} that should be converted
     * @throws Exception Thrown if something fails in the conversion process
     */
    public Result(Response response) throws Exception {
        if (response == null)
            return;
        try (ResponseBody b = response.body()) {
            assert b != null;
            body = b.string();
            bodyLength = b.contentLength();
            mediaType = Objects.requireNonNull(b.contentType()).type();
        }
        headers = response.headers();
        protocol = response.protocol().toString();
        code = response.code();
        success = response.isSuccessful();
        redirected = response.isRedirect();
        message = response.message();
        response.close();

    }

    /**
     * Returns the HTTP response code
     *
     * @return {@link Integer}
     */
    public int getCode() {
        return code;
    }

    /**
     * Returns the Body as a String
     *
     * @return {@link String}
     */
    public String body() {
        return body;
    }

    /**
     * Returns the length of the Body
     *
     * @return {@link Long}
     */
    public long bodyLength() {
        return bodyLength;
    }

    /**
     * Returns the {@link okhttp3.MediaType} as a string
     *
     * @return {@link String}
     */
    public String getMediaType() {
        return mediaType;
    }

    /**
     * Returns the header with the specified name
     *
     * @param name The name of the header
     * @return {@link String}
     */
    public String getHeader(String name) {
        return headers.get(name);
    }

    /**
     * Returns the HTTP protocol
     *
     * @return {@link String}
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * Returns the HTTP status message.
     *
     * @return {@link String}
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns whether the fetch was successful or not
     *
     * @return {@link Boolean}
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Returns whether the fetch was forwarded
     *
     * @return {@link Boolean}
     */
    public boolean isRedirected() {
        return redirected;
    }

}
