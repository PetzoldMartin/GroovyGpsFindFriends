package de.fh.zwickau.scriptsprachen.findme.client.rest

import android.util.Log
import com.arasthel.swissknife.annotations.OnBackground
import de.fh.zwickau.scriptsprachen.findme.client.activity.MainActivity
import de.fh.zwickau.scriptsprachen.findme.client.activity.RegisterActivity
import de.fh.zwickau.scriptsprachen.findme.client.friend.Friend
import de.fh.zwickau.scriptsprachen.findme.client.friend.FriendState
import de.fh.zwickau.scriptsprachen.findme.client.util.Connector
import de.fh.zwickau.scriptsprachen.findme.client.util.Core
import de.fh.zwickau.scriptsprachen.findme.client.util.StorageManager
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.client.RestTemplate

public class RESTRequests {

    private static final String SECRET = "geheim"
    private static final int TIMEOUT_MS = 5000
    private String lastResponse

    @OnBackground
    public void testRestRequest() {
        final String url = "http://rest-service.guides.spring.io/greeting"
        RestTemplate restTemplate = new RestTemplate()
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter())
        String response = restTemplate.getForObject(url, Object).toString()
        println response
    }

    @OnBackground
    public void getAllUsers(String email, Connector connector) {
        final String url = Core.SERVER_IP + "/medi/getOnlineUsers?email={email}"
        RestTemplate restTemplate = getRestTemplate()
        try {
            String response = restTemplate.getForObject(url, String.class, email)
            if (response.contains("Cannot Respose you are not logged in"))
                connector.restRequestFailed(response)
            else
                connector.restRequestDone(response)
        } catch (Exception ex) {
            connector.restRequestFailed("Server unavailable")
            Log.d("RESTClient", "Exception while REST request: " + ex.toString())
        }
    }

    @OnBackground
    public void getIpForEmail(String ownEmail, String targetEmail, Connector connector) {
        final String url = Core.SERVER_IP + "/medi/getIP?email={ownEmail}&targetEmail={targetEmail}"
        RestTemplate restTemplate = getRestTemplate()
        try {
            String response = restTemplate.getForObject(url, String.class, ownEmail, targetEmail)
            if (response.contains(" is not online"))
                connector.restRequestFailed(response)
            else
                connector.restRequestDone(response)
        } catch (Exception ex) {
            connector.restRequestFailed("Server unavailable")
            Log.d("RESTClient", "Exception while REST request: " + ex.toString())
        }
    }

    @OnBackground
    public void register(String email, String name, RegisterActivity activity) {
        final String url = Core.SERVER_IP + "/auth/register?email={email}&name={name}&secret=" + SECRET
        RestTemplate restTemplate = getRestTemplate()
        try {
            String response = restTemplate.getForObject(url, String.class, email, name)
            if (response.contains("Register successful"))
                activity.showRegisterSuccessful(email)
            else
                activity.showErrorMessage("E-Mail-Adresse wird bereits verwendet")
        } catch (Exception ex) {
            Log.d("RESTClient", "Exception while REST request: " + ex.toString())
            activity.showErrorMessage("Server ist nicht erreichbar")
        }
    }

    @OnBackground
    public void login(String email, RegisterActivity activity) {
        final String url = Core.SERVER_IP + "/auth/login?email={email}"
        RestTemplate restTemplate = getRestTemplate()
        try {
            String response = restTemplate.getForObject(url, String.class, email)
            if (response.contains("Login successful"))
                activity.showMapScreen(email)
            else
                activity.showErrorMessage("E-Mail-Adresse ist nicht registriert")
        } catch (Exception ex) {
            Log.d("RESTClient", "Exception while REST request: " + ex.toString())
            activity.showErrorMessage("Server ist nicht erreichbar")
        }
    }

    @OnBackground
    public void logout(MainActivity activity) {
        final String url = Core.SERVER_IP + "/auth/logout?email={email}"
        RestTemplate restTemplate = getRestTemplate()
        try {
            String response = restTemplate.getForObject(url, String.class, StorageManager.getInstance().getEmail(activity))
            if (response.contains("Logout successful"))
                activity.logoutSucceeded(true, response)
            else
                activity.logoutSucceeded(false, response)
        } catch (Exception ex) {
            Log.i("RESTClient", "Exception while REST request: " + ex.toString())
            activity.logoutSucceeded(false, "Server ist nicht erreichbar")
        }
    }

    @OnBackground
    public void getLocation(String targetIp, String ownEmail, Connector connector) {
        final String url = "http://" + targetIp + ":8080/locator/getLocation?ownEmail=$ownEmail"
        RestTemplate restTemplate = getRestTemplate()
        try {
            String response = restTemplate.getForObject(url, String.class)
            if (response.contains("null"))
                connector.restRequestFailed("Could not get IP for " + targetIp)
            else
                connector.restRequestDone(response)
        } catch (Exception ex) {
            connector.restRequestFailed("Target address unavailable")
            Log.d("RESTClient", "Exception while REST request: " + ex.toString())
        }
    }

    // TODO: The state of the friend has to be set outside of the following methods

    @OnBackground
    public void requestFriend(Friend friend, String ownEmail, String ownName) {
        final String url = "http://" + friend.lastKnownIp + ":8080/friend/requestFriend?ownEmail=$ownEmail&ownName=$ownName"
        RestTemplate restTemplate = getRestTemplate()
        try {
            String response = restTemplate.getForObject(url, String.class)
            if (response.equals("Okay"))
                println response
            else
                throw new Exception()
        } catch (Exception ex) {
            Connector con = Core.getConnector()
            con.showErrorToast("Fehler bei der Freundesanfrage!")
            Log.d("RESTClient", "Exception while REST request: " + ex.toString())
        }
    }

    @OnBackground
    public void accept(Friend friend, String ownEmail, String ownName, Connector connector) {
        final String url = "http://" + friend.lastKnownIp + ":8080/friend/accept?ownEmail=$ownEmail&ownName=$ownName"
        RestTemplate restTemplate = getRestTemplate()
        try {
            String response = restTemplate.getForObject(url, String.class)
            if (response.equals("Okay"))
                connector.updateFriend(friend, FriendState.FRIEND)
        } catch (Exception ex) {
            // Nothing to do - we will send this request again later if it fails
            Log.d("RESTClient", "Exception while REST request: " + ex.toString())
        }
    }

    @OnBackground
    public void deny(Friend friend, String ownEmail, Connector connector) {
        final String url = "http://" + friend.lastKnownIp + ":8080/friend/deny?ownEmail=$ownEmail"
        RestTemplate restTemplate = getRestTemplate()
        try {
            String response = restTemplate.getForObject(url, String.class)
            if (response.equals("Okay"))
                connector.removeFriend(friend, false)
        } catch (Exception ex) {
            // Nothing to do - we will send this request again later if it fails
            Log.d("RESTClient", "Exception while REST request: " + ex.toString())
        }
    }

    @OnBackground
    public void remove(Friend friend, String ownEmail, Connector connector) {
        final String url = "http://" + friend.lastKnownIp + ":8080/friend/remove?ownEmail=$ownEmail"
        RestTemplate restTemplate = getRestTemplate()
        try {
            String response = restTemplate.getForObject(url, String.class)
            if (response.equals("Okay"))
                connector.removeFriend(friend, false)
        } catch (Exception ex) {
            // Nothing to do - we will send this request again later if it fails
            Log.d("RESTClient", "Exception while REST request: " + ex.toString())
        }
    }

    private RestTemplate getRestTemplate() {
        def httpRequestFactory = new SimpleClientHttpRequestFactory()
        httpRequestFactory.setConnectTimeout(TIMEOUT_MS)
        RestTemplate restTemplate = new RestTemplate(httpRequestFactory)
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter())
        return restTemplate
    }

}
