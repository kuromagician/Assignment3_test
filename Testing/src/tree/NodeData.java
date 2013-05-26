package tree;

public class NodeData {
	private boolean order;

    public NodeData(boolean order)
    {
        this.order = order;
    }

    public void output()
    {
        System.out.println("The order is  " + order);
    }
    
    public boolean getOrder(){
    	return order;
    }
}
