// $Id: MainFrameApplet.java,v 1.20 2009/06/15 18:31:33 cmzmasek Exp $
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
/**
 * NOTE - The original file was obtained from SourceForge.net (ATV Version 4.1.04) on 2009.07.02
 *  and was modified by the LANL Influenza Sequence Database IT team (flu@lanl.gov)
 */
import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URI;
import java.net.URL;

import javax.swing.JApplet;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
    //********************************************END**********************************************************//
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
                phys = Util.readPhylogeniesFromUrl( url );
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
            _jmenubar.setBackground( Constants.MENU_BACKGROUND_COLOR_DEFAULT );
        }
      //******************************************START**********************************************************//
        if(AppletParams.isEitherTPorTDForLANLorBHB() || AppletParams.isArchaeopteryxForBHBorLANL()){
        	appletFileMenu.buildFileMenu(_jmenubar, _configuration.isUseNativeUI());
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
        setSize( FRAME_X_SIZE, FRAME_Y_SIZE );
      //******************************************START CHANGED**********************************************************//
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

            public void stateChanged( final ChangeEvent e ) {
                MainFrame.setOvPlacementColorChooseMenuItem( _overview_placment_mi, getCurrentTreePanel() );
                MainFrame.setTextColorChooseMenuItem( _switch_colors_mi, getCurrentTreePanel() );
                MainFrame
                        .setTextMinSupportMenuItem( _choose_minimal_confidence_mi, getOptions(), getCurrentTreePanel() );
                MainFrame.setTextForFontChooserMenuItem( _choose_font_mi, createCurrentFontDesc( getMainPanel()
                        .getTreeFontSet() ) );
                MainFrame.updateOptionsMenuDependingOnPhylogenyType( getMainPanel(),
                                                                     _show_scale_cbmi,
                                                                     _show_branch_length_values_cbmi,
                                                                     _non_lined_up_cladograms_cbmi,
                                                                     _label_direction_cbmi );
            }
        } );
        _options_jmenu.add( MainFrame.customizeMenuItemAsLabel( new JMenuItem( MainFrame.DISPLAY_SUBHEADER ),
                                                                getConfiguration() ) );
        _options_jmenu
                .add( _non_lined_up_cladograms_cbmi = new JCheckBoxMenuItem( MainFrame.NON_LINED_UP_CLADOGRAMS_LABEL ) );
        _options_jmenu.add( _show_node_boxes_cbmi = new JCheckBoxMenuItem( MainFrame.DISPLAY_NODE_BOXES_LABEL ) );
        _options_jmenu.add( _show_scale_cbmi = new JCheckBoxMenuItem( MainFrame.DISPLAY_SCALE_LABEL ) );
        _options_jmenu
                .add( _show_branch_length_values_cbmi = new JCheckBoxMenuItem( MainFrame.DISPLAY_BRANCH_LENGTH_VALUES_LABEL ) );
        _options_jmenu.add( _show_overview_cbmi = new JCheckBoxMenuItem( MainFrame.SHOW_OVERVIEW_LABEL ) );
        _options_jmenu.add( _label_direction_cbmi = new JCheckBoxMenuItem( LABEL_DIRECTION_LABEL ) );
        _label_direction_cbmi.setToolTipText( LABEL_DIRECTION_TIP );
        _options_jmenu.add( _screen_antialias_cbmi = new JCheckBoxMenuItem( MainFrame.SCREEN_ANTIALIAS_LABEL ) );
        _options_jmenu.add( _choose_minimal_confidence_mi = new JMenuItem( "" ) );
        _options_jmenu.add( _overview_placment_mi = new JMenuItem( "" ) );
        _options_jmenu.add( _switch_colors_mi = new JMenuItem( "" ) );
        _options_jmenu.add( _choose_font_mi = new JMenuItem( "" ) );
        _options_jmenu.addSeparator();
        _options_jmenu.add( MainFrame.customizeMenuItemAsLabel( new JMenuItem( MainFrame.SEARCH_SUBHEADER ),
                                                                getConfiguration() ) );
        _options_jmenu
                .add( _search_case_senstive_cbmi = new JCheckBoxMenuItem( MainFrame.SEARCH_CASE_SENSITIVE_LABEL ) );
        _options_jmenu.add( _search_whole_words_only_cbmi = new JCheckBoxMenuItem( MainFrame.SEARCH_TERMS_ONLY_LABEL ) );
        _options_jmenu.add( _inverse_search_result_cbmi = new JCheckBoxMenuItem( INVERSE_SEARCH_RESULT_LABEL ) );
        customizeJMenuItem( _choose_font_mi );
        customizeJMenuItem( _switch_colors_mi );
        customizeJMenuItem( _choose_minimal_confidence_mi );
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
        customizeCheckBoxMenuItem( _search_whole_words_only_cbmi, getOptions().isMatchWholeTermsOnly() );
        customizeCheckBoxMenuItem( _inverse_search_result_cbmi, getOptions().isInverseSearchResult() );
        _jmenubar.add( _options_jmenu );
    }

    JApplet getApplet() {
        return _applet;
    }

    @Override
    MainPanel getMainPanel() {
        return _mainpanel;
    }

    @Override
    void openPhyloXmlWebsite() {
        try {
            Util.launchWebBrowser( new URI( Constants.PHYLOXML_WEB_SITE ), true, getApplet(), Constants.PRG_NAME );
        }
        catch ( final Exception e ) {
            ForesterUtil.printErrorMessage( Constants.PRG_NAME, e.toString() );
        }
    }

    @Override
    void openWebsite() {
        try {
            Util.launchWebBrowser( new URI( Constants.WEB_SITE ), true, getApplet(), Constants.PRG_NAME );
        }
        catch ( final Exception e ) {
            ForesterUtil.printErrorMessage( Constants.PRG_NAME, e.toString() );
        }
    }

    @Override
    void readPhylogeniesFromURL() {
        throw new NoSuchMethodError( "not implemented" );
    }
}
