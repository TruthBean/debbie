package com.truthbean.debbie.console;

import java.time.LocalDateTime;

/**
 * @author TruthBean/Rogar·Q
 * @since 0.6.0
 */
public class DebbieConsoleConfig {
    private boolean enable = true;

    private String prompt = "console@truthbean [" + LocalDateTime.now() + "] :> ";

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}
