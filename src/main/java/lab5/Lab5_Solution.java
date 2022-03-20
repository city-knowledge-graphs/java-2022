package lab5;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.OWL;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.opencsv.CSVReader;

import util.WriteFile;

public class Lab5_Solution {
	
	
	String input_file;
	Model model;
	InfModel inf_model;
	
	String lab5_ns_str;
	
	DBpediaLookup dbpedia;
	
	List<String[]> csv_file;
	
	//Dictionary that keeps the URIs. Specially useful if accessing a remote service to get a candidate URI  to avoid repeated calls
    Map<String, String> stringToURI = new HashMap<String, String>();
    
    Map<String, Integer> column_index;
    
    I_Sub isub = new I_Sub();
	
	
	public Lab5_Solution(String input_file, Map<String, Integer> column_index) throws IOException {
		
		//The idea is to cover as much as possible from the original csv file, but for the lab and coursework I'm more interested 
        //n the ideas and proposed implementation than covering all possible cases in all rows (a perfect solution fall more into
        //the score of a PhD project). Also in terms of scalability calling the 
        //look-up services may be expensive so if this is a limitation, a solution tested over a reasonable percentage of the original 
        //file will be of course accepted.        
		
		this.input_file = input_file;
		//Useful to acces column by name (there are alternative ways to do so)
		this.column_index = column_index;
		
		
		//1. GRAPH INITIALIZATION
	    
        //Empty graph
		model = ModelFactory.createDefaultModel();
		
		
		//Note that this is the same namespace used in the ontology "ontology_lab5.ttl"
        lab5_ns_str= "http://www.semanticweb.org/ernesto/in3067-inm713/lab5/";
        
        
        //Prefixes for the serialization
		model.setNsPrefix("lab5", lab5_ns_str);
		model.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
        
        
        //Load data in matric to later use an iterator		
		CSVReader reader = new CSVReader(new FileReader(input_file));
	    csv_file = reader.readAll();
	    reader.close();
		
                
        //KG
        dbpedia = new DBpediaLookup();
		
	}
	
	
	public void Task1() throws JsonProcessingException, IOException, URISyntaxException {
        CovertCSVToRDF(false);
	}
        
    public void Task2() throws JsonProcessingException, IOException, URISyntaxException {
        CovertCSVToRDF(true);
    }


    protected void CovertCSVToRDF(boolean useExternalURI) throws JsonProcessingException, IOException, URISyntaxException {
    	
    	
    	//In a large ontology one would need to find a more automatic way to use the ontology vocabulary. 
        //e.g.,  via matching. In a similar way as we match entities to a large KG like DBPedia or Wikidata
        //Since we are dealing with very manageable ontologies, we can integrate their vocabulary 
        //within the code. E.g.,: lab5_ns_str + City
    	
    	
        
        //We modularize the transformation to RDF. The transformation is tailored to the given table, but 
        //he individual components/mappings are relatively generic (especially type and literal triples).
        
        //Mappings may required one or more columns as input and create 1 or more triples for an entity
    	
    	
    	//COUNTRY TRIPLES
    	//We give subject column and target type
        mappingToCreateTypeTriple(column_index.get("country"), lab5_ns_str + "Country", useExternalURI);
            
        //We give subject and object columns (they could be the same), predicate and datatype 
        mappingToCreateLiteralTriple(column_index.get("country"), column_index.get("country"), lab5_ns_str + "name", XSDDatatype.XSDstring);
                
        mappingToCreateLiteralTriple(column_index.get("country"), column_index.get("iso2"), lab5_ns_str + "iso2code", XSDDatatype.XSDstring);
        
        mappingToCreateLiteralTriple(column_index.get("country"), column_index.get("iso3"), lab5_ns_str + "iso3code", XSDDatatype.XSDstring);
        

        
        //CITY TRIPLES
        //We give subject column and target type
        mappingToCreateTypeTriple(column_index.get("city_ascii"), lab5_ns_str + "City", useExternalURI);
        
        //We give subject and object columns (they could be the same), predicate and datatype 
        mappingToCreateLiteralTriple(column_index.get("city_ascii"), column_index.get("city_ascii"), lab5_ns_str + "name_ascii", XSDDatatype.XSDstring);

        
        mappingToCreateLiteralTriple(column_index.get("city_ascii"), column_index.get("city"), lab5_ns_str + "name", XSDDatatype.XSDstring);
        
        
        mappingToCreateLiteralTriple(column_index.get("city_ascii"), column_index.get("admin_name"), lab5_ns_str + "admin_name", XSDDatatype.XSDstring);
        
        mappingToCreateLiteralTriple(column_index.get("city_ascii"), column_index.get("lat"), lab5_ns_str + "latitude", XSDDatatype.XSDfloat);
        
        mappingToCreateLiteralTriple(column_index.get("city_ascii"), column_index.get("lng"), lab5_ns_str + "longitude", XSDDatatype.XSDfloat);
        
        mappingToCreateLiteralTriple(column_index.get("city_ascii"), column_index.get("population"), lab5_ns_str + "population", XSDDatatype.XSDlong);
            
        
        //Special tailored mapping. We give column for subjects and objects 
        //and the column including the type of capital                
        mappingToCreateCapitalTriple(column_index.get("city_ascii"), column_index.get("country"), column_index.get("capital"));
        
        //Alternative simpler mapping, but it does not consider capital information
        //mappingToCreateObjectTriple(column_index.get("city_ascii"), column_index.get("country"), lab5_ns_str + "cityIsLocatedIn");
        
    }
        
        
    protected String createURIForEntity(String name, boolean useExternalURI) throws JsonProcessingException, IOException, URISyntaxException {
        
        //We create fresh URI (default option)
        stringToURI.put(name, lab5_ns_str + name.replaceAll(" ", "_"));
        
        if (useExternalURI) {//We connect to online KG
            String uri = getExternalKGURI(name);
            if (!uri.equals(""))
            	stringToURI.put(name, uri);
        }
        return stringToURI.get(name);
    
    }
        
