// $Id: ArchaeopteryxA.java,v 1.4 2009/02/23 18:59:17 cmzmasek Exp $
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

package org.forester.archaeopteryx;
/**
 * NOTE - The original file was obtained from SourceForge.net (ATV Version 4.1.04) on 2009.07.02
 *  and was modified by the LANL Influenza Sequence Database IT team (flu@lanl.gov)
 */
import java.awt.Color;


import java.awt.Font;
import java.awt.Graphics;
import java.awt.KeyboardFocusManager;
import java.io.File;
import java.net.URL;

import javax.swing.JApplet;
import javax.swing.UIManager;

import org.forester.phylogeny.Phylogeny;
import org.forester.util.ForesterUtil;

//******************************************START**********************************************************//
import com.lanl.application.TPTD.applet.AppletParams;
import com.lanl.application.TPTD.applet.AppletTerminate;
import com.lanl.application.TPTD.applet.CrashRevovery;
import com.lanl.application.TPTD.applet.SubTreePanel;
import com.lanl.application.treeDecorator.applet.communication.TreeDecoratorCommunication;
//********************************************END**********************************************************//

public class ArchaeopteryxA extends JApplet {

    private static final long  serialVersionUID    = 2314899014580484146L;
    private final static Color background_color    = new Color( 0, 0, 0 );
    private final static Color font_color          = new Color( 0, 255, 0 );
    private final static Color ex_background_color = new Color( 0, 0, 0 );
    private final static Color ex_font_color       = new Color( 255, 0, 0 );
    private final static Font  font                = new Font( Configuration.getDefaultFontFamilyName(), Font.BOLD, 9 );
    private MainFrameApplet    _mainframe_applet;
    private String             _url_string         = "";
    private String             _message_1          = "";
    private String             _message_2          = "";
    public final static String NAME                = "ArchaeopteryxA";
    //******************************************START**********************************************************//
    private CrashRevovery crashRecovery = new CrashRevovery();
    //********************************************END**********************************************************//
    @Override
    public void destroy() {
        Util.printAppletMessage( NAME, "going to be destroyed" );
        if ( getMainFrameApplet() != null ) {
            getMainFrameApplet().close();
        }
    }

    public String getUrlString() {
        return _url_string;
    }

    @Override
    public void init() {
        boolean has_exception = false;
        setName( NAME );
      //******************************************START CHANGED**********************************************************//
        if(getParameter("app_type").equals("1") || getParameter("app_type").equals("4")   //BHB TP TD
				|| getParameter("app_type").equals("2") || getParameter("app_type").equals("5")  //LANL TP TD
				|| getParameter("app_type").equals("3") || getParameter("app_type").equals("6")   //Others TP TD
				|| getParameter("app_type").equals("0")){  //LANL/BHB Archae
        	getParams();
        	setUrlString(AppletParams.urlOfTreeToLoad);
        }
        else{
        	setUrlString( getParameter( Constants.APPLET_PARAM_NAME_FOR_URL_OF_TREE_TO_LOAD ) );
        }
        // // take url of tree 2 load from applet params and not// from parameter -changed
      //********************************************END**********************************************************//
        
        Util.printAppletMessage( NAME, "URL of phylogenies to load: \"" + getUrlString() + "\"" );
        setBackground( background_color );
        setForeground( font_color );
        setFont( font );
        repaint();
        String s = null;
        try {
            s = System.getProperty( "java.version" );
        }
        catch ( final Exception e ) {
            ForesterUtil.printWarningMessage( NAME, "minor error: " + e.getLocalizedMessage() );
        }
        if ( ( s != null ) && ( s.length() > 0 ) ) {
            setMessage2( "[Your Java version: " + s + "]" );
            repaint();
        }
      //******************************************START CHANGED**********************************************************//
        final String config_filename;
        if(AppletParams.isEitherTPorTDForAll() || AppletParams.isArchaeopteryxForBHBorLANL()){
        	config_filename = AppletParams.configFilename;
        }
        else{
        	config_filename = getParameter( Constants.APPLET_PARAM_NAME_FOR_CONFIG_FILE_URL ); 
        }
        if(AppletParams.isTreeDecoratorForAll()){
        	TreeDecoratorCommunication.getSequenceDetailsComm();
        }
        //// take config filename from applet params  
																										  // and not from parameter -changed
      //********************************************END**********************************************************//
        
        Util.printAppletMessage( NAME, "URL for configuration file is: " + config_filename );
        final Configuration configuration = new Configuration( config_filename, true, true );
        try {
            if ( configuration.isUseNativeUI() ) {
                UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
            }
            else {
                UIManager.setLookAndFeel( UIManager.getCrossPlatformLookAndFeelClassName() );
            }
            setVisible( false );
            _mainframe_applet = new MainFrameApplet( this, configuration );
            URL url = null;
            url = new URL( getUrlString() );
            final Phylogeny[] phys = Util.readPhylogeniesFromUrl( url );
          //******************************************START CHANGED**********************************************************//
            if(AppletParams.isEitherTPorTDForAll()){
            	Util.addPhylogeniesToTabs( phys, AppletParams.tabName, getUrlString(), getMainFrameApplet()
            			.getConfiguration(), getMainFrameApplet().getMainPanel() );
            }
            else{
            	  Util.addPhylogeniesToTabs( phys, new File( url.getFile() ).getName(), getUrlString(), getMainFrameApplet()
            		                 .getConfiguration(), getMainFrameApplet().getMainPanel() );
            }
            // changed from name of file to AppletParams.tabName - changed
            //********************************************END**********************************************************//
            getMainFrameApplet().getMainPanel().getControlPanel().showWholeAll();
            getMainFrameApplet().getMainPanel().getControlPanel().showWhole();
            setVisible( true );
        }
        catch ( final Exception e ) {
            ForesterUtil.printErrorMessage( NAME, e.toString() );
            setBackground( ex_background_color );
            setForeground( ex_font_color );
            has_exception = true;
            setMessage1( "Exception: " + e );
            e.printStackTrace();
            repaint();
        }
        if ( !has_exception ) {
            setMessage1( NAME + " is now ready!" );
            repaint();
            Util.printAppletMessage( NAME, "successfully initialized" );
        }
        KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
        getMainFrameApplet().requestFocus();
        getMainFrameApplet().requestFocusInWindow();
        getMainFrameApplet().requestFocus();
        
      //******************************************START**********************************************************//
        if(AppletParams.isTreePrunerForAll()){
        	SubTreePanel.mainAppletFrame = _mainframe_applet;
        	AppletTerminate.appletContext = getAppletContext();
        	crashRecovery.treePrunerCrashRecoveryInit();
        }
        else if(AppletParams.isTreeDecoratorForAll()){
        	SubTreePanel.mainAppletFrame = _mainframe_applet;
        	AppletTerminate.appletContext = getAppletContext();
        	if(TreeDecoratorCommunication.isCommError){
        		AppletTerminate.closeParentAppletOnCommError();
        	}
        	crashRecovery.treeDecoratorCrashRecoveryInit();
        }
      //********************************************END**********************************************************//
    }

