package socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @description: 监听端口接收连接并分发至各个工作线程
 * @author: ChenHaoNan
 * @create: 2020-09-28
 **/
public class SocketThreadManage {

    public Map<String, Socket> clients = new ConcurrentHashMap<>();
    private ServerSocket serverSocket;
    private ThreadPoolExecutor readWorkGroup;
    private Map<String, WriteWorkThread> writeWorkGroup = new ConcurrentHashMap<>();

    public SocketThreadManage(int corePoolSize, int MaxPoolSize) {
        readWorkGroup = new ThreadPoolExecutor(corePoolSize, MaxPoolSize, 1000, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1), (r, executor) ->
                System.out.println("聊天服务器高负载"));
    }

    public void bind(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void accept() {
        // LOOP
        while (true) {
            try {
                Socket client = serverSocket.accept();
                String uuid = UUID.randomUUID().toString();
                clients.put(uuid, client);
                acceptHandler(client, uuid);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void acceptHandler(Socket client, String uuid) {
        readWorkGroup.execute(new readWorkerThread(this, client,uuid));
        // 写线程按照UUID存储 方便发送数据时做通知
        WriteWorkThread writeWorkThread = new WriteWorkThread(this, client,uuid);
        writeWorkGroup.put(uuid, writeWorkThread);
        new Thread(writeWorkThread).start();
    }
}
