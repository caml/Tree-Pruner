// $Id: NHXParser.java,v 1.52 2009/01/13 19:49:32 cmzmasek Exp $
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

package org.forester.io.parsers.nhx;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.forester.development.Development;
import org.forester.io.parsers.PhylogenyParser;
import org.forester.io.parsers.PhylogenyParserException;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyMethods;
import org.forester.phylogeny.PhylogenyNode;
import org.forester.phylogeny.data.Accession;
import org.forester.phylogeny.data.Annotation;
import org.forester.phylogeny.data.DomainArchitecture;
import org.forester.phylogeny.data.Event;
import org.forester.phylogeny.data.Identifier;
import org.forester.phylogeny.data.PropertiesMap;
import org.forester.phylogeny.data.Property;
import org.forester.phylogeny.data.Sequence;
import org.forester.phylogeny.data.Taxonomy;
import org.forester.phylogeny.iterators.PhylogenyNodeIterator;
import org.forester.util.ForesterUtil;

public final class NHXParser implements PhylogenyParser {

    public static final boolean             LIMIT_SPECIES_NAMES_TO_FIVE_CHARS = true;
    public static final TAXONOMY_EXTRACTION TAXONOMY_EXTRACTION_DEFAULT       = TAXONOMY_EXTRACTION.PFAM_STYLE_ONLY;
    final static private boolean            DEBUG                             = Development.isDebug();
    final static private boolean            TIME                              = Development.isTime();
    final static private boolean            GUESS_ROOTEDNESS_DEFAULT          = true;
    final static private boolean            GUESS_IF_SUPPORT_VALUES           = true;
    final static private byte               STRING                            = 0;
    final static private byte               STRING_BUFFER                     = 1;
    final static private byte               CHAR_ARRAY                        = 2;
    final static private byte               BUFFERED_READER                   = 3;
    public static final boolean             REPLACE_UNDERSCORES_DEFAULT       = false;
    private boolean                         _saw_closing_paren;
    private boolean                         _guess_rootedness;
    private byte                            _input_type;
    private int                             _source_length;
    private PhylogenyNode                   _current_node;
    private StringBuffer                    _current_anotation;
    private Object                          _nhx_source;
    private int                             _clade_level;
    private List<Phylogeny>                 _phylogenies;
    private Phylogeny                       _current_phylogeny;
    private TAXONOMY_EXTRACTION             _taxonomy_extraction;
    private boolean                         _replace_underscores;
    private final static Pattern            UC_LETTERS_NUMBERS_PATTERN        = Pattern.compile( "^[A-Z0-9]+$" );
    private final static Pattern            NUMBERS_ONLY_PATTERN              = Pattern.compile( "^[0-9]+$" );

    public NHXParser() {
        init();
    }

    public TAXONOMY_EXTRACTION getTaxonomyExtraction() {
        return _taxonomy_extraction;
    }

