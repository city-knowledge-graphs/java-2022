/*******************************************************************************
 * Copyright 2018 by The Alan Turing Institute
 * 
 *******************************************************************************/
package lab5;



/**
 *
 * Class to connect to the public DBpedia SPARQL endpoint. See contained 
 * datasets and more information here: http://wiki.dbpedia.org/public-sparql-endpoint
 *
 * @author ernesto
 * Created on 23 Jul 2018
 *
 */
public class DBpediaEndpoint extends SPARQLEndpointService {

	
	@Override
	public String getENDPOINT() {
		return "https://dbpedia.org/sparql";
	}

	
	
	
	/**
	 * To extract a portion of dbpedia relevant to the subject
	 * @param uri_subject
	 * @return
	 */	
	protected String createSPARQLQueryForSubject(String uri_subject){
		
		return //"PREFIX foaf: <http://xmlns.com/foaf/0.1/> \n "+
				"SELECT ?p ?o \n"
				+ "WHERE { <" + uri_subject + "> ?p ?o . "
				+ "FILTER (?p != <http://dbpedia.org/ontology/wikiPageWikiLink> "
				+ "&& ?p != <http://www.w3.org/2000/01/rdf-schema#comment> "
				+ "&& ?p != <http://dbpedia.org/ontology/abstract>)"
				+ "}";
		
	}
	
	
	/**
	 * To extract a portion of dbpedia relevant to the object
	 * @param uri_subject
	 * @return
	 */	
	protected String createSPARQLQueryForObject(String uri_object){
		
		return //"PREFIX foaf: <http://xmlns.com/foaf/0.1/> \n "+
				"SELECT ?s ?p \n"
				+ "WHERE { ?s ?p <" + uri_object + "> . "
				+ "FILTER (?p != <http://dbpedia.org/ontology/wikiPageWikiLink> "
				+ "&& ?p != <http://www.w3.org/2000/01/rdf-schema#comment> "
				+ "&& ?p != <http://dbpedia.org/ontology/abstract>)"
				+ "}";
		
	}
	
	
	
	/**
	 * To extract class types of the subject
	 * @param uri_subject
	 * @return
	 */	
	protected String createSPARQLQuery_TypesForSubject(String uri_subject){
		
		return //"PREFIX foaf: <http://xmlns.com/foaf/0.1/> \n "+
				"SELECT DISTINCT ?uri \n"
				+ "WHERE { <" + uri_subject + "> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?uri . "
				+ "}";
		
	}
	
	
	protected String createSPARQLQuery_AllTypesForSubject(String uri_subject){
		
		return //"PREFIX foaf: <http://xmlns.com/foaf/0.1/> \n "+
				"SELECT DISTINCT ?uri \n"
				+ "WHERE {\n"
				+ "{<" + uri_subject + "> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?dt . "
				+ "?dt <http://www.w3.org/2000/01/rdf-schema#subClassOf>* ?uri "
				+ "}\n"
				+ "UNION \n{"
				+ "<" + uri_subject + "> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?uri . " //direct types
				+ "}\n"
				+ "UNION \n{"
				+ "<" + uri_subject + "> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?dt . "
				+ "?dt <http://www.w3.org/2002/07/owl#equivalentClass> ?uri "
				+ "}\n"
				+ "}";
		
	}
	
	
	protected String createSPARQLQuery_AllSuperClassesForSubject(String uri_subject){
		
		return //"PREFIX foaf: <http://xmlns.com/foaf/0.1/> \n "+
				"SELECT DISTINCT ?uri \n"
				+ "WHERE {\n"
				+ "{<" + uri_subject + "> <http://www.w3.org/2000/01/rdf-schema#subClassOf>* ?uri "
				+ "}\n"
				+ "UNION \n{"
				+ "<" + uri_subject + "> <http://www.w3.org/2002/07/owl#equivalentClass> ?uri "
				+ "}\n"
				+ "}";
		
	}
	
	
	
	
	
	
	protected String craeteSPARQLQuery_TypeObjectsForPredicate(String uri_predicate){
		
		return //"PREFIX foaf: <http://xmlns.com/foaf/0.1/> \n "+
				"SELECT DISTINCT ?uri \n"
				+ "WHERE { ?s <" + uri_predicate + "> ?o . "
				+ "?o <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?uri ."
				+ "}";
		
	}
	
	
	
	
	
	
	public static void main(String[] args) {
		
		String uri_subject;
		uri_subject = "http://dbpedia.org/resource/London";
		
		
		DBpediaEndpoint dbe = new DBpediaEndpoint();
		
		try {
			
			System.out.println("Direct types: ");
			System.out.println(dbe.getTypesForSubject(uri_subject).size() + " " + dbe.getTypesForSubject(uri_subject));
			for (String type : dbe.getTypesForSubject(uri_subject)){
				if (type.startsWith("http://dbpedia.org/ontology"))
					System.out.println("\t"+type);
			}
			
			System.out.println("All types: ");
			System.out.println(dbe.getAllTypesForSubject(uri_subject).size() + " " + dbe.getAllTypesForSubject(uri_subject));
			for (String type : dbe.getAllTypesForSubject(uri_subject)){
				if (type.startsWith("http://dbpedia.org/ontology"))
					System.out.println("\t"+type);
			}
			
			
		//	}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		
	}



	
	
	
}
