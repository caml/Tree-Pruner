// $Id: Date.java,v 1.3 2008/09/24 16:42:48 cmzmasek Exp $
// FORESTER -- software libraries and applications
// for evolutionary biology research and applications.
//
// Copyright (C) 2008-2009 Christian M. Zmasek
// Copyright (C) 2008-2009 Burnham Institute for Medical Research
// Copyright (C) 2000-2001 Washington University School of Medicine
// and Howard Hughes Medical Institute
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

package org.forester.phylogeny.data;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;

import org.forester.io.parsers.phyloxml.PhyloXmlMapping;
import org.forester.util.ForesterUtil;

public class Date implements PhylogenyData {

    private final String     _desc;
    private final BigDecimal _value;
    private final BigDecimal _range;
    private final String     _unit;

    public Date( final String desc ) {
        _desc = desc;
        _value = null;
        _range = null;
        _unit = "";
    }

    public Date( final String desc, final BigDecimal value, final BigDecimal range, final String unit ) {
        _desc = desc;
        _value = value;
        _range = range;
        _unit = unit;
    }

    @Override
    public StringBuffer asSimpleText() {
        if ( getValue() != null ) {
            return new StringBuffer( getDesc() + " [" + getValue().toPlainString() + " +- "
                    + getRange().toPlainString() + " " + getUnit() );
        }
        else {
            return new StringBuffer( getDesc() );
        }
    }

    @Override
    public StringBuffer asText() {
        return asSimpleText();
    }

    @Override
    public PhylogenyData copy() {
        return new Date( new String( getDesc() ),
                         new BigDecimal( getValue().toPlainString() ),
                         new BigDecimal( getRange().toPlainString() ),
                         new String( getUnit() ) );
    }

    public String getDesc() {
        return _desc;
    }

    public BigDecimal getRange() {
        return _range;
    }

    public String getUnit() {
        return _unit;
    }

    public BigDecimal getValue() {
        return _value;
    }

    @Override
    public boolean isEqual( final PhylogenyData data ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public StringBuffer toNHX() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void toPhyloXML( final Writer writer, final int level, final String indentation ) throws IOException {
        writer.write( ForesterUtil.LINE_SEPARATOR );
        writer.write( indentation );
        PhylogenyDataUtil.appendOpen( writer,
                                      PhyloXmlMapping.CLADE_DATE_DESC,
                                      PhyloXmlMapping.CLADE_DATE_RANGE,
                                      getRange().toPlainString(),
                                      PhyloXmlMapping.CLADE_DATE_UNIT,
                                      getUnit() );
        if ( !ForesterUtil.isEmpty( getDesc() ) ) {
            PhylogenyDataUtil.appendElement( writer, PhyloXmlMapping.CLADE_DATE_DESC, getDesc(), indentation );
        }
        if ( getValue() != null ) {
            PhylogenyDataUtil.appendElement( writer,
                                             PhyloXmlMapping.CLADE_DATE_DESC,
                                             getValue().toPlainString(),
                                             indentation );
        }
        writer.write( ForesterUtil.LINE_SEPARATOR );
        writer.write( indentation );
        PhylogenyDataUtil.appendClose( writer, PhyloXmlMapping.CLADE_DATE );
    }

    @Override
    public String toString() {
        return asSimpleText().toString();
    }
}