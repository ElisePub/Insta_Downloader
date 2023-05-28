import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.fasterxml.jackson.databind.ObjectMapper;

public class InstagramDownloader {

    private String link;

    public InstagramDownloader(String ILink){
        if(ILink != null){
            this.link = ILink;
        } else {
            System.out.println("Lien incorrect");
        }
    }

    public void requetage(){
        try {
            String postId = extractPostId(this.link);
            if (postId != null) {
                String apiUrl = buildApiUrl(postId, "225972180145359|JyHkGxHO7HkpZrgFTLvSDJGSjMY"); 
                System.out.println(apiUrl + "\n\n");
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

    private String extractPostId(String instagramLink) throws IOException{
        //syntaxe lien vidéo instagram : https://www.instagram.com/reel/{idpost}/?igshid={suitedechar}==
        //syntaxe lien image instagram : https://www.instagram.com/p/{idpost}/?igshid={suitedechar}==
        //syntaxe lien story instagram : https://www.instagram.com/stories/{utilisateur}/{idpost}/?igshid={suitedechar}==
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
            throw new IOException("lien incompatible.");
        }
        
        j = this.link.indexOf("/", i);
        IDPost = this.link.substring(i, j);
        System.out.println(IDPost);
        return IDPost;
    }

    private String buildApiUrl(String postId, String accessToken) {
        return "https://graph.facebook.com/v17.0/" + postId + "?fields=username,media_url&access_token=" + accessToken;
    }
}
