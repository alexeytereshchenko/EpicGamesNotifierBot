package xyz.pythontop;

public enum Alerts {
    PIN_WARNING("Bot can't pin message, check permissions"),
    UNPIN_WARNING("Bot can't unpin message, check permissions or message was deleted"),
    ACTIVATE_SUBSCRIBE("Subscribe on notification is activate!"),
    DEACTIVATE_SUBSCRIBE("Subscribe on notification is deactivate!");

    private final String text;

    Alerts(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