    /**
     * Prints message when initialization is finished. Called automatically.
     * 
     * @param g
     *            Graphics
     */
    @Override
    public void paint( final Graphics g ) {
        g.setColor( background_color );
        g.fillRect( 0, 0, 300, 60 );
        g.setColor( font_color );
        g.drawString( getMessage2(), 10, 20 );
        g.drawString( getMessage1(), 10, 40 );
    }

    @Override
    public void start() {
        getMainFrameApplet().getMainPanel().validate();
        getMainFrameApplet().requestFocus();
        getMainFrameApplet().requestFocusInWindow();
        getMainFrameApplet().requestFocus();
        Util.printAppletMessage( NAME, "started" );
    }

    private MainFrameApplet getMainFrameApplet() {
        return _mainframe_applet;
    }

    private String getMessage1() {
        return _message_1;
    }

    private String getMessage2() {
        return _message_2;
    }

    private void setMessage1( final String message_1 ) {
        _message_1 = message_1;
    }

    private void setMessage2( final String message_2 ) {
        _message_2 = message_2;
    }

    private void setUrlString( final String url_string ) {
        _url_string = url_string;
    }
    
    //******************************************START**********************************************************//
    private void getParams(){
    	String urlOfTreeToLoad1 = getParameter(Constants.APPLET_PARAM_NAME_FOR_URL_OF_TREE_TO_LOAD);
    	String configFileName1 = getParameter(Constants.APPLET_PARAM_NAME_FOR_CONFIG_FILE_URL);
    	String filename1 = getParameter("filename");
    	String URLprefix1 = getParameter("URLPrefix");
    	int applicationType1=-1;
    	applicationType1 = Integer.parseInt(getParameter("app_type"));
    	String savedAcc1 = getParameter("saved_acc");
    	String savedAccFlag1 = getParameter("saved_acc_flag");
    	String tabName1 = getParameter("tree_panel_tab_name"); 
    	
    	AppletParams.setAppletParams(urlOfTreeToLoad1, configFileName1, getCodeBase(), filename1,
    								URLprefix1, applicationType1, savedAccFlag1,tabName1);
    	
    	
    	System.out.println(AppletParams.applicationType + " " +AppletParams.configFilename+ " " +
    			AppletParams.filename + " " +AppletParams.codeBase + " " +
    			AppletParams.urlOfTreeToLoad + " " + AppletParams.savedAccFlag +" "
     			+ "URL Prefix: "+AppletParams.URLprefix);
    }
    
    public MainFrameApplet create_new_Frame(){
    		setUrlString(AppletParams.urlOfTreeToLoad);
    		final Configuration configuration = new Configuration( AppletParams.configFilename, true, true );
    		MainFrameApplet mfa = new MainFrameApplet( this, configuration );
        	return mfa;
        }
    
    //********************************************END**********************************************************//
}
