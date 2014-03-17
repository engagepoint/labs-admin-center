package com.engagepoint.university.admincentre.synch;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.engagepoint.university.admincentre.synchronization.SynchMaster;

/**
 * Integration tests for Synch EJB
 * @author Roman Garkavenko
 *
 */
public class SynchTest {

//	private static final String CHANNEL_TEST_NAME = "channel_test_name";
//	private static final String ABCDEFGHIJKLMNOPQRSTUVWXYZ = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
//	
//	private static Synch synch;
//	
//	/**
//	 * Used to generate random string
//	 * @param rng - instance of java.util.Random
//	 * @param characters - String of allowed characters
//	 * @param length - length of result string
//	 * @return random line of characters with specified length
//	 */
//	private static String generateString(Random rng, String characters, int length){
//	    char[] text = new char[length];
//	    for (int i = 0; i < length; i++){
//	        text[i] = characters.charAt(rng.nextInt(characters.length()));
//	    }
//	    return new String(text);
//	}
//	
//	@BeforeClass
//	public static void beforeClass(){
//		synch = new SynchBean();
//	}
//	
//	@After
//	public void tearDown(){
//		synch.disconnect();
//	}
//	
//	@AfterClass
//	public static void teatDownAfterClass(){
//		SynchMaster.getInstance().close();
//	}
//	
//	@Test
//	public void isConnected() {
//		assertFalse(synch.isConnected());
//		synch.connect(generateString(new Random(), ABCDEFGHIJKLMNOPQRSTUVWXYZ, 15));
//		assertTrue(synch.isConnected());
//		
//	}
//
//	@Test
//	public void getAndSetChannelName() {
//		synch.setChannelName(CHANNEL_TEST_NAME);
//		assertFalse(null == synch.getChannelName());
//		assertTrue(CHANNEL_TEST_NAME.equals(synch.getChannelName()));
//	}
//	
//	@Test
//	public void getAndSetMode() {
//		assertTrue(synch.getMode().equals(SynchMaster.Mode.AUTO.name()));
//		synch.setMode(SynchMaster.Mode.MANUAL.name());
//		assertTrue(synch.getMode().equals(SynchMaster.Mode.MANUAL.name()));
//	}
//
//	@Test
//	public void setMode() {
//	}
//
//	@Test
//	public void getClusterName() {
//	}
//
//	@Test
//	public void connect() {
//	}

}
