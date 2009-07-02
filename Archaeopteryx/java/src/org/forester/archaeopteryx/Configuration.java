// $Id: Configuration.java,v 1.13 2009/03/30 20:23:41 cmzmasek Exp $
// FORESTER -- software libraries and applications
// for evolutionary biology research and applications.
//
// Copyright (C) 2008-2009 Christian M. Zmasek
// Copyright (C) 2008-2009 Burnham Institute for Medical Research
// Copyright (C) 2000-2001 Washington University School of Medicine
// and Howard Hughes Medical Institute
// Copyright (C) 2003-2007 Ethalinda K.S. Cannon
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

package org.forester.archaeopteryx;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.forester.archaeopteryx.Options.NODE_LABEL_DIRECTION;
import org.forester.archaeopteryx.Options.OVERVIEW_PLACEMENT_TYPE;
import org.forester.archaeopteryx.Options.PHYLOGENY_GRAPHICS_TYPE;
import org.forester.util.ForesterUtil;

final class Configuration {

    private static final String             WEB_LINK_KEY                  = "web_link";
    private static final String             DISPLAY_COLOR_KEY             = "display_color";
    private static final int                DEPRECATED                    = -2;
    private boolean                         _native_ui                    = false;
    private boolean                         _use_tabbed_display           = false;
    private boolean                         _hide_controls_and_menus      = false;
    private boolean                         _non_lined_up_cladogram       = false;
    private SortedMap<String, WebLink>      _weblinks                     = null;
    private SortedMap<String, Color>        _display_colors               = null;
    private boolean                         _antialias_screen             = true;
    private PHYLOGENY_GRAPHICS_TYPE         _phylogeny_graphics_type      = PHYLOGENY_GRAPHICS_TYPE.RECTANGULAR;
    private String                          _base_font_family_name        = "";
    private int                             _base_font_size               = -1;
    private int                             _graphics_export_x            = -1;
    private int                             _graphics_export_y            = -1;
    private short                           _ov_max_width                 = 60;
    private short                           _ov_max_height                = 80;
    private OVERVIEW_PLACEMENT_TYPE         _ov_placement                 = OVERVIEW_PLACEMENT_TYPE.UPPER_LEFT;
    private double                          _min_confidence_value         = Options.MIN_CONFIDENCE_DEFAULT;
    private double                          _print_line_width             = Constants.PDF_LINE_WIDTH_DEFAULT;
    private boolean                         _show_scale                   = false;
    private boolean                         _show_branch_length_values    = false;
    private boolean                         _show_overview                = true;
    final static int                        display_as_phylogram          = 0;
    final static int                        show_node_names               = 1;
    final static int                        show_tax_code                 = 2;
    final static int                        show_annotation               = 3;
    final static int                        write_confidence_values       = 4;
    final static int                        write_events                  = 5;
    final static int                        color_orthologous             = 6;
    final static int                        color_super_orthologous       = 7;
    final static int                        color_subtree_neighbors       = 8;
    final static int                        color_according_to_species    = 9;
    final static int                        color_branches                = 10;
    final static int                        width_branches                = 11;
    final static int                        show_domain_architectures     = 12;
    final static int                        show_binary_characters        = 13;
    final static int                        show_binary_character_counts  = 14;
    final static int                        show_gene_names               = 15;
    final static int                        show_sequence_acc             = 16;
    final static int                        display_internal_data         = 17;
    final static int                        dynamically_hide_data         = 18;
    final static int                        show_taxonomy_names           = 19;
    final static int                        color_according_to_annotation = 20;
    final static int                        show_property                 = 21;
    // ------------------
    // Click-to options
    // ------------------
    final static int                        display_node_data             = 0;
    final static int                        collapse_uncollapse           = 1;
    final static int                        reroot                        = 2;
    final static int                        subtree                       = 3;
    final static int                        swap                          = 4;
    final static int                        color_subtree                 = 5;
    final static int                        open_seq_web                  = 6;
    final static int                        open_tax_web                  = 7;
    // ---------------------------
    // Display options for trees
    // ---------------------------
    // ---------------------------------
    // Pertaining to the config itself
    // ---------------------------------
    // Full path to config (may be URL)
    String                                  config_filename;
    String                                  default_config_filename       = Constants.DEFAULT_CONFIGURATION_FILE_NAME;
    String                                  display_options[][]           = { { "Phylogram", "display", "?" },
            { "Node Name", "display", "yes" }, { "Taxonomy Code", "display", "yes" },
            { "Annotation", "nodisplay", "no" }, { "Confidence Value", "display", "?" }, { "Event", "display", "?" },
            { "Orthology", "nodisplay", "no" }, { "S-Orthology", "nodisplay", "no" },
            { "Subtr-Neighbors", "nodisplay", "no" }, { "Taxonomy colorize", "display", "yes" },
            { "Colorize Branches", "display", "no" }, { "Use Branch-Width", "nodisplay", "no" },
            { "Domain Architectures", "nodisplay", "no" }, { "Binary Characters", "nodisplay", "no" },
            { "Binary Char Counts", "nodisplay", "no" }, { "Protein/Gene Name", "display", "no" },
            { "Protein/Gene Acc", "display", "no" }, { "Show Internal Data", "display", "yes" },
            { "Dynamic Hiding", "display", "yes" }, { "Taxonomy Name", "display", "yes" },
            { "Annotation colorize", "nodisplay", "no" }, { "Property", "nodisplay", "no" } };
    String                                  clickto_options[][]           = { { "Display Node Data", "display" },
            { "Collapse/Uncollapse", "display" }, { "Root/Reroot", "display" }, { "Sub/Super Tree", "display" },
            { "Swap Descendants", "display" }, { "Colorize Subtree", "display" }, { "Open Sequence Web", "nodisplay" },
            { "Open Taxonomy Web", "nodisplay" }                         };
    // This option is selected in the dropdown
    int                                     default_clickto               = Configuration.display_node_data;
    // --------------
    // Color set
    // --------------
    TreeColorSet                            tree_color_set;
    // -------
    // Fonts
    // -------
    TreeFontSet                             tree_font_set;
    // ----------------
    // Species colors
    // ----------------
    private static Hashtable<String, Color> _species_colors;
    // ----------------
    // Domain colors
    // ----------------
    private static Hashtable<String, Color> _domain_colors;
    // ----------------
    // Function colors
    // ----------------
    private static Hashtable<String, Color> _annotation_colors;
    boolean                                 verbose                       = Constants.VERBOSE_DEFAULT;
    private NODE_LABEL_DIRECTION            _node_label_direction         = NODE_LABEL_DIRECTION.HORIZONTAL;
    private static String                   DEFAULT_FONT_FAMILY           = "";
    static {
        for( final String font_name : Constants.DEFAULT_FONT_CHOICES ) {
            if ( Arrays.binarySearch( Util.getAvailableFontFamiliesSorted(), font_name ) >= 0 ) {
                DEFAULT_FONT_FAMILY = font_name;
                break;
            }
        }
        if ( ForesterUtil.isEmpty( DEFAULT_FONT_FAMILY ) ) {
            DEFAULT_FONT_FAMILY = Constants.DEFAULT_FONT_CHOICES[ Constants.DEFAULT_FONT_CHOICES.length - 1 ];
        }
    }

