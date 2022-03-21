package lab7;


import java.io.IOException;
import java.util.Iterator;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;

import util.ReadFile;



public class GraphdbCommunication {
	
	
	public GraphdbCommunication() {
		
	}
	
	
	public void loadingData(String graphdb_endpoint, String file, String format) throws IOException, InterruptedException {
		
		System.out.println("Uploading file: " + file);
		String curl_command;
		
		if (format.equals("trig"))
			curl_command = "curl '" + graphdb_endpoint + "/statements' -X POST -H \"Content-Type:application/x-trig\" -T '" + file + "'";
		else
			curl_command = "curl '" + graphdb_endpoint + "/statements' -X POST -H \"Content-Type:application/x-turtle\" -T '" + file + "'";

	    //Windows
	    //String[] commands = new String[]{"CMD", "/c", curl_command};
	    
	    //Linux/Mac
	    String[] commands = new String[]{"/bin/bash", "-c", curl_command};
		Runtime.getRuntime().exec(commands);
		
	}
	
	
	public void queryGraphDBRepo(String graphdb_endpoint, String queryStr) {
		
		Query q = QueryFactory.create(queryStr);
	
		
		QueryExecution qe =
				QueryExecutionFactory.sparqlService(graphdb_endpoint,q);
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
	
		
								
		
		String test;		
		
		test="world-cities";
		test="nobel-prizes";
		test="named-graph";
		boolean load_Data=true;
		
		
		String graphdb_endpoint;
		String path_to_data_file;
		String path_to_onto_file=null;
		String queryStr;
		String format="ttl";
		String query_file;
		
		try {
		
			if (test.equals("world-cities")) {
				//GraphDB Endpoint
				graphdb_endpoint = "http://192.168.0.18:7200/repositories/Lab6_repository_automatic";
				
				path_to_data_file = "files/worldcities-free-100-task2.ttl";
				path_to_onto_file = "files/ontology_lab5.ttl";
				queryStr = 
			            "PREFIX lab6: <http://www.semanticweb.org/ernesto/inm713/lab6/>\n" +
			                   	"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
			                	"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> \n" +
			                   	"SELECT DISTINCT ?country (COUNT(?city) AS ?num_cities) WHERE { \n" +
			                    "?country lab6:hasCity ?city .  \n" +
			                "\n}"+
			                "GROUP BY ?country\n" +
			                "ORDER BY DESC(?num_cities)\n" +
			                "LIMIT 10";
			}
			else if(test.equals("nobel-prizes")) {
				
				//Load query from file				
				query_file="files/lab7/query_nobel-prize.txt";
				query_file="files/lab7/solution/query7.5_nobel-prize.txt";
				
				ReadFile qfile = new ReadFile(query_file);		
				queryStr = qfile.readFileIntoString();
				
				graphdb_endpoint = "http://192.168.0.18:7200/repositories/NobelPrize";
				path_to_onto_file = "files/nobel-prize-ontology.rdf";
				path_to_data_file = "files/nobelprize_kg.nt";
				
			}
			else {
				format="trig";
				graphdb_endpoint = "http://192.168.0.18:7200/repositories/namedGraphs";    
				path_to_data_file = "files/lab7/named_graphs.ttl";
				
					    
				query_file="files/lab7/query_named_default.txt";
				query_file="files/lab7/query_named1.txt";
				query_file="files/lab7/query_named2.txt";
				query_file="files/lab7/query_named_all.txt";
				query_file="files/lab7/query_named_from.txt";    
					    
				ReadFile qfile = new ReadFile(query_file);		
				queryStr = qfile.readFileIntoString();
				
			}
			
		

		
			GraphdbCommunication graphdb_access = new GraphdbCommunication();
		
			
			//LOAD DATA
			if (load_Data) {
				if (path_to_onto_file!=null) 
					graphdb_access.loadingData(graphdb_endpoint, path_to_onto_file, format);
				
				graphdb_access.loadingData(graphdb_endpoint, path_to_data_file, format);
			}
			
			
			
			//QUERY DATA			
			graphdb_access.queryGraphDBRepo(graphdb_endpoint, queryStr);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

}
