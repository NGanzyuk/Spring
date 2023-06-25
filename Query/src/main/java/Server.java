import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server {
    private ServerSocket serverSocket;

    private final ExecutorService pool;

    private final ConcurrentHashMap<Methods, ConcurrentHashMap<String, Handler>> handlers
            = new ConcurrentHashMap<>();

    public Server(int port, int poolSize) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.pool = Executors.newFixedThreadPool(poolSize);
    }

    public void start() {
        while (true) {
            pool.execute(() -> {
                try (final var socket = serverSocket.accept();
                     final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     final var out = new BufferedOutputStream(socket.getOutputStream())
                     ;) {
                    final var request = Request.pars(in);
                    if (request == null) {
                        out.write((
                                Errors.returnError(400, "", 0)
                        ).getBytes());
                        out.flush();
                        return;
                    }

                    var methodMap = handlers.get(request.getMethod());
                    if (methodMap == null) {
                        out.write((
                                Errors.returnError(404, "", 0)
                        ).getBytes());
                        out.flush();
                        return;
                    }

                    var handler = methodMap.get(request.getPath());
                    if (handler == null) {
                        out.write((
                                Errors.returnError(404, "", 0)
                        ).getBytes());
                        out.flush();
                        return;
                    }

                    handler.handle(request, out);


                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void addHandler(Methods method, String path, Handler handler) {
        var methodMap = handlers.get(method);
        if (methodMap == null) {
            methodMap = new ConcurrentHashMap<>();
            handlers.put(method, new ConcurrentHashMap<>());
        }

        if (!methodMap.containsKey(path)) {
            methodMap.put(path, handler);
        }
    }
}



