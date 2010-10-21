// $Id: MainFrameApplication.java,v 1.93 2010/10/11 19:34:33 cmzmasek Exp $
// FORESTER -- software libraries and applications
// for evolutionary biology research and applications.
//
// Copyright (C) 2008-2009 Christian M. Zmasek
// Copyright (C) 2008-2009 Burnham Institute for Medical Research
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
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import org.forester.archaeopteryx.Options.CLADOGRAM_TYPE;
import org.forester.archaeopteryx.Options.NODE_LABEL_DIRECTION;
import org.forester.archaeopteryx.Options.PHYLOGENY_GRAPHICS_TYPE;
import org.forester.archaeopteryx.Util.GraphicsExportType;
import org.forester.archaeopteryx.webservices.PhylogeniesWebserviceClient;
import org.forester.archaeopteryx.webservices.WebservicesManager;
import org.forester.io.parsers.FastaParser;
import org.forester.io.parsers.GeneralMsaParser;
import org.forester.io.parsers.PhylogenyParser;
import org.forester.io.parsers.nexus.NexusPhylogeniesParser;
import org.forester.io.parsers.nhx.NHXParser;
import org.forester.io.parsers.phyloxml.PhyloXmlParser;
import org.forester.io.parsers.tol.TolParser;
import org.forester.io.writers.PhylogenyWriter;
import org.forester.io.writers.SequenceWriter;
import org.forester.msa.Msa;
import org.forester.msa.MsaFormatException;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyMethods;
import org.forester.phylogeny.PhylogenyNode;
import org.forester.phylogeny.data.Confidence;
import org.forester.phylogeny.data.Taxonomy;
import org.forester.phylogeny.factories.ParserBasedPhylogenyFactory;
import org.forester.phylogeny.factories.PhylogenyFactory;
import org.forester.phylogeny.iterators.PhylogenyNodeIterator;
import org.forester.sdi.GSDI;
import org.forester.sdi.SDI;
import org.forester.sdi.SDIR;
import org.forester.sdi.SDIse;
import org.forester.sequence.Sequence;
import org.forester.util.ForesterUtil;
import org.forester.util.WindowsUtils;
import org.forester.util.ForesterUtil.PhylogenyNodeField;
import org.forester.util.ForesterUtil.TAXONOMY_EXTRACTION;

class DefaultFilter extends FileFilter {

    @Override
    public boolean accept( final File f ) {
        final String file_name = f.getName().trim().toLowerCase();
        return file_name.endsWith( ".nh" ) || file_name.endsWith( ".newick" ) || file_name.endsWith( ".phy" )
                || file_name.endsWith( ".nwk" ) || file_name.endsWith( ".phb" ) || file_name.endsWith( ".ph" )
                || file_name.endsWith( ".tr" ) || file_name.endsWith( ".dnd" ) || file_name.endsWith( ".tree" )
                || file_name.endsWith( ".nhx" ) || file_name.endsWith( ".xml" ) || file_name.endsWith( ".phyloxml" )
                || file_name.endsWith( "phylo.xml" ) || file_name.endsWith( ".pxml" ) || file_name.endsWith( ".nexus" )
                || file_name.endsWith( ".nx" ) || file_name.endsWith( ".nex" ) || file_name.endsWith( ".tre" )
                || file_name.endsWith( ".zip" ) || file_name.endsWith( ".tol" ) || file_name.endsWith( ".tolxml" )
                || file_name.endsWith( ".con" ) || f.isDirectory();
    }

    @Override
    public String getDescription() {
        return "All supported files (*.xml, *.phyloxml, *phylo.xml, *.nhx, *.nh, *.newick, *.nex, *.nexus, *.phy, *.tre, *.tree, *.tol, ...)";
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
}

class MsaFileFilter extends FileFilter {

    @Override
    public boolean accept( final File f ) {
        final String file_name = f.getName().trim().toLowerCase();
        return file_name.endsWith( ".msa" ) || file_name.endsWith( ".aln" ) || file_name.endsWith( ".fasta" )
                || file_name.endsWith( ".fas" ) || file_name.endsWith( ".fa" ) || f.isDirectory();
    }

    @Override
    public String getDescription() {
        return "Multiple sequence alignment files (*.msa, *.aln, *.fasta, *.fa, *.fas)";
    }
}

class SequencesFileFilter extends FileFilter {

    @Override
    public boolean accept( final File f ) {
        final String file_name = f.getName().trim().toLowerCase();
        return file_name.endsWith( ".fasta" ) || file_name.endsWith( ".fa" ) || file_name.endsWith( ".fas" ) || file_name.endsWith( ".seqs" )
                || f.isDirectory();
    }

    @Override
    public String getDescription() {
        return "Sequences files (*.fasta, *.fa, *.fas, *.seqs )";
    }
}

public final class MainFrameApplication extends MainFrame {

    private final static int                 FRAME_X_SIZE                    = 800;
    private final static int                 FRAME_Y_SIZE                    = 800;
    // Filters for the file-open dialog (classes defined in this file)
    private final static NHFilter            nhfilter                        = new NHFilter();
    private final static NHXFilter           nhxfilter                       = new NHXFilter();
    private final static XMLFilter           xmlfilter                       = new XMLFilter();
    private final static TolFilter           tolfilter                       = new TolFilter();
    private final static NexusFilter         nexusfilter                     = new NexusFilter();
    private final static PdfFilter           pdffilter                       = new PdfFilter();
    private final static GraphicsFileFilter  graphicsfilefilter              = new GraphicsFileFilter();
    private final static MsaFileFilter       msafilter                       = new MsaFileFilter();
    private final static SequencesFileFilter seqsfilter                      = new SequencesFileFilter();
    private final static DefaultFilter       defaultfilter                   = new DefaultFilter();
    private static final long                serialVersionUID                = -799735726778865234L;
    private final JFileChooser               _open_filechooser;
    private final JFileChooser               _msa_filechooser;
    private final JFileChooser               _seqs_filechooser;
    private final JFileChooser               _open_filechooser_for_species_tree;
    private final JFileChooser               _save_filechooser;
    private final JFileChooser               _writetopdf_filechooser;
    private final JFileChooser               _writetographics_filechooser;
    // Analysis menu
    private JMenu                            _analysis_menu;
    private JMenuItem                        _load_species_tree_item;
    private JMenuItem                        _sdi_item;
    private JMenuItem                        _gsdi_item;
    private JMenuItem                        _root_min_dups_item;
    private JMenuItem                        _root_min_cost_l_item;
    private JMenuItem                        _lineage_inference;
    // Application-only print menu items
    private JMenuItem                        _print_item;
    private JMenuItem                        _write_to_pdf_item;
    private JMenuItem                        _write_to_jpg_item;
    private JMenuItem                        _write_to_gif_item;
    private JMenuItem                        _write_to_tif_item;
    private JMenuItem                        _write_to_png_item;
    private JMenuItem                        _write_to_bmp_item;
    private Phylogeny                        _species_tree;
    private File                             _current_dir;
    private ButtonGroup                      _radio_group_1;
    // Others:
    double                                   _min_not_collapse               = Constants.MIN_NOT_COLLAPSE_DEFAULT;
    // Phylogeny Inference menu
    private JMenu                            _inference_menu;
    private JMenuItem                        _inference_from_msa_item;
    private JMenuItem                        _inference_from_seqs_item;
    // Phylogeny Inference
    private PhylogeneticInferenceOptions     _phylogenetic_inference_options = null;
    private Msa                              _msa                            = null;
    private File                             _msa_file                       = null;
    private List<Sequence>                              _seqs                            = null;
    private File                             _seqs_file                       = null;

