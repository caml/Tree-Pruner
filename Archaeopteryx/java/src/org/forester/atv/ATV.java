// $Id: ATV.java,v 1.44 2009/02/23 18:59:17 cmzmasek Exp $
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

package org.forester.atv;

import org.forester.archaeopteryx.Archaeopteryx;
import org.forester.archaeopteryx.MainFrame;
import org.forester.phylogeny.Phylogeny;

/*
 * @author Christian Zmasek
 */
public final class ATV {

    public static MainFrame createApplication( final Phylogeny phylogeny ) {
        return Archaeopteryx.createApplication( phylogeny );
    }

    public static MainFrame createApplication( final Phylogeny[] phylogenies ) {
        return Archaeopteryx.createApplication( phylogenies );
    }

    public static MainFrame createApplication( final Phylogeny[] phylogenies,
                                               final String config_file_name,
                                               final String title ) {
        return Archaeopteryx.createApplication( phylogenies, config_file_name, title );
    }

    public static void main( final String[] args ) {
        Archaeopteryx.main( args );
    }
}