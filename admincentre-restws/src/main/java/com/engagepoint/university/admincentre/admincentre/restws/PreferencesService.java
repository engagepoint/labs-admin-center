package main.java.com.engagepoint.university.admincentre.admincentre.restws;


//import com.engagepoint.university.admincentre.preferences.NodePreferences;
//import java.util.prefs.BackingStoreException;
//import java.util.prefs.Preferences;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/** Example resource class hosted at the URI path "/preferences"
 */
@Path("/preferences")
public class PreferencesService {
    
    /** Method processing HTTP GET requests which get key name as input and return key value
     * @param key
     * @return String that will be send back as a response".
     */
    @GET 
    @Path("/key/{key}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getValueByKey(@PathParam("key") String key) {
        return "Here must be key [" + key + "] value";
        //Preferences preferences = new NodePreferences(null, "");
        //return preferences.get(key, "Default value");
    }    
}
