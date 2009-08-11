// $Id: MainFrameApplication.java,v 1.28 2009/06/15 18:31:33 cmzmasek Exp $
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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import org.forester.archaeopteryx.Options.NODE_LABEL_DIRECTION;
import org.forester.archaeopteryx.Options.PHYLOGENY_GRAPHICS_TYPE;
import org.forester.archaeopteryx.Util.GraphicsExportType;
import org.forester.archaeopteryx.webservices.PhylogeniesWebserviceClient;
import org.forester.archaeopteryx.webservices.WebservicesManager;
import org.forester.io.parsers.PhylogenyParser;
import org.forester.io.parsers.nexus.NexusPhylogeniesParser;
import org.forester.io.parsers.nhx.NHXParser;
import org.forester.io.parsers.phyloxml.PhyloXmlParser;
import org.forester.io.parsers.tol.TolParser;
import org.forester.io.writers.PhylogenyWriter;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyNode;
import org.forester.phylogeny.data.Taxonomy;
import org.forester.phylogeny.factories.ParserBasedPhylogenyFactory;
import org.forester.phylogeny.factories.PhylogenyFactory;
import org.forester.phylogeny.iterators.PhylogenyNodeIterator;
import org.forester.sdi.GSDI;
import org.forester.sdi.SDI;
import org.forester.sdi.SDIR;
import org.forester.sdi.SDIse;
import org.forester.util.ForesterUtil;

public final class MainFrameApplication extends MainFrame {

    final static boolean                    __SNAPSHOT_RELEASE = true;                    // TODO remove me
    final static boolean                    __RELEASE          = true;                    // TODO remove me
    private final static int                FRAME_X_SIZE       = 800;
    private final static int                FRAME_Y_SIZE       = 800;
    // Filters for the file-open dialog (classes defined in this file)
    private final static NHFilter           nhfilter           = new NHFilter();
    private final static NHXFilter          nhxfilter          = new NHXFilter();
    private final static XMLFilter          xmlfilter          = new XMLFilter();
    private final static TolFilter          tolfilter          = new TolFilter();
    private final static NexusFilter        nexusfilter        = new NexusFilter();
    private final static PdfFilter          pdffilter          = new PdfFilter();
    private final static GraphicsFileFilter graphicsfilefilter = new GraphicsFileFilter();
    private final static DefaultFilter      defaultfilter      = new DefaultFilter();
    private static final long               serialVersionUID   = -799735726778865234L;
    private final JFileChooser              _open_filechooser;
    private final JFileChooser              _open_filechooser_for_species_tree;
    private final JFileChooser              _save_filechooser;
    private final JFileChooser              _writetopdf_filechooser;
    private final JFileChooser              _writetographics_filechooser;
    // Analysis menu
    private JMenu                           _analysis_menu;
    private JMenuItem                       _load_species_tree_item;
    private JMenuItem                       _sdi_item;
    private JMenuItem                       _gsdi_item;
    private JMenuItem                       _root_min_dups_item;
    private JMenuItem                       _root_min_cost_l_item;
    // Application-only print menu items
    private JMenuItem                       _print_item;
    private JMenuItem                       _write_to_pdf_item;
    private JMenuItem                       _write_to_jpg_item;
    private JMenuItem                       _write_to_gif_item;
    private JMenuItem                       _write_to_tif_item;
    private JMenuItem                       _write_to_png_item;
    private JMenuItem                       _write_to_bmp_item;
    // Remember last file-open location
    private File                            _open_dir;
    private Phylogeny                       _species_tree;

    private MainFrameApplication( final Phylogeny[] phys, final String config_file, final String title ) {
        // Read config file (false, false => not url, not applet)
        _configuration = new Configuration( config_file, false, false );
        if ( _configuration == null ) {
            throw new IllegalArgumentException( "configuration is null" );
        }
        try {
            if ( _configuration.isUseNativeUI() ) {
                UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
            }
            else {
                UIManager.setLookAndFeel( UIManager.getCrossPlatformLookAndFeelClassName() );
            }
        }
        catch ( final UnsupportedLookAndFeelException e ) {
            Util.dieWithSystemError( "UnsupportedLookAndFeelException: " + e.toString() );
        }
        catch ( final ClassNotFoundException e ) {
            Util.dieWithSystemError( "ClassNotFoundException: " + e.toString() );
        }
        catch ( final InstantiationException e ) {
            Util.dieWithSystemError( "InstantiationException: " + e.toString() );
        }
        catch ( final IllegalAccessException e ) {
            Util.dieWithSystemError( "IllegalAccessException: " + e.toString() );
        }
        catch ( final Exception e ) {
            Util.dieWithSystemError( e.toString() );
        }
        // hide until everything is ready
        setVisible( false );
        setOptions( Options.createInstance( _configuration ) );
        _textframe = null;
        _species_tree = null;
        // set title
        setTitle( Constants.PRG_NAME + " " + Constants.VERSION + " (" + Constants.PRG_DATE + ")" );
        _mainpanel = new MainPanel( _configuration, this );
        // The file dialogs
        _open_filechooser = new JFileChooser();
        _open_filechooser.setCurrentDirectory( new File( "." ) );
        _open_filechooser.setMultiSelectionEnabled( false );
        _open_filechooser.addChoosableFileFilter( MainFrameApplication.xmlfilter );
        _open_filechooser.addChoosableFileFilter( MainFrameApplication.nhxfilter );
        _open_filechooser.addChoosableFileFilter( MainFrameApplication.nhfilter );
        _open_filechooser.addChoosableFileFilter( MainFrameApplication.nexusfilter );
        _open_filechooser.addChoosableFileFilter( MainFrameApplication.tolfilter );
        _open_filechooser.addChoosableFileFilter( _open_filechooser.getAcceptAllFileFilter() );
        _open_filechooser.setFileFilter( MainFrameApplication.defaultfilter );
        _open_filechooser_for_species_tree = new JFileChooser();
        _open_filechooser_for_species_tree.setCurrentDirectory( new File( "." ) );
        _open_filechooser_for_species_tree.setMultiSelectionEnabled( false );
        _open_filechooser_for_species_tree.addChoosableFileFilter( MainFrameApplication.xmlfilter );
        _open_filechooser_for_species_tree.addChoosableFileFilter( MainFrameApplication.tolfilter );
        _open_filechooser_for_species_tree.setFileFilter( MainFrameApplication.xmlfilter );
        _save_filechooser = new JFileChooser();
        _save_filechooser.setCurrentDirectory( new File( "." ) );
        _save_filechooser.setMultiSelectionEnabled( false );
        _save_filechooser.setFileFilter( MainFrameApplication.xmlfilter );
        _save_filechooser.addChoosableFileFilter( MainFrameApplication.nhxfilter );
        _save_filechooser.addChoosableFileFilter( MainFrameApplication.nhfilter );
        _save_filechooser.addChoosableFileFilter( MainFrameApplication.nexusfilter );
        _save_filechooser.addChoosableFileFilter( _save_filechooser.getAcceptAllFileFilter() );
        _writetopdf_filechooser = new JFileChooser();
        _writetopdf_filechooser.addChoosableFileFilter( MainFrameApplication.pdffilter );
        _writetographics_filechooser = new JFileChooser();
        _writetographics_filechooser.addChoosableFileFilter( MainFrameApplication.graphicsfilefilter );
        // build the menu bar
        _jmenubar = new JMenuBar();
        if ( !_configuration.isUseNativeUI() ) {
            _jmenubar.setBackground( Constants.MENU_BACKGROUND_COLOR_DEFAULT );
        }
        buildFileMenu();
        buildToolsMenu();
        buildViewMenu();
        buildFontSizeMenu();
        buildOptionsMenu();
        buildTypeMenu();
        buildAnalysisMenu();
        buildHelpMenu();
        setJMenuBar( _jmenubar );
        _jmenubar.add( _help_jmenu );
        _contentpane = getContentPane();
        _contentpane.setLayout( new BorderLayout() );
        _contentpane.add( _mainpanel, BorderLayout.CENTER );
        // App is this big
        setSize( MainFrameApplication.FRAME_X_SIZE, MainFrameApplication.FRAME_Y_SIZE );
        //        addWindowFocusListener( new WindowAdapter() {
        //
        //            @Override
        //            public void windowGainedFocus( WindowEvent e ) {
        //                requestFocusInWindow();
        //            }
        //        } );
        // The window listener
        addWindowListener( new WindowAdapter() {

            @Override
            public void windowClosing( final WindowEvent e ) {
                close();
            }
        } );
        // The component listener
        addComponentListener( new ComponentAdapter() {

            @Override
            public void componentResized( final ComponentEvent e ) {
                if ( _mainpanel.getCurrentTreePanel() != null ) {
                    _mainpanel.getCurrentTreePanel().setParametersForPainting( _mainpanel.getCurrentTreePanel()
                                                                                       .getWidth(),
                                                                               _mainpanel.getCurrentTreePanel()
                                                                                       .getHeight(),
                                                                               false );
                }
            }
        } );
        requestFocusInWindow();
        // addKeyListener( this );
        setVisible( true );
        if ( ( phys != null ) && ( phys.length > 0 ) ) {
            Util.addPhylogeniesToTabs( phys, title, null, _configuration, _mainpanel );
            validate();
            getMainPanel().getControlPanel().showWholeAll();
            getMainPanel().getControlPanel().showWhole();
        }
        activateSaveAllIfNeeded();
        // ...and its children
        _contentpane.repaint();
        System.gc();
    }

