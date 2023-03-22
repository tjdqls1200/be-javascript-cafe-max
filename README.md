
## 스프링

---

<br> 

1. 서블릿
2. 서블릿 컨테이너
3. 스프링 부트의 기능
4. 스프링 컨테이너


<br>

### 서블릿

---

서블릿은 자바로 구현된 CGI(Common Gateway Interface)라고도 하는데 CGI는 웹 서버와 웹 애플리케이션 서버간에 데이터를 주고 받기 위한 규약을 말합니다.  
  
[참고] Servlet과 CGI (https://www.geeksforgeeks.org/difference-between-java-servlet-and-cgi/)  
  
서블릿을 자바로 구현된 CGI라고도 하는 이유는 서블릿이 웹 서버와 웹 애플리케이션 서버간에 데이터를 주고 받는 역할을 해주기 때문입니다.  
  
간단하게 정리하면 서블릿은 웹 서버의 요청을 받아서 동적인 페이지를 생성하고 반환하는 과정을 편리하게 처리할 수 있도록 지원하는 자바 웹 프로그래밍 기술입니다.  
  
하지만 서블릿은 독립적으로 생성, 실행이 되지 않기 때문에 서블릿을 관리할 무엇인가가 필요한데 이를 서블릿 컨테이너라고 합니다.  

<br>

### 서블릿 컨테이너

---

서블릿 컨테이너는 서블릿를 관리하며 클라이언트와 서버 간의 소켓 통신에 필요한 TCP/IP 연결, HTTP 프로토콜 해석 등의 네트워크 기반 작업을 추상화해 API로 제공하는 등의 역할을 합니다.   
  
서블릿 인터페이스(javax.servlet.Servlet)를 보면 init(), destroy()가 존재하는데 서블릿 컨테이너는 서블릿의 생명 주기를 관리하는 역할을 하고 있습니다.   
  
서블릿 컨테이너는 서블릿 인터페이스를 구현한 GenericServlet 추상 클래스를 제공하는데 GenericServlet 내부를 보면 추상 메서드로 선언 된 service()가 있으며 
서블릿이 요청을 처리할 수 있도록 서블릿 컨테이너에서 service()를 호출한다고 설명이 되어 있습니다.  
  
서블릿 컨테이너는 요청을 전달 받아서 Request, Response 객체를 생성하고 서블릿을 생성(최초 1회)해서 service()를 호출하는데
서블릿 컨테이너는 멀티 스레드를 지원하기 때문에 요청이 올 때마다 스레드를 생성해서 service() 메서드를 실행하고 스레드를 종료시킵니다.  

```java
    @Override
    public abstract void service(ServletRequest req, ServletResponse res)
            throws ServletException, IOException;
```

HttpServlet은 GenericServlet을 상속 받아서 service()를 구현하였고 HTTP 프로토콜과 관련된 여러 기능을 제공합니다.  
  
HTTP 요청에 적합하게 구현한 서블릿 클래스로 service() 내부를 보면 GET,POST 등 요청에 따라 doGet(), doPost()등으로 처리하는 것을 확인할 수 있습니다.  


```java
    @Override
    public void service(ServletRequest req, ServletResponse res)
        throws ServletException, IOException {

        HttpServletRequest  request;
        HttpServletResponse response;

        try {
            request = (HttpServletRequest) req;
            response = (HttpServletResponse) res;
        } catch (ClassCastException e) {
            throw new ServletException(lStrings.getString("http.non_http")); // HTTP 관련 요청이 아니면 예외 처리
        }
        service(request, response);
    }
```


```java
    protected void service(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {

        String method = req.getMethod();

        if (method.equals(METHOD_GET)) {
            long lastModified = getLastModified(req);
            if (lastModified == -1) {
                // servlet doesn't support if-modified-since, no reason
                // to go through further expensive logic
                doGet(req, resp);
                
        //...
```

<br>

### 스프링 부트

---

<br>

스프링 부트를 사용하면 클래스에 @Controller 애노테이션을 선언하고 메서드에 @GetMapping, @PostMapping 같은 애노테이션을 선언해서 매핑을 하면 
쉽게 요청을 전달 받을 수 있습니다.  
내부적으로는 서블릿 컨테이너에서 요청을 받고 서블릿을 통해 컨트롤러에 매핑된 메서드를 찾아 전달을 하는 등의 처리를 하지만 
스프링 부트로 처음 개발을 하는 경우 이런 설정을 자동으로 해줘서 개념을 모르거나 봐도 이해가 안 되는 경우가 많습니다.  

스프링 부트가 이런 설정들을 많은 부분 지원해주어서 마치 서블릿 컨테이너가 없는 것처럼(Containerless) 핵심 기능에만 집중할 수 있도록 도와주기 때문입니다.   

<br>

---

<br>

![image](https://user-images.githubusercontent.com/57752068/226642658-c168093f-9535-4274-a1be-d9ab485a5359.png)

```java
        ServletWebServerFactory servletWebServerFactory = new TomcatServletWebServerFactory();

        WebServer webServer = servletWebServerFactory.getWebServer(servletContext -> {
            servletContext.addServlet("myServlet", new HttpServlet() {
                @Override
                protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                }
            }).addMapping("/hello");
        });
```


```java
        GenericWebApplicationContext applicationContext = new GenericWebApplicationContext();

        WebServer webServer = servletWebServerFactory.getWebServer(servletContext -> {
            servletContext.addServlet("dispatcherServlet",
                            new DispatcherServlet(applicationContext))
                    .addMapping("/*");
        });

        webServer.start();
```