    /**
     * Parses the source set with setSource( final Object nhx_source ). Returns
     * the Phylogenies found in the source as Phylogeny[].
     * Everything between [ and ] is considered comment and ignored,
     * unless:
     * "[&&NHX... ]"
     * or
     * ":digits and/or.[bootstrap]" 
     * 
     * @see #setSource( final Object nhx_source )
     * @see org.forester.io.parsers.PhylogenyParser#parse()
     * @return Phylogeny[]
     * @throws IOException
     * @throws NHXFormatException
     * @throws PhylogenyParserException
     */
    public Phylogeny[] parse() throws IOException, NHXFormatException {
        if ( NHXParser.DEBUG ) {
            System.out.println( "\n[DEBUG] " + getClass() + ": START." );
        }
        long start_time = 0;
        boolean in_comment = false;
        boolean saw_colon = false;
        boolean saw_open_bracket = false;
        if ( NHXParser.TIME ) {
            start_time = new Date().getTime();
        }
        setPhylogenies( new ArrayList<Phylogeny>() );
        setCladeLevel( 0 );
        newCurrentAnotation();
        int i = 0;
        while ( true ) {
            char c = '\b';
            if ( getInputType() == NHXParser.BUFFERED_READER ) {
                final int ci = ( ( BufferedReader ) getNhxSource() ).read();
                if ( ci >= 0 ) {
                    c = ( char ) ci;
                }
                else {
                    break;
                }
            }
            else {
                if ( i >= getSourceLength() ) {
                    break;
                }
                else {
                    switch ( getInputType() ) {
                        case STRING:
                            c = ( ( String ) getNhxSource() ).charAt( i );
                            break;
                        case STRING_BUFFER:
                            c = ( ( StringBuffer ) getNhxSource() ).charAt( i );
                            break;
                        case CHAR_ARRAY:
                            c = ( ( char[] ) getNhxSource() )[ i ];
                            break;
                    }
                }
            }
            if ( c == ':' ) {
                saw_colon = true;
            }
            else if ( !( ( c < 33 ) || ( c > 126 ) ) && saw_colon
                    && ( ( c != '[' ) && ( c != '.' ) && ( ( c < 48 ) || ( c > 57 ) ) ) ) {
                saw_colon = false;
            }
            // any kind of whitespace is always ignored:
            if ( ( c < 33 ) || ( c > 126 ) || ( ( getCladeLevel() == 0 ) && ( c == ';' ) ) ) {
                // Do nothing.
            }
            else if ( in_comment ) {
                if ( c == ']' ) {
                    in_comment = false;
                }
            }
            else if ( c == '[' ) {
                saw_open_bracket = true;
            }
            else if ( saw_open_bracket ) {
                if ( c != ']' ) {
                    // everything not starting with "[&" is considered a comment
                    // unless ":digits and/or . [bootstrap]":
                    if ( c == '&' ) {
                        getCurrentAnotation().append( "[&" );
                    }
                    else if ( saw_colon ) {
                        getCurrentAnotation().append( "[" + c );
                    }
                    else {
                        in_comment = true;
                    }
                }
                // comment consisting just of "[]":
                saw_open_bracket = false;
            }
            else if ( c == '(' ) {
                processOpenParen();
            }
            else if ( c == ')' ) {
                processCloseParen();
            }
            else if ( c == ',' ) {
                processComma();
            }
            else {
                getCurrentAnotation().append( c );
            }
            ++i;
        }
        if ( getCladeLevel() != 0 ) {
            setPhylogenies( null );
            throw new PhylogenyParserException( "Error in NH / NHX: Most likely cause: Number of open parens does not equal number of close parens." );
        }
        if ( getCurrentPhylogeny() != null ) {
            finishPhylogeny();
        }
        else if ( getCurrentAnotation().length() > 0 ) {
            finishSingleNodePhylogeny();
        }
        else if ( getPhylogenies().size() < 1 ) {
            getPhylogenies().add( new Phylogeny() );
        }
        if ( NHXParser.TIME ) {
            System.out.println( "[TIME] Parsing required " + ( new Date().getTime() - start_time ) + "ms." );
        }
        if ( NHXParser.DEBUG ) {
            System.out.println( "[DEBUG] " + getClass() + ": END." );
        }
        return getPhylogeniesAsArray();
    } // parse()

    public void setGuessRootedness( final boolean guess_rootedness ) {
        _guess_rootedness = guess_rootedness;
    }

    public void setReplaceUnderscores( final boolean replace_underscores ) {
        _replace_underscores = replace_underscores;
    }