    Configuration( final String cf, final boolean is_url, final boolean is_applet ) {
        if ( ForesterUtil.isEmpty( cf ) ) {
            config_filename = default_config_filename;
        }
        else {
            config_filename = cf;
        }
        setWebLinks( new TreeMap<String, WebLink>() );
        setDisplayColors( new TreeMap<String, Color>() );
        config_filename = config_filename.trim();
        URL u = null;
        if ( is_url ) {
            // If URL, open accordingly
            try {
                u = new URL( config_filename );
                try {
                    final InputStreamReader isr = new InputStreamReader( u.openStream() );
                    final BufferedReader bf = new BufferedReader( isr );
                    readConfig( bf );
                    bf.close();
                    ForesterUtil.programMessage( Constants.PRG_NAME, "successfully read from configuration url ["
                            + config_filename + "]" );
                }
                catch ( final Exception e ) {
                    ForesterUtil.printWarningMessage( Constants.PRG_NAME, "failed to read configuration from ["
                            + config_filename + "]: " + e.getLocalizedMessage() );
                }
            }
            catch ( final Exception e ) {
                ForesterUtil.printWarningMessage( Constants.PRG_NAME, "cannot find or open configuration url ["
                        + config_filename + "]" );
            }
        }
        else {
            // Otherwise, open as a file
            File f = new File( config_filename );
            if ( !f.exists() ) {
                f = new File( config_filename + ".txt" );
            }
            if ( f.exists() && f.canRead() ) {
                try {
                    final BufferedReader bf = new BufferedReader( new FileReader( f ) );
                    readConfig( bf );
                    bf.close();
                }
                catch ( final Exception e ) {
                    ForesterUtil.printWarningMessage( Constants.PRG_NAME, "failed to read configuration from ["
                            + config_filename + "]: " + e );
                }
            }
            else {
                ForesterUtil.printWarningMessage( Constants.PRG_NAME, "cannot find or open configuration file ["
                        + config_filename + "]" );
            }
        }
    }

