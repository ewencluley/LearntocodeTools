package edu.ewencluley.javainterpreter;

public class SyntaxTreeNode {

	SyntaxTreeNode left=null;
	SyntaxTreeNode right=null;
	String root;

	public SyntaxTreeNode(String t)
	{
		root = t;
	}

	public void setLeft(SyntaxTreeNode l){
		left = l;
	}

	public void setRight(SyntaxTreeNode r){
		right = r;
	}

	public void setRoot(String r){
		root = r;
	}

	public boolean isLeaf(){
		if(left==null && right==null){
			return true;
		}else{
			return false;
		}
	}

	public String toString(){
		return root;
	}

	public String getRoot()
	{
		return root;
	}

	public SyntaxTreeNode getDaughter(String d){
		if(d=="right" && right !=null){
			return right;
		}else if(d=="left" && left !=null){
			return left;
		}else{
			return new SyntaxTreeNode("dummy");
			//return null;
		}
	}

	public boolean equals(Object o){
		if((String) ((SyntaxTreeNode) o).getRoot() == root){
			return true;
		} else {
			return false;
		}
	}
}
