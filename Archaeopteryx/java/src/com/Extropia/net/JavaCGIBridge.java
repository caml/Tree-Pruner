/*
 * @(#)JavaCGIBridge.java 1.00 08/10/97
 *
 * Copyright Info: This class was written by Gunther Birznieks
 * (birzniek@hlsun.redcross.org) and Selena Sol (selena@eff.org)
 * having been inspired by countless other Perl authors.
 *
 * Feel free to copy, cite, reference, sample, borrow, resell
 * or plagiarize the contents.  However, if you don't mind,
 * please let Selena know where it goes so that we can at least
 * watch and take part in the development of the memes. Information
 * wants to be free; support public domain freeware.  Donations are
 * appreciated and will be spent on further upgrades and other public
 * domain programs.
 *
 */

package com.Extropia.net;

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Vector;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.MalformedURLException;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * This is a class that POSTS and GETS data from URLs using various
 * helper methods.  This class also provides the capability of
 * timing out if the connection takes too long to transfer data
 * by using threads to monitor whether the data is taking too long
 * to transfer (implements Runnable).
 * <P>
 * Helper methods allow you to set up form variables, get setup
 * information over a URL, get raw or pre-parsed HTML data, and more.
 * <P>
 * The getParsedData method relies on instance variables which
 * tell the parser where the data begins and ends (top and bottom
 * separators respectively).   The field and row separators inform
 * the parser where records and fields end.
 * <P><BLOCKQUOTE>
 * The default top separator is &LT!--start of data-->
 * <P>
 * The default bottom separator is &LT!--end of data-->
 * <P>
 * The default field separator is ~|~ (tilde + pipe + tilde).
 * <P>
 * The default row separator is ~|~\n (tilde + pipe + tilde + newline).
 * </BLOCKQUOTE>
 * @version     1.00, 10 Aug 1997
 * @author      Gunther Birznieks
 */
public class JavaCGIBridge implements Runnable {

    /**
     * The field separator.  When a CGI script or HTML file
     * returns data, then the getParsedData() method will
     * know how to separate fields by looking at this variable.
     *
     * The default value is "~|~" (tilde + pipe + tilde). This is
     * considered sufficiently unlikely to appear inside of actual
     * field data that it is a pretty good field separator to parse
     * on. If the user of this class needs a different separator, it
     * may be overridden by calling the appropriate method.
     */
    private String fieldSeparator = "~|~";

    /**
     * The row separator.  When a CGI script or HTML file
     * returns data, then the getParsedData() method will
     * know how to separate returned rows by looking at this variable.
     *
     * The default value is "~|~\n" (tilde + pipe + tilde + newline). 
     * This is considered sufficiently unlikely to appear inside of
     * actual row data that it is a pretty good row separator to parse
     * on. If the user of this class needs a different separator, it
     * may be overridden by calling the appropriate method.
     */
    private String rowSeparator = "~|~\n";

    /**
     * The top of data separator.  When a CGI script or HTML file
     * returns data, then the getParsedData() method will
     * determine when to start parsing data by encountering this
     * separator.
     *
     * The default value is "<!--start of data-->" implicitly followed
     * by a newline.  That is, a newline is generally expected to follow
     * this separator.
     */
    private String topSeparator = "<!--start of data-->";

    /**
     * The bottom of data separator.  When a CGI script or HTML file
     * returns data, then the getParsedData() method will
     * determine when to stop parsing data by encountering this
     * separator.
     *
     * The default value is "<!--end of data-->" implicitly followed
     * by a newline.  That is, a newline is generally expected to follow
     * this separator.
     */
    private String bottomSeparator = "<!--end of data-->";

    /**
     * The URL That data is retrieved from.  This is an instance
     * variable rather than a parameter because the data retrieval
     * is done from a launched thread which gets no parameters.
     */
    private URL threadURL = null;

    /**
     * The HTML form data for the URL that data is retrieved from.  
     * This is an instance variable rather than a parameter because
     * the data retrieval is done from a launched thread which gets
     * no parameters.
     */
    private Hashtable threadFormVar = null;

    /**
     * This is a flag indicating whether the URL data was
     * retrieved inside the thread or not.
     */
    private boolean threadCompleted = false;

