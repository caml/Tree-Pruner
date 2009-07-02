// $Id: ColorParser.java,v 1.2 2009/01/13 19:49:30 cmzmasek Exp $
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

package org.forester.io.parsers.phyloxml.phylogenydata;

import java.awt.Color;

import org.forester.io.parsers.PhylogenyParserException;
import org.forester.io.parsers.phyloxml.PhyloXmlMapping;
import org.forester.io.parsers.phyloxml.XmlElement;
import org.forester.phylogeny.data.BranchColor;
import org.forester.phylogeny.data.PhylogenyData;

public class ColorParser implements PhylogenyDataPhyloXmlParser {

    private static PhylogenyDataPhyloXmlParser _instance = null;

    private ColorParser() {
    }

    public PhylogenyData parse( final XmlElement element ) throws PhylogenyParserException {
        int red = 0;
        int green = 0;
        int blue = 0;
        for( int j = 0; j < element.getNumberOfChildElements(); ++j ) {
            final XmlElement c = element.getChildElement( j );
            if ( c.getQualifiedName().equals( PhyloXmlMapping.COLOR_RED ) ) {
                red = c.getValueAsInt();
            }
            else if ( c.getQualifiedName().equals( PhyloXmlMapping.COLOR_GREEN ) ) {
                green = c.getValueAsInt();
            }
            else if ( c.getQualifiedName().equals( PhyloXmlMapping.COLOR_BLUE ) ) {
                blue = c.getValueAsInt();
            }
        }
        final BranchColor color = new BranchColor();
        color.setValue( new Color( red, green, blue ) );
        return color;
    }

    public static PhylogenyDataPhyloXmlParser getInstance() {
        if ( _instance == null ) {
            _instance = new ColorParser();
        }
        return _instance;
    }
}
