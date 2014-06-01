import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.String;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicSliderUI;

import biz.source_code.dsp.sound.AudioIo;
import biz.source_code.dsp.sound.AudioIo.AudioSignal;
import biz.source_code.dsp.swing.FunctionPlot;
import biz.source_code.dsp.swing.SignalPlot;

public class Menu extends JPanel implements ActionListener{
		
	public static int radOn = 15;
	public static int wavRatio;
	public static Color c = Color.BLUE;
	static int k = 0; // Active electrode iterator
	
	public static boolean drawn;
	public static boolean slideDone = true;
	public static boolean iterated;
	boolean isShowingMenu;
	boolean showMenu;
	
	public static String csvFile;
	public static String wavFile;

	public JPopupMenu menuPopup;
	public JButton playThroughButton;
	public JButton slideButton;
	private static JButton playButton;
	private static JButton pauseButton;
	private JButton menuButton;
	
	static JProgressBar palateProgress; 
	static JSlider palateSlide;
	static JSlider audioSlide;
	
	static ImageIcon playOn;
	static ImageIcon playOff;
	static ImageIcon pauseOn;
	static ImageIcon pauseOff;
	ImageIcon menu;
	UIDefaults defaults;
	
	public static ArrayList<Point> electrodeCoords = new ArrayList<Point>();
	public static Point center;
	
	static Clip audioClip;
	static int audioLength;
	
	public static Thread playSound = null;
	
	private static GridBagConstraints gbc;

	private Menu(){
		
		playThroughButton = new JButton("Play Smart Palate Data");
		playThroughButton.setHorizontalAlignment(SwingConstants.LEFT);
		playThroughButton.addActionListener(new PalatePlayAL());
		
		slideButton = new JButton("Play with Slider");
		slideButton.setHorizontalAlignment(SwingConstants.RIGHT);
		slideButton.addActionListener(new PalateSlideAL());
		
		add(playThroughButton);
		add(slideButton);
	}
	
	// Choose which operational mode to go into
	private static void chooseGUI(){

		JFrame menuFrame = new JFrame("Please select an operation mode");
		menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		menuFrame.setSize(400, 100);
		
		Menu palateMenu = new Menu();
		palateMenu.setOpaque(true);
		menuFrame.setContentPane(palateMenu);
		
		menuFrame.pack();
		menuFrame.setVisible(true);		
	}
	
