package com.engagepoint.university.admincentre.synchronization;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.jgroups.JChannel;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.engagepoint.university.admincentre.entity.AbstractEntity;
import com.engagepoint.university.admincentre.entity.Node;

@RunWith(MockitoJUnitRunner.class)
public class SynchMasterTest {

	Field fieldCacheData;
	Field fieldReceivedState;
	
	@BeforeClass
	public static void beforeClass(){
		
	}
	
	@Mock
	JChannel channel;
	
	@Spy
	@InjectMocks
	SynchMaster synchMaster;
	
	@Test
	public void test() {
		try {
			fieldCacheData = SynchMaster.class.getDeclaredField("cacheData");
			fieldCacheData.setAccessible(true);
			Map<String, AbstractEntity> cacheData = new HashMap<String, AbstractEntity>();
			cacheData.put("/", new Node("", ""));
			fieldCacheData.set(synchMaster, cacheData);
			assertEquals("/",  ((Map<String, AbstractEntity>) fieldCacheData.get(synchMaster)).get("/").getId()  );
			
			fieldReceivedState = SynchMaster.class.getDeclaredField("receivedState");
			fieldReceivedState.setAccessible(true);
			Map<String, AbstractEntity> receivedState = new HashMap<String, AbstractEntity>();
			receivedState.put("/", new Node("", ""));
			fieldReceivedState.set(synchMaster, receivedState);
			assertEquals("/",  ((Map<String, AbstractEntity>) fieldReceivedState.get(synchMaster)).get("/").getId()  );
			
		} catch (SecurityException e) {
		} catch (NoSuchFieldException e) {
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		}
		
		
		when(channel.isConnected()).thenReturn(true);
		doReturn(false).when(synchMaster).isSingle();
		doReturn(false).when(synchMaster).isCoordinator();
		doReturn(true).when(synchMaster).obtainState();
		doNothing().when(synchMaster).obtainCacheData();
		
		assertTrue(synchMaster.merge().size() == 1);
		assertFalse(synchMaster.push());
		assertFalse(synchMaster.pull());
		assertFalse(synchMaster.reset());
		assertFalse(synchMaster.revert());
		
	}

}
