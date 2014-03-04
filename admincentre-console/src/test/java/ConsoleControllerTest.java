import static org.junit.Assert.assertEquals;

import com.engagepoint.university.admincentre.entity.KeyType;
import org.junit.Test;

import com.engagepoint.university.admincentre.ConsoleController;

public class ConsoleControllerTest {

    ConsoleController controller = new ConsoleController();

    @Test
    public void testGetCurrentNode() throws Exception {

    }

    @Test
    public void testSetCurrentNode() throws Exception {

    }

    @Test
    public void testShowHelp() throws Exception {

    }

    @Test
    public void testShowVersion() throws Exception {

    }

    @Test
    public void testDisplayNodes() throws Exception {

    }

    @Test
    public void testSelectNode() throws Exception {

    }

    @Test
    public void testChooseParentNode() throws Exception {

    }

    @Test
    public void testCreateNode() throws Exception {

    }

    @Test
    public void testCreateKey() throws Exception {

    }

   @Test
    public void testNameValidation() throws Exception {
        String[] validNames = {"Aname", "_NName", "test_name", "1345Name"};
        String[] unValidNames = {"$Aert", "+adfaf", "!afsd", "#af", "-4sgd_32"};

        for (String validName : validNames) {
            assertEquals(true, controller.nameValidation(validName));
        }
        for (String unValidName : unValidNames) {
            assertEquals(false, controller.nameValidation(unValidName));
        }
    }

    @Test
    public void testKeyTypeValidation() throws Exception {
        for (KeyType keyType : KeyType.values()) {
            assertEquals(true, controller.keyTypeValidation(keyType.toString()));
        }
        assertEquals(false, controller.keyTypeValidation("string"));
        assertEquals(false, controller.keyTypeValidation("Random_String"));

    }
}
