package logic;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.CDL;
import org.json.JSONArray;

import utils.Logging;

public class RetrieveTickets {
	private static final Logger LOGGER = Logger.getLogger(RetrieveTickets.class.getName());
	private static final String pathCsv = System.getProperty("user.home") + "\\Google Drive\\milestone\\keyData.csv";
	private static final String path = System.getProperty("user.home") + "\\Google Drive\\milestone\\samza";
	private static final String projName ="Samza";
	
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
		try (BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)))
		{
			String jsonText = readAll(rd);
			return new JSONArray(jsonText);
		} finally {
			is.close();
		}
	}

	public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
		InputStream is = new URL(url).openStream();
		try (BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)))
		{
			String jsonText = readAll(rd);
			return new JSONObject(jsonText);
		} finally {
			is.close();
		}
	}
	
	public static void insertInCsv(JSONObject keyDate) throws IOException {
		File file = new File(pathCsv);
		JSONArray issues;
		issues = keyDate.getJSONArray("issues");
		
		String csv = CDL.toString(issues);
		FileUtils.writeStringToFile(file, csv);
	}
	
	public static JSONArray fixedBugFromJira(String projName) throws JSONException, IOException {
		int total;
		JSONObject json;
		String url = "";
		String urlTotal = "https://issues.apache.org/jira/rest/api/2/search?jql=project=%22"
					+ projName + "%22AND%22issueType%22=%22Bug%22AND(%22status%22=%22closed%22OR"
					+ "%22status%22=%22resolved%22)AND%22resolution%22=%22fixed%22&fields=key,resolutiondate,versions,created"
					+ "&startAt=0&maxResults=0";
		
		json = readJsonFromUrl(urlTotal);
		total = json.getInt("total");
		url = "https://issues.apache.org/jira/rest/api/2/search?jql=project=%22"
					+ projName + "%22AND%22issueType%22=%22Bug%22AND(%22status%22=%22closed%22OR"
					+ "%22status%22=%22resolved%22)AND%22resolution%22=%22fixed%22&fields=key,resolutiondate,versions,created"
					+ "&startAt=0&maxResults=" + total+1;
	
		json = readJsonFromUrl(url);
		return json.getJSONArray("issues");
	}

	public static void main(String[] args) throws IOException, JSONException {
		JSONArray issues;
		JSONObject ticketDate;
		ReadLog rl = new ReadLog(path);
		Logging lgg = new Logging(LOGGER);
		
		lgg.configOutputLogger();
		
		lgg.showOutput("Obtaining ticket of type fixed bugs from JIRA..");
		issues = fixedBugFromJira(projName);
		lgg.showOutput("Success.");
		
		
		lgg.showOutput("Reading dates from git log..");
		ticketDate = rl.fetchDates(issues);
		lgg.showOutput("Success.");
		
		
		lgg.showOutput("Writing in a csv file..");
		insertInCsv(ticketDate);
		lgg.showOutput("Success. You can read the csv file.");
	}
}
