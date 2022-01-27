package lab1;


import java.util.Iterator;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;


/**
 * 
 * @author ernesto
 *
 */
public class LoadOntology {

	public LoadOntology(String sourceURL) {
		
        OntModel model = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM );
        model.read( sourceURL, "RDF/XML" );

        
        System.out.println("Number of classes: " + model.listClasses().toList().size());
        
        for (Iterator<OntClass> i =  model.listClasses(); i.hasNext(); ) {
        	  OntClass c = i.next();
        	  System.out.println( c.getURI() );
        }
        
	}
	
	//https://github.com/castagna/jena-examples/tree/master/src/main'
	
	public static void main(String[] args) {
		
		new LoadOntology("http://www.cs.ox.ac.uk/isg/ontologies/dbpedia.owl");
		
	}

}