    /**
     * This sets the source to be parsed. The source can be: String,
     * StringBuffer, char[], File, or InputStream. The source can contain more
     * than one phylogenies in either New Hamphshire (NH) or New Hamphshire
     * Extended (NHX) format. There is no need to separate phylogenies with any
     * special character. White space is always ignored, as are semicolons
     * inbetween phylogenies. Example of a source describing two phylogenies
     * (source is a String, in this example): "(A,(B,(C,(D,E)de)cde)bcde)abcde
     * ((((A,B)ab,C)abc,D)abcd,E)abcde". Everything between a '[' followed by any
     * character other than '&' and ']' is considered a comment and ignored
     * (example: "[this is a comment]"). NHX tags are surrounded by '[&&NHX' and
     * ']' (example: "[&&NHX:S=Varanus_storri]"). A sequence like "[& some
     * info]" is ignored, too (at the PhylogenyNode level, though).
     * Exception: numbers only between [ and ] (e.g. [90]) are interpreted as support values.
     * 
     * @see #parse()
     * @see org.forester.io.parsers.PhylogenyParser#setSource(java.lang.Object)
     * @param nhx_source
     *            the source to be parsed (String, StringBuffer, char[], File,
     *            or InputStream)
     * @throws IOException
     * @throws PhylogenyParserException
     */
    public void setSource( final Object nhx_source ) throws PhylogenyParserException, IOException {
        if ( nhx_source == null ) {
            throw new PhylogenyParserException( getClass() + ": attempt to parse null object." );
        }
        else if ( nhx_source instanceof String ) {
            setInputType( NHXParser.STRING );
            setSourceLength( ( ( String ) nhx_source ).length() );
            setNhxSource( nhx_source );
        }
        else if ( nhx_source instanceof StringBuffer ) {
            setInputType( NHXParser.STRING_BUFFER );
            setSourceLength( ( ( StringBuffer ) nhx_source ).length() );
            setNhxSource( nhx_source );
        }
        else if ( nhx_source instanceof char[] ) {
            setInputType( NHXParser.CHAR_ARRAY );
            setSourceLength( ( ( char[] ) nhx_source ).length );
            setNhxSource( nhx_source );
        }
        else if ( nhx_source instanceof File ) {
            setInputType( NHXParser.BUFFERED_READER );
            setSourceLength( 0 );
            final File f = ( File ) nhx_source;
            final String error = ForesterUtil.isReadableFile( f );
            if ( !ForesterUtil.isEmpty( error ) ) {
                throw new PhylogenyParserException( error );
            }
            setNhxSource( new BufferedReader( new FileReader( f ) ) );
        }
        else if ( nhx_source instanceof InputStream ) {
            setInputType( NHXParser.BUFFERED_READER );
            setSourceLength( 0 );
            final InputStreamReader isr = new InputStreamReader( ( InputStream ) nhx_source );
            setNhxSource( new BufferedReader( isr ) );
        }
        else {
            throw new IllegalArgumentException( getClass() + " can only parse objects of type String,"
                    + " StringBuffer, char[], File," + " or InputStream " + " [attempt to parse object of "
                    + nhx_source.getClass() + "]." );
        }
    } // setSource( final Object nhx_source )

    public void setTaxonomyExtraction( final TAXONOMY_EXTRACTION taxonomy_extraction ) {
        _taxonomy_extraction = taxonomy_extraction;
    }

    /**
     * Decreases the clade level by one.
     * 
     * @throws PhylogenyParserException
     *             if level goes below zero.
     */
    private void decreaseCladeLevel() throws PhylogenyParserException {
        if ( getCladeLevel() < 0 ) {
            throw new PhylogenyParserException( "Error in NH / NHX: Most likely cause: Number of close parens is larger than number of open parens." );
        }
        --_clade_level;
    }

    /**
     * Finishes the current Phylogeny and adds it to the list of Phylogenies
     * created.
     * 
     * @throws PhylogenyParserException
     * @throws NHXFormatException
     */
    private void finishPhylogeny() throws PhylogenyParserException, NHXFormatException {
        setCladeLevel( 0 );
        if ( getCurrentPhylogeny() != null ) {
            parseNHX( getCurrentAnotation().toString(),
                      getCurrentPhylogeny().getRoot(),
                      getTaxonomyExtraction(),
                      isReplaceUnderscores() );
            if ( NHXParser.DEBUG ) {
                System.out.println( "[DEBUG] Node annotated with: " + getCurrentAnotation() + " [level="
                        + getCladeLevel() + "]." );
                System.out.println( "[DEBUG] " + "Completed Phylogeny: " + getCurrentPhylogeny().toNewHampshireX() );
            }
            if ( NHXParser.GUESS_IF_SUPPORT_VALUES ) {
                if ( NHXParser.isBranchLengthsLikeBootstrapValues( getCurrentPhylogeny() ) ) {
                    NHXParser.moveBranchLengthsToBootstrapValues( getCurrentPhylogeny() );
                }
            }
            if ( isGuessRootedness() ) {
                final PhylogenyNode root = getCurrentPhylogeny().getRoot();
                if ( ( root.getDistanceToParent() >= 0.0 ) || !ForesterUtil.isEmpty( root.getNodeName() )
                        || !ForesterUtil.isEmpty( PhylogenyMethods.getSpecies( root ) ) || root.isHasAssignedEvent() ) {
                    getCurrentPhylogeny().setRooted( true );
                }
            }
            getPhylogenies().add( getCurrentPhylogeny() );
        }
    } // finishPhylogeny()

