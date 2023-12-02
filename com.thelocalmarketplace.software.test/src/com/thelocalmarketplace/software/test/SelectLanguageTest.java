package com.thelocalmarketplace.software.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.thelocalmarketplace.GUI.SelectLanguage;

public class SelectLanguageTest {
    private SelectLanguage selectLanguage;
    @Before
    public void SetUp() {
        selectLanguage = new SelectLanguage();
    }

    @Test
    public void testCancelAction() {
        selectLanguage.getCancel().doClick();
        assertFalse(selectLanguage.isVisible());
    }

    @Test
    public void testEngButtonAction() {
        selectLanguage.getEngButton().doClick();
        assertFalse(selectLanguage.isVisible());
    }

}