    @Override
    public void actionPerformed( final ActionEvent e ) {
        try {
            super.actionPerformed( e );
            final Object o = e.getSource();
            // Handle app-specific actions here:
            if ( o == _open_item ) {
                readPhylogeniesFromFile();
            }
            else if ( o == _save_item ) {
                writeToFile( _mainpanel.getCurrentPhylogeny() );
                // If subtree currently displayed, save it, instead of complete
                // tree.
            }
            else if ( o == _save_all_item ) {
                writeAllToFile();
            }
            else if ( o == _close_item ) {
                closeCurrentPane();
            }
            else if ( o == _write_to_pdf_item ) {
                printToPdf( _mainpanel.getCurrentPhylogeny() );
            }
            else if ( o == _write_to_jpg_item ) {
                writeToGraphicsFile( _mainpanel.getCurrentPhylogeny(), GraphicsExportType.JPG );
            }
            else if ( o == _write_to_png_item ) {
                writeToGraphicsFile( _mainpanel.getCurrentPhylogeny(), GraphicsExportType.PNG );
            }
            else if ( o == _write_to_gif_item ) {
                writeToGraphicsFile( _mainpanel.getCurrentPhylogeny(), GraphicsExportType.GIF );
            }
            else if ( o == _write_to_tif_item ) {
                writeToGraphicsFile( _mainpanel.getCurrentPhylogeny(), GraphicsExportType.TIFF );
            }
            else if ( o == _write_to_bmp_item ) {
                writeToGraphicsFile( _mainpanel.getCurrentPhylogeny(), GraphicsExportType.BMP );
            }
            else if ( o == _print_item ) {
                print();
            }
            else if ( o == _load_species_tree_item ) {
                readSpeciesTreeFromFile();
            }
            else if ( o == _sdi_item ) {
                executeSDI();
            }
            else if ( o == _gsdi_item ) {
                executeGSDI();
            }
            else if ( o == _root_min_dups_item ) {
                executeSDIR( false );
            }
            else if ( o == _root_min_cost_l_item ) {
                executeSDIR( true );
            }
            else if ( o == _graphics_export_visible_only_cbmi ) {
                updateOptions( getOptions() );
            }
            else if ( o == _antialias_print_cbmi ) {
                updateOptions( getOptions() );
            }
            else if ( o == _print_black_and_white_cbmi ) {
                updateOptions( getOptions() );
            }
            else if ( o == _print_using_actual_size_cbmi ) {
                updateOptions( getOptions() );
            }
            else if ( o == _graphics_export_using_actual_size_cbmi ) {
                updateOptions( getOptions() );
            }
            else if ( o == _print_size_mi ) {
                choosePrintSize();
            }
            else if ( o == _choose_pdf_width_mi ) {
                choosePdfWidth();
            }
            else if ( o == _internal_number_are_confidence_for_nh_parsing_cbmi ) {
                updateOptions( getOptions() );
            }
            _contentpane.repaint();
        }
        catch ( final Exception ex ) {
            Util.unexpectedException( ex );
        }
        catch ( final Error err ) {
            Util.unexpectedError( err );
        }
    }

    //    @Override
    //    public void keyPressed( final KeyEvent e ) {
    //        MainFrameApplication.keyPressed( e, this );
    //    }
    //
    //    @Override
    //    public void keyReleased( final KeyEvent e ) {
    //        // Do nothing.
    //    }
    //
    //    @Override
    //    public void keyTyped( final KeyEvent e ) {
    //        // Do nothing.
    //    }
    void buildAnalysisMenu() {
        _analysis_menu = MainFrame.createMenu( "Analysis", getConfiguration() );
        _analysis_menu.add( _sdi_item = new JMenuItem( "SDI (Speciation Duplication Inference)" ) );
        if ( !__RELEASE && !__SNAPSHOT_RELEASE ) {
            _analysis_menu.add( _gsdi_item = new JMenuItem( "GSDI (Generalized Speciation Duplication Inference)" ) );
        }
        _analysis_menu.addSeparator();
        _analysis_menu.add( _root_min_dups_item = new JMenuItem( "Root by Minimizing Duplications | Height (SDI)" ) );
        _analysis_menu.add( _root_min_cost_l_item = new JMenuItem( "Root by Minimizing Cost L | Height (SDI)" ) );
        _analysis_menu.addSeparator();
        _analysis_menu.add( _load_species_tree_item = new JMenuItem( "Load Species Tree..." ) );
        customizeJMenuItem( _sdi_item );
        customizeJMenuItem( _gsdi_item );
        customizeJMenuItem( _root_min_dups_item );
        customizeJMenuItem( _root_min_cost_l_item );
        customizeJMenuItem( _load_species_tree_item );
        _jmenubar.add( _analysis_menu );
    }

    @Override
    void buildFileMenu() {
        _file_jmenu = MainFrame.createMenu( "File", getConfiguration() );
        _file_jmenu.add( _open_item = new JMenuItem( "Read Tree from File..." ) );
        _file_jmenu.addSeparator();
        _file_jmenu.add( _open_url_item = new JMenuItem( "Read Tree from URL/Webservice..." ) );
        _file_jmenu.addSeparator();
        final WebservicesManager webservices_manager = WebservicesManager.getInstance();
        _load_phylogeny_from_webservice_menu_items = new JMenuItem[ webservices_manager
                .getAvailablePhylogeniesWebserviceClients().size() ];
        for( int i = 0; i < webservices_manager.getAvailablePhylogeniesWebserviceClients().size(); ++i ) {
            final PhylogeniesWebserviceClient client = webservices_manager.getAvailablePhylogeniesWebserviceClient( i );
            _load_phylogeny_from_webservice_menu_items[ i ] = new JMenuItem( client.getMenuName() );
            _file_jmenu.add( _load_phylogeny_from_webservice_menu_items[ i ] );
        }
        _file_jmenu.addSeparator();
        _file_jmenu.add( _save_item = new JMenuItem( "Save Tree As..." ) );
        _file_jmenu.add( _save_all_item = new JMenuItem( "Save All Trees As..." ) );
        _save_all_item.setToolTipText( "Write all phylogenies to one file." );
        _save_all_item.setEnabled( false );
        _file_jmenu.addSeparator();
        _file_jmenu.add( _write_to_pdf_item = new JMenuItem( "Export to PDF file ..." ) );
        if ( Util.canWriteFormat( "tif" ) || Util.canWriteFormat( "tiff" ) || Util.canWriteFormat( "TIF" ) ) {
            _file_jmenu.add( _write_to_tif_item = new JMenuItem( "Export to TIFF file..." ) );
        }
        _file_jmenu.add( _write_to_png_item = new JMenuItem( "Export to PNG file..." ) );
        _file_jmenu.add( _write_to_jpg_item = new JMenuItem( "Export to JPG file..." ) );
        if ( Util.canWriteFormat( "gif" ) ) {
            _file_jmenu.add( _write_to_gif_item = new JMenuItem( "Export to GIF file..." ) );
        }
        if ( Util.canWriteFormat( "bmp" ) ) {
            _file_jmenu.add( _write_to_bmp_item = new JMenuItem( "Export to BMP file..." ) );
        }
        _file_jmenu.addSeparator();
        _file_jmenu.add( _print_item = new JMenuItem( "Print..." ) );
        _file_jmenu.addSeparator();
        _file_jmenu.add( _close_item = new JMenuItem( "Close Tab" ) );
        _close_item.setToolTipText( "To close the current pane." );
        _close_item.setEnabled( true );
        _file_jmenu.addSeparator();
        _file_jmenu.add( _exit_item = new JMenuItem( "Exit" ) );
        // For print in color option item
        customizeJMenuItem( _open_item );
        customizeJMenuItem( _open_url_item );
        for( int i = 0; i < webservices_manager.getAvailablePhylogeniesWebserviceClients().size(); ++i ) {
            customizeJMenuItem( _load_phylogeny_from_webservice_menu_items[ i ] );
        }
        customizeJMenuItem( _save_item );
        customizeJMenuItem( _close_item );
        customizeJMenuItem( _save_all_item );
        customizeJMenuItem( _write_to_pdf_item );
        customizeJMenuItem( _write_to_png_item );
        customizeJMenuItem( _write_to_jpg_item );
        customizeJMenuItem( _write_to_gif_item );
        customizeJMenuItem( _write_to_tif_item );
        customizeJMenuItem( _write_to_bmp_item );
        customizeJMenuItem( _print_item );
        customizeJMenuItem( _exit_item );
        _jmenubar.add( _file_jmenu );
    }

