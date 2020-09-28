package socket;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @description:
 * @author: ChenHaoNan
 * @create: 2020-09-28
 **/
public class WriteWorkThread implements Runnable {
    // 表明客户端处于那种状态： 1.主页 2.选择聊天对象 3.聊天室
    public int status;
    SocketThreadManage stm;
    Socket client;
    String uuid;
    // 存放多条聊天
    Map<String, Map<String, Socket>> map = new HashMap<>();
    LinkedBlockingQueue<String> message = new LinkedBlockingQueue<>();

    public WriteWorkThread(SocketThreadManage stm, Socket client,String uuid) {
        this.uuid = uuid;
        status = ConstrantStatus.MENU;
        this.stm = stm;
        this.client = client;
    }

    @Override
    public void run() {
        try (OutputStream outputStream = client.getOutputStream()) {
            //将需要发送的数据发送
            String take = message.take();
            outputStream.write(take.getBytes());
            outputStream.flush();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
