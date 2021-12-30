package com.hubex.learningsystem.app.controllers;

import com.hubex.learningsystem.app.models.entities.CourseEntity;
import com.hubex.learningsystem.app.models.repositories.CourseRepository;
import com.hubex.learningsystem.app.models.requests.webex.CreateMeetingRequest;
import com.hubex.learningsystem.app.models.requests.webex.DeleteMeetingRequest;
import com.hubex.learningsystem.app.models.requests.webex.GetMeetingsRequest;
import com.hubex.learningsystem.app.models.requests.webex.IntegrationRequest;
import com.hubex.learningsystem.security.models.entities.UserEntity;
import com.hubex.learningsystem.security.models.repositories.UserRepository;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/webex")
public class WebexController {
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    private final String tokenSecret = "Bearer MzA0OGNhZmQtNTZjOC00NDU5LTliMzYtMTNmMjY5MDIzZTM1N2QxZWVhNGEtYmFk_P0A1_cf268832-04e7-4280-8df2-96e676257777";
    private final String clientId = "Cb469f9350d574789b2363b5e7b9553e56cd40283bb7d9234ec9f3c13b64b41dd";
    private final String clientSecret = "53a3fa784654365737bd00dd4add1bf7aff2a3046a2bce606de4910d3d34e06e";

    public WebexController(UserRepository userRepository, CourseRepository courseRepository) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    @PostMapping("/{courseId}/get-meetings")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> getMeetings(@RequestBody GetMeetingsRequest request, @PathVariable String courseId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        UserEntity loggedUser = userRepository.findByEmail(currentPrincipalName).orElse(null);

        if (loggedUser == null) {
            throw new RuntimeException("Zaloguj się aby kontynuować");
        }
        if (loggedUser.getTeacherCourses().stream().noneMatch(course -> course.getId().equals(Long.valueOf(courseId)))) {
            throw new SecurityException("Wygląda na to że nie posiadasz kursu o podanym id");
        } else {
            CourseEntity course = courseRepository.findById(Long.valueOf(courseId)).orElse(null);

            if(course == null) {
                throw new NullPointerException("Nie znaleziono kursu o podanym id");
            }

            HttpGet get = new HttpGet("https://webexapis.com/v1/meetings");
            get.addHeader("content-type", "application/json");
            get.addHeader("Authorization", request.getToken());

            String result = "";

            try (CloseableHttpClient httpClient = HttpClients.createDefault();
                 CloseableHttpResponse response = httpClient.execute(get)) {

                result = EntityUtils.toString(response.getEntity());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return ResponseEntity.ok(result);
    }
}

    @PostMapping("/{courseId}/create-meeting")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> createMeeting(@RequestBody CreateMeetingRequest request, @PathVariable String courseId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        UserEntity loggedUser = userRepository.findByEmail(currentPrincipalName).orElse(null);

        if (loggedUser == null) {
            throw new RuntimeException("Zaloguj się aby kontynuować");
        }
        if (loggedUser.getTeacherCourses().stream().noneMatch(course -> course.getId().equals(Long.valueOf(courseId)))) {
            throw new SecurityException("Wygląda na to że nie posiadasz kursu o podanym id");
        } else {
            CourseEntity course = courseRepository.findById(Long.valueOf(courseId)).orElse(null);

            if(course == null) {
                throw new NullPointerException("Nie znaleziono kursu o podanym id");
            }

            List<String> participantsEmails = course.getStudents().stream().map(UserEntity::getEmail).collect(Collectors.toList());
            String result = "";

            HttpPost post = new HttpPost("https://webexapis.com/v1/meetings");
            post.addHeader("content-type", "application/json");
            post.addHeader("Authorization", request.getToken());

            StringBuilder entity = new StringBuilder();
            entity.append("{");
            entity.append("\"title\":" + "\"" + course.getName() + "\"" + ",");
            entity.append("\"start\":" + "\"" + request.getStart() + "\"" + ",");
            entity.append("\"end\":" + "\"" + request.getEnd() + "\"" + ",");
            entity.append("\"timezone\":" + "\"" + "Europe/Warsaw" + "\"" + ",");
            //entity.append("\"recurrence\":" + "\"" + "FREQ=WEEKLY" + "\"" + ",");

            entity.append("\"invitees\":");
            for(int i = 0; i < participantsEmails.size(); i ++){
                if(i == 0) {
                    entity.append("[");
                }
                if (i == (participantsEmails.size() - 1)) {
                    entity.append("{\"email\" : \"" + participantsEmails.get(i) + "\"" + "}");
                    entity.append("]");
                    continue;
                }
                entity.append("{\"email\" : \"" + participantsEmails.get(i) + "\"" + "} ,");
            }
            entity.append("}");


            // send a JSON data
            try {
                post.setEntity(new StringEntity(entity.toString()));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            try (CloseableHttpClient httpClient = HttpClients.createDefault();
                 CloseableHttpResponse response = httpClient.execute(post)) {

                result = EntityUtils.toString(response.getEntity());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return ResponseEntity.ok(result);
        }
    }

    @PostMapping("/integration")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> integration(@RequestBody IntegrationRequest request) {
        String urlParameters  = "grant_type=authorization_code&client_id=" + clientId +
                "&client_secret=" + clientSecret +
                "&code=" + request.getCode() + "&redirect_uri=http://localhost:3000/teacher/webex-integration";
        byte[] postData = urlParameters.getBytes( StandardCharsets.UTF_8 );
        int postDataLength = postData.length;
        String responseValue = "";
        String integrationRequest = "https://webexapis.com/v1/access_token";
        try {
            URL url = new URL(integrationRequest);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
            conn.setUseCaches(false);
            try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                wr.write(postData);
            }
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            //print in String
            return ResponseEntity.ok(response.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
        return ResponseEntity.internalServerError().build();
    }

    @DeleteMapping("/{courseId}/cancel-meeting")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> cancelMeeting(@RequestBody DeleteMeetingRequest request, @PathVariable String courseId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        UserEntity loggedUser = userRepository.findByEmail(currentPrincipalName).orElse(null);

        if (loggedUser == null) {
            throw new RuntimeException("Zaloguj się aby kontynuować");
        }
        if (loggedUser.getTeacherCourses().stream().noneMatch(course -> course.getId().equals(Long.valueOf(courseId)))) {
            throw new SecurityException("Wygląda na to że nie posiadasz kursu o podanym id");
        } else {
            CourseEntity course = courseRepository.findById(Long.valueOf(courseId)).orElse(null);

            if (course == null) {
                throw new NullPointerException("Nie znaleziono kursu o podanym id");
            }

            HttpDelete delete = new HttpDelete("https://webexapis.com/v1/meetings/" + request.getMeetingId());
            delete.addHeader("content-type", "application/json");
            delete.addHeader("Authorization", request.getToken());

            int result = -1;

            try (CloseableHttpClient httpClient = HttpClients.createDefault();
                 CloseableHttpResponse response = httpClient.execute(delete)) {

                result = response.getStatusLine().getStatusCode();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(result != -1)
                return ResponseEntity.ok(result);
            else {
                return ResponseEntity.internalServerError().build();
            }
        }
    }

}