    void buildOptionsMenu() {
        _options_jmenu = MainFrame.createMenu( OPTIONS_HEADER, getConfiguration() );
        _options_jmenu.addChangeListener( new ChangeListener() {

            public void stateChanged( final ChangeEvent e ) {
                MainFrame.setOvPlacementColorChooseMenuItem( _overview_placment_mi, getCurrentTreePanel() );
                MainFrame.setTextColorChooseMenuItem( _switch_colors_mi, getCurrentTreePanel() );
                MainFrame
                        .setTextMinSupportMenuItem( _choose_minimal_confidence_mi, getOptions(), getCurrentTreePanel() );
                MainFrame.setTextForFontChooserMenuItem( _choose_font_mi, MainFrame
                        .createCurrentFontDesc( getMainPanel().getTreeFontSet() ) );
                setTextForGraphicsSizeChooserMenuItem( _print_size_mi, getOptions() );
                setTextForPdfLineWidthChooserMenuItem( _choose_pdf_width_mi, getOptions() );
                MainFrame.updateOptionsMenuDependingOnPhylogenyType( getMainPanel(),
                                                                     _show_scale_cbmi,
                                                                     _show_branch_length_values_cbmi,
                                                                     _non_lined_up_cladograms_cbmi,
                                                                     _label_direction_cbmi );
            }
        } );
        _options_jmenu.add( customizeMenuItemAsLabel( new JMenuItem( DISPLAY_SUBHEADER ), getConfiguration() ) );
        _options_jmenu.add( _non_lined_up_cladograms_cbmi = new JCheckBoxMenuItem( NON_LINED_UP_CLADOGRAMS_LABEL ) );
        _options_jmenu.add( _show_node_boxes_cbmi = new JCheckBoxMenuItem( DISPLAY_NODE_BOXES_LABEL ) );
        _options_jmenu.add( _show_scale_cbmi = new JCheckBoxMenuItem( DISPLAY_SCALE_LABEL ) );
        _options_jmenu
                .add( _show_branch_length_values_cbmi = new JCheckBoxMenuItem( DISPLAY_BRANCH_LENGTH_VALUES_LABEL ) );
        _options_jmenu.add( _show_overview_cbmi = new JCheckBoxMenuItem( SHOW_OVERVIEW_LABEL ) );
        _options_jmenu.add( _label_direction_cbmi = new JCheckBoxMenuItem( LABEL_DIRECTION_LABEL ) );
        _label_direction_cbmi.setToolTipText( LABEL_DIRECTION_TIP );
        _options_jmenu.add( _screen_antialias_cbmi = new JCheckBoxMenuItem( SCREEN_ANTIALIAS_LABEL ) );
        _options_jmenu.add( _choose_minimal_confidence_mi = new JMenuItem( "" ) );
        _options_jmenu.add( _overview_placment_mi = new JMenuItem( "" ) );
        _options_jmenu.add( _switch_colors_mi = new JMenuItem( "" ) );
        _options_jmenu.add( _choose_font_mi = new JMenuItem( "" ) );
        _options_jmenu.addSeparator();
        _options_jmenu.add( customizeMenuItemAsLabel( new JMenuItem( SEARCH_SUBHEADER ), getConfiguration() ) );
        _options_jmenu.add( _search_case_senstive_cbmi = new JCheckBoxMenuItem( SEARCH_CASE_SENSITIVE_LABEL ) );
        _options_jmenu.add( _search_whole_words_only_cbmi = new JCheckBoxMenuItem( SEARCH_TERMS_ONLY_LABEL ) );
        _options_jmenu.add( _inverse_search_result_cbmi = new JCheckBoxMenuItem( INVERSE_SEARCH_RESULT_LABEL ) );
        _options_jmenu.addSeparator();
        _options_jmenu.add( customizeMenuItemAsLabel( new JMenuItem( "Graphics Export & Printing:" ),
                                                      getConfiguration() ) );
        _options_jmenu.add( _antialias_print_cbmi = new JCheckBoxMenuItem( "Antialias" ) );
        _options_jmenu.add( _print_black_and_white_cbmi = new JCheckBoxMenuItem( "Export in Black and White" ) );
        _options_jmenu
                .add( _print_using_actual_size_cbmi = new JCheckBoxMenuItem( "Use Current Image Size for PDF export and Printing" ) );
        _options_jmenu
                .add( _graphics_export_using_actual_size_cbmi = new JCheckBoxMenuItem( "Use Current Image Size for PNG, JPG, and GIF export" ) );
        _options_jmenu
                .add( _graphics_export_visible_only_cbmi = new JCheckBoxMenuItem( "Limit to Visible ('Screenshot') for PNG, JPG, and GIF export" ) );
        _options_jmenu.add( _print_size_mi = new JMenuItem( "" ) );
        _options_jmenu.add( _choose_pdf_width_mi = new JMenuItem( "" ) );
        _options_jmenu.addSeparator();
        _options_jmenu
                .add( customizeMenuItemAsLabel( new JMenuItem( "Newick/NH/Nexus Parsing:" ), getConfiguration() ) );
        _options_jmenu
                .add( _internal_number_are_confidence_for_nh_parsing_cbmi = new JCheckBoxMenuItem( "Internal Numbers Are Confidence Values" ) );
        customizeJMenuItem( _choose_font_mi );
        customizeJMenuItem( _choose_minimal_confidence_mi );
        customizeJMenuItem( _switch_colors_mi );
        customizeJMenuItem( _print_size_mi );
        customizeJMenuItem( _choose_pdf_width_mi );
        customizeJMenuItem( _overview_placment_mi );
        customizeCheckBoxMenuItem( _show_node_boxes_cbmi, getOptions().isShowNodeBoxes() );
        customizeCheckBoxMenuItem( _screen_antialias_cbmi, getOptions().isAntialiasScreen() );
        customizeCheckBoxMenuItem( _search_case_senstive_cbmi, getOptions().isSearchCaseSensitive() );
        customizeCheckBoxMenuItem( _show_scale_cbmi, getOptions().isShowScale() );
        customizeCheckBoxMenuItem( _non_lined_up_cladograms_cbmi, getOptions().isNonLinedUpCladogram() );
        customizeCheckBoxMenuItem( _show_branch_length_values_cbmi, getOptions().isShowBranchLengthValues() );
        customizeCheckBoxMenuItem( _show_overview_cbmi, getOptions().isShowOverview() );
        customizeCheckBoxMenuItem( _label_direction_cbmi,
                                   getOptions().getNodeLabelDirection() == NODE_LABEL_DIRECTION.RADIAL );
        customizeCheckBoxMenuItem( _antialias_print_cbmi, getOptions().isAntialiasPrint() );
        customizeCheckBoxMenuItem( _print_black_and_white_cbmi, getOptions().isPrintBlackAndWhite() );
        customizeCheckBoxMenuItem( _internal_number_are_confidence_for_nh_parsing_cbmi, getOptions()
                .isInternalNumberAreConfidenceForNhParsing() );
        customizeCheckBoxMenuItem( _search_whole_words_only_cbmi, getOptions().isMatchWholeTermsOnly() );
        customizeCheckBoxMenuItem( _inverse_search_result_cbmi, getOptions().isInverseSearchResult() );
        customizeCheckBoxMenuItem( _graphics_export_visible_only_cbmi, getOptions().isGraphicsExportVisibleOnly() );
        customizeCheckBoxMenuItem( _print_using_actual_size_cbmi, getOptions().isPrintUsingActualSize() );
        customizeCheckBoxMenuItem( _graphics_export_using_actual_size_cbmi, getOptions()
                .isGraphicsExportUsingActualSize() );
        _jmenubar.add( _options_jmenu );
    }

    @Override
    void close() {
        removeTextFrame();
        _mainpanel.terminate();
        _contentpane.removeAll();
        setVisible( false );
        dispose();
        System.exit( 0 );
    }

    void executeGSDI() {
        if ( !isOKforSDI( false, true ) ) {
            return;
        }
        if ( !_mainpanel.getCurrentPhylogeny().isRooted() ) {
            JOptionPane.showMessageDialog( this,
                                           "Gene tree is not rooted.",
                                           "Cannot execute GSDI",
                                           JOptionPane.ERROR_MESSAGE );
            return;
        }
        final Phylogeny gene_tree = _mainpanel.getCurrentPhylogeny().copy();
        gene_tree.setAllNodesToNotCollapse();
        gene_tree.recalculateNumberOfExternalDescendants( false );
        GSDI gsdi = null;
        int duplications = -1;
        try {
            gsdi = new GSDI( gene_tree, _species_tree.copy(), true );
            duplications = gsdi.getDuplicationsSum();
        }
        catch ( final Exception e ) {
            JOptionPane.showMessageDialog( this, e.toString(), "Error during GSDI", JOptionPane.ERROR_MESSAGE );
        }
        gene_tree.setRerootable( false );
        _mainpanel.getCurrentTreePanel().setTree( gene_tree );
        getControlPanel().setShowEvents( true );
        showWhole();
        JOptionPane.showMessageDialog( this,
                                       "Number of duplications: " + duplications,
                                       "GSDI successfully completed",
                                       JOptionPane.INFORMATION_MESSAGE );
    }

    void executeSDI() {
        if ( !isOKforSDI( true, true ) ) {
            return;
        }
        if ( !_mainpanel.getCurrentPhylogeny().isRooted() ) {
            JOptionPane.showMessageDialog( this,
                                           "Gene tree is not rooted",
                                           "Cannot execute SDI",
                                           JOptionPane.ERROR_MESSAGE );
            return;
        }
        final Phylogeny gene_tree = _mainpanel.getCurrentPhylogeny().copy();
        gene_tree.setAllNodesToNotCollapse();
        gene_tree.recalculateNumberOfExternalDescendants( false );
        SDI sdi = null;
        int duplications = -1;
        try {
            sdi = new SDIse( gene_tree, _species_tree.copy() );
            duplications = sdi.getDuplicationsSum();
        }
        catch ( final Exception e ) {
            JOptionPane.showMessageDialog( this, e.toString(), "Error during SDI", JOptionPane.ERROR_MESSAGE );
        }
        gene_tree.setRerootable( false );
        _mainpanel.getCurrentTreePanel().setTree( gene_tree );
        getControlPanel().setShowEvents( true );
        showWhole();
        JOptionPane.showMessageDialog( this,
                                       "Number of duplications: " + duplications,
                                       "SDI successfully completed",
                                       JOptionPane.INFORMATION_MESSAGE );
    }