    /**
     * This is the returned URL data. It is an instance variable because
     * the run() method of the thread cannot explicitly return data.
     */
    private String threadCGIData = null;

    /**
     * This is the default CGI Timeout in milliseconds.  For example,
     * a value of 10000 will tell the class to throw a
     * JavaCGIBridgeTimeOutException if the data is not retrieved within
     * 10 seconds of having initiated a data transfer.
     */
    private static int defaultThreadJavaCGIBridgeTimeOut = 10000;

    /**
     * This is the actual CGI Timeout value in milliseconds for the
     * instantiated object. Notice that the default value is a static
     * class variable that applies across all instances of this class.
     * This variable, on the other hand, is the actual value the object
     * uses.
     */
    private int threadJavaCGIBridgeTimeOut = defaultThreadJavaCGIBridgeTimeOut;

    /** 
     * This is a flag that makes it so that the run() method is
     * at least semi-private for Threading.  This flag has to be
     * set true in order for another object to execute the run() method.
     */
    private boolean threadCanRunMethodExecute = false;
 
    /**
     * Constructs the object with new separator values
     */
    public JavaCGIBridge(String field, String row, String top, String bottom) {
        fieldSeparator = new String(field);
        rowSeparator = new String(row);
        topSeparator = new String(top);
        bottomSeparator = new String(bottom);
    }

    /**
     * Constructs the object with no initialization. This is the default
     * (empty) constructor.
     */
    public JavaCGIBridge() {
    } // Empty constructor

    /**
     * Adds a form variable, value pair to the passed Hashtable.
     *
     * @param ht the Hashtable that contains the form variable/value pairs
     * @param formKey the String that contains the form variable to add
     * @param formValue the String that contains the form value to add
     */
    public void addFormValue(Hashtable ht, String formKey, String formValue) {
        Vector vValues = null;

        if (formValue != null && formValue.length() > 0) {
            if (ht.containsKey(formKey)) {
                vValues = (Vector)ht.get(formKey);
            } else {
                vValues = new Vector();
            }
            vValues.addElement(formValue);
            ht.put(formKey, vValues);
        }
    } // End of addFormValue

    /** 
     * Takes the parsed data returned from the getParsedData method
     * and changes it to a Hashtable of key, value pairs where the first
     * Vector entry of each Vector record is the key and the rest of the
     * second Vector entry for each record is the value of the Hashtable.
     *
     * @param vectorOfVectors the Vector of Vectors for the parsed data
     * @return Hashtable containing converted variable/value pairs
     * @see #getParsedData
     */ 
    public Hashtable getKeyValuePairs(Vector vectorOfVectors) {
        Hashtable h = new Hashtable();
        Vector v = null;

        for (Enumeration e = vectorOfVectors.elements(); e.hasMoreElements();) {
            v = (Vector)e.nextElement();
            h.put((String)v.elementAt(0), (String)v.elementAt(1));
        }
        return h;
    } // End of getKeyValuePairs

    /**
     * Returns parsed data in the form of a Vector of Vectors containing
     * the returned fields inside of a Vector of returned rows.
     *
     * @param u URL to get parsed data from.
     * @return Vector (records) of vectors (fields) of parsed data
     * @exception JavaCGIBridgeTimeOutException If the retrieval times out
     * @see #getRawCGIData
     */
    public Vector getParsedData(URL u) throws JavaCGIBridgeTimeOutException {
        return (getParsedData(u, null));
    }

