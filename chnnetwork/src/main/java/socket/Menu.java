package socket;

/**
 * @description:
 * @author: ChenHaoNan
 * @create: 2020-09-28
 **/
public class Menu {
    public static String menu(){
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("----------------\r\n");
        stringBuffer.append("1.私聊\r\n");
        stringBuffer.append("2.群聊\r\n");
        stringBuffer.append("3.退出\r\n");
        return stringBuffer.toString();
    }
}