    void executeSDIR( final boolean minimize_cost ) {
        if ( !isOKforSDI( true, true ) ) {
            return;
        }
        Phylogeny gene_tree = _mainpanel.getCurrentPhylogeny().copy();
        final SDIR sdiunrooted = new SDIR();
        gene_tree.setAllNodesToNotCollapse();
        gene_tree.recalculateNumberOfExternalDescendants( false );
        try {
            gene_tree = sdiunrooted.infer( gene_tree, _species_tree, minimize_cost, // minimize cost
                                           !minimize_cost, // minimize sum of dups
                                           true, // minimize height
                                           true, // return tree(s)
                                           1 )[ 0 ]; // # of trees to return
        }
        catch ( final Exception e ) {
            JOptionPane.showMessageDialog( this, e.toString(), "Error during SDIR", JOptionPane.ERROR_MESSAGE );
            return;
        }
        final int duplications = sdiunrooted.getMinimalDuplications();
        gene_tree.setRerootable( false );
        _mainpanel.getCurrentTreePanel().setTree( gene_tree );
        getControlPanel().setShowEvents( true );
        showWhole();
        JOptionPane.showMessageDialog( this,
                                       "Number of duplications: " + duplications,
                                       "SDIR successfully completed",
                                       JOptionPane.INFORMATION_MESSAGE );
    }

    @Override
    MainPanel getMainPanel() {
        return _mainpanel;
    }

    boolean isOKforSDI( final boolean species_tree_has_to_binary, final boolean gene_tree_has_to_binary ) {
        if ( ( _mainpanel.getCurrentPhylogeny() == null ) || _mainpanel.getCurrentPhylogeny().isEmpty() ) {
            return false;
        }
        else if ( ( _species_tree == null ) || _species_tree.isEmpty() ) {
            JOptionPane.showMessageDialog( this,
                                           "No species tree loaded",
                                           "Cannot execute SDI",
                                           JOptionPane.ERROR_MESSAGE );
            return false;
        }
        else if ( species_tree_has_to_binary && !_species_tree.isCompletelyBinary() ) {
            JOptionPane.showMessageDialog( this,
                                           "Species tree is not completely binary",
                                           "Cannot execute SDI",
                                           JOptionPane.ERROR_MESSAGE );
            return false;
        }
        else if ( gene_tree_has_to_binary && !_mainpanel.getCurrentPhylogeny().isCompletelyBinary() ) {
            JOptionPane.showMessageDialog( this,
                                           "Gene tree is not completely binary",
                                           "Cannot execute SDI",
                                           JOptionPane.ERROR_MESSAGE );
            return false;
        }
        else {
            return true;
        }
    }

    @Override
    void readPhylogeniesFromURL() {
        URL url = null;
        Phylogeny[] phys = null;
        final String message = "Please enter a complete URL, for example \"http://www.phyloxml.org/examples/apaf.xml\"";
        final String url_string = JOptionPane.showInputDialog( this,
                                                               message,
                                                               "Use URL/webservice to obtain a phylogeny",
                                                               JOptionPane.QUESTION_MESSAGE );
        boolean nhx_or_nexus = false;
        if ( ( url_string != null ) && ( url_string.length() > 0 ) ) {
            try {
                url = new URL( url_string );
                PhylogenyParser parser = null;
                if ( url.getHost().toLowerCase().indexOf( "tolweb" ) >= 0 ) {
                    parser = new TolParser();
                }
                else {
                    parser = ForesterUtil.createParserDependingOnUrlContents( url );
                }
                if ( parser instanceof NexusPhylogeniesParser ) {
                    nhx_or_nexus = true;
                }
                else if ( parser instanceof NHXParser ) {
                    nhx_or_nexus = true;
                }
                if ( _mainpanel.getCurrentTreePanel() != null ) {
                    _mainpanel.getCurrentTreePanel().setWaitCursor();
                }
                else {
                    _mainpanel.setWaitCursor();
                }
                final PhylogenyFactory factory = ParserBasedPhylogenyFactory.getInstance();
                phys = factory.create( url.openStream(), parser );
            }
            catch ( final MalformedURLException e ) {
                JOptionPane.showMessageDialog( this,
                                               "Malformed URL: " + url + "\n" + e.getLocalizedMessage(),
                                               "Malformed URL",
                                               JOptionPane.ERROR_MESSAGE );
            }
            catch ( final IOException e ) {
                JOptionPane.showMessageDialog( this,
                                               "Could not read from " + url + "\n" + e.getLocalizedMessage(),
                                               "Failed to read URL",
                                               JOptionPane.ERROR_MESSAGE );
            }
            catch ( final Exception e ) {
                e.printStackTrace();
                JOptionPane.showMessageDialog( this,
                                               e.getLocalizedMessage(),
                                               "Unexpected Exception",
                                               JOptionPane.ERROR_MESSAGE );
            }
            finally {
                if ( _mainpanel.getCurrentTreePanel() != null ) {
                    _mainpanel.getCurrentTreePanel().setArrowCursor();
                }
                else {
                    _mainpanel.setArrowCursor();
                }
            }
            if ( ( phys != null ) && ( phys.length > 0 ) ) {
                if ( nhx_or_nexus && getOptions().isInternalNumberAreConfidenceForNhParsing() ) {
                    for( final Phylogeny phy : phys ) {
                        ForesterUtil.transferInternalNodeNamesToConfidence( phy );
                    }
                }
                Util.addPhylogeniesToTabs( phys,
                                           new File( url.getFile() ).getName(),
                                           url.toString(),
                                           getConfiguration(),
                                           getMainPanel() );
                _mainpanel.getControlPanel().showWhole();
            }
        }
        activateSaveAllIfNeeded();
        System.gc();
    }

    private void choosePdfWidth() {
        final String s = ( String ) JOptionPane.showInputDialog( this,
                                                                 "Please enter the default line width for PDF export.\n"
                                                                         + "[current value: "
                                                                         + getOptions().getPrintLineWidth() + "]\n",
                                                                 "Line Width for PDF Export",
                                                                 JOptionPane.QUESTION_MESSAGE,
                                                                 null,
                                                                 null,
                                                                 getOptions().getPrintLineWidth() );
        if ( !ForesterUtil.isEmpty( s ) ) {
            boolean success = true;
            double d = 0.0;
            final String m_str = s.trim();
            if ( !ForesterUtil.isEmpty( m_str ) ) {
                try {
                    d = Double.parseDouble( m_str );
                }
                catch ( final Exception ex ) {
                    success = false;
                }
            }
            else {
                success = false;
            }
            if ( success && ( d > 0.0 ) ) {
                getOptions().setPrintLineWidth( d );
            }
        }
    }

    private void choosePrintSize() {
        final String s = ( String ) JOptionPane.showInputDialog( this,
                                                                 "Please enter values for width and height,\nseparated by a comma.\n"
                                                                         + "[current values: "
                                                                         + getOptions().getPrintSizeX() + ", "
                                                                         + getOptions().getPrintSizeY() + "]\n"
                                                                         + "[A4: " + Constants.A4_SIZE_X + ", "
                                                                         + Constants.A4_SIZE_Y + "]\n" + "[US Letter: "
                                                                         + Constants.US_LETTER_SIZE_X + ", "
                                                                         + Constants.US_LETTER_SIZE_Y + "]",
                                                                 "Default Size for Graphics Export",
                                                                 JOptionPane.QUESTION_MESSAGE,
                                                                 null,
                                                                 null,
                                                                 getOptions().getPrintSizeX() + ", "
                                                                         + getOptions().getPrintSizeY() );
        if ( !ForesterUtil.isEmpty( s ) && ( s.indexOf( ',' ) > 0 ) ) {
            boolean success = true;
            int x = 0;
            int y = 0;
            final String[] str_ary = s.split( "," );
            if ( str_ary.length == 2 ) {
                final String x_str = str_ary[ 0 ].trim();
                final String y_str = str_ary[ 1 ].trim();
                if ( !ForesterUtil.isEmpty( x_str ) && !ForesterUtil.isEmpty( y_str ) ) {
                    try {
                        x = Integer.parseInt( x_str );
                        y = Integer.parseInt( y_str );
                    }
                    catch ( final Exception ex ) {
                        success = false;
                    }
                }
                else {
                    success = false;
                }
            }
            else {
                success = false;
            }
            if ( success && ( x > 1 ) && ( y > 1 ) ) {
                getOptions().setPrintSizeX( x );
                getOptions().setPrintSizeY( y );
            }
        }
    }

    private void closeCurrentPane() {
        getMainPanel().closeCurrentPane();
        activateSaveAllIfNeeded();
    }

    private ControlPanel getControlPanel() {
        return getMainPanel().getControlPanel();
    }

