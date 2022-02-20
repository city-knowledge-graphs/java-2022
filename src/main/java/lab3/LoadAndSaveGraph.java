package lab3;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;


/**
 * 
 * @author ernesto
 *
 */
public class LoadAndSaveGraph {
	
	public LoadAndSaveGraph(String file, String output_file, RDFFormat target_format) throws FileNotFoundException {
		
		Dataset dataset = RDFDataMgr.loadDataset(file);
		Model model = dataset.getDefaultModel();
				
		StmtIterator iter = model.listStatements();
		       
        
        System.out.println("The graph contains '" + model.listStatements().toSet().size() + "' triples.");
        
        
        System.out.println("Printing '" + model.listStatements().toSet().size() + "' triples.");
        
        try {
            while ( iter.hasNext() ) {
                Statement stmt = iter.next();
                
                Resource s = stmt.getSubject();
                Resource p = stmt.getPredicate();
                RDFNode o = stmt.getObject();
                
                //System.out.println(s.getURI() + " " + p.getURI() + " " + o.toString());
                System.out.println(s.toString() + " " + p.getURI() + " " + o.toString());
            }
        } finally {
            if ( iter != null ) iter.close();
        }
        
        
        
        //Storing in RDF/xml
        OutputStream out = new FileOutputStream(output_file);
        RDFDataMgr.write(out, model, target_format);
		
	}

	public static void main(String[] args) {
		try {
			//new LoadAndSaveGraph("files/lab3/beatles.ttl", "files/lab3/beatles.rdf", RDFFormat.RDFXML); 
			new LoadAndSaveGraph("files/lab3/ernesto_foaf.rdf", "files/lab3/ernesto_foaf.ttl", RDFFormat.TURTLE);
			new LoadAndSaveGraph("files/lab3/solution/Solution_Task3.1.ttl", "files/lab3/solution/Solution_Task3.1.rdf", RDFFormat.RDFXML);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
