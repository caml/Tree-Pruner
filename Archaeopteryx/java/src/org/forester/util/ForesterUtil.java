// $Id: ForesterUtil.java,v 1.94 2009/04/28 01:59:46 cmzmasek Exp $
// FORESTER -- software libraries and applications
// for evolutionary biology research and applications.
//
// Copyright (C) 2008-2009 Christian M. Zmasek
// Copyright (C) 2008-2009 Burnham Institute for Medical Research
// All rights reserved
// 
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
//
// Contact: cmzmasek@yahoo.com
// WWW: www.phylosoft.org/forester

package org.forester.util;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.forester.io.parsers.PhylogenyParser;
import org.forester.io.parsers.nexus.NexusPhylogeniesParser;
import org.forester.io.parsers.nhx.NHXParser;
import org.forester.io.parsers.phyloxml.PhyloXmlParser;
import org.forester.io.parsers.tol.TolParser;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyMethods;
import org.forester.phylogeny.PhylogenyNode;
import org.forester.phylogeny.data.Confidence;
import org.forester.phylogeny.data.Sequence;
import org.forester.phylogeny.data.Taxonomy;
import org.forester.phylogeny.iterators.PhylogenyNodeIterator;

public final class ForesterUtil {

    public final static String FILE_SEPARATOR = System.getProperty( "file.separator" );
    public final static String LINE_SEPARATOR = System.getProperty( "line.separator" );
    public final static String JAVA_VENDOR    = System.getProperty( "java.vendor" );
    public final static String JAVA_VERSION   = System.getProperty( "java.version" );
    public final static String OS_ARCH        = System.getProperty( "os.arch" );
    public final static String OS_NAME        = System.getProperty( "os.name" );
    public final static String OS_VERSION     = System.getProperty( "os.version" );
    public final static double ZERO_DIFF      = 1.0E-9;

    private ForesterUtil() {
    }

    public static void appendSeparatorIfNotEmpty( final StringBuffer sb, final char separator ) {
        if ( sb.length() > 0 ) {
            sb.append( separator );
        }
    }

    /**
     * This calculates a color. If value is equal to min the returned color is
     * minColor, if value is equal to max the returned color is maxColor,
     * otherwise a color 'proportional' to value is returned.
     * 
     * @param value
     *            the value of this well
     * @param min
     *            the smallest value of the plate/screen
     * @param max
     *            the largest value of the plate/screen
     * @param minColor
     *            the color for min
     * @param maxColor
     *            the color for max
     * @return a Color
     */
    public static Color calcColor( double value,
                                   final double min,
                                   final double max,
                                   final Color minColor,
                                   final Color maxColor ) {
        if ( value < min ) {
            value = min;
        }
        if ( value > max ) {
            value = max;
        }
        final double x = ForesterUtil.calculateColorFactor( value, max, min );
        final int red = ForesterUtil.calculateColorComponent( minColor.getRed(), maxColor.getRed(), x );
        final int green = ForesterUtil.calculateColorComponent( minColor.getGreen(), maxColor.getGreen(), x );
        final int blue = ForesterUtil.calculateColorComponent( minColor.getBlue(), maxColor.getBlue(), x );
        return new Color( red, green, blue );
    }

