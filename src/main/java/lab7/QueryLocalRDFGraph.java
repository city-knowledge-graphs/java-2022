package lab7;

import java.io.FileNotFoundException;
import java.util.Iterator;

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
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;

import util.ReadFile;




public class QueryLocalRDFGraph {
	
	protected enum JenaReasoner {MICRO, MINI}

	
	public QueryLocalRDFGraph(String file, Lang format, String query_file) throws FileNotFoundException {
	
		
		Dataset dataset = RDFDataMgr.loadDataset(file, format);
		Model model = dataset.getDefaultModel();
		
		System.out.println("The input graph contains '" + model.listStatements().toSet().size() + "' triples.");

    
	    //Do reasoning
	    Reasoner reasoner;
	    JenaReasoner jenaReasoner=JenaReasoner.MINI;
	    
		if (jenaReasoner==JenaReasoner.MINI)
			reasoner = ReasonerRegistry.getOWLMiniReasoner();		//Approximate reasoner close to OWL 2 RL (but not exactly)
		else
			reasoner = ReasonerRegistry.getOWLMicroReasoner();		//Approximate reasoner close to OWL 2 RL (but not exactly). Less expressive but faster than Mini reasoner.
				
		InfModel inf_model = ModelFactory.createInfModel(reasoner, model);
	    
		System.out.println("The graph witn inferences contains '" + inf_model.listStatements().toSet().size() + "' triples.");
    

		
		
		//Load query
		ReadFile qfile = new ReadFile(query_file);		
		String queryStr = qfile.readFileIntoString();
		
		System.out.println("Query:");
		System.out.println(queryStr);
		
		
		//Execute query and print results
	    Query q = QueryFactory.create(queryStr);
		
	    System.out.println("Results: ");
		
		QueryExecution qe =
				QueryExecutionFactory.create(q, inf_model);
				try {
				ResultSet res = qe.execSelect();
				Iterator<String> it;
				String row;
				while( res.hasNext()) {
					QuerySolution soln = res.next();
					it = soln.varNames();
					row="";
					while (it.hasNext()) {
						RDFNode node = soln.get(it.next());
						row += node.toString() + ", ";						
					}										
					System.out.println(row);			
				}
			    
				} finally {
				qe.close();
				}
	    }
			
	

	public static void main(String[] args) {
		String dataset;
		String query_file;
		
		dataset = "files/playground.ttl"; 
		query_file = "files/lab7/query_playground.txt";
		
		query_file = "files/lab7/solution/query7.1.txt";
		query_file = "files/lab7/solution/query7.2.txt";
		//query_file = "files/lab7/solution/query7.3.txt";
		
		
		try {
			
			new QueryLocalRDFGraph(dataset, RDFLanguages.TURTLE, query_file);
			
		} 
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
		

}
