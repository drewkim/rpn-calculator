import java.util.*;

/**********************************************************************************************
 * Name: Drew Kim
 * Block: D
 * Date: 2/12/14
 * Description: 
 * 		A program that lets the user enter tokens in RPN, like old calculators, and then
 * 		evaluates the expression.
 **********************************************************************************************/
public class ExpressionEvaluator 
{
	static Scanner console = new Scanner(System.in);

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		ExpressionTree tree = generateExpressionTree();		
		if(tree != null)
		{
			System.out.println("Expression: \n" + tree.toStringPostOrder());
			System.out.println("Answer: " + tree.evaluate());
		}
	}

	/**
	 * Generates the expression tree after the user has entered all the tokens.
	 * @return
	 */
	public static ExpressionTree generateExpressionTree()
	{
		Stack stack = new Stack(0);
		Token token = getToken();
		while(token != null && token.getType() != Token.END)
		{
			if(token.getType() == Token.NUMBER)
			{
				ExpressionTree tree = new ExpressionTree(token, null, null);
				stack.push(tree);
			}
			else if(token.getType() == Token.UNARY)
			{
				ExpressionTree tree1 = (ExpressionTree) stack.pop();
				ExpressionTree newTree = new ExpressionTree(token, tree1, null);
				stack.push(newTree);
			}
			else if(token.getType() == Token.BINARY)
			{
				ExpressionTree tree1 = (ExpressionTree) stack.pop();
				ExpressionTree tree2 = (ExpressionTree) stack.pop();
				ExpressionTree newTree = new ExpressionTree(token, tree2, tree1);
				stack.push(newTree);
			}

			token = getToken();
		}
		
		ExpressionTree poppedTree = (ExpressionTree) stack.pop();
		return errorCases(poppedTree);
	}

	/**
	 * Deals with the error cases with the tree. If none, returns the tree.
	 * @param poppedTree
	 * @return
	 */
	public static ExpressionTree errorCases(ExpressionTree poppedTree)
	{
		if(poppedTree == null)
		{
			System.err.println("No Expression");
			return null;
		}
		else if(   ((Token)(poppedTree.getRoot().getValue())).getType() == Token.NUMBER 
				|| (((Token)(poppedTree.getRoot().getValue())).getType() == Token.BINARY && (poppedTree.getRoot().getLeft()) == null)
				|| (((Token)(poppedTree.getRoot().getValue())).getType() == Token.UNARY && (poppedTree.getRoot().getRight()) == null))
		{
			System.err.println("Invalid Expression");
			return null;
		}
		
		return poppedTree;
	}
	
	/**
	 * Gets a token from the user, creates it, and returns it. 
	 * Returns null if the type is invalid.
	 * @return
	 */
	public static Token getToken()
	{
		System.out.println("Enter a type:");
		String str = console.nextLine();
		if(!str.equals(""))
		{
			char type = str.charAt(0);
			if(type == '#')
			{
				return number();
			}
			else if(type == 'u')
			{
				return unary();
			}
			else if(type == 'b')
			{
				return binary();
			}
			else if (type == 'e')
			{
				return new Token(Token.END, Token.EMPTY, Token.EMPTY);
			}
			else
			{
				System.err.println("Invalid type! Expression Terminated.");
				return null;
			}
		}
		else
		{
			System.err.println("Invalid type! Expression Terminated.");
			return null;
		}
	}

	/**
	 * Case if user enters a number.
	 * @return
	 */
	public static Token number()
	{
		System.out.println("Enter a number:");
		double db = console.nextDouble();
		console.nextLine();
		return new Token(Token.NUMBER, db, Token.EMPTY);
	}

	/**
	 * Case if user enters a unary operator.
	 * @return
	 */
	public static Token unary()
	{
		System.out.println("Enter a unary operator:");
		char op = console.nextLine().charAt(0);
		if(op == '+')
		{
			return new Token(Token.UNARY, Token.EMPTY, Token.PLUS);
		}
		else if(op == '-')
		{
			return new Token(Token.UNARY, Token.EMPTY, Token.MINUS);
		}
		else
		{
			System.err.println("Invalid operator!");
			return null;
		}
	}

	/**
	 * Case if the user enters a binary operator.
	 * @return
	 */
	public static Token binary()
	{
		System.out.println("Enter a binary operator:");
		char op = console.nextLine().charAt(0);
		if(op == '+')
		{
			return new Token(Token.BINARY, Token.EMPTY, Token.ADD);
		}
		else if(op == '-')
		{
			return new Token(Token.BINARY, Token.EMPTY, Token.SUBTRACT);
		}
		else if(op == '*')
		{
			return new Token(Token.BINARY, Token.EMPTY, Token.MULTIPLY);
		}
		else if(op == '/')
		{
			return new Token(Token.BINARY, Token.EMPTY, Token.DIVIDE);
		}
		else
		{
			System.out.println("Invalid operator!");
			return null;
		}
	}
}

