package com.truthbean.debbie.console;

import com.truthbean.logger.util.DateTimeHelper;

import java.util.Locale;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.0
 */
public class DebbieConsoleConfig {
    private boolean enable = true;

    private String prompt = "console@truthbean [%s] :> ";

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getPrompt() {
        String now = DateTimeHelper.nowStr();
        return String.format(Locale.ROOT, prompt, now);
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}
