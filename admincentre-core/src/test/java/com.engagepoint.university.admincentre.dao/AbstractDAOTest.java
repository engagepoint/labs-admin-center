package com.engagepoint.university.admincentre.dao;

import com.engagepoint.university.admincentre.entity.*;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;

/**
 * Created by oleksandr.kushnir on 3/13/14.
 */
public class AbstractDAOTest {


    GenericDAO<Key> genericDAO;

    @Before
    public void setup(){
        AbstractDAO<Key> tAbstractDAO = (AbstractDAO<Key>) mock(GenericDAO.class);

            //when(tAbstractDAO.read("13")).thenReturn("13");


    }

    @Test
    public Key testRead(){

       // Assert.assertEquals("13",);
        Key key = null;
        return key;
    }


}
