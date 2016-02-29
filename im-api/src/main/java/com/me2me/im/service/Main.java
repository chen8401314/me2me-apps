package com.me2me.im.service;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;

import java.io.IOException;
import java.util.Set;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 赵朋扬
 * Date: 2016/2/29.
 */
public class Main {
//    XMPPTCPConnectionConfiguration.Builder config = XMPPTCPConnectionConfiguration.builder();
//    config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
//    config.setUsernameAndPassword("admin", "fish1314");
//    config.setServiceName("iz23a2nwds6z");
//    config.setHost("121.40.225.216");
//    config.setPort(5222);
//    config.setDebuggerEnabled(false);
//    XMPPTCPConnection conn = new XMPPTCPConnection(config.build());
//    conn.connect().login();
//    System.out.println("Esta conectat? "+ conn.isConnected());
//    // 注册用户
//    AccountManager accountManager = AccountManager.getInstance(conn);
//    for(int i = 0;i<1000000;i++) {
//        accountManager.createAccount("abc"+i,"abc111111");
//    }
    public static void main(String[] args) throws IOException, XMPPException, SmackException {
//        for(int i = 0;i<50000;i++){
//            login("abc"+i,"abc111111");
//        }
        // login("abc0","abc111111");
       Roster.getInstanceFor(getConnection("abc","abc111111")).createEntry("abc10","abc10",new String[]{"friends"});
    }


    private static XMPPTCPConnection getConnection(String username,String password) throws IOException, XMPPException, SmackException {
        XMPPTCPConnectionConfiguration.Builder config = XMPPTCPConnectionConfiguration.builder();
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        config.setUsernameAndPassword(username, password);
        config.setServiceName("iz23a2nwds6z");
        config.setHost("121.40.225.216");
        config.setPort(5222);
        config.setDebuggerEnabled(false);
        XMPPTCPConnection conn = new XMPPTCPConnection(config.build());
        conn.connect().login();
        return conn;
    }


    private static void login(String username,String password) throws IOException, XMPPException, SmackException {
        XMPPTCPConnectionConfiguration.Builder config = XMPPTCPConnectionConfiguration.builder();
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        config.setUsernameAndPassword(username, password);
        config.setServiceName("iz23a2nwds6z");
        config.setHost("121.40.225.216");
        config.setPort(5222);
        config.setDebuggerEnabled(false);
        XMPPTCPConnection conn = new XMPPTCPConnection(config.build());
        conn.connect().login();
        System.out.println("Esta conectat? "+ conn.isConnected());
        // 注册用户
    }
}
