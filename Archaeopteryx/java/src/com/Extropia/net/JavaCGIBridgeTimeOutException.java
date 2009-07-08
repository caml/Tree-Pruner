/*
 * @(#)JavaCGIBridgeTimeOutException.java 1.00 08/10/97
 *
 * Copyright Info: This class was written by Gunther Birznieks
 * (birzniek@hlsun.redcross.org) and Selena Sol (selena@eff.org) 
 * having been inspired by countlessd other Perl authors.
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

/**
 * Signals that the JavaCGIBridge Timed out while attempting to POST
 * or GET data from a URL connection.
 *
 * @version     1.00, 10 Aug 1997
 */

public
class JavaCGIBridgeTimeOutException extends Exception {
    /**
     * Constructs a JavaCGIBridgeTimeOutException with no detail message.
     * A detail message is a String that describes this particular
     * exception.
     */
     public JavaCGIBridgeTimeOutException() {
         super();
     }

    /**
     * Constructs a JavaCGIBridgeTimeOutException with the specified
     * detail message. A detail message is a String that describes
     * this particular exception.
     * @param s the String that contains a detailed message
     */

     public JavaCGIBridgeTimeOutException(String s) {
         super(s);
     }
}

