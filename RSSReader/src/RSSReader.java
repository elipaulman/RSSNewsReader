import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;
import components.xmltree.XMLTree;
import components.xmltree.XMLTree1;

/**
 * Program to convert an XML RSS (version 2.0) feed from a given URL into the
 * corresponding HTML output file.
 *
 * @author Elijah Paulman
 *
 */
public final class RSSReader {

    /**
     * Private constructor so this utility class cannot be instantiated.
     */
    private RSSReader() {
    }

    /**
     * Outputs the "opening" tags in the generated HTML file. These are the
     * expected elements generated by this method:
     *
     * <html> <head> <title>the channel tag title as the page title/title>
     * /head> body>
     * <h1>the page title inside a link to the <channel> link</h1>
     * <p>
     * the channel description
     * </p>
     * <table border="1"/>
     * <tr>
     * <th>Date</th>
     * <th>Source</th>
     * <th>News</th>
     * </tr>
     *
     * @param channel
     *            the channel element XMLTree
     * @param out
     *            the output stream
     * @updates out.content
     * @requires [the root of channel is a <channel> tag] and out.is_open
     * @ensures out.content = #out.content * [the HTML "opening" tags]
     */
    private static void outputHeader(XMLTree channel, SimpleWriter out) {
        assert channel != null : "Violation of: channel is not null";
        assert out != null : "Violation of: out is not null";
        assert channel.isTag() && channel.label().equals("channel") : ""
                + "Violation of: the label root of channel is a <channel> tag";
        assert out.isOpen() : "Violation of: out.is_open";

        //opens html, head, and title
        out.println("<html>");
        out.println("<head>");
        out.println("<title>");
        //finds index of title
        int index = getChildElement(channel, "title");

        //checks for empty title or prints title from index above
        String title = channel.child(index).child(0).label();
        //checks if child (title) is present
        if (channel.child(index).numberOfChildren() == 0) {
            out.println("No Title Available");
        } else {
            title = channel.child(index).child(0).label();
            out.println(title);
        }
        //closes title and head from above
        out.println("</title>");
        out.println("</head>");

        //outputs title link
        out.println("<body>");
        //gets index of child element of channel named link
        index = getChildElement(channel, "link");
        //gets link label
        String link = channel.child(index).child(0).label();
        //prints link as clickable url with the article title as the name
        out.print("<h1><a href=\"");
        out.print(link);
        out.print("\">");
        out.print(title);
        out.println("</a></h1>");

        //outputs website description
        out.println("<p>");
        index = getChildElement(channel, "description");
        //checks if description is present
        if (channel.child(index).numberOfChildren() == 0) {
            out.println("No description");
        } else {
            //gets description label as a string and prints
            String description = channel.child(index).child(0).label();
            out.println(description);
        }
        out.println("</p>");

        //creates table row with headers date, source, and news
        //table border of 1
        out.println("<table border = \"1\">");
        out.println("<tr>");
        out.println("<th>Date</th>");
        out.println("<th>Source</th>");
        out.println("<th>News</th>");
        out.println("</tr>");

    }

    /**
     * Outputs the "closing" tags in the generated HTML file. These are the
     * expected elements generated by this method:
     *
     * /table> /body> /html>
     *
     * @param out
     *            the output stream
     * @updates out.contents
     * @requires out.is_open
     * @ensures out.content = #out.content * [the HTML "closing" tags]
     */
    private static void outputFooter(SimpleWriter out) {
        assert out != null : "Violation of: out is not null";
        assert out.isOpen() : "Violation of: out.is_open";

        //closes table, body, and html which have already been opened
        out.println("</table>");
        out.println("</body>");
        out.println("</html>");
    }

    /**
     * Finds the first occurrence of the given tag among the children of the
     * given {@code XMLTree} and return its index; returns -1 if not found.
     *
     * @param xml
     *            the {@code XMLTree} to search
     * @param tag
     *            the tag to look for
     * @return the index of the first child of type tag of the {@code XMLTree}
     *         or -1 if not found
     * @requires [the label of the root of xml is a tag]
     * @ensures <pre>
     * getChildElement =
     *  [the index of the first child of type tag of the {@code XMLTree} or
     *   -1 if not found]
     * </pre>
     */
    private static int getChildElement(XMLTree xml, String tag) {
        assert xml != null : "Violation of: xml is not null";
        assert tag != null : "Violation of: tag is not null";
        assert xml.isTag() : "Violation of: the label root of xml is a tag";

        //initializes index to -1
        int index = -1;
        //checks is xml has children
        if (xml.numberOfChildren() > 0) {
            //runs through every child of xml
            for (int i = 0; i < xml.numberOfChildren(); i++) {
                //checks if the label of xml child i is equal to the string tag
                if (xml.child(i).label().equals(tag)) {
                    //gets index of label of string tag
                    index = i;
                }
            }
        }
        //returns index of tag or -1 if not present
        return index;
    }

