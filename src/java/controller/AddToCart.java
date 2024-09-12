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
import org.hibernate.Session;

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
                        //not developed
                        UserDTO userDTO = (UserDTO) httpSession.getAttribute("user");

                        Cart cart = new Cart();
                        cart.setProduct(product);
                    } else {
                        // session cart
                        ArrayList<CartDTO> sessionCart;
                        if (httpSession.getAttribute("sessionCart") != null) {
                            //already session cart exist
                            sessionCart = (ArrayList<CartDTO>) httpSession.getAttribute("sessionCart");

                            //check product is already in session cart
                            for (CartDTO cartDTO : sessionCart) {
                                if (cartDTO.getProduct().getId() == product.getId()) {
                                    // product already in session cart
                                    //check qty
                                    if (product.getQty() >= (cartDTO.getQty() + Double.parseDouble(qty))) {
                                        //updaete qty
                                        cartDTO.setQty(cartDTO.getQty() + Double.parseDouble(qty));
                                        responseObject.addProperty("message", "Successfully updated cart");
                                    } else {
                                        responseObject.addProperty("message", "Product quantity not enough");
                                    }
                                } else {
                                    //product new to session cart
                                    CartDTO cartDTO1 = new CartDTO();
                                    cartDTO1.setProduct(product);
                                    cartDTO1.setQty(Double.parseDouble(qty));

                                    sessionCart.add(cartDTO1);

                                    responseObject.addProperty("success", true);
                                    responseObject.addProperty("message", "Successfully added to cart");
                                }
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

    }

}
