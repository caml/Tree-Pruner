// / $Id: ResampleableMsa.java,v 1.2 2010/10/04 22:56:43 cmzmasek Exp $
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

public final class ResampleableMsa extends BasicMsa {

    private int[] _resampled_column_positions = null;

    public ResampleableMsa( final BasicMsa msa ) {
        super( msa );
    }

    public void resample( final int[] resampled_column_positions ) {
        if ( resampled_column_positions.length != getLength() ) {
            _resampled_column_positions = null;
            throw new IllegalArgumentException( "illegal attempt to use " + resampled_column_positions.length
                    + " resampled column positions on msa of length " + getLength() );
        }
        _resampled_column_positions = resampled_column_positions;
    }

    @Override
    public char getResidueAt( final int row, final int col ) {
        if ( _resampled_column_positions != null ) {
            return super.getResidueAt( row, _resampled_column_positions[ col ] );
        }
        return super.getResidueAt( row, col );
    }

    @Override
    public void setResidueAt( final int row, final int col, final char residue ) {
        throw new NoSuchMethodError( "illegal attempt to set residue in resampleable msa" );
    }
}
