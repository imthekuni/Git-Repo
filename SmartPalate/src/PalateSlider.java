import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.JPanel;



public class PalateSlider extends JPanel{
	
	public static Object slideLock = new Object();
	public static Object drawWait = new Object();
	
	public static int activeCoords[][] = {{263,67,1},{182,67,2},{262,96,3},{236,94,4},{209,94,5},{184,96,6},{284,125,7},{260,119,8},{236,117,9},{209,117,10},{184,119,11},{161,125,12},{307,157,13},{282,145,14},{259,139,15},{236,135,16},{209,135,17},{185,139,18},{162,145,19},{139,157,20},{304,175,21},{280,164,22},{257,158,23},{236,156,24},{209,156,25},{186,158,26},{163,164,27},{142,175,28},{300,193,29},{277,185,30},{256,179,31},{235,176,32},{209,176,33},{186,179,34},{165,185,35},{142,193,36},{297,209,37},{277,202,38},{255,198,39},{234,196,40},{209,196,41},{187,198,42},{163,201,43},{144,209,44},{256,218,45},{233,218,46},{210,218,47},{187,218,48},{233,238,49},{210,238,50},{329,233,51},{311,233,52},{293,233,53},{146,233,54},{128,233,55},{110,233,56},{338,255,57},{320,255,58},{302,255,59},{141,255,60},{123,255,61},{105,255,62},{337,278,63},{319,278,64},{301,278,65},{139,278,66},{121,278,67},{103,278,68},{354,339,69},{339,299,70},{321,299,71},{303,299,72},{285,299,73},{157,299,74},{139,299,75},{121,299,76},{103,299,77},{84,339,78},{354,360,79},{340,317,80},{322,317,81},{304,317,82},{286,317,83},{268,317,84},{232,317,85},{210,317,86},{174,317,87},{156,317,88},{138,317,89},{120,317,90},{102,317,91},{84,360,92},{336,339,93},{318,339,94},{300,339,95},{282,339,96},{264,339,97},{246,339,98},{228,339,99},{221,299,100},{221,260,101},{210,339,102},{192,339,103},{174,339,104},{156,339,105},{138,339,106},{120,339,107},{102,339,108},{336,360,109},{318,360,110},{300,360,111},{282,360,112},{264,360,113},{246,360,114},{221,286,115},{221,273,116},{228,360,117},{210,360,118},{192,360,119},{174,360,120},{156,360,121},{138,360,122},{120,360,123},{102,360,124}};
	
	
	// Call CSVReader
	public static int [][] coordArray = CSVRead.electrodeArray(null);
	
	// Get array dimensions
	int width = CSVRead.getWidth();
	//int rows = CSVRead.getRows();
	
	int radOn = 15; // radius of activated circle
	BufferedImage backgroundImage = null;

	public static int i; // Row iterator
	static int k = 0; // Active electrode iterator

	long startTime;
	long stopTime;
	long totalTime;
	boolean drawn = false;
	static int l;
	public static int _i;	
	
	public static ArrayList<Point> electrodeCoords = new ArrayList<Point>();
	public static Point center;
	private final ScheduledExecutorService paintScheduler = Executors.newScheduledThreadPool(1);
	
	public PalateSlider(){

		setBackground(Color.WHITE);
		addMouseListener(new HandleMouse());
		
		try{
			backgroundImage = ImageIO.read(getClass().getResource("/images/ElectrodeTeeth.jpg"));
		} catch(IOException e){System.out.print("Image not Found");}
	}

	class HandleMouse extends MouseAdapter{
		
		private final ScheduledExecutorService paintScheduler = Executors.newScheduledThreadPool(1);
		private final ExecutorService iterateService = Executors.newFixedThreadPool(1);		
		
		// Schedule a repaint every 10 milliseconds	
		public void paintScheduler(){
			final Runnable painter = new Runnable(){
				public void run(){
					repaint();
					
					iterateService.execute(new Runnable(){
						
						public void run(){
							
							if(i == CSVRead.rows){
								paintScheduler.shutdown();
							}
							
							//System.out.print("i: " + i);
							Menu.audioSlide.setValue(i);

							
							//------------------------------------------------------------------------------
							//---------- Can we iterate through and get points prior to playing? -----------
							//------------------------------------------------------------------------------
							k = 0;
							for(int j = 0; j < CSVRead.width; j++){
								
								if(coordArray[i][j] == 1){

									center = new Point();
									center.setLocation(activeCoords[j][0], activeCoords[j][1]);
									electrodeCoords.add(k, center);
									k++;
								}
							}
							System.out.println("electrodeCoords(schedule): " + electrodeCoords);
							i++;
						}
					});
				}
			};
			
			final ScheduledFuture<?> painterHandle = paintScheduler.scheduleAtFixedRate(painter, 0, 10000000, TimeUnit.NANOSECONDS);
		}
		
		Thread mappingArray = null;
		
		public void mousePressed(MouseEvent e){
			
			
			startTime = System.currentTimeMillis();
			//Menu.playSound.start();
			Menu.audioClip.start();
			
			mappingArray = new Thread(new Runnable(){
				public void run(){
					
					//--------Prevent multiple instances of thread?------------
					// Possibly using threadpool?
					
					Menu.playOff();
					
					paintScheduler();
					
					stopTime = System.currentTimeMillis();
					totalTime = (stopTime - startTime);
					System.out.println("Time Elapsed: " + totalTime + " s");
				}
			});
			mappingArray.start();
		}
	}
	
	public void paintComponent(Graphics g){
		//System.out.println("repainted");
		g.drawImage(backgroundImage, 0, 0, null);
		g.setColor(Color.BLUE);
		
		System.out.println("electrodeCoords(paint): " + electrodeCoords);
		/*
		for(int _k = 0; _k < k; _k++){
			g.fillOval(Menu.electrodeCoords.get(_k).x, electrodeCoords.get(_k).y, radOn, radOn);
		}*/
		drawn = true;
		//g.drawString(Integer.toString(i), 20, 20);
		//g.drawString("Total Time: " + Long.toString(totalTime) + " ms", 20, 50);
		//g.drawString("Comparison: " + Comparison.sound, 20, 80); 
	}
	
	public static void updateRowCount(int _i){
		i = _i;
	}
	
	public static void arraySeek(){
		Thread arraySeek = new Thread(new Runnable(){
			public void run(){
				k = 0;

				for(int j = 0; j < CSVRead.width; j++){
					if(coordArray[i][j] == 1){
						
						center = new Point();
						center.setLocation(activeCoords[j][0], activeCoords[j][1]);
						electrodeCoords.add(k, center);
						k++;
					}
				}
			}
		});
		arraySeek.start();
	}
}