    private void finishSingleNodePhylogeny() throws PhylogenyParserException, NHXFormatException {
        setCladeLevel( 0 );
        final PhylogenyNode new_node = new PhylogenyNode();
        parseNHX( getCurrentAnotation().toString(), new_node, getTaxonomyExtraction(), isReplaceUnderscores() );
        setCurrentPhylogeny( new Phylogeny() );
        getCurrentPhylogeny().setRoot( new_node );
        if ( NHXParser.DEBUG ) {
            System.out.println( "[DEBUG] " + "Completed single node Phylogeny: "
                    + getCurrentPhylogeny().toNewHampshireX() );
        }
        getPhylogenies().add( getCurrentPhylogeny() );
    }

    private int getCladeLevel() {
        return _clade_level;
    }

    // Private accessor methods
    // ------------------------
    private StringBuffer getCurrentAnotation() {
        return _current_anotation;
    }

    private PhylogenyNode getCurrentNode() {
        return _current_node;
    }

    private Phylogeny getCurrentPhylogeny() {
        return _current_phylogeny;
    }

    private byte getInputType() {
        return _input_type;
    }

    private Object getNhxSource() {
        return _nhx_source;
    }

    private List<Phylogeny> getPhylogenies() {
        return _phylogenies;
    }

    /**
     * Returns the Phylogenies created as Array.
     * 
     * @return the Phylogenies created as Array
     */
    private Phylogeny[] getPhylogeniesAsArray() {
        final Phylogeny[] p = new Phylogeny[ getPhylogenies().size() ];
        for( int i = 0; i < getPhylogenies().size(); ++i ) {
            p[ i ] = getPhylogenies().get( i );
        }
        return p;
    }

    private int getSourceLength() {
        return _source_length;
    }

    /**
     * Increases the clade level by one.
     */
    private void increaseCladeLevel() {
        ++_clade_level;
    }

    private void init() {
        setTaxonomyExtraction( TAXONOMY_EXTRACTION_DEFAULT );
        setReplaceUnderscores( REPLACE_UNDERSCORES_DEFAULT );
        setGuessRootedness( GUESS_ROOTEDNESS_DEFAULT );
    }

    private boolean isGuessRootedness() {
        return _guess_rootedness;
    }

    private boolean isReplaceUnderscores() {
        return _replace_underscores;
    }

    private boolean isSawClosingParen() {
        return _saw_closing_paren;
    }

    /**
     * Replaces the current annotation with a new StringBuffer.
     */
    private void newCurrentAnotation() {
        setCurrentAnotation( new StringBuffer() );
    }

    /**
     * Called if a closing paren is encountered.
     * 
     * @throws PhylogenyParserException
     * @throws NHXFormatException
     */
    private void processCloseParen() throws PhylogenyParserException, NHXFormatException {
        decreaseCladeLevel();
        if ( !isSawClosingParen() ) {
            final PhylogenyNode new_node = new PhylogenyNode();
            parseNHX( getCurrentAnotation().toString(), new_node, getTaxonomyExtraction(), isReplaceUnderscores() );
            if ( NHXParser.DEBUG ) {
                System.out.println( "[DEBUG] New node created for: " + getCurrentAnotation() + " [level="
                        + getCladeLevel() + "] [)]." );
            }
            newCurrentAnotation();
            getCurrentNode().addAsChild( new_node );
        }
        else {
            parseNHX( getCurrentAnotation().toString(),
                      getCurrentNode().getLastChildNode(),
                      getTaxonomyExtraction(),
                      isReplaceUnderscores() );
            if ( NHXParser.DEBUG ) {
                System.out.println( "[DEBUG] Node annotated with: " + getCurrentAnotation() + " [level="
                        + getCladeLevel() + "] [)]." );
            }
            newCurrentAnotation();
        }
        if ( !getCurrentNode().isRoot() ) {
            setCurrentNode( getCurrentNode().getParent() );
        }
        setSawClosingParen( true );
    } // processCloseParen()

