import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

import javax.swing.*;  
public class Screen extends JPanel {  
    
	public static int display[][];
	private JFrame frame;
	int scale = 10;
	public Keyboard k = new Keyboard();
	
	private JComboBox cb;
	
	public int speed = 0;
	public String game = "";
	
	
	public Screen() {
		this.display  = new int[64][32];
	}
	
	public void paint(Graphics g){
		
		for(int i = 0;i<64;i++){
		    for(int j = 0;j<32;j++){
		    	if(display[i][j]==1) {
		    		g.setColor(Color.WHITE);
		    		
		    	}else {
		    		g.setColor(Color.BLACK);
		    	}
		    	
		   
		    	g.fillRect(i*scale, j*scale, scale, scale);
		    	
		    }
		}
			
	}
	
	
	public static void generatePixels() {
		for(int i = 0;i<64;i++){
			System.out.println("");
		    for(int j = 0;j<32;j++){
		    	
		    	
		    	Random r = new Random();
			
		    	
		        display[i][j] = r.nextBoolean() ? 1 : 0;;
		       
		        
		        
		    }
		}
	}
	
	public void paintScreen() {
		//frame.validate();
		frame.repaint();
	}
	
	public void initializeScreen() {
		
		frame= new JFrame();	
		frame.setTitle("Chip-8 Emulator");
		frame.getContentPane().add(new Screen());
		frame.setSize(657, 357);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);	
		frame.addKeyListener(listener);
		Dimension size = frame.getBounds().getSize();
		
		JFrame frame2 = new JFrame();
		frame2.setTitle("Select");
		frame2.setVisible(true);
		frame2.setSize(300,300);
		frame2.setLocation(frame.getX()+frame.getWidth(),frame.getY());
		
		String country[]={"","15PUZZLE", "BLINKY", "BLITZ", "BRIX", "CONNECT4", "GUESS", "CONNECT4",
		"GUESS", "HIDDEN", "IBM Logo.ch8","INVADERS","KALEID","MAZE","MERLIN","MISSILE","PONG",		
		"PONG2","PUZZLE","SYZYGY","TANK","TETRIS","TICTAC","UFO","VBRIX","VERS","WIPEOFF"
		};        
	    cb=new JComboBox(country);    
	    cb.setBounds(130, 50,90,20);    
	    frame2.add(cb);        
	    frame2.setLayout(null);    
	    frame2.setSize(400,500);    
	    frame2.setVisible(true);
	    
	    
	      
	}
	
	public void setDisplay(int display[][]) {
		this.display = display;
	}
	
	public void printScreen() {
		for(int i = 0;i<64;i++){
			System.out.println("");
		    for(int j = 0;j<32;j++){
		    
		        System.out.print(display[i][j]);
		        
		        
		    }
		}
	}
	
	  KeyListener listener = new KeyListener() {
		  
		  @Override
		   
		  public void keyPressed(KeyEvent event) {
		     // printEventInfo("Key Pressed", event);
			  
			  
			  
			  if(event.getKeyCode()==38) {
				  speed++;
			  } 
			  
			  if(event.getKeyCode()==40) {
				  if(speed>0) {
					  speed--;
				  }
			  }
			  k.setCurrentKey(event.getKeyCode());
			  k.setKey(true, event.getKeyCode());
			  
			  k.printKeys();
			
		  }
		   
		  @Override
		  public void keyReleased(KeyEvent event) {
			 // printEventInfo("Key Released", event);
			  
			 // k.setCurrentKey(event.getKeyCode());
			  k.setKey(false, event.getKeyCode());
			  k.setCurrentKey(0);
			  k.printKeys();
			  
			  
			
		  }
		   
		  @Override
		  public void keyTyped(KeyEvent event) {
		      //printEventInfo("Key Typed", event);
		
		  }
		   
		  private void printEventInfo(String str, KeyEvent e) {
		   
		      System.out.println(str);
		      int code = e.getKeyCode();
		      System.out.println("   Code: " + KeyEvent.getKeyText(code));
		      System.out.println("   Char: " + e.getKeyChar());
		   
		   
		  }
		   
		   
		    };
	
  }  