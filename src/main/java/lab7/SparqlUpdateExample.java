package lab7;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.update.UpdateAction;

public class SparqlUpdateExample {

	
	public SparqlUpdateExample() throws FileNotFoundException {
		
		//Load data
		String file_data = "files/playground.ttl";
		Dataset dataset = RDFDataMgr.loadDataset(file_data);//, format);
		Model model = dataset.getDefaultModel();
				
		//Load ontology if any....
		//Dataset dataset_onto = RDFDataMgr.loadDataset(file_onto);
		//model.add(dataset_onto.getDefaultModel().listStatements().toList());
		
		System.out.println("The input graph contains '" + model.listStatements().toSet().size() + "' triples.");
		
		String update_insert = "" +
				"PREFIX ttr: <http://example.org/tuto/resource#>\n" +
				"PREFIX tto: <http://example.org/tuto/ontology#>\n" +
				"PREFIX dbo: <http://dbpedia.org/ontology/>\n" +
				"PREFIX dbp: <http://dbpedia.org/property/>\n" +
                //TODO
				"insert data {"
				+ "ttr:Bella dbp:name \"Bella\" .\n"
				+ "ttr:Bella a tto:Cat .\n"
				+ "ttr:Ernesto a dbo:Person .\n"
				+ "ttr:Ernesto ttr:pet ttr:Bella .\n"				
				+"}";				
                
		
		//System.out.println(update_insert);

        UpdateAction.parseExecute( update_insert, model );
		
		System.out.println("The input graph after the update contains '" + model.listStatements().toSet().size() + "' triples.");
		
		
		String update_delete = "" +
				"PREFIX ttr: <http://example.org/tuto/resource#>\n" +
				"PREFIX tto: <http://example.org/tuto/ontology#>\n" +
				"PREFIX dbo: <http://dbpedia.org/ontology/>\n" +
				"PREFIX dbp: <http://dbpedia.org/property/>\n" +
                //TODO
				"delete data {"
				+ "ttr:Bella dbp:name \"Bella\" .\n"
				+ "ttr:Bella a tto:Cat .\n"
				+ "ttr:Ernesto a dbo:Person .\n"
				+ "ttr:Ernesto ttr:pet ttr:Bella .\n"				
				+"}";
		
		
		UpdateAction.parseExecute( update_delete, model );		
		System.out.println("The input graph after the deletion contains '" + model.listStatements().toSet().size() + "' triples.");
		
		
		
		//Change dbp:name by rdfs:label
		String update_labels = "" +
				"PREFIX ttr: <http://example.org/tuto/resource#>\n" +
				"PREFIX tto: <http://example.org/tuto/ontology#>\n" +
				"PREFIX dbo: <http://dbpedia.org/ontology/>\n" +
				"PREFIX dbp: <http://dbpedia.org/property/>\n" +
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
				"delete { ?x dbp:name ?y }\n" +
                "insert { ?x rdfs:label ?y }\n" +
                "where { ?x dbp:name ?y }";
		
		UpdateAction.parseExecute( update_labels, model );		
		System.out.println("The input graph after the label update '" + model.listStatements().toSet().size() + "' triples.");
		
		
		//Save model
		String file_output = file_data.replace(".ttl", "-label-update.ttl");
		OutputStream out = new FileOutputStream(file_output);
		RDFDataMgr.write(out, model, RDFFormat.TURTLE);
		
		
		
	}
	
	
	public static void main(String[] args) {
		try {
			new SparqlUpdateExample();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
