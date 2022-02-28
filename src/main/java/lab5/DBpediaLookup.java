/*******************************************************************************
 * 
 *  Copyright 2018 by The Alan Turing Institute
 * 
 *
 *******************************************************************************/
package lab5;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.http.client.utils.URIBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;



/**
 *
 * Class to access the DBpedia look up REST API: https://github.com/dbpedia/lookup
 *
 * @author ernesto
 * Created on 23 Jul 2018
 *
 */
public class DBpediaLookup extends LookupService{
		
	//Old one
	//private final String REST_URL = "http://lookup.dbpedia.org/api/search/KeywordSearch?";
	//Old parameters:
	//private final String MaxHits = "MaxHits";
	//private final String QueryString = "QueryString";
	//private final String QueryClass = "QueryClass";
	
	//private final String REST_URL = "http://akswnc7.informatik.uni-leipzig.de/lookup/api/search?";
	//private final String REST_URL = "http://lookup.dbpedia.org/api/search?";
	private final String REST_URL =   "https://lookup.dbpedia.org/api/search?";
	
	private final String MaxHits = "maxResults";
	private final String QueryString = "query";
	private final String QueryClass = "typeName";
	private final String Format = "format";
	//Not supported
	//private final String language = "language";	
	
	
	private int hits=5;
	//private String query;
	
	
	
	
	
	
		
	public Set<KGEntity> getKGEntities(String query) throws JsonProcessingException, IOException, URISyntaxException{
		return getKGEntities(query,"", hits);
	}
	
	public Set<KGEntity> getKGEntities(String query, int max_hits) throws JsonProcessingException, IOException, URISyntaxException{
		return getKGEntities(query,"", max_hits);
	}
	
	public Set<KGEntity> getKGEntities(String query, String type) throws JsonProcessingException, IOException, URISyntaxException{
		return getKGEntities(query,type, hits);
	}

	
	
	/**
	 * Return a Map with "key"=URIs containing the DBPedia entities related to the query string, and "values"=sets of (ontology) class uris
	 * @param query
	 * @return
	 * @throws IOException 
	 * @throws JsonProcessingException 
	 * @throws URISyntaxException 
	 */
	public Set<KGEntity> getKGEntities(String query, String cls_type, int max_hits) throws JsonProcessingException, IOException, URISyntaxException{
		
		
		Set<KGEntity> entities = new HashSet<KGEntity>();
		
		//String urlToGet = REST_URL + QueryClass + "=" + cls_type + "&" + MaxHits + "="+ max_hits + "&" + QueryString + "=" + query;
		URL urlToGet = buildRequestURL(query, cls_type, max_hits);
		
		//System.out.println(urlToGet);
		
		JsonNode results = jsonToNode(getRequest(urlToGet));
		

		//System.out.println(results);
		
		if (results.has("docs")){
			
			for (JsonNode result : results.get("docs")){
				
				//System.out.println(result);
				
				if (result.has("resource")) {//expected only one
					
					KGEntity entity = new KGEntity();
					
					for (JsonNode uri : result.get("resource")) {
						entity.setId(uri.asText());
					}
					
					if (result.has("type")) {
						for (JsonNode cls : result.get("type")){
							
							if (!cls.asText().equals("http://www.w3.org/2002/07/owl#Thing"))
								entity.addType(cls.asText());
							
						}
					}
					
					if (result.has("label")) {//expected only one
						
						for (JsonNode label : result.get("label")) {
							entity.setName(label.asText());
						}
					}
					
					if (result.has("comment")) {//expected only one
						
						for (JsonNode comment : result.get("comment")) {
							entity.setDescription(comment.asText());
						}
					}
					
					if (result.has("score")) {//expected only one
						
						for (JsonNode score : result.get("score")) {
							entity.setScore(score.asDouble());
						}
					}
					
					
					entities.add(entity);
					
				}
			}
		}
		
		return entities;
			
		
		

		
	}
	
	
	
	protected String getREST_URL() {
		return REST_URL;
	}
	
	
	protected URL buildRequestURL(String query, String cls_type, int max_hits) throws URISyntaxException, MalformedURLException{
		URIBuilder ub = new URIBuilder(getREST_URL());
		if (cls_type!=null && !cls_type.equals(""))
			ub.addParameter(QueryClass, cls_type);
		
		ub.addParameter(MaxHits, String.valueOf(max_hits));
		ub.addParameter(QueryString, query);
		ub.addParameter(Format, "json");  //neded in new API
				
		return ub.build().toURL();
	}
	
	
	
	
	
	
	
	
	public static void main(String[] args){
		DBpediaLookup lookup = new DBpediaLookup();
		
		String word;		
		//word="Chicago Bulls";
		//word="Congo";
		word="City University London";
		
		
		try {
			
			Set<KGEntity> entities = lookup.getKGEntities(word);
			System.out.println(entities.size());
			for (KGEntity entity : entities){
				System.out.println(entity);
			}
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	

}
