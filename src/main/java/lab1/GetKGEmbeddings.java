package lab1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetKGEmbeddings {
	
	public GetKGEmbeddings(String kg_entity) throws IOException {
		
		HttpURLConnection conn;
		BufferedReader rd;
		String line;
		String result = "";
		
		String urlToGet = "http://www.kgvec2go.org/rest/get-vector/dbpedia/" + kg_entity; 

		
		conn = getConnection(urlToGet);
		
		rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

		while ((line = rd.readLine()) != null) {
			result += line;
		}
		rd.close();
		
		System.out.println(result);
		
		
	}
	
	private HttpURLConnection getConnection(String urlToGet) throws IOException {

		URL url;
		HttpURLConnection conn;

		url = new URL(urlToGet);
		conn = (HttpURLConnection) url.openConnection();

		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");

		return conn;

	}
	

	public static void main(String[] args) {
		
		//http://dbpedia.org/resource/Chicago_Bulls
		String kg_entity = "Chicago_Bulls";
		
		try {
			new GetKGEmbeddings(kg_entity);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
