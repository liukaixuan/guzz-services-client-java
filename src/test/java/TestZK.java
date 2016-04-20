import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

/**
 * 
 */

/**
 * 
 * 
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class TestZK {

	 public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
		 ZooKeeper zk = new ZooKeeper("10.100.4.31:2181,10.100.4.32:2181/gs.test", 3000, new Watcher(){

			public void process(WatchedEvent event) {
				String path = event.getPath() ;
				System.out.println("event:" + event) ;
			}
			 
		 }) ;
		 
		 byte[] bs = "hoho".getBytes() ;
		 
		 Stat stat = new Stat() ;
		 zk.getData("/test", true, stat) ;
		 zk.getData("/test", true, stat) ;
		 zk.getData("/test", true, stat) ;
		 zk.getData("/test", true, stat) ;
		 zk.getData("/test", false, null) ;
		 zk.getData("/test", false, null) ;
		 zk.getData("/test", false, null) ;
		 zk.getData("/test", false, null) ;
		 zk.getChildren("/test", true) ;
		 zk.getChildren("/test", false) ;
		 zk.create("/test/a", bs, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL) ;
		 zk.create("/test/b", bs, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL) ;
		 
		 zk.delete("/test/a", -1) ;
		 zk.delete("/test/b", -1) ;
		 zk.delete("/test", -1) ;
		 
		 
		 zk.create("/test", bs, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT) ;
		 zk.setData("/test/abc", "keke".getBytes(), -1, new StatCallback(){

			public void processResult(int rc, String path, Object ctx, Stat stat) {
				System.out.println("set-a--processResult:" + ctx) ;
			}
			 
		 }, stat) ;
		 
		 
		 zk.close() ;
		 
		 synchronized(Thread.currentThread()){
			 Thread.currentThread().wait(3000) ;
		 }
	 }
	 
}
