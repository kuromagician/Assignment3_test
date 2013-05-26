package test;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

import tree.NodeData;
import tree.TreeNode;

import java.rmi.*;

import message.Message;

public class Byzantine_OM extends UnicastRemoteObject implements Byzantine_OM_Interface, Runnable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6811798268590999116L;

	//store the processes' urls
	private List<String> urls;
	
	//total number of processes
	private int numProcesses;
	
	//this process's index
	private int index;
	
	//decision list
	private TreeNode decision_tree;	
	
	private boolean defaultOrder = true;
	
	private int numfaulty;
	
	private int numReceived = 0;
	
	private int numExpected = 1;
	
	//indicate whether round status are set or not 
	private boolean set = false;
	//private int round;
	
	//indicate whether the tree is constructed or not
	//private boolean initial = false;
	
	private byte[]  lock= new byte[0];
	
	//private Runnable action = new 
	
	private CyclicBarrier barrier;  
	
	private CountDownLatch startSignal;
	
	public Byzantine_OM (List<String> urls, int index) throws RemoteException{
			//get the processes' list
			this.urls = urls;
			//get the index of this process
			this.index = index;
			//get the total number of processes
			this.numProcesses = urls.size();	
	}
	

	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("I'm here!");
		
	}

	public void receive(Message msg, int setDelay) throws RemoteException {
		new Thread(new receiveProcess(msg, setDelay)).start();
		//new Thread(this).start();
	}
	
	public void startAlgorithm(boolean Order){
		Message msg = new Message(index, Order, 2);
		for(int i=0; i<numProcesses; i++){
			if(i != index){
				Byzantine_OM_Interface remoteProcess = getProcess(urls.get(i));
				try {
					//System.out.println("sending to " + urls.get(i) +"\n msg:" + msg.getSequence());
					remoteProcess.receive(msg, 0);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	/*protected class createBarrier implements Runnable{

		public void run() {
			barrier = new();
		}
		
	}*/
	
	public class receiveProcess implements Runnable{
		private Message msg;
		private int delay;
		
		public receiveProcess(Message msg, int setDelay){
			this.msg = msg;
			this.delay = setDelay;	
		}
		
		
	
		public void run() {
			List<Integer> localSq = new ArrayList<Integer>(msg.getSequence());
			
			if(localSq.size() == 1){
				numfaulty = msg.getNumFaulty();
				NodeData root = new NodeData(msg.getOrder());
				
				decision_tree = new TreeNode(localSq, null, null, root);
				createTree(numfaulty-1, decision_tree);
				if(index == 1)
				printWholeTree(decision_tree);
				startSignal = new CountDownLatch(1);  
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			else{
				TreeNode currNode = findNode(localSq, decision_tree, 1);
				//System.out.println("local Sq:"+ localSq + "Current Node:" + currNode + ", Order:" + msg.getOrder() + index);
				currNode.setData(new NodeData(msg.getOrder()));
			}
			startSignal.countDown();
			try {
				if(startSignal.await(5, TimeUnit.SECONDS));
					
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			
			synchronized(lock){
					numExpected = numExpected * ( numProcesses - 1 - msg.getSequence().size());
					System.out.println("numExpected:" + numExpected);
				numReceived++;
			}
			startSignal = new CountDownLatch(numExpected);
			
			
			
			/*synchronized(lock){
				if(numReceived == 0){
					set = true;
					numExpected = numExpected * msg.getSequence().size();
					System.out.println("numExpected:" + numExpected);
				}	
				numReceived++;
			}
			
			while((numReceived < numExpected) || set == false){
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// 
					e.printStackTrace();
				}
			}
			
			synchronized(lock){
				if(set){
					numReceived = 0;
					set = false;
				}
			}
			
			try {
				Thread.sleep(200);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}*/
			//add the local id to the message
		
				
			
			msg.add_id(index);
			//System.out.println("curr_index: " + index + "Sequence is:" + localSq);
			if(localSq.size() <= numfaulty){
				for(int i=0; i<numProcesses; i++){
					if(i!=index && !localSq.contains(i)){
						Byzantine_OM_Interface remoteProcess = getProcess(urls.get(i));
						try {
							//System.out.println("Sending to " + index + "Sequence is:" + sequence);
							remoteProcess.receive(msg, delay);
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}
				}
			}
			else if(localSq.size() == numfaulty + 1){
				System.out.println(majority(decision_tree)+ "by process:" + index);
			}
		}



		private int factorial(int i) {
			if(i < 0){
				throw new IllegalArgumentException("x must be>=0");
			}
			if(i == 1)
				return 1;
			else return i * factorial(i-1);
		}
	}	
	
	protected void createTree(int current_round, TreeNode node){
		for(int i=0; i<numProcesses; i++)
			if(i!=index && !node.getId().contains(i)){
				List<Integer> newid = new ArrayList<Integer>(node.getId());
				newid.add(i);
				TreeNode newchild = new TreeNode(newid, null, null, new NodeData(defaultOrder));
				TreeNode.appendAsChild(newchild, node);
				if(current_round!=0)
					createTree(current_round-1, newchild);
			}
					
	}
	
	private TreeNode findNode(List<Integer> sq, TreeNode node, int level){
		//System.out.println("Now searching the node " + node.getId());
		List<Integer> localSq = new ArrayList<Integer>(sq);
		TreeNode nextNode = TreeNode.search(localSq.subList(0, level), node);		
		if(level == sq.size())
			return nextNode;
		else return findNode(localSq, nextNode, level+1);
	}
	
	protected Byzantine_OM_Interface getProcess(String url){
		Byzantine_OM_Interface remoteProcess;
		try {
			remoteProcess = (Byzantine_OM_Interface) Naming.lookup(url);
			return remoteProcess;
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void printWholeTree(TreeNode node){
		System.out.println(node.getId());
		
		if(node.getChildren() == null){
			return;
		}
		else{			
			for(TreeNode m: node.getChildren()){
				if(m != null){
				//System.out.println(m.getId());
				printWholeTree(m);
				}
			}
		}
	}
	
	private boolean majority(TreeNode node){
		int con = 0;
		int pro = 0;
		
		if(node.getData().getOrder())
			pro++;
		else con++;
		
		if(node.isLeaf())
			return node.getData().getOrder();
		else {
			for(TreeNode m: node.getChildren())
				if(majority(m))
					pro++;
				else con++;
			if(pro > con){
				return true;
			}
			else if(pro < con){
				return false;
			}
			else return defaultOrder;
		}
	}
	

}