    /**
     * This calculates a color. If value is equal to min the returned color is
     * minColor, if value is equal to max the returned color is maxColor, if
     * value is equal to mean the returned color is meanColor, otherwise a color
     * 'proportional' to value is returned -- either between min-mean or
     * mean-max
     * 
     * @param value
     *            the value of this well
     * @param min
     *            the smallest value of the plate/screen
     * @param max
     *            the largest value of the plate/screen
     * @param mean
     *            the mean/median value of the plate/screen
     * @param minColor
     *            the color for min
     * @param maxColor
     *            the color for max
     * @param meanColor
     *            the color for mean
     * @return a Color
     */
    public static Color calcColor( double value,
                                   final double min,
                                   final double max,
                                   final double mean,
                                   final Color minColor,
                                   final Color maxColor,
                                   final Color meanColor ) {
        if ( value < min ) {
            value = min;
        }
        if ( value > max ) {
            value = max;
        }
        if ( value < mean ) {
            final double x = ForesterUtil.calculateColorFactor( value, mean, min );
            final int red = ForesterUtil.calculateColorComponent( minColor.getRed(), meanColor.getRed(), x );
            final int green = ForesterUtil.calculateColorComponent( minColor.getGreen(), meanColor.getGreen(), x );
            final int blue = ForesterUtil.calculateColorComponent( minColor.getBlue(), meanColor.getBlue(), x );
            return new Color( red, green, blue );
        }
        else if ( value > mean ) {
            final double x = ForesterUtil.calculateColorFactor( value, max, mean );
            final int red = ForesterUtil.calculateColorComponent( meanColor.getRed(), maxColor.getRed(), x );
            final int green = ForesterUtil.calculateColorComponent( meanColor.getGreen(), maxColor.getGreen(), x );
            final int blue = ForesterUtil.calculateColorComponent( meanColor.getBlue(), maxColor.getBlue(), x );
            return new Color( red, green, blue );
        }
        else {
            return meanColor;
        }
    }

    public static String collapseWhiteSpace( final String s ) {
        return s.replaceAll( "[\\s]+", " " );
    }

    public static String colorToHex( final Color color ) {
        final String rgb = Integer.toHexString( color.getRGB() );
        return rgb.substring( 2, rgb.length() );
    }

    public static int countChars( final String str, final char c ) {
        int count = 0;
        for( int i = 0; i < str.length(); ++i ) {
            if ( str.charAt( i ) == c ) {
                ++count;
            }
        }
        return count;
    }

    public static BufferedWriter createBufferedWriter( final File file ) throws IOException {
        if ( file.exists() ) {
            throw new IOException( "[" + file + "] already exists" );
        }
        return new BufferedWriter( new FileWriter( file ) );
    }

    public static BufferedWriter createBufferedWriter( final String name ) throws IOException {
        return new BufferedWriter( new FileWriter( createFileForWriting( name ) ) );
    }

    public static File createFileForWriting( final String name ) throws IOException {
        final File file = new File( name );
        if ( file.exists() ) {
            throw new IOException( "[" + name + "] already exists" );
        }
        return file;
    }

    public static PhylogenyParser createParserDependingFileContents( final File file ) throws FileNotFoundException,
            IOException {
        PhylogenyParser parser = null;
        final String first_line = ForesterUtil.getFirstLine( file ).trim().toLowerCase();
        if ( first_line.startsWith( "<" ) ) {
            parser = new PhyloXmlParser();
        }
        else if ( ( first_line.startsWith( "nexus" ) ) || ( first_line.startsWith( "#nexus" ) )
                || ( first_line.startsWith( "# nexus" ) ) || ( first_line.startsWith( "begin" ) ) ) {
            parser = new NexusPhylogeniesParser();
        }
        else {
            parser = new NHXParser();
        }
        return parser;
    }

    public static PhylogenyParser createParserDependingOnFileType( final File file ) throws FileNotFoundException,
            IOException {
        PhylogenyParser parser = null;
        parser = createParserDependingOnSuffix( file.getName() );
        if ( parser == null ) {
            parser = createParserDependingFileContents( file );
        }
        return parser;
    }

    /**
     * Return null if it can not guess the parser to use based on name suffix.
     * 
     * @param filename
     * @return
     */
    public static PhylogenyParser createParserDependingOnSuffix( final String filename ) {
        PhylogenyParser parser = null;
        final String filename_lc = filename.toLowerCase();
        if ( filename_lc.endsWith( ".tol" ) || filename_lc.endsWith( ".tolxml" ) || filename_lc.endsWith( ".tol.zip" ) ) {
            parser = new TolParser();
        }
        else if ( filename_lc.endsWith( ".xml" ) || filename_lc.endsWith( ".px" ) || filename_lc.endsWith( "phyloxml" )
                || filename_lc.endsWith( ".zip" ) ) {
            parser = new PhyloXmlParser();
        }
        else if ( filename_lc.endsWith( ".nexus" ) || filename_lc.endsWith( ".nex" ) || filename_lc.endsWith( ".nx" ) ) {
            parser = new NexusPhylogeniesParser();
        }
        else if ( filename_lc.endsWith( ".nhx" ) || filename_lc.endsWith( ".nh" ) || filename_lc.endsWith( ".newick" ) ) {
            parser = new NHXParser();
        }
        return parser;
    }