    /**
     * Called if a comma is encountered.
     * 
     * @throws PhylogenyParserException
     * @throws NHXFormatException
     */
    private void processComma() throws PhylogenyParserException, NHXFormatException {
        if ( !isSawClosingParen() ) {
            final PhylogenyNode new_node = new PhylogenyNode();
            parseNHX( getCurrentAnotation().toString(), new_node, getTaxonomyExtraction(), isReplaceUnderscores() );
            if ( getCurrentNode() == null ) {
                throw new NHXFormatException( "format might not be NH or NHX" );
            }
            getCurrentNode().addAsChild( new_node );
            if ( NHXParser.DEBUG ) {
                System.out.println( "[DEBUG] New node created for: " + getCurrentAnotation() + " [level="
                        + getCladeLevel() + "] [,]." );
            }
        }
        else {
            parseNHX( getCurrentAnotation().toString(),
                      getCurrentNode().getLastChildNode(),
                      getTaxonomyExtraction(),
                      isReplaceUnderscores() );
            if ( NHXParser.DEBUG ) {
                System.out.println( "[DEBUG] Node annotated with: " + getCurrentAnotation() + " [level="
                        + getCladeLevel() + "] [,]." );
            }
        }
        newCurrentAnotation();
        setSawClosingParen( false );
    } // processComma()

    /**
     * Called if a opening paren is encountered.
     * 
     * @throws PhylogenyParserException
     * @throws NHXFormatException
     */
    private void processOpenParen() throws PhylogenyParserException, NHXFormatException {
        final PhylogenyNode new_node = new PhylogenyNode();
        if ( getCladeLevel() == 0 ) {
            if ( getCurrentPhylogeny() != null ) {
                finishPhylogeny();
            }
            setCladeLevel( 1 );
            newCurrentAnotation();
            setCurrentPhylogeny( new Phylogeny() );
            getCurrentPhylogeny().setRoot( new_node );
            if ( NHXParser.DEBUG ) {
                System.out.println( "[DEBUG] " + "Starting a new Phylogeny [(]." );
            }
        }
        else {
            if ( NHXParser.DEBUG ) {
                System.out.println( "[DEBUG] [level=" + getCladeLevel() + "] [(]." );
            }
            increaseCladeLevel();
            getCurrentNode().addAsChild( new_node );
        }
        setCurrentNode( new_node );
        setSawClosingParen( false );
    } // processOpenParen()

    private void setCladeLevel( final int clade_level ) {
        if ( clade_level < 0 ) {
            throw new IllegalArgumentException( "Attempt to set clade level to a number smaller than zero." );
        }
        _clade_level = clade_level;
    }

    private void setCurrentAnotation( final StringBuffer current_anotation ) {
        _current_anotation = current_anotation;
    }

    private void setCurrentNode( final PhylogenyNode current_node ) {
        _current_node = current_node;
    }

    private void setCurrentPhylogeny( final Phylogeny current_phylogeny ) {
        _current_phylogeny = current_phylogeny;
    }

    private void setInputType( final byte input_type ) {
        _input_type = input_type;
    }

    private void setNhxSource( final Object nhx_source ) {
        _nhx_source = nhx_source;
    }

    private void setPhylogenies( final ArrayList<Phylogeny> phylogenies ) {
        _phylogenies = phylogenies;
    }

    private void setSawClosingParen( final boolean saw_closing_paren ) {
        _saw_closing_paren = saw_closing_paren;
    }

    private void setSourceLength( final int source_length ) {
        _source_length = source_length;
    }

