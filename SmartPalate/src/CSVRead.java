import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import au.com.bytecode.opencsv.CSVReader;


public class CSVRead{
	
	public static int width = 0;
	public static int rows = 0;
	public static int count = 0;
	
	public static int[][] electrodeArray(int palateData[][]){
		//System.out.println("Inside CSVRead");
		//String FileName = FileBrowser.browser(null);
		//System.out.println("Got FileName");
		String[] rowCount;
		
		// Read data from CSV to numerate rows
		try{
			CSVReader palateRows = new CSVReader(new FileReader(Menu.csvFile)); // Parse data from CSV
			// Determine the # of rows in CSV file
			while((rowCount = palateRows.readNext()) != null){
				count++;
				width = rowCount.length;
			}
			palateRows.close();
			
			// Set number of rows in CSV file
			rows = count-1;

			// Read data from the CSV file
			CSVReader palateArray = new CSVReader(new FileReader(Menu.csvFile));
			String[] nextLine;
			int i = 0; // Row counter
			int j = 0; // Cell counter
			palateData = new int[rows][]; // Set object "palateData" as new integer array
			
			// Read one line of CSV at a time
			while((nextLine = palateArray.readNext()) != null){
				
				try{
					System.out.println("i: " + i);
					palateData[i] = new int[rows]; // Set i-th row in int array, palateData
				} catch(ArrayIndexOutOfBoundsException e){
					System.out.println("Array Index Out of Bounds");
				}
				
				for(j = 1; j < width; j++){
					
					palateData[i][j] = Integer.parseInt(nextLine[j]); // Import j-th value
				}
				if(i < rows-1){
					i++; // Increment row counter
				}	
			}
			
			palateArray.close();

		} catch(FileNotFoundException e){
			e.printStackTrace();
		} catch(IOException e){
			e.printStackTrace();
		}
		
		return palateData;
	}
	
	public static int getWidth(){
		return width;
	}
	public static int getRows(){
		return rows;
	}
}