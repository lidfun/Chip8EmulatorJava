import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;




public class Processor {
	private byte memory[];
	private byte registers[];
	private int display[][];
	private short stack[];
	private boolean[] keyboard;
	private short I;
	private short pc;
	private byte sp;
	private byte delayTimer;
	public byte soundTimer;
	public boolean drawFlag;
	private Beep beep;
	private final byte[] fontset = {
            (byte)0xF0, (byte)0x90, (byte)0x90, (byte)0x90, (byte)0xF0, // 0
            (byte)0x20, (byte)0x60, (byte)0x20, (byte)0x20, (byte)0x70, // 1
            (byte)0xF0, (byte)0x10, (byte)0xF0, (byte)0x80, (byte)0xF0, // 2
            (byte)0xF0, (byte)0x10, (byte)0xF0, (byte)0x10, (byte)0xF0, // 3
            (byte)0x90, (byte)0x90, (byte)0xF0, (byte)0x10, (byte)0x10, // 4
            (byte)0xF0, (byte)0x80, (byte)0xF0, (byte)0x10, (byte)0xF0, // 5
            (byte)0xF0, (byte)0x80, (byte)0xF0, (byte)0x90, (byte)0xF0, // 6
            (byte)0xF0, (byte)0x10, (byte)0x20, (byte)0x40, (byte)0x40, // 7
            (byte)0xF0, (byte)0x90, (byte)0xF0, (byte)0x90, (byte)0xF0, // 8
            (byte)0xF0, (byte)0x90, (byte)0xF0, (byte)0x10, (byte)0xF0, // 9
            (byte)0xF0, (byte)0x90, (byte)0xF0, (byte)0x90, (byte)0x90, // A
            (byte)0xE0, (byte)0x90, (byte)0xE0, (byte)0x90, (byte)0xE0, // B
            (byte)0xF0, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0xF0, // C
            (byte)0xE0, (byte)0x90, (byte)0x90, (byte)0x90, (byte)0xE0, // D
            (byte)0xF0, (byte)0x80, (byte)0xF0, (byte)0x80, (byte)0xF0, // E
            (byte)0xF0, (byte)0x80, (byte)0xF0, (byte)0x80, (byte)0x80  // F
        };
	
	
	public Processor() {
		this.memory = new byte[4096];
		this.registers = new byte[16];
		this.I = 0x0000;
		this.pc = 0x200;
		this.stack = new short[16];
		this.sp = 0x00;
		this.delayTimer = 0;
		this.soundTimer = 0;
		this.keyboard = new boolean[16];
		this.display= new int[64][32];
		this.drawFlag = false;
		this.beep = new Beep();
		for(int i = 0;i<fontset.length;i++) {
			memory[50+i] = fontset[i];
		}
	}

	
	
	public void printState() {
		System.out.println("");
		System.out.println("PC: "+pc);
		System.out.print("Registers: ");
		for(int i = 0;i<registers.length;i++) {
			System.out.print("V"+i+":"+String.format("%02X",registers[i])+" ");
			
		}
		System.out.println("");
		System.out.println("I: "+String.format("%02X",I));
		System.out.println("SP: "+String.format("%02X",sp));
		
		System.out.print("Stack: ");
		for(int i = 0;i<stack.length;i++) {
			
			System.out.print(String.format(" "+"%02X",stack[i])+" ");
			
		}
		System.out.println("");
		System.out.println("");
	}
	
	
	
