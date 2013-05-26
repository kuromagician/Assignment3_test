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
	
	public Message(int id, boolean order, int num_faulty){
		this.sequence.add(id);
		this.order = order;
		this.faulty = num_faulty;
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

}
