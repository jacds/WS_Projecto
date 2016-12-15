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
        search = search.toLowerCase();
        QueryManager qm = new QueryManager();

        //  Artists
        ArrayList<String> artists = qm.getArtistByName(search);
        request.setAttribute("artists", artists);

        //  Albums
        ArrayList<String> albums = qm.getAlbumsByTitle(search);
        request.setAttribute("albums", albums);

        //  Tracks
        ArrayList<String> tracks = qm.getTracksByTitle(search);
        request.setAttribute("tracks", tracks);

        qm.closeConnections();
        RequestDispatcher view=request.getRequestDispatcher("search.jsp");
        view.forward(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
