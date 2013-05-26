package test;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Test_main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			Byzantine_OM_Interface process0 = (Byzantine_OM_Interface)Naming.lookup("rmi://localhost/process0");
			//Byzantine_OM_Interface process1 = (Byzantine_OM_Interface)Naming.lookup("rmi://localhost/process1");
			//Byzantine_OM_Interface process2 = (Byzantine_OM_Interface)Naming.lookup("rmi://localhost/process2");
			//Byzantine_OM_Interface process3 = (Byzantine_OM_Interface)Naming.lookup("rmi://localhost/process3");
			
			process0.startAlgorithm(false, 3);
			
			
			
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
