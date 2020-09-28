package socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: 获取client发送的数据并给予响应
 * @author: ChenHaoNan
 * @create: 2020-09-28
 **/
public class readWorkerThread implements Runnable {
    // 表明客户端处于那种状态： 1.主页 2.选择聊天对象 3.聊天室
    public int status;
    SocketThreadManage stm;
    Socket client;
    String uuid;
    // 存放多条聊天
    Map<String,Map<String,Socket>> map = new HashMap<>();


    public readWorkerThread(SocketThreadManage stm, Socket client,String uuid) {
        this.uuid = uuid;
        status = ConstrantStatus.MENU;
        this.stm = stm;
        this.client = client;
    }

    // 根据client所处的状态返回与之相对应的回复
    public String LogicHandler(StringBuilder str) {
        String result = null;
        switch (status) {
            case ConstrantStatus.MENU:
                // 直接返回主页菜单
                result = Menu.menu();
                break;
            case ConstrantStatus.CREATE:
                // 判断数据是新增聊天ID还是添加完毕
                break;
            case ConstrantStatus.CHAT:
                // 对话
                break;
            default:
                break;
        }
        return result;
    }

    @Override
    public void run() {
        try (InputStream inputStream = client.getInputStream();
             OutputStream outputStream = client.getOutputStream()) {
            outputStream.write(Menu.menu().getBytes("UTF-8"));
            outputStream.flush();
            while (true) {
                // 没有定义包头包尾没有处理粘包
                byte[] bytes = new byte[1024];
                int length;
                StringBuilder str = new StringBuilder();
                while ((length = inputStream.read(bytes)) != -1) {
                    str.append(new String(bytes, "UTF-8"));
                    System.out.println(str);
                    //根据状态返回菜单或发送数据至聊天的客户端
                    String s = LogicHandler(str);
                }
                if (length == -1) {
                    System.out.println("断开连接");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
