package com.engagepoint.university.admincentre.admincentre.restws;


//import com.engagepoint.university.admincentre.preferences.NodePreferences;
//import java.util.prefs.BackingStoreException;
//import java.util.prefs.Preferences;
import java.util.prefs.Preferences;

import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.engagepoint.university.admincentre.preferences.NodePreferences;

/** Example resource class hosted at the URI path "/preferences"
 */
@Path("/preferences")
@Stateless
public class PreferencesService {
    
    /** Method processing HTTP GET requests which get key name as input and return key value
     * @param key
     * @return String that will be send back as a response".
     */
    @GET 
    @Path("/key/{key}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getValueByKey(@PathParam("key") String key) {
        String value = null;
        // You can find keys globally
        Preferences preferences = new NodePreferences(null, "");
        value = preferences.get(key, null);
        return "Here must be key [" + key + "] " + "and value [" + value + "]";
    }



}
