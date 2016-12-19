import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Rocha on 14/12/2016.
 */
@WebServlet(name = "/ArtistPage")
public class ArtistPage extends HttpServlet {

    public void init(){
        System.out.println("Checking artist");
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name").replace("+"," ");
        String id = request.getParameter("id");
        QueryManager qm = new QueryManager();
        ArrayList<ArrayList<String>> result = qm.getArtistInfo(name, id);
        qm.closeConnections();
        ArrayList<String> artistInfo = result.get(0);
        ArrayList<String> artistAlbums = result.get(1);


        request.setAttribute("result", artistInfo);
        request.setAttribute("albums", artistAlbums);
        request.setAttribute("albumsID", result.get(2));
        RequestDispatcher view=request.getRequestDispatcher("artistPage.jsp");
        view.forward(request,response);
    }
}
