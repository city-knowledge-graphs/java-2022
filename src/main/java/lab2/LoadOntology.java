package lab2;


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

        
        System.out.println("Number of classes: " + model.listNamedClasses().toList().size());
        
        for (Iterator<OntClass> i =  model.listClasses(); i.hasNext(); ) {
        	  OntClass c = i.next();
        	  if (!c.isAnon())  //To filter complex classes
        		  System.out.println( c.getURI() );
        }
        
	}
	
	//https://github.com/castagna/jena-examples/tree/master/src/main'
	
	public static void main(String[] args) {
		
		new LoadOntology("http://protege.stanford.edu/ontologies/pizza/pizza.owl");
		
	}

}
