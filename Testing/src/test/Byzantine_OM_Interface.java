package test;

import java.rmi.Remote;
import java.rmi.RemoteException;

import message.Message;

public interface Byzantine_OM_Interface extends Remote  {
	
	/**
	 * 
	 * @param msg
	 * depending on the type: MSG and ACK
	 * @param setDelay
	 * Artificial delay
	 * @throws RemoteException
	 */
	public void receive(Message msg, int setDelay) throws RemoteException;
	
	/**
	 * @param order
	 * specifies the starting order from the commander 
	 * @param numFaulty
	 * number of faulty processes
	 */
	public void startAlgorithm (boolean order, int numFaulty) throws RemoteException;
	
	public void report() throws RemoteException; 


}
