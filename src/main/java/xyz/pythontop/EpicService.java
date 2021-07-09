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
    private static final String GAMES_URL = "http://pythontop.xyz/api/epic/games";
    public static final Logger LOG = LoggerFactory.getLogger(EpicService.class.getName());

    public EpicService() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
    }

    public List<Game> findGames() {
        try {
            return mapper.readValue(
                    new URL(GAMES_URL),
                    new TypeReference<List<Game>>() {}
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
