import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

public class Server {
    private int port;
    private final List validPaths = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html",
            "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");

    public Server(int port) {
        this.port = port;
    }

    public void start() {

        try (final var serverSocket = new ServerSocket(this.port)) {
            try (
                    final var socket = serverSocket.accept();
                    final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    final var out = new BufferedOutputStream(socket.getOutputStream());
            ) {

                final var requestLine = in.readLine();
                final var parts = requestLine.split(" ");
                if (parts.length != 3) {
                }

                final var path = parts[1];
                if (!validPaths.contains(path)) {
                    out.write((
                            Errors.returnError(404, "", 0)
                    ).getBytes());
                    out.flush();
                }

                final var filePath = Path.of(".", "public", path);
                final var mimeType = Files.probeContentType(filePath);

                if (path.equals("/classic.html")) {
                    final var template = Files.readString(filePath);
                    final var content = template.replace(
                            "{time}",
                            LocalDateTime.now().toString()
                    ).getBytes();
                    out.write((
                            Errors.returnError(200, mimeType, content.length)
                    ).getBytes());
                    out.write(content);
                    out.flush();
                }

                final var length = Files.size(filePath);
                out.write((
                        Errors.returnError(200, mimeType, length)
                ).getBytes());
                Files.copy(filePath, out);
                out.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}