    /**
     * Processes one news item and outputs one table row. The row contains three
     * elements: the publication date, the source, and the title (or
     * description) of the item.
     *
     * @param item
     *            the news item
     * @param out
     *            the output stream
     * @updates out.content
     * @requires [the label of the root of item is an <item> tag] and
     *           out.is_open
     * @ensures <pre>
     * out.content = #out.content *
     *   [an HTML table row with publication date, source, and title of news item]
     * </pre>
     */
    private static void processItem(XMLTree item, SimpleWriter out) {
        assert item != null : "Violation of: item is not null";
        assert out != null : "Violation of: out is not null";
        assert item.isTag() && item.label().equals("item") : ""
                + "Violation of: the label root of item is an <item> tag";
        assert out.isOpen() : "Violation of: out.is_open";

        //creates new table row
        out.print("<tr>");

        //gets child element index of various elements or -1 if not available
        int date = getChildElement(item, "pubDate");
        int source = getChildElement(item, "source");
        int title = getChildElement(item, "title");
        int description = getChildElement(item, "description");
        int link = getChildElement(item, "link");

        //checks for date tag and prints output or no date
        if (date != -1) {
            //adds table data for date
            out.println("<td>");
            out.print(item.child(date).child(0).label());
            out.println("</td>");
        } else {
            //prints if no date available
            out.println("No date available");
        }

        //checks for source tag and prints output or no source
        if (source != -1) {
            //adds table data for article as a clickable link for the source
            out.println("<td>");
            out.print("<a href=\"");
            out.print(item.child(source).attributeValue("url"));
            out.println("\">" + item.child(source).child(0).label() + "</a>");
            out.println("</td>");
        } else {
            //adds table data which indicates no source is available if true
            out.println("<td>No source available</td>");
        }

        //checks for link tag and prints output
        out.println("<td>");
        if (link != -1) {
            //adds table data for link as a clickable url
            out.print("<a href=\"");
            out.print(item.child(link).child(0).label());
            out.print("\">");
        }

        /*
         * checks for title/description tag and prints output or no
         * title/description we know one or the other will be presents (or both)
         * must check all outcomes possible
         */
        if (title != -1) {
            //checking for empty title
            if (item.child(title).child(0).label().equals("")) {
                out.print("No title available");
            } else {
                //prints title of article
                out.print(item.child(title).child(0).label());
            }
        } else if (description != -1) {
            //checking for present or empty description label
            if (item.child(description).child(0).label().equals("")) {
                out.print("No description");
            } else {
                //prints description of article
                out.print(item.child(description).child(0).label());
            }
        }

        //closes link tag if present
        if (link != -1) {
            out.println("</a>");
        }

        //close row tag and table data tag
        out.println("</td>");
        out.println("</tr>");
    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments; unused here
     */
    public static void main(String[] args) {
        SimpleReader in = new SimpleReader1L();
        SimpleWriter out = new SimpleWriter1L();

        //prompts user for url of an rss 2.0 feed
        out.print("Enter the URL of an RSS feed: ");
        String url = in.nextLine();

        //creates xml tree from provided url
        XMLTree xml = new XMLTree1(url);

        //checks that provided link is an rss 2.0 link
        if (xml.label().equals("rss") && xml.hasAttribute("version")
                && xml.attributeValue("version").equals("2.0")) {
            //prompts user for output file name of html file
            out.print(
                    "Enter the name of the output file (including .html extension)");
            //creates file output stream using SimpleWriter
            String outFile = in.nextLine();
            SimpleWriter fileOut = new SimpleWriter1L(outFile);

            //creates channel xml tree
            XMLTree channel = xml.child(0);

            //calls outputHeader method above
            outputHeader(channel, fileOut);

            //calls processItem method for every item tag present
            //runs through every child of channel to check for tag and item label
            for (int i = 0; i < channel.numberOfChildren(); i++) {
                if (channel.child(i).isTag()) {
                    if (channel.child(i).label().equals("item")) {
                        XMLTree item = channel.child(i);
                        processItem(item, fileOut);
                    }
                }
            }
            //outputs footer using outputFooter method above
            outputFooter(fileOut);
            //closes file output stream
            fileOut.close();
            //prints if user does not provide valid rss 2.0 link
        } else {
            out.println("URL not valid RSS 2.0 file");
        }

        //closes input and output streams
        in.close();
        out.close();
    }

}