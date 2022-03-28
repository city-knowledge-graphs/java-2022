package lab8;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.vocabulary.RDFS;

public class AccessEntityLabels {
	
	OntModel model;
	
	public void loadOntologyFromURL(String sourceURL) {
		
		model = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM );
        model.read(sourceURL, "RDF/XML" );

        
        System.out.println("Number of classes: " + model.listNamedClasses().toList().size());
        
        
	}
	
	public void loadOntologyFromLocalFile(String onto_file) throws FileNotFoundException {
		
		InputStream input_file = new FileInputStream(onto_file);
		
		model = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM );
        model.read(input_file, "RDF/XML" );

        
        System.out.println("Number of classes: " + model.listNamedClasses().toList().size());
        
        
	}
	
	
	public Set<String> getRDFSLabelsForClass(OntClass cls) {
		
		final NodeIterator labels = cls.listPropertyValues(RDFS.label);
		
		Set<String> labels_set =  new HashSet<String>();
		
		while( labels.hasNext() ) {
		    final RDFNode labelNode = labels.next();
		    final Literal label = labelNode.asLiteral();
		    //label.getLanguage(; In case we want to filter by language
		    labels_set.add(label.getString());
		}
		
		return labels_set;
		
	}
	
	
	public void iterateOverLabels() {
		
		for (Iterator<OntClass> i =  model.listClasses(); i.hasNext(); ) {
      	  OntClass c = i.next();
      	  if (!c.isAnon()) {  //To filter complex classes. One could listNamedClasses too
      		  System.out.println(c.getURI() );
      		  System.out.println("\t" + c.getLocalName());  //Access to name in URI (it can be a non informative ID)
      		  System.out.println("\t" + getRDFSLabelsForClass(c) ); //Access to rdfs:label
      		  
      	  }
      }
	}

	public static void main(String[] args) {
		AccessEntityLabels access = new AccessEntityLabels();
		
		String onto_file;
		
		onto_file = "files/lab8/confOf.owl";
		//onto_file = "files/lab8/cmt.owl";
		//onto_file = "files/lab8/ekaw.owl";
		//onto_file = "files/lab8/mouse.owl";
		//onto_file = "files/lab8/human.owl";
							
		try {
		
			access.loadOntologyFromLocalFile(onto_file);
			access.iterateOverLabels();

		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
		
	}

}