    public static PhylogenyParser createParserDependingOnUrlContents( final URL url ) throws FileNotFoundException,
            IOException {
        final String lc_filename = url.getFile().toString().toLowerCase();
        PhylogenyParser parser = createParserDependingOnSuffix( lc_filename );
        if ( ( parser != null ) && lc_filename.endsWith( ".zip" ) ) {
            if ( parser instanceof PhyloXmlParser ) {
                ( ( PhyloXmlParser ) parser ).setZippedInputstream( true );
            }
            else if ( parser instanceof TolParser ) {
                ( ( TolParser ) parser ).setZippedInputstream( true );
            }
        }
        if ( parser == null ) {
            final String first_line = getFirstLine( url ).trim().toLowerCase();
            if ( first_line.startsWith( "<" ) ) {
                parser = new PhyloXmlParser();
            }
            else if ( ( first_line.startsWith( "nexus" ) ) || ( first_line.startsWith( "#nexus" ) )
                    || ( first_line.startsWith( "# nexus" ) ) || ( first_line.startsWith( "begin" ) ) ) {
                parser = new NexusPhylogeniesParser();
            }
            else {
                parser = new NHXParser();
            }
        }
        return parser;
    }

    public static void fatalError( final String prg_name, final String message ) {
        System.err.println();
        System.err.println( "[" + prg_name + "] > " + message );
        System.err.println();
        System.exit( -1 );
    }

    public static String[] file2array( final File file ) throws IOException {
        final List<String> list = file2list( file );
        final String[] ary = new String[ list.size() ];
        int i = 0;
        for( final String s : list ) {
            ary[ i++ ] = s;
        }
        return ary;
    }

    public static List<String> file2list( final File file ) throws IOException {
        final List<String> list = new ArrayList<String>();
        final BufferedReader in = new BufferedReader( new FileReader( file ) );
        String str;
        while ( ( str = in.readLine() ) != null ) {
            str = str.trim();
            if ( ( str.length() > 0 ) && !str.startsWith( "#" ) ) {
                for( final String s : splitString( str ) ) {
                    list.add( s );
                }
            }
        }
        in.close();
        return list;
    }

    public static SortedSet<String> file2set( final File file ) throws IOException {
        final SortedSet<String> set = new TreeSet<String>();
        final BufferedReader in = new BufferedReader( new FileReader( file ) );
        String str;
        while ( ( str = in.readLine() ) != null ) {
            str = str.trim();
            if ( ( str.length() > 0 ) && !str.startsWith( "#" ) ) {
                for( final String s : splitString( str ) ) {
                    set.add( s );
                }
            }
        }
        in.close();
        return set;
    }

    public static String getCurrentDateTime() {
        final DateFormat format = new SimpleDateFormat( "yyyy/MM/dd HH:mm:ss" );
        return format.format( new Date() );
    }

    public static String getFileSeparator() {
        return ForesterUtil.FILE_SEPARATOR;
    }