    protected String getExternalKGURI(String name) throws JsonProcessingException, IOException, URISyntaxException {
        
        //Approximate solution: We get the entity with highest lexical similarity
        //The use of context may be necessary in some cases        
        
        
        Set<KGEntity> entities = dbpedia.getKGEntities(name, 5);
        //print("Entities from DBPedia:")
        double current_sim = -1.0;
        String current_uri="";
        
        for (KGEntity ent : entities) {           
            double isub_score = isub.score(name, ent.getName()); 
            if (current_sim < isub_score) {
                current_uri = ent.getId();
                current_sim = isub_score;
            }
        }
            
        return current_uri;
    }
            
    
    /**
     * Mapping to create triples like lab5:London rdf:type lab5:City
     * A mapping may create more than one triple    
     * @throws URISyntaxException 
     * @throws IOException 
     * @throws JsonProcessingException 
     */
    protected void mappingToCreateTypeTriple(int subject_column_index, String class_type_uri, boolean useExternalURI) throws JsonProcessingException, IOException, URISyntaxException {
        
    	for (String[] row : csv_file) {
    		
    		//Ignore rows with less elements than expected
    		if (row.length<column_index.size())
    			continue;
    		
    		
    		String subject = row[subject_column_index].toLowerCase();
    		String subject_uri;
    		
    		//We use the ascii name to create the fresh URI for a city in the dataset
    		if (stringToURI.containsKey(subject))
    			subject_uri=stringToURI.get(subject);
            else
                subject_uri=createURIForEntity(subject, useExternalURI);
    		
    		
    		//TYPE TRIPLE    		
    		Resource subject_resource = model.createResource(subject_uri);
    		Resource type_resource = model.createResource(class_type_uri);
    		
    		model.add(subject_resource, RDF.type, type_resource);
    	
    	}
    	
    }
                        
            


    private boolean is_nan(String value) {
        return (!value.equals(value));
    }

    
    
    protected void mappingToCreateLiteralTriple(int subject_column, int object_column, String predicate, XSDDatatype datatype) {
        
    	for (String[] row : csv_file) {
    		
    		//Ignore rows with less elements than expected
    		if (row.length<column_index.size())
    			continue;
    		
    		
    		
    		String subject = row[subject_column];
    		String lit_value = row[object_column];
    		
    		if (is_nan(lit_value))
    			continue;
    		

            //Uri as already created
            String entity_uri = stringToURI.get(subject.toLowerCase());
                
            
            //New triple            
            Resource subject_resource = model.createResource(entity_uri);
            Property predicate_resource = model.createProperty(predicate);
            
    		//Literal
            Literal lit = model.createTypedLiteral(lit_value, datatype);

    		
    		model.add(subject_resource, predicate_resource, lit);
    		
    	}
    	
            
    }
    
    
    
