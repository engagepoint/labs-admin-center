package com.engagepoint.university.admincentre.web;

import org.junit.Test;

/**
 *
 * @author Alex Korotysh
 */
public class ValidaorEditTest {

    @Test(expected = Error.class)
    public void testValidateAlpha() throws Exception {
        ValidaorEdit validaorEdit = new ValidaorEdit();
        validaorEdit.validateAlpha(null, null, null);
    }
}
