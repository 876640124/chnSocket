package chat;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;
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
                    // char[] data = new char[1024];
                    while (true) {
                        /*int num = reader.read(data);
                        if (num>0){
                            System.out.println(new String(data, 0, num));
                        }else{
                            client.close();
                            break;
                        }*/

                        // 阻塞获取数据
                        String dataline = reader.readLine();
                        if (null != dataline) {
                            System.out.println(dataline);
                        } else {
                            client.close();
                            break;
                        }
                    }
                    System.out.println("客户端连接断开");
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            // 发送线程
            threadPool.execute(() -> {
                Thread.currentThread().setName("发送线程");
                OutputStream out = null;
                try {
                    /*client.setSendBufferSize(20);
                    client.setTcpNoDelay(true);*/
                    out = client.getOutputStream();

                    InputStream in = System.in;
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
                    while (true) {
                        String line = reader.readLine();
                        if (line != null) {
                            /*byte[] bb = line.getBytes();
                            for (byte b : bb) {
                                out.write(b);
                            }
                            out.flush();*/
                            writer.write(line);
                            writer.write("\n");
                            writer.flush();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
