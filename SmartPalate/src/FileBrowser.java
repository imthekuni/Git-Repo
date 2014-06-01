import java.io.File;

import javax.swing.JFileChooser;


public class FileBrowser {
	
	public static File[] browser(File[] fileName){
		final JFileChooser fc = new JFileChooser();
		fc.setMultiSelectionEnabled(true);
		fc.showOpenDialog(fc);
		fileName = fc.getSelectedFiles();
		System.out.println("Root File:" + fileName);
		return fileName;
	}
}