// $Id: MainFrameApplet.java,v 1.31 2010/10/02 21:34:07 cmzmasek Exp $
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
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

import javax.swing.ButtonGroup;
import javax.swing.JApplet;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.forester.archaeopteryx.Options.CLADOGRAM_TYPE;
import org.forester.archaeopteryx.Options.NODE_LABEL_DIRECTION;
import org.forester.phylogeny.Phylogeny;
import org.forester.util.ForesterUtil;

//******************************************START**********************************************************//
import com.lanl.application.TPTD.applet.AppletFileMenu;
import com.lanl.application.TPTD.applet.AppletParams;
import com.lanl.application.TPTD.applet.NewWindowSubtree;
//********************************************END**********************************************************//

public final class MainFrameApplet extends MainFrame {

    private static final long    serialVersionUID = 1941019292746717053L;
  
    private final static int     FRAME_X_SIZE     = 640, FRAME_Y_SIZE = 580;
    private final ArchaeopteryxA _applet;
    
  //******************************************START**********************************************************//
    AppletFileMenu appletFileMenu = new AppletFileMenu(this);
    //Default Applet Window size is much small
    private final static int     FRAME_X_SIZE_NEW     = 800, FRAME_Y_SIZE_NEW = 800;
    //********************************************END**********************************************************//
    private ButtonGroup          _radio_group_1;

    MainFrameApplet( final ArchaeopteryxA parent_applet, final Configuration configuration ) {
        setTitle( ArchaeopteryxA.NAME );
        _applet = parent_applet;
        setConfiguration( configuration );
        setOptions( Options.createInstance( configuration ) );
        _textframe = null;
        URL url = null;
        Phylogeny[] phys = null;
        // Get URL to tree file
        if ( _applet.getUrlString() != null ) {
            try {
                url = new URL( _applet.getUrlString() );
            }
            catch ( final Exception e ) {
                ForesterUtil.printErrorMessage( ArchaeopteryxA.NAME, e.toString() );
                
              //******************************************START**********************************************************//
                if(AppletParams.isTreePrunerForAll() ){
                	NewWindowSubtree.destroyWarningWindow();
                }
               //********************************************END**********************************************************//
                e.printStackTrace();
                JOptionPane
                        .showMessageDialog( this,
                                            ArchaeopteryxA.NAME + ": Could not create URL from: \""
                                                    + _applet.getUrlString() + "\"\nError: " + e,
                                            "Failed to create URL",
                                            JOptionPane.ERROR_MESSAGE );
                close();
            }
        }
        // Load the tree from URL
        if ( url != null ) {
            try {
                phys = Util.readPhylogeniesFromUrl( url, getConfiguration().isValidatePhyloXmlAgainstSchema() );
            }
            catch ( final Exception e ) {
                ForesterUtil.printErrorMessage( ArchaeopteryxA.NAME, e.toString() );
                e.printStackTrace();
                JOptionPane.showMessageDialog( this, ArchaeopteryxA.NAME + ": Failed to read phylogenies: "
                        + "\nError: " + e, "Failed to read phylogenies", JOptionPane.ERROR_MESSAGE );
                close();
            }
        }
        if ( ( phys == null ) || ( phys.length < 1 ) ) {
            ForesterUtil.printErrorMessage( ArchaeopteryxA.NAME, "phylogenies from [" + url + "] are null or empty" );
            JOptionPane.showMessageDialog( this, ArchaeopteryxA.NAME + ": phylogenies from [" + url
                    + "] are null or empty", "Failed to read phylogenies", JOptionPane.ERROR_MESSAGE );
        }
        else {
            Util.printAppletMessage( ArchaeopteryxA.NAME, "loaded " + phys.length + " phylogenies from: " + url );
        }
        _mainpanel = new MainPanelApplets( _configuration, this );
        // build the menu bar
        _jmenubar = new JMenuBar();
        if ( !_configuration.isUseNativeUI() ) {
            _jmenubar.setBackground( _configuration.getGuiMenuBackgroundColor() );
        }
        
      //******************************************START**********************************************************//
        if(AppletParams.isEitherTPorTDForLANLorBHB() || AppletParams.isArchaeopteryxForBHBorLANL()){
        	appletFileMenu.buildFileMenu(_jmenubar, _configuration);
        }
      //********************************************END**********************************************************//
        buildToolsMenu();
        buildViewMenu();
        buildFontSizeMenu();
        buildOptionsMenu();
        buildTypeMenu();
        buildHelpMenu();
        setJMenuBar( _jmenubar );
        _contentpane = getContentPane();
        _contentpane.setLayout( new BorderLayout() );
        _contentpane.add( _mainpanel, BorderLayout.CENTER );
        
      //******************************************START CHANGED**********************************************************//
        if(AppletParams.isEitherTPorTDForLANLorBHB() || AppletParams.isArchaeopteryxForBHBorLANL()){
        	setSize( FRAME_X_SIZE_NEW, FRAME_Y_SIZE_NEW );
        }
        else {
        	setSize( FRAME_X_SIZE, FRAME_Y_SIZE );
        }
        addWindowListener(closeWindowAdapter);
        /**addWindowListener( new WindowAdapter() {  //Using predefined variable closeWindowAdapter - changed

            @Override
            public void windowClosing( final WindowEvent e ) {
                close();
            }
        } );*/

        //********************************************END**********************************************************//
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
        setFocusable( true );
        requestFocus();
        requestFocusInWindow();
        // All done: hello, world!
        setVisible( true );
        System.gc();
    }

