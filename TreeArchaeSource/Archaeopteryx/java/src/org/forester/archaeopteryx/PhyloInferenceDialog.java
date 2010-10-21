// $Id: PhyloInferenceDialog.java,v 1.7 2010/10/13 21:12:18 cmzmasek Exp $
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

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.forester.evoinference.distance.PairwiseDistanceCalculator.PWD_DISTANCE_METHOD;
import org.forester.sequence.Sequence;
import org.forester.util.BasicDescriptiveStatistics;
import org.forester.util.DescriptiveStatistics;
import org.forester.util.ForesterUtil;

public class PhyloInferenceDialog extends JDialog implements ActionListener {

    private static final long                  serialVersionUID = 8337543508238133614L;
    private final JPanel                       _pnl;
    private final JButton                      _launch_btn;
    private final JFormattedTextField          _bootstrap_tf;
    private final JCheckBox                    _bootstrap_cb;
    private final PhylogeneticInferenceOptions _opts;
    private JTextField                         _input_msa_file_tf;
    private JButton                            _select_input_msa_btn;
    private final MainFrameApplication         _parent_frame;
    private JTextField                         _msa_length_tf;
    private JTextField                         _msa_size_tf;
    private JTextField                         _msa_type_tf;
    private final JRadioButton                 _distance_calc_kimura_rb;
    private final JRadioButton                 _distance_calc_poisson_rb;
    private final JRadioButton                 _distance_calc_fract_dissimilarity_rb;
    private int                                _value           = JOptionPane.CANCEL_OPTION;
    private JTextField                         _input_seqs_tf;
    private JButton                            _select_input_seqs_btn;
    private JTextField                         _input_seqs_number_tf;
    private JTextField                         _input_seqs_median_length_tf;
    private JTextField                         _input_seqs_min_length_tf;
    private JTextField                         _input_seqs_max_length_tf;
    private JTextField                         _input_seqs_type_tf;

