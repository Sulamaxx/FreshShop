/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.UserDTO;
import entity.Address;
import entity.Cart;
import entity.City;
import entity.OrderItem;
import entity.OrderStatus;
import entity.Orders;
import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
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
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author sjeew
 */
@WebServlet(name = "CheckOut", urlPatterns = {"/CheckOut"})
public class CheckOut extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("success", false);

        Gson gson = new Gson();
        Session session = HibernateUtil.getSessionFactory().openSession();
        HttpSession httpSession = req.getSession();

        try {

            JsonObject requestObject = gson.fromJson(req.getReader(), JsonObject.class);

            int selectedAddress = requestObject.get("selectedAddress").getAsInt();

            String first_name = requestObject.get("first_name").getAsString();
            String last_name = requestObject.get("last_name").getAsString();
            String mobile = requestObject.get("mobile").getAsString();
            String email = requestObject.get("email").getAsString();
            String line1 = requestObject.get("line1").getAsString();
            String line2 = requestObject.get("line2").getAsString();
            String city = requestObject.get("city").getAsString();
            String postal_code = requestObject.get("postal_code").getAsString();

            if (httpSession.getAttribute("user") != null) {
                Transaction transaction = session.beginTransaction();

                UserDTO userDTO = (UserDTO) httpSession.getAttribute("user");

                Criteria criteria = session.createCriteria(User.class);
                criteria.add(Restrictions.eq("email", userDTO.getEmail()));

                User user = (User) criteria.uniqueResult();

                if (selectedAddress > 0) {
                    // exist address
                    if (session.get(Address.class, selectedAddress) != null) {
                        Address address = (Address) session.get(Address.class, selectedAddress);
                        //checkout process
                        orderPlace(address, user, session, responseObject, transaction);

                    } else {
                        responseObject.addProperty("message", "Something went wrong try again later");
                    }
                } else {
                    // new address
                    if (first_name.isEmpty()) {
                        responseObject.addProperty("message", "Enter first name");
                    } else if (last_name.isEmpty()) {
                        responseObject.addProperty("message", "Enter last name");
                    } else if (mobile.isEmpty()) {
                        responseObject.addProperty("message", "Enter mobile number");
                    } else if (!Validations.isMobile(mobile)) {
                        responseObject.addProperty("message", "Invalid mobile number");
                    } else if (email.isEmpty()) {
                        responseObject.addProperty("message", "Enter email address");
                    } else if (!Validations.isEmail(email)) {
                        responseObject.addProperty("message", "Invalid email address");
                    } else if (line1.isEmpty()) {
                        responseObject.addProperty("message", "Enter address line 1");
                    } else if (line2.isEmpty()) {
                        responseObject.addProperty("message", "Enter adderss line 2");
                    } else if (!Validations.isInteger(city)) {
                        responseObject.addProperty("message", "Select city");
                    } else if (postal_code.isEmpty()) {
                        responseObject.addProperty("message", "Enter postal code");
                    } else if (!Validations.isInteger(postal_code)) {
                        responseObject.addProperty("message", "Invalid postal code");
                    } else if (postal_code.length() != 5) {
                        responseObject.addProperty("message", "postal code must contain 5 numbers");
                    } else {
                        if (session.get(City.class, Integer.parseInt(city)) != null) {
                            
                            City cityDB = (City) session.get(City.class, Integer.parseInt(city));

                            // save address
                            Address address = new Address();
                            address.setCity(cityDB);
                            address.setUser(user);
                            address.setEmail(email);
                            address.setFirst_name(first_name);
                            address.setLast_name(last_name);
                            address.setLine1(line1);
                            address.setLine2(line2);
                            address.setMobile(mobile);
                            address.setPostal_code(postal_code);
                            session.save(address);

                            //checkout process
                            orderPlace(address, user, session, responseObject, transaction);

                        } else {
                            responseObject.addProperty("message", "Invalid city");
                        }
                    }

                }
            } else {
                responseObject.addProperty("message", "Some thing went wrong");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }

        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseObject));

    }

    private void orderPlace(Address address, User user, Session session, JsonObject responseObject, Transaction transaction) {
        try {

            Orders order = new Orders();
            order.setAddress(address);
            order.setUser(user);
            session.save(order);

            Criteria criteria = session.createCriteria(Cart.class);
            criteria.add(Restrictions.eq("user", user));

            if (criteria.list().isEmpty()) {
                responseObject.addProperty("message", "Cart empty");
            } else {
                List<Cart> cartList = criteria.list();
                OrderStatus orderStatus = (OrderStatus) session.get(OrderStatus.class, 1);

                for (Cart cart : cartList) {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setDatetime(new Date());
                    orderItem.setOrder(order);
                    orderItem.setOrderStatus(orderStatus);
                    orderItem.setProduct(cart.getProduct());
                    orderItem.setQty(cart.getQty());
                    // save order
                    session.save(orderItem);

                    //product qty update
                    cart.getProduct().setQty(cart.getProduct().getQty() - cart.getQty());
                    session.update(cart.getProduct());

                    // delete cat item
                    session.delete(cart);
                }

                //payhere dto
                responseObject.addProperty("success", true);
                responseObject.addProperty("message", "Order Place Successfully");

            }

            transaction.commit();

        } catch (Exception e) {
            transaction.rollback();
        }
    }

}