    private void print() {
        if ( ( getCurrentTreePanel() == null ) || ( getCurrentTreePanel().getPhylogeny() == null )
                || getCurrentTreePanel().getPhylogeny().isEmpty() ) {
            return;
        }
        if ( !getOptions().isPrintUsingActualSize() ) {
            getCurrentTreePanel().setParametersForPainting( getOptions().getPrintSizeX() - 80,
                                                            getOptions().getPrintSizeY() - 140,
                                                            true );
            getCurrentTreePanel().resetPreferredSize();
            getCurrentTreePanel().repaint();
        }
        final String job_name = Constants.PRG_NAME;
        boolean error = false;
        String printer_name = null;
        try {
            printer_name = Printer.print( getCurrentTreePanel(), job_name );
        }
        catch ( final Exception e ) {
            error = true;
            JOptionPane.showMessageDialog( this, e.getMessage(), "Printing Error", JOptionPane.ERROR_MESSAGE );
        }
        if ( !error && ( printer_name != null ) ) {
            String msg = "Printing data sent to printer";
            if ( printer_name.length() > 1 ) {
                msg += " [" + printer_name + "]";
            }
            JOptionPane.showMessageDialog( this, msg, "Printing...", JOptionPane.INFORMATION_MESSAGE );
        }
        if ( !getOptions().isPrintUsingActualSize() ) {
            getControlPanel().showWhole();
        }
    }

    private void printPhylogenyToPdf( final String file_name ) {
        if ( !getOptions().isPrintUsingActualSize() ) {
            getCurrentTreePanel().setParametersForPainting( getOptions().getPrintSizeX(),
                                                            getOptions().getPrintSizeY(),
                                                            true );
            getCurrentTreePanel().resetPreferredSize();
            getCurrentTreePanel().repaint();
        }
        String pdf_written_to = "";
        boolean error = false;
        try {
            if ( getOptions().isPrintUsingActualSize() ) {
                pdf_written_to = PdfWriter.writePhylogenyToPdf( file_name,
                                                                getCurrentTreePanel(),
                                                                getOptions(),
                                                                getCurrentTreePanel().getWidth(),
                                                                getCurrentTreePanel().getHeight() );
            }
            else {
                pdf_written_to = PdfWriter.writePhylogenyToPdf( file_name,
                                                                getMainPanel().getCurrentTreePanel(),
                                                                getOptions(),
                                                                getOptions().getPrintSizeX(),
                                                                getOptions().getPrintSizeY() );
            }
        }
        catch ( final IOException e ) {
            error = true;
            JOptionPane.showMessageDialog( this, e.toString(), "Error", JOptionPane.ERROR_MESSAGE );
        }
        if ( !error ) {
            if ( !ForesterUtil.isEmpty( pdf_written_to ) ) {
                JOptionPane.showMessageDialog( this,
                                               "Wrote PDF to: " + pdf_written_to,
                                               "Information",
                                               JOptionPane.INFORMATION_MESSAGE );
            }
            else {
                JOptionPane.showMessageDialog( this,
                                               "There was an unknown problem when attempting to write to PDF file: \""
                                                       + file_name + "\"",
                                               "Error",
                                               JOptionPane.ERROR_MESSAGE );
            }
        }
        if ( !getOptions().isPrintUsingActualSize() ) {
            getControlPanel().showWhole();
        }
    }

    private void printToPdf( final Phylogeny t ) {
        if ( ( t == null ) || t.isEmpty() ) {
            return;
        }
        if ( ( getMainPanel().getCurrentTreePanel().getPhylogenyGraphicsType() == PHYLOGENY_GRAPHICS_TYPE.CONVEX )
                || ( getMainPanel().getCurrentTreePanel().getPhylogenyGraphicsType() == PHYLOGENY_GRAPHICS_TYPE.CURVED ) ) {
            JOptionPane.showMessageDialog( this,
                                           "Cannot export this graphic type to PDF.",
                                           "Cannot export to PDF",
                                           JOptionPane.ERROR_MESSAGE );
            return;
        }
        String initial_filename = "";
        if ( getMainPanel().getCurrentTreePanel().getTreeFile() != null ) {
            initial_filename = getMainPanel().getCurrentTreePanel().getTreeFile().toString();
        }
        if ( initial_filename.indexOf( '.' ) > 0 ) {
            initial_filename = initial_filename.substring( 0, initial_filename.indexOf( '.' ) );
        }
        initial_filename = initial_filename + ".pdf";
        _writetopdf_filechooser.setSelectedFile( new File( initial_filename ) );
        final int result = _writetopdf_filechooser.showSaveDialog( _contentpane );
        File file = _writetopdf_filechooser.getSelectedFile();
        if ( ( file != null ) && ( result == JFileChooser.APPROVE_OPTION ) ) {
            if ( !file.toString().toLowerCase().endsWith( ".pdf" ) ) {
                file = new File( file.toString() + ".pdf" );
            }
            if ( file.exists() ) {
                final int i = JOptionPane.showConfirmDialog( this,
                                                             file + " already exists. Overwrite?",
                                                             "WARNING",
                                                             JOptionPane.OK_CANCEL_OPTION,
                                                             JOptionPane.WARNING_MESSAGE );
                if ( i != JOptionPane.OK_OPTION ) {
                    return;
                }
            }
            printPhylogenyToPdf( file.toString() );
        }
    }

    private void readPhylogeniesFromFile() {
        boolean exception = false;
        Phylogeny[] phys = null;
        // Set an initial directory if none set yet
        if ( _open_dir == null ) {
            if ( System.getProperty( "user.home" ) != null ) {
                _open_dir = new File( System.getProperty( "user.home" ) );
            }
            else if ( System.getProperty( "user.dir" ) != null ) {
                _open_dir = new File( System.getProperty( "user.dir" ) );
            }
        }
        _open_filechooser.setMultiSelectionEnabled( true );
        // Open file-open dialog and set current directory
        if ( _open_dir != null ) {
            _open_filechooser.setCurrentDirectory( _open_dir );
        }
        final int result = _open_filechooser.showOpenDialog( _contentpane );
        // All done: get the file
        final File[] files = _open_filechooser.getSelectedFiles();
        _open_dir = _open_filechooser.getCurrentDirectory();
        boolean nhx_or_nexus = false;
        if ( ( files != null ) && ( files.length > 0 ) && ( result == JFileChooser.APPROVE_OPTION ) ) {
            for( final File file : files ) {
                if ( ( file != null ) && !file.isDirectory() ) {
                    if ( _mainpanel.getCurrentTreePanel() != null ) {
                        _mainpanel.getCurrentTreePanel().setWaitCursor();
                    }
                    else {
                        _mainpanel.setWaitCursor();
                    }
                    if ( ( _open_filechooser.getFileFilter() == MainFrameApplication.nhfilter )
                            || ( _open_filechooser.getFileFilter() == MainFrameApplication.nhxfilter ) ) {
                        try {
                            final NHXParser parser = new NHXParser();
                            phys = Util.readPhylogenies( parser, file );
                            nhx_or_nexus = true;
                        }
                        catch ( final Exception e ) {
                            e.printStackTrace();
                            exception = true;
                            exceptionOccuredDuringOpenFile( e );
                        }
                    }
                    else if ( _open_filechooser.getFileFilter() == MainFrameApplication.xmlfilter ) {
                        try {
                            phys = Util.readPhylogenies( new PhyloXmlParser(), file );
                        }
                        catch ( final Exception e ) {
                            exception = true;
                            exceptionOccuredDuringOpenFile( e );
                        }
                    }
                    else if ( _open_filechooser.getFileFilter() == MainFrameApplication.tolfilter ) {
                        try {
                            phys = Util.readPhylogenies( new TolParser(), file );
                        }
                        catch ( final Exception e ) {
                            exception = true;
                            exceptionOccuredDuringOpenFile( e );
                        }
                    }
                    else if ( _open_filechooser.getFileFilter() == MainFrameApplication.nexusfilter ) {
                        try {
                            final NexusPhylogeniesParser parser = new NexusPhylogeniesParser();
                            phys = Util.readPhylogenies( parser, file );
                            nhx_or_nexus = true;
                        }
                        catch ( final Exception e ) {
                            exception = true;
                            exceptionOccuredDuringOpenFile( e );
                        }
                    }
                    // "*.*":
                    else {
                        try {
                            final PhylogenyParser parser = ForesterUtil.createParserDependingOnFileType( file );
                            if ( parser instanceof NexusPhylogeniesParser ) {
                                nhx_or_nexus = true;
                            }
                            else if ( parser instanceof NHXParser ) {
                                nhx_or_nexus = true;
                            }
                            phys = Util.readPhylogenies( parser, file );
                        }
                        catch ( final Exception e ) {
                            exception = true;
                            exceptionOccuredDuringOpenFile( e );
                        }
                    }
                    if ( _mainpanel.getCurrentTreePanel() != null ) {
                        _mainpanel.getCurrentTreePanel().setArrowCursor();
                    }
                    else {
                        _mainpanel.setArrowCursor();
                    }
                    if ( !exception && ( phys != null ) && ( phys.length > 0 ) ) {
                        if ( nhx_or_nexus && getOptions().isInternalNumberAreConfidenceForNhParsing() ) {
                            for( final Phylogeny phy : phys ) {
                                ForesterUtil.transferInternalNodeNamesToConfidence( phy );
                            }
                        }
                        Util.addPhylogeniesToTabs( phys,
                                                   file.getName(),
                                                   file.getAbsolutePath(),
                                                   getConfiguration(),
                                                   getMainPanel() );
                        _mainpanel.getControlPanel().showWhole();
                    }
                }
            }
        }
        activateSaveAllIfNeeded();
        System.gc();
    }