    public PhyloInferenceDialog( final MainFrameApplication frame,
                                 final PhylogeneticInferenceOptions options,
                                 final boolean from_unaligned_seqs ) {
        super( frame, true );
        setVisible( false );
        _parent_frame = frame;
        _opts = options;
        _pnl = new JPanel();
        getContentPane().add( _pnl );
        final BoxLayout box_layout = new BoxLayout( _pnl, BoxLayout.PAGE_AXIS );
        _pnl.setLayout( box_layout );
        if ( from_unaligned_seqs ) {
            setTitle( "Phylogenetic Inference (including multiple sequence alignment)" );
            final JPanel inputfile_pnl_1 = new JPanel();
            final JPanel inputfile_pnl_2 = new JPanel();
            inputfile_pnl_1.setLayout( new FlowLayout() );
            inputfile_pnl_2.setLayout( new FlowLayout() );
            inputfile_pnl_1.add( new JLabel( "Input Sequence File:" ) );
            inputfile_pnl_1.add( _input_seqs_tf = new JTextField() );
            inputfile_pnl_1.add( _select_input_seqs_btn = new JButton( "Select Input File" ) );
            inputfile_pnl_2.add( new JLabel( "Sequences: " ) );
            inputfile_pnl_2.add( new JLabel( "Number of Sequences:" ) );
            inputfile_pnl_2.add( _input_seqs_number_tf = new JTextField() );
            inputfile_pnl_2.add( new JLabel( "Length: median:" ) );
            inputfile_pnl_2.add( _input_seqs_median_length_tf = new JTextField() );
            inputfile_pnl_2.add( new JLabel( "min:" ) );
            inputfile_pnl_2.add( _input_seqs_min_length_tf = new JTextField() );
            inputfile_pnl_2.add( new JLabel( "max:" ) );
            inputfile_pnl_2.add( _input_seqs_max_length_tf = new JTextField() );
            inputfile_pnl_2.add( new JLabel( "Type:" ) );
            inputfile_pnl_2.add( _input_seqs_type_tf = new JTextField() );
            _input_seqs_median_length_tf.setColumns( 4 );
            _input_seqs_min_length_tf.setColumns( 4 );
            _input_seqs_max_length_tf.setColumns( 4 );
            _input_seqs_number_tf.setColumns( 4 );
            _input_seqs_type_tf.setColumns( 2 );
            _input_seqs_tf.setColumns( 20 );
            _input_seqs_tf.setEditable( false );
            _input_seqs_median_length_tf.setEditable( false );
            _input_seqs_min_length_tf.setEditable( false );
            _input_seqs_max_length_tf.setEditable( false );
            _input_seqs_number_tf.setEditable( false );
            _input_seqs_type_tf.setEditable( false );
            _select_input_seqs_btn.addActionListener( this );
            _pnl.add( inputfile_pnl_1 );
            _pnl.add( inputfile_pnl_2 );
        }
        else {
            setTitle( "Phylogenetic Inference (from already aligned sequences) " );
            // Inputfile (MSA):
            final JPanel inputfile_pnl_1 = new JPanel();
            final JPanel inputfile_pnl_2 = new JPanel();
            inputfile_pnl_1.setLayout( new FlowLayout() );
            inputfile_pnl_2.setLayout( new FlowLayout() );
            inputfile_pnl_1.add( new JLabel( "Input MSA File:" ) );
            inputfile_pnl_1.add( _input_msa_file_tf = new JTextField() );
            inputfile_pnl_1.add( _select_input_msa_btn = new JButton( "Select Input File" ) );
            inputfile_pnl_2.add( new JLabel( "MSA: " ) );
            inputfile_pnl_2.add( new JLabel( "Number of Sequences:" ) );
            inputfile_pnl_2.add( _msa_size_tf = new JTextField() );
            inputfile_pnl_2.add( new JLabel( "Length:" ) );
            inputfile_pnl_2.add( _msa_length_tf = new JTextField() );
            inputfile_pnl_2.add( new JLabel( "Type:" ) );
            inputfile_pnl_2.add( _msa_type_tf = new JTextField() );
            _msa_length_tf.setColumns( 4 );
            _msa_size_tf.setColumns( 4 );
            _msa_type_tf.setColumns( 2 );
            _input_msa_file_tf.setColumns( 20 );
            _input_msa_file_tf.setEditable( false );
            _msa_length_tf.setEditable( false );
            _msa_size_tf.setEditable( false );
            _msa_type_tf.setEditable( false );
            _select_input_msa_btn.addActionListener( this );
            _pnl.add( inputfile_pnl_1 );
            _pnl.add( inputfile_pnl_2 );
        }
        // Distance calculation:
        //TODO if type==AA...
        final JPanel distance_calc_pnl_1 = new JPanel();
        distance_calc_pnl_1.setLayout( new FlowLayout() );
        distance_calc_pnl_1.add( new JLabel( "Distance calculation:" ) );
        distance_calc_pnl_1.add( _distance_calc_kimura_rb = new JRadioButton( "Kimura correction" ) );
        distance_calc_pnl_1.add( _distance_calc_poisson_rb = new JRadioButton( "Poisson" ) );
        distance_calc_pnl_1
                .add( _distance_calc_fract_dissimilarity_rb = new JRadioButton( "Fractional dissimilarity" ) );
        final ButtonGroup distance_calc_group_1 = new ButtonGroup();
        distance_calc_group_1.add( _distance_calc_kimura_rb );
        distance_calc_group_1.add( _distance_calc_poisson_rb );
        distance_calc_group_1.add( _distance_calc_fract_dissimilarity_rb );
        _pnl.add( distance_calc_pnl_1 );
        // Bootstrap resampling:
        final JPanel bootstrap_pnl = new JPanel();
        bootstrap_pnl.setLayout( new FlowLayout() );
        bootstrap_pnl.add( _bootstrap_cb = new JCheckBox( "Perform Bootstrap Resampling" ) );
        bootstrap_pnl.add( new JLabel( "Number of Bootstrap Samples:" ) );
        bootstrap_pnl.add( _bootstrap_tf = new JFormattedTextField( Util.createMaskFormatter( "###" ) ) );
        // TODO see http://download.oracle.com/javase/tutorial/uiswing/components/formattedtextfield.html
        //_bootstrap_tf.setColumns( 4 );
        _pnl.add( bootstrap_pnl );
        _launch_btn = new JButton( "Go!" );
        _launch_btn.addActionListener( this );
        _pnl.add( _launch_btn );
        initializeValues( from_unaligned_seqs );
        pack();
        setLocationRelativeTo( getParentFrame() );
        setResizable( false );
    }

