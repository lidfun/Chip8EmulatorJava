import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;


public class Beep {
	
	
	String beep = "C:\\Users\\lidfun\\Documents\\EclipseProjects\\Chip8Emulator\\beep-07.wav";
	
	public Beep(){
		
	}
	

	
	public synchronized void play()
    {
        // Note: use .wav files            
        new Thread(new Runnable() {
            public void run() {
                try {
                    Clip clip = AudioSystem.getClip();
                    AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File(beep));
                    clip.open(inputStream);
                    clip.start();
                } catch (Exception e) {
                    System.out.println("play sound error: " + e.getMessage() + " for " + beep);
                }
            }
        }).start();
    }
}
