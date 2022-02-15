package lab2;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.vocabulary.RDFS;

public class SolutionExercise2_8 {
	
	public SolutionExercise2_8(String sourceURL) {
	
	 OntModel model = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM );
     model.read( sourceURL, "RDF/XML" );

     
     System.out.println("Number of classes: " + model.listNamedClasses().toList().size());
     
     for (Iterator<OntClass> i =  model.listClasses(); i.hasNext(); ) {
     	  OntClass c = i.next();
     	  if (!c.isAnon()) {  //To filter complex classes
     		  	System.out.println(c.getURI() );
 		  		System.out.println("\tName in URI: " + c.getLocalName());  //Access to name in URI (it can be a non informative ID)
 		  		System.out.println("\tLabels: " + getRDFSLabelsForResource(c) ); //Access to rdfs:label
     	  }
     }
     
     System.out.println("Number of properties: " + model.listAllOntProperties().toList().size());
     
     for (Iterator<OntProperty> i =  model.listAllOntProperties(); i.hasNext(); ) {
    	 OntProperty p = i.next();
     	  if (!p.isAnon()) {  //To filter complex classes
     		  	System.out.println(p.getURI() );
 		  		System.out.println("\tName in URI: " + p.getLocalName());  //Access to name in URI (it can be a non informative ID)
 		  		System.out.println("\tLabels: " + getRDFSLabelsForResource(p) ); //Access to rdfs:label
     	  }
     }
     
     System.out.println("Number of individuals: " + model.listIndividuals().toList().size());
     
     for (Iterator<Individual> i =  model.listIndividuals(); i.hasNext(); ) {
    	 Individual indiv = i.next();
     	  if (!indiv.isAnon()) {  //To filter complex classes
     		  	System.out.println(indiv.getURI() );
 		  		System.out.println("\tName in URI: " + indiv.getLocalName());  //Access to name in URI (it can be a non informative ID)
 		  		System.out.println("\tLabels: " + getRDFSLabelsForResource(indiv) ); //Access to rdfs:label
     	  }
     }
     
	}
	
	
	/**
	 * Extracts rdf:labels associated to a class (if any)
	 * @param cls
	 * @return
	 */
	public Set<String> getRDFSLabelsForResource(OntResource resource) {
		
		final NodeIterator labels = resource.listPropertyValues(RDFS.label);
		
		Set<String> labels_set =  new HashSet<String>();
		
		while( labels.hasNext() ) {
		    final RDFNode labelNode = labels.next();
		    final Literal label = labelNode.asLiteral();
		    //label.getLanguage(; In case we want to filter by language
		    labels_set.add(label.getString());
		}
		
		return labels_set;
		
	}
	
	
	//https://github.com/castagna/jena-examples/tree/master/src/main'
	
	public static void main(String[] args) {
		
		new SolutionExercise2_8("http://protege.stanford.edu/ontologies/pizza/pizza.owl");
		
	}

}
