import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Asus on 15-12-2016.
 */
@WebServlet(name = "/TrackPage")
public class TrackPage extends HttpServlet {
    /*protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }*/

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name").replace("+"," ");
        QueryManager qm = new QueryManager();
        ArrayList<ArrayList<String>> results = qm.getTrackInfo(name);
        String title = results.get(0).get(0);
        ArrayList<String> album = results.get(1);
        ArrayList<String> artist = results.get(2);
        ArrayList<String> number = results.get(3);
        ArrayList<String> length = results.get(4);

        request.setAttribute("title", title);
        request.setAttribute("album", album);
        request.setAttribute("artist", artist);
        request.setAttribute("number", number);
        request.setAttribute("length", length);

        qm.closeConnections();
        RequestDispatcher view=request.getRequestDispatcher("trackPage.jsp");
        view.forward(request,response);
    }
}
