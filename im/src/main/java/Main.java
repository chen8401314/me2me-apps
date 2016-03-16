import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.packet.GroupChatInvitation;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.FormField;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/3/2.
 */
public class Main {

    public static void main(String[] args) throws IOException, XMPPException, SmackException {
        XMPPTCPConnectionConfiguration.Builder config = XMPPTCPConnectionConfiguration.builder();
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        config.setUsernameAndPassword("abc7", "abc111111");
        config.setServiceName("iz23a2nwds6z");
        config.setHost("121.40.225.216");
        config.setPort(5222);
        config.setDebuggerEnabled(false);
        XMPPTCPConnection conn = new XMPPTCPConnection(config.build());
        conn.connect().login();
//        Chat chat = ChatManager.getInstanceFor(conn).createChat("abc0@iz23a2nwds6z");
//        // chat.sendMessage("Hello");
//        Message message = new Message();
//        message.setSubject("tips");
//        message.setFrom("abc1000");
//        message.setBody("hello");
//        message.setType(Message.Type.chat);
//        chat.sendMessage(message);
//        Set<String> account = AccountManager.getInstance(conn).getAccountAttributes();
//        for (String a : account) {
//            System.out.println(a);
//        }
//
//        // 获取好友列表
//        Set<RosterEntry> entries = Roster.getInstanceFor(conn).getEntries();
//        for (RosterEntry e : entries) {
//            System.out.println(e.getUser());
//        }
//
//        // 添加好友接口
//        Roster.getInstanceFor(conn).createEntry("abc4@iz23a2nwds6z", "abc4", new String[]{"teacher"});
//        // 创建群聊天
//        MultiUserChat muc = MultiUserChatManager.getInstanceFor(conn).getMultiUserChat("约炮官方群" + "@conference.iz23a2nwds6z");
//        boolean isCreated = muc.createOrJoin("abc0");
//        if (isCreated) {
//            Form form = muc.getConfigurationForm();
//            Form submitForm = form.createAnswerForm();
//            List<FormField> list = form.getFields();
//            for (FormField formField : list) {
//                if (FormField.Type.hidden != formField.getType() && formField.getVariable() != null) {
//                    submitForm.setDefaultAnswer(formField.getVariable());
//                }
//            }
//            List owners = new ArrayList();
//            owners.add(conn.getUser());// 用户JID
//            submitForm.setAnswer("muc#roomconfig_roomowners", owners);
//            // 设置聊天室是持久聊天室，即将要被保存下来
//            submitForm.setAnswer("muc#roomconfig_persistentroom", true);
//            // 房间仅对成员开放
//            submitForm.setAnswer("muc#roomconfig_membersonly", false);
//            // 允许占有者邀请其他人
//            submitForm.setAnswer("muc#roomconfig_allowinvites", true);
////            if(password != null && password.length() != 0) {
////                // 进入是否需要密码
////                submitForm.setAnswer("muc#roomconfig_passwordprotectedroom",  true);
////                // 设置进入密码
////                submitForm.setAnswer("muc#roomconfig_roomsecret", password);
////            }
//            submitForm.setAnswer("muc#roomconfig_enablelogging", true);
//            // 仅允许注册的昵称登录
//            submitForm.setAnswer("x-muc#roomconfig_reservednick", true);
//            // 允许使用者修改昵称
//            submitForm.setAnswer("x-muc#roomconfig_canchangenick", false);
//            // 允许用户注册房间
//            submitForm.setAnswer("x-muc#roomconfig_registration", false);
//            // 发送已完成的表单（有默认值）到服务器来配置聊天室
//            muc.sendConfigurationForm(submitForm);
//        }
//        MultiUserChat muc = joinChatRoom(conn,"约炮官方群","abc7","");
//        muc.addMessageListener(new MessageListener() {
//            public void processMessage(Message message) {
//                System.out.println(message.getBody());
//            }
//        });
//
        MultiUserChat muc = joinChatRoom(conn,"约炮官方群","abc31","");
        muc.create("abc32");
        while(true);
    }

    public static MultiUserChat joinChatRoom(XMPPTCPConnection connection,String roomName,  String nickName, String password) {
        if(!connection.isConnected()) {
            throw new NullPointerException("服务器连接失败，请先连接服务器");
        }
        try {
            // 使用XMPPConnection创建一个MultiUserChat窗口
            MultiUserChat muc = MultiUserChatManager.getInstanceFor(connection).
                    getMultiUserChat(roomName + "@conference." + connection.getServiceName());
            // 聊天室服务将会决定要接受的历史记录数量
            DiscussionHistory history = new DiscussionHistory();
            history.setMaxChars(0);
            // history.setSince(new Date());
            // 用户加入聊天室
            muc.join(nickName, password);
            return muc;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
