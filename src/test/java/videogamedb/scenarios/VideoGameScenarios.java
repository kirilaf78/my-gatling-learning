package videogamedb.scenarios;

import io.gatling.javaapi.core.FeederBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import videogamedb.requests.VideoGameRequests;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static io.gatling.javaapi.core.CoreDsl.*;

public class VideoGameScenarios {

    // Оставляем старый фидер для второго сценария, чтобы не было ошибок компиляции
    private static FeederBuilder.Batchable<String> csvFeeder = csv("data/gameData.csv").circular();

    // Наш новый кастомный генератор уникальных ID и имен
    private static Iterator<Map<String, Object>> customFeeder = Stream.generate((Supplier<Map<String, Object>>) () -> {
        Random rand = new Random();
        int randomId = rand.nextInt(90000) + 10000; // Генерируем случайный ID от 10000 до 99999
        String randomName = "Game-" + randomId; // Имя тоже будет уникальным

        return Map.of(
                "gameId", randomId,
                "gameName", randomName);
    }).iterator();

    // Scenario 1: Full Creation Cycle
    public static ScenarioBuilder fullCreationCycle() {
        return scenario("Full Creation Cycle")
                .feed(customFeeder) // <-- Обрати внимание, здесь без скобочек ()
                .exec(VideoGameRequests.authenticate())
                .pause(2)
                .exec(VideoGameRequests.createNewGame())
                .pause(2)
                .exec(VideoGameRequests.verifyApi());
    }

    // Scenario 2: Read only cycle (example of reuse)
    public static ScenarioBuilder readOnlyCycle() {
        return scenario("Read Only Check")
                .feed(csvFeeder)
                .exec(VideoGameRequests.verifyApi());
    }
}