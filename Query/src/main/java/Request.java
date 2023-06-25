import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

public class Request {
    private Methods method;
    private String path;
    private String version;
    private List<String> headers;
    private byte[]body;
    private List<NameValuePair> queryParams;

    private final int limit = 4096;
    public Request(BufferedInputStream in) throws IOException, URISyntaxException {
        in.mark(limit);
        var buffer = new byte[limit];
        var input = in.read(buffer);
        var requestLineDelimiter = new byte[]{'\r', '\n'};;
        var requestLineEnd = search(buffer, requestLineDelimiter, 0, input);
        var requestLine = new String(Arrays.copyOf(buffer, requestLineEnd)).split(" ");
        this.method = Methods.valueOf(requestLine[0]);
        var pathAndQuery = requestLine[1];
        var path = pathAndQuery;
        if (pathAndQuery.contains("?")) {
            path = pathAndQuery.split("/?")[0];
        }
        this.path = path;
        this.version = requestLine[2];

        final var headersDelimiter = new byte[]{'\r', '\n', '\r', '\n'};
        final var headersStart = requestLineEnd + requestLineDelimiter.length;
        final var headersEnd = search(buffer, headersDelimiter, headersStart, input);

        in.reset();
        in.skip(headersStart);

        final var headersBytes = in.readNBytes(headersEnd - headersStart);
        this.headers = Arrays.asList(new String(headersBytes).split("\r\n"));

        this.body = null;
        if (!method.equals(Methods.valueOf("GET"))) {
            in.skip(headersDelimiter.length);
            final var contentLength = extractHeader(headers, "Content-Length");
            if (contentLength.isPresent()) {
                var length = Integer.parseInt(contentLength.get());
                this.body = in.readNBytes(length);
            }
        }
        this.queryParams = new URIBuilder(pathAndQuery).getQueryParams();
    }
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Methods getMethod() {
        return method;
    }

    public void setMethod(Methods method) {
        this.method = method;
    }
    private static int search(byte[] array, byte[] target, int start, int max) {
        outer:
        for (int i = start; i < max - target.length + 1; i++) {
            for (int j = 0; j < target.length; j++) {
                if (array[i + j] != target[j]) {
                    continue outer;
                }
            }
            return i;
        }
        return -1;
    }
    public List<NameValuePair> getQueryParam(String name) {
        return queryParams
                .stream()
                .filter(o -> Objects.equals(o.getName(), name))
                .collect(Collectors.toList());
    }

    public List<NameValuePair> getQueryParams() {
        return queryParams;
    }

    private static Optional<String> extractHeader(List<String> headers, String header) {
        return headers.stream()
                .filter(o -> o.startsWith(header))
                .map(o -> o.substring(o.indexOf(" ")))
                .map(String::trim)
                .findFirst();
    }
}
