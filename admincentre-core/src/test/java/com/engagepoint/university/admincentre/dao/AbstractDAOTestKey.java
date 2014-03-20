package com.engagepoint.university.admincentre.dao;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.engagepoint.university.admincentre.entity.*;

/**
 * Created by oleksandr.kushnir on 3/13/14.
 */
public class AbstractDAOTestKey {

	KeyDAO keydao;
	Key key;
	KeyType keyType;
	
	@Before
	public void createDataBase() {
		System.out.println("Before");
		keydao = KeyDAO.getInstance();
		key = new Key();
		key.setName("Khylko");
		key.setType(KeyType.String);
		key.setValue("Sergei");
	}

	@Test
	public void testInsertKey() throws Exception {
		System.out.println("............TESTING KEY INSERTION......................");
		try {
			keydao.create(key);
			keydao.update(key);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		key = new Key();
		key.setName("Khylko");
		key.setType(KeyType.String);
		key.setValue("Sergei");
		assertEquals(key, keydao.read(key.getId()));
	}
	
	@After
	public void refreshDataBase() throws IOException {
		System.out.println("After");
		key = new Key();
		key.setName("Khylko");
		key.setType(KeyType.String);
		key.setValue("Sergei");
		keydao.delete(key.getId());
	}


}
