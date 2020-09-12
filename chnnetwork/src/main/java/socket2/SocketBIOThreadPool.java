package socket2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * 使用线程池的方式实现BIO
 */
public class SocketBIOThreadPool {
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(9090);
        System.out.println("Port 9090 has been listening");

        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(5, 10, 1, TimeUnit.MINUTES, new LinkedBlockingDeque<>(10), new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                System.out.println("连接已满");
            }
        });

        while (true){
            Socket client = server.accept();
            threadPool.execute(()->{
                try (InputStream in = client.getInputStream()) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    while (true) {
                        // 阻塞获取数据
                        String data = reader.readLine();
                        if (null != data) {
                            System.out.println(data);
                        } else {
                            client.close();
                            break;
                        }
                    }
                    System.out.println("连接断开");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

    }
}
