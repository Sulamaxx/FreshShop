/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.CartDTO;
import dto.UserDTO;
import entity.Address;
import entity.Cart;
import entity.Category;
import entity.City;
import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author sjeew
 */
@WebServlet(name = "LoadCheckOut", urlPatterns = {"/LoadCheckOut"})
public class LoadCheckOut extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("success", false);

        Gson gson = new Gson();
        Session session = HibernateUtil.getSessionFactory().openSession();
        HttpSession httpSession = req.getSession();

        try {
            // check user in session
            if (httpSession.getAttribute("user") != null) {
                UserDTO userDTO = (UserDTO) httpSession.getAttribute("user");
                //get user in db
                Criteria criteria = session.createCriteria(User.class);
                criteria.add(Restrictions.eq("email", userDTO.getEmail()));
                User user = (User) criteria.uniqueResult();
                //get cart in db
                Criteria criteria1 = session.createCriteria(Cart.class);
                criteria1.add(Restrictions.eq("user", user));

                if (criteria1.list().isEmpty()) {
                    // empty cart db
                    responseObject.addProperty("message", "empty cart. add required product to cart");
                } else {

                    List<Cart> cartList = criteria1.list();

                    for (Cart cart : cartList) {
                        cart.getProduct().setUser(null);
                    }
                    responseObject.addProperty("success", true);
                    responseObject.addProperty("message", "Loading checkout items...");
                    responseObject.add("cartList", gson.toJsonTree(cartList));

                    // get cities
                    Criteria criteria3 = session.createCriteria(City.class);
                    List<City> cityList = criteria3.list();

                    responseObject.add("cityList", gson.toJsonTree(cityList));

                    // get address
                    Criteria criteria2 = session.createCriteria(Address.class);
                    criteria2.add(Restrictions.eq("user", user));

                    if (criteria2.list().isEmpty()) {
                        responseObject.add("addressList", gson.toJsonTree(new ArrayList<Address>()));
                    } else {
                        //user has addresses
                        List<Address> addressList = criteria2.list();
                        responseObject.add("addressList", gson.toJsonTree(addressList));
                    }

                }

            } else {
                responseObject.addProperty("message", "Something went wrong");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseObject));
    }

}
