//package edu.rpi.tw.impav;
//
//import com.hp.hpl.jena.query.Query;
//import com.hp.hpl.jena.query.QueryExecutionFactory;
//import com.hp.hpl.jena.query.QueryFactory;
//import com.hp.hpl.jena.query.QuerySolution;
//import com.hp.hpl.jena.query.ResultSet;
//import com.hp.hpl.jena.rdf.model.Literal;
//import com.hp.hpl.jena.rdf.model.RDFNode;
//import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;
//import java.io.*;
//import java.util.*;
//
//public class QuerySparql {
//		
//	private String service = null;
//	private String apikey = null;
//
//	public QuerySparql(String service, String apikey) {
//		this.service = service;
//		this.apikey = apikey;
//	}
//	public ResultSet executeQuery(String queryString) throws Exception {
//		 Query query = QueryFactory.create(queryString) ;
//
//		 QueryEngineHTTP qexec = QueryExecutionFactory.createServiceRequest(this.service, query);
//		 qexec.addParam("apikey", this.apikey);
//		 ResultSet results = qexec.execSelect() ;
//		 return results;
//
//	}
//	public static void main(String[] args) throws Exception {
//		String sparqlService = "http://sparql.bioontology.org/sparql";
//		String apikey = "b2aa80e5-8af9-4cc8-9226-55547c5faa65";
//		String fileName = "query.txt";
//
//		/*
//		 * More query examples here:
//		 * http://sparql.bioontology.org/examples
//		 */
//		String query = getQuery(fileName);
//
//		QuerySparql test = new QuerySparql(sparqlService,apikey);
//		ResultSet results = test.executeQuery(query);
//		    for ( ; results.hasNext() ; ) {
//		      QuerySolution soln = results.nextSolution() ;
//		      RDFNode ontUri = soln.get("ont") ;
//		      Literal name = soln.getLiteral("name") ;
//		      Literal acr = soln.getLiteral("acr") ;
//		      System.out.println(ontUri + " ---- " + name + " ---- " + acr);
//		    }
//	}
//
//	private static String getQuery(String fileName) {
//		
//		String queryString = null;
//		
//		try{
//			BufferedReader in  = new BufferedReader(new FileReader(fileName));
//			String line = null;
//			queryString = in.readLine();
//			while((line = in.readLine()) != null){
//				queryString = queryString.concat("\n");			
//				queryString = queryString.concat(line);
//			}
//			
//			in.close();
//					
//			System.out.println("Query:" + queryString);
//		} catch( IOException e){
//			System.err.println(e);
//			System.exit(1);
//		}
//		
//		return queryString;
//
//	}
//
//}