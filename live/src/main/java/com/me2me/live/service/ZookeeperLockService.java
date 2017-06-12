package com.me2me.live.service;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
/**
 * zookeeper 锁服务。使用竞争创建节点算法。 
 * @author zhangjiwei
 * @date Jun 9, 2017
 */
//@Component
public class ZookeeperLockService  {
	@Value("#{app.dubboRegistry}")
	private String zkAddr;
	private final static String LOCK_DIR="/live/locks";
	private byte[] lock= new byte[0];
	
	class MyWatcher implements Watcher{
		@Override
		public void process(WatchedEvent event) {
			if(event.getType().equals(EventType.NodeDeleted)){
				synchronized(lock){
					lock.notifyAll();	// 大家都来抢锁啦……
				}
			}
		}
	}
	private ZooKeeper zk;
	/**
	 * 使用指定独占锁执行代码
	 * @author zhangjiwei
	 * @date Jun 9, 2017
	 * @param lockName
	 * @param run
	 */
	public void execute(String lockName,Runnable run) throws Exception{
		String nodePath= LOCK_DIR+"/"+lockName;
		boolean lock = getLock(nodePath);
		if(lock){
			try{
				run.run();
			}catch(Exception e){
				throw e;
			}finally{
				freeLock(nodePath);
			}
		}
	}
	private boolean getLock(String nodePath) {
		long begin= System.currentTimeMillis();
		while(true){
			try {
				String path = zk.create(nodePath, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL);
				return true;
			} catch (Exception e) {
				synchronized(lock){	// 没获取到锁的线程等待。
					try {
						lock.wait();
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
				if(System.currentTimeMillis()-begin>30*1000){		// 30秒后还没得到锁，抛弃。因为此时客户端早就等崩溃了。
					//throw new RuntimeException("未获取到锁");
					return false;
				}
			}
		}
	}
	private void freeLock(String lockName) {
		int retry=3;
		while(--retry>0)
			try {
				zk.delete(lockName, -1);
				return ;
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (KeeperException e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}
	@PostConstruct
	public void init(){
		try {
			zkAddr= zkAddr.replace("zookeeper://", "");
			zk = new ZooKeeper(zkAddr, 10*1000,new MyWatcher());
			Stat stat = zk.exists(LOCK_DIR, false);
			if(stat == null){
				// 创建根节点
				zk.create(LOCK_DIR, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT); 
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
