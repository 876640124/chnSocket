package chat;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 使用线程池的方式实现BIO
 */
public class MyChatServer {

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(9090);
        System.out.println("Port 9090 has been listening");
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(5, 10, 1, TimeUnit.MINUTES, new LinkedBlockingDeque<>(10), new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                System.out.println("连接已满");
            }
        });

        while (true) {
            Socket client = server.accept();
            // 接收线程
            threadPool.execute(() -> {
                Thread.currentThread().setName("接收线程");
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
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            // 发送线程
            threadPool.execute(() -> {
                Thread.currentThread().setName("发送线程");
                OutputStream outputStream = null;
                try {
                    outputStream = client.getOutputStream();
                    while (true) {
                        Scanner sc = new Scanner(System.in);
                        String str = sc.nextLine();
                        outputStream.write(str.getBytes("UTF-8"));
                        outputStream.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
