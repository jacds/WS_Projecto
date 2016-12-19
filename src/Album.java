import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Asus on 14-12-2016.
 */
@WebServlet(name = "/Album")
public class Album extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        QueryManager qm = new QueryManager();
        ArrayList<ArrayList<String>> result = qm.getAlbums();
        qm.closeConnections();

        request.setAttribute("result", result.get(0));
        request.setAttribute("id", result.get(1));
        RequestDispatcher view=request.getRequestDispatcher("albumList.jsp");
        view.forward(request,response);
    }
}
