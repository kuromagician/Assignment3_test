package tree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * node of tree
 * 
 */
public class TreeNode
{
    private List<Integer> id;
    private TreeNode parent;
    private ArrayList<TreeNode> children;
    private NodeData data;

    public TreeNode(List<Integer> sequence, TreeNode parent, ArrayList<TreeNode> children,
            NodeData data)
    {
        super();
        this.id = sequence;
        this.parent = parent;
        this.children = children;
        this.data = data;
    }

    public List<Integer> getId()
    {
        return id;
    }

    public void setId(List<Integer> id)
    {
        this.id = id;
    }

    public TreeNode getParent()
    {
        return parent;
    }

    public void setParent(TreeNode parent)
    {
        this.parent = parent;
    }

    public ArrayList<TreeNode> getChildren()
    {
        return children;
    }

    public void setChildren(ArrayList<TreeNode> children)
    {
        this.children = children;
    }

    public NodeData getData()
    {
        return data;
    }

    public void setData(NodeData data)
    {
        this.data = data;
    }

    /**
     * ��ǰ�ڵ��Ƿ��Ǹ��ڵ�
     * 
     * @return
     */
    public boolean isRoot()
    {
        if (this.parent == null)
        {
            return true;
        } else
        {
            return false;
        }
    }

    /**
     * ��ǰ�ڵ��Ƿ���Ҷ�ӽڵ�
     * 
     * @return
     */
    public boolean isLeaf()
    {
        if (this.children == null || this.children.size() == 0)
        {
            return true;
        } else
        {
            return false;
        }
    }

    /**
     * �Ըýڵ㼰���������в�α���
     * 
     * @param treeNode
     */
    public static void levelTraversal(TreeNode treeNode)
    {
        if (treeNode == null)
        {
            return;
        }
        if (treeNode.isLeaf())
        {
            treeNode.data.output();
        } else
        {
            Queue<TreeNode> queue = new LinkedList<TreeNode>();
            queue.offer(treeNode);
            while (queue.size() > 0)
            {
                TreeNode node = queue.poll();
                node.data.output();

                if (node.isLeaf() == false)
                {
                    for (int i = 0; i < node.children.size(); i++)
                    {
                        queue.offer(node.children.get(i));
                    }
                }
            }
        }
    }

    /**
     * �����ص�Ľڵ㣬�ڴ�ʹ�ò�α������в���
     * 
     * @param id
     * @param treeNode
     * @return
     */
    public static TreeNode search(List<Integer> id, TreeNode treeNode)
    {
        if (treeNode == null)
        {
            return null;
        }
        if (treeNode.isLeaf())
        {
            if (treeNode.id.equals(id))
            {
                return treeNode;
            } else
            {
                return null;
            }
        } else
        {
            Queue<TreeNode> queue = new LinkedList<TreeNode>();
            queue.offer(treeNode);
            while (queue.size() > 0)
            {
                TreeNode node = queue.poll();
                if (node.id.equals(id))
                {
                    return node;
                }

                if (node.isLeaf() == false)
                {
                    for (int i = 0; i < node.children.size(); i++)
                    {
                        queue.offer(node.children.get(i));
                    }
                }
            }

            return null;
        }
    }

    /**
     * ���ڵ����Ϊ��һ���ڵ�ĺ���
     * 
     * @param childNode
     * @param toBeAppended
     * @return
     */
    public static boolean appendAsChild(TreeNode childNode,
            TreeNode toBeAppended)
    {
        if (childNode == null || toBeAppended == null)
        {
            return false;
        } else
        {
            if (toBeAppended.isLeaf())
            {
                ArrayList<TreeNode> children = new ArrayList<TreeNode>();
                children.add(childNode);
                toBeAppended.children = children;
            } else
            {
                toBeAppended.children.add(childNode);
            }

            return true;
        }
    }

    /**
     * ���ڵ����Ϊ��һ���ڵ���ֵ�
     * 
     * @param sublingNode
     * @param toBeAppended
     * @return
     */
    public static boolean appendAsSubling(TreeNode sublingNode,
            TreeNode toBeAppended)
    {
        if (sublingNode == null || toBeAppended == null)
        {
            return false;
        } else
        {
            // ���������޷��Ը��ڵ�����ֵ�
            if (toBeAppended.isRoot() == true)
            {
                return false;
            } else
            {
                toBeAppended.parent.children.add(sublingNode);
                return true;
            }
        }
    }
}
