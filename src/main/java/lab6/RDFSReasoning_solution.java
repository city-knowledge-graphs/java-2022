package lab6;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;



public class RDFSReasoning_solution {

	
	public RDFSReasoning_solution(String file_input, String file_ouput) throws FileNotFoundException {
	
		Dataset dataset = RDFDataMgr.loadDataset(file_input);
		Model model = dataset.getDefaultModel();
		
		System.out.println("The input graph contains '" + model.listStatements().toSet().size() + "' triples.");
		
		
		//Option 1
		//Get defaults reasoners
		//Reasoner reasoner = ReasonerRegistry.getRDFSSimpleReasoner();
		Reasoner reasoner = ReasonerRegistry.getRDFSReasoner();		//Includes also axiomatic triples
		InfModel inf_model = ModelFactory.createInfModel(reasoner, model);
		
		//Option 2
		//Uses a RDFS reasoner internally
		//InfModel inf_model = ModelFactory.createRDFSModel(model);
		
		System.out.println("The graph witn inferences contains '" + inf_model.listStatements().toSet().size() + "' triples.");
		
		
		
		System.out.println("\nChecking entailments: ");
		checkEntailments(inf_model);
		
		
		
		
		//Storing in RDF/xml
        OutputStream out = new FileOutputStream(file_ouput);
        RDFDataMgr.write(out, inf_model, RDFFormat.TURTLE);
	}
	
	
	
	public void checkEntailments(InfModel inf_model) {
		
		
		    
		    String triple1 = ":Father rdfs:subClassOf :Person ."; 
		    String triple2 = ":Woman rdfs:subClassOf :Person .";
		    String triple3 = ":Juliet a :Person .";
		    String triple4 = ":Ann a :Child .";
		    String triple5 = ":Ann :isChildOf :Carl .";
		    String triple6 = ":Ann :hasParent :Juliet .";
		    String triple7 = "rdfs:range rdf:type rdfs:Resource .";
		    String triple8 = ":Mother rdfs:subClassOf :Person .";
		    
		    
		    checkEntailment(inf_model, triple1);
		    checkEntailment(inf_model, triple2);
		    checkEntailment(inf_model, triple3);
		    checkEntailment(inf_model, triple4);
		    checkEntailment(inf_model, triple5);
		    checkEntailment(inf_model, triple6);
		    checkEntailment(inf_model, triple7);
		    checkEntailment(inf_model, triple8);
	}
	
	
	public void checkEntailment(InfModel inf_model, String triple) {
		
		 //Query local model
	    String queryStr =
	     "PREFIX : <http://city.ac.uk/kg/lab4/>" +
	     "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + 
	     "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
	     "ASK {" +
	      triple + 
	    "}";
	    
	    Query q = QueryFactory.create(queryStr);
		
	   
		
		QueryExecution qe =
				QueryExecutionFactory.create(q, inf_model);
				try {	
					
					//Different methods for SELECT (execSelect()) and ASK (execAsk()) queries:
					System.out.println("Does '" + triple + "' holds? " + qe.execAsk());
				
				} finally {
				qe.close();
				}
		
	}
	
	
	
	
	public static void main(String[] args) {

		try {
			
			new RDFSReasoning_solution("files/lab6/lab6-rdfs.ttl", "files/lab6/lab6-rdfs-extended.ttl");
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
		

}