    private MainFrameApplication( final Phylogeny[] phys, final Configuration config, final String title ) {
        _configuration = config;
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
        setPhylogeneticInferenceOptions( PhylogeneticInferenceOptions.createInstance( _configuration ) );
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
        // Msa:
        _msa_filechooser = new JFileChooser();
        _msa_filechooser.setName( "Read Multiple Sequence Alignment File" );
        _msa_filechooser.setCurrentDirectory( new File( "." ) );
        _msa_filechooser.setMultiSelectionEnabled( false );
        _msa_filechooser.addChoosableFileFilter( _msa_filechooser.getAcceptAllFileFilter() );
        _msa_filechooser.addChoosableFileFilter( MainFrameApplication.msafilter );
        // Seqs:
        _seqs_filechooser = new JFileChooser();
        _seqs_filechooser.setName( "Read Sequences File" );
        _seqs_filechooser.setCurrentDirectory( new File( "." ) );
        _seqs_filechooser.setMultiSelectionEnabled( false );
        _seqs_filechooser.addChoosableFileFilter( _seqs_filechooser.getAcceptAllFileFilter() );
        _seqs_filechooser.addChoosableFileFilter( MainFrameApplication.seqsfilter );
        // build the menu bar
        _jmenubar = new JMenuBar();
        if ( !_configuration.isUseNativeUI() ) {
            _jmenubar.setBackground( getConfiguration().getGuiMenuBackgroundColor() );
        }
        buildFileMenu();
        if ( Constants.__ALLOW_PHYLOGENETIC_INFERENCE ) {
            buildPhylogeneticInferenceMenu();
        }
        buildAnalysisMenu();
        buildToolsMenu();
        buildViewMenu();
        buildFontSizeMenu();
        buildOptionsMenu();
        buildTypeMenu();
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
        setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );
        addWindowListener( new WindowAdapter() {

            @Override
            public void windowClosing( final WindowEvent e ) {
                if ( isUnsavedDataPresent() ) {
                    final int r = JOptionPane.showConfirmDialog( null,
                                                                 "Exit despite potentially unsaved changes?",
                                                                 "Exit?",
                                                                 JOptionPane.YES_NO_OPTION );
                    if ( r != JOptionPane.YES_OPTION ) {
                        return;
                    }
                }
                else {
                    final int r = JOptionPane.showConfirmDialog( null,
                                                                 "Exit Archaeopteryx?",
                                                                 "Exit?",
                                                                 JOptionPane.YES_NO_OPTION );
                    if ( r != JOptionPane.YES_OPTION ) {
                        return;
                    }
                }
                exit();
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

    private MainFrameApplication( final Phylogeny[] phys, final String config_file, final String title ) {
        // Reads the config file (false, false => not url, not applet):
        this( phys, new Configuration( config_file, false, false ), title );
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
            else if ( o == _new_item ) {
                newTree();
            }
            else if ( o == _save_all_item ) {
                writeAllToFile();
            }
            else if ( o == _close_item ) {
                closeCurrentPane();
            }
            else if ( o == _write_to_pdf_item ) {
                writeToPdf( _mainpanel.getCurrentPhylogeny() );
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
            else if ( o == _lineage_inference ) {
                executeLineageInference();
            }
            else if ( o == _obtain_detailed_taxonomic_information_jmi ) {
                obtainDetailedTaxonomicInformation();
            }
            else if ( o == _move_node_names_to_tax_sn_jmi ) {
                moveNodeNamesToTaxSn();
            }
            else if ( o == _move_node_names_to_seq_names_jmi ) {
                moveNodeNamesToSeqNames();
            }
            else if ( o == _extract_tax_code_from_node_names_jmi ) {
                extractTaxCodeFromNodeNames();
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
            else if ( o == _replace_underscores_cbmi ) {
                if ( ( _extract_pfam_style_tax_codes_cbmi != null ) && _replace_underscores_cbmi.isSelected() ) {
                    _extract_pfam_style_tax_codes_cbmi.setSelected( false );
                }
                updateOptions( getOptions() );
            }
            else if ( o == _collapse_below_threshold ) {
                collapseBelowThreshold();
            }
            else if ( o == _extract_pfam_style_tax_codes_cbmi ) {
                if ( ( _replace_underscores_cbmi != null ) && _extract_pfam_style_tax_codes_cbmi.isSelected() ) {
                    _replace_underscores_cbmi.setSelected( false );
                }
                updateOptions( getOptions() );
            }
            else if ( o == _inference_from_msa_item ) {
                executePhyleneticInference( false );
            }
            else if ( o == _inference_from_seqs_item ) {
                executePhyleneticInference( true );
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

    void buildAnalysisMenu() {
        _analysis_menu = MainFrame.createMenu( "Analysis", getConfiguration() );
        _analysis_menu.add( _sdi_item = new JMenuItem( "SDI (Speciation Duplication Inference)" ) );
        if ( !Constants.__RELEASE && !Constants.__SNAPSHOT_RELEASE ) {
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
        _analysis_menu.addSeparator();
        _analysis_menu.add( _lineage_inference = new JMenuItem( "Infer Ancestor Taxonomies" ) );
        customizeJMenuItem( _lineage_inference );
        _lineage_inference.setToolTipText( "Inference of ancestor taxonomies/lineages" );
        _jmenubar.add( _analysis_menu );
    }

    void buildPhylogeneticInferenceMenu() {
        _inference_menu = MainFrame.createMenu( "Inference", getConfiguration() );
        _inference_menu
                .add( _inference_from_msa_item = new JMenuItem( "From Multiple Sequence Alignment...(EXPERIMENTAL - DO NOT USE!!) " ) );
        customizeJMenuItem( _inference_from_msa_item );
        _inference_from_msa_item.setToolTipText( "Basic phylogenetic inference from MSA" );
        _inference_menu
        .add( _inference_from_seqs_item = new JMenuItem( "From Unaligned Sequences...(EXPERIMENTAL - DO NOT USE!!) " ) );
customizeJMenuItem( _inference_from_seqs_item );
_inference_from_seqs_item.setToolTipText( "Basic phylogenetic inference including multiple sequence alignment" );
        _jmenubar.add( _inference_menu );
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
        if ( getConfiguration().isEditable() ) {
            _file_jmenu.addSeparator();
            _file_jmenu.add( _new_item = new JMenuItem( "New" ) );
            _new_item.setToolTipText( "to create a new tree with one node, as source for manual tree construction" );
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
        _open_item
                .setFont( new Font( _open_item.getFont().getFontName(), Font.BOLD, _open_item.getFont().getSize() + 4 ) );
        customizeJMenuItem( _open_url_item );
        for( int i = 0; i < webservices_manager.getAvailablePhylogeniesWebserviceClients().size(); ++i ) {
            customizeJMenuItem( _load_phylogeny_from_webservice_menu_items[ i ] );
        }
        customizeJMenuItem( _save_item );
        if ( getConfiguration().isEditable() ) {
            customizeJMenuItem( _new_item );
        }
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

            @Override
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
                                                                     _non_lined_up_cladograms_rbmi,
                                                                     _uniform_cladograms_rbmi,
                                                                     _ext_node_dependent_cladogram_rbmi,
                                                                     _label_direction_cbmi );
            }
        } );
        _options_jmenu.add( customizeMenuItemAsLabel( new JMenuItem( DISPLAY_SUBHEADER ), getConfiguration() ) );
        _options_jmenu
                .add( _ext_node_dependent_cladogram_rbmi = new JRadioButtonMenuItem( MainFrame.NONUNIFORM_CLADOGRAMS_LABEL ) );
        _options_jmenu.add( _uniform_cladograms_rbmi = new JRadioButtonMenuItem( MainFrame.UNIFORM_CLADOGRAMS_LABEL ) );
        _options_jmenu.add( _non_lined_up_cladograms_rbmi = new JRadioButtonMenuItem( NON_LINED_UP_CLADOGRAMS_LABEL ) );
        _radio_group_1 = new ButtonGroup();
        _radio_group_1.add( _ext_node_dependent_cladogram_rbmi );
        _radio_group_1.add( _uniform_cladograms_rbmi );
        _radio_group_1.add( _non_lined_up_cladograms_rbmi );
        _options_jmenu.add( _show_node_boxes_cbmi = new JCheckBoxMenuItem( DISPLAY_NODE_BOXES_LABEL ) );
        _options_jmenu.add( _show_scale_cbmi = new JCheckBoxMenuItem( DISPLAY_SCALE_LABEL ) );
        _options_jmenu
                .add( _show_branch_length_values_cbmi = new JCheckBoxMenuItem( DISPLAY_BRANCH_LENGTH_VALUES_LABEL ) );
        _options_jmenu.add( _show_overview_cbmi = new JCheckBoxMenuItem( SHOW_OVERVIEW_LABEL ) );
        _options_jmenu.add( _label_direction_cbmi = new JCheckBoxMenuItem( LABEL_DIRECTION_LABEL ) );
        _label_direction_cbmi.setToolTipText( LABEL_DIRECTION_TIP );
        _options_jmenu.add( _color_labels_same_as_parent_branch = new JCheckBoxMenuItem( COLOR_LABELS_LABEL ) );
        _color_labels_same_as_parent_branch.setToolTipText( MainFrame.COLOR_LABELS_TIP );
        _options_jmenu.add( _screen_antialias_cbmi = new JCheckBoxMenuItem( SCREEN_ANTIALIAS_LABEL ) );
        _options_jmenu.add( _background_gradient_cbmi = new JCheckBoxMenuItem( BG_GRAD_LABEL ) );
        if ( getConfiguration().doDisplayOption( Configuration.show_domain_architectures ) ) {
            _options_jmenu.add( _show_domain_labels = new JCheckBoxMenuItem( SHOW_DOMAIN_LABELS_LABEL ) );
        }
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
                .add( customizeMenuItemAsLabel( new JMenuItem( "Newick/NHX/Nexus Parsing:" ), getConfiguration() ) );
        _options_jmenu
                .add( _internal_number_are_confidence_for_nh_parsing_cbmi = new JCheckBoxMenuItem( "Internal Numbers Are Confidence Values" ) );
        _options_jmenu.add( _replace_underscores_cbmi = new JCheckBoxMenuItem( "Replace Underscores with Spaces" ) );
        _options_jmenu
                .add( _extract_pfam_style_tax_codes_cbmi = new JCheckBoxMenuItem( "Extract Taxonomy Codes from Pfam-style Labels" ) );
        customizeJMenuItem( _choose_font_mi );
        customizeJMenuItem( _choose_minimal_confidence_mi );
        customizeJMenuItem( _switch_colors_mi );
        customizeJMenuItem( _print_size_mi );
        customizeJMenuItem( _choose_pdf_width_mi );
        customizeJMenuItem( _overview_placment_mi );
        customizeCheckBoxMenuItem( _show_node_boxes_cbmi, getOptions().isShowNodeBoxes() );
        customizeCheckBoxMenuItem( _color_labels_same_as_parent_branch, getOptions().isColorLabelsSameAsParentBranch() );
        customizeCheckBoxMenuItem( _screen_antialias_cbmi, getOptions().isAntialiasScreen() );
        customizeCheckBoxMenuItem( _background_gradient_cbmi, getOptions().isBackgroundColorGradient() );
        customizeCheckBoxMenuItem( _show_domain_labels, getOptions().isShowDomainLabels() );
        customizeCheckBoxMenuItem( _search_case_senstive_cbmi, getOptions().isSearchCaseSensitive() );
        customizeCheckBoxMenuItem( _show_scale_cbmi, getOptions().isShowScale() );
        customizeRadioButtonMenuItem( _non_lined_up_cladograms_rbmi,
                                      getOptions().getCladogramType() == CLADOGRAM_TYPE.NON_LINED_UP );
        customizeRadioButtonMenuItem( _uniform_cladograms_rbmi,
                                      getOptions().getCladogramType() == CLADOGRAM_TYPE.TOTAL_NODE_SUM_DEP );
        customizeRadioButtonMenuItem( _ext_node_dependent_cladogram_rbmi,
                                      getOptions().getCladogramType() == CLADOGRAM_TYPE.EXT_NODE_SUM_DEP );
        customizeCheckBoxMenuItem( _show_branch_length_values_cbmi, getOptions().isShowBranchLengthValues() );
        customizeCheckBoxMenuItem( _show_overview_cbmi, getOptions().isShowOverview() );
        customizeCheckBoxMenuItem( _label_direction_cbmi,
                                   getOptions().getNodeLabelDirection() == NODE_LABEL_DIRECTION.RADIAL );
        customizeCheckBoxMenuItem( _antialias_print_cbmi, getOptions().isAntialiasPrint() );
        customizeCheckBoxMenuItem( _print_black_and_white_cbmi, getOptions().isPrintBlackAndWhite() );
        customizeCheckBoxMenuItem( _internal_number_are_confidence_for_nh_parsing_cbmi, getOptions()
                .isInternalNumberAreConfidenceForNhParsing() );
        customizeCheckBoxMenuItem( _extract_pfam_style_tax_codes_cbmi, getOptions()
                .isExtractPfamTaxonomyCodesInNhParsing() );
        customizeCheckBoxMenuItem( _replace_underscores_cbmi, getOptions().isReplaceUnderscoresInNhParsing() );
        customizeCheckBoxMenuItem( _search_whole_words_only_cbmi, getOptions().isMatchWholeTermsOnly() );
        customizeCheckBoxMenuItem( _inverse_search_result_cbmi, getOptions().isInverseSearchResult() );
        customizeCheckBoxMenuItem( _graphics_export_visible_only_cbmi, getOptions().isGraphicsExportVisibleOnly() );
        customizeCheckBoxMenuItem( _print_using_actual_size_cbmi, getOptions().isPrintUsingActualSize() );
        customizeCheckBoxMenuItem( _graphics_export_using_actual_size_cbmi, getOptions()
                .isGraphicsExportUsingActualSize() );
        _jmenubar.add( _options_jmenu );
    }

    void buildToolsMenu() {
        _tools_menu = createMenu( "Tools", getConfiguration() );
        _tools_menu.add( _confcolor_item = new JMenuItem( "Colorize Branches Depending on Confidence" ) );
        customizeJMenuItem( _confcolor_item );
        _tools_menu.add( _taxcolor_item = new JMenuItem( "Taxonomy Colorize Branches" ) );
        customizeJMenuItem( _taxcolor_item );
        _tools_menu.add( _remove_branch_color_item = new JMenuItem( "Delete Branch Colors" ) );
        _remove_branch_color_item.setToolTipText( "To delete branch color values from the current phylogeny" );
        customizeJMenuItem( _remove_branch_color_item );
        _tools_menu.addSeparator();
        _tools_menu.add( _midpoint_root_item = new JMenuItem( "Midpoint-Root" ) );
        customizeJMenuItem( _midpoint_root_item );
        _tools_menu.addSeparator();
        _tools_menu.add( _collapse_species_specific_subtrees = new JMenuItem( "Collapse Species-Specific Subtrees" ) );
        customizeJMenuItem( _collapse_species_specific_subtrees );
        _tools_menu
                .add( _collapse_below_threshold = new JMenuItem( "Collapse Branches with Confidence Below Threshold" ) );
        customizeJMenuItem( _collapse_below_threshold );
        _collapse_below_threshold
                .setToolTipText( "To permanently collapse branches without at least one support value above a given threshold" );
        _tools_menu.addSeparator();
        _tools_menu
                .add( _move_node_names_to_tax_sn_jmi = new JMenuItem( "Transfer Node Names to Taxonomic Scientific Names" ) );
        customizeJMenuItem( _move_node_names_to_tax_sn_jmi );
        _move_node_names_to_tax_sn_jmi.setToolTipText( "To interpret node names as taxonomic scientific names" );
        _tools_menu.add( _move_node_names_to_seq_names_jmi = new JMenuItem( "Transfer Node Names to Sequence Names" ) );
        customizeJMenuItem( _move_node_names_to_seq_names_jmi );
        _move_node_names_to_seq_names_jmi.setToolTipText( "To interpret node names as sequence (protein, gene) names" );
        _tools_menu
                .add( _extract_tax_code_from_node_names_jmi = new JMenuItem( "Extract Taxonomic Codes from Node Names" ) );
        customizeJMenuItem( _extract_tax_code_from_node_names_jmi );
        _extract_tax_code_from_node_names_jmi
                .setToolTipText( "To extract taxonomic codes (mnemonics) from nodes names in the form of 'xyz_ECOLI'" );
        _tools_menu.addSeparator();
        _tools_menu
                .add( _infer_common_sn_names_item = new JMenuItem( "Infer Common Parts of Internal Scientific Names" ) );
        customizeJMenuItem( _infer_common_sn_names_item );
        _tools_menu.addSeparator();
        _tools_menu
                .add( _obtain_detailed_taxonomic_information_jmi = new JMenuItem( "Obtain Detailed Taxonomic Information" ) );
        customizeJMenuItem( _obtain_detailed_taxonomic_information_jmi );
        _obtain_detailed_taxonomic_information_jmi
                .setToolTipText( "To add additional taxonomic information (from UniProt Taxonomy)" );
        _jmenubar.add( _tools_menu );
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
            float f = 0.0f;
            final String m_str = s.trim();
            if ( !ForesterUtil.isEmpty( m_str ) ) {
                try {
                    f = Float.parseFloat( m_str );
                }
                catch ( final Exception ex ) {
                    success = false;
                }
            }
            else {
                success = false;
            }
            if ( success && ( f > 0.0 ) ) {
                getOptions().setPrintLineWidth( f );
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

    @Override
    void close() {
        if ( isUnsavedDataPresent() ) {
            final int r = JOptionPane.showConfirmDialog( this,
                                                         "Exit despite potentially unsaved changes?",
                                                         "Exit?",
                                                         JOptionPane.YES_NO_OPTION );
            if ( r != JOptionPane.YES_OPTION ) {
                return;
            }
        }
        exit();
    }

    private void closeCurrentPane() {
        if ( getMainPanel().getCurrentTreePanel() != null ) {
            if ( getMainPanel().getCurrentTreePanel().isEdited() ) {
                final int r = JOptionPane.showConfirmDialog( this,
                                                             "Close tab despite potentially unsaved changes?",
                                                             "Close Tab?",
                                                             JOptionPane.YES_NO_OPTION );
                if ( r != JOptionPane.YES_OPTION ) {
                    return;
                }
            }
            getMainPanel().closeCurrentPane();
            activateSaveAllIfNeeded();
        }
    }

    private void collapse( final Phylogeny phy, final double m ) {
        final PhylogenyNodeIterator it = phy.iteratorPostorder();
        final List<PhylogenyNode> to_be_removed = new ArrayList<PhylogenyNode>();
        double min_support = Double.MAX_VALUE;
        boolean conf_present = false;
        while ( it.hasNext() ) {
            final PhylogenyNode n = it.next();
            if ( !n.isExternal() && !n.isRoot() ) {
                final List<Confidence> c = n.getBranchData().getConfidences();
                if ( ( c != null ) && ( c.size() > 0 ) ) {
                    conf_present = true;
                    double max = 0;
                    for( final Confidence confidence : c ) {
                        if ( confidence.getValue() > max ) {
                            max = confidence.getValue();
                        }
                    }
                    if ( max < getMinNotCollapseConfidenceValue() ) {
                        to_be_removed.add( n );
                    }
                    if ( max < min_support ) {
                        min_support = max;
                    }
                }
            }
        }
        if ( conf_present ) {
            for( final PhylogenyNode node : to_be_removed ) {
                PhylogenyMethods.removeNode( node, phy );
            }
            if ( to_be_removed.size() > 0 ) {
                phy.externalNodesHaveChanged();
                phy.hashIDs();
                phy.recalculateNumberOfExternalDescendants( true );
                getCurrentTreePanel().resetNodeIdToDistToLeafMap();
                getCurrentTreePanel().setEdited( true );
                getCurrentTreePanel().repaint();
            }
            if ( to_be_removed.size() > 0 ) {
                JOptionPane.showMessageDialog( this, "Collapsed " + to_be_removed.size()
                        + " branches with\nconfidence values below " + getMinNotCollapseConfidenceValue(), "Collapsed "
                        + to_be_removed.size() + " branches", JOptionPane.INFORMATION_MESSAGE );
            }
            else {
                JOptionPane.showMessageDialog( this, "No branch collapsed,\nminimum confidence value per branch is "
                        + min_support, "No branch collapsed", JOptionPane.INFORMATION_MESSAGE );
            }
        }
        else {
            JOptionPane.showMessageDialog( this,
                                           "No branch collapsed because no confidence values present",
                                           "No confidence values present",
                                           JOptionPane.INFORMATION_MESSAGE );
        }
    }

    private void collapseBelowThreshold() {
        if ( getCurrentTreePanel() != null ) {
            final Phylogeny phy = getCurrentTreePanel().getPhylogeny();
            if ( ( phy != null ) && !phy.isEmpty() ) {
                final String s = ( String ) JOptionPane.showInputDialog( this,
                                                                         "Please enter the minimum confidence value\n",
                                                                         "Minimal Confidence Value",
                                                                         JOptionPane.QUESTION_MESSAGE,
                                                                         null,
                                                                         null,
                                                                         getMinNotCollapseConfidenceValue() );
                if ( !ForesterUtil.isEmpty( s ) ) {
                    boolean success = true;
                    double m = 0.0;
                    final String m_str = s.trim();
                    if ( !ForesterUtil.isEmpty( m_str ) ) {
                        try {
                            m = Double.parseDouble( m_str );
                        }
                        catch ( final Exception ex ) {
                            success = false;
                        }
                    }
                    else {
                        success = false;
                    }
                    if ( success && ( m >= 0.0 ) ) {
                        setMinNotCollapseConfidenceValue( m );
                        collapse( phy, m );
                    }
                }
            }
        }
    }

    private PhyloXmlParser createPhyloXmlParser() {
        PhyloXmlParser xml_parser = null;
        if ( getConfiguration().isValidatePhyloXmlAgainstSchema() ) {
            try {
                xml_parser = PhyloXmlParser.createPhyloXmlParserXsdValidating();
            }
            catch ( final Exception e ) {
                JOptionPane.showMessageDialog( this,
                                               e.getLocalizedMessage(),
                                               "failed to create validating XML parser",
                                               JOptionPane.WARNING_MESSAGE );
            }
        }
        if ( xml_parser == null ) {
            xml_parser = new PhyloXmlParser();
        }
        return xml_parser;
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
        _mainpanel.getCurrentTreePanel().setEdited( true );
        JOptionPane.showMessageDialog( this,
                                       "Number of duplications: " + duplications,
                                       "GSDI successfully completed",
                                       JOptionPane.INFORMATION_MESSAGE );
    }

    void executeLineageInference() {
        if ( ( _mainpanel.getCurrentPhylogeny() == null ) || ( _mainpanel.getCurrentPhylogeny().isEmpty() ) ) {
            return;
        }
        if ( !_mainpanel.getCurrentPhylogeny().isRooted() ) {
            JOptionPane.showMessageDialog( this,
                                           "Phylogeny is not rooted.",
                                           "Cannot infer ancestral taxonomies",
                                           JOptionPane.ERROR_MESSAGE );
            return;
        }
        final Phylogeny phy = _mainpanel.getCurrentPhylogeny().copy();
        final AncestralTaxonomyInferrer inferrer = new AncestralTaxonomyInferrer( this, _mainpanel
                .getCurrentTreePanel(), phy );
        new Thread( inferrer ).start();
    }

    private void executePhyleneticInference(boolean from_unaligned_seqs) {
        final PhyloInferenceDialog dialog = new PhyloInferenceDialog( this, getPhylogeneticInferenceOptions(), from_unaligned_seqs );
        dialog.activate();
        if ( dialog.getValue() == JOptionPane.OK_OPTION ) {
            if ( !from_unaligned_seqs )
            {            if ( getMsa() != null ) {
                final PhylogeneticInferrer inferrer = new PhylogeneticInferrer( getMsa(),
                                                                                getPhylogeneticInferenceOptions()
                                                                                        .copy(),
                                                                                this );
                new Thread( inferrer ).start();
            }
            else {
                JOptionPane.showMessageDialog( this,
                                               "No multiple sequence alignment selected",
                                               "Phylogenetic Inference Not Launched",
                                               JOptionPane.WARNING_MESSAGE );
            }
            }
            else {
                
                if ( getSeqs() != null ) {
                    final PhylogeneticInferrer inferrer = new PhylogeneticInferrer( getSeqs(),
                                                                                    getPhylogeneticInferenceOptions()
                                                                                            .copy(),
                                                                                    this );
                    new Thread( inferrer ).start();
                }
                else {
                    JOptionPane.showMessageDialog( this,
                                                   "No input sequences selected",
                                                   "Phylogenetic Inference Not Launched",
                                                   JOptionPane.WARNING_MESSAGE );
                }
                
                
            }
        }
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
        _mainpanel.getCurrentTreePanel().setEdited( true );
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
        _mainpanel.getCurrentTreePanel().setEdited( true );
        JOptionPane.showMessageDialog( this,
                                       "Number of duplications: " + duplications,
                                       "SDIR successfully completed",
                                       JOptionPane.INFORMATION_MESSAGE );
    }

    void exit() {
        removeTextFrame();
        _mainpanel.terminate();
        _contentpane.removeAll();
        setVisible( false );
        dispose();
        System.exit( 0 );
    }

    private void extractTaxCodeFromNodeNames() {
        if ( getCurrentTreePanel() != null ) {
            final Phylogeny phy = getCurrentTreePanel().getPhylogeny();
            if ( ( phy != null ) && !phy.isEmpty() ) {
                final PhylogenyNodeIterator it = phy.iteratorPostorder();
                while ( it.hasNext() ) {
                    final PhylogenyNode n = it.next();
                    final String name = n.getNodeName().trim();
                    if ( !ForesterUtil.isEmpty( name ) ) {
                        final String code = ForesterUtil.extractTaxonomyCodeFromNodeName( name,
                                                                                          false,
                                                                                          TAXONOMY_EXTRACTION.YES );
                        if ( !ForesterUtil.isEmpty( code ) ) {
                            PhylogenyMethods.setTaxonomyCode( n, code );
                        }
                    }
                }
            }
        }
    }

    private ControlPanel getControlPanel() {
        return getMainPanel().getControlPanel();
    }

    private File getCurrentDir() {
        if ( ( _current_dir == null ) || !_current_dir.canRead() ) {
            if ( ForesterUtil.OS_NAME.toLowerCase().indexOf( "win" ) > -1 ) {
                try {
                    _current_dir = new File( WindowsUtils.getCurrentUserDesktopPath() );
                }
                catch ( final Exception e ) {
                    _current_dir = null;
                }
            }
        }
        if ( ( _current_dir == null ) || !_current_dir.canRead() ) {
            if ( System.getProperty( "user.home" ) != null ) {
                _current_dir = new File( System.getProperty( "user.home" ) );
            }
            else if ( System.getProperty( "user.dir" ) != null ) {
                _current_dir = new File( System.getProperty( "user.dir" ) );
            }
        }
        return _current_dir;
    }

    @Override
    MainPanel getMainPanel() {
        return _mainpanel;
    }

    private double getMinNotCollapseConfidenceValue() {
        return _min_not_collapse;
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

    private boolean isUnsavedDataPresent() {
        final List<TreePanel> tps = getMainPanel().getTreePanels();
        for( final TreePanel tp : tps ) {
            if ( tp.isEdited() ) {
                return true;
            }
        }
        return false;
    }

    private void moveNodeNamesToSeqNames() {
        if ( getCurrentTreePanel() != null ) {
            final Phylogeny phy = getCurrentTreePanel().getPhylogeny();
            if ( ( phy != null ) && !phy.isEmpty() ) {
                ForesterUtil.transferNodeNameToField( phy, PhylogenyNodeField.SEQUENCE_NAME );
            }
        }
    }

    private void moveNodeNamesToTaxSn() {
        if ( getCurrentTreePanel() != null ) {
            final Phylogeny phy = getCurrentTreePanel().getPhylogeny();
            if ( ( phy != null ) && !phy.isEmpty() ) {
                ForesterUtil.transferNodeNameToField( phy, PhylogenyNodeField.TAXONOMY_SCIENTIFIC_NAME );
            }
        }
    }

    private void newTree() {
        final Phylogeny[] phys = new Phylogeny[ 1 ];
        final Phylogeny phy = new Phylogeny();
        final PhylogenyNode node = new PhylogenyNode();
        phy.setRoot( node );
        phy.setRooted( true );
        phys[ 0 ] = phy;
        Util.addPhylogeniesToTabs( phys, "", "", getConfiguration(), getMainPanel() );
        _mainpanel.getControlPanel().showWhole();
        _mainpanel.getCurrentTreePanel().setPhylogenyGraphicsType( PHYLOGENY_GRAPHICS_TYPE.RECTANGULAR );
        _mainpanel.getOptions().setPhylogenyGraphicsType( PHYLOGENY_GRAPHICS_TYPE.RECTANGULAR );
        if ( getMainPanel().getMainFrame() == null ) {
            // Must be "E" applet version.
            ( ( ArchaeopteryxE ) ( ( MainPanelApplets ) getMainPanel() ).getApplet() )
                    .setSelectedTypeInTypeMenu( PHYLOGENY_GRAPHICS_TYPE.RECTANGULAR );
        }
        else {
            getMainPanel().getMainFrame().setSelectedTypeInTypeMenu( PHYLOGENY_GRAPHICS_TYPE.RECTANGULAR );
        }
        activateSaveAllIfNeeded();
        System.gc();
    }

    private void obtainDetailedTaxonomicInformation() {
        if ( getCurrentTreePanel() != null ) {
            final Phylogeny phy = getCurrentTreePanel().getPhylogeny();
            if ( ( phy != null ) && !phy.isEmpty() ) {
                final TaxonomyDataObtainer t = new TaxonomyDataObtainer( this, _mainpanel.getCurrentTreePanel(), phy
                        .copy() );
                new Thread( t ).start();
            }
        }
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
                pdf_written_to = PdfExporter.writePhylogenyToPdf( file_name,
                                                                  getCurrentTreePanel(),
                                                                  getCurrentTreePanel().getWidth(),
                                                                  getCurrentTreePanel().getHeight() );
            }
            else {
                pdf_written_to = PdfExporter.writePhylogenyToPdf( file_name, getCurrentTreePanel(), getOptions()
                        .getPrintSizeX(), getOptions().getPrintSizeY() );
            }
        }
        catch ( final IOException e ) {
            error = true;
            JOptionPane.showMessageDialog( this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE );
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

    private void readPhylogeniesFromFile() {
        boolean exception = false;
        Phylogeny[] phys = null;
        // Set an initial directory if none set yet
        final File my_dir = getCurrentDir();
        _open_filechooser.setMultiSelectionEnabled( true );
        // Open file-open dialog and set current directory
        if ( my_dir != null ) {
            _open_filechooser.setCurrentDirectory( my_dir );
        }
        final int result = _open_filechooser.showOpenDialog( _contentpane );
        // All done: get the file
        final File[] files = _open_filechooser.getSelectedFiles();
        setCurrentDir( _open_filechooser.getCurrentDirectory() );
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
                            final NHXParser nhx = new NHXParser();
                            setSpecialOptionsForNhxParser( nhx );
                            phys = Util.readPhylogenies( nhx, file );
                            nhx_or_nexus = true;
                        }
                        catch ( final Exception e ) {
                            exception = true;
                            exceptionOccuredDuringOpenFile( e );
                        }
                    }
                    else if ( _open_filechooser.getFileFilter() == MainFrameApplication.xmlfilter ) {
                        warnIfNotPhyloXmlValidation( getConfiguration() );
                        try {
                            final PhyloXmlParser xml_parser = createPhyloXmlParser();
                            phys = Util.readPhylogenies( xml_parser, file );
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
                            final NexusPhylogeniesParser nex = new NexusPhylogeniesParser();
                            setSpecialOptionsForNexParser( nex );
                            phys = Util.readPhylogenies( nex, file );
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
                            final PhylogenyParser parser = ForesterUtil
                                    .createParserDependingOnFileType( file, getConfiguration()
                                            .isValidatePhyloXmlAgainstSchema() );
                            if ( parser instanceof NexusPhylogeniesParser ) {
                                final NexusPhylogeniesParser nex = ( NexusPhylogeniesParser ) parser;
                                setSpecialOptionsForNexParser( nex );
                                nhx_or_nexus = true;
                            }
                            else if ( parser instanceof NHXParser ) {
                                final NHXParser nhx = ( NHXParser ) parser;
                                setSpecialOptionsForNhxParser( nhx );
                                nhx_or_nexus = true;
                            }
                            else if ( parser instanceof PhyloXmlParser ) {
                                warnIfNotPhyloXmlValidation( getConfiguration() );
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
                        boolean one_desc = false;
                        if ( nhx_or_nexus ) {
                            for( final Phylogeny phy : phys ) {
                                if ( getOptions().isInternalNumberAreConfidenceForNhParsing() ) {
                                    ForesterUtil.transferInternalNodeNamesToConfidence( phy );
                                }
                                if ( PhylogenyMethods.getMinimumDescendentsPerInternalNodes( phy ) == 1 ) {
                                    one_desc = true;
                                    break;
                                }
                            }
                        }
                        Util.addPhylogeniesToTabs( phys,
                                                   file.getName(),
                                                   file.getAbsolutePath(),
                                                   getConfiguration(),
                                                   getMainPanel() );
                        _mainpanel.getControlPanel().showWhole();
                        if ( nhx_or_nexus && one_desc ) {
                            JOptionPane
                                    .showMessageDialog( this,
                                                        "One or more trees contain (a) node(s) with one descendant, "
                                                                + ForesterUtil.LINE_SEPARATOR
                                                                + "possibly indicating illegal parentheses within node names.",
                                                        "Warning: Possible Error in New Hampshire Formatted Data",
                                                        JOptionPane.WARNING_MESSAGE );
                        }
                    }
                }
            }
        }
        activateSaveAllIfNeeded();
        System.gc();
    }

    
    void readSeqsFromFile() {
        // Set an initial directory if none set yet
        final File my_dir = getCurrentDir();
        _seqs_filechooser.setMultiSelectionEnabled( false );
        // Open file-open dialog and set current directory
        if ( my_dir != null ) {
            _seqs_filechooser.setCurrentDirectory( my_dir );
        }
        final int result = _seqs_filechooser.showOpenDialog( _contentpane );
        // All done: get the seqs
        final File file = _seqs_filechooser.getSelectedFile();
        setCurrentDir( _seqs_filechooser.getCurrentDirectory() );
        if ( ( file != null ) && !file.isDirectory() && ( result == JFileChooser.APPROVE_OPTION ) ) {
            setSeqsFile( null );
            setSeqs( null );
            List<Sequence> seqs = null;
            try {
                if ( FastaParser.isLikelyFasta( new FileInputStream( file ) ) ) {
                    seqs = FastaParser.parse( new FileInputStream( file ) );
                    for( Sequence seq : seqs ) {
                        System.out.println( SequenceWriter.toFasta( seq, 60 ) );
                    }
                    
                }
                else {
                    //TODO error
                }
            }
            catch ( final MsaFormatException e ) {
                try {
                    _mainpanel.getCurrentTreePanel().setArrowCursor();
                }
                catch ( final Exception ex ) {
                    // Do nothing.
                }
                JOptionPane.showMessageDialog( this,
                                               e.getLocalizedMessage(),
                                               "Multiple sequence file format error",
                                               JOptionPane.ERROR_MESSAGE );
                return;
            }
            catch ( final IOException e ) {
                try {
                    _mainpanel.getCurrentTreePanel().setArrowCursor();
                }
                catch ( final Exception ex ) {
                    // Do nothing.
                }
                JOptionPane.showMessageDialog( this,
                                               e.getLocalizedMessage(),
                                               "Failed to read multiple sequence file",
                                               JOptionPane.ERROR_MESSAGE );
                return;
            }
            catch ( final IllegalArgumentException e ) {
                try {
                    _mainpanel.getCurrentTreePanel().setArrowCursor();
                }
                catch ( final Exception ex ) {
                    // Do nothing.
                }
                JOptionPane.showMessageDialog( this,
                                               e.getLocalizedMessage(),
                                               "Unexpected error during reading of multiple sequence file",
                                               JOptionPane.ERROR_MESSAGE );
                return;
            }
            catch ( final Exception e ) {
                try {
                    _mainpanel.getCurrentTreePanel().setArrowCursor();
                }
                catch ( final Exception ex ) {
                    // Do nothing.
                }
                e.printStackTrace();
                JOptionPane.showMessageDialog( this,
                                               e.getLocalizedMessage(),
                                               "Unexpected error during reading of multiple sequence file",
                                               JOptionPane.ERROR_MESSAGE );
                return;
            }
            if ( ( seqs == null ) || ( seqs.size() < 1 ) ) {
                JOptionPane.showMessageDialog( this,
                                               "Multiple sequence file is empty",
                                               "Illegal multiple sequence file",
                                               JOptionPane.ERROR_MESSAGE );
                return;
            }
            if ( seqs.size() < 4 ) {
                JOptionPane.showMessageDialog( this,
                                               "Multiple sequence file needs to contain at least 3 sequences",
                                               "Illegal multiple sequence file",
                                               JOptionPane.ERROR_MESSAGE );
                return;
            }
          //  if ( msa.getLength() < 2 ) {
         //       JOptionPane.showMessageDialog( this,
         //                                      "Multiple sequence alignment needs to contain at least 2 residues",
         //                                      "Illegal multiple sequence file",
         //                                      JOptionPane.ERROR_MESSAGE );
         //       return;
         //   }
            System.gc();
            
            setSeqsFile( _seqs_filechooser.getSelectedFile() );
            setSeqs( seqs );
        }
    }
    
    void readMsaFromFile() {
        // Set an initial directory if none set yet
        final File my_dir = getCurrentDir();
        _msa_filechooser.setMultiSelectionEnabled( false );
        // Open file-open dialog and set current directory
        if ( my_dir != null ) {
            _msa_filechooser.setCurrentDirectory( my_dir );
        }
        final int result = _msa_filechooser.showOpenDialog( _contentpane );
        // All done: get the msa
        final File file = _msa_filechooser.getSelectedFile();
        setCurrentDir( _msa_filechooser.getCurrentDirectory() );
        if ( ( file != null ) && !file.isDirectory() && ( result == JFileChooser.APPROVE_OPTION ) ) {
            setMsaFile( null );
            setMsa( null );
            Msa msa = null;
            try {
                if ( FastaParser.isLikelyFasta( new FileInputStream( file ) ) ) {
                    msa = FastaParser.parseMsa( new FileInputStream( file ) );
                    System.out.println( msa.toString() );
                }
                else {
                    msa = GeneralMsaParser.parse( new FileInputStream( file ) );
                }
            }
            catch ( final MsaFormatException e ) {
                try {
                    _mainpanel.getCurrentTreePanel().setArrowCursor();
                }
                catch ( final Exception ex ) {
                    // Do nothing.
                }
                JOptionPane.showMessageDialog( this,
                                               e.getLocalizedMessage(),
                                               "Multiple sequence alignment format error",
                                               JOptionPane.ERROR_MESSAGE );
                return;
            }
            catch ( final IOException e ) {
                try {
                    _mainpanel.getCurrentTreePanel().setArrowCursor();
                }
                catch ( final Exception ex ) {
                    // Do nothing.
                }
                JOptionPane.showMessageDialog( this,
                                               e.getLocalizedMessage(),
                                               "Failed to read multiple sequence alignment",
                                               JOptionPane.ERROR_MESSAGE );
                return;
            }
            catch ( final IllegalArgumentException e ) {
                try {
                    _mainpanel.getCurrentTreePanel().setArrowCursor();
                }
                catch ( final Exception ex ) {
                    // Do nothing.
                }
                JOptionPane.showMessageDialog( this,
                                               e.getLocalizedMessage(),
                                               "Unexpected error during reading of multiple sequence alignment",
                                               JOptionPane.ERROR_MESSAGE );
                return;
            }
            catch ( final Exception e ) {
                try {
                    _mainpanel.getCurrentTreePanel().setArrowCursor();
                }
                catch ( final Exception ex ) {
                    // Do nothing.
                }
                e.printStackTrace();
                JOptionPane.showMessageDialog( this,
                                               e.getLocalizedMessage(),
                                               "Unexpected error during reading of multiple sequence alignment",
                                               JOptionPane.ERROR_MESSAGE );
                return;
            }
            if ( ( msa == null ) || ( msa.getNumberOfSequences() < 1 ) ) {
                JOptionPane.showMessageDialog( this,
                                               "Multiple sequence alignment is empty",
                                               "Illegal Multiple Sequence Alignment",
                                               JOptionPane.ERROR_MESSAGE );
                return;
            }
            if ( msa.getNumberOfSequences() < 4 ) {
                JOptionPane.showMessageDialog( this,
                                               "Multiple sequence alignment needs to contain at least 3 sequences",
                                               "Illegal multiple sequence alignment",
                                               JOptionPane.ERROR_MESSAGE );
                return;
            }
            if ( msa.getLength() < 2 ) {
                JOptionPane.showMessageDialog( this,
                                               "Multiple sequence alignment needs to contain at least 2 residues",
                                               "Illegal multiple sequence alignment",
                                               JOptionPane.ERROR_MESSAGE );
                return;
            }
            System.gc();
            setMsaFile( _msa_filechooser.getSelectedFile() );
            setMsa( msa );
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
                    parser = ForesterUtil.createParserDependingOnUrlContents( url, getConfiguration()
                            .isValidatePhyloXmlAgainstSchema() );
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
                                               "Could not read from " + url + "\n"
                                                       + ForesterUtil.wordWrap( e.getLocalizedMessage(), 80 ),
                                               "Failed to read URL",
                                               JOptionPane.ERROR_MESSAGE );
            }
            catch ( final Exception e ) {
                JOptionPane.showMessageDialog( this,
                                               ForesterUtil.wordWrap( e.getLocalizedMessage(), 80 ),
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
                Util.addPhylogeniesToTabs( phys, new File( url.getFile() ).getName(), new File( url.getFile() )
                        .toString(), getConfiguration(), getMainPanel() );
                _mainpanel.getControlPanel().showWhole();
            }
        }
        activateSaveAllIfNeeded();
        System.gc();
    }

    private void readSpeciesTreeFromFile() {
        Phylogeny t = null;
        boolean exception = false;
        final File my_dir = getCurrentDir();
        _open_filechooser_for_species_tree.setSelectedFile( new File( "" ) );
        if ( my_dir != null ) {
            _open_filechooser_for_species_tree.setCurrentDirectory( my_dir );
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

    private void setCurrentDir( final File current_dir ) {
        _current_dir = current_dir;
    }

    private void setMinNotCollapseConfidenceValue( final double min_not_collapse ) {
        _min_not_collapse = min_not_collapse;
    }

    private void setSpecialOptionsForNexParser( final NexusPhylogeniesParser nex ) {
        nex.setReplaceUnderscores( getOptions().isReplaceUnderscoresInNhParsing() );
    }

    private void setSpecialOptionsForNhxParser( final NHXParser nhx ) {
        nhx.setReplaceUnderscores( getOptions().isReplaceUnderscoresInNhParsing() );
        ForesterUtil.TAXONOMY_EXTRACTION te = ForesterUtil.TAXONOMY_EXTRACTION.NO;
        if ( getOptions().isExtractPfamTaxonomyCodesInNhParsing() ) {
            te = ForesterUtil.TAXONOMY_EXTRACTION.YES;
        }
        nhx.setTaxonomyExtraction( te );
    }

    private void writeAllToFile() {
        if ( ( getMainPanel().getTabbedPane() == null ) || ( getMainPanel().getTabbedPane().getTabCount() < 1 ) ) {
            return;
        }
        final File my_dir = getCurrentDir();
        if ( my_dir != null ) {
            _save_filechooser.setCurrentDirectory( my_dir );
        }
        _save_filechooser.setSelectedFile( new File( "" ) );
        final int result = _save_filechooser.showSaveDialog( _contentpane );
        final File file = _save_filechooser.getSelectedFile();
        setCurrentDir( _save_filechooser.getCurrentDirectory() );
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
                else {
                    try {
                        file.delete();
                    }
                    catch ( final Exception e ) {
                        JOptionPane.showMessageDialog( this,
                                                       "Failed to delete: " + file,
                                                       "Error",
                                                       JOptionPane.WARNING_MESSAGE );
                    }
                }
            }
            final int count = getMainPanel().getTabbedPane().getTabCount();
            final List<Phylogeny> trees = new ArrayList<Phylogeny>();
            for( int i = 0; i < count; ++i ) {
                trees.add( getMainPanel().getPhylogeny( i ) );
                getMainPanel().getTreePanels().get( i ).setEdited( false );
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
        final File my_dir = getCurrentDir();
        if ( my_dir != null ) {
            _save_filechooser.setCurrentDirectory( my_dir );
        }
        final int result = _save_filechooser.showSaveDialog( _contentpane );
        final File file = _save_filechooser.getSelectedFile();
        setCurrentDir( _save_filechooser.getCurrentDirectory() );
        boolean exception = false;
        if ( ( file != null ) && ( result == JFileChooser.APPROVE_OPTION ) ) {
            if ( file.exists() ) {
                final int i = JOptionPane.showConfirmDialog( this,
                                                             file + " already exists.\nOverwrite?",
                                                             "Overwrite?",
                                                             JOptionPane.OK_CANCEL_OPTION,
                                                             JOptionPane.QUESTION_MESSAGE );
                if ( i != JOptionPane.OK_OPTION ) {
                    return;
                }
                else {
                    final File to = new File( file.getAbsoluteFile().toString() + Constants.BACKUP_FILE_SUFFIX );
                    try {
                        ForesterUtil.copyFile( file, to );
                    }
                    catch ( final Exception e ) {
                        JOptionPane.showMessageDialog( this,
                                                       "Failed to create backup copy " + to,
                                                       "Failed to Create Backup Copy",
                                                       JOptionPane.WARNING_MESSAGE );
                    }
                    try {
                        file.delete();
                    }
                    catch ( final Exception e ) {
                        JOptionPane.showMessageDialog( this,
                                                       "Failed to delete: " + file,
                                                       "Failed to Delete",
                                                       JOptionPane.WARNING_MESSAGE );
                    }
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
                getMainPanel().getCurrentTreePanel().setEdited( false );
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
            initial_filename = initial_filename.substring( 0, initial_filename.lastIndexOf( '.' ) );
        }
        initial_filename = initial_filename + "." + type;
        _writetographics_filechooser.setSelectedFile( new File( initial_filename ) );
        final File my_dir = getCurrentDir();
        if ( my_dir != null ) {
            _writetographics_filechooser.setCurrentDirectory( my_dir );
        }
        final int result = _writetographics_filechooser.showSaveDialog( _contentpane );
        File file = _writetographics_filechooser.getSelectedFile();
        setCurrentDir( _writetographics_filechooser.getCurrentDirectory() );
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
                else {
                    try {
                        file.delete();
                    }
                    catch ( final Exception e ) {
                        JOptionPane.showMessageDialog( this,
                                                       "Failed to delete: " + file,
                                                       "Error",
                                                       JOptionPane.WARNING_MESSAGE );
                    }
                }
            }
            writePhylogenyToGraphicsFile( file.toString(), type );
        }
    }

    private void writeToPdf( final Phylogeny t ) {
        if ( ( t == null ) || t.isEmpty() ) {
            return;
        }
        String initial_filename = "";
        if ( getMainPanel().getCurrentTreePanel().getTreeFile() != null ) {
            initial_filename = getMainPanel().getCurrentTreePanel().getTreeFile().toString();
        }
        if ( initial_filename.indexOf( '.' ) > 0 ) {
            initial_filename = initial_filename.substring( 0, initial_filename.lastIndexOf( '.' ) );
        }
        initial_filename = initial_filename + ".pdf";
        _writetopdf_filechooser.setSelectedFile( new File( initial_filename ) );
        final File my_dir = getCurrentDir();
        if ( my_dir != null ) {
            _writetopdf_filechooser.setCurrentDirectory( my_dir );
        }
        final int result = _writetopdf_filechooser.showSaveDialog( _contentpane );
        File file = _writetopdf_filechooser.getSelectedFile();
        setCurrentDir( _writetopdf_filechooser.getCurrentDirectory() );
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

    static MainFrame createInstance( final Phylogeny[] phys, final Configuration config, final String title ) {
        return new MainFrameApplication( phys, config, title );
    }

    static MainFrame createInstance( final Phylogeny[] phys, final String config_file_name, final String title ) {
        return new MainFrameApplication( phys, config_file_name, title );
    }

    static void setTextForGraphicsSizeChooserMenuItem( final JMenuItem mi, final Options o ) {
        mi.setText( "Enter Default Size for Graphics Export... (current: " + o.getPrintSizeX() + ", "
                + o.getPrintSizeY() + ")" );
    }

    static void setTextForPdfLineWidthChooserMenuItem( final JMenuItem mi, final Options o ) {
        mi.setText( "Enter Default Line Width for PDF Export... (current: " + o.getPrintLineWidth() + ")" );
    }

    static void warnIfNotPhyloXmlValidation( final Configuration c ) {
        if ( !c.isValidatePhyloXmlAgainstSchema() ) {
            JOptionPane
                    .showMessageDialog( null,
                                        ForesterUtil
                                                .wordWrap( "phyloXML XSD-based validation is turned off [enable with line 'validate_against_phyloxml_xsd_schem: true' in configuration file]",
                                                           80 ),
                                        "Warning",
                                        JOptionPane.WARNING_MESSAGE );
        }
    }

    private void setPhylogeneticInferenceOptions( final PhylogeneticInferenceOptions phylogenetic_inference_options ) {
        _phylogenetic_inference_options = phylogenetic_inference_options;
    }

    private PhylogeneticInferenceOptions getPhylogeneticInferenceOptions() {
        if ( _phylogenetic_inference_options == null ) {
            _phylogenetic_inference_options = new PhylogeneticInferenceOptions();
        }
        return _phylogenetic_inference_options;
    }

    Msa getMsa() {
        return _msa;
    }

    void setMsa( final Msa msa ) {
        _msa = msa;
    }

    void setMsaFile( final File msa_file ) {
        _msa_file = msa_file;
    }

    File getMsaFile() {
        return _msa_file;
    }
    
    List<Sequence> getSeqs() {
        return _seqs;
    }

    void setSeqs( final  List<Sequence> seqs ) {
        _seqs = seqs;
    }

    void setSeqsFile( final File seqs_file ) {
        _seqs_file = seqs_file;
    }

    File getSeqsFile() {
        return _seqs_file;
    }
} // MainFrameApplication.

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
                || file_name.endsWith( ".ph" ) || file_name.endsWith( ".phb" ) || file_name.endsWith( ".nwk" )
                || f.isDirectory();
    }

    @Override
    public String getDescription() {
        return "New Hampshire - Newick files (*.nh, *.newick, *.phy, *.tree, *.dnd, *.tr, *.ph, *.phb, *.nwk)";
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
        return file_name.endsWith( ".xml" ) || file_name.endsWith( ".phyloxml" ) || file_name.endsWith( "phylo.xml" )
                || file_name.endsWith( ".pxml" ) || file_name.endsWith( ".zip" ) || f.isDirectory();
    }

    @Override
    public String getDescription() {
        return "phyloXML files (*.xml, *.phyloxml, *phylo.xml, *.pxml, *.zip)";
    }
} // XMLFilter
