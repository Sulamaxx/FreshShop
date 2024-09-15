/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dto.UserDTO;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author sjeew
 */
@WebServlet(name = "CheckLogin", urlPatterns = {"/CheckLogin"})
public class CheckLogin extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("success", false);
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        if(req.getSession().getAttribute("user")!=null){
            UserDTO userDTO=(UserDTO)  req.getSession().getAttribute("user");
        
        responseObject.add("user", gson.toJsonTree(userDTO));
        responseObject.addProperty("success", true);
        }

        
        resp.setContentType("appplication/json");
        resp.getWriter().write(gson.toJson(responseObject));
        
    }

}
