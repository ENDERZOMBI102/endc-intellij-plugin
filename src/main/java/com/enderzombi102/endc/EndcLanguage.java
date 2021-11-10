package com.enderzombi102.endc;

import com.intellij.lang.Language;

public class EndcLanguage extends Language {
    public static final EndcLanguage INSTANCE = new EndcLanguage();

    private EndcLanguage() {
        super("EndC");
    }
}
