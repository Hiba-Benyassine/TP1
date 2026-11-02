package ma.emsi.benyassine.tp0.rtp1.jsf; // Package personnalisé


import ma.emsi.benyassine.tp0.rtp1.service.GenerateurReponse;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.SelectItem;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Backing bean pour la page JSF index.xhtml.
 * PortÃ©e view pour conserver l'Ã©tat de la conversation qui dure pendant plusieurs requÃªtes HTTP.
 * La portÃ©e view nÃ©cessite l'implÃ©mentation de Serializable (le backing bean peut Ãªtre mis en mÃ©moire secondaire).
 */
@Named
@ViewScoped
public class Bb implements Serializable {

    /**
     * RÃ´le "systÃ¨me" que l'on attribuera plus tard Ã  un LLM.
     * Possible d'Ã©crire un nouveau rÃ´le dans la liste dÃ©roulante.
     */
    private String roleSysteme;

    /**
     * Quand le rÃ´le est choisi par l'utilisateur dans la liste dÃ©roulante,
     * il n'est plus possible de le modifier (voir code de la page JSF), sauf si on veut un nouveau chat.
     */
    private boolean roleSystemeChangeable = true;

    /**
     * Liste de tous les rÃ´les de l'API prÃ©dÃ©finis.
     */
    private List<SelectItem> listeRolesSysteme;

    /**
     * DerniÃ¨re question posÃ©e par l'utilisateur.
     */
    private String question;
    /**
     * DerniÃ¨re rÃ©ponse de l'API OpenAI.
     */
    private String reponse;
    /**
     * La conversation depuis le dÃ©but.
     */
    private StringBuilder conversation = new StringBuilder();

    /**
     * Service pour modifier la question et gÃ©nÃ©rer la rÃ©ponse.
     */
    @Inject
    private GenerateurReponse generateurReponse;

    /**
     * Contexte JSF. UtilisÃ© pour qu'un message d'erreur s'affiche dans le formulaire.
     */
    @Inject
    private FacesContext facesContext;

    /**
     * Obligatoire pour un bean CDI (classe gÃ©rÃ©e par CDI), s'il y a un autre constructeur.
     */
    public Bb() {
    }

    public String getRoleSysteme() {
        return this.roleSysteme;
    }

    public void setRoleSysteme(String roleSysteme) {
        this.roleSysteme = roleSysteme;
    }

    public boolean isRoleSystemeChangeable() {
        return this.roleSystemeChangeable;
    }

    public String getQuestion() {
        return this.question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getReponse() {
        return this.reponse;
    }

    /**
     * setter indispensable pour le textarea.
     *
     * @param reponse la rÃ©ponse Ã  la question.
     */
    public void setReponse(String reponse) {
        this.reponse = reponse;
    }

    public String getConversation() {
        return conversation.toString();
    }

    public void setConversation(String conversation) {
        this.conversation = new StringBuilder(conversation);
    }

    /**
     * Envoie la question au serveur.
     * En attendant de l'envoyer Ã  un LLM, le serveur fait un traitement quelconque, juste pour tester :
     * Le traitement consiste Ã  copier la question en minuscules et Ã  l'entourer avec "||". Le rÃ´le systÃ¨me
     * est ajoutÃ© au dÃ©but de la premiÃ¨re rÃ©ponse.
     *
     * @return null pour rester sur la mÃªme page.
     */
    public String envoyer() {
        if (this.question == null || this.question.isBlank()) {
            // Erreur ! Le formulaire va Ãªtre rÃ©affichÃ© en rÃ©ponse Ã  la requÃªte POST, avec un message d'erreur.
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Texte question vide", "Il manque le texte de la question");
            facesContext.addMessage(null, message);
            return null;
        }
        
        // Traite la question pour construire la rÃ©ponse.
        if (this.conversation.isEmpty()) { // Si la conversation n'a pas encore commencÃ©
            // Fige la liste dÃ©roulante qui permet de choisir le rÃ´le systÃ¨me
            this.roleSystemeChangeable = false;
        }
        this.reponse = this.generateurReponse.genererReponse(this.question, this.roleSysteme);
        
        // La conversation contient l'historique des questions-rÃ©ponses depuis le dÃ©but.
        afficherConversation();
        return null;
    }

    /**
     * Pour un nouveau chat.
     * Termine la portÃ©e view en retournant "index" (la page index.xhtml sera affichÃ©e aprÃ¨s le traitement
     * effectuÃ© pour construire la rÃ©ponse) et pas null. null aurait indiquÃ© de rester dans la mÃªme page (index.xhtml)
     * sans changer de vue.
     * Le fait de changer de vue va faire supprimer l'instance en cours du backing bean par CDI et donc on reprend
     * tout comme au dÃ©but puisqu'une nouvelle instance du backing va Ãªtre utilisÃ©e par la page index.xhtml.
     * @return "index"
     */
    public String nouveauChat() {
        return "index";
    }

    /**
     * Pour afficher la conversation dans le textArea de la page JSF.
     */
    private void afficherConversation() {
        this.conversation.append("== User:\n").append(question).append("\n== Serveur:\n").append(reponse).append("\n");
    }

    public List<SelectItem> getRolesSysteme() {
        if (this.listeRolesSysteme == null) {
            // GÃ©nÃ¨re les rÃ´les de l'API prÃ©dÃ©finis
            this.listeRolesSysteme = new ArrayList<>();
            // Vous pouvez Ã©videmment Ã©crire ces rÃ´les dans la langue que vous voulez.
            String role = """
                    You are a helpful assistant. You help the user to find the information they need.
                    If the user type a question, you answer it.
                    """;
            // 1er argument : la valeur du rÃ´le (envoyÃ© au serveur)
            // 2Ã¨me argument : le libellÃ© du rÃ´le dans la liste dÃ©roulante.
            this.listeRolesSysteme.add(new SelectItem(role, "Assistant"));

            role = """
                    You are an interpreter. You translate from English to French and from French to English.
                    If the user type a French text, you translate it into English.
                    If the user type an English text, you translate it into French.
                    If the text contains only one to three words, give some examples of usage of these words in English.
                    """;
            this.listeRolesSysteme.add(new SelectItem(role, "Traducteur Anglais-FranÃ§ais"));

            role = """
                    Your are a travel guide. If the user type the name of a country or of a town,
                    you tell them what are the main places to visit in the country or the town
                    are you tell them the average price of a meal.
                    """;
            this.listeRolesSysteme.add(new SelectItem(role, "Guide touristique"));
        }

        return this.listeRolesSysteme;
    }

}