    void buildOptionsMenu() {
        _options_jmenu = MainFrame.createMenu( MainFrame.OPTIONS_HEADER, getConfiguration() );
        _options_jmenu.addChangeListener( new ChangeListener() {

            @Override
            public void stateChanged( final ChangeEvent e ) {
            	
                MainFrame.setOvPlacementColorChooseMenuItem( _overview_placment_mi, getCurrentTreePanel() );
              //******************************************START Changed**********************************************************//
                if(AppletParams.isEitherTPorTDForLANLorBHB()){
                	setTextForGraphicsSizeChooserMenuItem( _print_size_mi, getOptions() );
                    setTextForPdfLineWidthChooserMenuItem( _choose_pdf_width_mi, getOptions() );
            	}
            	else {
            		MainFrame.setTextColorChooseMenuItem( _switch_colors_mi, getCurrentTreePanel() );
                    MainFrame
                            .setTextMinSupportMenuItem( _choose_minimal_confidence_mi, getOptions(), getCurrentTreePanel() );

            	}
              
                if (!AppletParams.isTreeDecoratorForBHB() && !AppletParams.isTreeDecoratorForLANL()) {
                	MainFrame.setTextForFontChooserMenuItem( _choose_font_mi, createCurrentFontDesc( getMainPanel()
                			.getTreeFontSet() ) );
                }
              //********************************************END**********************************************************//
                MainFrame.updateOptionsMenuDependingOnPhylogenyType( getMainPanel(),
                                                                     _show_scale_cbmi,
                                                                     _show_branch_length_values_cbmi,
                                                                     _non_lined_up_cladograms_rbmi,
                                                                     _uniform_cladograms_rbmi,
                                                                     _ext_node_dependent_cladogram_rbmi,
                                                                     _label_direction_cbmi );
            }
        } );
        _options_jmenu.add( MainFrame.customizeMenuItemAsLabel( new JMenuItem( MainFrame.DISPLAY_SUBHEADER ),
                                                                getConfiguration() ) );
        _options_jmenu
                .add( _ext_node_dependent_cladogram_rbmi = new JRadioButtonMenuItem( MainFrame.NONUNIFORM_CLADOGRAMS_LABEL ) );
        _options_jmenu.add( _uniform_cladograms_rbmi = new JRadioButtonMenuItem( MainFrame.UNIFORM_CLADOGRAMS_LABEL ) );
        _options_jmenu.add( _non_lined_up_cladograms_rbmi = new JRadioButtonMenuItem( NON_LINED_UP_CLADOGRAMS_LABEL ) );
        _radio_group_1 = new ButtonGroup();
        _radio_group_1.add( _ext_node_dependent_cladogram_rbmi );
        _radio_group_1.add( _uniform_cladograms_rbmi );
        _radio_group_1.add( _non_lined_up_cladograms_rbmi );
        _options_jmenu.add( _show_node_boxes_cbmi = new JCheckBoxMenuItem( MainFrame.DISPLAY_NODE_BOXES_LABEL ) );
        _options_jmenu.add( _show_scale_cbmi = new JCheckBoxMenuItem( MainFrame.DISPLAY_SCALE_LABEL ) );
        _options_jmenu
                .add( _show_branch_length_values_cbmi = new JCheckBoxMenuItem( MainFrame.DISPLAY_BRANCH_LENGTH_VALUES_LABEL ) );
        _options_jmenu.add( _show_overview_cbmi = new JCheckBoxMenuItem( MainFrame.SHOW_OVERVIEW_LABEL ) );
        _options_jmenu.add( _label_direction_cbmi = new JCheckBoxMenuItem( LABEL_DIRECTION_LABEL ) );
        _label_direction_cbmi.setToolTipText( LABEL_DIRECTION_TIP );
        //******************************************START Changed**********************************************************//
        if(!AppletParams.isEitherTPorTDForLANLorBHB()){
        	_options_jmenu.add( _color_labels_same_as_parent_branch = new JCheckBoxMenuItem( COLOR_LABELS_LABEL ) );
        	_color_labels_same_as_parent_branch.setToolTipText( MainFrame.COLOR_LABELS_TIP );
        }
        //********************************************END**********************************************************//

        _options_jmenu.add( _screen_antialias_cbmi = new JCheckBoxMenuItem( MainFrame.SCREEN_ANTIALIAS_LABEL ) );
      //******************************************START Changed**********************************************************//
        if(!AppletParams.isEitherTPorTDForLANLorBHB()){
        	_options_jmenu.add( _background_gradient_cbmi = new JCheckBoxMenuItem( MainFrame.BG_GRAD_LABEL ) );
        }
      //********************************************END**********************************************************//
        if ( getConfiguration().doDisplayOption( Configuration.show_domain_architectures ) ) {
            _options_jmenu.add( _show_domain_labels = new JCheckBoxMenuItem( SHOW_DOMAIN_LABELS_LABEL ) );
        }
      //******************************************START Changed**********************************************************//
        if(!AppletParams.isEitherTPorTDForLANLorBHB()){
        	_options_jmenu.add( _choose_minimal_confidence_mi = new JMenuItem( "" ) );
        }
      //********************************************END**********************************************************//
        _options_jmenu.add( _overview_placment_mi = new JMenuItem( "" ) );
      //******************************************START Changed**********************************************************//
        if(!AppletParams.isEitherTPorTDForLANLorBHB()){
        	_options_jmenu.add( _switch_colors_mi = new JMenuItem( "" ) );
        }
        if (!AppletParams.isTreeDecoratorForBHB() && !AppletParams.isTreeDecoratorForLANL()) {
        	_options_jmenu.add( _choose_font_mi = new JMenuItem( "" ) );
        }
      //********************************************END**********************************************************//
        _options_jmenu.addSeparator();
        _options_jmenu.add( MainFrame.customizeMenuItemAsLabel( new JMenuItem( MainFrame.SEARCH_SUBHEADER ),
                                                                getConfiguration() ) );
        _options_jmenu
                .add( _search_case_senstive_cbmi = new JCheckBoxMenuItem( MainFrame.SEARCH_CASE_SENSITIVE_LABEL ) );
        _options_jmenu.add( _search_whole_words_only_cbmi = new JCheckBoxMenuItem( MainFrame.SEARCH_TERMS_ONLY_LABEL ) );
        _options_jmenu.add( _inverse_search_result_cbmi = new JCheckBoxMenuItem( INVERSE_SEARCH_RESULT_LABEL ) );
        
      //******************************************START**********************************************************//
        //TODO AAC Code Duplication New Stuff from MainFrameApplication
        if(AppletParams.isEitherTPorTDForLANLorBHB()){
        	_options_jmenu.addSeparator();
        	_options_jmenu.add( MainFrame.customizeMenuItemAsLabel( new JMenuItem( "Graphics Export & Printing:" ),
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
        	.add( MainFrame.customizeMenuItemAsLabel( new JMenuItem( "Newick/NHX/Nexus Parsing:" ), getConfiguration() ) );

        	_options_jmenu.add( _replace_underscores_cbmi = new JCheckBoxMenuItem( "Replace Underscores with Spaces" ) );
        	customizeJMenuItem( _choose_pdf_width_mi );
        	customizeJMenuItem( _print_size_mi );
        	//Customization of the above added Options from MainFrameApplication
        	customizeCheckBoxMenuItem( _antialias_print_cbmi, getOptions().isAntialiasPrint() );
        	customizeCheckBoxMenuItem( _print_black_and_white_cbmi, getOptions().isPrintBlackAndWhite() );
        	customizeCheckBoxMenuItem( _replace_underscores_cbmi, getOptions().isReplaceUnderscoresInNhParsing() );
        	customizeCheckBoxMenuItem( _graphics_export_visible_only_cbmi, getOptions().isGraphicsExportVisibleOnly() );
        	customizeCheckBoxMenuItem( _print_using_actual_size_cbmi, getOptions().isPrintUsingActualSize() );
        	customizeCheckBoxMenuItem( _graphics_export_using_actual_size_cbmi, getOptions()
        			.isGraphicsExportUsingActualSize() );
        }
      //********************************************END**********************************************************//
      //******************************************START Changed**********************************************************//
        if (!AppletParams.isTreeDecoratorForBHB() && !AppletParams.isTreeDecoratorForLANL()) {
        	customizeJMenuItem( _choose_font_mi );
        }
        if(!AppletParams.isEitherTPorTDForLANLorBHB()){
	        customizeJMenuItem( _switch_colors_mi );
	        customizeJMenuItem( _choose_minimal_confidence_mi );
        }
        //********************************************END**********************************************************//
        
        customizeJMenuItem( _overview_placment_mi );
        customizeCheckBoxMenuItem( _show_node_boxes_cbmi, getOptions().isShowNodeBoxes() );
      //******************************************START Changed**********************************************************//
        if(!AppletParams.isEitherTPorTDForLANLorBHB()){
	        customizeCheckBoxMenuItem( _color_labels_same_as_parent_branch, getOptions().isColorLabelsSameAsParentBranch() );
        }
        //********************************************END**********************************************************//

        customizeCheckBoxMenuItem( _screen_antialias_cbmi, getOptions().isAntialiasScreen() );
        
      //******************************************START Changed**********************************************************//
        if(!AppletParams.isEitherTPorTDForLANLorBHB()){
        	customizeCheckBoxMenuItem( _background_gradient_cbmi, getOptions().isBackgroundColorGradient() );
        }
      //********************************************END**********************************************************//
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
        customizeCheckBoxMenuItem( _search_whole_words_only_cbmi, getOptions().isMatchWholeTermsOnly() );
        customizeCheckBoxMenuItem( _inverse_search_result_cbmi, getOptions().isInverseSearchResult() );
        _jmenubar.add( _options_jmenu );
    }

    void buildToolsMenu() {
        _tools_menu = MainFrame.createMenu( "Tools", getConfiguration() );
      //******************************************START Changed**********************************************************//
        //MID-POINT ROOT Item is needed other everything goes
        if(!AppletParams.isEitherTPorTDForLANLorBHB()){
        	_tools_menu.add( _confcolor_item = new JMenuItem( "Colorize Branches Depending on Confidence" ) );
            customizeJMenuItem( _confcolor_item );
            _tools_menu.add( _taxcolor_item = new JMenuItem( "Taxonomy Colorize Branches" ) );
            customizeJMenuItem( _taxcolor_item );
            _tools_menu.add( _remove_branch_color_item = new JMenuItem( "Delete Branch Colors" ) );
            _remove_branch_color_item.setToolTipText( "To delete branch color values from the current phylogeny." );
            customizeJMenuItem( _remove_branch_color_item );
            _tools_menu.addSeparator();
        }
      //********************************************END**********************************************************//
        _tools_menu.add( _midpoint_root_item = new JMenuItem( "Midpoint-Root" ) );
        customizeJMenuItem( _midpoint_root_item );
      //******************************************START Changed**********************************************************//
        if(!AppletParams.isEitherTPorTDForLANLorBHB()){ 
        	//MID-POINT ROOT Item is needed other everything goes
            _tools_menu.addSeparator();
            _tools_menu
                    .add( _infer_common_sn_names_item = new JMenuItem( "Infer Common Parts of Internal Scientific Names" ) );
            customizeJMenuItem( _infer_common_sn_names_item );
            _tools_menu.add( _collapse_species_specific_subtrees = new JMenuItem( "Collapse Species-Specific Subtrees" ) );
            customizeJMenuItem( _collapse_species_specific_subtrees );
    	}
 
      //********************************************END**********************************************************//
        _jmenubar.add( _tools_menu );
    }

  //******************************************START**********************************************************//
    //TODO AAC Code Duplication from MainFrameApplication
    static void setTextForGraphicsSizeChooserMenuItem( final JMenuItem mi, final Options o ) {
        mi.setText( "Enter Default Size for Graphics Export... (current: " + o.getPrintSizeX() + ", "
                + o.getPrintSizeY() + ")" );
    }
    
    static void setTextForPdfLineWidthChooserMenuItem( final JMenuItem mi, final Options o ) {
        mi.setText( "Enter Default Line Width for PDF Export... (current: " + o.getPrintLineWidth() + ")" );
    }
  //********************************************END**********************************************************//
    
    JApplet getApplet() {
        return _applet;
    }
  
    
    @Override
    MainPanel getMainPanel() {
        return _mainpanel;
    }

    @Override
    void readPhylogeniesFromURL() {
        throw new NoSuchMethodError( "not implemented" );
    }
}
