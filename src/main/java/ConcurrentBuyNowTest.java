import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConcurrentBuyNowTest {
    public static void main(String[] args) throws InterruptedException {
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(1);
        HttpClient client = HttpClient.newHttpClient();

        for (int i = 0; i < threadCount; i++) {
            int threadNum = i;
            executor.submit(() -> {
                try {
                    latch.await();

                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create("http://localhost:8080/orders/buy-now"))
                            .header("Content-Type", "application/json")
                            .header("X-User-Id", "2")
                            .POST(HttpRequest.BodyPublishers.ofString("{\"productId\": 2, \"quantity\": 2}"))
                            .build();

                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    System.out.println("Thread " + threadNum + " status=" + response.statusCode() + " body=" + response.body());
                } catch (Exception e) {
                    System.out.println("Thread " + threadNum + " failed: " + e.getMessage());
                }
            });
        }

        Thread.sleep(500);
        latch.countDown();

        executor.shutdown();
    }
}