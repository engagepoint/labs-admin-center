package com.engagepoint.acceptancetest;

import com.engagepoint.acceptancetest.base.pages.UIBootstrapBasePage;
import com.engagepoint.acceptancetest.base.steps.JbehaveBaseSteps;
import net.thucydides.core.pages.Pages;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

/**
 *
 * @author artem.lysenko
 */
public class EditButtonCheck extends JbehaveBaseSteps {

    private UIBootstrapBasePage uIBootstrapBasePage;

    public EditButtonCheck(Pages pages) {
        super(pages);
        uIBootstrapBasePage = pages().get(UIBootstrapBasePage.class);
    }

    @When("clicks on element with id '$id'")
    public void clickBySelector(String id) {
        uIBootstrapBasePage.element(findVisibleElementAndGetSelector(id)).click();
    }

    @Then("the user fills '$id' field with '$value'")
    public void thenTheUserFillspreferencesFormtreeTable2j_idt57FieldWithNodeAdmin(String id, String value) {
        fillField(id, value);
//        uIBootstrapBasePage.enter(value).intoField(findVisibleElementAndGetSelector(id));
    }

    @When("clicks on element with id '$id' appeared in target list")
    public void clickBySelectorTwo(String id) {
        uIBootstrapBasePage.element(findVisibleElementAndGetSelector(id)).click();
    }

    @Then("clicks on element with id '$id'")
    public void thenClicksOnElementWithIdnameclassNameuitreetabletogglerUiiconUicUiicontriangle1e(String id) {
        uIBootstrapBasePage.element(findVisibleElementAndGetSelector(id)).click();
    }
}