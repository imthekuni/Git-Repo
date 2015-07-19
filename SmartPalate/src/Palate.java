import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class Palate extends JPanel{
	
	// Call CSVReader
	int [][] coordArray = CSVRead.electrodeArray(null);
	
	// Get array dimensions
	int width = CSVRead.getWidth();
	int rows = CSVRead.getRows();
	
	Graphics g = null;
	Point center;
	Color c = Color.BLUE;
	
	int radOff = 0; // radius of inactive circle
	int radOn = 10; // radius of activated circle
	Image backgroundImage;

	int i = 0; // Row numerator
	int j = 0; // Electrode numerator
	int x;
	int y;
	int k = 0; // Active electrode numerator
	int electrodeNum; // Electrode number
	
	public Palate(){
		setBackground(Color.WHITE);
		addMouseListener(new HandleMouse());
	}
	
	class HandleMouse extends MouseAdapter implements Runnable{
		
		Thread mappingArray = null;
		
		public void mousePressed(MouseEvent e){
			System.out.println("Mouse Pressed");
			mappingArray = new Thread(this);
			mappingArray.start();
		}
		
		public void run(){
			System.out.println("Inside run()");
			for(i = 0; i < rows-1; i++){
				System.out.println("Row: " + i);
				for(j = 0; j < width; j++){
					//System.out.print(coordArray[i][j] + " ");
					if(coordArray[i][j] == 1){ // If electrode activated
						//k++; // Increment electrode numerator
						x = GUI.electrodeCoords[j][0]; // Set electrode x-coordinate
						y = GUI.electrodeCoords[j][1]; // Set electrode y-coordinate
						//electrodeNum = GUI.electrodeCoords[1][2]; System.out.println("Electrode #:" + electrodeNum);						
						//g.fillOval(x, y, radOn, radOn);
						circleDraw( x, y, radOn, radOn);
						//repaint();
					}
				}
				try{
					Thread.sleep(100);
				}catch(InterruptedException e){}
				
				repaint();
			}
			System.out.println("Done!");
		}
	}

	public void circleDraw(int x, int y, int radius1, int radius2){
		System.out.println("Inside circleDraw");
		System.out.println("x: " + x);
		System.out.println("y: " + y);
		System.out.println("Radius1: " + radius1);
		System.out.println("Radius2: " + radius2);
		g.fillOval(x, y, radius1, radius2);
	}
	
	public void drawFillCircle(Graphics g, Color c, int x, int y, int radius){
		//System.out.println("Inside drawFillCircle()");
		g.setColor(c);
		//System.out.println("After setColor");
		g.fillOval(x, y, radOn, radOn);
	}
	
	public void paintComponent(Graphics g){
		//System.out.println("Inside paintComponent");
		drawFillCircle(g, Color.BLUE, x, y, radOn);
	}
	
	public void update(Graphics g){
		//System.out.println("Inside update()");
		paintComponent(g);
	}
	
	/*
	// Below here check ----------------------------------------------------------------------
	private JPanel box;
	
	// Call CSVReader
	int [][] coordArray = CSVRead.electrodeArray(null);
	
	// Get array dimensions
	int width = CSVRead.getWidth();
	int rows = CSVRead.getRows();
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);

		BufferedImage backgroundImage = null;
		try{
			backgroundImage = ImageIO.read(new File("/Users/kuni/Dropbox/CSULB/Class/2014 - Spring/FCS 597/Smart Palate/SmartPalate/src/images/ElectrodeTeeth.jpg"));
		} catch(IOException e){
			System.out.print("Image not Found");
		}
		
		g.drawImage(backgroundImage, 0, 0, null); // Set background image		
		
		g.setColor(Color.BLUE);
		g.fillOval(x, y, radOn, radOn); // Draw a filled circle at set x,y coordinates
		
		
		// Read data from palateData and plot on JPanel
		for(i = 0; i < rows; i++){
			for(j = 0; j < width; j++){
				System.out.print(coordArray[i][j] + " ");
				if(coordArray[i][j] == 1){ // If electrode activated
					x = GUI.electrodeCoords[j][0]; // Set electrode x-coordinate
					y = GUI.electrodeCoords[j][1]; // Set electrode y-coordinate
					//electrodeNum = GUI.electrodeCoords[1][2]; System.out.println("Electrode #:" + electrodeNum);
					//g.fillOval(x, y, radOn, radOn); // Draw a filled circle at set x,y coordinates
				}
			}
			System.out.println("");
			
			try{
				Thread.sleep(100);
			}catch(InterruptedException e){
			}
		}
	}*/
}