    /**
     * Returns parsed data in the form of a Vector of Vectors containing
     * the returned fields inside of a Vector of returned rows. This form
     * POSTs the HTML Form variable data to the URL.
     *
     * @param u URL to get parsed data from.
     * @param ht Hashtable contains form variables to POST
     * @return Vector (records) of vectors (fields) of parsed data
     * @exception JavaCGIBridgeTimeOutException If the retrieval times out
     * @see #getRawCGIData
     */
    public Vector getParsedData(URL u, Hashtable ht) 
      throws JavaCGIBridgeTimeOutException {

        // getParsedData calls getRawCGIData to obtain
        // the actual HTML text
        String data = getRawCGIData(u,ht);
        int topIndex, bottomIndex;
        Vector v = new Vector(); // parsed data is returned
                                // as a series of Vectors of Vectors

        // topIndex and bottomIndex delimit the top and bottom
        // of data in the return HTML from the URL
        topIndex = data.indexOf(topSeparator);
        bottomIndex = data.indexOf(bottomSeparator);

        // Of course, there must be data between
        // the topSeparator and bottomSeparator in order
        // for us to build a vector...
        if (topIndex == -1 ||
            bottomIndex == -1 ||
            bottomIndex < topIndex ||
            topIndex + topSeparator.length() > bottomIndex) {
            return v;
        }

        // Now that we have the top and bottom, we can clip the data

        // Make sure that the whole top of data separator line is
        // is clipped off using index of newline.
        //
        data = data.substring(data.indexOf("\n", topIndex) + 1
                                , bottomIndex);

        while (data.length() > 0) {
            int rowIndex = data.indexOf(rowSeparator);
            String rowData = null;
            if (rowIndex == -1) {
                rowData = data;
                data = "";
            } else {
                rowData = data.substring(0,rowIndex);
                data = data.substring(rowIndex + rowSeparator.length());
            }
            if (rowData.length() > 0) {
                v.addElement(new Vector());
                while (rowData.length() > 0) {
                    int fieldIndex = rowData.indexOf(fieldSeparator);
                    String fieldData = null;
                    if (fieldIndex == -1) {
                        fieldData = rowData;
                        rowData = "";
                    } else {
                        fieldData = rowData.substring(0,fieldIndex);
                        rowData = rowData.substring(fieldIndex + fieldSeparator.length());
                    }
                    //if (fieldData.length() > 0)
                    ((Vector)v.lastElement()).addElement(fieldData);
                }
            }
        }

        return v;
    }

    /**
     * Returns raw HTML data as a String from the passed URL.
     *
     * @param u URL to get raw HTML from.
     * @return String containing plain HTML text
     * @exception JavaCGIBridgeTimeOutException If the retrieval times out
     * @see #getParsedData
     */
    public String getRawCGIData(URL u) throws JavaCGIBridgeTimeOutException {
        return (getRawCGIData(u,null));
    }

    /**
     * Returns raw HTML data as a String from the passed URL and list
     * of Form variable/value pairs stored in a Hashtable.  This form
     * POSTs the HTML Form variable data to the URL.
     *
     * @param u URL to get raw HTML from.
     * @param ht Hashtable contains form variables to POST
     * @return String containing plain HTML text
     * @exception JavaCGIBridgeTimeOutException If the retrieval times out
     * @see #getParsedData
     */
    public String getRawCGIData(URL u, Hashtable ht)
      throws JavaCGIBridgeTimeOutException 
    {
        // We set up the information for passing to the thread
        // ahead of time.
        threadURL = u;
        threadFormVar = ht;
        
        // Create a new thread and flag the fact that the thread
        // did not get us any data yet. Then start the thread.
        Thread t = new Thread(this);
        threadCompleted = false;
        threadCanRunMethodExecute = true;
        t.start();

        // We calculate the current time that the thread started.
        // The delay to wait will be calculated below.  
        long base = System.currentTimeMillis();
        long delay = 0;
        synchronized(this) {
            try {
                while(t.isAlive() && threadCompleted == false) {
                    delay = threadJavaCGIBridgeTimeOut - 
                        (System.currentTimeMillis() - base);
                    if (delay <= 0)	break;
                    wait(delay);
                } // End of while
            } catch (InterruptedException e) { /* nothing */  }
        } // end of synchronized block
        //t.stop(); // No effect if already done //CHECK later
                  // but stops if timed out...

        if (threadCompleted == false) {
            throw new JavaCGIBridgeTimeOutException();
        }
        return threadCGIData;
    }

    /**
     * Static method returns the default communication time out 
     * in milliseconds for the class.
     *
     * When the object retrieves data from a URL, it must get
     * the data within timeout milliseconds or a 
     * JavaCGIBridgeTimeOutException is thrown.
     *
     * @return default communication time out in milliseconds
     * @see #setDefaultThreadJavaCGIBridgeTimeOut
     */
    public static int getDefaultThreadJavaCGIBridgeTimeOut() { 
        return defaultThreadJavaCGIBridgeTimeOut; 
    }

