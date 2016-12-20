import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;


@WebServlet(name = "Search")
public class Search extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String search = request.getParameter("query");
        request.setAttribute("query", search);
        String searchType = request.getParameter("searchType");
        search = search.toLowerCase();
        QueryManager qm = new QueryManager();
        String page;

        if (searchType.equals("keyword")) {
            //  Artists
            ArrayList<ArrayList<String>> artists = qm.getArtistByName(search);
            request.setAttribute("artists", artists.get(0));
            request.setAttribute("artistsID", artists.get(1));

            //  Albums
            ArrayList<ArrayList<String>> albums = qm.getAlbumsByTitle(search);
            request.setAttribute("albums", albums.get(0));
            request.setAttribute("albumsID", albums.get(1));

            //  Tracks
            ArrayList<ArrayList<String>> tracks = qm.getTracksByTitle(search);
            request.setAttribute("tracks", tracks.get(0));
            request.setAttribute("tracksID", tracks.get(1));

            page = "keywordsearch.jsp";

        }

        else{
            ArrayList<ArrayList<String>> results = qm.getSemanticResults(search);
            request.setAttribute("results", results.get(0));
            request.setAttribute("resultsID", results.get(1));

            page = "semanticsearch.jsp";
        }

        qm.closeConnections();
        RequestDispatcher view = request.getRequestDispatcher(page);
        view.forward(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