    protected void mappingToCreateObjectTriple(int subject_column, int object_column, String predicate) {
        
    	for (String[] row : csv_file) {
    		
    		//Ignore rows with less elements than expected
    		if (row.length<column_index.size())
    			continue;
    		
    		
    		
    		String subject = row[subject_column];
    		String object = row[object_column];
    		
    		if (is_nan(object))
    			continue;
    		

            //Uri as already created
            String subject_uri = stringToURI.get(subject.toLowerCase());
            String object_uri = stringToURI.get(object.toLowerCase());
                
            
            //New triple            
            Resource subject_resource = model.createResource(subject_uri);
            Property predicate_resource = model.createProperty(predicate);
            Resource object_resource = model.createResource(object_uri);
            
    		
    		model.add(subject_resource, predicate_resource, object_resource);
    		
    	}
    	
            
    }
    
    
    protected void mappingToCreateCapitalTriple(int subject_column, int object_column, int capital_value_column) {
        
    	for (String[] row : csv_file) {
    		
    		//Ignore rows with less elements than expected
    		if (row.length<column_index.size())
    			continue;
    		
    		String subject = row[subject_column];
    		String object = row[object_column];
    		String value = row[capital_value_column];
    		
    		if (is_nan(object))
    			continue;
    		
    		
    		//(default) if value is empty or not expected
            String predicate = lab5_ns_str + "cityIsLocatedIn";
    		if (value.equals("admin"))
                predicate = lab5_ns_str + "isFirstLevelAdminCapitalOf";
    		else if (value.equals("primary"))
                predicate = lab5_ns_str + "isCapitalOf";
    		else if (value.equals("minor"))
				predicate = lab5_ns_str + "isSecondLevelAdminCapitalOf";
    		

            //Uri as already created
            String subject_uri = stringToURI.get(subject.toLowerCase());
            String object_uri = stringToURI.get(object.toLowerCase());
                
            
            //New triple            
            Resource subject_resource = model.createResource(subject_uri);
            Property predicate_resource = model.createProperty(predicate);
            Resource object_resource = model.createResource(object_uri);
            
    		
    		model.add(subject_resource, predicate_resource, object_resource);
    		
    	}
    	
            
    }
    
    
    
     
    
    
    
    
    public void performReasoning(String ontology_file) {
        
        //We expand the graph with the inferred triples
        //Instead of RDFS Semantics, we use approximate OWL 2 reasoner close to OWL 2 RL (but not exactly)
        //More about OWL 2 RL Semantics in lecture/lab 7
        
        //Option 2
		//Uses a RDFS reasoner internally
		//InfModel inf_model = ModelFactory.createRDFSModel(model);
		
		System.out.println("Data triples from CSV: '" + model.listStatements().toSet().size() + "'.");
		        
        //We should load the ontology first        
        Dataset dataset = RDFDataMgr.loadDataset(ontology_file);
        model.add(dataset.getDefaultModel().listStatements().toList());
        
        
        System.out.println("Triples including ontology: '" + model.listStatements().toSet().size() + "'.");
        
        Reasoner reasoner = ReasonerRegistry.getOWLMiniReasoner();		
		inf_model = ModelFactory.createInfModel(reasoner, model);
		
        
        System.out.println("Triples after reasoning: '" + inf_model.listStatements().toSet().size() + "'.");
        
        
    }
    
    
    public void performSPARQLQuery(Model model, String file_query_out) {
    	
    	WriteFile writer = new WriteFile(file_query_out);
    	
        
       String queryStr = 
            "PREFIX lab5: <http://www.semanticweb.org/ernesto/in3067-inm713/lab5/>" +
           	"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
        	"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>" +
           	"SELECT DISTINCT ?country ?city ?pop WHERE {" +
            "?city rdf:type lab5:City ." +
            "?city lab5:isCapitalOf ?country ." +
            "?city lab5:population ?pop ." +
            //"FILTER (?pop > xsd:integer(\"5000000\"))" +
            "FILTER (xsd:integer(?pop) > 5000000)" +
        "}"+
        "ORDER BY DESC(?pop)";

        Query q = QueryFactory.create(queryStr);
		
		QueryExecution qe =
				QueryExecutionFactory.create(q, model);
		try {
			ResultSet res = qe.execSelect();
				
			int solutions = 0;
				
			while( res.hasNext()) {
				solutions++;
				QuerySolution soln = res.next();
				RDFNode country = soln.get("?country");
				RDFNode city = soln.get("?city");
				RDFNode population = soln.get("?pop");
				
				writer.writeLine(country.toString()+","+city.toString()+","+population.toString()+",");
				
			}
			System.out.println(solutions + " capitals satisfying the query.");
			    
		} finally {
			qe.close();
		}
		
		writer.closeBuffer();
    
				
    }
    
    
    
