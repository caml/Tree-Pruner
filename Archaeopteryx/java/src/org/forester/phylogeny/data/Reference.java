// $Id: Reference.java,v 1.5 2008/09/24 16:42:49 cmzmasek Exp $
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
// WWW: www.phylosoft.org

package org.forester.phylogeny.data;

import java.io.IOException;
import java.io.Writer;

import org.forester.io.parsers.phyloxml.PhyloXmlMapping;
import org.forester.util.ForesterUtil;

public class Reference implements PhylogenyData {

    final String _value;
    final String _doig;

    public Reference( final String value ) {
        _value = value;
        _doig = "";
    }

    public Reference( final String value, final String doi ) {
        _value = value;
        _doig = doi;
    }

    public StringBuffer asSimpleText() {
        return new StringBuffer( getValue() );
    }

    public StringBuffer asText() {
        final StringBuffer sb = new StringBuffer();
        if ( !ForesterUtil.isEmpty( getDoi() ) ) {
            sb.append( "[doi:" );
            sb.append( getDoi() );
            sb.append( "] " );
        }
        sb.append( getValue() );
        return sb;
    }

    public PhylogenyData copy() {
        return new Reference( new String( getValue() ), new String( getDoi() ) );
    }

    public String getDoi() {
        return _doig;
    }

    public String getValue() {
        return _value;
    }

    public boolean isEqual( final PhylogenyData data ) {
        if ( ( data == null ) || ( getValue() == null ) ) {
            return false;
        }
        return ( ( Reference ) data ).getValue().equals( getValue() )
                && ( ( Reference ) data ).getDoi().equals( getDoi() );
    }

    public StringBuffer toNHX() {
        throw new UnsupportedOperationException();
    }

    public void toPhyloXML( final Writer writer, final int level, final String indentation ) throws IOException {
        writer.write( ForesterUtil.LINE_SEPARATOR );
        writer.write( indentation );
        PhylogenyDataUtil.appendElement( writer,
                                         PhyloXmlMapping.REFERENCE,
                                         getValue(),
                                         PhyloXmlMapping.REFERENCE_DOI_ATTR,
                                         getDoi() );
    }

    @Override
    public String toString() {
        return asText().toString();
    }
}