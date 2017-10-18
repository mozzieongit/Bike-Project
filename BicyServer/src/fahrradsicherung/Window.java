package fahrradsicherung;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class Window extends JFrame{

	public static final int MAX_HEIGHT_FOR_COMPONENTS = 10000;
	public static final int MAX_WIDTH_FOR_COMPONENTS = 10000;
	
	//53.53 8.61
	
	public Window(Server server){
		setWindowSettings(server.getPort());
		addComponents();
		initFunctions(server);

		setVisible(true);
		setFocusable(true);
		setAlwaysOnTop(true);
	}
	
	
	private void setWindowSettings(int port ){
		Dimension windowSize = new Dimension(800,600);
		setPreferredSize(new Dimension(windowSize.width, windowSize.height));
		setSize(windowSize);
		setMinimumSize(new Dimension(400, 300));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screenSize.width - windowSize.width) / 2,(screenSize.height - windowSize.height) / 2);
		
		InetAddress ip = null;
		try {
			ip = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		setTitle("Server: " + ip.getHostAddress() + " : " + port);
	}
	
	
	private JPanel pConsole;
	private JPanel pThreads;
	private JList<String> lClients;
	private DefaultListModel<String> model = new DefaultListModel<>();
	private JTextPane areaConsole;
	private JTextField tfCommand;
	private JButton btnSend;
	
	private Font mainTextFont = new Font("Segoe UI", 1 , 18);
	private Font mainTextFontSmall = new Font("Segoe UI", 1 , 14);
	
	private HashMap<Color,SimpleAttributeSet> colors = new HashMap<Color,SimpleAttributeSet>();
	
	public static final Color DARK_GREEN = new Color(0, 127, 0);
	
	private void addComponents(){
		
		addColor(Color.BLACK);
		addColor(Color.RED);
		addColor(Color.GREEN);
		addColor(DARK_GREEN);
		addColor(Color.CYAN);
		addColor(Color.BLUE);
		addColor(Color.GRAY);
		addColor(Color.DARK_GRAY);
		addColor(Color.LIGHT_GRAY);
		addColor(Color.ORANGE);
		addColor(Color.YELLOW);
		addColor(Color.MAGENTA);
		addColor(Color.PINK);
		addColor(Color.WHITE);

		////////////////////////////////////
		//WEST
		lClients = new JList<String>(model);
		lClients.setPreferredSize(new Dimension(200,MAX_HEIGHT_FOR_COMPONENTS));
		add(lClients,BorderLayout.WEST);
		//WEST
		////////////////////////////////////
		
		////////////////////////////////////
		//CENTER
		JTabbedPane jPane = new JTabbedPane();
		
		pConsole = new JPanel(new BorderLayout());
		

		
		areaConsole = new JTextPane();
		areaConsole.setEditable(false);
		areaConsole.setFont(mainTextFontSmall);
		pConsole.add(new JScrollPane(areaConsole),BorderLayout.CENTER);
		
		JPanel pComandPane = new JPanel(new BorderLayout());
		pComandPane.setPreferredSize(new Dimension(MAX_WIDTH_FOR_COMPONENTS, 30));
		tfCommand = new JTextField();
		tfCommand.setUI(new JTextFieldHintUI("Text...", Color.gray));
		tfCommand.setFont(mainTextFont);
		
		btnSend = new JButton("Senden");
		btnSend.setPreferredSize(new Dimension(100, MAX_HEIGHT_FOR_COMPONENTS));

		pComandPane.add(tfCommand,BorderLayout.CENTER);
		pComandPane.add(btnSend,BorderLayout.EAST);
		
		pConsole.add(pComandPane,BorderLayout.SOUTH);
		
		pThreads = new JPanel(new BorderLayout());
		jPane.addTab("Console", pConsole);
		jPane.addTab("Threads", pThreads);
		
		add(jPane,BorderLayout.CENTER);
		//CENTER
		////////////////////////////////////
	}
	
	private void initFunctions(Server server){
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				addText(DARK_GREEN,"SERVER",tfCommand.getText() + "\n");
				server.broadcast(tfCommand.getText());
				tfCommand.setText("");
			}
		});
		
		tfCommand.addKeyListener(new KeyListener() {
		
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER){

					addText(DARK_GREEN,"SERVER",tfCommand.getText() + "\n");
					server.broadcast(tfCommand.getText());
					tfCommand.setText("");
				}
				
			}

			@Override
			public void keyTyped(KeyEvent e) {
				
			}

			@Override
			public void keyPressed(KeyEvent e) {
				
			}
		});
		
		lClients.addMouseListener(new MouseListener() {
			
			public void mouseReleased(MouseEvent e) {
			}
			
			public void mousePressed(MouseEvent e) {
			}
			
			public void mouseExited(MouseEvent e) {
			}
			
			public void mouseEntered(MouseEvent e) {
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2){
					int SelectedID = lClients.getSelectedIndex();
					
				}
			}
		});
	}
	
	public void addText(Color c,String von,String text){
		if(von.length() != 0){
		    try {
				areaConsole.getDocument().insertString(areaConsole.getDocument().getLength(),"[" + von + "]: ",colors.get(c) );
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
	    try {
	    	areaConsole.getDocument().insertString(areaConsole.getDocument().getLength(), text,colors.get(Color.black) );
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	public int addClient(String name){
		model.addElement(name);
		return model.size() -1;
	}
	
	public void removeClient(int ID){
		model.remove(ID);
		
	}
	
	
	private void addColor(Color color){
		SimpleAttributeSet c = new SimpleAttributeSet();
		StyleConstants.setFontFamily(c, "Segoe UI");
		StyleConstants.setForeground(c, color);
		colors.put(color, c);
	}
	
	public void setName(int ID,String name){
		model.remove(ID);
		model.insertElementAt(name, ID);
		
	}
	
	
	
	
}
