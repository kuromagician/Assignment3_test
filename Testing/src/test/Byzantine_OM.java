package test;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import tree.NodeData;
import tree.TreeNode;

import java.rmi.*;

import message.Message;
import message.Message.type;

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
	
	private int numToReceive;
	
	private int numExpected = 1;
	
	private int numSend;
	
	private int round = 0;
	
	private byte[]  lock= new byte[0];
	
	private long pre_time;
	
	private final Byzantine_OM outer = this;
	
	private boolean set = false;
	
	private int counter = 0;
	
	private int sendCounter = 0;
	
	public enum faultyType {NOR, AFK, RAN}; 
	
	private faultyType fault;
	
	public Byzantine_OM (List<String> urls, int index, faultyType fault) throws RemoteException{
			//get the processes' list
			this.urls = urls;
			//get the index of this process
			this.index = index;
			//get the total number of processes
			this.numProcesses = urls.size();
			
			this.numToReceive = this.numProcesses;
			
			this.fault = fault;
	}
	

	

	public void receive(Message msg, int setDelay) throws RemoteException {
		new Thread(new receiveProcess(msg, setDelay)).start();
		//new Thread(this).start();
	}
	
	public void startAlgorithm(boolean Order){
		Message msg = new Message(index, Order, 2, type.MSG);
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
	@Override
	public void run() {
		while(round < numfaulty + 1){
			while(numExpected > counter){
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			//System.out.println("now is round: " + round + " from "+ index + "numfalty:"+ numfaulty);
			while(System.currentTimeMillis() - pre_time < 2000){
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			Message ackMsg = new Message(type.ACK, round);
			
			for(int i=0; i<numProcesses; i++)
				if(i != index)
					try {
						getProcess(urls.get(i)).receive(ackMsg, 0);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
			
			while(numToReceive > numfaulty + 1){
				//System.out.println("numToReceive: " + numToReceive+ "  numExpected:" + numExpected +" "+ index);
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			//System.out.println("round: " + round+ "  numExpected:" + numExpected);
			while(System.currentTimeMillis() - pre_time < 2000){
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			synchronized(lock){
				round++;
				numExpected = numExpected*(numProcesses - round - 1);
				numToReceive = numProcesses;
				counter = 0;
				sendCounter = 0;
				set = true;
			}
			
			if(round < numfaulty){
				while(sendCounter <= numExpected){
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				set = false;
			}
			
			System.out.println("round: " + round+ "  numExpected:" + numExpected +" "+ index);
			
		}
		System.out.println("decision: " + majority(decision_tree)+ " is made by process:" + index);
	}
	
	private class receiveProcess implements Runnable{
		private Message msg;
		private int delay;
		
		public receiveProcess(Message msg, int setDelay){
			this.msg = msg;
			this.delay = setDelay;	
		}
		
		
	
		public void run() {
			type msgtype = msg.getType();
			switch (msgtype){
			//if it's real message, change the corresponding node in the tree 
			case MSG:
				List<Integer> localSq = new ArrayList<Integer>(msg.getSequence());
				//refresh current time when receiving new message
				pre_time = System.currentTimeMillis();
				if(localSq.size() == 1){
					numfaulty = msg.getNumFaulty();
					//create local tree
					NodeData root = new NodeData(msg.getOrder());
					decision_tree = new TreeNode(localSq, null, null, root);
					createTree(numfaulty-1, decision_tree);
					if(index == 1)
					printWholeTree(decision_tree);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} 
					//start the thread to deal with the incoming message
					new Thread(outer).start(); 
				}
				else{
					//set corresponding node to the received value
					TreeNode currNode = findNode(localSq, decision_tree, 1);
					currNode.setData(new NodeData(msg.getOrder()));
				}
				while(round < localSq.size() - 1){
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				synchronized(lock){
					counter++;
				//	System.out.println("counter: "+counter +" index "+ index +"local sq: " + localSq);
				}

				while(!set){
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				//System.out.println("Sending to " + index + "sendcounter: "+ sendCounter);
				msg.add_id(index);
				//System.out.println("curr_index: " + index + "Sequence is:" + localSq);
				if(localSq.size() <= numfaulty){
					for(int i=0; i<numProcesses; i++){
						
						if(i!=index && !localSq.contains(i)){
							Byzantine_OM_Interface remoteProcess = getProcess(urls.get(i));
							try {
								
								remoteProcess.receive(msg, delay);
								sendCounter++;
								
							} catch (RemoteException e) {
								e.printStackTrace();
							}
						}
					}
				}
				//System.out.println("numSend: " + numSend +" by " + index + "sequnce" + localSq);
				break;
			//if it's the signal to enter next round
			case ACK:
				int curr_round = msg.getRound();
				
				synchronized(lock){
					numToReceive--;
					//System.out.println("numTiReceive: " + numToReceive + " by " + index + "@round" + round);
				}
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
