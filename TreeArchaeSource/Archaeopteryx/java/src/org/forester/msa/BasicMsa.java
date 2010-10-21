// $Id: BasicMsa.java,v 1.4 2010/10/08 22:02:23 cmzmasek Exp $
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

package org.forester.msa;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.forester.sequence.Sequence;
import org.forester.sequence.Sequence.TYPE;
import org.forester.util.ForesterUtil;

public class BasicMsa implements Msa {

    private final char[][] _data;
    private final Object[] _identifiers;
    private final TYPE     _type;

    public BasicMsa( final int rows, final int columns, final TYPE type ) {
        if ( ( rows < 1 ) || ( columns < 1 ) ) {
            throw new IllegalArgumentException( "basic msa of size zero are illegal" );
        }
        _data = new char[ rows ][ columns ];
        _identifiers = new Object[ rows ];
        _type = type;
    }

    BasicMsa( final BasicMsa msa ) {
        _data = msa._data;
        _identifiers = msa._identifiers;
        _type = msa._type;
    }

    private int determineMaxIdLength() {
        int max = 0;
        for( int row = 0; row < _data.length; ++row ) {
            final int l = _identifiers[ row ].toString().length();
            if ( l > max ) {
                max = l;
            }
        }
        return max;
    }

    @Override
    public Object getIdentifier( final int row ) {
        return _identifiers[ row ];
    }

    @Override
    public int getLength() {
        return _data[ 0 ].length;
    }

    @Override
    public int getNumberOfSequences() {
        return _identifiers.length;
    }

    @Override
    public char getResidueAt( final int row, final int col ) {
        return _data[ row ][ col ];
    }

    @Override
    public StringBuffer getSequenceAsString( final int row ) {
        final StringBuffer sb = new StringBuffer( _data[ 0 ].length );
        for( int col = 0; col < _data[ 0 ].length; ++col ) {
            sb.append( getResidueAt( row, col ) );
        }
        return sb;
    }

    @Override
    public TYPE getType() {
        return _type;
    }

    public void setIdentifier( final int row, final Object id ) {
        _identifiers[ row ] = id;
    }

    public void setResidueAt( final int row, final int col, final char residue ) {
        _data[ row ][ col ] = residue;
    }

    @Override
    public String toString() {
        final int max = determineMaxIdLength() + 1;
        final StringBuffer sb = new StringBuffer();
        for( int row = 0; row < _data.length; ++row ) {
            sb.append( ForesterUtil.pad( _identifiers[ row ].toString(), max, ' ', false ) );
            for( int col = 0; col < _data[ 0 ].length; ++col ) {
                sb.append( getResidueAt( row, col ) );
            }
            sb.append( ForesterUtil.LINE_SEPARATOR );
        }
        return sb.toString();
    }

    public void write( final Writer w ) throws IOException {
        final int max = determineMaxIdLength() + 1;
        for( int row = 0; row < _data.length; ++row ) {
            w.write( ForesterUtil.pad( _identifiers[ row ].toString(), max, ' ', false ).toString() );
            for( int col = 0; col < _data[ 0 ].length; ++col ) {
                w.write( getResidueAt( row, col ) );
            }
            w.write( ForesterUtil.LINE_SEPARATOR );
        }
    }

    public static Msa createInstance( final List<Sequence> seqs ) {
        if ( seqs.size() < 1 ) {
            throw new IllegalArgumentException( "basic msa of size zero are illegal" );
        }
        final int length = seqs.get( 0 ).getLength();
        final BasicMsa msa = new BasicMsa( seqs.size(), length, seqs.get( 0 ).getType() );
        for( int row = 0; row < seqs.size(); ++row ) {
            final Sequence seq = seqs.get( row );
            if ( seq.getLength() != length ) {
                throw new IllegalArgumentException( "illegal attempt to build msa from sequences of unequal length" );
            }
            if ( seq.getType() != msa.getType() ) {
                throw new IllegalArgumentException( "illegal attempt to build msa from sequences of different type" );
            }
            msa.setIdentifier( row, seq.getIdentifier() );
            for( int col = 0; col < length; ++col ) {
                msa._data[ row ][ col ] = seq.getResidueAt( col );
            }
        }
        return msa;
    }
}
