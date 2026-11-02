package ma.emsi.benyassine.tp0.rtp1.service;

/** Exception levée lorsqu'une requête à l'API du LLM échoue. */
public class RequeteException extends Exception {
    private static final long serialVersionUID = 1L;


    private final String requeteJson;

    public RequeteException(String message) {
        super(message);
        this.requeteJson = null;
    }

    public RequeteException(String message, String requeteJson) {
        super(message);
        this.requeteJson = requeteJson;
    }

    public String getRequeteJson() {
        return requeteJson;
    }
}
