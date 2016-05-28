package coursesbr.examples.p2popularmovies;

/**
 * Created by Soledad on 5/19/2016.
 */
public class AndroidMovie {
    String originalTitle;
    //int image;
    String image_url;
    String synopsis;
    String userRating;
    String releaseDate;
    String backdrop_url;

    public AndroidMovie(String oTitle, String imageUrl, String sypno,String uRating,String rDate, String backdropUrl){
        this.originalTitle=oTitle;
        this.image_url=imageUrl;
        this.synopsis=sypno;
        this.userRating=uRating;
        this.releaseDate=rDate;
        this.backdrop_url=backdropUrl;
    }
}
