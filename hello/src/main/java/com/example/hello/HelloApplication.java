package com.example.hello;

import jakarta.servlet.GenericServlet;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.server.WebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.IOException;

//확인용
public class HelloApplication {
    public static void main(String[] args) {
        ServletWebServerFactory servletWebServerFactory = new TomcatServletWebServerFactory();

        final GenericWebApplicationContext applicationContext = new GenericWebApplicationContext();

        applicationContext.registerBean(Controller.class); // bean 등록
        applicationContext.refresh(); // 스프링 컨테이너 초기화, bean 오브젝트 생성

        final WebServer webServer = servletWebServerFactory.getWebServer(servletContext -> {
            servletContext.addServlet("dispatcherServlet",
                            new DispatcherServlet(applicationContext))
                    .addMapping("/*");
        });

        webServer.start();
    }
}
