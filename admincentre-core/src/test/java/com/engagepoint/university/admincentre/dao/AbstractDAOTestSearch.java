package com.engagepoint.university.admincentre.dao;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.engagepoint.university.admincentre.entity.Key;
import com.engagepoint.university.admincentre.entity.KeyType;

public class AbstractDAOTestSearch {

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
		System.out.println("........TESTING KEY SEARCHING.......");
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
		String name = key.getName();
		List<Key> names = keydao.search(name);
		String result = "";
		for (int i = 0; i < names.size(); i++) {
		result = result+names.get(i);	
		}
		assertEquals(key.toString(), result);
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
