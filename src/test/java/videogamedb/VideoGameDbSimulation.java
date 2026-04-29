package videogamedb;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class VideoGameDbSimulation extends Simulation {
    private HttpProtocolBuilder httpProtocol = http
        .baseUrl("https://videogamedb.uk/api")
        .acceptHeader("application/json")
        .contentTypeHeader("application/json");

    private ScenarioBuilder scn = scenario("Video Game DB Stress Test")
        .exec(http("Get all games")
            .get("/videogame")
        );

    {
        setUp(scn.injectOpen(atOnceUsers(10)).protocols(httpProtocol));
    }
}
