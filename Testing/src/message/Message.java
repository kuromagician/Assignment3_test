package message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Message implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1823529654720247989L;


	private List<Integer> sequence = new ArrayList<Integer>();
	
	private boolean order;
	
	private int faulty;
	
	public enum type{MSG, ACK};
	
	private type msgType;
	
	private int round;
	
	public Message(int id, boolean order, int num_faulty, type msgtype){
		this.sequence.add(id);
		this.order = order;
		this.faulty = num_faulty;
		this.msgType = msgtype;
	}
	
	public Message(type msgtype, int round){
		this.msgType = msgtype;
		this.round = round;
	}
	
	public void add_id(Integer id){
		sequence.add(id);
	}
	
	public boolean getOrder(){
		return order;
	}
	
	public List<Integer> getSequence(){
		return sequence;
	}
	
	public int getNumFaulty(){
		return faulty;
	}
	
	public type getType(){
		return msgType;
	}
	
	public int getRound(){
		return round;
	}
	
	public void setOrder(boolean Order){
		order = Order;
	}

}
