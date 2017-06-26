package com.me2me.live.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
  	DistributedLock lock = null;
	try {
		lock = new DistributedLock("127.0.0.1:2182","test");
		lock.lock();
		//do something...
	} catch (Exception e) {
		e.printStackTrace();
	} 
	finally {
		if(lock != null)
			lock.unlock();
	}
 *
 */
public class DistributedLock implements Lock, Watcher{
	private Logger logger = LoggerFactory.getLogger(DistributedLock.class);
	private ZooKeeper zk;
	private final static String root = "/live_locks";//根
	private String lockName;//竞争资源的标志
	private String waitNode;//等待前一个锁
	private String myZnode;//当前锁
	private CountDownLatch latch;//计数器
	private int sessionTimeout = 30*1000;		// 默认最长等待时间为30秒。
	private List<Exception> exception = new ArrayList<Exception>();
	private boolean notified = false;	// 服务器是否已经唤醒我。
	/**
	 * 使用指定的zk初始化一个分布式锁
	 * @author zhangjiwei
	 * @date Jun 9, 2017
	 * @param zk
	 * @param lockName
	 */
	public DistributedLock(String config, String lockName) throws Exception{
		this.lockName = lockName;
		// 创建一个与服务器的连接
		 try {
			zk = new ZooKeeper(config, sessionTimeout, this);
			Stat stat = zk.exists(root, false);
			if(stat == null){
				// 创建根节点
				zk.create(root, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT); 
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * zookeeper节点的监视器
	 */
	public void process(WatchedEvent event) {
		if(event.getType().equals(EventType.NodeDeleted)){
			if(this.latch != null) {  
				notified=true;		// 服务器已经通知我。
	            this.latch.countDown();  
	        }
		}
	}
	
	public void lock() {
		if(exception.size() > 0){
			throw new LockException(exception.get(0));
		}
		try {
			if(this.tryLock()){
				logger.info("Thread " + Thread.currentThread().getId() + " " +myZnode + " get lock true");
				return;
			}else{
				waitForLock(waitNode, sessionTimeout);//等待锁
			}
		} catch (KeeperException e) {
			throw new LockException(e);
		} catch (InterruptedException e) {
			throw new LockException(e);
		} 
	}

	public boolean tryLock() {
		try {
			String splitStr = "_lock_";
			if(lockName.contains(splitStr))
				throw new LockException("lockName can not contains \\u000B");
			//创建临时子节点
			myZnode = zk.create(root + "/" + lockName + splitStr, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
			logger.info(myZnode + " is created ");
			//取出所有子节点
			List<String> subNodes = zk.getChildren(root, false);
			//取出所有lockName的锁
			List<String> lockObjNodes = new ArrayList<String>();
			for (String node : subNodes) {
				String _node = node.split(splitStr)[0];
				if(_node.equals(lockName)){
					lockObjNodes.add(node);
				}
			}
			Collections.sort(lockObjNodes);
			if(myZnode.equals(root+"/"+lockObjNodes.get(0))){
				//如果是最小的节点,则表示取得锁
	            return true;
	        }
			//如果不是最小的节点，找到比自己小1的节点
			String subMyZnode = myZnode.substring(myZnode.lastIndexOf("/") + 1);
			waitNode = lockObjNodes.get(Collections.binarySearch(lockObjNodes, subMyZnode) - 1);
		} catch (KeeperException e) {
			throw new LockException(e);
		} catch (InterruptedException e) {
			throw new LockException(e);
		}
		return false;
	}

	public boolean tryLock(long time, TimeUnit unit) {
		try {
			if(this.tryLock()){
				return true;
			}
	        return waitForLock(waitNode,time);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean waitForLock(String lower, long waitTime) throws InterruptedException, KeeperException {
        Stat stat = zk.exists(root + "/" + lower,true);
        //判断比自己小一个数的节点是否存在,如果不存在则无需等待锁,同时注册监听
        if(stat != null){
        	logger.info("Thread " + Thread.currentThread().getId() + " waiting for " + root + "/" + lower);
        	this.latch = new CountDownLatch(1);
        	this.latch.await(waitTime, TimeUnit.MILLISECONDS);
        	this.latch = null;
        	if(!notified){
        		throw new InterruptedException("wait timeout,cannot get lock from zookeeper.");
        	}
        }
        return true;
    }

	public void unlock() {
		try {
			logger.debug("unlock " + myZnode);
			zk.delete(myZnode,-1);
			myZnode = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		try{
			zk.close();
			logger.debug("lock closed");
		}catch (Exception e) {
		}
	}

	public void lockInterruptibly() throws InterruptedException {
		this.lock();
	}

	public Condition newCondition() {
		return null;
	}
	
	public class LockException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		public LockException(String e){
			super(e);
		}
		public LockException(Exception e){
			super(e);
		}
	}

}