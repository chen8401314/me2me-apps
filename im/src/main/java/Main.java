import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.IOException;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/3/2.
 */
public class Main {

    public static void main(String[] args) throws IOException, XMPPException, SmackException {
        XMPPTCPConnectionConfiguration.Builder config = XMPPTCPConnectionConfiguration.builder();
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        config.setUsernameAndPassword("abc1", "abc111111");
        config.setServiceName("iz23a2nwds6z");
        config.setHost("121.40.225.216");
        config.setPort(5222);
        config.setDebuggerEnabled(false);
        XMPPTCPConnection conn = new XMPPTCPConnection(config.build());
        conn.connect().login();
        Chat chat = ChatManager.getInstanceFor(conn).createChat("abc0@iz23a2nwds6z");
        // chat.sendMessage("Hello");
        Message message = new Message();
        message.setSubject("tips");
        message.setFrom("abc1000");
        message.setBody("hello");
        message.setType(Message.Type.chat);
        chat.sendMessage(message);

    }
}
