package com.engagepoint.university.admincentre.admincentre.restws;


//import com.engagepoint.university.admincentre.preferences.NodePreferences;
//import java.util.prefs.BackingStoreException;
//import java.util.prefs.Preferences;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.prefs.BackingStoreException;

import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.engagepoint.university.admincentre.dao.KeyDAO;
import com.engagepoint.university.admincentre.entity.Key;
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
        KeyDAO keyDAO = KeyDAO.getInstance();
        String value = null;

        try {
            List<Key> keyList = keyDAO.search(key);
            if (!keyList.isEmpty()) {
                value = keyList.get(0).getValue();

            }
 else {
                Key keyObj = searchKeyInPreferences(new NodePreferences(null, ""), key);
                if (keyObj != null) {
                    value = keyObj.getValue();
                }
            }

            // Preferences preferences = new NodePreferences(null, "");
            // Preferences another = preferences.node("/newNode/Node1/Node1.1");
            // List<Key> list = keyDAO.search("11");
            //
            // System.out.println(list.get(0).getName() + "\n" +
            // list.get(0).getValue());

        } catch (IOException e) {
        }
        return "Here must be key [" + key + "] " + value;
    }

    private Key searchKeyInPreferences(NodePreferences preferences, String key) {
        Key keyObj = null;
        try {
            List<String> keys = Arrays.asList(preferences.keys());
            if (keys.contains(key)) {
                keyObj = preferences.getKey(key);
            } else {
                Iterator entries = preferences.getKidCache().entrySet().iterator();
                while (keyObj == null && entries.hasNext()) {
                    Entry<String, NodePreferences> thisEntry = (Entry) entries.next();
                    NodePreferences value = thisEntry.getValue();
                    keyObj = searchKeyInPreferences(value, key);
                }

            }

        } catch (BackingStoreException e) {

        } catch (IOException e) {

        }
        return keyObj;
    }

}
