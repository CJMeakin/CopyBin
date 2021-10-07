package window;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
 
public class window extends JFrame{
	
	static JFrame frame = new JFrame("Dria's CopyBin v3.6");
	
	//Listeners
	static ActionListener al;
	static KeyListener kl;
	
	//Panels
	static JPanel OverPane;
	static JPanel copyPastesGridpnl;
	static JPanel[] copyPasteContainer;	
	
	//Buttons
	static JButton addNewPasteablebtn;
	static JButton[] copyPastebtn;
	static JButton[] copyPasteDeletebtn;
	
	//Textfield
	static JTextField[] copyPastetxt;
	
	static JCheckBox onTopckbox;
	
	static public int currPasteButton = 0;
	static public int availiblePastes[] = {0,0,0,0,0,0,0,0,0,0};	
	public void start() {
		
		buildActionListeners();
		buildKeyListeners();
		initComponents();
		addComponents();
		addListeners();
		listenToWindow();

		List<Image> icons = new ArrayList<Image>();
		icons.add(new ImageIcon(getClass().getResource("/itslit16x16.png")).getImage());
		//	icons.add(new ImageIcon("res/itslit16x16.png").getImage());
		icons.add(new ImageIcon(getClass().getResource("/itslit32x32.png")).getImage());	
		frame.setIconImages(icons);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(50, 50, 630, 500);
		frame.setResizable(false);
		frame.setVisible(true);
		
	}
	public static void initComponents(){
		//Panels
		OverPane = new JPanel();
		OverPane.setLayout(new BorderLayout());
		copyPastesGridpnl = new JPanel();
		copyPastesGridpnl.setLayout(new GridLayout(10,3));
		copyPasteContainer = new JPanel[10];
		for(int i = 0 ; i < 10; i++){
			copyPasteContainer[i] = new JPanel();
			copyPasteContainer[i].setLayout(new FlowLayout());
			copyPasteContainer[i].setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
			copyPasteContainer[i].setBackground(new Color(200,200,200));
		}		
		//Buttons
		addNewPasteablebtn = new JButton("New Paste");
		addNewPasteablebtn.setToolTipText("Add a new Paste Slot");
		copyPastebtn = new JButton[10];
		copyPasteDeletebtn = new JButton[10];
		for(int i = 0 ; i < 10; i++){
			if(i < 9)
			copyPastebtn[i] = new JButton("Copy (Ctrl + " + (i+1) +")");
			else
				copyPastebtn[i] = new JButton("Copy (Ctrl + 0)");
			copyPasteDeletebtn[i] = new JButton("Remove");
			copyPastebtn[i].setToolTipText("Click to copy this paste, or press Ctrl + " + ((i < 9) ? i+1 : 0));
			copyPasteDeletebtn[i].setToolTipText("Click to remove this paste");
		}
		//text fields
		copyPastetxt = new JTextField[10];
		for(int i = 0 ; i < 10; i++){
			copyPastetxt[i] = new JTextField();
			copyPastetxt[i].setPreferredSize(new Dimension(400,27));
		}
		onTopckbox = new JCheckBox("Always on top?");
		onTopckbox.setToolTipText("Check to keep this window on top");
	}
	public static void addComponents(){
		
		OverPane.add(addNewPasteablebtn, BorderLayout.NORTH);
		OverPane.add(onTopckbox, BorderLayout.SOUTH);
		OverPane.add(copyPastesGridpnl, BorderLayout.CENTER);
		for(int i = 0 ; i < 10; i++){
			copyPasteContainer[i].add(copyPastebtn[i]);						
			copyPasteContainer[i].add(copyPastetxt[i]);
			copyPasteContainer[i].add(copyPasteDeletebtn[i]);
		}
		frame.add(OverPane);
	}
	public static void setPastable(int i){
		StringSelection stringSelection = new StringSelection(copyPastetxt[i].getText());
		Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
		clpbrd.setContents(stringSelection, null);
		for(int n = 0 ; n < 10; n++)
			copyPasteContainer[n].setBackground(new Color(200,200,200));
		copyPasteContainer[i].setBackground(new Color(51, 153, 102));
	}
	private static void addListeners(){
		//Buttons
		addNewPasteablebtn.addActionListener(al);
		onTopckbox.addActionListener(al);
		for(int i = 0 ; i < 10; i++){
			copyPasteDeletebtn[i].addActionListener(al);
			copyPastebtn[i].addActionListener(al);
		}
	}
	private static void buildActionListeners(){
		al = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getSource() ==  addNewPasteablebtn && currPasteButton < 10){ //add a new paste
					int i;
					for(i = 0 ; i < 10; i++)
						if(availiblePastes[i] == 0)
							break;
					availiblePastes[i] = 1;
					
					copyPastesGridpnl.add(copyPasteContainer[i]);
					copyPastetxt[i].requestFocus();
					currPasteButton++;
				}
				for(int i = 0; i < 10; i++)
				if(e.getSource() == copyPasteDeletebtn[i]){ //remove a paste
					copyPastetxt[i].setText("");
					copyPastesGridpnl.remove(copyPasteContainer[i]);
					currPasteButton--;
					availiblePastes[i] = 0;
					copyPasteContainer[i].setBackground(new Color(200,200,200));
				}
				for(int i = 0; i < 10; i++)
					if(e.getSource() == copyPastebtn[i]) //copy a paste
						setPastable(i);
				for(int i = 0; i < 10; i++)
					if(e.getSource() == copyPastetxt[i])
						copyPastetxt[i].setText("");
				if(e.getSource() == onTopckbox){
					if(onTopckbox.isSelected())
						frame.setAlwaysOnTop(true);
					else
						frame.setAlwaysOnTop(false);
				}
				OverPane.revalidate();
				OverPane.repaint();	
			}
		};
	}
	private  void getSavedPastes() throws IOException{
		File pastes = new File(System.getProperty("user.home") + File.separator + "Documents"+ File.separator +"Dria Tools"+ File.separator +"CopyBin"+ File.separator +"pastes.txt");
		
		if(pastes.exists()){
			Scanner scn = new Scanner(pastes);
			String paste = "";
			for(int i = 0 ; i < 10; i++){
				paste = scn.nextLine();
				if(paste.length() > 0){
					availiblePastes[i] = 1;
					copyPastetxt[i].setText(paste);
					copyPastesGridpnl.add(copyPasteContainer[i]);
					currPasteButton++;
				}
			}
		}
		OverPane.revalidate();
		OverPane.repaint();	
	}
	private static void savePastes() throws IOException{
		File saveFile = new File(System.getProperty("user.home") + File.separator + "Documents/Dria Tools/CopyBin/pastes.txt");
		if (saveFile.exists()) {
		    System.out.println( "already exists");
		} else if (saveFile.getParentFile().mkdirs()) {
		    System.out.println("was created");
		} else {
		    System.out.println("was not created");
		}
		BufferedWriter bw = new BufferedWriter(new FileWriter(saveFile));
		bw.write("");
		for (int i = 0; i < 10; i++) {
			bw.write(""+copyPastetxt[i].getText());
			bw.newLine();
		}
		bw.close();
	}
	private static void buildKeyListeners(){
		KeyboardFocusManager.getCurrentKeyboardFocusManager()
		  .addKeyEventDispatcher(new KeyEventDispatcher() {
		      @Override
		      public boolean dispatchKeyEvent(KeyEvent e) {
		        if(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_1)
		        	setPastable(0);
		        else if(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_2)
		        	setPastable(1);
		        else if(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_3)
		        	setPastable(2);
		        else if(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_4)
		        	setPastable(3);
		        else if(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_5)
		        	setPastable(4);
		        else if(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_6)
		        	setPastable(5);
		        else if(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_7)
		        	setPastable(6);
		        else if(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_8)
		        	setPastable(7);
		        else if(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_9)
		        	setPastable(8);
		        else if(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_0)
		        	setPastable(9);
		        return false;
		      }
		});
	}
	private void listenToWindow(){
		frame.addWindowListener(new WindowListener() {
		      @Override
		      public void windowOpened(WindowEvent e) {
		        System.out.println("JFrame has  been  made visible first  time");
		        try {
					getSavedPastes();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		      }
		      @Override
		      public void windowClosing(WindowEvent e) {
		        System.out.println("JFrame is closing.");		        
		        try {
					savePastes();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		      }

		      @Override
		      public void windowClosed(WindowEvent e) {
		        System.out.println("JFrame is closed.");
		      }

		      @Override
		      public void windowIconified(WindowEvent e) {
		        System.out.println("JFrame is  minimized.");
		      }

		      @Override
		      public void windowDeiconified(WindowEvent e) {
		        System.out.println("JFrame is restored.");
		      }

		      @Override
		      public void windowActivated(WindowEvent e) {
		        System.out.println("JFrame is activated.");
		      }

		      @Override
		      public void windowDeactivated(WindowEvent e) {
		        System.out.println("JFrame is deactivated.");
		      }
		    });

		    // Use the WindowAdapter class to intercept only the window closing event
		    frame.addWindowListener(new WindowAdapter() {
		      @Override
		      public void windowClosing(WindowEvent e) {
		        System.out.println("JFrame is closing.");
		      }
		    });
	}
}
