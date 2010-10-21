// $Id: TaxonomyDataObtainer.java,v 1.7 2010/09/18 03:34:03 cmzmasek Exp $
//
// forester -- software libraries and applications
// for genomics and evolutionary biology research.
//
// Copyright (C) 2010 Christian M Zmasek
// Copyright (C) 2010 Sanford-Burnham Medical Research Institute
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

import java.io.IOException;
import java.util.SortedSet;

import javax.swing.JOptionPane;

import org.forester.analysis.AncestralTaxonomyInference;
import org.forester.phylogeny.Phylogeny;

public class TaxonomyDataObtainer implements Runnable {

    private final Phylogeny            _phy;
    private final MainFrameApplication _mf;
    private final TreePanel            _treepanel;

    TaxonomyDataObtainer( final MainFrameApplication mf, final TreePanel treepanel, final Phylogeny phy ) {
        _phy = phy;
        _mf = mf;
        _treepanel = treepanel;
    }

    private void execute() {
        _mf.getMainPanel().getCurrentTreePanel().setWaitCursor();
        SortedSet<String> not_found = null;
        try {
            not_found = AncestralTaxonomyInference.obtainDetailedTaxonomicInformation( _phy );
        }
        catch ( final IOException e ) {
            e.printStackTrace();
            JOptionPane.showMessageDialog( _mf,
                                           e.toString(),
                                           "Failed to obtain taxonomic information",
                                           JOptionPane.ERROR_MESSAGE );
        }
        finally {
            _mf.getMainPanel().getCurrentTreePanel().setArrowCursor();
        }
        _phy.setRerootable( false );
        _treepanel.setTree( _phy );
        _mf.showWhole();
        _treepanel.setEdited( true );
        if ( ( not_found != null ) && ( not_found.size() > 0 ) ) {
            int max = not_found.size();
            boolean more = false;
            if ( max > 20 ) {
                more = true;
                max = 20;
            }
            final StringBuffer sb = new StringBuffer();
            sb.append( "Not all taxonomies could be resolved.\n" );
            if ( not_found.size() == 1 ) {
                sb.append( "The following taxonomy was not found:\n" );
            }
            else {
                sb.append( "The following taxonomies were not found (total: " + not_found.size() + "):\n" );
            }
            int i = 0;
            for( final String string : not_found ) {
                if ( i > 19 ) {
                    break;
                }
                sb.append( string );
                sb.append( "\n" );
                ++i;
            }
            if ( more ) {
                sb.append( "..." );
            }
            try {
                JOptionPane.showMessageDialog( _mf,
                                               sb.toString(),
                                               "Taxonomy Tool Completed",
                                               JOptionPane.WARNING_MESSAGE );
            }
            catch ( final Exception e ) {
                // Not important if this fails, do nothing. 
            }
        }
        else {
            try {
                JOptionPane.showMessageDialog( _mf,
                                               "Taxonomy tool successfully completed",
                                               "Taxonomy Tool Completed",
                                               JOptionPane.INFORMATION_MESSAGE );
            }
            catch ( final Exception e ) {
                // Not important if this fails, do nothing.
            }
        }
    }

    @Override
    public void run() {
        execute();
    }
}
