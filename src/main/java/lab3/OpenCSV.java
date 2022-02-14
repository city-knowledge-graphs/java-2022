package lab3;

import java.io.FileReader;
import java.io.IOException;

import com.opencsv.CSVIterator;
import com.opencsv.CSVReader;

public class OpenCSV {
	
	public OpenCSV(String fstring) throws IOException {
		
		CSVReader reader = new CSVReader(new FileReader(fstring));
		String [] nextRecord;//nextRecord
		 while ((nextRecord = reader.readNext()) != null) {
			  
			 for (String col : nextRecord){
			 	 System.out.print(col + " ");			 	 
			 }
			 System.out.println();
		 	  
		 }
		  
		  reader.close();
	}


	public static void main (String[] args) {
		
		try {
			new OpenCSV("files/lab3/lab3_companies_file.csv");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
}