    public String getBaseFontFamilyName() {
        return _base_font_family_name;
    }

    boolean doCheckOption( final int which ) {
        return display_options[ which ][ 2 ].equals( "yes" );
    }

    boolean doDisplayClickToOption( final int which ) {
        return clickto_options[ which ][ 1 ].equals( "display" );
    }

    boolean doDisplayOption( final int which ) {
        return display_options[ which ][ 1 ].equals( "display" );
    }

    /**
     * Will attempt to use the phylogeny to determine whether to check
     * this or not (e.g. phylogram)
     * 
     */
    boolean doGuessCheckOption( final int which ) {
        return display_options[ which ][ 2 ].equals( "?" );
    }

    Map<String, Color> getAnnotationColors() {
        if ( _annotation_colors == null ) {
            _annotation_colors = new Hashtable<String, Color>();
        }
        return _annotation_colors;
    }

    int getBaseFontSize() {
        return _base_font_size;
    }

    int getClickToOptionsCount() {
        return clickto_options.length;
    }

    String getClickToTitle( final int which ) {
        return clickto_options[ which ][ 0 ];
    }

    int getDefaultDisplayClicktoOption() {
        return default_clickto;
    }

    SortedMap<String, Color> getDisplayColors() {
        return _display_colors;
    }

    String getDisplayTitle( final int which ) {
        return display_options[ which ][ 0 ];
    }

    Map<String, Color> getDomainColors() {
        if ( _domain_colors == null ) {
            _domain_colors = new Hashtable<String, Color>();
        }
        return _domain_colors;
    }

    int getGraphicsExportX() {
        return _graphics_export_x;
    }

    int getGraphicsExportY() {
        return _graphics_export_y;
    }

    double getMinConfidenceValue() {
        return _min_confidence_value;
    }

    NODE_LABEL_DIRECTION getNodeLabelDirection() {
        return _node_label_direction;
    }

    short getOvMaxHeight() {
        return _ov_max_height;
    }

    short getOvMaxWidth() {
        return _ov_max_width;
    }

    OVERVIEW_PLACEMENT_TYPE getOvPlacement() {
        return _ov_placement;
    }

    PHYLOGENY_GRAPHICS_TYPE getPhylogenyGraphicsType() {
        return _phylogeny_graphics_type;
    }

    double getPrintLineWidth() {
        return _print_line_width;
    }

    Hashtable<String, Color> getSpeciesColors() {
        if ( _species_colors == null ) {
            _species_colors = new Hashtable<String, Color>();
        }
        return _species_colors;
    }

    TreeColorSet getTreeColorSet() {
        return null;
    }

    TreeFontSet getTreeFontSet() {
        return null;
    }

    WebLink getWebLink( final String source ) {
        return getWebLinks().get( source );
    }

    Map<String, WebLink> getWebLinks() {
        return _weblinks;
    }

    boolean isAntialiasScreen() {
        return _antialias_screen;
    }

    /**
     * Convenience method.
     * 
     * @return true if value in configuration file was 'yes'
     */
    boolean isDrawAsPhylogram() {
        return doCheckOption( display_as_phylogram );
    }

    boolean isHasWebLink( final String source ) {
        return getWebLinks().containsKey( source );
    }

    /**
     * Only used by ArchaeoptryxE.
     *
     */
    boolean isHideControlPanelAndMenubar() {
        return _hide_controls_and_menus;
    }

    boolean isNonLinedUpCladogram() {
        return _non_lined_up_cladogram;
    }

    boolean isShowBranchLengthValues() {
        return _show_branch_length_values;
    }

    boolean isShowOverview() {
        return _show_overview;
    }

    boolean isShowScale() {
        return _show_scale;
    }

    boolean isUseNativeUI() {
        if ( Util.isMac() ) {
            return true;
        }
        return _native_ui;
    }

    /**
     * Only used by ArchaeoptryxE.
     *
     */
    boolean isUseTabbedDisplay() {
        return _use_tabbed_display;
    }

    void setDisplayColors( final SortedMap<String, Color> display_colors ) {
        _display_colors = display_colors;
    }

    void setNodeLabelDirection( final NODE_LABEL_DIRECTION node_label_direction ) {
        _node_label_direction = node_label_direction;
    }

    void setPhylogenyGraphicsType( final PHYLOGENY_GRAPHICS_TYPE phylogeny_graphics_type ) {
        _phylogeny_graphics_type = phylogeny_graphics_type;
    }

