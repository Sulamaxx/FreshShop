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
import entity.Product;
import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.HibernateUtil;
import model.Validations;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author sjeew
 */
@WebServlet(name = "AddToCart", urlPatterns = {"/AddToCart"})
public class AddToCart extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("success", false);

        Gson gson = new Gson();
        Session session = HibernateUtil.getSessionFactory().openSession();
        HttpSession httpSession = req.getSession();

        String id = req.getParameter("id");
        String qty = req.getParameter("qty");

        if (id.isEmpty()) {
            responseObject.addProperty("message", "Product must required");
        } else if (!Validations.isInteger(id)) {
            responseObject.addProperty("message", "Product not found");
        } else if (qty.isEmpty()) {
            responseObject.addProperty("message", "Quantity must required");
        } else if (!Validations.isDouble(qty)) {
            responseObject.addProperty("message", "Invalid quantity");
        } else {

            try {
                //check product
                Product product = (Product) session.get(Product.class, Integer.parseInt(id));

                if (product == null) {
                    responseObject.addProperty("message", "Product not found");
                } else {

                    if (httpSession.getAttribute("user") != null) {
                        //db cart
                        UserDTO userDTO = (UserDTO) httpSession.getAttribute("user");

                        Criteria criteria0 = session.createCriteria(User.class);
                        criteria0.add(Restrictions.eq("email", userDTO.getEmail()));
                        User user = (User) criteria0.uniqueResult();

                        //check user has already db cart
                        Criteria criteria1 = session.createCriteria(Cart.class);
                        criteria1.add(Restrictions.eq("user", user));
                        criteria1.add(Restrictions.eq("product", product));
                        if (criteria1.list().isEmpty()) {
                            //user hasn't db cart yet for this product
                            //check qty availability
                            if (product.getQty() >= Double.parseDouble(qty)) {
                                Cart cart = new Cart();
                                cart.setProduct(product);
                                cart.setQty(Double.parseDouble(qty));
                                cart.setUser(user);
                                session.save(cart);

                                responseObject.addProperty("success", true);
                                responseObject.addProperty("message", "Successfully added to cart");
                            } else {
                                responseObject.addProperty("message", "Quantity not available");
                            }

                        } else {
                            // uesr has db cart for this product
                            Cart cart = (Cart) criteria1.list().get(0);
                            //check qty availability
                            if (product.getQty() >= (cart.getQty() + Double.parseDouble(qty))) {

                                cart.setQty(cart.getQty() + Double.parseDouble(qty));

                                session.update(cart);

                                responseObject.addProperty("success", true);
                                responseObject.addProperty("message", "Successfully updated cart");
                            } else {
                                responseObject.addProperty("message", "Quantity not available");
                            }

                        }
                        session.beginTransaction().commit();

                    } else {
                        // session cart
                        ArrayList<CartDTO> sessionCart;
                        if (httpSession.getAttribute("sessionCart") != null) {
                            //already session cart exist
                            sessionCart = (ArrayList<CartDTO>) httpSession.getAttribute("sessionCart");

                            boolean isFound = false;
                            //check product is already in session cart
                            for (CartDTO cartDTO : sessionCart) {

                                if (cartDTO.getProduct().getId() == product.getId()) {
                                    // product already in session cart
                                    //check qty
                                    isFound = true;
                                    if (product.getQty() >= (cartDTO.getQty() + Double.parseDouble(qty))) {
                                        //updaete qty
                                        cartDTO.setQty(cartDTO.getQty() + Double.parseDouble(qty));

                                        responseObject.addProperty("success", true);
                                        responseObject.addProperty("message", "Successfully updated cart");
                                    } else {
                                        responseObject.addProperty("message", "Product quantity not enough");
                                    }
                                    break;
                                }
                            }

                            if (!isFound) {
                                //product new to session cart
                                CartDTO cartDTO1 = new CartDTO();
                                product.setUser(null);
                                cartDTO1.setProduct(product);
                                cartDTO1.setQty(Double.parseDouble(qty));

                                sessionCart.add(cartDTO1);

                                responseObject.addProperty("success", true);
                                responseObject.addProperty("message", "Successfully added to cart");
                            }

                        } else {
                            // not found sesion cart
                            sessionCart = new ArrayList<>();
                            //check qty
                            if (product.getQty() >= Double.parseDouble(qty)) {
                                CartDTO cartDTO = new CartDTO();
                                cartDTO.setProduct(product);
                                cartDTO.setQty(Double.parseDouble(qty));
                                sessionCart.add(cartDTO);
                                httpSession.setAttribute("sessionCart", sessionCart);

                                responseObject.addProperty("success", true);
                                responseObject.addProperty("message", "Successfully added to cart");

                            } else {
                                responseObject.addProperty("message", "Quatity not available");
                            }
                        }

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                session.close();
            }
        }

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseObject));

    }

}