    public static String getFirstLine( final Object source ) throws FileNotFoundException, IOException {
        BufferedReader reader = null;
        if ( source instanceof File ) {
            final File f = ( File ) source;
            if ( !f.exists() ) {
                throw new IOException( "[" + f.getAbsolutePath() + "] does not exist" );
            }
            else if ( !f.isFile() ) {
                throw new IOException( "[" + f.getAbsolutePath() + "] is not a file" );
            }
            else if ( !f.canRead() ) {
                throw new IOException( "[" + f.getAbsolutePath() + "] is not a readable" );
            }
            reader = new BufferedReader( new FileReader( f ) );
        }
        else if ( source instanceof InputStream ) {
            reader = new BufferedReader( new InputStreamReader( ( InputStream ) source ) );
        }
        else if ( source instanceof String ) {
            reader = new BufferedReader( new StringReader( ( String ) source ) );
        }
        else if ( source instanceof StringBuffer ) {
            reader = new BufferedReader( new StringReader( source.toString() ) );
        }
        else if ( source instanceof URL ) {
            reader = new BufferedReader( new InputStreamReader( ( ( URL ) source ).openStream() ) );
        }
        else {
            throw new IllegalArgumentException( "dont know how to read [" + source.getClass() + "]" );
        }
        String line;
        while ( ( line = reader.readLine() ) != null ) {
            line = line.trim();
            if ( !ForesterUtil.isEmpty( line ) ) {
                if ( reader != null ) {
                    reader.close();
                }
                return line;
            }
        }
        if ( reader != null ) {
            reader.close();
        }
        return line;
    }

    public static String getLineSeparator() {
        return ForesterUtil.LINE_SEPARATOR;
    }

    /**
     * Returns all custom data tag names of this Phylogeny as Hashtable. Tag
     * names are keys, values are Boolean set to false.
     */
    public static Hashtable<String, Boolean> getPropertyRefs( final Phylogeny phylogeny ) {
        final Hashtable<String, Boolean> ht = new Hashtable<String, Boolean>();
        if ( phylogeny.isEmpty() ) {
            return ht;
        }
        for( final PhylogenyNodeIterator iter = phylogeny.iteratorPreorder(); iter.hasNext(); ) {
            final PhylogenyNode current_node = iter.next();
            if ( current_node.getNodeData().isHasProperties() ) {
                final String[] tags = current_node.getNodeData().getProperties().getPropertyRefs();
                for( int i = 0; i < tags.length; ++i ) {
                    ht.put( tags[ i ], new Boolean( false ) );
                }
            }
        }
        return ht;
    }

    public static void increaseCountingMap( final Map<String, Integer> counting_map, final String item_name ) {
        if ( !counting_map.containsKey( item_name ) ) {
            counting_map.put( item_name, 1 );
        }
        else {
            counting_map.put( item_name, counting_map.get( item_name ) + 1 );
        }
    }

