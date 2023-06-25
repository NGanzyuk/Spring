import java.io.BufferedOutputStream;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        final var server = new Server(9999, 64);

        server.addHandler(Methods.GET, "/messages", new Handler() {
            public void handle(Request request, BufferedOutputStream responseStream) throws IOException {
                String qwer = "QWER GET";
                responseStream.write(
                        Errors.returnError(200, "text/plain", qwer.length()).getBytes()
                );
                responseStream.write(qwer.getBytes());
            }
        });
        server.addHandler(Methods.POST, "/messages", new Handler() {
            public void handle(Request request, BufferedOutputStream responseStream) throws IOException {
                String qwer = "QWER POSt";
                responseStream.write(
                        Errors.returnError(200, "text/plain", qwer.length()).getBytes()
                );
                responseStream.write(qwer.getBytes());
            }
        });

        server.start();
    }
}