	public static void main(String[] args){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				chooseGUI();
			}
		});
	}
	
	
	//--------------------------------------------------------
	//------------------ Action Listeners --------------------
	//--------------------------------------------------------
	
	// Action listener for play through
	class PalatePlayAL implements ActionListener{
		public void actionPerformed(ActionEvent e){
			
			JFrame palatePlayFrame = new JFrame("Smart Palate");
			palatePlayFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			Palate p = new Palate();
			palatePlayFrame.add(p);
			palatePlayFrame.setSize(800, 450);
			palatePlayFrame.setVisible(true);
		}
	}
	
	class PalateSlideAL extends JPanel implements ActionListener{
		
		
		public void actionPerformed(ActionEvent e){
			
			JFrame palateSlideFrame = new JFrame("Smart Palate");
			palateSlideFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			//palateSlideFrame.setLayout(new BorderLayout());
			
			try {
				palateSlideFrame.add(new buildFrame());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			palateSlideFrame.pack();
			palateSlideFrame.setLocationRelativeTo(null);
			palateSlideFrame.setSize(800, 670);
			palateSlideFrame.setVisible(true);
		}
		
		class buildFrame extends JPanel{
			
			private AudioSignal audioSignal;
			
			public buildFrame() throws Exception{
				
				setBackground(Color.WHITE);
				setOpaque(true);
				
				File[] fileName = FileBrowser.browser(null);
				csvFile = fileName[0].getAbsolutePath();
				wavFile = fileName[1].getAbsolutePath();
				
				audioSignal = AudioIo.loadWavFile(wavFile);
				
				File wavAudio = new File(Menu.wavFile);
				AudioInputStream audioStream = AudioSystem.getAudioInputStream(wavAudio);
				
				AudioFormat format = audioStream.getFormat();
				DataLine.Info info = new DataLine.Info(Clip.class, format);
				
				//final Clip audioClip = (Clip) AudioSystem.getLine(info);
				audioClip = (Clip) AudioSystem.getLine(info);
				System.out.println("audio format: " + audioStream.getFormat());
				audioClip.open(audioStream);
				audioLength = (int) (Menu.audioClip.getMicrosecondLength()/10000);
				
				final PalateSlider palateSlider = new PalateSlider();
				final SignalPlot signalPlot = new SignalPlot(audioSignal.data[0], -1, 1);
				
				// Create Layout Manager
				setLayout(new GridBagLayout());
				gbc = new GridBagConstraints();
				
				// Load icon images
				playOn = new ImageIcon(getClass().getResource("/images/playOn.png"));
				playOff = new ImageIcon(getClass().getResource("/images/playOff.png"));
				pauseOn = new ImageIcon(getClass().getResource("/images/pauseOn.png"));
				pauseOff = new ImageIcon(getClass().getResource("/images/pauseOff.png"));
				menu = new ImageIcon(getClass().getResource("/images/menu.png"));
				
				// Initiate Progress and Slider Bars
				palateProgress = new JProgressBar(0, CSVRead.rows);
				palateProgress.setValue(0);
		
				palateSlide = new JSlider(JSlider.HORIZONTAL, 0, CSVRead.rows, 0);
				audioSlide = new JSlider(JSlider.HORIZONTAL, 0, ((int) audioClip.getMicrosecondLength())/10000, 0);
				
				audioSlide.putClientProperty("Slider.paintThumbArrowShape", true);
				//audioSlide.putClientProperty("Slider.horizontalThumbIcon", new ImageIcon(getClass().getResource("/images/thumbIcon.png")));
		
				// Initiate Play, Pause, and Menu Buttons
				playButton = new JButton();
				playButton.setPreferredSize(new Dimension(16,16));
				playButton.setIcon(playOff);
				
				pauseButton = new JButton();
				pauseButton.setPreferredSize(new Dimension(16,16));
				pauseButton.setIcon(pauseOff);
				
				menuButton = new JButton();
				menuButton.setPreferredSize(new Dimension(16,16));
				menuButton.setIcon(menu);
				
				// Specify layout
				gbc.fill = GridBagConstraints.HORIZONTAL;
				gbc.gridy = 0; // first row
				gbc.gridx = 0; // first column
				add(playButton, gbc);
				
				gbc.fill = GridBagConstraints.HORIZONTAL;
				gbc.gridy = 0; // first row
				gbc.gridx = 1; // second column
				add(pauseButton,gbc);
				
				gbc.gridy = 0; // first row
				gbc.gridx = 3; // fourth column

				add(menuButton, gbc);

				gbc.fill = GridBagConstraints.HORIZONTAL;
				//gbc.weightx = 1; // fill entire row
				gbc.gridy = 0; // first row
				gbc.gridx = 2; // third column
				gbc.weightx = 1;
				//add(palateSlide, gbc);
				gbc.insets = new Insets(0, 14, 0, 14);
				//add(palateProgress, gbc);
				
				gbc.gridx = 0; // first column
				gbc.gridwidth = 4; // span four columns
				gbc.fill = GridBagConstraints.BOTH; // fill both directions
				gbc.gridy = 1; // second row
				gbc.anchor = GridBagConstraints.CENTER;
				gbc.weightx = 1; // stretch to fill horizontal
				gbc.weighty = 1; // stretch to fill vertical
				gbc.insets = new Insets(0, 175, 0, 175);
				add(palateSlider, gbc);
				
				// Add waveform
				gbc.gridx = 0; // first column
				gbc.gridy = 2; // third row
				//gbc.weightx = 1;
				gbc.weighty = 0.35;
				gbc.insets = new Insets(0, 0, 0, 0);
				add(audioSlide, gbc);
				gbc.insets = new Insets(0, 12, 0, 12);
				add(signalPlot, gbc);
				
				palateSlide.setPaintTrack(false);
				audioSlide.setPaintTrack(false);
				//audioSlide.setExtent(10);
				
				playButton.addMouseListener(new MouseAdapter(){
					public void mouseClicked(MouseEvent ePlayButton){
						playOff();
					}
				});
				
				pauseButton.addMouseListener(new MouseAdapter(){
					public void mouseClicked(MouseEvent ePauseButton){
						playOn();
					}
				});
				
				/*
				palateSlide.addMouseListener(new MouseAdapter(){
					public void mouseClicked(MouseEvent eSlideClick){
						final JSlider newSlider = (JSlider) eSlideClick.getSource();
						System.out.println("inside palateSlide");
						
						synchronized(PalateSlider.slideLock){
							slideDone = false;
							int value = newSlider.getValue();
							PalateSlider.updateRowCount(value);
							PalateSlider.arraySeek();
						}
					}
				});
				
				palateSlide.addChangeListener(new ChangeListener(){
					public void stateChanged(ChangeEvent eSL){
						slideDone = false;
						final JSlider palateSlider = (JSlider) eSL.getSource();
						palateProgress.setValue(palateSlide.getValue());
						audioSlide.setValue(palateSlide.getValue());
	
						// If JSlider dragged ahead
						synchronized(PalateSlider.slideLock){
							int value = palateSlider.getValue();
							PalateSlider.updateRowCount(value);
							PalateSlider.arraySeek();
							slideDone = true;
						}					
					}
				});*/
				
				/*
				audioSlide.addChangeListener(new ChangeListener(){
					public void stateChanged(ChangeEvent eSL){
						slideDone = false;
						final JSlider audioSlider = (JSlider) eSL.getSource();
						//palateProgress.setValue(palateSlide.getValue());
						audioSlide.setValue(audioSlider.getValue());
						//audioClip.setMicrosecondPosition((audioSlider.getValue()));
	
						// If JSlider dragged ahead
						synchronized(PalateSlider.slideLock){
							int value = (audioSlider.getValue());
							PalateSlider.updateRowCount(value);
							PalateSlider.arraySeek();
							slideDone = true;
						}					
					}
				});*/
				
				/*
				audioSlide.addMouseListener(new MouseAdapter(){
					public void mouseClicked(MouseEvent eASL){
						slideDone = false;
						final JSlider audioSlider = (JSlider) eASL.getSource();
						//palateProgress.setValue(palateSlide.getValue());
						audioSlide.setValue(audioSlider.getValue());
						audioClip.setMicrosecondPosition((audioSlider.getValue()));
	
						// If JSlider dragged ahead
						synchronized(PalateSlider.slideLock){
							int value = (audioSlider.getValue());
							PalateSlider.updateRowCount(value);
							PalateSlider.arraySeek();
							slideDone = true;
						}	
					}
				});*/
				
				
				// All menu stuff below
				JMenuItem compare = new JMenuItem("Compare against standards");
				compare.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e){
						//Comparison.compare(PalateSlider.i);
					}
				});
				
				menuPopup = new JPopupMenu();
				menuPopup.add(compare);				
				
				menuButton.addFocusListener(new FocusListener(){
					public void focusLost(FocusEvent e){
						isShowingMenu = false;
					}
					
					public void focusGained(FocusEvent e){
						isShowingMenu = true;
					}
				});
				
				menuButton.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent eMenuAction){
						if(!showMenu){
							Component c = (Component) eMenuAction.getSource();
							menuPopup.show(c, -1, c.getHeight());
							menuPopup.requestFocus();
						}else{
							showMenu = true;
						}
					}
				});
				
				menuButton.addMouseListener(new MouseAdapter(){
					public void mouseClicked(MouseEvent eMenuButton){

						if(isShowingMenu){
							showMenu = false;
						} else{
							showMenu = true;
						}
					}
				});
			}
		}
	}
	
	// Get time from iterator method (i = 10 ms)
	public static void incrementTimers(int i){
		palateProgress.setValue(i);
		palateSlide.setValue(i);
		audioSlide.setValue(i);
	}
	
	// Change icons to denote playing status
	public static void playOn(){
		playButton.setIcon(playOn);
		pauseButton.setIcon(pauseOff);
		
		synchronized(PalateSlider.slideLock){
			slideDone = false;
		}
	}
	
	// Change icons to denote paused status
	public static void playOff(){
		playButton.setIcon(playOff);
		pauseButton.setIcon(pauseOn);
		
		synchronized(PalateSlider.slideLock){
			PalateSlider.slideLock.notify();
			slideDone = true;
		}
	}
}