    void setWebLinks( final SortedMap<String, WebLink> weblinks ) {
        _weblinks = weblinks;
    }

    private void createWebLink( final String url_str, final String desc, final String source_identifier ) {
        WebLink weblink = null;
        boolean ex = false;
        try {
            weblink = new WebLink( new URL( url_str.trim() ), desc.trim(), source_identifier.trim() );
        }
        catch ( final MalformedURLException e ) {
            ForesterUtil.printWarningMessage( Constants.PRG_NAME, "could not create URL from [" + url_str + "]" );
            ex = true;
        }
        if ( !ex && ( weblink != null ) ) {
            getWebLinks().put( weblink.getSourceIdentifier().toLowerCase(), weblink );
        }
    }

    private int getClickToIndex( final String name ) {
        int index = -1;
        if ( name.equals( "edit_info" ) ) {
            index = Configuration.display_node_data;
            ForesterUtil
                    .printWarningMessage( Constants.PRG_NAME,
                                          "configuration key [edit_info] is deprecated, use [display node data] instead" );
        }
        else if ( name.equals( "display_node_data" ) ) {
            index = Configuration.display_node_data;
        }
        else if ( name.equals( "collapse_uncollapse" ) ) {
            index = Configuration.collapse_uncollapse;
        }
        else if ( name.equals( "reroot" ) ) {
            index = Configuration.reroot;
        }
        else if ( name.equals( "subtree" ) ) {
            index = Configuration.subtree;
        }
        else if ( name.equals( "swap" ) ) {
            index = Configuration.swap;
        }
        else if ( name.equals( "display_sequences" ) ) {
            ForesterUtil
                    .printWarningMessage( Constants.PRG_NAME, "configuration key [display_sequences] is deprecated" );
            return DEPRECATED;
        }
        else if ( name.equals( "open_seq_web" ) ) {
            index = Configuration.open_seq_web;
        }
        else if ( name.equals( "open_tax_web" ) ) {
            index = Configuration.open_tax_web;
        }
        else if ( name.equals( "display_node_popup" ) ) {
            ForesterUtil.printWarningMessage( Constants.PRG_NAME,
                                              "configuration key [display_node_popup] is deprecated" );
            return DEPRECATED;
        }
        else if ( name.equals( "custom_option" ) ) {
            ForesterUtil.printWarningMessage( Constants.PRG_NAME, "configuration key [custom_option] is deprecated" );
            return DEPRECATED;
        }
        else if ( name.equals( "color_subtree" ) ) {
            index = Configuration.color_subtree;
        }
        else if ( name.equals( "go_to_swiss_prot" ) ) {
            ForesterUtil.printWarningMessage( Constants.PRG_NAME, "configuration key [go_to_swiss_prot] is deprecated" );
            return DEPRECATED;
        }
        return index;
    }

    private double parseDouble( final String str ) {
        double d = 0.0;
        try {
            d = Double.parseDouble( str );
        }
        catch ( final Exception e ) {
            ForesterUtil.printWarningMessage( Constants.PRG_NAME, "could not parse double from [" + str + "]" );
            d = 0.0;
        }
        return d;
    }

    private int parseInt( final String str ) {
        int i = -1;
        try {
            i = Integer.parseInt( str );
        }
        catch ( final Exception e ) {
            ForesterUtil.printWarningMessage( Constants.PRG_NAME, "could not parse integer from [" + str + "]" );
            i = -1;
        }
        return i;
    }

    private void processFontFamily( final StringTokenizer st ) {
        setBaseFontFamilyName( "" );
        final String font_str = ( ( String ) st.nextElement() ).trim();
        final String[] fonts = font_str.split( ",+" );
        for( String font : fonts ) {
            font = font.replace( '_', ' ' ).trim();
            if ( Arrays.binarySearch( Util.getAvailableFontFamiliesSorted(), font ) >= 0 ) {
                setBaseFontFamilyName( font );
                break;
            }
        }
    }

    /**
     * read each line of config file, process non-comment lines
     * @throws IOException 
     */
    private void readConfig( final BufferedReader conf_in ) throws IOException {
        String line;
        do {
            line = conf_in.readLine();
            if ( line != null ) {
                line = line.trim();
                // skip comments and blank lines
                if ( !line.startsWith( "#" ) && ( !ForesterUtil.isEmpty( line ) ) ) {
                    // convert runs of spaces to tabs
                    line = line.replaceAll( "\\s+", "\t" );
                    final StringTokenizer st = new StringTokenizer( line, "\t" );
                    setKeyValue( st );
                }
            }
        } while ( line != null );
    }

