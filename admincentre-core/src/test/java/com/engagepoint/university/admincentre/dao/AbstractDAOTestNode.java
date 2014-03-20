package com.engagepoint.university.admincentre.dao;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.engagepoint.university.admincentre.entity.Node;

public class AbstractDAOTestNode {
	
	NodeDAO nodedao;
	Node node;

	@Before
	public void createDataBase() {
		System.out.println("Before");
		nodedao = NodeDAO.getInstance();
		node = new Node();
		node.setName("Key's Directory");
	
	}

	@Test
	public void testInsertNode() throws Exception {
		System.out.println("............TESTING NODE INSERTION......................");
		try {
			nodedao.create(node);
			nodedao.update(node);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		node = new Node();
		node.setName("Key's Directory");
		
		assertEquals(node, nodedao.read(node.getId()));
	}
	
	@After
	public void refreshDataBase() throws IOException {
		System.out.println("After");
		node = new Node();
		node.setName("Key's Directory");
		nodedao.delete(node.getId());
	}
}
