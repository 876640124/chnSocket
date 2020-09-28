package socket;

/**
 * @description:
 * @author: ChenHaoNan
 * @create: 2020-09-28
 **/
public class MySocketServer {
    public static void main(String[] args) {
        SocketThreadManage socketThreadManage = new SocketThreadManage(5, 10);
        socketThreadManage.bind(9090);
        socketThreadManage.accept();

    }
}
