package videogamedb;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class VideoGameDbSimulation extends Simulation {
    private static FeederBuilder.Batchable<String> csvFeeder = csv("data/gameData.csv").circular();
    private HttpProtocolBuilder httpProtocol = http
        .baseUrl("https://videogamedb.uk/api")
        .acceptHeader("application/json")
        .contentTypeHeader("application/json");

private ScenarioBuilder scn = scenario("Video Game DB Stress Test")
        .feed(csvFeeder) // Each time a user enters the scenario, they take a new line from the CSV
        .exec(authenticate)
        .pause(2)
        .exec(createNewGame)
        .pause(2)
        .exec(http("Verify API is alive")
            .get("/videogame/1")
            .check(status().is(200))
        );
    // authenticate video game db:
    private static ChainBuilder authenticate = exec(http("Authenticate")
        .post("/authenticate")
        .body(StringBody("{\"password\": \"admin\", \"username\": \"admin\"}"))
        .check(jsonPath("$.token").saveAs("jwtToken"))
    );

{
        setUp(
            scn.injectOpen(
                // 1. First run one scout
                atOnceUsers(1),
                // 2. Then pause for 5 seconds
                nothingFor(5),
                // 3. Then smoothly introduce 10 users over 30 seconds
                rampUsers(10).during(30)
            )
        ).protocols(httpProtocol);
     }

private static ChainBuilder createNewGame = exec(http("Create new game - #{gameName}")
        .post("/videogame")
        .header("Authorization", "Bearer #{jwtToken}")
        .body(StringBody(
            "{" +
            "  \"id\": #{gameId}, " +
            "  \"category\": \"RPG\", " +
            "  \"name\": \"#{gameName}\", " +
            "  \"rating\": \"Mature\", " +
            "  \"releaseDate\": \"2025-05-04\", " +
            "  \"reviewScore\": 99" +
            "}"
        ))
    );
}
