import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private  ServerSocket serverSocket;
    private final List validPaths = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html",
            "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");
    private final ExecutorService pool;
    public Server(int port, int poolSize) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.pool = Executors.newFixedThreadPool(poolSize);
    }
    public void start(){
        while (true){
            pool.execute( ()-> {
                try (   final var socket = serverSocket.accept();
                        final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        final var out = new BufferedOutputStream(socket.getOutputStream())
                        ;) {
                    // read only request line for simplicity
                    // must be in form GET /path HTTP/1.1
                    final var requestLine = in.readLine();
                    final var parts = requestLine.split(" ");

                    if (parts.length != 3) {
                        // just close socket
                        // continue;
                        socket.close();
                    }

                    final var path = parts[1];
                    if (!validPaths.contains(path)) {
                        out.write((
                                Errors.returnError(404, "", 0)
                        ).getBytes());
                        out.flush();
                        // continue;
                        socket.close();
                    }

                    final var filePath = Path.of(".", "public", path);
                    final var mimeType = Files.probeContentType(filePath);

                    // special case for classic
                    if (path.equals("/classic.html")) {
                        final var template = Files.readString(filePath);
                        final var content = template.replace(
                                "{time}",
                                LocalDateTime.now().toString()).getBytes();
                        out.write((
                                Errors.returnError(200, mimeType, content.length)
                        ).getBytes());
                        out.write(content);
                        out.flush();
                        // continue;
                        socket.close();
                    }

                    final var length = Files.size(filePath);
                    out.write((
                            Errors.returnError(200, mimeType, length)
                    ).getBytes());
                    Files.copy(filePath, out);
                    out.flush();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}






