package ServerSide;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatInterface extends Remote{
	public void setMsg(String name, String m) throws RemoteException;
	public String[] getMsg() throws RemoteException;
	public void root(String pw, String tempName) throws RemoteException;
	public int qasjaIme() throws RemoteException;
	
	public void taskList(String Client, String type, String Admin, String pid) throws RemoteException;
	public String[] amISearched() throws RemoteException;
	public void getTaskList(String task, String Administrator) throws RemoteException;
	public String getTaskClientList() throws RemoteException;
}
