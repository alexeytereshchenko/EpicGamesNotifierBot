package xyz.pythontop;

import com.coreoz.wisp.Scheduler;
import com.coreoz.wisp.schedule.Schedules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Locality;
import org.telegram.abilitybots.api.objects.Privacy;
import xyz.pythontop.pojo.Game;

import java.time.Duration;
import java.util.List;
import java.util.Set;


public class FGNBot extends AbilityBot {

    private static final String BOT_TOKEN = System.getenv("GNF_BOT_TOKEN");
    private static final String BOT_USERNAME = System.getenv("FGN_BOT_USERNAME");
    private static final String CREATOR_ID = System.getenv("MY_TELEGRAM_ID");
    public static final Logger LOG = LoggerFactory.getLogger(FGNBot.class.getName());

    private final EpicService epicService = new EpicService();
    private final Set<Long> subscribers = db.getSet("subscribers");
    private final Set<String> sentGamesId = db.getSet("sentGamesId");

    public FGNBot() {
        super(BOT_TOKEN, BOT_USERNAME);
        startScheduler();
    }

    private void startScheduler() {
        Scheduler scheduler = new Scheduler();
        scheduler.schedule(
                () -> subscribers.forEach(this::sendNotifyWithoutDuplicates),
                Schedules.fixedDelaySchedule(Duration.ofHours(5))
        );
    }

    private void sendNotifyWithoutDuplicates(Long chatId) {
        List<Game> games = epicService.findGames();
        LOG.info("Games: {}", games);
        if (games == null) return;
        games
                .stream()
                .filter(game -> !sentGamesId.contains(game.getId() + "_" + chatId))
                .forEach(game -> {
                    silent.send(game.getUrl(), chatId);
                    sentGamesId.add(game.getId() + "_" + chatId);
                });
    }

    private void sendNotify(Long chatId) {
        List<Game> games = epicService.findGames();
        if (games == null) return;
        games.forEach(game -> {
            silent.send(game.getUrl(), chatId);
        });
    }

    @Override
    public long creatorId() {
        return Long.parseLong(CREATOR_ID);
    }

    public Ability findGames() {
        return Ability
                .builder()
                .name("games")
                .info("get free games")
                .locality(Locality.ALL)
                .privacy(Privacy.PUBLIC)
                .action(ctx -> sendNotify(ctx.chatId()))
                .build();
    }

    public Ability subscribe() {
        return Ability
                .builder()
                .name("subscribe")
                .locality(Locality.ALL)
                .privacy(Privacy.PUBLIC)
                .action(ctx -> {
                    subscribers.add(ctx.chatId());
                    silent.send("Subscribe on notification is activate!", ctx.chatId());
                })
                .build();
    }

    public Ability unSubscribe() {
        return Ability
                .builder()
                .name("unsubscribe")
                .locality(Locality.ALL)
                .privacy(Privacy.PUBLIC)
                .action(ctx -> {
                    subscribers.remove(ctx.chatId());
                    silent.send("Subscribe on notification is deactivate!", ctx.chatId());
                })
                .build();
    }
}
