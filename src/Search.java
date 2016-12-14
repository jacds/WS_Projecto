import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebServlet(name = "Search")
public class Search extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String search = request.getParameter("query");
        search = search.toLowerCase();
        String type = request.getParameter("type");
        QueryManager qm = new QueryManager();

        if(type.equals("artist")){

        }
        else if(type.equals("album")){

        }
        else if(type.equals("track")){

        }

        qm.closeConnections();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
