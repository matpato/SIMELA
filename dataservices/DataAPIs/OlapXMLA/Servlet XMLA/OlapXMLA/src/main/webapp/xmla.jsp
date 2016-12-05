<%@ page import="mondrian.xmla.test.XmlaTestServletRequestWrapper"
         language="java"
         contentType="text/xml" %><%

    final ServletRequest requestWrapper = new XmlaTestServletRequestWrapper(request);
    final ServletContext servletContext = config.getServletContext();
    final RequestDispatcher dispatcher = servletContext.getNamedDispatcher("MondrianXmlaServlet");
    dispatcher.forward(requestWrapper, response);
%>