    public static void parseNHX( String s,
                                 final PhylogenyNode node_to_annotate,
                                 final TAXONOMY_EXTRACTION taxonomy_extraction,
                                 final boolean replace_underscores ) throws NHXFormatException {
        if ( ( taxonomy_extraction != TAXONOMY_EXTRACTION.NO ) && replace_underscores ) {
            throw new IllegalArgumentException( "cannot extract taxonomies and replace under scores at the same time" );
        }
        if ( ( s != null ) && ( s.length() > 0 ) ) {
            if ( replace_underscores ) {
                s = s.replaceAll( "_+", " " );
            }
            int ob = 0;
            int cb = 0;
            String a = "";
            String b = "";
            StringTokenizer t = null;
            boolean is_nhx = false;
            ob = s.indexOf( "[" );
            cb = s.indexOf( "]" );
            if ( ob > -1 ) {
                a = "";
                b = "";
                is_nhx = true;
                if ( cb < 0 ) {
                    throw new NHXFormatException( "Error in NHX format: No closing \"]\"" );
                }
                if ( s.indexOf( "&&NHX" ) == ( ob + 1 ) ) {
                    b = s.substring( ob + 6, cb );
                }
                else {
                    // No &&NHX and digits only: is likely to be a support value.
                    final String bracketed = s.substring( ob + 1, cb );
                    final Matcher numbers_only = NUMBERS_ONLY_PATTERN.matcher( bracketed );
                    if ( numbers_only.matches() ) {
                        b = ":" + NHXtags.SUPPORT + bracketed;
                    }
                }
                a = s.substring( 0, ob );
                s = a + b;
                if ( ( s.indexOf( "[" ) > -1 ) || ( s.indexOf( "]" ) > -1 ) ) {
                    throw new NHXFormatException( "Error in NHX format: More than one \"]\" or \"[\"" );
                }
            }
            t = new StringTokenizer( s, ":" );
            if ( t.countTokens() >= 1 ) {
                if ( !s.startsWith( ":" ) ) {
                    node_to_annotate.setName( t.nextToken() );
                    if ( !replace_underscores && ( !is_nhx && ( taxonomy_extraction != TAXONOMY_EXTRACTION.NO ) ) ) {
                        final String tax = extractTaxonomyCodeFromNHname( node_to_annotate.getNodeName(),
                                                                          LIMIT_SPECIES_NAMES_TO_FIVE_CHARS,
                                                                          taxonomy_extraction );
                        if ( !ForesterUtil.isEmpty( tax ) ) {
                            if ( !node_to_annotate.getNodeData().isHasTaxonomy() ) {
                                node_to_annotate.getNodeData().setTaxonomy( new Taxonomy() );
                            }
                            node_to_annotate.getNodeData().getTaxonomy().setTaxonomyCode( tax );
                        }
                    }
                }
                while ( t.hasMoreTokens() ) {
                    s = t.nextToken();
                    if ( s.startsWith( org.forester.io.parsers.nhx.NHXtags.SPECIES_NAME ) ) {
                        if ( !node_to_annotate.getNodeData().isHasTaxonomy() ) {
                            node_to_annotate.getNodeData().setTaxonomy( new Taxonomy() );
                        }
                        node_to_annotate.getNodeData().getTaxonomy().setTaxonomyCode( s.substring( 2 ) );
                    }
                    else if ( s.startsWith( org.forester.io.parsers.nhx.NHXtags.ANNOTATION ) ) {
                        if ( !node_to_annotate.getNodeData().isHasSequence() ) {
                            node_to_annotate.getNodeData().setSequence( new Sequence() );
                        }
                        final Annotation annotation = new Annotation();
                        annotation.setDesc( s.substring( 3 ) );
                        node_to_annotate.getNodeData().getSequence().addAnnotation( annotation );
                    }
                    else if ( s.startsWith( org.forester.io.parsers.nhx.NHXtags.IS_DUPLICATION ) ) {
                        if ( ( s.charAt( 2 ) == 'Y' ) || ( s.charAt( 2 ) == 'T' ) ) {
                            node_to_annotate.getNodeData().setEvent( Event.createSingleDuplicationEvent() );
                        }
                        else if ( ( s.charAt( 2 ) == 'N' ) || ( s.charAt( 2 ) == 'F' ) ) {
                            node_to_annotate.getNodeData().setEvent( Event.createSingleSpeciationEvent() );
                        }
                        else if ( s.charAt( 2 ) == '?' ) {
                            node_to_annotate.getNodeData().setEvent( Event.createSingleSpeciationOrDuplicationEvent() );
                        }
                        else {
                            throw new NHXFormatException( "Error in NHX format: :D=Y or :D=N or :D=?" );
                        }
                    }
                    else if ( s.startsWith( NHXtags.SUPPORT ) ) {
                        PhylogenyMethods.setConfidence( node_to_annotate, Double.parseDouble( s.substring( 2 ) ) );
                    }
                    else if ( s.startsWith( NHXtags.TAXONOMY_ID ) ) {
                        if ( !node_to_annotate.getNodeData().isHasTaxonomy() ) {
                            node_to_annotate.getNodeData().setTaxonomy( new Taxonomy() );
                        }
                        node_to_annotate.getNodeData().getTaxonomy().setIdentifier( new Identifier( s.substring( 2 ) ) );
                    }
                    else if ( s.startsWith( NHXtags.PARENT_BRANCH_WIDTH ) ) {
                        PhylogenyMethods.setBranchWidthValue( node_to_annotate, Integer.parseInt( s.substring( 2 ) ) );
                    }
                    else if ( s.startsWith( NHXtags.COLOR ) ) {
                        final Color c = NHXParser.stringToColor( s.substring( 2 ) );
                        if ( c != null ) {
                            PhylogenyMethods.setBranchColorValue( node_to_annotate, c );
                        }
                    }
                    else if ( s.startsWith( NHXtags.CUSTOM_DATA_ON_NODE ) ) {
                        if ( !node_to_annotate.getNodeData().isHasProperties() ) {
                            node_to_annotate.getNodeData().setProperties( new PropertiesMap() );
                        }
                        node_to_annotate.getNodeData().getProperties().addProperty( Property.createFromNhxString( s ) );
                    }
                    else if ( s.startsWith( NHXtags.DOMAIN_STRUCTURE ) ) {
                        if ( !node_to_annotate.getNodeData().isHasSequence() ) {
                            node_to_annotate.getNodeData().setSequence( new Sequence() );
                        }
                        node_to_annotate.getNodeData().getSequence().setDomainArchitecture( new DomainArchitecture( s
                                .substring( 3 ) ) );
                    }
                    else if ( s.startsWith( NHXtags.NODE_IDENTIFIER ) ) {
                        node_to_annotate.getNodeData().setNodeIdentifier( new Identifier( s.substring( 3 ) ) );
                    }
                    else if ( s.startsWith( NHXtags.SEQUENCE_ACCESSION ) ) {
                        if ( !node_to_annotate.getNodeData().isHasSequence() ) {
                            node_to_annotate.getNodeData().setSequence( new Sequence() );
                        }
                        node_to_annotate.getNodeData().getSequence()
                                .setAccession( new Accession( s.substring( 3 ), "?" ) );
                    }
                    else if ( s.startsWith( NHXtags.GENE_NAME ) ) {
                        if ( !node_to_annotate.getNodeData().isHasSequence() ) {
                            node_to_annotate.getNodeData().setSequence( new Sequence() );
                        }
                        node_to_annotate.getNodeData().getSequence().setName( s.substring( 3 ) );
                    }
                    else if ( s.startsWith( NHXtags.GENE_NAME_SYNONYM ) ) {
                        if ( !node_to_annotate.getNodeData().isHasSequence() ) {
                            node_to_annotate.getNodeData().setSequence( new Sequence() );
                        }
                        node_to_annotate.getNodeData().getSequence().setName( s.substring( 2 ) );
                    }
                    else if ( s.indexOf( '=' ) < 0 ) {
                        if ( node_to_annotate.getDistanceToParent() != PhylogenyNode.DISTANCE_DEFAULT ) {
                            throw new NHXFormatException( "Error in NHX format: More than one distance to parent:"
                                    + "\"" + s + "\"" );
                        }
                        node_to_annotate.setDistanceToParent( Double.valueOf( s ).doubleValue() );
                    }
                } // while ( t.hasMoreTokens() ) 
            }
        }
    }

