import java.io.BufferedReader;
import java.io.IOException;

public class Request {
    private Methods method;
    private String path;
    private String version;

    public String getPath() {
        return path;
    }

    public Request(Methods method,String path, String version) {
        this.method = method;
        this.path = path;
        this.version = version;
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

    public static Request pars(BufferedReader in) throws IOException {
        final var requestLine = in.readLine();
        final var parts = requestLine.split(" ");

        if (parts.length != 3) {
            return null;
        }
         return new Request(Methods.valueOf(parts[0]), parts[1], parts[2]);


    }
}
