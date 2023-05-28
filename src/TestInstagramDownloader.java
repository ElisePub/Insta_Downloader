import java.net.URL;
import java.net.MalformedURLException;

public class TestInstagramDownloader{
    public static void main(String[] args){
        String url = "https://www.instagram.com/p/CsoZOlVPdu0/?utm_source=ig_web_copy_link&igshid=MzRlODBiNWFlZA==";
        
        InstagramDownloader v = new InstagramDownloader(url);
        v.requetage();
    }
}