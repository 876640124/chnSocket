package socket2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * 使用线程池的方式实现BIO,但是连接的数量有限
 */
public class SocketBIOThreadPool {

    //server socket listen property:
    private static final int RECEIVE_BUFFER = 10;
    private static final int SO_TIMEOUT = 0;
    private static final boolean REUSE_ADDR = false;
    private static final int BACK_LOG = 2;
    //client socket listen property on server endpoint:
    private static final boolean CLI_KEEPALIVE = false;
    private static final boolean CLI_OOB = false;
    private static final int CLI_REC_BUF = 20;
    private static final boolean CLI_REUSE_ADDR = false;
    private static final int CLI_SEND_BUF = 20;
    private static final boolean CLI_LINGER = true;
    private static final int CLI_LINGER_N = 0;
    private static final int CLI_TIMEOUT = 0;
    private static final boolean CLI_NO_DELAY = false;


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
           /* client.setKeepAlive(true);
            // 设置发送缓冲区大小
            client.setSendBufferSize(10);
            // 设置不优化tcp在buffer中有数据直接发送不等待缓存区存满
            client.setTcpNoDelay(true);*/
            threadPool.execute(()->{
                try (InputStream in = client.getInputStream()) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    client.getOutputStream();
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
        }
    }
}
