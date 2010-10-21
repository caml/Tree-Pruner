// $Id: Msa.java,v 1.1 2010/09/29 23:50:16 cmzmasek Exp $
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

package org.forester.msa;

import java.io.IOException;
import java.io.Writer;

import org.forester.sequence.Sequence.TYPE;

public interface Msa {

    public Object getIdentifier( int row );

    public void setIdentifier( int row, Object identifier );

    public int getLength();

    public int getNumberOfSequences();

    public char getResidueAt( int row, int col );

    public StringBuffer getSequenceAsString( int row );

    public abstract TYPE getType();

    public void setResidueAt( final int row, final int col, final char residue );

    public void write( Writer w ) throws IOException;
}
