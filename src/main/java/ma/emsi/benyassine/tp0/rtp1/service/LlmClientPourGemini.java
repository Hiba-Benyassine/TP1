package ma.emsi.benyassine.tp0.rtp1.service;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.Dependent;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.Serializable;

/** Client REST pour l'API Gemini. */
@Dependent
public class LlmClientPourGemini implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String GEMINI_ENDPOINT =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash-lite:generateContent";


    private final String key;
    private transient Client clientRest;
    private final WebTarget target;

    public LlmClientPourGemini() {
        this.key = System.getenv("GEMINI_KEY");
        if (this.key == null || this.key.isBlank()) {
            throw new IllegalStateException("La variable d'environnement GEMINI_KEY n'est pas définie.");
        }
        this.clientRest = ClientBuilder.newClient();
        this.target = clientRest.target(GEMINI_ENDPOINT).queryParam("key", this.key);
    }

    public Response envoyerRequete(Entity<String> requestEntity) {
        Invocation.Builder request = target.request(MediaType.APPLICATION_JSON_TYPE);
        return request.post(requestEntity);
    }

    @PreDestroy
    public void closeClient() {
        if (this.clientRest != null) {
            this.clientRest.close();
        }
    }
}
