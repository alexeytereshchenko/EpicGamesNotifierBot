package xyz.pythontop;

import org.telegram.abilitybots.api.bot.AbilityBot;

public class FGNBot extends AbilityBot {

    private static final String BOT_TOKEN = System.getenv("GNF_BOT_TOKEN");
    private static final String BOT_USERNAME = System.getenv("FGN_BOT_USERNAME");
    private static final String CREATOR_ID = System.getenv("MY_TELEGRAM_ID");

    public FGNBot() {
        super(BOT_TOKEN, BOT_USERNAME);
    }

    @Override
    public long creatorId() {
        return Long.parseLong(CREATOR_ID);
    }

}
