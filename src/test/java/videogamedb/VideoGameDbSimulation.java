package videogamedb;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;
import java.util.concurrent.ThreadLocalRandom;

public class VideoGameDbSimulation extends Simulation {
    private HttpProtocolBuilder httpProtocol = http
        .baseUrl("https://videogamedb.uk/api")
        .acceptHeader("application/json")
        .contentTypeHeader("application/json");

private ScenarioBuilder scn = scenario("Video Game DB Stress Test")
        .exec(authenticate)
        .pause(2) // Pause between actions
        .exec(createNewGame)
        .pause(2)
        .exec(http("Get all games")
        .get("/videogame")
        .check(status().is(200)) // Checking status is 200
        .check(jsonPath("$[?(@.name == 'Super Mario')]").exists()) // Checking that the name "Super Mario" exists in the list
        
    )
    .exec(session -> {
        System.out.println("Response Body: " + session.getString("responseBody")); // Печатаем его в консоль VS Code
        return session;
    });
    // authenticate video game db:
    private static ChainBuilder authenticate = exec(http("Authenticate")
        .post("/authenticate")
        .body(StringBody("{\"password\": \"admin\", \"username\": \"admin\"}"))
        .check(jsonPath("$.token").saveAs("jwtToken"))
    );

    {
        setUp(scn.injectOpen(atOnceUsers(1)).protocols(httpProtocol));
    }

private static ChainBuilder createNewGame = exec(session -> {
         // Generate random ID from 2000 to 9999 and save it to the session
         int randomId = ThreadLocalRandom.current().nextInt(2000, 10000);
         return session.set("gameId", randomId);
     })
     .exec(http("Create new game")
         .post("/videogame")
         .header("Authorization", "Bearer #{jwtToken}")
         .body(StringBody(
             "{" +
             "  \"id\": #{gameId}, " + // Generate random ID from 2000 to 9999 and save it to the session
             "  \"category\": \"Platform\", " +
             "  \"name\": \"Super Mario\", " +
             "  \"rating\": \"Mature\", " +
             "  \"releaseDate\": \"2025-05-04\", " +
             "  \"reviewScore\": 91" +
             "}"
         ))
     );
}
