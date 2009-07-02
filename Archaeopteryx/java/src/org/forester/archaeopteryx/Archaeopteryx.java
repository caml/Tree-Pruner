// $Id: Archaeopteryx.java,v 1.22 2009/06/30 01:38:00 cmzmasek Exp $
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

import java.io.File;

import org.forester.phylogeny.Phylogeny;
import org.forester.util.ForesterUtil;

/*
 * @author Christian Zmasek
 */
public final class Archaeopteryx {

    public static MainFrame createApplication( final Phylogeny phylogeny ) {
        final Phylogeny[] phylogenies = new Phylogeny[ 1 ];
        phylogenies[ 0 ] = phylogeny;
        return createApplication( phylogenies, "", "" );
    }

    public static MainFrame createApplication( final Phylogeny[] phylogenies ) {
        return createApplication( phylogenies, "", "" );
    }

    public static MainFrame createApplication( final Phylogeny[] phylogenies,
                                               final String config_file_name,
                                               final String title ) {
        return MainFrameApplication.createInstance( phylogenies, config_file_name, title );
    }

    public static void main( final String args[] ) {
        Phylogeny[] phylogenies = null;
        String config_filename = null;
        File f = null;
        try {
            int filename_index = 0;
            if ( args.length > 0 ) {
                // check for a config file
                if ( args[ 0 ].startsWith( "-c" ) ) {
                    config_filename = args[ 1 ];
                    filename_index += 2;
                }
                if ( args.length > filename_index ) {
                    f = new File( args[ filename_index ] );
                    final String err = ForesterUtil.isReadableFile( f );
                    if ( !ForesterUtil.isEmpty( err ) ) {
                        ForesterUtil.fatalError( Constants.PRG_NAME, err );
                    }
                    phylogenies = Util.readPhylogenies( ForesterUtil.createParserDependingOnFileType( f ), f );
                }
            }
        }
        catch ( final Exception e ) {
            ForesterUtil.fatalError( Constants.PRG_NAME, "failed to start: " + e.getLocalizedMessage() );
        }
        String title = "";
        if ( f != null ) {
            title = f.getName();
        }
        try {
            //~~~~~~~~~~~~~~~<<< remove me TODO 
            // final PhylogenyFactory factory = ParserBasedPhylogenyFactory.getInstance();
            //final PhyloXmlParser xml_parser = new PhyloXmlParser();
            //   final String s = "C:\\Documents and Settings\\czmasek\\";
            // final String s = "/home/czmasek/species_tree_1.xml";
            // final String s = "C:\\888.xml";
            //  final Phylogeny[] p = ParserBasedPhylogenyFactory.getInstance().create( s, new PhyloXmlParser() );
            //  final Phylogeny[] p = factory.create( new File( s + "basics.nh" ), new NHXParser() );
            //MainFrameApplication.createInstance( ParserBasedPhylogenyFactory.getInstance()
            //        .create( s, new PhyloXmlParser() ), config_filename, title );
            //~~~~~~~~~~~~~~~<<< remove me TODO 
            MainFrameApplication.createInstance( phylogenies, config_filename, title );
        }
        // catch ( final IOException ex ) {
        //     ForesterUtil.fatalError( Constants.PRG_NAME, "failed to start: " + ex.getLocalizedMessage() );
        // }
        catch ( final Exception ex ) {
            Util.unexpectedException( ex );
        }
        catch ( final Error err ) {
            Util.unexpectedError( err );
        }
    }
}