    private void readSpeciesTreeFromFile() {
        Phylogeny t = null;
        boolean exception = false;
        if ( _open_dir == null ) {
            if ( System.getProperty( "user.home" ) != null ) {
                _open_dir = new File( System.getProperty( "user.home" ) );
            }
            else if ( System.getProperty( "user.dir" ) != null ) {
                _open_dir = new File( System.getProperty( "user.dir" ) );
            }
        }
        _open_filechooser_for_species_tree.setSelectedFile( new File( "" ) );
        if ( _open_dir != null ) {
            _open_filechooser_for_species_tree.setCurrentDirectory( _open_dir );
        }
        final int result = _open_filechooser_for_species_tree.showOpenDialog( _contentpane );
        final File file = _open_filechooser_for_species_tree.getSelectedFile();
        if ( ( file != null ) && ( result == JFileChooser.APPROVE_OPTION ) ) {
            if ( _open_filechooser_for_species_tree.getFileFilter() == MainFrameApplication.xmlfilter ) {
                try {
                    final Phylogeny[] trees = Util.readPhylogenies( new PhyloXmlParser(), file );
                    t = trees[ 0 ];
                }
                catch ( final Exception e ) {
                    exception = true;
                    exceptionOccuredDuringOpenFile( e );
                }
            }
            else if ( _open_filechooser_for_species_tree.getFileFilter() == MainFrameApplication.tolfilter ) {
                try {
                    final Phylogeny[] trees = Util.readPhylogenies( new TolParser(), file );
                    t = trees[ 0 ];
                }
                catch ( final Exception e ) {
                    exception = true;
                    exceptionOccuredDuringOpenFile( e );
                }
            }
            // "*.*":
            else {
                try {
                    final Phylogeny[] trees = Util.readPhylogenies( new PhyloXmlParser(), file );
                    t = trees[ 0 ];
                }
                catch ( final Exception e ) {
                    exception = true;
                    exceptionOccuredDuringOpenFile( e );
                }
            }
            if ( !exception && ( t != null ) && !t.isRooted() ) {
                exception = true;
                t = null;
                JOptionPane.showMessageDialog( this,
                                               "Species tree is not rooted",
                                               "Species tree not loaded",
                                               JOptionPane.ERROR_MESSAGE );
            }
            if ( !exception && ( t != null ) ) {
                final Set<Taxonomy> tax_set = new HashSet<Taxonomy>();
                for( final PhylogenyNodeIterator it = t.iteratorExternalForward(); it.hasNext(); ) {
                    final PhylogenyNode node = it.next();
                    if ( !node.getNodeData().isHasTaxonomy() ) {
                        exception = true;
                        t = null;
                        JOptionPane
                                .showMessageDialog( this,
                                                    "Species tree contains external node(s) without taxonomy information",
                                                    "Species tree not loaded",
                                                    JOptionPane.ERROR_MESSAGE );
                        break;
                    }
                    else {
                        if ( tax_set.contains( node.getNodeData().getTaxonomy() ) ) {
                            exception = true;
                            t = null;
                            JOptionPane.showMessageDialog( this,
                                                           "Taxonomy ["
                                                                   + node.getNodeData().getTaxonomy().asSimpleText()
                                                                   + "] is not unique in species tree",
                                                           "Species tree not loaded",
                                                           JOptionPane.ERROR_MESSAGE );
                            break;
                        }
                        else {
                            tax_set.add( node.getNodeData().getTaxonomy() );
                        }
                    }
                }
            }
            if ( !exception && ( t != null ) ) {
                _species_tree = t;
                JOptionPane.showMessageDialog( this,
                                               "Species tree successfully loaded",
                                               "Species tree loaded",
                                               JOptionPane.INFORMATION_MESSAGE );
            }
            _contentpane.repaint();
            System.gc();
        }
    }

    private void writeAllToFile() {
        if ( ( getMainPanel().getTabbedPane() == null ) || ( getMainPanel().getTabbedPane().getTabCount() < 1 ) ) {
            return;
        }
        if ( _open_dir != null ) {
            _save_filechooser.setCurrentDirectory( _open_dir );
        }
        else {
            File dir = null;
            if ( System.getProperty( "user.home" ) != null ) {
                dir = new File( System.getProperty( "user.home" ) );
            }
            else if ( System.getProperty( "user.dir" ) != null ) {
                dir = new File( System.getProperty( "user.dir" ) );
            }
            _save_filechooser.setCurrentDirectory( dir );
        }
        _save_filechooser.setSelectedFile( new File( "" ) );
        final int result = _save_filechooser.showSaveDialog( _contentpane );
        final File file = _save_filechooser.getSelectedFile();
        if ( ( file != null ) && ( result == JFileChooser.APPROVE_OPTION ) ) {
            if ( file.exists() ) {
                final int i = JOptionPane.showConfirmDialog( this,
                                                             file + " already exists. Overwrite?",
                                                             "Warning",
                                                             JOptionPane.OK_CANCEL_OPTION,
                                                             JOptionPane.WARNING_MESSAGE );
                if ( i != JOptionPane.OK_OPTION ) {
                    return;
                }
            }
            final int count = getMainPanel().getTabbedPane().getTabCount();
            final List<Phylogeny> trees = new ArrayList<Phylogeny>();
            for( int i = 0; i < count; ++i ) {
                trees.add( getMainPanel().getPhylogeny( i ) );
            }
            final PhylogenyWriter writer = new PhylogenyWriter();
            try {
                writer.toPhyloXML( file, trees, 0, ForesterUtil.LINE_SEPARATOR );
            }
            catch ( final IOException e ) {
                JOptionPane.showMessageDialog( this,
                                               "Failed to write to: " + file,
                                               "Error",
                                               JOptionPane.WARNING_MESSAGE );
            }
        }
    }

    private boolean writeAsNewHampshire( final Phylogeny t, boolean exception, final File file ) {
        try {
            final PhylogenyWriter writer = new PhylogenyWriter();
            writer.toNewHampshire( t, false, true, file );
        }
        catch ( final Exception e ) {
            exception = true;
            exceptionOccuredDuringSaveAs( e );
        }
        return exception;
    }

    private boolean writeAsNexus( final Phylogeny t, boolean exception, final File file ) {
        try {
            final PhylogenyWriter writer = new PhylogenyWriter();
            writer.toNexus( file, t );
        }
        catch ( final Exception e ) {
            exception = true;
            exceptionOccuredDuringSaveAs( e );
        }
        return exception;
    }

    private boolean writeAsNHX( final Phylogeny t, boolean exception, final File file ) {
        try {
            final PhylogenyWriter writer = new PhylogenyWriter();
            writer.toNewHampshireX( t, file );
        }
        catch ( final Exception e ) {
            exception = true;
            exceptionOccuredDuringSaveAs( e );
        }
        return exception;
    }

    private boolean writeAsPhyloXml( final Phylogeny t, boolean exception, final File file ) {
        try {
            final PhylogenyWriter writer = new PhylogenyWriter();
            writer.toPhyloXML( file, t, 0 );
        }
        catch ( final Exception e ) {
            exception = true;
            exceptionOccuredDuringSaveAs( e );
        }
        return exception;
    }

    private void writePhylogenyToGraphicsFile( final String file_name, final GraphicsExportType type ) {
        _mainpanel.getCurrentTreePanel().setParametersForPainting( _mainpanel.getCurrentTreePanel().getWidth(),
                                                                   _mainpanel.getCurrentTreePanel().getHeight(),
                                                                   true );
        String file_written_to = "";
        boolean error = false;
        try {
            file_written_to = Util.writePhylogenyToGraphicsFile( file_name,
                                                                 _mainpanel.getCurrentTreePanel().getWidth(),
                                                                 _mainpanel.getCurrentTreePanel().getHeight(),
                                                                 _mainpanel.getCurrentTreePanel(),
                                                                 _mainpanel.getControlPanel(),
                                                                 type,
                                                                 getOptions() );
        }
        catch ( final IOException e ) {
            error = true;
            JOptionPane.showMessageDialog( this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE );
        }
        if ( !error ) {
            if ( ( file_written_to != null ) && ( file_written_to.length() > 0 ) ) {
                JOptionPane.showMessageDialog( this,
                                               "Wrote image to: " + file_written_to,
                                               "Graphics Export",
                                               JOptionPane.INFORMATION_MESSAGE );
            }
            else {
                JOptionPane.showMessageDialog( this,
                                               "There was an unknown problem when attempting to write to an image file: \""
                                                       + file_name + "\"",
                                               "Error",
                                               JOptionPane.ERROR_MESSAGE );
            }
        }
        _contentpane.repaint();
    }

