/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.UserDTO;
import entity.User;
import model.MD5HashChecker;
import model.Validations;
import java.io.IOException;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import model.Mail;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author sjeew
 */
@WebServlet(name = "UserRegistration", urlPatterns = {"/UserRegistration"})
public class UserRegistration extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("successs", false);

        Gson gson = new Gson();
        Session session = HibernateUtil.getSessionFactory().openSession();

        UserDTO userDTO = gson.fromJson(req.getReader(), UserDTO.class);

        //validation
        if (userDTO.getFirst_name().isEmpty()) {
            responseObject.addProperty("message", "Please enter you first name");
        } else if (userDTO.getLast_name().isEmpty()) {
            responseObject.addProperty("message", "Please enter you last name");
        } else if (userDTO.getEmail().isEmpty()) {
            responseObject.addProperty("message", "Please enter you email");
        } else if (!Validations.isEmail(userDTO.getEmail())) {
            responseObject.addProperty("message", "Invalid email address");
        } else if (userDTO.getPassword().isEmpty()) {
            responseObject.addProperty("message", "Please enter you password");
        } else if (!Validations.isPassword(userDTO.getPassword())) {
            responseObject.addProperty("message", "Password must include at least one uppercase letter, number," + "special character and be at eight characters length");
        } else {
            //check user exist
            Criteria criteria = session.createCriteria(User.class);
            criteria.add(Restrictions.eq("email", userDTO.getEmail()));
            if (!criteria.list().isEmpty()) {
                //user not found
                responseObject.addProperty("message", "User already exist");
            } else {
                // user registration
                try {
                    int code = (int) (Math.random() * 1000000);
                    User user = new User();
                    user.setFirst_name(userDTO.getFirst_name());
                    user.setLast_name(userDTO.getLast_name());
                    user.setEmail(userDTO.getEmail());
                    user.setPassword(MD5HashChecker.hashPassword(userDTO.getPassword()));
                    user.setVerification(code);
                    user.setDatetime(new Date());
                    session.save(user);

                    new Thread(() -> {
                        Mail.sendMail(user.getEmail(), "Account Verification", "<h1>verification Code :" + user.getVerification() + "</h1>");
                    }).start();

                    req.getSession().setAttribute("email", user.getEmail());
                    session.beginTransaction().commit();

                    responseObject.addProperty("success", true);
                    responseObject.addProperty("message", "user registered successfully");

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    session.close();
                }

            }

        }
        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseObject));

    }
}