    /**
     * Static method sets the default communication time out in 
     * milliseconds for the class.
     *
     * When the object retrieves data from a URL, it must get
     * the data within timeout milliseconds or a 
     * JavaCGIBridgeTimeOutException is thrown.
     *
     * @param t default communication time out in milliseconds
     * @see #getDefaultThreadJavaCGIBridgeTimeOut
     */
    public static void setDefaultThreadJavaCGIBridgeTimeOut(int t) {
        defaultThreadJavaCGIBridgeTimeOut = t;
    }

    /**
     * Returns the actual communication time out in milliseconds
     * for the object.
     *
     * When the object retrieves data from a URL, it must get
     * the data within timeout milliseconds or a
     * JavaCGIBridgeTimeOutException is thrown.
     * 
     * @return communication time out in milliseconds
     * @see #setThreadJavaCGIBridgeTimeOut
     * @see #getDefaultThreadJavaCGIBridgeTimeOut
     * @see #setDefaultThreadJavaCGIBridgeTimeOut
     */
    public int getThreadJavaCGIBridgeTimeOut() { 
        return threadJavaCGIBridgeTimeOut; 
    }

    /**
     * Sets the actual communication time out in milliseconds
     * for the object.
     *
     * When the object retrieves data from a URL, it must get
     * the data within timeout milliseconds or a
     * JavaCGIBridgeTimeOutException is thrown.
     *
     * @param t communication time out in milliseconds
     * @see #getThreadJavaCGIBridgeTimeOut
     * @see #getDefaultThreadJavaCGIBridgeTimeOut
     * @see #setDefaultThreadJavaCGIBridgeTimeOut
     */
    public void setThreadJavaCGIBridgeTimeOut(int t) { 
        threadJavaCGIBridgeTimeOut = t; 
    }

    /**
     * Sets the field separator for the object.  When getParsedData
     * method is called, the object uses the field separator to determine 
     * where fields in a returned record of the raw HTML result set
     * begin and end.
     *
     * @param s String containing new delimiting separator
     * @see #getParsedData
     * @see #getFieldSeparator
     */
    public void setFieldSeparator(String s) { fieldSeparator = s; }

    /**
     * Returns the field separator for the object.  When getParsedData
     * method is called, the object uses the field separator to determine 
     * where fields in a returned record of the raw HTML result set
     * begin and end.
     *
     * @return Separator string
     * @see #getParsedData
     * @see #setFieldSeparator
     */
    public String getFieldSeparator() { return new String(fieldSeparator); }

    /**
     * Sets the row separator for the object.  When getParsedData
     * method is called, the object uses the row separator to determine 
     * where rows/records of the raw HTML result set
     * begin and end.
     *
     * @param s String containing new delimiting separator
     * @see #getParsedData
     * @see #getRowSeparator
     */
    public void setRowSeparator(String s) { rowSeparator = s; }

    /**
     * Returns the row separator for the object.  When getParsedData
     * method is called, the object uses the row separator to determine 
     * where rows/records of the raw HTML result set
     * begin and end.
     *
     * @return Separator string
     * @see #getParsedData
     * @see #setRowSeparator
     */
    public String getRowSeparator() { return new String(rowSeparator); }

    /**
     * Sets the top separator for the object.  When getParsedData
     * method is called, the object uses the top separator to determine 
     * where the rows of data inside the raw HTML output actually begin.
     *
     * @param s String containing new delimiting separator
     * @see #getParsedData
     * @see #getTopSeparator
     */
    public void setTopSeparator(String s) { topSeparator = s; }

    /**
     * Returns the top separator for the object.  When getParsedData
     * method is called, the object uses the top separator to determine
     * where the rows of data inside the raw HTML output actually begin.
     *
     * @return Separator string
     * @see #getParsedData
     * @see #setTopSeparator
     */
    public String getTopSeparator() { return new String(topSeparator); }

    /**
     * Sets the bottom separator for the object.  When getParsedData
     * method is called, the object uses the bottom separator to determine
     * where the rows of data inside the raw HTML output actually end.
     *
     * @param s String containing new delimiting separator
     * @see #getParsedData
     * @see #getBottomSeparator
     */
    public void setBottomSeparator(String s) { bottomSeparator = s; }

