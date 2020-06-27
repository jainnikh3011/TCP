import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TCPServer extends JPanel implements Runnable {

	ObjectOutputStream output;
	ObjectInputStream input;

	JButton jButton1;
	JPanel jPanel1;
	JScrollPane jScrollPane1;
	JTextArea jTextArea1;
	JTextField jTextField1;

	public TCPServer() {
		createUI();                   //create UI
	}

	public void run() {
		ServerSocket server;   
		Socket connection;    
		int counter = 1;
		try {
			//Step 1 
			server = new ServerSocket(5111, 100);
			while (true) {
				jTextArea1.setText("Waiting for connection\n");
				//Step 2
				connection = server.accept();
				jTextArea1.append(
						"Connection " + counter + " received from: " + connection.getInetAddress().getHostName());// kaha se aaya h reuest it'll print 
				//step 3
				output = new ObjectOutputStream(connection.getOutputStream());
				output.flush();
				input = new ObjectInputStream(connection.getInputStream());
				jTextArea1.append("\nGot I/O streams\n");
				//step 4
				String message = "SERVER>>> Connection successful";
				output.writeObject(message);
				output.flush();
				jTextField1.setEnabled(true);
				do {
					try {
						message = (String) input.readObject();   //Reading data from Client 
						jTextArea1.append("\n" + message);
						jTextArea1.setCaretPosition(jTextArea1.getText().length());
					} catch (ClassNotFoundException cnfex) {
						jTextArea1.append("\nUnknown object type received");
					}
				} while (!message.equals("CLIENT>>> BYE"));

				jTextArea1.append("\nUser terminated connection");
				jTextField1.setEnabled(false);
				//Step 5
				output.close();
				input.close();
				connection.close();
				++counter;
			}
		} catch (EOFException eof) {
			System.out.println("Client terminated connection");
		} catch (IOException io) {
			io.printStackTrace();
		}

	}

	private void createUI() {
		setLayout(new BorderLayout());
		jPanel1 = new JPanel();
		jTextField1 = new JTextField();
		jButton1 = new JButton();
		jScrollPane1 = new JScrollPane();
		jTextArea1 = new JTextArea();
		jPanel1.setLayout(new BorderLayout());

		jTextField1.setColumns(30);
		jTextField1.setFont(new java.awt.Font("Times New Roman", 1, 24));
		jTextField1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				sendData(evt);
			}
		});
		jPanel1.add(jTextField1, java.awt.BorderLayout.CENTER);

		jButton1.setFont(new java.awt.Font("Times New Roman", 1, 24));
		jButton1.setText("Send");
		jButton1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				sendData(evt);
			}
		});
		jPanel1.add(jButton1, java.awt.BorderLayout.EAST);
		add(jPanel1, java.awt.BorderLayout.NORTH);
		jTextArea1.setColumns(20);
		jTextArea1.setFont(new java.awt.Font("Times New Roman", 1, 24));
		jTextArea1.setRows(5);
		jScrollPane1.setViewportView(jTextArea1);
		add(jScrollPane1, java.awt.BorderLayout.CENTER);
	}

	private void sendData(java.awt.event.ActionEvent evt) {
		try {
			output.writeObject("SERVER>>> " + jTextField1.getText());
			output.flush();
			jTextArea1.append("\nSERVER>>>" + jTextField1.getText());
			jTextField1.setText("");
		} catch (IOException cnfex) {
			jTextArea1.append("\nError writing object");
		}
	}

	public static void main(String args[]) {
		TCPServer ts = new TCPServer();
		Thread t = new Thread(ts);
		t.start();                 //run From ts (Tcs server) 
		JFrame f = new JFrame("Jain Nikhil: TCP Server");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setContentPane(ts);
		f.pack();
		f.setLocationRelativeTo(null);
		f.setVisible(true);

	}

}
