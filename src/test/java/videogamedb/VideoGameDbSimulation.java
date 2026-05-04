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

    // authenticate video game db:
    private static ChainBuilder authenticate = exec(http("Authenticate")
            .post("/authenticate")
            .body(StringBody("{\"password\": \"admin\", \"username\": \"admin\"}"))
            .check(jsonPath("$.token").saveAs("jwtToken")));

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
                            "}")));

    private ChainBuilder verifyApi = 
        exec(http("Verify API is alive")
            .get("/videogame/1")
            .check(status().is(200)));              

    ScenarioBuilder scn = scenario("Video Game DB Stress Test")
            .feed(csvFeeder)   // Each user will take one line from the CSV file
            .exec(authenticate)
            .pause(2)
            .exec(createNewGame)
            .pause(2)
            .exec(verifyApi);

    {
        setUp(
                scn.injectOpen(
                        atOnceUsers(5), // Five users at once
                        rampUsers(50).during(30) // Add 50 users over 30 seconds
                )
        ).protocols(httpProtocol)
                .assertions(
                        // Check: 95th percentile of response time should be less than 800 ms
                        global().responseTime().percentile3().lt(800),
                        // Check: 100% successful responses from the server
                        global().successfulRequests().percent().is(100.0)
                );
    }


}
