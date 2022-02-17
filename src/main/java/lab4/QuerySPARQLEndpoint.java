package lab4;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;

import lab1.QueryEndpoint;



public class QuerySPARQLEndpoint {
	
	public QuerySPARQLEndpoint(String endpoint, String queryStr) {
		
		Query q = QueryFactory.create(queryStr);
	
		
		QueryExecution qe =
				QueryExecutionFactory.sparqlService(endpoint,q);
				try {
				ResultSet res = qe.execSelect();
				while( res.hasNext()) {
					QuerySolution soln = res.next();
					RDFNode a = soln.get("?x");  //Variable name in select
					System.out.println(""+a);
				}
			    
				} finally {
				qe.close();
				}
			
	}

	
	public static void main(String[] args) {

		//Query a remote RDF graph (e.g., SPARQL endpoint)
		String dbpedia_endpoint = "http://dbpedia.org/sparql";
		
		String dbpedia_query1 = "SELECT DISTINCT ?x WHERE { <http://dbpedia.org/resource/Chicago_Bulls> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?x . }";
		System.out.println("\nQuerying DBPedia Knowledge Graph (types of Chicago Bulls)");
		new QueryEndpoint(dbpedia_endpoint, dbpedia_query1);
		
		System.out.println("\n\n");
		
		String dbpedia_query2 = "PREFIX foaf: <http://xmlns.com/foaf/0.1/> PREFIX dbo: <http://dbpedia.org/ontology/> SELECT DISTINCT ?x WHERE { ?jd foaf:name \"Johnny Depp\"@en . ?m dbo:starring ?jd .?m dbo:starring ?other . ?other foaf:name ?x . FILTER (STR(?x)!=\"Johnny Depp\")} ORDER BY ?x";
		System.out.println("\nQuerying DBPedia Knowledge Graph (actors co-starring with Johnny Depp)");
		new QueryEndpoint(dbpedia_endpoint, dbpedia_query2);
		


	}
	
}