    public void actionPerformed( final ActionEvent e ) {
        if ( e.getSource() == _select_input_msa_btn ) {
            readInputFile();
        }
        else if ( e.getSource() == _select_input_seqs_btn ) {
            readInputSeqsFile();
        }
        else if ( e.getSource() == _launch_btn ) {
            launch();
        }
    }

    public void activate() {
        setVisible( true );
    }

    private MainFrameApplication getParentFrame() {
        return _parent_frame;
    }

    public PhylogeneticInferenceOptions getPhylogeneticInferenceOptions() {
        return _opts;
    }

    public int getValue() {
        return _value;
    }

    private void initializeValues(boolean from_unaligned_seqs) {
        _value = JOptionPane.CANCEL_OPTION;
        if ( from_unaligned_seqs) {
            updateSeqsItems();
        }
        else  {
            updateMsaItems();
        }
        updateDistanceCalcMethod();
        _bootstrap_tf.setText( getPhylogeneticInferenceOptions().getBootstrapSamples() + "" );
    }

    private void launch() {
        processPerformBootstrapResampling();
        if ( _bootstrap_cb.isSelected() ) {
            processBootstrapSamplesNumber();
        }
        processDistanceCalcMethod();
        setVisible( false );
        _value = JOptionPane.OK_OPTION;
    }

    private void processBootstrapSamplesNumber() {
        int bootstrap_samples = 0;
        try {
            bootstrap_samples = Integer.parseInt( _bootstrap_tf.getText() );
        }
        catch ( final NumberFormatException e ) {
            bootstrap_samples = 0;
        }
        if ( bootstrap_samples >= 0 ) {
            getPhylogeneticInferenceOptions().setBootstrapSamples( bootstrap_samples );
        }
    }

    private void processDistanceCalcMethod() {
        if ( ( _distance_calc_kimura_rb != null ) && _distance_calc_kimura_rb.isSelected() ) {
            getPhylogeneticInferenceOptions().setPwdDistanceMethod( PWD_DISTANCE_METHOD.KIMURA_DISTANCE );
        }
        else if ( ( _distance_calc_poisson_rb != null ) && _distance_calc_poisson_rb.isSelected() ) {
            getPhylogeneticInferenceOptions().setPwdDistanceMethod( PWD_DISTANCE_METHOD.POISSON_DISTANCE );
        }
        else if ( ( _distance_calc_fract_dissimilarity_rb != null )
                && _distance_calc_fract_dissimilarity_rb.isSelected() ) {
            getPhylogeneticInferenceOptions().setPwdDistanceMethod( PWD_DISTANCE_METHOD.FRACTIONAL_DISSIMILARITY );
        }
    }

    private void processPerformBootstrapResampling() {
        getPhylogeneticInferenceOptions().setPerformBootstrapResampling( _bootstrap_cb.isSelected() );
    }

    private void readInputFile() {
        getParentFrame().readMsaFromFile();
        updateMsaItems();
        
    }
    
    private void readInputSeqsFile() {
        getParentFrame().readSeqsFromFile();
        updateSeqsItems();
    }