    private void writeToFile( final Phylogeny t ) {
        if ( t == null ) {
            return;
        }
        String initial_filename = null;
        if ( getMainPanel().getCurrentTreePanel().getTreeFile() != null ) {
            try {
                initial_filename = getMainPanel().getCurrentTreePanel().getTreeFile().getCanonicalPath();
            }
            catch ( final IOException e ) {
                initial_filename = null;
            }
        }
        if ( !ForesterUtil.isEmpty( initial_filename ) ) {
            _save_filechooser.setSelectedFile( new File( initial_filename ) );
        }
        else {
            _save_filechooser.setSelectedFile( new File( "" ) );
        }
        if ( _open_dir != null ) {
            _save_filechooser.setCurrentDirectory( _open_dir );
        }
        else {
            File dir = null;
            if ( System.getProperty( "user.home" ) != null ) {
                dir = new File( System.getProperty( "user.home" ) );
            }
            else if ( System.getProperty( "user.dir" ) != null ) {
                dir = new File( System.getProperty( "user.dir" ) );
            }
            _save_filechooser.setCurrentDirectory( dir );
        }
        final int result = _save_filechooser.showSaveDialog( _contentpane );
        final File file = _save_filechooser.getSelectedFile();
        boolean exception = false;
        if ( ( file != null ) && ( result == JFileChooser.APPROVE_OPTION ) ) {
            if ( file.exists() ) {
                final int i = JOptionPane.showConfirmDialog( this,
                                                             file + " already exists. Overwrite?",
                                                             "Warning",
                                                             JOptionPane.OK_CANCEL_OPTION,
                                                             JOptionPane.WARNING_MESSAGE );
                if ( i != JOptionPane.OK_OPTION ) {
                    return;
                }
            }
            if ( _save_filechooser.getFileFilter() == MainFrameApplication.nhfilter ) {
                exception = writeAsNewHampshire( t, exception, file );
            }
            else if ( _save_filechooser.getFileFilter() == MainFrameApplication.nhxfilter ) {
                exception = writeAsNHX( t, exception, file );
            }
            else if ( _save_filechooser.getFileFilter() == MainFrameApplication.xmlfilter ) {
                exception = writeAsPhyloXml( t, exception, file );
            }
            else if ( _save_filechooser.getFileFilter() == MainFrameApplication.nexusfilter ) {
                exception = writeAsNexus( t, exception, file );
            }
            // "*.*":
            else {
                final String file_name = file.getName().trim().toLowerCase();
                if ( file_name.endsWith( ".nh" ) || file_name.endsWith( ".newick" ) || file_name.endsWith( ".phy" )
                        || file_name.endsWith( ".tree" ) ) {
                    exception = writeAsNewHampshire( t, exception, file );
                }
                else if ( file_name.endsWith( ".nhx" ) ) {
                    exception = writeAsNHX( t, exception, file );
                }
                else if ( file_name.endsWith( ".nex" ) || file_name.endsWith( ".nexus" ) ) {
                    exception = writeAsNexus( t, exception, file );
                }
                // XML is default:
                else {
                    exception = writeAsPhyloXml( t, exception, file );
                }
            }
            if ( !exception ) {
                getMainPanel().getCurrentTreePanel().setTreeFile( file );
            }
        }
    }

    private void writeToGraphicsFile( final Phylogeny t, final GraphicsExportType type ) {
        if ( ( t == null ) || t.isEmpty() ) {
            return;
        }
        String initial_filename = "";
        if ( getMainPanel().getCurrentTreePanel().getTreeFile() != null ) {
            initial_filename = getMainPanel().getCurrentTreePanel().getTreeFile().toString();
        }
        if ( initial_filename.indexOf( '.' ) > 0 ) {
            initial_filename = initial_filename.substring( 0, initial_filename.indexOf( '.' ) );
        }
        initial_filename = initial_filename + "." + type;
        _writetographics_filechooser.setSelectedFile( new File( initial_filename ) );
        final int result = _writetographics_filechooser.showSaveDialog( _contentpane );
        File file = _writetographics_filechooser.getSelectedFile();
        if ( ( file != null ) && ( result == JFileChooser.APPROVE_OPTION ) ) {
            if ( !file.toString().toLowerCase().endsWith( type.toString() ) ) {
                file = new File( file.toString() + "." + type );
            }
            if ( file.exists() ) {
                final int i = JOptionPane.showConfirmDialog( this,
                                                             file + " already exists. Overwrite?",
                                                             "Warning",
                                                             JOptionPane.OK_CANCEL_OPTION,
                                                             JOptionPane.WARNING_MESSAGE );
                if ( i != JOptionPane.OK_OPTION ) {
                    return;
                }
            }
            writePhylogenyToGraphicsFile( file.toString(), type );
        }
    }

    static MainFrame createInstance( final Phylogeny[] phys, final String config_file, final String title ) {
        return new MainFrameApplication( phys, config_file, title );
    }

    //    static void keyPressed( final KeyEvent e, final MainFrame mf ) {
    //        // You should only rely on the key char if the event
    //        // is a key typed event.
    //        //final int id = e.getID();
    //        String keyString;
    //        final int keyCode = e.getKeyCode();
    //        keyString = "key code = " + keyCode + " (" + KeyEvent.getKeyText( keyCode ) + ")";
    //        final int modifiersEx = e.getModifiersEx();
    //        String modString = "extended modifiers = " + modifiersEx;
    //        final String tmpString = KeyEvent.getModifiersExText( modifiersEx );
    //        if ( tmpString.length() > 0 ) {
    //            modString += " (" + tmpString + ")";
    //        }
    //        else {
    //            modString += " (no extended modifiers)";
    //        }
    //        String actionString = "action key? ";
    //        if ( e.isActionKey() ) {
    //            actionString += "YES";
    //        }
    //        else {
    //            actionString += "NO";
    //        }
    //        String locationString = "key location: ";
    //        final int location = e.getKeyLocation();
    //        if ( location == KeyEvent.KEY_LOCATION_STANDARD ) {
    //            locationString += "standard";
    //        }
    //        else if ( location == KeyEvent.KEY_LOCATION_LEFT ) {
    //            locationString += "left";
    //        }
    //        else if ( location == KeyEvent.KEY_LOCATION_RIGHT ) {
    //            locationString += "right";
    //        }
    //        else if ( location == KeyEvent.KEY_LOCATION_NUMPAD ) {
    //            locationString += "numpad";
    //        }
    //        else { // (location == KeyEvent.KEY_LOCATION_UNKNOWN)
    //            locationString += "unknown";
    //        }
    //        final StringBuffer sb = new StringBuffer();
    //        sb.append( keyString + " " + modString + " " + actionString + " " + locationString );
    //        //System.out.println( sb.toString() );
    //        if ( e.getModifiersEx() == Constants.STRG_KEY_CODE ) {
    //            if ( e.getKeyCode() == Constants.BACK_SLASH_KEY_CODE ) {
    //                mf.getMainPanel().getTreeFontSet().mediumFonts();
    //                mf.getMainPanel().getControlPanel().displayedPhylogenyMightHaveChanged( true );
    //            }
    //            else if ( e.getKeyCode() == Constants.MINUS_KEY_CODE ) {
    //                mf.getMainPanel().getTreeFontSet().decreaseFontSize();
    //                mf.getMainPanel().getControlPanel().displayedPhylogenyMightHaveChanged( true );
    //            }
    //            else if ( e.getKeyCode() == Constants.PLUS_KEY_CODE ) {
    //                mf.getMainPanel().getTreeFontSet().increaseFontSize();
    //                mf.getMainPanel().getControlPanel().displayedPhylogenyMightHaveChanged( true );
    //            }
    //            else if ( e.getKeyCode() == Constants.UP_KEY_CODE ) {
    //                mf.getMainPanel().getControlPanel().zoomInY( Constants.WHEEL_ZOOM_IN_FACTOR );
    //                mf.getMainPanel().getControlPanel().displayedPhylogenyMightHaveChanged( false );
    //            }
    //            else if ( e.getKeyCode() == Constants.DOWN_KEY_CODE ) {
    //                mf.getMainPanel().getControlPanel().zoomOutY( Constants.WHEEL_ZOOM_OUT_FACTOR );
    //                mf.getMainPanel().getControlPanel().displayedPhylogenyMightHaveChanged( false );
    //            }
    //            else if ( e.getKeyCode() == Constants.LEFT_KEY_CODE ) {
    //                mf.getMainPanel().getControlPanel().zoomOutX( Constants.WHEEL_ZOOM_OUT_FACTOR,
    //                                                              Constants.WHEEL_ZOOM_OUT_X_CORRECTION_FACTOR );
    //                mf.getMainPanel().getControlPanel().displayedPhylogenyMightHaveChanged( false );
    //            }
    //            else if ( e.getKeyCode() == Constants.RIGHT_KEY_CODE ) {
    //                mf.getMainPanel().getControlPanel().zoomInX( Constants.WHEEL_ZOOM_IN_FACTOR,
    //                                                             Constants.WHEEL_ZOOM_IN_FACTOR );
    //                mf.getMainPanel().getControlPanel().displayedPhylogenyMightHaveChanged( false );
    //            }
    //        }
    //        else {
    //            if ( e.getKeyCode() == Constants.BACK_SLASH_KEY_CODE ) {
    //                mf.showWhole();
    //            }
    //            else if ( ( e.getKeyCode() == Constants.UP_KEY_CODE ) || ( e.getKeyCode() == Constants.DOWN_KEY_CODE )
    //                    || ( e.getKeyCode() == Constants.LEFT_KEY_CODE ) || ( e.getKeyCode() == Constants.RIGHT_KEY_CODE ) ) {
    //                int d = 80;
    //                if ( e.getModifiersEx() == Constants.UMSCHALT_KEY_CODE ) {
    //                    d = 10;
    //                }
    //                int dx = 0;
    //                int dy = -d;
    //                if ( e.getKeyCode() == Constants.DOWN_KEY_CODE ) {
    //                    dy = d;
    //                }
    //                else if ( e.getKeyCode() == Constants.LEFT_KEY_CODE ) {
    //                    dx = -d;
    //                    dy = 0;
    //                }
    //                else if ( e.getKeyCode() == Constants.RIGHT_KEY_CODE ) {
    //                    dx = d;
    //                    dy = 0;
    //                }
    //                final Point scroll_position = mf.getMainPanel().getCurrentScrollPane().getViewport().getViewPosition();
    //                scroll_position.x = scroll_position.x + dx;
    //                scroll_position.y = scroll_position.y + dy;
    //                if ( scroll_position.x <= 0 ) {
    //                    scroll_position.x = 0;
    //                    dx = 0;
    //                }
    //                else {
    //                    final int max_x = mf.getMainPanel().getCurrentScrollPane().getHorizontalScrollBar().getMaximum()
    //                            - mf.getMainPanel().getCurrentScrollPane().getHorizontalScrollBar().getVisibleAmount();
    //                    if ( scroll_position.x >= max_x ) {
    //                        dx = 0;
    //                        scroll_position.x = max_x;
    //                    }
    //                }
    //                if ( scroll_position.y <= 0 ) {
    //                    dy = 0;
    //                    scroll_position.y = 0;
    //                }
    //                else {
    //                    final int max_y = mf.getMainPanel().getCurrentScrollPane().getVerticalScrollBar().getMaximum()
    //                            - mf.getMainPanel().getCurrentScrollPane().getVerticalScrollBar().getVisibleAmount();
    //                    if ( scroll_position.y >= max_y ) {
    //                        dy = 0;
    //                        scroll_position.y = max_y;
    //                    }
    //                }
    //                mf.repaint();
    //                mf.getMainPanel().getCurrentScrollPane().getViewport().setViewPosition( scroll_position );
    //                //   mf.setLastMouseDragPointX( ( float ) ( e.getX() + dx ) );
    //                //   mf.setLastMouseDragPointY( ( float ) ( e.getY() + dy ) );            
    //            }
    //            else if ( e.getKeyCode() == Constants.MINUS_KEY_CODE ) {
    //                mf.getMainPanel().getControlPanel().zoomOutY( Constants.WHEEL_ZOOM_OUT_FACTOR );
    //                mf.getMainPanel().getControlPanel().zoomOutX( Constants.WHEEL_ZOOM_OUT_FACTOR,
    //                                                              Constants.WHEEL_ZOOM_OUT_X_CORRECTION_FACTOR );
    //                mf.getMainPanel().getControlPanel().displayedPhylogenyMightHaveChanged( false );
    //            }
    //            else if ( e.getKeyCode() == Constants.PLUS_KEY_CODE ) {
    //                mf.getMainPanel().getControlPanel().zoomInX( Constants.WHEEL_ZOOM_IN_FACTOR,
    //                                                             Constants.WHEEL_ZOOM_IN_FACTOR );
    //                mf.getMainPanel().getControlPanel().zoomInY( Constants.WHEEL_ZOOM_IN_FACTOR );
    //                mf.getMainPanel().getControlPanel().displayedPhylogenyMightHaveChanged( false );
    //            }
    //        }
    //        //TODO FIXME implemt me...
    //        //        if ( need to get coords of cursor, like: mf.getCurrentTreePanel().inOvVirtualRectangle( e ) ) {
    //        //        if ( !mf.getCurrentTreePanel().isInOvRect() ) {
    //        //            mf.getCurrentTreePanel().setInOvRect( true );
    //        //            mf.repaint();
    //        //        }
    //        //        else 
    //        //       if (  mf.getCurrentTreePanel().isInOvRect() ) {
    //        //            mf.getCurrentTreePanel().setInOvRect( false );
    //        //            mf.repaint();
    //        //        }
    //        //        }
    //  }
    static void setTextForGraphicsSizeChooserMenuItem( final JMenuItem mi, final Options o ) {
        mi.setText( "Enter Default Size for Graphics Export... (current: " + o.getPrintSizeX() + ", "
                + o.getPrintSizeY() + ")" );
    }