    /**
     * Extracts a code if and only if:
     * one and only one _, 
     * shorter than 25, 
     * no |, 
     * no ., 
     * if / present it has to be after the _, 
     * if PFAM_STYLE_ONLY: / must be present,
     * tax code can only contain uppercase letters and numbers,
     * and must contain at least one uppercase letter.
     * Return null if no code extractable.
     * 
     * @param name
     * @param limit_to_five
     * @return
     */
    private static String extractTaxonomyCodeFromNHname( final String name,
                                                         final boolean limit_to_five,
                                                         final TAXONOMY_EXTRACTION taxonomy_extraction ) {
        if ( ( name.indexOf( "_" ) > 0 ) && ( name.length() < 25 ) && ( name.lastIndexOf( "_" ) == name.indexOf( "_" ) )
                && ( name.indexOf( "|" ) < 0 ) && ( name.indexOf( "." ) < 0 )
                && ( ( taxonomy_extraction != TAXONOMY_EXTRACTION.PFAM_STYLE_ONLY ) || ( name.indexOf( "/" ) >= 0 ) )
                && ( ( ( name.indexOf( "/" ) ) < 0 ) || ( name.indexOf( "/" ) > name.indexOf( "_" ) ) ) ) {
            final String[] s = name.split( "[_/]" );
            if ( s.length > 1 ) {
                String str = s[ 1 ];
                if ( limit_to_five ) {
                    if ( str.length() > 5 ) {
                        str = str.substring( 0, 5 );
                    }
                    else if ( ( str.length() < 5 ) && ( str.startsWith( "RAT" ) || str.startsWith( "PIG" ) ) ) {
                        str = str.substring( 0, 3 );
                    }
                }
                final Matcher letters_and_numbers = UC_LETTERS_NUMBERS_PATTERN.matcher( str );
                if ( !letters_and_numbers.matches() ) {
                    return null;
                }
                final Matcher numbers_only = NUMBERS_ONLY_PATTERN.matcher( str );
                if ( numbers_only.matches() ) {
                    return null;
                }
                return str;
            }
        }
        return null;
    }

