import java.io.IOException;
import java.lang.IllegalArgumentException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Cette classe permet d'obtenir les données d'un post Instagram
 * à partir de son lien url.
 */
public class InstagramDownloader {

    private String link; //lien Instagram
    private String token; //token de Facebook (/Instagram)

    /**
     * Crée l'objet à partir d'un lien
     * @param ILink un lien Instagram 
     */
    public InstagramDownloader(String ILink){
        if(ILink != null){
            this.link = ILink;
        } else {
            System.out.println("Lien incorrect");
        }
    }

    /**
     * réalise une requête avec l'api d'Instagram
     * et enregistre les données obtenues
     */
    public void requetage(){
        try {
            String postId = extractPostId(this.link);
            getNewToken();
            if (postId != null && this.token !=null) {
                String apiUrl = buildApiUrl(postId, this.token); 
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("accept", "application/json");

                // Cette ligne effectue la requête
                InputStream responseStream = connection.getInputStream();

                // Conversion manuelle du corps de réponse InputStream en PostData en utilisant Jackson
                ObjectMapper mapper = new ObjectMapper();
                PostData postData = mapper.readValue(responseStream, PostData.class);

                // Nous avons maintenant les données du post
                System.out.println("Nom du propriétaire : " + postData.username);
                System.out.println("URL de la vidéo : " + postData.media_url);
            } else {
                System.out.println("Identifiant de publication non valide.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * extrait l'id d'un post à partir de son url
     * La syntaxe du lien dépend du type de post.
     * vidéo instagram : https://www.instagram.com/reel/{idpost}/?igshid={suitedechar}==
     * image instagram : https://www.instagram.com/p/{idpost}/?igshid={suitedechar}==
     * story instagram : https://www.instagram.com/stories/{utilisateur}/{idpost}/?igshid={suitedechar}==
     * @param instagramLink le lien url vers le post Instagram
     * @return l'ID du post
     * @throws IllegalArgumentException si le lien ne correspond pas à l'une des trois syntaxe
     */
    private String extractPostId(String instagramLink) throws IllegalArgumentException{
       
        String IDPost = "";
        int i=0; //index du premier caractère de l'id
        int j; //index du dernier caractère de l'id
        if(this.link.charAt(26) == 'r' ){
            i = 31;
        } else if ( this.link.charAt(26) == 'p'){
            i = 28;
        } else if ( this.link.charAt(26) == 's'){
            i = 34;
        } else {
            throw new IllegalArgumentException("lien incompatible.");
        }
        
        j = this.link.indexOf("/", i);
        IDPost = this.link.substring(i, j);
        return IDPost;
    }

    /**
     * Construit la syntaxe de la requete
     * @param postID l'id du post
     * @param accessToken le token généré
     * @return l'url complet
     */
    private String buildApiUrl(String postId, String accessToken) {
        return "https://graph.facebook.com/v17.0/" + postId + "?fields=id,username,media_url&access_token=" + accessToken;
    }

    /**
     * Genere un nouveau token grâce à une requête
     * @throws IOException si l'acquisition du token n'a pas fonctionné
     */
    private void getNewToken() throws IOException{

        String apiTokenUrl = "https://graph.facebook.com/oauth/access_token?client_id=225972180145359&client_secret=d0ed44c112607874052179cd692ebd89&grant_type=client_credentials"; 
        
        try {
            URL url = new URL(apiTokenUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("accept", "application/json");

            // effectue la requête
            InputStream responseStream = connection.getInputStream();

            // Conversion manuelle du corps de réponse InputStream en PostData en utilisant Jackson
            ObjectMapper mapper = new ObjectMapper();
            PostData postData = mapper.readValue(responseStream, PostData.class);

            if(TokenData.leToken !=null){
                // Nous avons maintenant accès au token
                this.token = TokenData.leToken;
            }else{
                throw new IOException("le Token n'a pas pu être récupéré.");
            }
            
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    
}
