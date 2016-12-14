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
@WebServlet(name = "/AlbumPage")
public class AlbumPage extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name").replace("+"," ");

        QueryManager qm = new QueryManager();
        ArrayList<ArrayList<String>> result = qm.getAlbumInfo(name);
        ArrayList<String> albumInfo = result.get(0);
        //ArrayList<String> albumTracks = result.get(1);

        request.setAttribute("result", albumInfo);
        //request.setAttribute("tracks", albumTracks);
        RequestDispatcher view=request.getRequestDispatcher("albumPage.jsp");
        view.forward(request,response);

    }
}
