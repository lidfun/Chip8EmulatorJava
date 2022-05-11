
import java.awt.*;
import java.awt.event.KeyEvent;

public class Keyboard {
	private boolean[] keys;
	private int currentKey;
	
	public Keyboard() {
		this.keys = new boolean[16];
		for(boolean k: keys) {
			k = false;
		}
		this.currentKey = 0;
	}
	
	public void setCurrentKey(int c) {
		this.currentKey = c;
	}
	public boolean[] getKeys() {
		return this.keys;
	}
	
	public void printKeys() {
		System.out.println("CURRENT KEY: "+this.currentKey);
		  for(int i = 0;i<keys.length;i++) {
			  System.out.print(i+":"+keys[i]+" ");
		  }
		  System.out.println("");
		  System.out.println("");
	}
	
	
	
	public boolean setKey(boolean value, int key) {
		switch(key) {
			case KeyEvent.VK_1:
				keys[0] = value;
				break;
			case KeyEvent.VK_2:
				keys[1] = value;
				break;
			case KeyEvent.VK_3:
				keys[2] = value;
				break;
			case KeyEvent.VK_4:
				keys[3] = value;
				break;
			case KeyEvent.VK_Q:
				keys[4] = value;
				break;
			case KeyEvent.VK_W:
				keys[5] = value;
				break;
			case KeyEvent.VK_E:
				keys[6] = value;
				break;
			case KeyEvent.VK_R:
				keys[7] = value;
				break;
			case KeyEvent.VK_A:
				keys[8] = value;
				break;
			case KeyEvent.VK_S:
				keys[9] = value;
				break;
			case KeyEvent.VK_D:
				keys[10] = value;
				break;
			case KeyEvent.VK_F:
				keys[11] = value;
				break;
			case KeyEvent.VK_Z:
				keys[12] = value;
				break;
			case KeyEvent.VK_X:
				keys[13] = value;
				break;
			case KeyEvent.VK_C:
				keys[14] = value;
				break;
			case KeyEvent.VK_V:
				keys[15] = value;
				break;
			default:
				return false;
		}
		
		return true;
	}
	
	
}