    private void setAntialiasScreen( final boolean antialias_screen ) {
        _antialias_screen = antialias_screen;
    }

    private void setBaseFontFamilyName( final String base_font_family_name ) {
        _base_font_family_name = base_font_family_name;
    }

    private void setBaseFontSize( final int base_font_size ) {
        _base_font_size = base_font_size;
    }

    private void setGraphicsExportX( final int graphics_export_x ) {
        _graphics_export_x = graphics_export_x;
    }

    private void setGraphicsExportY( final int graphics_export_y ) {
        _graphics_export_y = graphics_export_y;
    }

    /**
     * Set a key-value(s) tuple
     */
    private void setKeyValue( final StringTokenizer st ) {
        String key = ( String ) st.nextElement();
        key = key.replace( ':', ' ' );
        key = key.trim();
        // Handle single value settings first:
        if ( key.equals( "default_click_to" ) ) {
            final String clickto_name = ( String ) st.nextElement();
            default_clickto = getClickToIndex( clickto_name );
            if ( default_clickto == -1 ) {
                ForesterUtil.printWarningMessage( Constants.PRG_NAME, "invalid value [" + clickto_name
                        + "] for [default_click_to]" );
                default_clickto = 0;
            }
            else if ( default_clickto == DEPRECATED ) {
                // Deprecated.
            }
        }
        else if ( key.equals( "native_ui" ) ) {
            if ( ( ( String ) st.nextElement() ).trim().toLowerCase().equals( "yes" ) ) {
                _native_ui = true;
            }
            else {
                _native_ui = false;
            }
        }
        else if ( key.equals( "antialias_screen" ) ) {
            if ( ( ( String ) st.nextElement() ).trim().toLowerCase().equals( "yes" ) ) {
                setAntialiasScreen( true );
            }
            else {
                setAntialiasScreen( false );
            }
        }
        else if ( key.equals( "phylogeny_graphics_type" ) ) {
            final String type_str = ( ( String ) st.nextElement() ).trim();
            if ( type_str.equalsIgnoreCase( PHYLOGENY_GRAPHICS_TYPE.CONVEX.toString() ) ) {
                setPhylogenyGraphicsType( PHYLOGENY_GRAPHICS_TYPE.CONVEX );
            }
            else if ( type_str.equalsIgnoreCase( PHYLOGENY_GRAPHICS_TYPE.CURVED.toString() ) ) {
                setPhylogenyGraphicsType( PHYLOGENY_GRAPHICS_TYPE.CURVED );
            }
            else if ( type_str.equalsIgnoreCase( PHYLOGENY_GRAPHICS_TYPE.EURO_STYLE.toString() ) ) {
                setPhylogenyGraphicsType( PHYLOGENY_GRAPHICS_TYPE.EURO_STYLE );
            }
            else if ( type_str.equalsIgnoreCase( PHYLOGENY_GRAPHICS_TYPE.RECTANGULAR.toString() ) ) {
                setPhylogenyGraphicsType( PHYLOGENY_GRAPHICS_TYPE.RECTANGULAR );
            }
            else if ( type_str.equalsIgnoreCase( PHYLOGENY_GRAPHICS_TYPE.TRIANGULAR.toString() ) ) {
                setPhylogenyGraphicsType( PHYLOGENY_GRAPHICS_TYPE.TRIANGULAR );
            }
            else if ( type_str.equalsIgnoreCase( PHYLOGENY_GRAPHICS_TYPE.UNROOTED.toString() ) ) {
                setPhylogenyGraphicsType( PHYLOGENY_GRAPHICS_TYPE.UNROOTED );
            }
            else if ( type_str.equalsIgnoreCase( PHYLOGENY_GRAPHICS_TYPE.CIRCULAR.toString() ) ) {
                setPhylogenyGraphicsType( PHYLOGENY_GRAPHICS_TYPE.CIRCULAR );
            }
            else {
                setPhylogenyGraphicsType( PHYLOGENY_GRAPHICS_TYPE.RECTANGULAR );
                ForesterUtil.printWarningMessage( Constants.PRG_NAME, "unknown value [" + type_str
                        + "] for [phylogeny_graphics_type]" );
            }
        }
        else if ( key.equals( "min_confidence_value" ) ) {
            final String mcv_str = ( ( String ) st.nextElement() ).trim();
            final double d = parseDouble( mcv_str );
            setMinConfidenceValue( d );
        }
        else if ( key.equals( "font_family" ) ) {
            processFontFamily( st );
        }
        else if ( key.equals( "font_size" ) ) {
            final String size_str = ( ( String ) st.nextElement() ).trim();
            final int i = parseInt( size_str );
            setBaseFontSize( i );
        }
        else if ( key.equals( "graphics_export_x" ) ) {
            final String str = ( ( String ) st.nextElement() ).trim();
            final int i = parseInt( str );
            setGraphicsExportX( i );
        }
        else if ( key.equals( "graphics_export_y" ) ) {
            final String str = ( ( String ) st.nextElement() ).trim();
            final int i = parseInt( str );
            setGraphicsExportY( i );
        }
        else if ( key.equals( "pdf_export_line_width" ) ) {
            final String str = ( ( String ) st.nextElement() ).trim();
            final double d = parseDouble( str );
            if ( d > 0 ) {
                setPrintLineWidth( d );
            }
        }
        else if ( key.equals( "show_scale" ) ) {
            if ( ( ( String ) st.nextElement() ).trim().toLowerCase().equals( "yes" ) ) {
                setShowScale( true );
            }
            else {
                setShowScale( false );
            }
        }
        else if ( key.equals( "show_overview" ) ) {
            if ( ( ( String ) st.nextElement() ).trim().toLowerCase().equals( "yes" ) ) {
                setShowOverview( true );
            }
            else {
                setShowOverview( false );
            }
        }
        else if ( key.equals( "show_branch_length_values" ) ) {
            if ( ( ( String ) st.nextElement() ).trim().toLowerCase().equals( "yes" ) ) {
                setShowBranchLengthValues( true );
            }
            else {
                setShowBranchLengthValues( false );
            }
        }
        else if ( key.equals( "non_lined_up_cladogram" ) ) {
            if ( ( ( String ) st.nextElement() ).trim().toLowerCase().equals( "yes" ) ) {
                setNonLinedUpCladogram( true );
            }
            else {
                setNonLinedUpCladogram( false );
            }
        }
        else if ( key.equals( "hide_controls_and_menus" ) ) {
            if ( ( ( String ) st.nextElement() ).trim().toLowerCase().equals( "yes" ) ) {
                _hide_controls_and_menus = true;
            }
        }
        else if ( key.equals( "use_tabbed_display" ) ) {
            if ( ( ( String ) st.nextElement() ).trim().toLowerCase().equals( "yes" ) ) {
                _use_tabbed_display = true;
            }
        }
        else if ( key.equals( "overview_width" ) ) {
            final int i = parseInt( ( ( String ) st.nextElement() ).trim() );
            setOvMaxWidth( ( short ) i );
        }
        else if ( key.equals( "overview_height" ) ) {
            final int i = parseInt( ( ( String ) st.nextElement() ).trim() );
            setOvMaxHeight( ( short ) i );
        }
        else if ( key.equals( "overview_placement_type" ) ) {
            final String type_str = ( ( String ) st.nextElement() ).trim();
            if ( type_str.equalsIgnoreCase( OVERVIEW_PLACEMENT_TYPE.UPPER_LEFT.toTag() ) ) {
                setOvPlacement( OVERVIEW_PLACEMENT_TYPE.UPPER_LEFT );
            }
            else if ( type_str.equalsIgnoreCase( OVERVIEW_PLACEMENT_TYPE.UPPER_RIGHT.toTag() ) ) {
                setOvPlacement( OVERVIEW_PLACEMENT_TYPE.UPPER_RIGHT );
            }
            else if ( type_str.equalsIgnoreCase( OVERVIEW_PLACEMENT_TYPE.LOWER_LEFT.toTag() ) ) {
                setOvPlacement( OVERVIEW_PLACEMENT_TYPE.LOWER_LEFT );
            }
            else if ( type_str.equalsIgnoreCase( OVERVIEW_PLACEMENT_TYPE.LOWER_RIGHT.toTag() ) ) {
                setOvPlacement( OVERVIEW_PLACEMENT_TYPE.LOWER_RIGHT );
            }
            else {
                setOvPlacement( OVERVIEW_PLACEMENT_TYPE.UPPER_LEFT );
                ForesterUtil.printWarningMessage( Constants.PRG_NAME, "unknown value [" + type_str
                        + "] for [overview_placement_type]" );
            }
        }
        else if ( key.equals( "node_label_direction" ) ) {
            final String type_str = ( ( String ) st.nextElement() ).trim();
            if ( type_str.equalsIgnoreCase( NODE_LABEL_DIRECTION.HORIZONTAL.toString() ) ) {
                setNodeLabelDirection( NODE_LABEL_DIRECTION.HORIZONTAL );
            }
            else if ( type_str.equalsIgnoreCase( NODE_LABEL_DIRECTION.RADIAL.toString() ) ) {
                setNodeLabelDirection( NODE_LABEL_DIRECTION.RADIAL );
            }
            else {
                setNodeLabelDirection( NODE_LABEL_DIRECTION.HORIZONTAL );
                ForesterUtil.printWarningMessage( Constants.PRG_NAME, "unknown value [" + type_str
                        + "] for [node_label_direction]" );
            }
        }
        else if ( key.equals( "glyph_type" ) ) {
            ForesterUtil.printWarningMessage( Constants.PRG_NAME, "configuration key [glyph_type] is deprecated" );
        }
        else if ( st.countTokens() >= 2 ) { // counts the tokens that are not
            // yet retrieved!
            int key_index = -1;
            if ( key.equals( "use_real_br_lengths" ) || key.equals( "phylogram" ) ) {
                key_index = Configuration.display_as_phylogram;
                if ( key.equals( "use_real_br_lengths" ) ) {
                    ForesterUtil
                            .printWarningMessage( Constants.PRG_NAME,
                                                  "configuration key [use_real_br_lengths] is deprecated, use [phylogram] instead" );
                }
            }
            else if ( key.equals( "color_according_to_species" ) ) {
                key_index = Configuration.color_according_to_species;
            }
            else if ( key.equals( "show_node_names" ) ) {
                key_index = Configuration.show_node_names;
            }
            else if ( key.equals( "show_taxonomy" ) || key.equals( "show_taxonomy_code" ) ) {
                key_index = Configuration.show_tax_code;
                if ( key.equals( "show_taxonomy" ) ) {
                    ForesterUtil
                            .printWarningMessage( Constants.PRG_NAME,
                                                  "configuration key [show_taxonomy] is deprecated, use [show_taxonomy_code] instead" );
                }
            }
            else if ( key.equals( "write_br_length_values" ) ) {
                ForesterUtil.printWarningMessage( Constants.PRG_NAME,
                                                  "configuration key [write_br_length_values] is deprecated" );
                key_index = DEPRECATED;
            }
            else if ( key.equals( "write_bootstrap_values" ) || key.equals( "write_confidence_values" ) ) {
                key_index = Configuration.write_confidence_values;
                if ( key.equals( "write_bootstrap_values" ) ) {
                    ForesterUtil
                            .printWarningMessage( Constants.PRG_NAME,
                                                  "configuration key [write_bootstrap_values] is deprecated, use [write_confidence_values] instead" );
                }
            }
            else if ( key.equals( "write_events" ) || key.equals( "write_dup_spec" ) ) {
                key_index = Configuration.write_events;
                if ( key.equals( "write_dup_spec" ) ) {
                    ForesterUtil
                            .printWarningMessage( Constants.PRG_NAME,
                                                  "configuration key [write_dup_spec] is deprecated, use [write_events] instead" );
                }
            }
            else if ( key.equals( "color_branches" ) ) {
                key_index = Configuration.color_branches;
            }
            else if ( key.equals( "width_branches" ) ) {
                key_index = Configuration.width_branches;
            }
            else if ( key.equals( "color_orthologous" ) ) {
                key_index = Configuration.color_orthologous;
            }
            else if ( key.equals( "color_subtree_neighbors" ) ) {
                key_index = Configuration.color_subtree_neighbors;
            }
            else if ( key.equals( "color_super_orthologous" ) ) {
                key_index = Configuration.color_super_orthologous;
            }
            else if ( key.equals( "mark_nodes_with_box" ) ) {
                ForesterUtil.printWarningMessage( Constants.PRG_NAME,
                                                  "configuration key [mark_nodes_with_box] is deprecated" );
                key_index = DEPRECATED;
            }
            else if ( key.equals( "show_domain_architectures" ) ) {
                key_index = Configuration.show_domain_architectures;
            }
            else if ( key.equals( "show_annotations" ) ) {
                key_index = Configuration.show_annotation;
            }
            else if ( key.equals( "show_binary_characters" ) ) {
                key_index = Configuration.show_binary_characters;
            }
            else if ( key.equals( "show_binary_character_counts" ) ) {
                key_index = Configuration.show_binary_character_counts;
            }
            else if ( key.equals( "show_gene_names" ) ) {
                key_index = Configuration.show_gene_names;
            }
            else if ( key.equals( "show_sequence_acc" ) ) {
                key_index = Configuration.show_sequence_acc;
            }
            else if ( key.equals( "show_node_ids" ) ) {
                ForesterUtil
                        .printWarningMessage( Constants.PRG_NAME, "configuration key [show_node_ids] is deprecated" );
                key_index = DEPRECATED;
            }
            else if ( key.equals( "display_internal_data" ) ) {
                key_index = Configuration.display_internal_data;
            }
            else if ( key.equals( "dynamically_hide_data" ) ) {
                key_index = Configuration.dynamically_hide_data;
            }
            else if ( key.equals( "show_taxonomy_names" ) ) {
                key_index = Configuration.show_taxonomy_names;
            }
            else if ( key.equals( "color_according_to_annotation" ) ) {
                key_index = Configuration.color_according_to_annotation;
            }
            else if ( key.equals( "show_property" ) ) {
                key_index = Configuration.show_property;
            }
            // If we've found the key, set the values
            if ( key_index >= 0 ) {
                display_options[ key_index ][ 1 ] = ( String ) st.nextElement();
                display_options[ key_index ][ 2 ] = ( String ) st.nextElement();
                // otherwise, keep looking
            }
            else {
                if ( key_index == DEPRECATED ) {
                    // Deprecated.
                }
                else if ( key.equals( "click_to" ) ) {
                    final String click_to_name = ( String ) st.nextElement();
                    key_index = getClickToIndex( click_to_name );
                    if ( key_index >= 0 ) {
                        clickto_options[ key_index ][ 1 ] = ( String ) st.nextElement();
                    }
                    else if ( key_index == DEPRECATED ) {
                        // Deprecated.
                    }
                    else {
                        ForesterUtil.printWarningMessage( Constants.PRG_NAME, "unknown click-to option: "
                                + click_to_name );
                    }
                }
                else if ( key.equals( "species_color" ) ) {
                    getSpeciesColors().put( ( String ) st.nextElement(), Color.decode( ( String ) st.nextElement() ) );
                }
                else if ( key.equals( "domain_color" ) ) {
                    getDomainColors().put( ( String ) st.nextElement(), Color.decode( ( String ) st.nextElement() ) );
                }
                else if ( key.equals( "annotation_color" ) ) {
                    getAnnotationColors()
                            .put( ( String ) st.nextElement(), Color.decode( ( String ) st.nextElement() ) );
                }
                else if ( key.equals( "function_color" ) ) {
                    ForesterUtil.printWarningMessage( Constants.PRG_NAME,
                                                      "configuration key [function_color] is deprecated" );
                }
                else if ( key.equals( DISPLAY_COLOR_KEY ) ) {
                    getDisplayColors().put( ( String ) st.nextElement(), Color.decode( ( String ) st.nextElement() ) );
                }
                else if ( key.equals( WEB_LINK_KEY ) ) {
                    if ( st.countTokens() == 3 ) {
                        createWebLink( ( String ) st.nextElement(), ( String ) st.nextElement(), ( String ) st
                                .nextElement() );
                    }
                    else {
                        ForesterUtil.printWarningMessage( Constants.PRG_NAME,
                                                          "illegal format in configuration file for key [" + key + "]" );
                    }
                }
                else {
                    ForesterUtil.printWarningMessage( Constants.PRG_NAME, "unknown configuration key [" + key
                            + "] in: " + config_filename );
                }
            }
        }
        else {
            ForesterUtil.printWarningMessage( Constants.PRG_NAME, "unknown configuration key [" + key + "] in: "
                    + config_filename );
        }
    }

    private void setMinConfidenceValue( final double min_confidence_value ) {
        _min_confidence_value = min_confidence_value;
    }

    private void setNonLinedUpCladogram( final boolean non_lined_up_cladogram ) {
        _non_lined_up_cladogram = non_lined_up_cladogram;
    }

    private void setOvMaxHeight( final short ov_max_height ) {
        _ov_max_height = ov_max_height;
    }

    private void setOvMaxWidth( final short ov_max_width ) {
        _ov_max_width = ov_max_width;
    }

    private void setOvPlacement( final OVERVIEW_PLACEMENT_TYPE ov_placement ) {
        _ov_placement = ov_placement;
    }

    private void setPrintLineWidth( final double print_line_width ) {
        _print_line_width = print_line_width;
    }

    private void setShowBranchLengthValues( final boolean show_branch_length_values ) {
        _show_branch_length_values = show_branch_length_values;
    }

    private void setShowOverview( final boolean show_overview ) {
        _show_overview = show_overview;
    }

    private void setShowScale( final boolean show_scale ) {
        _show_scale = show_scale;
    }

    static String getDefaultFontFamilyName() {
        return DEFAULT_FONT_FAMILY;
    }
}
