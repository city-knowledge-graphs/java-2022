package lab3;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.vocabulary.RDF;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;



public class Solution_Task3_5 {
	
	Model model;
	
	String lab3_ns_str;
	
	List<String[]> csv_file;
	
	//Dictionary that keeps the URIs. Specially useful if accessing a remote service to get a candidate URI  to avoid repeated calls
    Map<String, String> stringToURI = new HashMap<String, String>();
	
	public Solution_Task3_5() throws IOException {
		
		
		//1. GRAPH INITIALIZATION
	    
        //Empty graph
		model = ModelFactory.createDefaultModel();
		
		
        //Example namespace for this lab
		lab3_ns_str= "http://www.semanticweb.org/ernesto/in3067-inm713/lab3/";
                
        
        //Prefixes for the serialization
		model.setNsPrefix("lab3", lab3_ns_str);
		model.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
        
        
        //Load data in matrix to later use an iterator
		String input_file="files/lab3/lab3_companies_file.csv";		
		//CSVReader reader = new CSVReader(new FileReader(input_file));
		//If first line is to be ignored (header)
		CSVReader reader = new CSVReaderBuilder(new FileReader(input_file)).withSkipLines(1).build();
	    csv_file = reader.readAll();
	    reader.close();
		
		
		
	}
	
	
	protected String cellToURI(String name) {
		
        //We create fresh URI (default option)
		
		if (stringToURI.containsKey(name.toLowerCase()))
			return stringToURI.get(name.toLowerCase());
		else				
			return lab3_ns_str + name;
    
    }

	
	
	public void solution() throws FileNotFoundException {
		
		//This solution assumes the manual or automatic mapping of the CSV file to a KG like DBPedia
        // Such that:
        //- Column 0 elements are of type https://dbpedia.org/ontology/Company
        //- Column 2 elements are of type https://dbpedia.org/ontology/City
        //- Columns 0 and 1 are related via the predicate https://dbpedia.org/ontology/foundingYear
        //- Columns 0 and 2 are related via the predicate https://dbpedia.org/ontology/headquarter
        // The KG also contains the following entities that can be reused from the KG:
        //http://dbpedia.org/resource/Oxford
        //http://dbpedia.org/resource/London
        //http://dbpedia.org/resource/DeepMind
        //http://dbpedia.org/resource/Oxbotica               

        //Manual mapping. Tip: google the entity name + dbpedia: e.g. "Oxford DBpedia" and get the URI 
        //from the suggested DBPedia page.
        //Automatic mapping: More in week 5. Typically using a fuzzy search (aka look-up) over the KG.
        
        //In this lab I'm just creating a very small dictionary with entities (to be used as a very basic look-up)
        //In Week 5 we will use DBPedia look-up service that provides a fuzzy search functionality
        stringToURI.put("oxford", "http://dbpedia.org/resource/Oxford");
        stringToURI.put("london", "http://dbpedia.org/resource/London");
        stringToURI.put("deepmind", "http://dbpedia.org/resource/DeepMind");
        stringToURI.put("oxbotica", "http://dbpedia.org/resource/Oxbotica");
        
        //Namespaces and prefixes
        String dbo_ns = "http://dbpedia.org/ontology/";
		String dbr_ns = "http://dbpedia.org/resource/";
		model.setNsPrefix("dbo", dbo_ns);
		model.setNsPrefix("dbr", dbr_ns);
		
		
		//Iterate over csv file / matrix
		//Format csv file        
        //0         1               2
        //"Company","Founding year","Headquarters"
		
		
		//Define type resources
		Resource company = model.createResource(dbo_ns+"Company");
		Resource city = model.createResource(dbo_ns+"City");
		//Define property/predicate resources
		Property headquarter = model.createProperty(dbo_ns+"headquarter");
		Property foundingYear = model.createProperty(dbo_ns+"foundingYear");
		
		for (String[] row : csv_file) {			
			
			//We check if entity in our small local dictionary
			Resource col0_entity = model.createResource(cellToURI(row[0]));
			Resource col2_entity = model.createResource(cellToURI(row[2]));
			
            //Year column
			Literal col1_literal = model.createTypedLiteral(row[1], XSDDatatype.XSDgYear);

			//We create types
			model.add(col0_entity, RDF.type, company);
			model.add(col2_entity, RDF.type, city);
			
			
            //Relationship between col0 and col2
			model.add(col0_entity, headquarter, col2_entity);
            
            //Relationship between col0 and col1
			model.add(col0_entity, foundingYear, col1_literal);
			
		}
		
		//We save the graph
		saveGraph(model, "files/lab3/solution/Solution_Task3.5_rdflib.ttl");
        
		
	}
	
	public void saveGraph(Model model, String file_output) throws FileNotFoundException {
        
	    //SAVE/SERIALIZE GRAPH
		System.out.println("Saving file '"+ file_output +"'.");
		
	    OutputStream out = new FileOutputStream(file_output);
	    RDFDataMgr.write(out, model, RDFFormat.TURTLE);	       

	}	
	
	
	
	public static void main(String[] args) {
		
		Solution_Task3_5 task_3_5;
		try {
			task_3_5 = new Solution_Task3_5();			
			task_3_5.solution();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	

}
