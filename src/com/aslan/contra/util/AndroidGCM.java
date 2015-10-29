package com.aslan.contra.util;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.rmi.server.UID;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class AndroidGCM {
	//This is our new project detail
//	private String APP_NAME = "ConTra";
//	private String API_KEY = "AIzaSyAYc-ACqE0IlT6cEbQ2W9ykqyw0It79Tzc";
//	private String SENDER_ID = "189417379323";
	
	//this is our old project data used only for testing on Vishnu's mobile
	private String APP_NAME = "ConTra";
	private String API_KEY = "AIzaSyBgJ6BDMOxDgZ8GLlHZTava2BeAsyZ9QGI";
	private String SENDER_ID = "986180772600";

	private String messagePayload = "success";
	private static final String BASE_URL = "https://android.googleapis.com/gcm/send";
	private List<String> devicesIds;
	private String deviceId = "APA91bGh4b6ymUKcqfVGTr3WB5E78OZ6dcackbxtKzcPSrOKuF0LDeBtLvL_B7eN07Ryjq_AtluEbey-wtJ5qUp9PpjCOhZthb35kyMThI8byk0jpgoWuCT162rJGpVTBA0PolGZxtO6z-T_8rMbAW49sVY_rI4Zjw";

	//TODO remove main and use the call where needed
	public static void main(String[] args) {
		AndroidGCM androidGCM = new AndroidGCM("success", "APA91bGh4b6ymUKcqfVGTr3WB5E78OZ6dcackbxtKzcPSrOKuF0LDeBtLvL_B7eN07Ryjq_AtluEbey-wtJ5qUp9PpjCOhZthb35kyMThI8byk0jpgoWuCT162rJGpVTBA0PolGZxtO6z-T_8rMbAW49sVY_rI4Zjw");
		System.out.println(androidGCM.executeHTTPSConnectionBuilder());
	}
	
	public AndroidGCM(String messagePayload, String deviceId) {
		super();
		this.messagePayload = messagePayload;
		this.deviceId = deviceId;
	}

	public AndroidGCM(String messagePayload, List<String> devicesIds) {
		super();
		this.messagePayload = messagePayload;
		this.devicesIds = devicesIds;
	}

	public Integer executeHTTPSConnectionBuilder() {
		Integer responseCode = 0;
		try {
			URL url = new URL(BASE_URL);
			responseCode = post(getPostHTTPConnection(url, "application/json"), getJSONPayload());

		} catch (Exception exception) {
		}
		return responseCode;
	}

	private HttpURLConnection getPostHTTPConnection(URL url, String contentType) throws Exception {
		HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
		httpURLConnection.setRequestMethod("POST");

		if (!contentType.isEmpty())
			httpURLConnection.setRequestProperty("Content-Type", contentType);

		httpURLConnection.setRequestProperty("Authorization", " key=" + API_KEY);

		httpURLConnection.setDoOutput(true);
		httpURLConnection.setUseCaches(false);

		return httpURLConnection;
	}

	private Integer post(HttpURLConnection httpURLConnection, JSONObject jsonObject) {
		Integer responseCode = 0;
		OutputStream outputStream = null;
		try {
			outputStream = httpURLConnection.getOutputStream();
			outputStream.write(jsonObject.toString().getBytes());
			responseCode = httpURLConnection.getResponseCode();
		} catch (Exception exception) {
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (Exception e) {
				}
			}
		}
		return responseCode;
	}

	@SuppressWarnings("unchecked")
	private JSONObject getJSONPayload() {
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		JSONObject subJsonObject = new JSONObject();
		subJsonObject.put("score", "4x8");
		subJsonObject.put("time", getTime());
		subJsonObject.put("message", messagePayload);
		if (devicesIds != null && devicesIds.size() > 0) {
			for (String deviceId : devicesIds)
				jsonArray.put(deviceId);
		} else {
			jsonArray.put(deviceId);
		}
		jsonObject.put("registration_ids", jsonArray);
		jsonObject.put("collapse_key", new UID().toString());
		jsonObject.put("data", subJsonObject);

		System.out.println(jsonObject.toString());

		return jsonObject;
	}

	private String getTime() {
		DateFormat dateFormat = new SimpleDateFormat("HH:mm");
		Date date = new Date();

		return dateFormat.format(date);
	}
}
