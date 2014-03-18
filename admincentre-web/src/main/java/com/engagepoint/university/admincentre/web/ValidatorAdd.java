package com.engagepoint.university.admincentre.web;

import java.text.MessageFormat;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import org.apache.commons.lang.StringUtils;
import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author aleksey.korotysh on 13.03.14.
 */
@ManagedBean
public class ValidatorAdd {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidatorAdd.class.getName());

    protected static boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
        } catch (NumberFormatException e) {
            LOGGER.error(MessageFormat.format("Can not parse value {0} to Double", str), e);
            return false;
        }
        return true;
    }

    public void validateAlpha(FacesContext facesContext, UIComponent uiComponent, Object value) throws ValidatorException {
        String selectedValue;
        UIComponent parent = uiComponent.getParent();
        if (null != parent) {
            UIComponent sone = parent.findComponent("selectTypeMenu2");
            if (null != sone) {
                LOGGER.debug("----------SELECTED------------------- " + parent.getId() + " ----------------------------------------------");
                SelectOneMenu selectOneMenu = (SelectOneMenu) sone;
                selectedValue = (String) selectOneMenu.getSubmittedValue();
                LOGGER.debug("++++++++++++++++++++++++++SELECTED VALUE++++++++++++++++++++++++++++++++++ " + selectedValue);

                if ("Integer".equals(selectedValue)) {
                    if (!StringUtils.isNumeric((String) value)) {
                        HtmlInputText htmlInputText = (HtmlInputText) uiComponent;
                        FacesMessage facesMessage = new FacesMessage(htmlInputText.getLabel() + ": Must be Integer Number");
                        throw new ValidatorException(facesMessage);
                    } else {
                        if (("Double".equals(selectedValue) || "Float".equals(selectedValue)) && (!isDouble((String) value))) {
                            HtmlInputText htmlInputText = (HtmlInputText) uiComponent;
                            FacesMessage facesMessage = new FacesMessage(htmlInputText.getLabel() + ": Must be Floating-Point Number");
                            throw new ValidatorException(facesMessage);
                        }
                    }
                }
            }
        }
    }
}