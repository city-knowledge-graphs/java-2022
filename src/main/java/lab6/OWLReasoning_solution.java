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



public class OWLReasoning_solution {

	protected enum JenaReasoner {MICRO, MINI}

	
	public OWLReasoning_solution(String file_input, String file_ouput, JenaReasoner jenaReasoner) throws FileNotFoundException {
	
		Dataset dataset = RDFDataMgr.loadDataset(file_input);
		Model model = dataset.getDefaultModel();
		
		System.out.println("The input graph contains '" + model.listStatements().toSet().size() + "' triples.");
		
		
		//Option 1
		//Get reasoners
		Reasoner reasoner;
		if (jenaReasoner==JenaReasoner.MINI)
			reasoner = ReasonerRegistry.getOWLMiniReasoner();		//Approximate reasoner close to OWL 2 RL (but not exactly)
		else
			reasoner = ReasonerRegistry.getOWLMicroReasoner();		//Approximate reasoner close to OWL 2 RL (but not exactly). Less expressive but faster than Mini reasoner.
				
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
		
		
		    
		    String triple1 = ":Carl :hasChild :Ann .";
		    String triple2 = ":Ann rdf:type :Child .";
		    String triple3 = ":Juliet :hasChild :Ann .";
		    
		    checkEntailment(inf_model, triple1);
		    checkEntailment(inf_model, triple2);
		    checkEntailment(inf_model, triple3);
		   
	}
	
	
	public void checkEntailment(InfModel inf_model, String triple) {
		
		 //Query local model
	    String queryStr =
	     "PREFIX : <http://city.ac.uk/kg/lab7/>" +
	     "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + 
	     "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
	     "PREFIX owl: <http://www.w3.org/2002/07/owl#>" +
	     "ASK {" +
	      triple + 
	    "}";
	    
	    Query q = QueryFactory.create(queryStr);
		
	   
		
		QueryExecution qe =
				QueryExecutionFactory.create(q, inf_model);
				try {	
					
					//Different methods for SELECT (execSelect()) and ASK (execAsk()) queries:
					System.out.println("Does '" + triple + "' hold? " + qe.execAsk());
				
				} finally {
				qe.close();
				}
		
	}
	
	
	
	
	public static void main(String[] args) {

		try {
			
			//new OWLReasoning("http://protege.stanford.edu/ontologies/pizza/pizza.owl", "files/lab6/pizza_inference.ttl");
			new OWLReasoning_solution("files/lab6/lab6-owl2rl.ttl", "files/lab6/lab6-owl2rl-extended.ttl",JenaReasoner.MINI);			
			
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
		

}
