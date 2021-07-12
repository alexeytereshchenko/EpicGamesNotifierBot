package xyz.pythontop;

import com.coreoz.wisp.Scheduler;
import com.coreoz.wisp.schedule.Schedules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Locality;
import org.telegram.abilitybots.api.objects.Privacy;
import org.telegram.telegrambots.meta.api.methods.pinnedmessages.PinChatMessage;
import org.telegram.telegrambots.meta.api.methods.pinnedmessages.UnpinChatMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import xyz.pythontop.pojo.Game;
import xyz.pythontop.service.EpicService;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class Bot extends AbilityBot {

    private static final String BOT_TOKEN = System.getenv("GNF_BOT_TOKEN");
    private static final String BOT_USERNAME = System.getenv("FGN_BOT_USERNAME");
    private static final String CREATOR_ID = System.getenv("MY_TELEGRAM_ID");
    public static final Logger LOG = LoggerFactory.getLogger(Bot.class.getName());

    private final EpicService epicService = new EpicService();
    private final Set<Long> subscribers = db.getSet("subscribers");
    private final Map<String, Long> sendGames = db.getMap("sendGames");
    private final Map<Long, List<Integer>> pinnedMessages = db.getMap("pinnedMsg");
    private final Scheduler scheduler = new Scheduler();

    public Bot() {
        super(BOT_TOKEN, BOT_USERNAME);
        startScheduler();
    }

    private void startScheduler() {
        scheduler.schedule(
                () -> subscribers.forEach(this::sendNotify),
                Schedules.fixedDelaySchedule(Duration.ofHours(5))
        );
    }

    private void sendNotify(Long chatId) {
        List<Game> games = epicService.findGames();
        LOG.info("Games: {}", games);
        if (games == null) return;
        games
                .stream()
                .filter(game ->
                        !(sendGames.containsKey(game.getId())
                        && sendGames.containsValue(chatId))
                )
                .peek(game -> {
                    if (!pinnedMessages.containsKey(chatId)) return;
                    pinnedMessages.get(chatId).forEach(msgId -> unpinMessage(chatId, msgId));
                })
                .forEach(game -> silent.send(game.getUrl(), chatId).ifPresent(msg -> {
                    sendGames.put(game.getId(), chatId);
                    pinMessage(msg);
                }));
    }

    private void sendGames(Long chatId) {
        List<Game> games = epicService.findGames();
        if (games == null) return;
        games.forEach(game -> silent.send(game.getUrl(), chatId));
    }

    private void pinMessage(Message message) {
        sendApiMethodAsync(PinChatMessage.builder()
                    .chatId(message.getChatId().toString())
                    .messageId(message.getMessageId())
                    .build())
                .thenRunAsync(() -> {
                    List<Integer> messages = new ArrayList<>();
                    if (pinnedMessages.containsKey(message.getChatId())) {
                        messages.addAll(pinnedMessages.get(message.getChatId()));
                    }
                    messages.add(message.getMessageId());
                    pinnedMessages.put(message.getChatId(), messages);
                })
                .exceptionally(e -> {
                    LOG.error("Pin message", e);
                    silent.send(Alerts.PIN_WARNING.getText(), message.getChatId());
                    return null;
                });
    }

    private void unpinMessage(Long chatId, Integer messageId) {
        sendApiMethodAsync(UnpinChatMessage.builder()
                    .chatId(chatId.toString())
                    .messageId(messageId)
                    .build())
                .exceptionally(e -> {
                    LOG.error("Unpin message", e);
                    silent.send(Alerts.UNPIN_WARNING.getText(), chatId);
                    return null;
                });
    }

    private void sendComingSoonGames(Long chatId) {
        List<Game> games = epicService.findComingSoonGames();
        if (games == null) return;
        games.forEach(game -> silent.send(game.getUrl(), chatId));
    }

    @Override
    public long creatorId() {
        return Long.parseLong(CREATOR_ID);
    }

    public Ability findGames() {
        return Ability
                .builder()
                .name("games")
                .locality(Locality.ALL)
                .privacy(Privacy.PUBLIC)
                .action(ctx -> sendGames(ctx.chatId()))
                .build();
    }

    public Ability findComingSoonGames() {
        return Ability
                .builder()
                .name("coming_soon")
                .locality(Locality.ALL)
                .privacy(Privacy.PUBLIC)
                .action(ctx -> sendComingSoonGames(ctx.chatId()))
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
                    silent.send(Alerts.ACTIVATE_SUBSCRIBE.getText(), ctx.chatId());
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
                    silent.send(Alerts.DEACTIVATE_SUBSCRIBE.getText(), ctx.chatId());
                })
                .build();
    }
}
