package com.makas;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

@WebServlet(
        name = "storeServlet",
        urlPatterns = "/shop"
)
public class StoreServlet extends HttpServlet
{
	// We are using a map as a shopping cart instead of a database for this is a 
	//a demonstration exercice
    private final Map<Integer, String> products = new Hashtable<>();
    
    // The map is populated with products when the servlet is instanciated
    public StoreServlet()
    {
        this.products.put(1, "Sandpaper");
        this.products.put(2, "Nails");
        this.products.put(3, "Glue");
        this.products.put(4, "Paint");
        this.products.put(5, "Tape");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
    	
        String action = request.getParameter("action");
        if(action == null)
        //We want the browse.jsp to the entry page of the app
            action = "browse";

        switch(action)
        {
            case "addToCart":
                this.addToCart(request, response);
                break;

            case "emptyCart":
                this.emptyCart(request, response);
                break;

            case "viewCart":
                this.viewCart(request, response);
                break;

            case "browse":
            default:
                this.browse(request, response);
                break;
        }
    }

    private void addToCart(HttpServletRequest request,
                           HttpServletResponse response)
            throws ServletException, IOException
    {
        int productId;
        
        //get the product to be added to the cart
        try
        {
            productId = Integer.parseInt(request.getParameter("productId"));
            
        }
        catch(Exception e)
        {
        	//if ParseException occurs, go back to the 
            response.sendRedirect("shop");
            return;
        }
  //if cart does not exist (case of a new session) create a cart and add it to the session scope.
        HttpSession session = request.getSession();
        if(session.getAttribute("cart") == null)
            session.setAttribute("cart", new Hashtable<Integer, Integer>());

        @SuppressWarnings("unchecked")
        Map<Integer, Integer> cart =
                (Map<Integer, Integer>)session.getAttribute("cart");
        if(!cart.containsKey(productId))
            cart.put(productId, 0);
        cart.put(productId, cart.get(productId) + 1);

        response.sendRedirect("shop?action=viewCart");
    }
    // empty cart just remove the cart from the session
    private void emptyCart(HttpServletRequest request,
                           HttpServletResponse response)
            throws ServletException, IOException
    {
        request.getSession().removeAttribute("cart");
        response.sendRedirect("shop?action=viewCart");
    }

    private void viewCart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
    	//set the product on the request scop and sand it to viewCart.jsp
        request.setAttribute("products", this.products);
        request.getRequestDispatcher("/WEB-INF/jsp/view/viewCart.jsp")
                .forward(request, response);
    }

    private void browse(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        request.setAttribute("products", this.products);
        request.getRequestDispatcher("/WEB-INF/jsp/view/browse.jsp")
               .forward(request, response);
    }
}
