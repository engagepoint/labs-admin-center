package com.engagepoint.university.admincentre.preferences;

import static org.junit.Assert.*;

import com.engagepoint.university.admincentre.entity.Key;
import com.engagepoint.university.admincentre.entity.KeyType;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;


/**
 * Created by oleksandr.kushnir on 3/20/14.
 */
public class NodePreferencesTests {



    String name;
    String key;
    String value;
    String filePath;
    NodePreferences parent;
    NodePreferences nodePreferences;
    KeyType keyType;
    File file;
    InputStream inputStream;

    @Before
    public void testSetUp(){
        name ="";
        nodePreferences = new NodePreferences(parent,name);
        key = "DummyKEY";
        value = "DummyVALUE";
        filePath = "http://introcs.cs.princeton.edu/java/data/words.txt";
        file = new File(filePath);
        inputStream = null;
    }


    @Test
    public void testNodePreferencesPutAndGet() throws IOException {
    	nodePreferences.put(key,KeyType.String,value);        
    	Key key = new Key();
    	key.setName("DummyKEY");
    	key.setType(KeyType.String);
    	key.setValue("DummyVALUE");
    	assertEquals(key.getValue(),nodePreferences.get(key.getName(),"//"+key.getName()));
    	
    }
   
}
