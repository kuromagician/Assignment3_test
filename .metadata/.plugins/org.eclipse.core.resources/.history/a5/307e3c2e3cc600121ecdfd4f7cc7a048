package test;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.*;

import message.Message;

import tree.NodeData;
import tree.TreeNode;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.util.ArrayList;
import java.util.List;

public class Start_main {

	/**
	 * @param args
	 */
	private TreeNode decision_tree;
	
	
	/*public  void test1(){
		List<List<Integer>> d_list;
		List<Integer> id = new ArrayList<Integer>(1);
		id.add(1);
		Message msg = new Message(1, true, 0);
		d_list = new ArrayList<List<Integer>>(9);
		List<Integer> inside_list1 = new ArrayList<Integer>(2);
		//List<Integer> inside_list2 = new ArrayList<Integer>(3);
		List<Integer> inside_list3 = new LinkedList<Integer>();
		inside_list1.add(0,1);
		d_list.add(0,inside_list1);
		System.out.println("The output is " + d_list.get(0).get(0));
		System.out.println("The output is " + inside_list3.size());
		//int numfaulty = msg.getNumFaulty();
		NodeData root = new NodeData(msg.getOrder());
		decision_tree = new TreeNode(msg.getSequence(), null, null, root);
		NodeData children1 = new NodeData(false);
		NodeData children2 = new NodeData(false);
		TreeNode child_node1 = new TreeNode(msg.getSequence(), null, null, children1);
		TreeNode child_node2 = new TreeNode(msg.getSequence(), null, null, children2);

		TreeNode.appendAsChild(child_node1, decision_tree);
		TreeNode.appendAsChild(child_node2, child_node1);
	}*/
	
	public  void verify(){
		
		for(TreeNode  node: decision_tree.getChildren() )
			System.out.println(node.toString() +" "+ node.getChildren());	
	}

	/**
	 * this main create all the processes and register them
	 */
	private static List<Byzantine_OM_Interface> processList;
	
	public static void main(String[] args) {
		
		try {
			LocateRegistry.createRegistry(1099);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
	List<String> urls = new ArrayList<String>();
	for(int i=0; i<8; i++){
		String curr_url = "rmi://localhost/process" + i;
		urls.add(curr_url);
	}
	//System.out.println(urls);
		
		
		processList = new ArrayList<Byzantine_OM_Interface>(urls.size());
		Byzantine_OM_Interface process;
		
		for(int i = 0; i < urls.size(); i++) {
			try {
				process = new Byzantine_OM (urls, i);
                try {
					Naming.bind(urls.get(i), process); 
					processList.add(process);
					//newThread = new Thread(process);
					
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (AlreadyBoundException e) {
					e.printStackTrace();
				}
               
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		
		try {
			Byzantine_OM_Interface process0 = (Byzantine_OM_Interface)Naming.lookup("rmi://localhost/process0");
			Byzantine_OM_Interface process1 = (Byzantine_OM_Interface)Naming.lookup("rmi://localhost/process1");
			Byzantine_OM_Interface process2 = (Byzantine_OM_Interface)Naming.lookup("rmi://localhost/process2");
			Byzantine_OM_Interface process3 = (Byzantine_OM_Interface)Naming.lookup("rmi://localhost/process3");
			
			//new Thread(process0).start();
			
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}


