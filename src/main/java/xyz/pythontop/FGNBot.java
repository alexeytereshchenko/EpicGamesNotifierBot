package xyz.pythontop;

import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Locality;
import org.telegram.abilitybots.api.objects.Privacy;
import xyz.pythontop.pojo.Game;

import java.util.List;

public class FGNBot extends AbilityBot {

    private static final String BOT_TOKEN = System.getenv("GNF_BOT_TOKEN");
    private static final String BOT_USERNAME = System.getenv("FGN_BOT_USERNAME");
    private static final String CREATOR_ID = System.getenv("MY_TELEGRAM_ID");

    private final EpicService epicService = new EpicService();

    public FGNBot() {
        super(BOT_TOKEN, BOT_USERNAME);
    }

    @Override
    public long creatorId() {
        return Long.parseLong(CREATOR_ID);
    }

    public Ability findFreeGames() {
        return Ability
                .builder()
                .name("games")
                .info("get free games")
                .locality(Locality.ALL)
                .privacy(Privacy.PUBLIC)
                .action(ctx -> {
                    List<Game> games = epicService.findFreeGames();
                    if (games == null) return;
                    games.forEach(game -> silent.send(epicService.createUrl(game), ctx.chatId())
                    );
                })
                .build();
    }
}
