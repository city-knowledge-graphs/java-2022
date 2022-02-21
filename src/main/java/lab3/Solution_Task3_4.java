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
public class Solution_Task3_4 {
	
	//Doc: https://jena.apache.org/tutorials/rdf_api.html
	
	public Solution_Task3_4(String file) throws FileNotFoundException {
		
		// create an empty Model
		Model model = ModelFactory.createDefaultModel();
		
		//This namepace is created as example for the lab
		String city_ns = "http://www.example.org/university/london/city#";
		
		//These namespaces exist within the FOAF vocabulary and DBPedia KG
		String foaf_ns = "http://xmlns.com/foaf/0.1/";
		String dbo_ns = "http://dbpedia.org/ontology/";
		String dbr_ns = "http://dbpedia.org/resource/";
		String dbp_ns = "http://dbpedia.org/property/";
		
		//Set prefixes
		model.setNsPrefix("city", city_ns);
		model.setNsPrefix("foaf", foaf_ns);
		model.setNsPrefix("dbo", dbo_ns);
		model.setNsPrefix("dbr", dbr_ns);
		model.setNsPrefix("dbp", dbp_ns);
		model.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		
		
		// create the resources
		Resource ernesto = model.createResource(city_ns+"ernesto");
		Resource person = model.createResource(foaf_ns+"Person");
				
		Resource inm713 = model.createResource(city_ns+"inm713");
		Resource module = model.createResource(city_ns+"Module");
		
		Resource castellon = model.createResource(dbr_ns + "Castellón_de_la_Plana");
		Resource spain = model.createResource(dbr_ns + "Spain");
		Resource city_university = model.createResource(dbr_ns + "City,_University_of_London");
		Resource spanish = model.createResource(dbr_ns + "English_language");
		Resource english = model.createResource(dbr_ns + "Spanish_language");
		Resource italian = model.createResource(dbr_ns + "Italian_language");

		
		
		//Special type of resource
		Property teaches = model.createProperty(city_ns+"teaches");
		Property name = model.createProperty(foaf_ns+"givenName");
		Property familyname = model.createProperty(foaf_ns+"familyName");
		Property year = model.createProperty(dbo_ns+"year");
		Property startDate = model.createProperty(dbo_ns+"startDate");
		
		Property birthPlace = model.createProperty(dbo_ns+"birthPlace");
		Property nationality = model.createProperty(dbo_ns+"nationality");
		Property employer = model.createProperty(dbp_ns+"employer");
		Property speaks = model.createProperty(city_ns+"speaks");
		
		
		//Literals
		Literal name_lit = model.createTypedLiteral("Ernesto", XSDDatatype.XSDstring);
		Literal lastname_lit = model.createTypedLiteral("Jimenez Ruiz", XSDDatatype.XSDstring);
		Literal year_lit = model.createTypedLiteral("2021", XSDDatatype.XSDgYear);
		Literal date_lit = model.createTypedLiteral("2019-09-23T00:00:00", XSDDatatype.XSDgYear);
		//Literal d = model.createLiteral("Germany","en");  //lan tag
		
		//Blank node2
		Resource blank1 =  model.createResource();
		Resource blank2 =  model.createResource();
				
	    
		//Adds triples to model
	    model.add(ernesto, RDF.type, person);
	    model.add(inm713, RDF.type, module);
	    model.add(ernesto, name, name_lit);
		model.add(ernesto, familyname, lastname_lit);

		
		model.add(ernesto, birthPlace, castellon);
		model.add(ernesto, nationality, spain);
		model.add(ernesto, speaks, spanish);
		model.add(ernesto, speaks, italian);
		model.add(ernesto, speaks, english);
		
		
		
		//Triples with annotation
		model.add(ernesto, teaches, inm713);
		model.add(ernesto, employer, city_university);
		
		
		//Reification for ernesto teaches inm713
		model.add(blank1, RDF.type, RDF.Statement);
		model.add(blank1, RDF.subject, ernesto);
		model.add(blank1, RDF.predicate, teaches);
		model.add(blank1, RDF.object, inm713);
		model.add(blank1, year, year_lit);
		
		//Reification for ernesto employer city
		model.add(blank2, RDF.type, RDF.Statement);
		model.add(blank2, RDF.subject, ernesto);
		model.add(blank2, RDF.predicate, employer);
		model.add(blank2, RDF.object, city_university);
		model.add(blank2, startDate, date_lit);
		
		
		
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
			new Solution_Task3_4("files/lab3/solution/Solution_Task3.4_jena.ttl");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


}
