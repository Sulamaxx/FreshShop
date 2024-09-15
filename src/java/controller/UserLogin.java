/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import model.MD5HashChecker;
import model.Mail;
import model.Validations;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author sjeew
 */
@WebServlet(name = "UserLogin", urlPatterns = {"/UserLogin"})
public class UserLogin extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("success", false);

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Session session = HibernateUtil.getSessionFactory().openSession();

        HttpSession httpSession = req.getSession();

        UserDTO userDTO = gson.fromJson(req.getReader(), UserDTO.class);

        try {

            if (userDTO.getEmail().isEmpty()) {
                responseObject.addProperty("message", "Please enter your email");
            } else if (!Validations.isEmail(userDTO.getEmail())) {
                responseObject.addProperty("message", "Invalid email");
            } else if (userDTO.getPassword().isEmpty()) {
                responseObject.addProperty("message", "Please enter your password");
            } else {
                //find user
                Criteria criteria = session.createCriteria(User.class);
                criteria.add(Restrictions.eq("email", userDTO.getEmail()));

                if (criteria.list().isEmpty()) {
                    //not found
                    responseObject.addProperty("message", "Invalid email or password");
                } else {
                    //found
                    User user = (User) criteria.list().get(0);
                    // check user credential
                    if (MD5HashChecker.checkPassword(userDTO.getPassword(), user.getPassword())) {
                        //Password matched

                        // check user has session cart and convert it db cart
                        if (httpSession.getAttribute("sessionCart") != null) {
                            ArrayList<CartDTO> sessionCart = (ArrayList<CartDTO>) httpSession.getAttribute("sessionCart");

                            //check user has db cart
                            Criteria criteria1 = session.createCriteria(Cart.class);
                            criteria1.add(Restrictions.eq("user", user));

                            if (criteria1.list().isEmpty()) {
                                //new to db cart
                                for (CartDTO cartDTO : sessionCart) {
                                    Cart cart = new Cart();
                                    cart.setProduct(cartDTO.getProduct());
                                    cart.setQty(cartDTO.getQty());
                                    cart.setUser(user);
                                    session.save(cart);
                                }
                            } else {
                                List<Cart> dbCartList = criteria1.list();

                                for (CartDTO cartDTO : sessionCart) {

                                    boolean isFound = false;

                                    for (Cart cart : dbCartList) {

                                        if (cartDTO.getProduct().getId() == cart.getProduct().getId()) {
                                            // exist product in db cart
                                            isFound = true;
                                            //check qty
                                            if (cart.getProduct().getQty() >= (cart.getQty() + cartDTO.getQty())) {
                                                //quantity available
                                                cart.setQty(cart.getQty() + cartDTO.getQty());

                                            } else {
                                                //qty not available
                                                // set max qty
                                                cart.setQty(cart.getProduct().getQty());

                                            }
                                            session.update(cart);
                                            break;
                                        }

                                    }

                                    if (!isFound) {
                                        // new to db cart
                                        Cart cart = new Cart();
                                        cart.setProduct(cartDTO.getProduct());
                                        cart.setQty(cartDTO.getQty());
                                        cart.setUser(user);

                                        session.save(cart);

                                    }

                                }

                            }
                            httpSession.removeAttribute("sessionCart");
                            session.beginTransaction().commit();

                        } else {
                            //no issue
                        }

                        if (user.getVerification() == 1111) {
                            // already verified
                            //create user dto
                            UserDTO userDTO1 = new UserDTO();
                            userDTO1.setFirst_name(user.getFirst_name());
                            userDTO1.setLast_name(user.getLast_name());
                            userDTO1.setEmail(user.getEmail());
                            userDTO1.setDatetime(user.getDatetime());
                            
                            httpSession.setAttribute("user", userDTO1);

                            responseObject.addProperty("message", "Login successfully");
                        } else {
                            // not verified
                            httpSession.setAttribute("email", user.getEmail());

                            //verification code sent and update db
                            int code = (int) (Math.random() * 1000000);

                            user.setVerification(code);
                            session.save(user);

                            new Thread(() -> {
                                Mail.sendMail(user.getEmail(), "Account Verification", "<h1>verification Code :" + user.getVerification() + "</h1>");
                            }).start();

                            session.beginTransaction().commit();
                            responseObject.addProperty("message", "Please verify your account");
                        }
                        responseObject.addProperty("success", true);

                    } else {
                        //password not matched
                        responseObject.addProperty("message", "Invalid email or password");
                    }

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
