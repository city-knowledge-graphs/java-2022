package lab4;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;


public class QueryLocalRDFGraph_NobelPrize {
	
	public QueryLocalRDFGraph_NobelPrize(String file) {
	
		Dataset dataset = RDFDataMgr.loadDataset(file, RDFLanguages.NT);
		Model model = dataset.getDefaultModel();
		
		
		//Query local model
	    //Name of female laureates
	    String queryStr = "SELECT DISTINCT ?name WHERE { "+
	    		"?laur <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://data.nobelprize.org/terms/Laureate> . " +
	    		"?laur <http://www.w3.org/2000/01/rdf-schema#label> ?name . " +
	    		"?laur <http://xmlns.com/foaf/0.1/gender> \"female\" . }";
	    
	    
	    
	    Query q = QueryFactory.create(queryStr);
		
	    System.out.println("Querying local model, name of female laureates: ");
		
		QueryExecution qe =
				QueryExecutionFactory.create(q, model);
				try {
				ResultSet res = qe.execSelect();
				while( res.hasNext()) {
					QuerySolution soln = res.next();					
					RDFNode name = soln.get("?name"); //Variable name in select	
					System.out.println("\t'"+ name + "'.");					
				}
			    
				} finally {
				qe.close();
				}
	    }
			
	
	public static void main(String[] args) {

		new QueryLocalRDFGraph_NobelPrize("files/nobelprize_kg.nt");
		
	}
		

}
