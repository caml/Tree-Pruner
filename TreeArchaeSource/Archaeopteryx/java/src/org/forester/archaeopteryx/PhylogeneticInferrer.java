// $Id: PhylogeneticInferrer.java,v 1.17 2010/10/13 21:12:18 cmzmasek Exp $
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.forester.evoinference.distance.NeighborJoining;
import org.forester.evoinference.distance.PairwiseDistanceCalculator;
import org.forester.evoinference.matrix.distance.BasicSymmetricalDistanceMatrix;
import org.forester.evoinference.tools.BootstrapResampler;
import org.forester.io.parsers.FastaParser;
import org.forester.io.writers.SequenceWriter;
import org.forester.io.writers.SequenceWriter.SEQ_FORMAT;
import org.forester.msa.BasicMsa;
import org.forester.msa.MafftOLD;
import org.forester.msa.Mafft;
import org.forester.msa.Msa;
import org.forester.msa.MsaInferrer;
import org.forester.msa.MsaTools;
import org.forester.msa.ResampleableMsa;
import org.forester.phylogeny.Phylogeny;
import org.forester.sequence.Sequence;
import org.forester.tools.ConfidenceAssessor;
import org.forester.util.ForesterUtil;

public class PhylogeneticInferrer implements Runnable {

    private Msa                                _msa;
    private final MainFrameApplication         _mf;
    private final PhylogeneticInferenceOptions _options;
    private final List<Sequence>               _seqs;
    public final static String                 MSA_FILE_SUFFIX = ".aln";
    public final static String                 PWD_FILE_SUFFIX = ".pwd";

    public PhylogeneticInferrer( final List<Sequence> seqs,
                                 final PhylogeneticInferenceOptions options,
                                 final MainFrameApplication mf ) {
        _msa = null;
        _seqs = seqs;
        _mf = mf;
        _options = options;
    }

    public PhylogeneticInferrer( final Msa msa,
                                 final PhylogeneticInferenceOptions options,
                                 final MainFrameApplication mf ) {
        _msa = msa;
        _seqs = null;
        _mf = mf;
        _options = options;
    }

