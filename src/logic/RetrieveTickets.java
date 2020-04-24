package logic;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import org.apache.commons.io.FileUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.CDL;
import org.json.JSONArray;

public class RetrieveTickets {
	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	public static JSONArray readJsonArrayFromUrl(String url) throws IOException, JSONException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			JSONArray json = new JSONArray(jsonText);
			return json;
		} finally {
			is.close();
		}
	}

	public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			JSONObject json = new JSONObject(jsonText);
			return json;
		} finally {
			is.close();
		}
	}
	
	public static void insertInCsv(JSONObject keyDate) throws IOException {
		String pathCsv = "C:/Users/Flavio/Desktop/milestone/keyDate.csv";
		File file = new File(pathCsv);
		JSONArray issues;
		issues = keyDate.getJSONArray("issues");
		
		//String csv = CDL.rowToString(issues);
		String csv = CDL.toString(issues);
		FileUtils.writeStringToFile(file, csv);
	}
	
	public static JSONArray fixedBugFromJira(String projName, String path) throws JSONException, IOException {
		int total;
		JSONObject json;
		String url = "";
		String url_total = "https://issues.apache.org/jira/rest/api/2/search?jql=project=%22"
					+ projName + "%22AND%22issueType%22=%22Bug%22AND(%22status%22=%22closed%22OR"
					+ "%22status%22=%22resolved%22)AND%22resolution%22=%22fixed%22&fields=key,resolutiondate,versions,created"
					+ "&startAt=0&maxResults=0";
		
		json = readJsonFromUrl(url_total);
		total = json.getInt("total");
		//System.out.println("Numero ticket " + total); //debug
		url = "https://issues.apache.org/jira/rest/api/2/search?jql=project=%22"
					+ projName + "%22AND%22issueType%22=%22Bug%22AND(%22status%22=%22closed%22OR"
					+ "%22status%22=%22resolved%22)AND%22resolution%22=%22fixed%22&fields=key,resolutiondate,versions,created"
					+ "&startAt=0&maxResults=" + total+1;
	
		json = readJsonFromUrl(url);
		JSONArray issues = json.getJSONArray("issues");
		return issues;
	}

	public static void main(String[] args) throws IOException, JSONException {
		String projName ="Samza";
		String path = "C:/Users/Flavio/Desktop/milestone/samza";
		JSONArray issues;
		JSONObject ticketDate;
		ReadLog rl = new ReadLog(path);
		
		System.out.println("Obtaining ticked of type fixed bugs from JIRA..");
		issues = fixedBugFromJira(projName, path);
		System.out.println("Success.");
		
		System.out.println("Reading dates from git log..");
		ticketDate = rl.fetchDates(issues);
		System.out.println("Success.");
		
		
		System.out.println("Writing in a csv file..");
		insertInCsv(ticketDate);
		System.out.println("Success. You can read the csv file.");

		return;   
	}
}
