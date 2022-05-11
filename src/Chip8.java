

import java.io.*;




public class Chip8 {
	
	
	
	public static void main(String[] args) {
		
		Processor ch8 = new Processor();
		Screen scr = new Screen();
		Beep beep = new Beep();
		
		String filename = "C:\\Users\\lidfun\\Documents\\EclipseProjects\\Chip8Emulator\\c8games\\";  //   TETRIS INVADERS 
		ch8.readRom(filename);
		
		scr.initializeScreen();
	
		while(true) {		
			
			try
			{
			    Thread.sleep(scr.speed);
			    
			    //Fetch
			    short opcode = ch8.fetch();
				//Decode and Execute;
				ch8.runOpcode(opcode);
				
				//Delay and Sound Timers
				ch8.delayAndSound();
				
				
				//Draw
				if(ch8.drawFlag == true) {
					scr.setDisplay(ch8.getDisplay());
					scr.paintScreen();
					ch8.drawFlag = false;
				}
				
				//Set Keyboard to Processor
				ch8.setKeys(scr.k.getKeys());
			    
			  
			}
			catch(InterruptedException ex){
			    Thread.currentThread().interrupt();
			}
			
			
			
		}
		
		
		
	}
	
	
}