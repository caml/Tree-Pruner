// $Id: ReferenceParser.java,v 1.2 2009/01/13 19:49:30 cmzmasek Exp $
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
// WWW: www.phylosoft.org/

package org.forester.io.parsers.phyloxml.phylogenydata;

import org.forester.io.parsers.PhylogenyParserException;
import org.forester.io.parsers.phyloxml.PhyloXmlMapping;
import org.forester.io.parsers.phyloxml.XmlElement;
import org.forester.phylogeny.data.PhylogenyData;
import org.forester.phylogeny.data.Reference;

public class ReferenceParser implements PhylogenyDataPhyloXmlParser {

    private static PhylogenyDataPhyloXmlParser _instance = null;

    private ReferenceParser() {
    }

    @Override
    public PhylogenyData parse( final XmlElement element ) throws PhylogenyParserException {
        if ( element.isHasAttribute( PhyloXmlMapping.REFERENCE_DOI_ATTR ) ) {
            return new Reference( element.getValueAsString(), element.getAttribute( PhyloXmlMapping.REFERENCE_DOI_ATTR ) );
        }
        else {
            return new Reference( element.getValueAsString() );
        }
    }

    public static PhylogenyDataPhyloXmlParser getInstance() {
        if ( _instance == null ) {
            _instance = new ReferenceParser();
        }
        return _instance;
    }
}
