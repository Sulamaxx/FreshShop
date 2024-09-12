/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.CartDTO;
import dto.UserDTO;
import entity.Cart;
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
@WebServlet(name = "LoadCartData", urlPatterns = {"/LoadCartData"})
public class LoadCartData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("success", false);

        Gson gson = new Gson();
        Session session = HibernateUtil.getSessionFactory().openSession();
        HttpSession httpSession = req.getSession();

        try {
            //check user signed in
            if (httpSession.getAttribute("user") != null) {
                // need load db cart
                UserDTO userDTO = (UserDTO) httpSession.getAttribute("user");

                Criteria criteria0 = session.createCriteria(User.class);
                criteria0.add(Restrictions.eq("email", userDTO.getEmail()));
                Object user = (User) criteria0.uniqueResult();

                Criteria criteria = session.createCriteria(Cart.class);
                criteria.add(Restrictions.eq("user", user));

                if (criteria.list().isEmpty()) {
                    //empty db cart
                    responseObject.addProperty("success", true);
                    responseObject.addProperty("message", "Cart empty");
                    responseObject.add("cartList", gson.toJsonTree(new ArrayList<CartDTO>()));
                } else {
                    //db cart
                    List<Cart> cartList = criteria.list();

                    for (Cart cart : cartList) {
                        cart.setUser(null);
                    }

                    responseObject.addProperty("success", true);
                    responseObject.addProperty("message", "Cart loading");
                    responseObject.add("cartList", gson.toJsonTree(cartList));

                }

            } else {
                // need load session cart
                //check availability of session cart
                if (httpSession.getAttribute("sessionCart") != null) {
                    //available
                    ArrayList<CartDTO> sessionCart = (ArrayList<CartDTO>) httpSession.getAttribute("sessionCart");
                    responseObject.addProperty("success", true);
                    responseObject.addProperty("message", "Session cart");
                    responseObject.add("cartList", gson.toJsonTree(sessionCart));
                } else {
                    // not available
                    responseObject.addProperty("success", true);
                    responseObject.addProperty("message", "Session cart empty");
                    responseObject.add("cartList", gson.toJsonTree(new ArrayList<CartDTO>()));
                }

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