/**********************************************************************************************
Class:		ExpressionTree
Created by:	Drew Kim

Description:
	The ExpressionTree class. Only holds a TreeNode as a root. Implements methods to print
	and evaluate the tree in post order.

 *********************************************************************************************/
class ExpressionTree
{
	private TreeNode root;

	/**
	 * Constructor. Creates the Expression tree.
	 * @param rootToken
	 * @param leftExpr
	 * @param rightExpr
	 */
	public ExpressionTree(Token rootToken, ExpressionTree leftExpr, ExpressionTree rightExpr)
	{
		root = new TreeNode(rootToken, null, null);
		if(leftExpr == null)
			root.setLeft(null);
		else
			root.setLeft(leftExpr.getRoot());

		if(rightExpr == null)
			root.setRight(null);
		else
			root.setRight(rightExpr.getRoot());
	}

	/**
	 * Wrapper for the actual toString method, the one that is hidden from the user.
	 * @return
	 */
	public String toStringPostOrder()
	{
		return auxToStringPostOrder(root);
	}

	/**
	 * Returns string with tree converted to a string in post order.
	 * @param t
	 * @return
	 */
	private String auxToStringPostOrder(TreeNode t)
	{
		if(t == null)
			return "";
		else
			return "" + auxToStringPostOrder(t.getLeft()) + auxToStringPostOrder(t.getRight()) + t.getValue().toString() + '\n';
	}

	/**
	 * Wrapper for the actual evaluate method, the one that is hidden from the user.
	 * @return
	 */
	public double evaluate()
	{
		return auxEvaluate(root);
	}

	/**
	 * Recursively evaluates the tree in post order.
	 * @param t
	 * @return
	 */
	private double auxEvaluate(TreeNode t)
	{
		if(((Token)(t.getValue())).getType() == Token.NUMBER)
		{
			return ((Token)(t.getValue())).getNum();
		}
		else if(((Token)(t.getValue())).getType() == Token.UNARY)
		{
			if(((Token)(t.getValue())).getOp() == Token.PLUS)
				return ((Token)(t.getLeft().getValue())).getNum();
			else if(((Token)(t.getValue())).getOp() == Token.MINUS)
				return -((Token)(t.getLeft().getValue())).getNum();
			else
				return 0.0;
		}
		else if(((Token)(t.getValue())).getType() == Token.BINARY)
		{
			if(((Token)(t.getValue())).getOp() == Token.ADD)
				return auxEvaluate(t.getLeft()) + auxEvaluate(t.getRight());
			else if (((Token)(t.getValue())).getOp() == Token.SUBTRACT)
				return auxEvaluate(t.getLeft()) - auxEvaluate(t.getRight());
			else if (((Token)(t.getValue())).getOp() == Token.MULTIPLY)
				return auxEvaluate(t.getLeft()) * auxEvaluate(t.getRight());
			else if (((Token)(t.getValue())).getOp() == Token.DIVIDE)
				return auxEvaluate(t.getLeft()) / auxEvaluate(t.getRight());
			else
				return 0.0;
		}
		else
		{
			return 0.0;
		}
	}

	/**
	 * Returns the root of the tree.
	 * @return
	 */
	public TreeNode getRoot()
	{
		return root;
	}
}

/**********************************************************************************************
Class:		Token
Created by:	Drew Kim

Description:
	The Token class. Holds a type, number, and operator. The number and operator fields are
	not filled if the other one is. This is because a token can either be a number or an
	operator. This is determined based on the type the user sends over.

 *********************************************************************************************/
class Token
{
	public static final int EMPTY = 0;

	//Types
	public static final int NUMBER = 1;
	public static final int UNARY = 2;
	public static final int BINARY = 3;
	public static final int END = 4;

	//Unary Operators
	public static final int PLUS = 5;
	public static final int MINUS = 6;

	//Binary Operators
	public static final int ADD = 7;
	public static final int SUBTRACT = 8;
	public static final int MULTIPLY = 9;
	public static final int DIVIDE = 10;

	//Value fields
	private int type;
	private double num;
	private int op;

	/**
	 * Constructor. Creates the Token object.
	 * @param typeIn
	 * @param numIn
	 * @param opIn
	 */
	public Token(int typeIn, double numIn, int opIn)
	{
		type = typeIn;
		num = numIn;
		op = opIn;
	}

	/**
	 * Converts the Token to a string based on the type.
	 */
	public String toString()
	{
		if(type == NUMBER)
		{
			return new String("" + num);
		}
		else if(type == UNARY)
		{			
			if(op == PLUS)
				return new String("+");
			else
				return new String("-");
		}
		else if(type == BINARY)
		{
			if(op == ADD)
				return new String("+");
			else if(op == SUBTRACT)
				return new String("-");
			else if(op == MULTIPLY)
				return new String("*");
			else
				return new String("/");
		}	
		else
		{
			return new String("");
		}
	}

