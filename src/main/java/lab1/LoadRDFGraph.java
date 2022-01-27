package lab1;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.RDFDataMgr;
//import org.apache.jena.util.FileManager;

/**
 * 
 * @author ernesto
 *
 */
public class LoadRDFGraph {
	
	Model model;
	
	public LoadRDFGraph(String file, boolean printStatements) {
				
		Dataset dataset = RDFDataMgr.loadDataset(file);
		model = dataset.getDefaultModel();
				
		//Deprecated
		//FileManager.g.get().addLocatorClassLoader(LoadRDFGraph.class.getClassLoader());
        //Model model = FileManager.get().loadModel(file, null, "TURTLE");

        StmtIterator iter = model.listStatements();
        
    	//Prints triples in graph
        if (printStatements) {
        	System.out.println("Printing '" + model.listStatements().toSet().size() + "' triples.");
        
	        try {
	            while ( iter.hasNext() ) {
	                Statement stmt = iter.next();
	                
	                Resource s = stmt.getSubject();
	                Resource p = stmt.getPredicate();
	                RDFNode o = stmt.getObject();
	                
	                System.out.println(s.getURI() + " " + p.getURI() + " " + o.toString());
	            }
	        } finally {
	            if ( iter != null ) iter.close();
	        }
        }
	}
	
	public void QueryLocalModel(String queryStr) {

        //Query local model
        Query q = QueryFactory.create(queryStr);
		
		
		QueryExecution qe =
				QueryExecutionFactory.create(q, model);
				try {
				ResultSet res = qe.execSelect();
				while( res.hasNext()) {
					QuerySolution soln = res.next();
					RDFNode a = soln.get("?x");
					System.out.println(""+a);
				}
			    
				} finally {
				qe.close();
				}
        
        
        
    }
	
	public static void main(String[] args) {

		String queryStr;
		
		LoadRDFGraph beatlesKG = new LoadRDFGraph("files/lab1/beatles.ttl", true);
        //Query for solo artists
        System.out.println("\nQuerying local model (solo artists): ");
		queryStr = "SELECT DISTINCT ?x WHERE { ?x <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://stardog.com/tutorial/SoloArtist> . }";
		beatlesKG.QueryLocalModel(queryStr);
		
		
		
		LoadRDFGraph nobelPrizeKG = new LoadRDFGraph("files/nobelprize_kg.nt", false);
		//Female laureates
        System.out.println("\nQuerying local model (female laureates): ");
		queryStr = "SELECT DISTINCT ?x WHERE { ?laur <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://data.nobelprize.org/terms/Laureate> . ?laur <http://www.w3.org/2000/01/rdf-schema#label> ?x . ?laur <http://xmlns.com/foaf/0.1/gender> \"female\" . }";
		nobelPrizeKG.QueryLocalModel(queryStr);
		
		
		
		
		//See more examples here: https://www.nobelprize.org/about/linked-data-examples/
				//String nobelprize_endpoint = "http://data.nobelprize.org/sparql";
				//String nobelprize_query = "SELECT DISTINCT ?x WHERE { ?laur <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://data.nobelprize.org/terms/Laureate> . ?laur <http://www.w3.org/2000/01/rdf-schema#label> ?x . ?laur <http://xmlns.com/foaf/0.1/gender> \"female\" . }";

		
		
	}
		

}
