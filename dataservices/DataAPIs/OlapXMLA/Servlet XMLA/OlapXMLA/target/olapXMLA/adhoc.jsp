<%@page contentType="text/html"%>

<%
    final String nl = System.getProperty("line.separator");
    String[] queries = new String[] {
        // #0
        "SELECT { [DimDate.year-quarter-monthname_pt-day].[2015].[2015 - 2].Children, [DimDate.year-quarter-monthname_pt-day].[2015].[2015 - 3].Children } ON COLUMNS," + nl + 
        " { [DimPatient.name].Children } ON ROWS" + nl +
		" FROM ALSMon",

        // mdx sample #1
        "SELECT CrossJoin( [DimDate.year-monthname_pt-day].[2015].Children, [Measures].[area]) ON COLUMNS," + nl + 
        " { [DimPatient.name].Children } ON ROWS" + nl +
        " FROM ALSMon",
    };
%>

<html>
<head>
<style>
.{
font-family:"verdana";
}

.resulttable {
background-color:#AAAAAA;
}

.slicer {
background-color:#DDDDDD;
font-size:10pt;
}

.columnheading {
background-color:#DDDDDD;
font-size:10pt;
}

.rowheading {
background-color:#DDDDDD;
font-size:10pt;
}

.cell {
font-family:"courier";
background-color:#FFFFFF;
font-size:10pt;
text-align:right;
}

</style>

<title>JSP Page</title>
</head>
<body>

<a href=".">back to index</a><p/>

    <form action="adhoc.jsp" method="post">
    <table>
        <tr>
            <td>
                <select name="whichquery">
        <%

        for (int i=0; i<queries.length; i++) {

            %>
            <option
            <%

            if (request.getParameter("whichquery") != null) {
                if (Integer.valueOf(request.getParameter("whichquery")).intValue() == i) {
                    out.print(" selected");
                }
            }
            %>

            value="<% out.print(i);%>">Sample Query #<%out.print(i);

            %>

            </option>

        <%
        }
        %>

                </select>
            </td>
        </tr>

        <tr>
            <td>
                <input type="submit" value="show query">
            </td>
        </tr>
    </table>
    </form>

    <form action="mdxquery">
        <table>
        <tr>
        <td>
        <tr>
            <td>
                <textarea id='queryArea' name="queryString" rows=10 cols=80><%
            if (request.getParameter("whichquery") != null) {
                out.println(queries[Integer.valueOf(request.getParameter("whichquery")).intValue()]);
            }
            if (request.getParameter("queryString") != null) {
                out.println(request.getParameter("queryString"));
            }
        %></textarea>
            </td>
        </tr>
        <tr>
            <td>
                <input type="submit" value="process MDX query">
            </td>
        </tr>

        <% if (request.getAttribute("result") != null) { %>
        <tr>
            <td valign=top>Results:
            <% out.println(request.getAttribute("result")); %>
            </td>
        </tr>
        <% } %>

    </table>
    </form>

<a href=".">back to index</a><p/>

</body>
</html>
