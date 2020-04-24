package logic;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.json.JSONArray;
import org.json.JSONObject;

public class ReadLog {
	private String path;

	public ReadLog(String path) {
		this.path = path;
	}

	public JSONObject fetchDates(JSONArray issues) {
		int total = issues.length(), totalCommit = 0;
		String date;

		JSONObject mainJO = new JSONObject();
		
		JSONArray ja = new JSONArray();

		for (int i = 0; i < total; i++) {
			String key = issues.getJSONObject(i).get("key").toString();
			date = dateFromTicketKey(key);
			if (date.equals("")) {
				// non esiste commit associato al ticket, scarto il ticket e vado avanti
			} else {
				totalCommit ++;
				JSONObject jo = new JSONObject();
				jo.put("key", key);
				jo.put("date", date);
				ja.put(jo);
			}
		}
		mainJO.put("total", totalCommit);
		mainJO.put("issues", ja);

		return mainJO;
	}

	private String dateFromTicketKey(String ticket) {
		/* Si effettua una ricerca su log del ticket specificato, comando eseguito attraverso linea di comando.
		 * Per ogni richiesta si restituisce la DATA del commit più aggiornato (l'ultimo, basato sulla data) associato al ticket inserito.
		 * Lo '\b' inserito nel comando sta ad indicare di resituire esattamente il ticket richiesto e non tutti i simili.
		 * Il formato della data sarà YYYY/MM/DD
		 *  */
		String date = "";
		try {
			File dir = new File(path);

			String cmd = "git log --grep=\""+ticket+"\\b\" --date=short --date-order --pretty=format:\"%ad\""; 
			Process pr = Runtime.getRuntime().exec(cmd, null, dir);

			BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));

			String line = null;
			//while ((line = input.readLine()) != null) {
			if((line = input.readLine()) != null) {
				date = line; //debug
			} else {
				//il comando non ha trovato un commit associato al ticket
				date = "";
			}
		}
		catch (Exception e)
		{
			System.out.println(e.toString());
			e.printStackTrace();
		}
		return date;
	}
}