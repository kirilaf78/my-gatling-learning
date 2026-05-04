package videogamedb.simulations;

import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import videogamedb.scenarios.VideoGameScenarios; // Import scenario
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class StressTestSimulation extends Simulation {

    private HttpProtocolBuilder httpProtocol = http
            .baseUrl("https://videogamedb.uk/api")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    {
        // Call the desired scenario and set the load profile
        setUp(
                VideoGameScenarios.fullCreationCycle().injectOpen(
                        rampUsers(50).during(60) // Pour 50 users over a minute
                )
        ).protocols(httpProtocol);
    }
}