    private static boolean isBranchLengthsLikeBootstrapValues( final Phylogeny p ) {
        final PhylogenyNodeIterator it = p.iteratorExternalForward();
        final double d0 = it.next().getDistanceToParent();
        if ( ( d0 < 10 ) || !it.hasNext() ) {
            return false;
        }
        while ( it.hasNext() ) {
            final double d = it.next().getDistanceToParent();
            if ( ( d != d0 ) || ( d < 10 ) ) {
                return false;
            }
        }
        return true;
    }

    private static void moveBranchLengthsToBootstrapValues( final Phylogeny p ) {
        final PhylogenyNodeIterator it = p.iteratorPostorder();
        while ( it.hasNext() ) {
            final PhylogenyNode n = it.next();
            PhylogenyMethods.setBootstrapConfidence( n, n.getDistanceToParent() );
            n.setDistanceToParent( PhylogenyNode.DISTANCE_DEFAULT );
        }
    }

    /**
     * Parses String s in the format r.g.b (e.g. "12.34.234" ) into red, green,
     * and blue and returns the corresponding Color.
     */
    private static Color stringToColor( final String s ) {
        final StringTokenizer st = new StringTokenizer( s, "." );
        if ( st.countTokens() != 3 ) {
            throw new IllegalArgumentException( "illegal format for color: " + s );
        }
        final int red = ForesterUtil.limitRangeForColor( Integer.parseInt( st.nextToken() ) );
        final int green = ForesterUtil.limitRangeForColor( Integer.parseInt( st.nextToken() ) );
        final int blu = ForesterUtil.limitRangeForColor( Integer.parseInt( st.nextToken() ) );
        return new Color( red, green, blu );
    }

    public static enum TAXONOMY_EXTRACTION {
        NO, YES, PFAM_STYLE_ONLY;
    }
}