    public void performSPARQLQueryLab7(Model model) {
    	    
    	//Using the generated RDF graph from the World Cities dataset (lab session 5), 
    	//create a SPARQL query that counts the cities in each country.  
    	//Order by number of cities. Test it programmatically.
       String queryStr = 
            "PREFIX lab5: <http://www.semanticweb.org/ernesto/in3067-inm713/lab5/>\n" +
           	"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
        	"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> \n" +
           	"SELECT DISTINCT ?country (COUNT(?city) AS ?num_cities) WHERE { \n" +
            "?country lab5:hasCity ?city .  \n" +
            //"FILTER (?pop > xsd:integer(\"5000000\"))" +
        "\n}"+
        "GROUP BY ?country\n" +
        "ORDER BY DESC(?num_cities)" 
        //"ORDER BY DESC(COUNT(?city))";
        ;

        Query q = QueryFactory.create(queryStr);
		
		QueryExecution qe =
				QueryExecutionFactory.create(q, model);
		try {
			ResultSet res = qe.execSelect();
				
			int solutions = 0;
				
			while( res.hasNext()) {
				solutions++;
				QuerySolution soln = res.next();
				RDFNode country = soln.get("?country");
				RDFNode cities = soln.get("?num_cities");
				
				System.out.println(country.asResource().getURI() + " " + cities.asLiteral().getValue());
				
				
			}
			    
		} finally {
			qe.close();
		}
		
		
    
				
    }
    
    
     
    
    public void saveGraph(Model model, String file_output) throws FileNotFoundException {
        
      //SAVE/SERIALIZE GRAPH
      OutputStream out = new FileOutputStream(file_output);
      RDFDataMgr.write(out, model, RDFFormat.TURTLE);
       
    }	
	

	public static void main(String[] args) {
		
		String file = "files/lab5/solution/worldcities-free-100.csv";
		
		//Format
    	//city    city_ascii    lat    lng    country    iso2    iso3    admin_name    capital    population
    	Map<String, Integer> column_index = new HashMap<String, Integer>();
    	column_index.put("city", 0);
    	column_index.put("city_ascii", 1);
    	column_index.put("lat", 2);
    	column_index.put("lng", 3);
    	column_index.put("country", 4);
    	column_index.put("iso2", 5);
    	column_index.put("iso3", 6);
    	column_index.put("admin_name", 7);
    	column_index.put("capital", 8);
    	column_index.put("population", 9);
    	
    	try {
			Lab5_Solution solution = new Lab5_Solution(file, column_index);
			
			String task;
			//task = "task1";
			task = "task2";
				    
			//Create RDF triples
			if (task.equals("task1"))
				solution.Task1();  //Fresh entity URIs
		    else
		    	solution.Task2();  //Reusing URIs from DBPedia
				    
			//Graph with only data
			solution.saveGraph(solution.model, file.replace(".csv", "-"+task)+".ttl");
				    
			//OWL 2 RL reasoning
			solution.performReasoning("files/lab5/ontology_lab5.ttl");
			//solution.performReasoning("files_lab5/ontology_lab5.owl");
				    
			//#Graph with ontology triples and entailed triples
			solution.saveGraph(solution.inf_model, file.replace(".csv", "-"+task)+"-reasoning.ttl");
				    
			//SPARQL results into CSV
			solution.performSPARQLQuery(solution.inf_model, file.replace(".csv", "-"+task)+"-query-results.csv");				    
			
			
			//SPARQL lab7
			//solution.performSPARQLQueryLab7(solution.inf_model);				    
			
			
			
		} catch (Exception e) {			
			e.printStackTrace();
		}
    	
    	
    	
    	 
    	
	}

}