	/**
	 * Returns the type of the Token.
	 * @return
	 */
	public int getType()
	{
		return type;
	}

	/**
	 * Returns the value of the Token.
	 * @return
	 */
	public double getNum()
	{
		return num;
	}

	/**
	 * Returns the operator of the Token.
	 * @return
	 */
	public int getOp()
	{
		return op;
	}
}

/**********************************************************************************************
Class:		Stack
Created by:	Drew Kim

Description:
	The Stack class. Implements a LinkedList, so the only data structure it contains is a
	header ListNode. Has methods to push and pop new objects onto the stack and test if
	it is empty or full.

 *********************************************************************************************/
class Stack
{
	private ListNode top;

	/**
	 * Constructor. Creates the Stack object.
	 * @param size
	 */
	public Stack(int size)
	{
		top = null;
	}

	/**
	 * Returns true or false based on whether the stack is empty or not.
	 * @return
	 */
	public boolean isEmpty()
	{
		return top == null;
	}

	/**
	 * Returns true or false based on whether the stack is full or not.
	 * @return
	 */
	public boolean isFull()
	{
		return false;
	}

	/**
	 * Pushes a new object onto the top of the stack.
	 * @param obj
	 */
	public void push(Object obj)
	{
		top = new ListNode(obj, top);
	}

	/**
	 * Pops the top object off the stack and returns it.
	 * @return
	 */
	public Object pop()
	{
		if(!isEmpty())
		{
			Object temp = top.getValue();
			top = top.getNext();
			return temp;
		}
		else
		{
			return null;
		}
	}

	/**
	 * Returns a string with the stack converted to words.
	 */
	public String toString()
	{
		String str = "";
		ListNode s = top;
		while(s != null)
		{
			str = "" + ((ExpressionTree)s.getValue()).getRoot().getValue() + " " + str;
			s = s.getNext();
		}
		return str;
	}
}

/**********************************************************************************************
Class:		ListNode
Created by:	Drew Kim

Description:
	The ListNode Class. Used for LinkedLists. Holds a value, and a next, which is also a
	ListNode.

 *********************************************************************************************/
class ListNode 
{
	private Object value;
	private ListNode next;

	/**
	 * Constructor. Creates the ListNode object.
	 * @param initValue
	 * @param initNext
	 */
	public ListNode(Object initValue, ListNode initNext)
	{
		value = initValue;
		next = initNext;
	}

	/**
	 * Returns the value of the ListNode.
	 * @return
	 */
	public Object getValue()
	{
		return value;
	}

	/**
	 * Returns the next ListNode.
	 * @return
	 */
	public ListNode getNext()
	{
		return next;
	}

	/**
	 * Sets the value of the ListNode.
	 * @param theNewVal
	 */
	public void setValue(Object theNewVal)
	{
		value = theNewVal;
	}

	/**
	 * Sets the next ListNode.
	 * @param theNewNext
	 */
	public void setNext(ListNode theNewNext)
	{
		next = theNewNext;
	}
}            

/**********************************************************************************************
Class:		TreeNode
Created by:	Drew Kim

Description:
	The TreeNode class. Holds a value, a left child, and a right child, both of which are 
	also TreeNodes.

 *********************************************************************************************/
class TreeNode
{
	private Object value;
	private TreeNode right;
	private TreeNode left;

	/**
	 * Constructor. Creates the TreeNode object.
	 * @param valueIn
	 * @param rightIn
	 * @param leftIn
	 */
	public TreeNode(Object valueIn, TreeNode rightIn, TreeNode leftIn)
	{
		value = valueIn;
		right = rightIn;
		left = leftIn;				
	}

	/**
	 * Returns the value of the TreeNode.
	 * @return
	 */
	public Object getValue()
	{
		return value;
	}

	/**
	 * Returns the right child of the TreeNode.
	 * @return
	 */
	public TreeNode getRight()
	{
		return right;
	}

	/**
	 * Returns the left child of the TreeNode.
	 * @return
	 */
	public TreeNode getLeft()
	{
		return left;
	}

	/**
	 * Sets the value of the TreeNode.
	 * @param obj
	 */
	public void setValue(Object obj)
	{
		value = obj;
	}

	/**
	 * Sets the right child of the TreeNode.
	 * @param rightIn
	 */
	public void setRight(TreeNode rightIn)
	{
		right = rightIn;
	}

	/**
	 * Sets the left child of the TreeNode.
	 * @param leftIn
	 */
	public void setLeft(TreeNode leftIn)
	{
		left = leftIn;
	}
}