	public void readRom(String rom) {
		 File f = new File(rom);
		 FileInputStream fin = null;
		 int i = 0;
	     byte c;
	     
	     int index = pc;

	        try {
	           
	            fin = new FileInputStream(f);
	            while((i = fin.read())!=-1) {
	                c = (byte)i;
	                memory[index] = c;
	                index+=1;
	             }
	        }
	        catch (FileNotFoundException e) {
	            System.out.println("File not found" + e);
	        }
	        catch (IOException ioe) {
	            System.out.println("Exception while reading file " + ioe);
	        }
	        finally {
	            // close the streams using close method
	            try {
	                if (fin != null) {
	                    fin.close();
	                }
	            }
	            catch (IOException ioe) {
	                System.out.println("Error while closing stream: " + ioe);
	            }
	        }
	}
	
	
	public short fetch() {
		printState();
		
		byte first = memory[pc];
		byte second = memory[pc+1];
		return (short)  ((short)(first<<8) | (second & 0x00FF));
	}
	
	
	public void runOpcode(short opcode) {
		
		
		System.out.println("OPCODE: " + String.format("%02X",opcode));
		
		switch(opcode) {
			//CLS
			case 0x00E0:
				  for(int i = 0;i < 64; i++) {
		    		  for(int j = 0; j<32;j++) {
		    			  display[i][j] = 0;
		    		  }
		    	  }
				  pc+=2;
				  this.drawFlag = true;
				  return;
				  
			//RET
			case 0x00EE:
				pc = stack[sp--];
				drawFlag = true;
				pc += 2;
				return;      
		}
		
		
		
		switch(opcode & 0xF000) {
			
			//1nnn - JP addr
			case 0x1000:
				short location = (short) (opcode & 0xFFF);
				
				if(location < 0xFFF) {
					
					pc = location;
					
				} else {
					System.out.println("LOCATION OUT OF BOUNDS");
					pc+=2;
				}
					
				
				return;
					
			//2nnn - CALL addr
			case 0x2000:
				
				
				/*
				stack[sp] = pc;
				++sp;//maybe also increment before
				pc = (short) (opcode & 0x0FFF);
				return;
				*/
				stack[++sp]  = pc;
				pc = (short) (opcode & 0xFFF);
				
				return;
				
				
				
				
			//3xkk - SE Vx, byte
			case 0x3000:
				byte x = (byte) ((opcode & 0x0F00) >> 8);
				byte kk= (byte) (opcode & 0x00FF);
				
				if(registers[x]==kk) {
					pc += 2;
				}
				
				pc+=2;
				
				return;
				
			//4xkk - SNE Vx, byte
			case 0x4000:
				byte x1 = (byte) ((opcode & 0x0F00) >> 8);
				byte kk1 = (byte) (opcode & 0x00FF);
				
				if(registers[x1]!=kk1) {
					pc += 2;
				}
				
				pc+=2;
				return;
				
			//5xy0 - SE Vx, Vy
			case 0x5000:
				byte first = (byte) ((opcode & 0xF00)>>8);
				byte second =  (byte) ((opcode & 0x0F0)>>4);
				if(registers[first]==registers[second]) {
					pc += 2;
				}
				
				pc+=2;
				return;
				
			//6xkk - LD Vx, byte
			case 0x6000:
				byte reg = (byte) ((opcode & 0x0F00)>>8);
				byte value = (byte) ((opcode & 0x00FF));
				
				registers[reg] = value;
				pc+=2;
				return;
				
			//7xkk - ADD Vx, byte
			case 0x7000:
				byte reg2 = (byte) ((opcode & 0x0F00)>>8);
				byte value2 = (byte) ((opcode & 0x00FF));
				
				byte result = (byte) (registers[reg2] + value2);
				
				if (result >= 256) {
		            registers[reg2] = (byte) (result - 256);
		        } else {
		            registers[reg2] = result;
		        }	
				
				
				
				
				pc+=2;
				return;
				
			//Annn - LD I, addr
			case 0xA000:
				short reg1 = (short) (opcode & 0x0FFF);
				
				I = (reg1);
			
				pc+=2;
				return;
				
			//Bnnn - JP V0, addr
			case 0xB000:
				byte reg11 = (byte) (opcode & 0x0FFF);
				pc = (short) (reg11 + registers[0]);
				return;
				
			//Cxkk - RND Vx, byte
			case 0xC000:
				byte reg22 =  (byte) ((opcode & 0x0F00)>>8);
				byte kk2 = (byte) (opcode & 0x00FF);
				
				registers[reg22] = (byte) (new Random().nextInt(256) & kk2);
				pc+=2;
				return;
				
			//Dxyn - DRW Vx, Vy, nibble
			case 0xD000:
				
				byte n = (byte) (opcode & 0x000F);
				byte xcor = registers[((opcode & 0x0F00) >>8)];
				byte ycor = registers[((opcode & 0x00F0)>>4)];
				
				registers[0xF] = 0;
				
				//int xcoordinate = xcor % 64;
				//int ycoordinate = ycor % 32;
				
				
				//i = row, j = column
				for(int i = 0;i<(int)n;i++) {
					
					byte sprite = memory[I + i];
					
					for(int j = 0; j<8; j++) {
						if( ( sprite & (0x80>>j) ) != 0) {
							
							int xcoordinate = Math.abs((xcor + j)%64);//64
							int ycoordinate = Math.abs((ycor + i)%32);//32
							
							System.out.println("xc: " + String.format("%02X",xcoordinate));
							System.out.println("yc: " + String.format("%02X",ycoordinate));
							
							
							if(xcoordinate < 64 && ycoordinate < 32) {
								if(display[xcoordinate][ycoordinate]==1) {
									registers[0xF] = 1;
								} 
								
								display[xcoordinate][ycoordinate] ^= 1;
								
							}
						}
						
						
					}
				}
				
				
				
				drawFlag = true;
				pc+=2;
				return;
			
		}
		
		
		switch(opcode & 0xF00F) {
			//8xy0 - LD Vx, Vy
			case 0x8000:
				byte vx  = (byte) ((opcode & 0x0F00)>>8);
				byte vy = (byte) ((opcode & 0x00F0)>>4);
				registers[vx] = registers[vy];
				pc+=2;
				return;
				
			//8xy1 - OR Vx, Vy
			case 0x8001:
				byte vx2  = (byte) ((opcode & 0x0F00)>>8);
				byte vy2= (byte) ((opcode & 0x00F0)>>4);
				registers[vx2] = (byte) (registers[vx2] |  registers[vy2]);
				pc+=2;
				return;
				
			//8xy2 - AND Vx, Vy
			case 0x8002:
				byte vx3  = (byte) ((opcode & 0x0F00)>>8);
				byte vy3= (byte) ((opcode & 0x00F0)>>4);
				registers[vx3] = (byte) (registers[vx3] &  registers[vy3]);
				pc+=2;
				return;
				
			//8xy3 - XOR Vx, Vy
			case 0x8003:
				byte vx4  = (byte) ((opcode & 0x0F00)>>8);
				byte vy4= (byte) ((opcode & 0x00F0)>>4);
				registers[vx4] = (byte) (registers[vx4] ^  registers[vy4]);
				pc+=2;
				return;
				
			//8xy4 - ADD Vx, Vy
			case 0x8004:
				byte Vx =  (byte) ((opcode & 0x0F00) >> 8);
				byte Vy = 	(byte) ((opcode & 0x00F0) >> 4);
				
				byte sum =  (byte) (registers[Vx] + registers[Vy]);
				
				if(sum>0xFF) {
					registers[0xF] = (byte) 0x01;
				} 
				else {
					registers[0xF] = (byte) 0x00;
				}
				registers[Vx] = (byte) (sum & 0xFF);
				pc+=2;
				return;
				
			//8xy5 - SUB Vx, Vy
			case 0x8005:
				byte Vx2 =  (byte) ((opcode & 0x0F00) >> 8);
				byte Vy2 = 	(byte) ((opcode & 0x00F0) >> 4);
				
				if(registers[Vx2] > registers[Vy2]) {
					registers[0xF] = (byte) 0x01;
				}
				else {
					registers[0xF] = (byte) 0x00;
				}
				
				registers[Vx2] = (byte) ((registers[Vx2] - registers[Vy2]) & 0xFF);
				pc+=2;
				return;
				
			//8xy6 - SHR Vx {, Vy}
			case 0x8006:
				byte Vx3 =  (byte) ((opcode & 0x0F00) >> 8);
				registers[0xF] = (byte) ((registers[Vx3] & 0x1) == 1 ? 1 : 0);
				registers[Vx3] = (byte) ((registers[Vx3]>> 1) & 0xFF); //mabye no shift 
				pc+=2;
				return;
				
			//8xy7 - SUBN Vx, Vy
			case 0x8007:
				byte Vx4 =  (byte) ((opcode & 0x0F00) >> 8);
				byte Vy4 = 	(byte) ((opcode & 0x00F0) >> 4);
				
				if(registers[Vy4]>registers[Vx4]) {
					registers[0xF] = (byte) 0x01;
				} 
				else {
					registers[0xF] = (byte) 0x00;
				}
				
				registers[Vx4] = (byte) ((registers[Vy4] - registers[Vx4]) & 0xFF);
				pc+=2;
				return;
				
			//8xyE - SHL Vx {, Vy}
			case 0x800E:
				byte Vx5 =  (byte) ((opcode & 0x0F00) >> 8);
				registers[0xF] = (byte) ((registers[Vx5] >>> 7) == 0x1 ? 1 : 0);
				registers[Vx5] = (byte) ((registers[Vx5]<< 1) & 0xFF);
				pc+=2;
				return;
				
			//9xy0 - SNE Vx, Vy
			case 0x9000:
				byte Vx6 =  (byte) ((opcode & 0x0F00) >> 8);
				byte Vy6 = 	(byte) ((opcode & 0x00F0) >> 4);
				
				if(registers[Vx6]!=registers[Vy6]) {
					pc+=2;
				}
				pc+=2;
				return;
				
		}
		
		switch(opcode & 0xF0FF) {
			//Ex9E - SKP Vx
			case 0xE09E:
				byte rg = (byte) ((opcode & 0x0F00) >> 8);
				byte k = registers[rg];
				
				if( keyboard[k]  ) {
					pc+=4;
				}
				else {
					pc+=2;
				}
				return;
				
			//ExA1 - SKNP Vx
			case 0xE0A1:
				byte rg2 = (byte) ((opcode & 0x0F00) >> 8);
				byte k2 = registers[rg2];
				if( !keyboard[k2] ) {
					pc+=4;
				}
				else {
					pc+=2;
				}
				return;
				
			//Fx07 - LD Vx, DT
			case 0xF007:
				byte rg3 = (byte) ((opcode & 0x0F00) >> 8);
				registers[rg3] = (byte) (delayTimer & 0xFF);
				pc+=2;
				return;
			
			//Fx0A - LD Vx, K
			case 0xF00A:
				byte rg4 = (byte) ((opcode & 0x0F00) >> 8);
				for(int i = 0;i<16;i++) {
					if(keyboard[i]) {
						registers[rg4] = (byte) i;
						pc+=2;
						return;
					}
				}
				return;
			
			//Fx15 - LD DT, Vx
			case 0xF015:
				byte rg5 = (byte) ((opcode & 0x0F00) >> 8);
				delayTimer = registers[rg5];
				pc+=2;
				return;
			
			//Fx18 - LD ST, Vx
			case 0xF018:
				byte rg6 = (byte) ((opcode & 0x0F00) >> 8);
				soundTimer = registers[rg6];
				pc+=2;
				return;
			
			//Fx1E - ADD I, Vx
			case 0xF01E:
				byte rg7 = (byte) ((opcode & 0x0F00) >> 8);
				short total =(short) (I + registers[rg7]);
				if(total>0xFFF) {
					registers[0xF] = 1;
				} else {
					registers[0xF] = 0;
				}
				
				I =(short) (total & 0xFFF);
				pc+=2;
				return;
				//may change
			
			//Fx29 - LD F, Vx
			case 0xF029:
				byte rg8 = (byte) ((opcode & 0x0F00) >> 8);
				I = (short) (50+registers[rg8] * 5); //assuming I is set to correct initial address (fontset start address)
				pc+=2;
				return;
				
			//Fx33 - LD B, Vx
			case 0xF033:
				byte num = registers[ ((opcode & 0x0F00) >> 8)];
				memory[I] = (byte) (num/100);
				memory[I+1] = (byte) ((num%100) / 10);
				memory[I+2] = (byte) ((num%100) % 10);
				pc+=2;
				return;	
				
			//Fx55 - LD [I], Vx
			case 0xF055:
				byte num2 = (byte) ((opcode & 0x0F00) >> 8);
				
				for(int i = 0; i<=num2; i++) {
					memory[I+i] = registers[i];
				}
				pc+=2;
				return;
				
			//Fx65 - LD Vx, [I]
			case 0xF065:
				byte num3 = (byte) ((opcode & 0x0F00) >> 8);
				
				for(int i = 0;i<=num3;i++) {
					registers[i] = (byte) (memory[I+i] & 0xFF);
				}
				
				pc+=2;
				return;
				
			default:
				System.out.printf ("Unknown opcode [0x0000]: 0x%X\n", opcode);
				pc+=2;
				return;
				
		}
		
		
			
		
}
	
	
	
	
	
	public void delayAndSound() {
		if (delayTimer > 0) {
			 --delayTimer;
		}
	       
	    if (soundTimer > 0) {
	    	beep.play();
	        --soundTimer;
	    }
	    
	}
	
	public int[][] getDisplay() {
		return this.display;
	}
	
	public void setKeys(boolean[] keys) {
		this.keyboard = keys;
	}

}