    /**
     * Returns the bottom separator for the object.  When getParsedData
     * method is called, the object uses the bottom separator to determine
     * where the rows of data inside the raw HTML output actually end.   
     *
     * @return Separator string
     * @see #getParsedData
     * @see #setBottomSeparator
     */
    public String getBottomSeparator() { return new String(bottomSeparator); }

    /**
     * This run thread asynchronously POSTs and GETs data from
     * a URL and places the contents into the threadCGIData variable.
     *
     * Since Threads do not return or pass parameter values, instance
     * variables in this object are used to maintain state.  This method
     * is only public so that the Thread class can launch the thread.
     *
     * @see #getRawCGIData
     */
    public void run() {
        if (threadCanRunMethodExecute) {
            threadCGIData = getHttpRequestInThread(
                            threadURL, threadFormVar);
            threadCompleted = true; // set completed flag
            synchronized(this) { notifyAll(); } 
              // ends the wait() for thread to end in getRawCGIData
        }
    } // end of run -- The thread runs

    /** 
     * Returns the form variables inside the hashtable as a 
     * URL encoded string of parameters for a CGI based program
     * to process.
     *
     * @param ht Hashtable containing form variables
     * @return URLencoded string of HTML form variables & values
     * @see #addFormValue
     */
    private String getURLEncodedHashTable(Hashtable ht) {
        StringBuffer encodedString = null;
        Vector vFormValues = null;

        // First, we enumerate through the keys (Form variables)
        for (Enumeration eKeys = ht.keys() ; eKeys.hasMoreElements() ;) {
            String formVariable = (String)eKeys.nextElement();

            vFormValues = (Vector)ht.get(formVariable);
            // Now, we have to enumerate through the values for the form variables.
            //
            // NOTE: It IS entirely possible that a form variable has MANY values
            // to simulate events such as multiple checkboxes or multiple select <SELECT>
            // form elements in an HTML form.
            for (Enumeration eVals = vFormValues.elements() ; eVals.hasMoreElements() ;) {
                String formValue = (String)eVals.nextElement();

                if (encodedString != null) {
                    encodedString.append("&");
                } else {
                    encodedString = new StringBuffer();
                }

                encodedString.append(URLEncoder.encode(formVariable) + "=" +
                    URLEncoder.encode(formValue));
            }
        }
        return encodedString.toString();
    } // End of getURLEncodedHashTable

    /**
     * Returns the HTTP Request data to the thread that
     * was launched to GET/POST data for a URL.  This is
     * a private method.
     *
     * @param u URL to retrieve and post data for
     * @param ht Form variables to send to URL
     * @return String containing retrieved HTML text
     * @see #run
     */
    private String getHttpRequestInThread(URL u, Hashtable ht) {
        String postContent = null;
        StringBuffer strBuffer = new StringBuffer();


        if (ht != null) {
            postContent = getURLEncodedHashTable(ht);
        }
        System.out.println("POST CONTENT " + postContent);
        try {
            URLConnection       urlConn;
            DataOutputStream    dos;
            DataInputStream     dis;


            // Establish the URL connection
            urlConn = u.openConnection();

            // We always want some input from the CGI script
            // in general even if it is just a success story
            urlConn.setDoInput (true);

            // If ht was not null, then we know there is
            // data to be posted.
            if (ht != null) {
                urlConn.setDoOutput(true);
            }

            // Turn off caching as this may screw with
            // our dynamic programs

            urlConn.setUseCaches (false);
            // Specify the content type
            urlConn.setRequestProperty
               ("Content-type", "application/x-www-form-urlencoded");

            // Send the POST data if we are writing
            if (ht != null) {
                dos = new DataOutputStream(urlConn.getOutputStream());
                dos.writeBytes(postContent);
                dos.flush();
                dos.close();
            }

            dis = new DataInputStream (urlConn.getInputStream());

            String str;
            while (null != ((str = dis.readLine())))
            {
                // Do troubleshooting here such as System.println
                strBuffer.append(str + "\n");
            }

            dis.close ();

        }   catch (MalformedURLException me)
            {
                System.err.println("MalformedURLException: " + me);
            }
            catch (IOException ioe)
            {
                System.err.println("IOException: " + ioe.getMessage());
            }

            return strBuffer.toString();

    } // end of getHttpRequestInThread


} // End of JavaCGIBridge class
