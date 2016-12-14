import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalTime;
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
        ArrayList<String> tracksNumber = result.get(1);
        ArrayList<String> tracksTitle = result.get(2);
        ArrayList<String> tracksLength = result.get(3);

        for(int i=0; i<tracksLength.size(); i++){
            LocalTime timeOfDay = LocalTime.ofSecondOfDay(Integer.parseInt(tracksLength.get(i)));
            tracksLength.set(i, timeOfDay.toString().substring(3));
        }

        request.setAttribute("result", albumInfo);
        request.setAttribute("number", tracksNumber);
        request.setAttribute("title", tracksTitle);
        request.setAttribute("length", tracksLength);
        RequestDispatcher view=request.getRequestDispatcher("albumPage.jsp");
        view.forward(request,response);

    }
}