    static void setTextForPdfLineWidthChooserMenuItem( final JMenuItem mi, final Options o ) {
        mi.setText( "Enter Default Line Width for PDF Export... (current: " + o.getPrintLineWidth() + ")" );
    }
} // MainFrameApplication.

class DefaultFilter extends FileFilter {

    @Override
    public boolean accept( final File f ) {
        final String file_name = f.getName().trim().toLowerCase();
        return file_name.endsWith( ".nh" ) || file_name.endsWith( ".newick" ) || file_name.endsWith( ".phy" )
                || file_name.endsWith( ".tr" ) || file_name.endsWith( ".dnd" ) || file_name.endsWith( ".tree" )
                || file_name.endsWith( ".nhx" ) || file_name.endsWith( ".xml" ) || file_name.endsWith( ".phyloxml" )
                || file_name.endsWith( ".px" ) || file_name.endsWith( ".pxml" ) || file_name.endsWith( ".nexus" )
                || file_name.endsWith( ".nx" ) || file_name.endsWith( ".nex" ) || file_name.endsWith( ".tre" )
                || file_name.endsWith( ".zip" ) || file_name.endsWith( ".tol" ) || file_name.endsWith( ".tolxml" )
                || f.isDirectory();
    }

    @Override
    public String getDescription() {
        return "All supported files (*.xml, *.phyloxml, *.nhx, *.nh, *.newick, *.nex, *.nexus, *.phy, *.tre, *.tree, *.tol)";
    }
}

class GraphicsFileFilter extends FileFilter {

    @Override
    public boolean accept( final File f ) {
        final String file_name = f.getName().trim().toLowerCase();
        return file_name.endsWith( ".jpg" ) || file_name.endsWith( ".jpeg" ) || file_name.endsWith( ".png" )
                || file_name.endsWith( ".gif" ) || file_name.endsWith( ".bmp" ) || f.isDirectory();
    }

    @Override
    public String getDescription() {
        return "Image files (*.jpg, *.jpeg, *.png, *.gif, *.bmp)";
    }
} // XMLFilter

class NexusFilter extends FileFilter {

    @Override
    public boolean accept( final File f ) {
        final String file_name = f.getName().trim().toLowerCase();
        return file_name.endsWith( ".nex" ) || file_name.endsWith( ".nexus" ) || file_name.endsWith( ".nx" )
                || file_name.endsWith( ".tre" ) || f.isDirectory();
    }

    @Override
    public String getDescription() {
        return "Nexus files (*.nex, *.nexus, *.nx, *.tre)";
    }
} // NexusFilter

class NHFilter extends FileFilter {

    @Override
    public boolean accept( final File f ) {
        final String file_name = f.getName().trim().toLowerCase();
        return file_name.endsWith( ".nh" ) || file_name.endsWith( ".newick" ) || file_name.endsWith( ".phy" )
                || file_name.endsWith( ".tr" ) || file_name.endsWith( ".tree" ) || file_name.endsWith( ".dnd" )
                || f.isDirectory();
    }

    @Override
    public String getDescription() {
        return "New Hampshire - Newick files (*.nh, *.newick, *.phy, *.tree, *.dnd, *.tr)";
    }
} // NHFilter

class NHXFilter extends FileFilter {

    @Override
    public boolean accept( final File f ) {
        final String file_name = f.getName().trim().toLowerCase();
        return file_name.endsWith( ".nhx" ) || f.isDirectory();
    }

    @Override
    public String getDescription() {
        return "NHX files (*.nhx)";
    }
}

class PdfFilter extends FileFilter {

    @Override
    public boolean accept( final File f ) {
        return f.getName().trim().toLowerCase().endsWith( ".pdf" ) || f.isDirectory();
    }

    @Override
    public String getDescription() {
        return "PDF files (*.pdf)";
    }
} // PdfFilter

class TolFilter extends FileFilter {

    @Override
    public boolean accept( final File f ) {
        final String file_name = f.getName().trim().toLowerCase();
        return ( file_name.endsWith( ".tol" ) || file_name.endsWith( ".tolxml" ) || file_name.endsWith( ".zip" ) || f
                .isDirectory() )
                && ( !file_name.endsWith( ".xml.zip" ) );
    }

    @Override
    public String getDescription() {
        return "Tree of Life files (*.tol, *.tolxml)";
    }
} // TolFilter

class XMLFilter extends FileFilter {

    @Override
    public boolean accept( final File f ) {
        final String file_name = f.getName().trim().toLowerCase();
        return file_name.endsWith( ".xml" ) || file_name.endsWith( ".phyloxml" ) || file_name.endsWith( ".px" )
                || file_name.endsWith( ".pxml" ) || file_name.endsWith( ".zip" ) || f.isDirectory();
    }

    @Override
    public String getDescription() {
        return "phyloXML files (*.xml, *.phyloxml)";
    }
} // XMLFilter
