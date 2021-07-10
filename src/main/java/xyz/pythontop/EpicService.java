package xyz.pythontop;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.pythontop.pojo.Game;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class EpicService {

    private final ObjectMapper mapper;
    private static final String GAMES_URL_ACTIVE = "http://pythontop.xyz/api/epic/games/active";
    private static final String GAMES_URL_COMINGSOON = "http://pythontop.xyz/api/epic/games/coming-soon";
    public static final Logger LOG = LoggerFactory.getLogger(EpicService.class.getName());

    public EpicService() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
    }

    public List<Game> findComingSoonGames() {
        try {
            return mapper.readValue(
                    new URL(GAMES_URL_COMINGSOON),
                    new TypeReference<List<Game>>() {}
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Game> findGames() {
        try {
            return mapper.readValue(
                    new URL(GAMES_URL_ACTIVE),
                    new TypeReference<List<Game>>() {}
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
