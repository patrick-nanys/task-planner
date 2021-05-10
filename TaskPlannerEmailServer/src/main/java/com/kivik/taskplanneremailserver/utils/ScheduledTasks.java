package com.kivik.taskplanneremailserver.utils;

import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class ScheduledTasks {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Autowired
    private JavaMailSender javaMailSender;

    @Scheduled(fixedRate = 10000)
    public void runScheduledTask() {
        log.info("Checking for overtime tasks " + dateFormat.format(new Date()));
        checkForOvertimeTasks();
    }

    private void sendEmail(String toMail, String text) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom("team.kivik@outlook.com");
        msg.setTo(toMail);

        msg.setSubject("Heads up! Task passed the deadline!");
        msg.setText(text);

        javaMailSender.send(msg);
        log.info("Email sent to: " + toMail);
    }

    private JSONObject performGetRequest(String url) {
        CredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("admin", "admin");
        provider.setCredentials(AuthScope.ANY, credentials);

        HttpClient client = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();

        HttpResponse response;
        try {
            response = client.execute(new HttpGet(url));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != 200) {
            log.error("There was an error querying the data! Status code: " + statusCode);
            return null;
        }

        String responseString;
        try {
            responseString = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return new JSONObject(responseString);
    }

    private void performPutRequest(String url, String json) {
        CredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("admin", "admin");
        provider.setCredentials(AuthScope.ANY, credentials);

        HttpClient client = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();

        HttpPut httpPut = new HttpPut(url);
        httpPut.setHeader("Accept", "application/json");
        httpPut.setHeader("Content-type", "application/json");
        StringEntity stringEntity;
        try {
            stringEntity = new StringEntity(json);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }
        httpPut.setEntity(stringEntity);

        HttpResponse response;
        try {
            response = client.execute(httpPut);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != 200) {
            log.error("There was an error putting the data! Status code: " + statusCode);

            String responseString;
            try {
                responseString = EntityUtils.toString(response.getEntity());
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            log.info("Put request response was: " + responseString);
        }
    }

    private void updateTaskReminderSetting(Long taskId, JSONObject taskJson) {
        Task task = new Task(
                (String) taskJson.get("name"),
                (String) taskJson.get("description"),
                (String) taskJson.get("deadline"),
                true);
        String updateTaskJson = new Gson().toJson(task);
        performPutRequest("http://localhost:8080/task/" + taskId, updateTaskJson);
        log.info("Updated task reminder for task with id: " + taskId);
    }

    private void constructAndSendEmail(JSONObject taskJson) {
        String assignedUserUrl = (String) ((JSONObject) ((JSONObject) taskJson.get("_links")).get("assignedUser")).get("href");
        String taskName = (String) taskJson.get("name");

        // Extract task id
        String part1 = assignedUserUrl.substring(0, assignedUserUrl.lastIndexOf('/'));
        Long taskId = Long.parseLong(part1.substring(part1.lastIndexOf('/') + 1));
        updateTaskReminderSetting(taskId, taskJson);

        // Get assigned user data
        JSONObject jsonObject = performGetRequest(assignedUserUrl);
        if (jsonObject == null) {
            log.error("There was an error with receiving a json object from the assigned user get request!");
            return;
        }
        String userFirstName = (String) jsonObject.get("firstName");
        String userEmail = (String) jsonObject.get("email");

        // Construct message
        String message =
                "Dear " + userFirstName + ",\n\n" +
                "We kindly inform you that your \n" +
                taskName + "\n" +
                "task is over it's deadline.\n\n" +
                "Keep up the good work,\n" +
                "kiVIK team";
        sendEmail(userEmail, message);
    }

    private void checkForOvertimeTasks() {
        JSONObject jsonObject = performGetRequest("http://localhost:8080/task");
        if (jsonObject == null) {
            log.error("There was an error with receiving a json object from the task get request!");
            return;
        }

        Date currentTime = new Date();
        JSONArray taskList = (JSONArray) ((JSONObject)jsonObject.get("_embedded")).get("task");
        for (Object taskObject : taskList) {
            JSONObject taskJson = (JSONObject) taskObject;
            String deadlineStr = (String) taskJson.get("deadline");
            Date deadlineDate;
            try {
                 deadlineDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(deadlineStr.replace("T"," "));
            } catch (ParseException e) {
                e.printStackTrace();
                continue;
            }
            if (deadlineDate.compareTo(currentTime) < 0 && taskJson.get("deadlineReminderTriggered").equals(false)) {
                log.info("Deadline passed for task: " + taskJson.get("name") + " sending email...");
                constructAndSendEmail(taskJson);
            }
        }
    }
}
