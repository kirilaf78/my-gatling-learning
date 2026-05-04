package videogamedb.requests;

import io.gatling.javaapi.core.ChainBuilder;
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class VideoGameRequests {

    public static ChainBuilder authenticate() {
        return exec(http("Authenticate")
                .post("/authenticate")
                .body(StringBody("{\"password\": \"admin\", \"username\": \"admin\"}"))
                .check(jsonPath("$.token").saveAs("jwtToken")));
    }

    public static ChainBuilder createNewGame() {
        return exec(http("Create new game - #{gameName}")
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
    }

    public static ChainBuilder verifyApi() {
        return exec(http("Verify API is alive")
                .get("/videogame/1")
                .check(status().is(200)));
    }
}