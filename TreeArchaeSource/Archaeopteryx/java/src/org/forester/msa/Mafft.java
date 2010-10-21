// $Id: Mafft.java,v 1.2 2010/10/13 21:12:18 cmzmasek Exp $
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.forester.io.parsers.FastaParser;
import org.forester.util.SystemCommandExecutor;

public final class Mafft implements MsaInferrer {

    public static MsaInferrer createInstance( final String path_to_prg ) {
        return new Mafft( path_to_prg );
    }
    private String       _error;
    private int          _exit_code;
    private final String _path_to_prg;

    private Mafft( final String path_to_prg ) {
        _path_to_prg = new String( path_to_prg );
        init();
    }

    @Override
    public Object clone() {
        throw new NoSuchMethodError();
    }

    public String getErrorDescription() {
        return _error;
    }

    public int getExitCode() {
        return _exit_code;
    }

    public Msa infer( final File path_to_input_seqs, final List<String> opts ) throws IOException, InterruptedException {
        init();
        final List<String> my_opts = new ArrayList<String>();
        my_opts.add( _path_to_prg );
        for( int i = 0; i < opts.size(); i++ ) {
            my_opts.add( opts.get( i ) );
        }
        my_opts.add( path_to_input_seqs.getAbsolutePath() );
        final SystemCommandExecutor commandExecutor = new SystemCommandExecutor( my_opts );
        final int _exit_code = commandExecutor.executeCommand();
        if ( _exit_code != 0 ) {
            throw new IOException( "MAFFT failed, exit code: " + _exit_code );
        }
        final StringBuilder stdout = commandExecutor.getStandardOutputFromCommand();
        final StringBuilder stderr = commandExecutor.getStandardErrorFromCommand();
        System.out.println( stdout );
        System.out.println();
        System.out.println( stderr );
        _error = stderr.toString();
        final Msa msa = FastaParser.parseMsa( stdout.toString() );
        return msa;
    }

    private void init() {
        _error = null;
        _exit_code = -100;
    }
}
