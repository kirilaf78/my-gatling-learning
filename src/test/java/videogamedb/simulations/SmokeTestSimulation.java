package videogamedb.simulations;

import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import videogamedb.scenarios.VideoGameScenarios;
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class SmokeTestSimulation extends Simulation {
    private HttpProtocolBuilder httpProtocol = http
            .baseUrl("https://videogamedb.uk/api")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    {
        setUp(
                VideoGameScenarios.fullCreationCycle().injectOpen(
                        atOnceUsers(1) // Only one user to check the code
                )).protocols(httpProtocol);
    }
}