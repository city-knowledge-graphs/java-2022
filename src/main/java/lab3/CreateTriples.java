package lab3;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.OWL;

/**
 * 
 * @author ernesto
 *
 */	
public class CreateTriples {
	
	//Doc: https://jena.apache.org/tutorials/rdf_api.html
	
	public CreateTriples(String file) throws FileNotFoundException {
		
		// create an empty Model
		Model model = ModelFactory.createDefaultModel();
		
		//Set prefixes
		model.setNsPrefix("city", "http://www.example.org/university/london/city#");
		model.setNsPrefix("foaf", "http://xmlns.com/foaf/0.1/");
		model.setNsPrefix("dbpo", "https://dbpedia.org/ontology/");
		
		
		String city_ns = "http://www.example.org/university/london/city#";
		String foaf_ns = "http://xmlns.com/foaf/0.1/";
		String dbpo_ns = "http://dbpedia.org/ontology/";
		
		// create the resources
		Resource ernesto = model.createResource(city_ns+"ernesto");
		Resource person = model.createResource(foaf_ns+"Person");
				
		Resource inm713 = model.createResource(city_ns+"inm713");
		Resource module = model.createResource(city_ns+"Module");
		
		//Special type of resource
		Property teaches = model.createProperty(city_ns+"teaches");
		Property name = model.createProperty(foaf_ns+"name");
		Property year = model.createProperty(dbpo_ns+"year");
		
		//Literals
		Literal name_lit = model.createTypedLiteral("Ernesto Jimenez Ruiz", XSDDatatype.XSDstring);
		Literal year_lit = model.createTypedLiteral("2021", XSDDatatype.XSDgYear);
		//Literal d = model.createLiteral("Germany","en");  //lan tag
		
		//Blank node
		Resource blank =  model.createResource();
				
	    
		//Adds triples to model
	    model.add(ernesto, RDF.type, person);
		model.add(ernesto, teaches, inm713);
		model.add(ernesto, name, name_lit);
		model.add(inm713, RDF.type, module);
		
		model.add(blank, RDF.type, RDF.Statement);
		model.add(blank, RDF.subject, ernesto);
		model.add(blank, RDF.predicate, teaches);
		model.add(blank, RDF.object, inm713);
		model.add(blank, year, year_lit);
		
		
		//Create statements or triples
		//Statement stmt = model.createStatement(ernesto,name,name_lit);
		//model.add(stmt);
		
		System.out.println("The graph contains '" + model.listStatements().toSet().size() + "' triples.");
        
        //Storing in RDF/xml
        OutputStream out = new FileOutputStream(file);
        RDFDataMgr.write(out, model, RDFFormat.TTL);
		
		
		
	}
	
	public static void main(String[] args) {
		try {
			new CreateTriples("files/lab3/lab3_example_jena.ttl");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


}
