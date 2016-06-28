package ServerSide;
//@author Mentor Bibaj
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ChatImpl extends UnicastRemoteObject implements ChatInterface{

	private static final long serialVersionUID = 1L;
	public String myName = "none";
	public String msg="";
	public int numOfMessages = 0;
	protected String sysPw = "pdistribuar";
	
	private int qasja = 0; //0 jo 1 po 
	
	public ChatImpl() throws RemoteException {
		super();
	}
	
	public void setMsg(String name, String m) throws RemoteException{
		myName = name;
		msg = m;
		
	}
	
	public String[] getMsg() throws RemoteException{
		String[] s = new String[2];
		s[0] = myName;
		s[1] = msg;
		
		return s;
	}
	
	public String Password;
	public void root(String pw, String tempName) throws RemoteException{
		Password = pw;
		checkRoot(tempName);
	}
	
	public void checkRoot(String tempName){
		if(Password.equals(sysPw)){
			msg = tempName + " ka garantuar te drejtat per\n"+ "\tqasje te klientet";
			myName = "Chat & CMD System - Root Access";
			qasja = 1;
		}else{
			msg = tempName + " nuk ka mundur te garantoj\n"+"\tte drejtat per qasje te klientet";
			myName = "Chat & CMD System - Root Access";
			qasja = 0;
		}
	}
	
	public int qasjaIme() throws RemoteException{
		return qasja;
	}
	
	public String ClientToBeViewed = "";
	public String Type = "";
	public String Administrator = "";
	public String PID;
	
	public void taskList(String Client,String type, String Admin, String pid) throws RemoteException{
		ClientToBeViewed = Client;
		Type = type;
		Administrator = Admin;
		PID = pid;
	}
	
	public String[] amISearched() throws RemoteException{
		String[] sp = {ClientToBeViewed, Type, Administrator,PID+""};
		return sp;
	}
	
	
	public String tasklista;
	public String thisAdmin;
	public void getTaskList(String task, String Administrator) throws RemoteException{
		tasklista = task;
		thisAdmin = Administrator;
	}
	
	public String getTaskClientList() throws RemoteException{
		return tasklista;
	}
}