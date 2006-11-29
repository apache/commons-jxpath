/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.servlet.JXPathServletContexts;

/**
 * Invoke like this: http://localhost:8080/jxpath?parm=OK
 * 
 * @version $Revision$ $Date$
 */
public class JXPathServletContextsTestServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
    {
        ServletContext servletContext = getServletContext();
        servletContext.setAttribute("app", "OK");
        JXPathContext appContext = JXPathServletContexts
                .getApplicationContext(servletContext);
        
        request.setAttribute("attr", "OK");
        JXPathContext reqContext = JXPathServletContexts.getRequestContext(
                request,
                servletContext);
                
        HttpSession session = request.getSession();
        Integer count = (Integer) session.getAttribute("count");
        if (count == null) {
            count = new Integer(0);
        }
        else {
            count = new Integer(count.intValue() + 1);
        }
        session.setAttribute("count", count);
        
        JXPathContext sessionContext = JXPathServletContexts.getSessionContext(
                session,
                servletContext);
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head>");
        out.println("<title>JXPathServletContext</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>JXPathServletContexts Servlet Context Test</h1>");
        assertEqual(
                out,
                "Application Context",
                appContext.getValue("app"),
                "OK");
        assertEqual(
                out,
                "Request Context Attribute",
                reqContext.getValue("attr"),
                "OK");
        assertEqual(
                out,
                "Request Context Attribute",
                reqContext.getValue("attr"),
                "OK");
        
        if (request.getParameter("parm") == null) {
            out.println("<p><b>Invoke this test servlet like this: "
                    + "http://localhost:8080/jxpath-war/jxpath?parm=OK<b>");
        }
        else {
            assertEqual(
                    out,
                    "Request Context Parameter",
                    reqContext.getValue("parm"),
                    "OK");
        }
        assertEqual(
                out,
                "Session Context Parameter (reload for actual test)",
                sessionContext.getValue("count"),
                count);
        assertEqual(
                out,
                "Application Context via Request Context",
                reqContext.getValue("app"),
                "OK");
        assertEqual(
                out,
                "Session Context via Request Context",
                reqContext.getValue("count"),
                count);
        assertEqual(
                out,
                "Application Context via Session Context",
                sessionContext.getValue("app"),
                "OK");
        
        out.println("</body>");
        out.println("</html>");
    }

    private void assertEqual(
            PrintWriter out,
            String title,
            Object actual,
            Object expected) 
    {
        if ((actual == null && expected == null)
                || (actual != null && actual.equals(expected))) {
            out.println("<p>" + title + ": Ok");
        }
        else {
            out.println("<p><font color=red>" + title + ": Failure</font>");
        }
    }
}

