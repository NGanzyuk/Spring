import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {
    public static void main(String[] args) {
        Callable<Integer> task = () -> {
            var server = new Server(9999);
            server.start();
            return 0;
        };
        ExecutorService threadPool = Executors.newFixedThreadPool(64);
        for (int i = 0; i < 64; i++) {
            Future result = threadPool.submit(task);
        }
        threadPool.shutdown();
    }
}