    static public boolean isAllNonEmptyInternalLabelsArePositiveNumbers( final Phylogeny phy ) {
        final PhylogenyNodeIterator it = phy.iteratorPostorder();
        while ( it.hasNext() ) {
            final PhylogenyNode n = it.next();
            if ( !n.isRoot() && !n.isExternal() ) {
                if ( !ForesterUtil.isEmpty( n.getNodeName() ) ) {
                    double d = -1.0;
                    try {
                        d = Double.parseDouble( n.getNodeName() );
                    }
                    catch ( final Exception e ) {
                        d = -1.0;
                    }
                    if ( d < 0.0 ) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static boolean isEmpty( final String s ) {
        return ( ( s == null ) || ( s.trim().length() < 1 ) );
    }

    public static boolean isEqual( final double a, final double b ) {
        return ( ( Math.abs( a - b ) ) < ZERO_DIFF );
    }

    public static boolean isEven( final int n ) {
        return n % 2 == 0;
    }

    static public boolean isHasAtLeastNodeWithEvent( final Phylogeny phy ) {
        final PhylogenyNodeIterator it = phy.iteratorPostorder();
        while ( it.hasNext() ) {
            if ( it.next().getNodeData().isHasEvent() ) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if at least one branch has a length larger than zero.
     * 
     * 
     * @param phy
     */
    static public boolean isHasAtLeastOneBranchLengthLargerThanZero( final Phylogeny phy ) {
        final PhylogenyNodeIterator it = phy.iteratorPostorder();
        while ( it.hasNext() ) {
            if ( it.next().getDistanceToParent() > 0.0 ) {
                return true;
            }
        }
        return false;
    }

    static public boolean isHasAtLeastOneBranchWithSupportValues( final Phylogeny phy ) {
        final PhylogenyNodeIterator it = phy.iteratorPostorder();
        while ( it.hasNext() ) {
            if ( it.next().getBranchData().isHasConfidences() ) {
                return true;
            }
        }
        return false;
    }

    /**
     * This determines whether String[] a and String[] b have at least one
     * String in common (intersect). Returns false if at least one String[] is
     * null or empty.
     * 
     * @param a
     *            a String[] b a String[]
     * @return true if both a and b or not empty or null and contain at least
     *         one element in common false otherwise
     */
    public static boolean isIntersecting( final String[] a, final String[] b ) {
        if ( ( a == null ) || ( b == null ) ) {
            return false;
        }
        if ( ( a.length < 1 ) || ( b.length < 1 ) ) {
            return false;
        }
        for( int i = 0; i < a.length; ++i ) {
            final String ai = a[ i ];
            for( int j = 0; j < b.length; ++j ) {
                if ( ( ai != null ) && ( b[ j ] != null ) && ai.equals( b[ j ] ) ) {
                    return true;
                }
            }
        }
        return false;
    }

    public static double isLargerOrEqualToZero( final double d ) {
        if ( d > 0.0 ) {
            return d;
        }
        else {
            return 0.0;
        }
    }

    public static String isReadableFile( final File f ) {
        if ( !f.exists() ) {
            return "file [" + f + "] does not exist";
        }
        if ( f.isDirectory() ) {
            return "[" + f + "] is a directory";
        }
        if ( !f.isFile() ) {
            return "[" + f + "] is not a file";
        }
        if ( !f.canRead() ) {
            return "file [" + f + "] is not readable";
        }
        if ( f.length() < 1 ) {
            return "file [" + f + "] is empty";
        }
        return null;
    }

    public static String isWritableFile( final File f ) {
        if ( f.isDirectory() ) {
            return "[" + f + "] is a directory";
        }
        if ( f.exists() ) {
            return "[" + f + "] already exists";
        }
        return null;
    }

    /**
     * Helper for method "stringToColor".
     * <p>
     * (Last modified: 12/20/03)
     */
    public static int limitRangeForColor( int i ) {
        if ( i > 255 ) {
            i = 255;
        }
        else if ( i < 0 ) {
            i = 0;
        }
        return i;
    }

    public static SortedMap<Object, Integer> listToSortedCountsMap( final List list ) {
        final SortedMap<Object, Integer> map = new TreeMap<Object, Integer>();
        for( final Object key : list ) {
            if ( !map.containsKey( key ) ) {
                map.put( key, 1 );
            }
            else {
                map.put( key, map.get( key ) + 1 );
            }
        }
        return map;
    }

    public static StringBuffer mapToStringBuffer( final Map map, final String key_value_separator ) {
        final StringBuffer sb = new StringBuffer();
        for( final Iterator iter = map.keySet().iterator(); iter.hasNext(); ) {
            final Object key = iter.next();
            sb.append( key.toString() );
            sb.append( key_value_separator );
            sb.append( map.get( key ).toString() );
            sb.append( ForesterUtil.getLineSeparator() );
        }
        return sb;
    }

    public static String normalizeString( final String s, final int length, final boolean left_pad, final char pad_char ) {
        if ( s.length() > length ) {
            return s.substring( 0, length );
        }
        else {
            final StringBuffer pad = new StringBuffer( length - s.length() );
            for( int i = 0; i < ( length - s.length() ); ++i ) {
                pad.append( pad_char );
            }
            if ( left_pad ) {
                return pad + s;
            }
            else {
                return s + pad;
            }
        }
    }

    public static BufferedReader obtainReader( final Object source ) throws IOException, FileNotFoundException {
        BufferedReader reader = null;
        if ( source instanceof File ) {
            final File f = ( File ) source;
            if ( !f.exists() ) {
                throw new IOException( "\"" + f.getAbsolutePath() + "\" does not exist" );
            }
            else if ( !f.isFile() ) {
                throw new IOException( "\"" + f.getAbsolutePath() + "\" is not a file" );
            }
            else if ( !f.canRead() ) {
                throw new IOException( "\"" + f.getAbsolutePath() + "\" is not a readable" );
            }
            reader = new BufferedReader( new FileReader( f ) );
        }
        else if ( source instanceof InputStream ) {
            reader = new BufferedReader( new InputStreamReader( ( InputStream ) source ) );
        }
        else if ( source instanceof String ) {
            reader = new BufferedReader( new StringReader( ( String ) source ) );
        }
        else if ( source instanceof StringBuffer ) {
            reader = new BufferedReader( new StringReader( source.toString() ) );
        }
        else {
            throw new IllegalArgumentException( "attempt to parse object of type [" + source.getClass()
                    + "] (can only parse objects of type File, InputStream, String, or StringBuffer)" );
        }
        return reader;
    }

    public static StringBuffer pad( final double number, final int size, final char pad, final boolean left_pad ) {
        return pad( new StringBuffer( number + "" ), size, pad, left_pad );
    }

    public static StringBuffer pad( final String string, final int size, final char pad, final boolean left_pad ) {
        return pad( new StringBuffer( string ), size, pad, left_pad );
    }

    public static StringBuffer pad( final StringBuffer string, final int size, final char pad, final boolean left_pad ) {
        final StringBuffer padding = new StringBuffer();
        final int s = size - string.length();
        if ( s < 1 ) {
            return new StringBuffer( string.substring( 0, size ) );
        }
        for( int i = 0; i < s; ++i ) {
            padding.append( pad );
        }
        if ( left_pad ) {
            return padding.append( string );
        }
        else {
            return string.append( padding );
        }
    }

    public static void postOrderRelabelInternalNodes( final Phylogeny phylogeny, final int starting_number ) {
        int i = starting_number;
        for( final PhylogenyNodeIterator it = phylogeny.iteratorPostorder(); it.hasNext(); ) {
            final PhylogenyNode node = it.next();
            if ( !node.isExternal() ) {
                node.setName( String.valueOf( i++ ) );
            }
        }
    }

    public static void printArray( final Object[] a ) {
        for( int i = 0; i < a.length; ++i ) {
            System.out.println( "[" + i + "]=" + a[ i ] );
        }
    }

    public static void printCountingMap( final Map<String, Integer> counting_map ) {
        for( final String key : counting_map.keySet() ) {
            System.out.println( key + ": " + counting_map.get( key ) );
        }
    }

    public static void printErrorMessage( final String prg_name, final String message ) {
        System.out.println( "[" + prg_name + "] > error: " + message );
    }

    public static void printProgramInformation( final String prg_name, final String prg_version, final String date ) {
        final int l = prg_name.length() + prg_version.length() + date.length() + 4;
        System.out.println();
        System.out.println( prg_name + " " + prg_version + " (" + date + ")" );
        for( int i = 0; i < l; ++i ) {
            System.out.print( "_" );
        }
        System.out.println();
    }

    public static void printProgramInformation( final String prg_name,
                                                final String prg_version,
                                                final String date,
                                                final String email,
                                                final String www ) {
        final int l = prg_name.length() + prg_version.length() + date.length() + 4;
        System.out.println();
        System.out.println( prg_name + " " + prg_version + " (" + date + ")" );
        for( int i = 0; i < l; ++i ) {
            System.out.print( "_" );
        }
        System.out.println();
        System.out.println();
        System.out.println( "WWW    : " + www );
        System.out.println( "Contact: " + email );
        if ( !ForesterUtil.isEmpty( ForesterUtil.JAVA_VERSION ) && !ForesterUtil.isEmpty( ForesterUtil.JAVA_VENDOR ) ) {
            System.out.println();
            System.out.println( "[running on Java " + ForesterUtil.JAVA_VERSION + " " + ForesterUtil.JAVA_VENDOR + "]" );
        }
        System.out.println();
    }

    public static void printWarningMessage( final String prg_name, final String message ) {
        System.out.println( "[" + prg_name + "] > warning: " + message );
    }

    public static void programMessage( final String prg_name, final String message ) {
        System.out.println( "[" + prg_name + "] > " + message );
    }

    public static String removeSuffix( final String file_name ) {
        final int i = file_name.lastIndexOf( '.' );
        if ( i > 1 ) {
            return file_name.substring( 0, i );
        }
        return file_name;
    }

    /**
     * Removes all white space from String s.
     * 
     * @return String s with white space removed
     */
    public static String removeWhiteSpace( String s ) {
        int i;
        for( i = 0; i <= s.length() - 1; i++ ) {
            if ( ( s.charAt( i ) == ' ' ) || ( s.charAt( i ) == '\t' ) || ( s.charAt( i ) == '\n' )
                    || ( s.charAt( i ) == '\r' ) ) {
                s = s.substring( 0, i ) + s.substring( i + 1 );
                i--;
            }
        }
        return s;
    }

    public static String replaceIllegalNhCharacters( final String nh ) {
        if ( nh == null ) {
            return "";
        }
        return nh.trim().replaceAll( "[\\[\\](),:;\\s]+", "_" );
    }

    public static String replaceIllegalNhxCharacters( final String nhx ) {
        if ( nhx == null ) {
            return "";
        }
        return nhx.trim().replaceAll( "[\\[\\](),:;\\s]+", "_" );
    }

    public static double round( final double value, final int decimal_place ) {
        BigDecimal bd = new BigDecimal( value );
        bd = bd.setScale( decimal_place, BigDecimal.ROUND_HALF_UP );
        return bd.doubleValue();
    }

    /**
     * Rounds d to an int.
     */
    public static int roundToInt( final double d ) {
        return ( int ) ( d + 0.5 );
    }

    public static int roundToInt( final float f ) {
        return ( int ) ( f + 0.5f );
    }

    public static String sanitizeString( final String s ) {
        if ( s == null ) {
            return "";
        }
        else {
            return s.trim();
        }
    }

    public static String stringArrayToString( final String[] a ) {
        final StringBuffer sb = new StringBuffer();
        if ( ( a != null ) && ( a.length > 0 ) ) {
            for( int i = 0; i < a.length - 1; ++i ) {
                sb.append( a[ i ] + ", " );
            }
            sb.append( a[ a.length - 1 ] );
        }
        return sb.toString();
    }

    static public void transferInternalNamesToBootstrapSupport( final Phylogeny phy ) {
        final PhylogenyNodeIterator it = phy.iteratorPostorder();
        while ( it.hasNext() ) {
            final PhylogenyNode n = it.next();
            if ( !n.isExternal() && !ForesterUtil.isEmpty( n.getNodeName() ) ) {
                double value = -1;
                try {
                    value = Double.parseDouble( n.getNodeName() );
                }
                catch ( final NumberFormatException e ) {
                    throw new IllegalArgumentException( "failed to parse number from [" + n.getNodeName() + "]: "
                            + e.getLocalizedMessage() );
                }
                if ( value >= 0.0 ) {
                    n.getBranchData().addConfidence( new Confidence( value, "bootstrap" ) );
                    n.setName( "" );
                }
            }
        }
    }

    static public void transferInternalNodeNamesToConfidence( final Phylogeny phy ) {
        final PhylogenyNodeIterator it = phy.iteratorPostorder();
        while ( it.hasNext() ) {
            final PhylogenyNode n = it.next();
            if ( !n.isRoot() && !n.isExternal() && !n.getBranchData().isHasConfidences() ) {
                if ( !ForesterUtil.isEmpty( n.getNodeName() ) ) {
                    double d = -1.0;
                    try {
                        d = Double.parseDouble( n.getNodeName() );
                    }
                    catch ( final Exception e ) {
                        d = -1.0;
                    }
                    if ( d >= 0.0 ) {
                        n.getBranchData().addConfidence( new Confidence( d, "" ) );
                        n.setName( "" );
                    }
                }
            }
        }
    }

    static public void transferNodeNameToField( final Phylogeny phy, final PhylogenyNodeField field ) {
        final PhylogenyNodeIterator it = phy.iteratorPostorder();
        while ( it.hasNext() ) {
            final PhylogenyNode n = it.next();
            final String name = n.getNodeName().trim();
            if ( !ForesterUtil.isEmpty( name ) ) {
                switch ( field ) {
                    case TAXONOMY_CODE:
                        n.setName( "" );
                        PhylogenyMethods.setTaxonomyCode( n, name );
                        break;
                    case TAXONOMY_SCIENTIFIC_NAME:
                        n.setName( "" );
                        if ( !n.getNodeData().isHasTaxonomy() ) {
                            n.getNodeData().setTaxonomy( new Taxonomy() );
                        }
                        n.getNodeData().getTaxonomy().setScientificName( name );
                        break;
                    case TAXONOMY_COMMON_NAME:
                        n.setName( "" );
                        if ( !n.getNodeData().isHasTaxonomy() ) {
                            n.getNodeData().setTaxonomy( new Taxonomy() );
                        }
                        n.getNodeData().getTaxonomy().setCommonName( name );
                        break;
                    case SEQUENCE_SYMBOL:
                        n.setName( "" );
                        if ( !n.getNodeData().isHasSequence() ) {
                            n.getNodeData().setSequence( new Sequence() );
                        }
                        n.getNodeData().getSequence().setSymbol( name );
                        break;
                    case SEQUENCE_NAME:
                        n.setName( "" );
                        if ( !n.getNodeData().isHasSequence() ) {
                            n.getNodeData().setSequence( new Sequence() );
                        }
                        n.getNodeData().getSequence().setName( name );
                        break;
                }
            }
        }
    }

    public static void unexpectedFatalError( final String prg_name, final Exception e ) {
        System.err.println();
        System.err.println( "[" + prg_name
                + "] > unexpected error (Should not have occured! Please contact program author(s).)" );
        e.printStackTrace( System.err );
        System.err.println();
        System.exit( -1 );
    }

    public static void unexpectedFatalError( final String prg_name, final String message ) {
        System.err.println();
        System.err.println( "[" + prg_name
                + "] > unexpected error. Should not have occured! Please contact program author(s)." );
        System.err.println( message );
        System.err.println();
        System.exit( -1 );
    }

    public static void unexpectedFatalError( final String prg_name, final String message, final Exception e ) {
        System.err.println();
        System.err.println( "[" + prg_name
                + "] > unexpected error. Should not have occured! Please contact program author(s)." );
        System.err.println( message );
        e.printStackTrace( System.err );
        System.err.println();
        System.exit( -1 );
    }

    /**
     * Helper method for calcColor methods.
     * 
     * @param smallercolor_component_x
     *            color component the smaller color
     * @param largercolor_component_x
     *            color component the larger color
     * @param x
     *            factor
     * @return an int representing a color component
     */
    private static int calculateColorComponent( final double smallercolor_component_x,
                                                final double largercolor_component_x,
                                                final double x ) {
        return ( int ) ( smallercolor_component_x + ( ( x * ( largercolor_component_x - smallercolor_component_x ) ) / 255.0 ) );
    }

    /**
     * Helper method for calcColor methods.
     * 
     * 
     * @param value
     *            the value
     * @param larger
     *            the largest value
     * @param smaller
     *            the smallest value
     * @return a normalized value between larger and smaller
     */
    private static double calculateColorFactor( final double value, final double larger, final double smaller ) {
        return ( 255.0 * ( value - smaller ) ) / ( larger - smaller );
    }

    private static String[] splitString( final String str ) {
        final String regex = "[\\s;,]+";
        return str.split( regex );
    }

    public static enum PhylogenyNodeField {
        CLADE_NAME, TAXONOMY_CODE, TAXONOMY_SCIENTIFIC_NAME, TAXONOMY_COMMON_NAME, SEQUENCE_SYMBOL, SEQUENCE_NAME;
    }
}
