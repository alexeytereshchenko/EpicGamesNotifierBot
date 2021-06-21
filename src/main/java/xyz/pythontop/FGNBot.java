package xyz.pythontop;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Locality;
import org.telegram.abilitybots.api.objects.Privacy;
import xyz.pythontop.pojo.Game;

import java.io.IOException;
import java.net.URL;
import java.util.List;

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

    public Ability sayHelloWorld() {
        return Ability
                .builder()
                .name("games")
                .info("get free games")
                .locality(Locality.ALL)
                .privacy(Privacy.PUBLIC)
                .action(ctx -> silent.send(findFreeGame(), ctx.chatId()))
                .build();
    }

    public String findFreeGame() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String url = "https://store-site-backend-static.ak.epicgames.com/freeGamesPromotions";
        try {
            JsonNode jsonNode = mapper.readTree(new URL(url));
            List<Game> games = mapper.readValue(
                    jsonNode.findValue("elements").toString(),
                    new TypeReference<List<Game>>() {
                    }
            );
            return games.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ":(";
    }
}