    private Msa inferMsa() throws IOException {
        File temp_seqs_file = File.createTempFile("aptx", ".fasta"); 
        System.out.println( "temp file: " + temp_seqs_file );
        //final File temp_seqs_file = new File( _options.getTempDir() + ForesterUtil.FILE_SEPARATOR + "s.fasta" );
        final BufferedWriter writer = new BufferedWriter( new FileWriter( temp_seqs_file ) );
        SequenceWriter.writeSeqs( _seqs, writer, SEQ_FORMAT.FASTA, 100 );
        writer.close();
        final List<String> opts = new ArrayList<String>();
        opts.add( "--maxiterate" );
        opts.add( "1000" );
        opts.add( "--localpair" );
        opts.add( "--quiet" );
       
        Msa msa = null;
        try {
            msa = runMAFFT( temp_seqs_file, opts );
        }
        catch ( final InterruptedException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // copy aln file to intermediate dir file
        // delete temp seqs file
        return msa;
    }

    private Phylogeny inferPhylogeny( final Msa msa ) {
        BasicSymmetricalDistanceMatrix m = null;
        switch ( _options.getPwdDistanceMethod() ) {
            case KIMURA_DISTANCE:
                m = PairwiseDistanceCalculator.calcKimuraDistances( msa );
                break;
            case POISSON_DISTANCE:
                m = PairwiseDistanceCalculator.calcPoissonDistances( msa );
                break;
            case FRACTIONAL_DISSIMILARITY:
                m = PairwiseDistanceCalculator.calcFractionalDissimilarities( msa );
                break;
            default:
                throw new IllegalStateException( "invalid pwd method" );
        }
        if ( !ForesterUtil.isEmpty( _options.getIntermediateFilesBase() ) ) {
            BufferedWriter pwd_writer;
            try {
                pwd_writer = new BufferedWriter( new FileWriter( _options.getIntermediateFilesBase() + PWD_FILE_SUFFIX ) );
                m.write( pwd_writer );
                pwd_writer.close();
            }
            catch ( final IOException e ) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        final NeighborJoining nj = new NeighborJoining();
        final Phylogeny phy = nj.execute( m );
        FastaParser.extractFastaInformation( phy );
        return phy;
    }

    private void infer() {
        //_mf.getMainPanel().getCurrentTreePanel().setWaitCursor();
        if ( ( _msa == null ) && ( _seqs == null ) ) {
            throw new IllegalArgumentException( "cannot run phylogenetic analysis with null msa and seq array" );
        }
        if ( _msa == null ) {
            Msa msa = null;
            try {
                msa = inferMsa();
            }
            catch ( final IOException e ) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println( msa.toString() );
            System.out.println( MsaTools.calcBasicGapinessStatistics( msa ).toString() );
            MsaTools msa_tools = MsaTools.createInstance();
            msa = msa_tools.removeGapColumns( 0.5, 50, msa );
            System.out.println( msa_tools.getIgnoredSequenceIds());
            System.out.println( msa.toString() );
            System.out.println( MsaTools.calcBasicGapinessStatistics( msa ).toString() );
            _msa = msa;
        }
        final int n = _options.getBootstrapSamples();
        final long seed = _options.getRandomNumberGeneratorSeed();
        final Phylogeny master_phy = inferPhylogeny( _msa );
        if ( _options.isPerformBootstrapResampling() && ( n > 0 ) ) {
            final ResampleableMsa resampleable_msa = new ResampleableMsa( ( BasicMsa ) _msa );
            final int[][] resampled_column_positions = BootstrapResampler.createResampledColumnPositions( _msa
                    .getLength(), n, seed );
            final Phylogeny[] eval_phys = new Phylogeny[ n ];
            for( int i = 0; i < n; ++i ) {
                resampleable_msa.resample( resampled_column_positions[ i ] );
                eval_phys[ i ] = inferPhylogeny( resampleable_msa );
            }
            ConfidenceAssessor.evaluate( "bootstrap", eval_phys, master_phy, true, 1 );
        }
        _mf.getMainPanel().addPhylogenyInNewTab( master_phy, _mf.getConfiguration(), "nj", "njpath" );
        _mf.getMainPanel().getCurrentTreePanel().setArrowCursor();
        JOptionPane.showMessageDialog( _mf,
                                       "NJ successfully completed",
                                       "Inference Completed",
                                       JOptionPane.INFORMATION_MESSAGE );
    }

    @Override
    public void run() {
        infer();
    }

    private Msa runMAFFT( final File input_seqs, final List<String> opts ) throws IOException, InterruptedException {
        Msa msa = null;
        final MsaInferrer mafft = Mafft.createInstance( "/usr/local/bin/mafft" );
        try {
            msa = mafft.infer( input_seqs, opts );
        }
        catch ( final IOException e ) {
            System.out.println( mafft.getErrorDescription() );
        }
        return msa;
    }

    private void writeToFiles( final BasicSymmetricalDistanceMatrix m ) {
        if ( !ForesterUtil.isEmpty( _options.getIntermediateFilesBase() ) ) {
            try {
                final BufferedWriter msa_writer = new BufferedWriter( new FileWriter( _options
                        .getIntermediateFilesBase()
                        + MSA_FILE_SUFFIX ) );
                _msa.write( msa_writer );
                msa_writer.close();
                final BufferedWriter pwd_writer = new BufferedWriter( new FileWriter( _options
                        .getIntermediateFilesBase()
                        + PWD_FILE_SUFFIX ) );
                m.write( pwd_writer );
                pwd_writer.close();
            }
            catch ( final Exception e ) {
                System.out.println( "Error: " + e.getMessage() );
            }
        }
    }
}