    private void updateDistanceCalcMethod() {
        switch ( getPhylogeneticInferenceOptions().getPwdDistanceMethod() ) {
            case KIMURA_DISTANCE:
                _distance_calc_kimura_rb.setSelected( true );
                break;
            case POISSON_DISTANCE:
                _distance_calc_poisson_rb.setSelected( true );
                break;
            case FRACTIONAL_DISSIMILARITY:
                _distance_calc_fract_dissimilarity_rb.setSelected( true );
                break;
            default:
                throw new IllegalStateException( "invalid distance calc method" );
        }
    }

    private void updateMsaItems() {
        if ( getParentFrame().getMsa() != null ) {
            _input_msa_file_tf.setText( getParentFrame().getMsaFile().toString() );
            _msa_length_tf.setText( getParentFrame().getMsa().getLength() + "" );
            _msa_size_tf.setText( getParentFrame().getMsa().getNumberOfSequences() + "" );
            _msa_type_tf.setText( getParentFrame().getMsa().getType() + "" );
            _input_msa_file_tf.setEnabled( true );
            _msa_length_tf.setEnabled( true );
            _msa_size_tf.setEnabled( true );
            _msa_type_tf.setEnabled( true );
            _launch_btn.setEnabled( true );
        }
        else {
            _input_msa_file_tf.setText( "" );
            _msa_length_tf.setText( "" );
            _msa_size_tf.setText( "" );
            _msa_type_tf.setText( "" );
            _input_msa_file_tf.setEnabled( false );
            _msa_length_tf.setEnabled( false );
            _msa_size_tf.setEnabled( false );
            _msa_type_tf.setEnabled( false );
            _launch_btn.setEnabled( false );
        }
    }
    
    private void updateSeqsItems() {
        if ( getParentFrame().getSeqs() != null ) {
            final DescriptiveStatistics stats = calcSequenceStats(  getParentFrame().getSeqs() );
            _input_seqs_tf.setText( getParentFrame().getSeqsFile().toString() );
            _input_seqs_median_length_tf.setText( (int) stats.median() + "" );
            _input_seqs_min_length_tf.setText( (int) stats.getMin()  + "" );
            
            _input_seqs_max_length_tf.setText( (int) stats.getMax()  + "" );
            
            _input_seqs_number_tf.setText( getParentFrame().getSeqs().size() + "" );
            _input_seqs_type_tf.setText( getParentFrame().getSeqs().get( 0 ).getType() + "" );
            _input_seqs_tf.setEnabled( true );
            _input_seqs_median_length_tf.setEnabled( true );
            _input_seqs_min_length_tf.setEnabled( true );
            _input_seqs_max_length_tf.setEnabled( true );
            _input_seqs_number_tf.setEnabled( true );
            _input_seqs_type_tf.setEnabled( true );
            _launch_btn.setEnabled( true );
        }
        else {
            _input_seqs_tf.setText( "" );
            _input_seqs_median_length_tf.setText( "" );
            _input_seqs_min_length_tf.setText( "" );
            _input_seqs_max_length_tf.setText( "" );
            _input_seqs_number_tf.setText( "" );
            _input_seqs_type_tf.setText( "" );
            _input_seqs_tf.setEnabled( false );
            _input_seqs_median_length_tf.setEnabled( false );
            _input_seqs_min_length_tf.setEnabled( false );
            _input_seqs_max_length_tf.setEnabled( false );
            _input_seqs_number_tf.setEnabled( false );
            _input_seqs_type_tf.setEnabled( false );
            _launch_btn.setEnabled( false );
        }
    }
    
    DescriptiveStatistics calcSequenceStats( List<Sequence> seqs ) {
        DescriptiveStatistics stats = new  BasicDescriptiveStatistics();
        for( Sequence s : seqs ) {
            stats.addValue( s.getLength() );
        }
        return stats;
    }
}
