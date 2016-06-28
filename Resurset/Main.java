package Resurset;
//@author Mentor Bibaj
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Date;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.WindowConstants;

import ServerSide.ChatImpl;
import ServerSide.ChatInterface;

public class Main extends javax.swing.JFrame{   
		
	private static final long serialVersionUID = 1L;
	public static String myName = "";
	public static String IP = "localhost";
	public static String PORT = "2016";
	
	public static int RootAccess = 0; //0 jo 1 po
	 
	public static void main(String[] args) {
		startServer();
		String str = JOptionPane.showInputDialog("Shenoni Emrin tuaj");
		myName = str;
		GUI();
		startTimer();
	 }
	
	public static void startServer(){
		if(JOptionPane.showInputDialog("Lesho Serverin (s) ose qfardo fjale tjeter per Jo").equals("s")){
			try {
	    		int convertPort = new Integer(PORT).intValue();
				LocateRegistry.createRegistry(convertPort);
				ChatImpl impl= new ChatImpl();
				Naming.rebind("rmi://"+IP+":"+PORT+"/chat", impl);
			} catch (RemoteException e1) {
				e1.printStackTrace();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			System.out.println("Serveri u startua.");
		}
		
	}
	 
	static JTextArea chatArea;
	static boolean searched = false;
	 public static void GUI(){
		  chatArea = new JTextArea(8, 40);
	      chatArea.setEditable(false);
	      chatArea.setFocusable(false);
	      JScrollPane chatScroll = new JScrollPane(chatArea);
	      JPanel chatPanel = new JPanel(new BorderLayout());
	      chatPanel.add(new JLabel("Chat:", SwingConstants.LEFT), BorderLayout.PAGE_START);
	      chatPanel.add(chatScroll);

	      JTextField inputField = new JTextField(40);
	      JButton sendBtn = new JButton("Send");
	      sendBtn.addActionListener(new ActionListener(){
	        public void actionPerformed(ActionEvent e)//  I dergojme te dhenat
	        {
	          chatArea.append(myName+": " + inputField.getText()+"   ("+
	        		  new Date()+")\n");
	          if(!inputField.equals("")){
	        	  try {
	        		  String msg = inputField.getText();
						ChatInterface stubChat= (ChatInterface)Naming.lookup("rmi://"
														+IP+":"+PORT+"/chat");
						if(msg.equals("root")){
							String rootSt = JOptionPane.showInputDialog("Sheno Passwordin:");
							stubChat.root(rootSt, myName);
							RootAccess = stubChat.qasjaIme();
						}else{
							stubChat.setMsg(myName, msg);
						}
						
						if(RootAccess == 1 && msg.equals("tasklist")){
							String argTasklist = JOptionPane.showInputDialog("Sheno Klientin per te pare Tasklisten e Programeve:");
							stubChat.taskList(argTasklist, msg, myName,"");
							searched = true;
						}
						
						if(RootAccess ==1 && msg.equals("taskkill")){
							String argTasklist = JOptionPane.showInputDialog("Sheno Klientin te cilit deshironi ti mbyllni procesin");
							String tPID = JOptionPane.showInputDialog("Sheno PID e klientit " + argTasklist );
							stubChat.taskList(argTasklist, msg, myName, tPID);
							searched = true;
						}
						inputField.setText("");
					} catch (Exception re) {
						//System.out.println("Dergimi i mesazhit nuk mund te behet");
					}
	          }
	        }
	      });
	      JPanel inputPanel = new JPanel();
	      inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.LINE_AXIS));
	      inputPanel.add(inputField);
	      inputPanel.add(sendBtn);

	      JPanel youLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
	      youLabelPanel.add(new JLabel("You("+myName+"):"));

	      JPanel mainPanel = new JPanel();
	      mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
	      mainPanel.add(chatPanel);
	      mainPanel.add(Box.createVerticalStrut(10));
	      mainPanel.add(youLabelPanel);
	      mainPanel.add(inputPanel);

	      JFrame frame = new JFrame("Chat & CMD");
	      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	      frame.add(mainPanel);
	      frame.pack();
	      frame.setLocationRelativeTo(null);
	      frame.setVisible(true);

	 }
	 
	 
	static Timer timer;
	static String mesazhiParaprak = "";
	static boolean processed = false;
	public static void startTimer(){
		ActionListener actionListener = new ActionListener(){
			public void actionPerformed(ActionEvent actionEvent){
				try {
						ChatInterface stubChat= (ChatInterface)Naming.lookup("rmi://"
														+IP+":"+PORT+"/chat");
						String[] s = stubChat.getMsg();
						if(!s[0].equals(myName) && !s[0].equals("none") && !mesazhiParaprak.equals(s[1])){
							chatArea.append(s[0]+": " + s[1]+"   ("+
					        		  new Date()+")\n");
							mesazhiParaprak = s[1];
						}
						
						String[] g = stubChat.amISearched();
						if(g[0].equals(myName) && g[1].equals("tasklist")){
							stubChat.getTaskList(TaskList(),g[2]);
							//processed = true;
						}else if(g[0].equals(myName) && g[1].equals("taskkill")){
							stubChat.getTaskList(TaskKill(g[3]),g[2]);
						}
						
						if(RootAccess ==1 && searched == true && stubChat.getTaskClientList() != null){
							chatArea.append(stubChat.getTaskClientList());
							searched = false;
						}
						
					} catch (Exception re) {
						//System.out.println("Dergimi i mesazhit nuk mund te behet");
					}
			}
		};
		timer = new Timer(500, actionListener);
		timer.start();
		
	}
	
	
	public static String TaskList(){
		String myTask = "";
		try {
		    String line;
		    Process p = Runtime.getRuntime().exec(System.getenv("windir") +"\\system32\\"+"tasklist.exe");
		    BufferedReader input =
		            new BufferedReader(new InputStreamReader(p.getInputStream()));
		    while ((line = input.readLine()) != null) {
		        myTask += line+"\n";
		    }
		    input.close();
		} catch (Exception err) {
		    err.printStackTrace();
		}
		
		return myTask;
	}
	
	public static String TaskKill(String pid){
		String myTask = "";
		try {
		    String line;
		    Process p = Runtime.getRuntime().exec("Taskkill -f /pid " + pid );
		    BufferedReader input =
		            new BufferedReader(new InputStreamReader(p.getInputStream()));
		    while ((line = input.readLine()) != null) {
		        myTask += line+"\n"; 
		    }
		    input.close();
		} catch (Exception err) {
		    err.printStackTrace();
		}
		
		return myTask;
		
	}
		
	 
	 
	 
	 
	 
	 
}