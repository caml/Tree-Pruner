// $Id: RIO.java,v 1.18 2009/04/15 23:11:36 cmzmasek Exp $
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

package org.forester.sdi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.forester.io.parsers.phyloxml.PhyloXmlParser;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyMethods;
import org.forester.phylogeny.PhylogenyNode;
import org.forester.phylogeny.factories.ParserBasedPhylogenyFactory;
import org.forester.phylogeny.factories.PhylogenyFactory;

/*
 * @author Christian M. Zmasek
 */
public class RIO {

    private final static boolean                      ROOT_BY_MINIMIZING_MAPPING_COST = false;
    private final static boolean                      ROOT_BY_MINIMIZING_SUM_OF_DUPS  = true;
    private final static boolean                      ROOT_BY_MINIMIZING_TREE_HEIGHT  = true;
    private final static boolean                      TIME                            = false;
    // This is just to create StringBuffers
    // of optimal size, number of bootstraps is
    // flexible.
    private HashMap<String, HashMap<String, Integer>> o_hash_maps_;                           // HashMap of HashMaps
    // (key=name,value=HashMap).
    // The resulting orthologs are stored in this.
    private HashMap<String, HashMap<String, Integer>> so_hash_maps_;                          // HashMap of HashMaps
    // (key=name,value=HashMap).
    // The resulting super orthologs are stored in
    // this.
    private HashMap<String, HashMap<String, Integer>> up_hash_maps_;                          // HashMap of HashMaps
    // (key=name,value=HashMap).
    // The resulting ultra paralogs are stored in
    // this.
    private HashMap<String, HashMap<String, Integer>> sn_hash_maps_;                          // HashMap of HashMaps
    // (key=name,value=HashMap).
    // The resulting "subtree neighbors" are stored
    // in this.
    private HashMap<String, Vector>                   _m;                                     // HashMap of Vectors
    // (key=name,value=Vector).
    // The distance matrix might be stored in this.
    private HashMap<String, Double>                   _l;                                     // HashMap
    // of
    // Doubles
    // (key=name,value=Double).
    // The distance list might be stored in this.
    // Same function as m_ but used if distances
    // come as list as opposed to a matrix.
    private String[]                                  seq_names_;                             // Stores
    // all
    // sequencs
    // names
    // in the
    // gene
    // tree.
    private int                                       bootstraps_, ext_nodes_;                // Ext
    // nodes
    // in
    // analyzed
    // gene
    // trees
    // (after
    // stripping).
    private long                                      time_;

    /**
     * Default constructor.
     */
    public RIO() {
        reset();
    } // RIO()

    /**
     * Returns the numbers of trees analyzed.
     * <p>
     * (Last modified: 02/20/00)
     * 
     * @return the numbers of trees analyzed
     * @see #inferOrthologs(Phylogeny[],Phylogeny)
     * @see #inferOrthologs(File,Phylogeny)
     */
    public int getBootstraps() {
        return bootstraps_;
    }

    /**
     * Returns the distance to a sequences/taxa after a distance list file has
     * been read in with readDistanceList(File). Throws an exception if name is
     * not found or if no list has been read in.
     * <p>
     * (Last modified: 05/30/01)
     * 
     * @param name
     *            a sequence name
     * @see #readDistanceList(File)
     */
    public double getDistance( String name ) {
        double distance = 0.0;
        name = name.trim();
        if ( _l == null ) {
            throw new IllegalStateException( "Distance list has probably not been read in (successfully)." );
        }
        if ( _l.get( name ) == null ) {
            throw new IllegalArgumentException( name + " not found." );
        }
        distance = ( _l.get( name ) ).doubleValue();
        return distance;
    } // getDistance( String )

    /**
     * Returns the distance between two sequences/taxa after a distance matrix
     * file has been read in with readDistanceMatrix(File). Throws an exception
     * if name1 or name2 are not found or if no matrix has been read in.
     * <p>
     * (Last modified: 11/28/00)
     * 
     * @param name1
     *            a sequence name name2 a sequence name
     * @see #readDistanceMatrix(File)
     */
    public double getDistance( String name1, String name2 ) {
        Vector<Integer> vector1 = null;
        Vector<String> vector2 = null;
        int position = 0;
        String distance_value = null;
        double distance = 0.0;
        name1 = name1.trim();
        name2 = name2.trim();
        if ( _m == null ) {
            throw new IllegalStateException( "Distance matrix has probably not been read in (successfully)." );
        }
        vector1 = _m.get( name1 );
        if ( vector1 == null ) {
            throw new IllegalArgumentException( name1 + " not found." );
        }
        vector2 = _m.get( name2 );
        if ( vector2 == null ) {
            throw new IllegalArgumentException( name2 + " not found." );
        }
        position = vector1.elementAt( 0 );
        distance_value = vector2.elementAt( position + 1 );
        if ( distance_value == null ) {
            throw new RuntimeException( "Unexpected failure in method getDistance" );
        }
        distance = Double.parseDouble( distance_value.trim() );
        return distance;
    } // getDistance( String, String )

    /**
     * Returns the numbers of number of ext nodes in gene trees analyzed (after
     * stripping).
     * <p>
     * (Last modified: 05/02/00)
     * 
     * @return number of ext nodes in gene trees analyzed (after stripping)
     * @see #inferOrthologs(Phylogeny[],Phylogeny)
     * @see #inferOrthologs(File,Phylogeny)
     */
    public int getExtNodesOfAnalyzedGeneTrees() {
        return ext_nodes_;
    }

    /**
     * Returns a HashMap containing the inferred orthologs of the external gene
     * tree node with the sequence name seq_name. Sequence names are the keys
     * (String), numbers of observations are the values (Int). Orthologs are to
     * be inferred by method "inferOrthologs". Throws an exception if seq_name
     * is not found. (Last modified: 05/08/01)
     * 
     * @param seq_name
     *            sequence name of a external node of the gene trees
     * @return HashMap containing the inferred orthologs
     *         (name(String)->value(Int))
     * @see #inferOrthologs(Phylogeny[],Phylogeny)
     * @see #inferOrthologs(File,Phylogeny)
     */
    public HashMap<String, Integer> getInferredOrthologs( final String seq_name ) {
        if ( o_hash_maps_ == null ) {
            return null;
        }
        return o_hash_maps_.get( seq_name );
    } // getInferredOrthologs( String )

    /**
     * Returns a HashMap containing the inferred "subtree neighbors" of the
     * external gene tree node with the sequence name seq_name. Sequence names
     * are the keys (String), numbers of observations are the values (Int).
     * "subtree neighbors" are to be inferred by method "inferOrthologs". Throws
     * an exception if seq_name is not found. (Last modified: 12/26/01)
     * 
     * @param seq_name
     *            sequence name of a external node of the gene trees
     * @return HashMap containing the inferred "subtree neighbors"
     *         (name(String)->value(Int))
     * @see #inferOrthologs(Phylogeny[],Phylogeny)
     * @see #inferOrthologs(File,Phylogeny)
     */
    public HashMap<String, Integer> getInferredSubtreeNeighbors( final String seq_name ) {
        if ( sn_hash_maps_ == null ) {
            return null;
        }
        return sn_hash_maps_.get( seq_name );
    } // getInferredSubtreeNeighbors( String )

    /**
     * Returns a HashMap containing the inferred "super orthologs" of the
     * external gene tree node with the sequence name seq_name. Sequence names
     * are the keys (String), numbers of observations are the values (Int).
     * Super orthologs are to be inferred by method "inferOrthologs". Throws an
     * exception if seq_name is not found. (Last modified: 05/08/01)
     * 
     * @param seq_name
     *            sequence name of a external node of the gene trees
     * @return HashMap containing the inferred super orthologs
     *         (name(String)->value(Int))
     * @see #inferOrthologs(Phylogeny[],Phylogeny)
     * @see #inferOrthologs(File,Phylogeny)
     */
    public HashMap<String, Integer> getInferredSuperOrthologs( final String seq_name ) {
        if ( so_hash_maps_ == null ) {
            return null;
        }
        return so_hash_maps_.get( seq_name );
    } // getInferredSuperOrthologs( String )

    /**
     * Returns a HashMap containing the inferred "ultra paralogs" of the
     * external gene tree node with the sequence name seq_name. Sequence names
     * are the keys (String), numbers of observations are the values (Int).
     * "ultra paralogs" are to be inferred by method "inferOrthologs". Throws an
     * exception if seq_name is not found. (Last modified: 05/08/01)
     * 
     * @param seq_name
     *            sequence name of a external node of the gene trees
     * @return HashMap containing the inferred ultra paralogs
     *         (name(String)->value(Int))
     * @see #inferOrthologs(Phylogeny[],Phylogeny)
     * @see #inferOrthologs(File,Phylogeny)
     */
    public HashMap<String, Integer> getInferredUltraParalogs( final String seq_name ) {
        if ( up_hash_maps_ == null ) {
            return null;
        }
        return up_hash_maps_.get( seq_name );
    } // getInferredUltraParalogs( String )

    /**
     * Returns the order in which ortholog (o), "super ortholog" (s) and
     * distance (d) are returned and sorted (priority of sort always goes from
     * left to right), given sort. For the meaning of sort
     * 
     * @see #inferredOrthologsToString(String,int,double,double)
     *      <p>
     *      
     * @param sort
     *            determines order and sort priority
     * @return String indicating the order
     * @see #inferredOrthologsToString(String,int,double,double)
     */
    public String getOrder( final int sort ) {
        String order = "";
        switch ( sort ) {
            case 0:
                order = "orthologies";
                break;
            case 1:
                order = "orthologies > super orthologies";
                break;
            case 2:
                order = "super orthologies > orthologies";
                break;
            case 3:
                order = "orthologies > distance to query";
                break;
            case 4:
                order = "distance to query > orthologies";
                break;
            case 5:
                order = "orthologies > super orthologies > distance to query";
                break;
            case 6:
                order = "orthologies > distance to query > super orthologies";
                break;
            case 7:
                order = "super orthologies > orthologies > distance to query";
                break;
            case 8:
                order = "super orthologies > distance to query > orthologies";
                break;
            case 9:
                order = "distance to query > orthologies > super orthologies";
                break;
            case 10:
                order = "distance to query > super orthologies > orthologies";
                break;
            case 11:
                order = "orthologies > subtree neighbors > distance to query";
                break;
            case 12:
                order = "orthologies > subtree neighbors > super orthologies > distance to query";
                break;
            case 13:
                order = "orthologies > super orthologies > subtree neighbors > distance to query";
                break;
            case 14:
                order = "subtree neighbors > orthologies > super orthologies > distance to query";
                break;
            case 15:
                order = "subtree neighbors > distance to query > orthologies > super orthologies";
                break;
            case 16:
                order = "orthologies > distance to query > subtree neighbors > super orthologies";
                break;
            case 17:
                order = "orthologies > subtree neighbors > distance to query > super orthologies";
                break;
            default:
                order = "orthologies";
                break;
        }
        return order;
    } // getOrder( int )

    /**
     * Returns the time (in ms) needed to run "inferOrthologs". Final variable
     * TIME needs to be set to true.
     * <p>
     * (Last modified: 11/14/00)
     * 
     * @return time (in ms) needed to run method "inferOrthologs"
     * @see #inferOrthologs(Phylogeny[],Phylogeny)
     * @see #inferOrthologs(File,Phylogeny)
     */
    public long getTime() {
        return time_;
    }

    /**
     * Infers the orthologs (as well the "super orthologs", the "x orthologs",
     * and the "ultra paralogs") for each external node in Phylogeny array
     * gene_trees. Tallies how many times each sequence is (super-) orthologous
     * towards all other ones. Tallies how many times each sequence is ultra
     * paralogous towards all other ones. Tallies how many times each sequence
     * is a subtree neighbor of the query. Gene duplications are inferred using
     * SDI. Modifies its argument species_tree. Might be a little slower than
     * "inferOrthologs(File,Phylogeny,String)". Needs less memory than
     * "inferOrthologs(Phylogeny[],Phylogeny)" since not all gene Trees have to
     * be kept in memory.
     * <p>
     * To obtain the results use the methods listed below.
     * <p>
     * (Last modified: 10/19/01)
     * 
     * @param gene_trees_file
     *            a File containing gene Trees in NH format, which is the result
     *            of performing a bootstrap analysis in PHYLIP
     * @param species_tree
     *            a species Phylogeny, which has species names in its species
     *            fields
     * @see #inferOrthologs(File,Phylogeny,String)
     * @see #getInferredOrthologs(String)
     * @see #getInferredSuperOrthologs(String)
     * @see #inferredOrthologsToString(String,int,double,double)
     * @see #inferredOrthologTableToFile(File)
     * @see #inferredSuperOrthologTableToFile(File)
     */
    public void inferOrthologs( final File gene_trees_file, final Phylogeny species_tree ) throws IOException {
        int bs = 0;
        if ( RIO.TIME ) {
            time_ = System.currentTimeMillis();
        }
        if ( !gene_trees_file.exists() ) {
            throw new IOException( gene_trees_file.getAbsolutePath() + " does not exist." );
        }
        else if ( !gene_trees_file.isFile() ) {
            throw new IOException( gene_trees_file.getAbsolutePath() + " is not a file." );
        }
        // Read in first tree to get its sequence names
        // and strip species_tree.
        final PhylogenyFactory factory = ParserBasedPhylogenyFactory.getInstance();
        final Phylogeny gene_tree = factory.create( gene_trees_file, new PhyloXmlParser() )[ 0 ];
        // Removes from species_tree all species not found in gene_tree.
        PhylogenyMethods.taxonomyBasedDeletionOfExternalNodes( gene_tree, species_tree );
        // Removes from gene_tree all species not found in species_tree.
        PhylogenyMethods.taxonomyBasedDeletionOfExternalNodes( species_tree, gene_tree );
        seq_names_ = gene_tree.getAllExternalSeqNames();
        if ( ( seq_names_ == null ) || ( seq_names_.length < 1 ) ) {
            return;
        }
        o_hash_maps_ = new HashMap<String, HashMap<String, Integer>>( seq_names_.length );
        so_hash_maps_ = new HashMap<String, HashMap<String, Integer>>( seq_names_.length );
        up_hash_maps_ = new HashMap<String, HashMap<String, Integer>>( seq_names_.length );
        sn_hash_maps_ = new HashMap<String, HashMap<String, Integer>>( seq_names_.length );
        for( int i = 0; i < seq_names_.length; ++i ) {
            o_hash_maps_.put( seq_names_[ i ], new HashMap<String, Integer>( seq_names_.length ) );
            so_hash_maps_.put( seq_names_[ i ], new HashMap<String, Integer>( seq_names_.length ) );
            up_hash_maps_.put( seq_names_[ i ], new HashMap<String, Integer>( seq_names_.length ) );
            sn_hash_maps_.put( seq_names_[ i ], new HashMap<String, Integer>( seq_names_.length ) );
        }
        // Go through all gene trees in the file.
        final Phylogeny[] gene_trees = factory.create( gene_trees_file, new PhyloXmlParser() );
        for( final Phylogeny gt : gene_trees ) {
            bs++;
            PhylogenyMethods.taxonomyBasedDeletionOfExternalNodes( species_tree, gt );
            doInferOrthologs( gt, species_tree );
        }
        setBootstraps( bs );
        if ( RIO.TIME ) {
            time_ = ( System.currentTimeMillis() - time_ );
        }
    }

    /**
     * Infers the orthologs (as well the "super orthologs", the "subtree
     * neighbors", and the "ultra paralogs") for each external node of the gene
     * Trees in multiple tree File gene_trees_file (=output of PHYLIP NEIGHBOR,
     * for example). Tallies how many times each sequence is (super-)
     * orthologous towards the query. Tallies how many times each sequence is
     * ultra paralogous towards the query. Tallies how many times each sequence
     * is a subtree neighbor of the query. Gene duplications are inferred using
     * SDI. Modifies its argument species_tree. Is a little faster than
     * "inferOrthologs(File,Phylogeny)" since orthologs are only inferred for
     * query.
     * <p>
     * To obtain the results use the methods listed below.
     * <p>
     * (Last modified: 10/19/01)
     * 
     * @param gene_trees_file
     *            a File containing gene Trees in NH format, which is the result
     *            of performing a bootstrap analysis in PHYLIP
     * @param species_tree
     *            a species Phylogeny, which has species names in its species
     *            fields
     * @param query
     *            the sequence name of the squence whose orthologs are to be
     *            inferred
     * @see #getInferredOrthologs(String)
     * @see #getInferredSuperOrthologs(String)
     * @see #inferredOrthologsToString(String,int,double,double)
     * @see #inferredOrthologTableToFile(File)
     * @see #inferredSuperOrthologTableToFile(File)
     */
    public void inferOrthologs( final File gene_trees_file, final Phylogeny species_tree, final String query )
            throws IOException {
        int bs = 0;
        if ( RIO.TIME ) {
            time_ = System.currentTimeMillis();
        }
        if ( !gene_trees_file.exists() ) {
            throw new IllegalArgumentException( gene_trees_file.getAbsolutePath() + " does not exist." );
        }
        else if ( !gene_trees_file.isFile() ) {
            throw new IllegalArgumentException( gene_trees_file.getAbsolutePath() + " is not a file." );
        }
        // Read in first tree to get its sequence names
        // and strip species_tree.
        final PhylogenyFactory factory = ParserBasedPhylogenyFactory.getInstance();
        final Phylogeny gene_tree = factory.create( gene_trees_file, new PhyloXmlParser() )[ 0 ];
        // Removes from species_tree all species not found in gene_tree.
        PhylogenyMethods.taxonomyBasedDeletionOfExternalNodes( gene_tree, species_tree );
        // Removes from gene_tree all species not found in species_tree.
        PhylogenyMethods.taxonomyBasedDeletionOfExternalNodes( species_tree, gene_tree );
        seq_names_ = gene_tree.getAllExternalSeqNames();
        if ( ( seq_names_ == null ) || ( seq_names_.length < 1 ) ) {
            return;
        }
        o_hash_maps_ = new HashMap( 1 );
        so_hash_maps_ = new HashMap( 1 );
        up_hash_maps_ = new HashMap( 1 );
        sn_hash_maps_ = new HashMap( 1 );
        o_hash_maps_.put( query, new HashMap( seq_names_.length ) );
        so_hash_maps_.put( query, new HashMap( seq_names_.length ) );
        up_hash_maps_.put( query, new HashMap( seq_names_.length ) );
        sn_hash_maps_.put( query, new HashMap( seq_names_.length ) );
        // Go through all gene trees in the file.
        final Phylogeny[] gene_trees = factory.create( gene_trees_file, new PhyloXmlParser() );
        for( final Phylogeny gt : gene_trees ) {
            bs++;
            // Removes from gene_tree all species not found in species_tree.
            PhylogenyMethods.taxonomyBasedDeletionOfExternalNodes( species_tree, gt );
            doInferOrthologs( gt, species_tree, query );
        }
        setBootstraps( bs );
        if ( RIO.TIME ) {
            time_ = ( System.currentTimeMillis() - time_ );
        }
    }

    /**
     * Infers the orthologs (as well the "super orthologs", the "x orthologs",
     * and the "ultra paralogs") for each external node in Phylogeny array
     * gene_trees. Tallies how many times each sequence is (super-) orthologous
     * towards all other ones. Tallies how many times each sequence is super
     * paralogous towards all other ones. Tallies how many times each sequence
     * is a subtree neighbor of the query. Gene duplications are inferred using
     * SDI. Modifies its arguments gene_trees and species_tree. Method
     * "inferOrthologs(File,Phylogeny)" accomplishes the same, but needs less
     * memory since not all gene Trees have to be kept in memory.
     * <p>
     * To obtain the results use the methods listed below.
     * <p>
     * (Last modified: 12/07/00)
     * 
     * @param gene_trees
     *            a Phylogeny[] which is the result of performing a bootstrap
     *            analysis, nodes of trees must have species names and sequence
     *            names in the appropriate fields
     * @param species_tree
     *            a species Phylogeny, which has species names in its species
     *            fields
     * @see #getInferredOrthologs(String)
     * @see #getInferredSuperOrthologs(String)
     * @see #inferredOrthologsToString(String,int,double,double)
     * @see #inferredOrthologTableToFile(File)
     * @see #inferredSuperOrthologTableToFile(File)
     */
    public void inferOrthologs( final Phylogeny[] gene_trees, final Phylogeny species_tree ) {
        if ( RIO.TIME ) {
            time_ = System.currentTimeMillis();
        }
        // Removes from species_tree all species not found in gene_trees_[0].
        PhylogenyMethods.taxonomyBasedDeletionOfExternalNodes( gene_trees[ 0 ], species_tree );
        seq_names_ = gene_trees[ 0 ].getAllExternalSeqNames();
        if ( ( seq_names_ == null ) || ( seq_names_.length < 1 ) ) {
            return;
        }
        o_hash_maps_ = new HashMap( seq_names_.length );
        so_hash_maps_ = new HashMap( seq_names_.length );
        up_hash_maps_ = new HashMap( seq_names_.length );
        sn_hash_maps_ = new HashMap( seq_names_.length );
        for( int i = 0; i < seq_names_.length; ++i ) {
            o_hash_maps_.put( seq_names_[ i ], new HashMap( seq_names_.length ) );
            so_hash_maps_.put( seq_names_[ i ], new HashMap( seq_names_.length ) );
            up_hash_maps_.put( seq_names_[ i ], new HashMap( seq_names_.length ) );
            sn_hash_maps_.put( seq_names_[ i ], new HashMap( seq_names_.length ) );
        }
        for( int i = 0; i < gene_trees.length; ++i ) {
            doInferOrthologs( gene_trees[ i ], species_tree );
        }
        setBootstraps( gene_trees.length );
        if ( RIO.TIME ) {
            time_ = ( System.currentTimeMillis() - time_ );
        }
    } // inferOrthologs( Phylogeny[], Phylogeny )

    /**
     * Returns an ArrayList containg the names of orthologs of the PhylogenyNode
     * with seq name seq_name.
     * <p>
     * (Last modified: 10/07/01)
     * 
     * @param seq_name
     *            sequence name of a external node of the gene trees
     * @param threshold_orthologs
     *            the minimal number of observations for a a sequence to be
     *            reported as orthologous as percentage (0.0-100.0%)
     * @return ArrayList containg the names of orthologs of the PhylogenyNode
     *         with seq name seq_name
     * @see #inferOrthologs(Phylogeny[],Phylogeny)
     * @see #inferOrthologs(File,Phylogeny)
     * @see #getOrder(int)
     */
    public ArrayList inferredOrthologsToArrayList( final String seq_name, double threshold_orthologs ) {
        HashMap o_hashmap = null;
        String name = null;
        double o = 0.0;
        final ArrayList arraylist = new ArrayList();
        if ( o_hash_maps_ == null ) {
            throw new IllegalStateException( "Orthologs have not been calculated (successfully)." );
        }
        if ( threshold_orthologs < 0.0 ) {
            threshold_orthologs = 0.0;
        }
        else if ( threshold_orthologs > 100.0 ) {
            threshold_orthologs = 100.0;
        }
        o_hashmap = getInferredOrthologs( seq_name );
        if ( o_hashmap == null ) {
            throw new IllegalStateException( "Orthologs for " + seq_name + " were not established." );
        }
        if ( seq_names_.length > 0 ) {
            final ArrayList nv = new ArrayList();
            I: for( int i = 0; i < seq_names_.length; ++i ) {
                name = seq_names_[ i ];
                if ( name.equals( seq_name ) ) {
                    continue I;
                }
                o = getBootstrapValueFromHash( o_hashmap, name );
                if ( o < threshold_orthologs ) {
                    continue I;
                }
                arraylist.add( name );
            } // End of I for loop.
        }
        return arraylist;
    } // inferredOrthologsToArrayList( String, double )

    /**
     * Returns a String containg the names of orthologs of the PhylogenyNode
     * with seq name query_name. The String also contains how many times a
     * particular ortholog has been observed.
     * <p>
     * <ul>
     * The output order is (per line): Name, Ortholog, Subtree neighbor, Super
     * ortholog, Distance
     * </ul>
     * <p>
     * The sort priority of this is determined by sort in the following manner:
     * <ul>
     * <li>0 : Ortholog
     * <li>1 : Ortholog, Super ortholog
     * <li>2 : Super ortholog, Ortholog
     * <li>3 : Ortholog, Distance
     * <li>4 : Distance, Ortholog
     * <li>5 : Ortholog, Super ortholog, Distance
     * <li>6 : Ortholog, Distance, Super ortholog
     * <li>7 : Super ortholog, Ortholog, Distance
     * <li>8 : Super ortholog, Distance, Ortholog
     * <li>9 : Distance, Ortholog, Super ortholog
     * <li>10 : Distance, Super ortholog, Ortholog
     * <li>11 : Ortholog, Subtree neighbor, Distance
     * <li>12 : Ortholog, Subtree neighbor, Super ortholog, Distance (default)
     * <li>13 : Ortholog, Super ortholog, Subtree neighbor, Distance
     * <li>14 : Subtree neighbor, Ortholog, Super ortholog, Distance
     * <li>15 : Subtree neighbor, Distance, Ortholog, Super ortholog
     * <li>16 : Ortholog, Distance, Subtree neighbor, Super ortholog
     * <li>17 : Ortholog, Subtree neighbor, Distance, Super ortholog
     * </ul>
     * <p>
     * Returns "-" if no putative orthologs have been found (given
     * threshold_orthologs).
     * <p>
     * Orthologs are to be inferred by method "inferOrthologs".
     * <p>
     * (Last modified: 05/08/01)
     * 
     * @param query_name
     *            sequence name of a external node of the gene trees
     * @param sort
     *            order and sort priority
     * @param threshold_orthologs
     *            the minimal number of observations for a a sequence to be
     *            reported as orthologous, in percents (0.0-100.0%)
     * @param threshold_subtreeneighborings
     *            the minimal number of observations for a a sequence to be
     *            reported as orthologous, in percents (0.0-100.0%)
     * @return String containing the inferred orthologs, String containing "-"
     *         if no orthologs have been found null in case of error
     * @see #inferOrthologs(File,Phylogeny,String)
     * @see #inferOrthologs(Phylogeny[],Phylogeny)
     * @see #inferOrthologs(File,Phylogeny)
     * @see #getOrder(int)
     */
    public String inferredOrthologsToString( final String query_name,
                                             int sort,
                                             double threshold_orthologs,
                                             double threshold_subtreeneighborings ) {
        HashMap o_hashmap = null, s_hashmap = null, n_hashmap = null;
        String name = "", orthologs = "";
        double o = 0.0, // Orthologs.
        s = 0.0, // Super orthologs.
        sn = 0.0, // Subtree neighbors.
        value1 = 0.0, value2 = 0.0, value3 = 0.0, value4 = 0.0, d = 0.0;
        final ArrayList<Tuplet> nv = new ArrayList<Tuplet>();
        if ( ( o_hash_maps_ == null ) || ( so_hash_maps_ == null ) || ( sn_hash_maps_ == null ) ) {
            throw new IllegalStateException( "Orthologs have not been calculated (successfully)." );
        }
        if ( ( sort < 0 ) || ( sort > 17 ) ) {
            sort = 12;
        }
        if ( ( sort > 2 ) && ( _m == null ) && ( _l == null ) ) {
            throw new IllegalStateException( "Distance list or matrix have not been read in (successfully)." );
        }
        if ( threshold_orthologs < 0.0 ) {
            threshold_orthologs = 0.0;
        }
        else if ( threshold_orthologs > 100.0 ) {
            threshold_orthologs = 100.0;
        }
        if ( threshold_subtreeneighborings < 0.0 ) {
            threshold_subtreeneighborings = 0.0;
        }
        else if ( threshold_subtreeneighborings > 100.0 ) {
            threshold_subtreeneighborings = 100.0;
        }
        o_hashmap = getInferredOrthologs( query_name );
        s_hashmap = getInferredSuperOrthologs( query_name );
        n_hashmap = getInferredSubtreeNeighbors( query_name );
        if ( ( o_hashmap == null ) || ( s_hashmap == null ) || ( n_hashmap == null ) ) {
            throw new IllegalStateException( "Orthologs for " + query_name + " were not established" );
        }
        if ( seq_names_.length > 0 ) {
            I: for( int i = 0; i < seq_names_.length; ++i ) {
                name = seq_names_[ i ];
                if ( name.equals( query_name ) ) {
                    continue I;
                }
                o = getBootstrapValueFromHash( o_hashmap, name );
                if ( o < threshold_orthologs ) {
                    continue I;
                }
                sn = getBootstrapValueFromHash( n_hashmap, name );
                if ( sn < threshold_subtreeneighborings ) {
                    continue I;
                }
                s = getBootstrapValueFromHash( s_hashmap, name );
                if ( sort >= 3 ) {
                    if ( _m != null ) {
                        d = getDistance( query_name, name );
                    }
                    else {
                        d = getDistance( name );
                    }
                }
                switch ( sort ) {
                    case 0:
                        nv.add( new Tuplet( name, o, 5 ) );
                        break;
                    case 1:
                        nv.add( new Tuplet( name, o, s, 5 ) );
                        break;
                    case 2:
                        nv.add( new Tuplet( name, s, o, 5 ) );
                        break;
                    case 3:
                        nv.add( new Tuplet( name, o, d, 1 ) );
                        break;
                    case 4:
                        nv.add( new Tuplet( name, d, o, 0 ) );
                        break;
                    case 5:
                        nv.add( new Tuplet( name, o, s, d, 2 ) );
                        break;
                    case 6:
                        nv.add( new Tuplet( name, o, d, s, 1 ) );
                        break;
                    case 7:
                        nv.add( new Tuplet( name, s, o, d, 2 ) );
                        break;
                    case 8:
                        nv.add( new Tuplet( name, s, d, o, 1 ) );
                        break;
                    case 9:
                        nv.add( new Tuplet( name, d, o, s, 0 ) );
                        break;
                    case 10:
                        nv.add( new Tuplet( name, d, s, o, 0 ) );
                        break;
                    case 11:
                        nv.add( new Tuplet( name, o, sn, d, 2 ) );
                        break;
                    case 12:
                        nv.add( new Tuplet( name, o, sn, s, d, 3 ) );
                        break;
                    case 13:
                        nv.add( new Tuplet( name, o, s, sn, d, 3 ) );
                        break;
                    case 14:
                        nv.add( new Tuplet( name, sn, o, s, d, 3 ) );
                        break;
                    case 15:
                        nv.add( new Tuplet( name, sn, d, o, s, 1 ) );
                        break;
                    case 16:
                        nv.add( new Tuplet( name, o, d, sn, s, 1 ) );
                        break;
                    case 17:
                        nv.add( new Tuplet( name, o, sn, d, s, 2 ) );
                        break;
                    default:
                        nv.add( new Tuplet( name, o, 5 ) );
                }
            } // End of I for loop.
            if ( ( nv != null ) && ( nv.size() > 0 ) ) {
                final Tuplet[] nv_array = new Tuplet[ nv.size() ];
                for( int j = 0; j < nv.size(); ++j ) {
                    nv_array[ j ] = nv.get( j );
                }
                Arrays.sort( nv_array );
                for( int i = 0; i < nv_array.length; ++i ) {
                    name = nv_array[ i ].getKey();
                    value1 = nv_array[ i ].getValue1();
                    value2 = nv_array[ i ].getValue2();
                    value3 = nv_array[ i ].getValue3();
                    value4 = nv_array[ i ].getValue4();
                    orthologs += addNameAndValues( name, value1, value2, value3, value4, sort );
                }
            }
        }
        // No orthologs found.
        if ( ( orthologs == null ) || ( orthologs.length() < 1 ) ) {
            orthologs = "-";
        }
        return orthologs;
    } // inferredOrthologsToString( String, int, double )

    /**
     * Writes the orthologs for each external node of the gene trees to outfile
     * in the form of a table. Orthologs are to be inferred by method
     * "inferOrthologs". Overwrites without asking! (Last modified: 12/07/00)
     * 
     * @param outfile
     *            the File to write to
     * @see #inferOrthologs(Phylogeny[],Phylogeny)
     * @see #inferOrthologs(File,Phylogeny)
     */
    public void inferredOrthologTableToFile( final File outfile ) throws IOException {
        if ( o_hash_maps_ == null ) {
            return;
        }
        inferredOrthologTableToFile( outfile, false );
    } // inferredOrthologTableToFile( File )

    /**
     * Writes the "super orthologs" for each external nodes of the gene trees to
     * outfile in the form of a table. Super orthologs are to be inferred by
     * method "inferOrthologs". Overwrites without asking! (Last modified:
     * 11/28/00)
     * 
     * @param outfile
     *            the File to write to
     * @see #inferOrthologs(Phylogeny[],Phylogeny)
     * @see #inferOrthologs(File,Phylogeny)
     */
    public void inferredSuperOrthologTableToFile( final File outfile ) throws IOException {
        if ( so_hash_maps_ == null ) {
            return;
        }
        inferredOrthologTableToFile( outfile, true );
    } // inferredSuperOrthologTableToFile( File )

    /**
     * Returns a String containg the names of orthologs of the PhylogenyNode
     * with seq name query_name. The String also contains how many times a
     * particular ortholog has been observed. Returns "-" if no putative
     * orthologs have been found (given threshold_orthologs).
     * <p>
     * Orthologs are to be inferred by method "inferOrthologs".
     * <p>
     * (Last modified: 10/16/01)
     * 
     * @param query_name
     *            sequence name of a external node of the gene trees
     * @param return_dists
     * @param threshold_ultra_paralogs
     *            between 1 and 100
     * @return String containing the inferred orthologs, String containing "-"
     *         if no orthologs have been found null in case of error
     * @see #inferOrthologs(File,Phylogeny,String)
     * @see #inferOrthologs(Phylogeny[],Phylogeny)
     * @see #inferOrthologs(File,Phylogeny)
     * @see #getOrder(int)
     */
    public String inferredUltraParalogsToString( final String query_name,
                                                 final boolean return_dists,
                                                 double threshold_ultra_paralogs ) {
        HashMap sp_hashmap = null;
        String name = "", ultra_paralogs = "";
        int sort = 0;
        double sp = 0.0, value1 = 0.0, value2 = 0.0, d = 0.0;
        final ArrayList nv = new ArrayList();
        if ( threshold_ultra_paralogs < 1.0 ) {
            threshold_ultra_paralogs = 1.0;
        }
        else if ( threshold_ultra_paralogs > 100.0 ) {
            threshold_ultra_paralogs = 100.0;
        }
        if ( up_hash_maps_ == null ) {
            throw new IllegalStateException( "Ultra paralogs have not been calculated (successfully)." );
        }
        if ( return_dists && ( _m == null ) && ( _l == null ) ) {
            throw new IllegalStateException( "Distance list or matrix have not been read in (successfully)." );
        }
        sp_hashmap = getInferredUltraParalogs( query_name );
        if ( sp_hashmap == null ) {
            throw new IllegalStateException( "Ultra paralogs for " + query_name + " were not established" );
        }
        if ( seq_names_.length > 0 ) {
            I: for( int i = 0; i < seq_names_.length; ++i ) {
                name = seq_names_[ i ];
                if ( name.equals( query_name ) ) {
                    continue I;
                }
                sp = getBootstrapValueFromHash( sp_hashmap, name );
                if ( sp < threshold_ultra_paralogs ) {
                    continue I;
                }
                if ( return_dists ) {
                    if ( _m != null ) {
                        d = getDistance( query_name, name );
                    }
                    else {
                        d = getDistance( name );
                    }
                    nv.add( new Tuplet( name, sp, d, 1 ) );
                }
                else {
                    nv.add( new Tuplet( name, sp, 5 ) );
                }
            } // End of I for loop.
            if ( ( nv != null ) && ( nv.size() > 0 ) ) {
                final Tuplet[] nv_array = new Tuplet[ nv.size() ];
                for( int j = 0; j < nv.size(); ++j ) {
                    nv_array[ j ] = ( Tuplet ) nv.get( j );
                }
                Arrays.sort( nv_array );
                if ( return_dists ) {
                    sort = 91;
                }
                else {
                    sort = 90;
                }
                for( int i = 0; i < nv_array.length; ++i ) {
                    name = nv_array[ i ].getKey();
                    value1 = nv_array[ i ].getValue1();
                    value2 = nv_array[ i ].getValue2();
                    ultra_paralogs += addNameAndValues( name, value1, value2, 0.0, 0.0, sort );
                }
            }
        }
        // No ultra paralogs found.
        if ( ( ultra_paralogs == null ) || ( ultra_paralogs.length() < 1 ) ) {
            ultra_paralogs = "-";
        }
        return ultra_paralogs;
    } // inferredUltraParalogsToString( String )

    /**
     * Reads in the distance list file dist_list. The file must have the
     * following format.
     * <p>
     * 
     * <pre>
     *  
     *   
     *    
     *         name x
     *         name x
     *         name x
     *         
     *    
     *   
     * </pre>
     * 
     * Number of white spaces does not matter. Such files are produces by
     * modified TREE-PUZZLE.
     * <p>
     * (Last modified: 05/30/01)
     * 
     * @param dist_list
     *            a File containing a distance matrix
     * @see #getDistance(String)
     */
    public void readDistanceList( final File dist_list ) throws IOException {
        String incoming = null;
        StringTokenizer st = null;
        if ( !dist_list.exists() ) {
            throw new IOException( dist_list.getAbsolutePath() + " does not exist." );
        }
        else if ( !dist_list.isFile() ) {
            throw new IOException( dist_list.getAbsolutePath() + " is not a file." );
        }
        _m = null;
        _l = new HashMap();
        final BufferedReader in = new BufferedReader( new FileReader( dist_list ) );
        if ( in == null ) {
            throw new RuntimeException( "readDistanceList: failure to create BufferedReader." );
        }
        while ( ( incoming = in.readLine() ) != null ) {
            st = new StringTokenizer( incoming, " \t\n\r" );
            if ( st.countTokens() == 2 ) {
                _l.put( st.nextToken(), new Double( st.nextToken().toString() ) );
                // Name is key, Double is value.
            }
        }
        in.close();
    } // readDistanceList( File )

    /**
     * Reads in the distance matrix file matrix_file. The file must have the
     * following format.
     * <p>
     * 
     * <pre>
     *  
     *   
     *    
     *         [number of sequences, is ignored, and is not necessary]
     *         name x x x 
     *         name x x x
     *         name x x x
     *         
     *    
     *   
     * </pre>
     * 
     * <p>
     * Number of white spaces does not matter. Such files are produces by PHYLIP
     * PROTDIST. Files produced by TREE-PUZZLE need to be modified to remove the
     * newlines "inside" the rows.
     * <p>
     * (Last modified: 11/28/00)
     * 
     * @param matrix_file
     *            a File containing a distance matrix
     * @see #getDistance(String,String)
     */
    public void readDistanceMatrix( final File matrix_file ) throws IOException {
        String incoming = null;
        StringTokenizer st = null;
        int i = 0, j = 0;
        if ( !matrix_file.exists() ) {
            throw new IOException( matrix_file.getAbsolutePath() + " does not exist." );
        }
        else if ( !matrix_file.isFile() ) {
            throw new IOException( matrix_file.getAbsolutePath() + " is not a file." );
        }
        _m = new HashMap();
        _l = null;
        final BufferedReader in = new BufferedReader( new FileReader( matrix_file ) );
        if ( in == null ) {
            throw new RuntimeException( "readDistanceMatrix: failure to create BufferedReader." );
        }
        i = j = 0;
        while ( ( incoming = in.readLine() ) != null ) {
            st = new StringTokenizer( incoming, " \t\n\r" );
            if ( st.countTokens() > 2 ) {
                final Vector current = new Vector();
                _m.put( st.nextToken(), current ); // Name is key, Vector is
                // value.
                current.addElement( i++ ); // Number at position 0.
                while ( st.hasMoreTokens() ) {
                    current.addElement( st.nextToken() );
                }
                // Sanity check.
                if ( ( j != 0 ) && ( current.size() != j ) ) {
                    throw new RuntimeException( "readDistanceMatrix: " + matrix_file
                            + " might not have the expected format (failed sanity check)." );
                }
                j = current.size();
            }
        }
        in.close();
        // Sanity check.
        if ( ( j - 1 ) != _m.size() ) {
            throw new RuntimeException( "readDistanceMatrix: " + matrix_file
                    + " might not have the expected format (failed sanity check)." );
        }
    } // readDistanceMatrix( File )

    /**
     * Brings this into the same state as immediately after construction.
     */
    public void reset() {
        o_hash_maps_ = null;
        so_hash_maps_ = null;
        up_hash_maps_ = null;
        sn_hash_maps_ = null;
        seq_names_ = null;
        _m = null;
        _l = null;
        bootstraps_ = 1;
        ext_nodes_ = 0;
        time_ = 0;
    } // reset()

    // Helper method for inferredOrthologsToString
    // and inferredUltraParalogsToString.
    // (Last modified: 10/06/01)
    private String addNameAndValues( final String name,
                                     final double value1,
                                     final double value2,
                                     final double value3,
                                     final double value4,
                                     final int sort ) {
        final java.text.DecimalFormat df = new java.text.DecimalFormat( "0.#####" );
        df.setDecimalSeparatorAlwaysShown( false );
        String line = "";
        if ( name.length() < 8 ) {
            line += ( name + "\t\t\t" );
        }
        else if ( name.length() < 16 ) {
            line += ( name + "\t\t" );
        }
        else {
            line += ( name + "\t" );
        }
        switch ( sort ) {
            case 0:
                line += addToLine( value1, df );
                line += "-\t";
                line += "-\t";
                line += "-\t";
                break;
            case 1:
                line += addToLine( value1, df );
                line += "-\t";
                line += addToLine( value2, df );
                line += "-\t";
                break;
            case 2:
                line += addToLine( value2, df );
                line += "-\t";
                line += addToLine( value1, df );
                line += "-\t";
                break;
            case 3:
                line += addToLine( value1, df );
                line += "-\t";
                line += "-\t";
                line += addToLine( value2, df );
                break;
            case 4:
                line += addToLine( value2, df );
                line += "-\t";
                line += "-\t";
                line += addToLine( value1, df );
                break;
            case 5:
                line += addToLine( value1, df );
                line += "-\t";
                line += addToLine( value2, df );
                line += addToLine( value3, df );
                break;
            case 6:
                line += addToLine( value1, df );
                line += "-\t";
                line += addToLine( value3, df );
                line += addToLine( value2, df );
                break;
            case 7:
                line += addToLine( value2, df );
                line += "-\t";
                line += addToLine( value1, df );
                line += addToLine( value3, df );
                break;
            case 8:
                line += addToLine( value3, df );
                line += "-\t";
                line += addToLine( value1, df );
                line += addToLine( value2, df );
                break;
            case 9:
                line += addToLine( value2, df );
                line += "-\t";
                line += addToLine( value3, df );
                line += addToLine( value1, df );
                break;
            case 10:
                line += addToLine( value3, df );
                line += "-\t";
                line += addToLine( value2, df );
                line += addToLine( value1, df );
                break;
            case 11:
                line += addToLine( value1, df );
                line += addToLine( value2, df );
                line += "-\t";
                line += addToLine( value3, df );
                break;
            case 12:
                line += addToLine( value1, df );
                line += addToLine( value2, df );
                line += addToLine( value3, df );
                line += addToLine( value4, df );
                break;
            case 13:
                line += addToLine( value1, df );
                line += addToLine( value3, df );
                line += addToLine( value2, df );
                line += addToLine( value4, df );
                break;
            case 14:
                line += addToLine( value2, df );
                line += addToLine( value1, df );
                line += addToLine( value3, df );
                line += addToLine( value4, df );
                break;
            case 15:
                line += addToLine( value3, df );
                line += addToLine( value1, df );
                line += addToLine( value4, df );
                line += addToLine( value2, df );
                break;
            case 16:
                line += addToLine( value1, df );
                line += addToLine( value3, df );
                line += addToLine( value4, df );
                line += addToLine( value2, df );
                break;
            case 17:
                line += addToLine( value1, df );
                line += addToLine( value2, df );
                line += addToLine( value4, df );
                line += addToLine( value3, df );
                break;
            case 90:
                line += addToLine( value1, df );
                line += "-\t";
                break;
            case 91:
                line += addToLine( value1, df );
                line += addToLine( value2, df );
                break;
        }
        line += "\n";
        return line;
    } // addNameAndValues

    // Helper for addNameAndValues.
    // (Last modified: 05/08/01)
    private String addToLine( final double value, final java.text.DecimalFormat df ) {
        String s = "";
        if ( value != Tuplet.DEFAULT ) {
            s = df.format( value ) + "\t";
        }
        else {
            s = "-\t";
        }
        return s;
    } // addToLine( double, java.text.DecimalFormat )

    // Helper method which performs the actual ortholog inference for
    // each sequence in gene_tree.
    // (Last modified: 10/07/01)
    private void doInferOrthologs( final Phylogeny gene_tree, final Phylogeny species_tree ) {
        Phylogeny assigned_tree = null;
        PhylogenyNode node = null;
        final SDIR sdiunrooted = new SDIR();
        List orthologs = null, super_orthologs = null, ultra_paralogs = null, subtree_neighbors = null;
        assigned_tree = sdiunrooted.infer( gene_tree,
                                           species_tree,
                                           RIO.ROOT_BY_MINIMIZING_MAPPING_COST,
                                           RIO.ROOT_BY_MINIMIZING_SUM_OF_DUPS,
                                           RIO.ROOT_BY_MINIMIZING_TREE_HEIGHT,
                                           true,
                                           1 )[ 0 ];
        setExtNodesOfAnalyzedGeneTrees( assigned_tree.getNumberOfExternalNodes() );
        node = assigned_tree.getFirstExternalNode();
        while ( node != null ) {
            orthologs = PhylogenyMethods.getOrthologousNodes( node );
            updateHash( o_hash_maps_, node, orthologs );
            super_orthologs = PhylogenyMethods.getSuperOrthologousNodes( node );
            updateHash( so_hash_maps_, node, super_orthologs );
            subtree_neighbors = PhylogenyMethods.getSubtreeNeighbors( node );
            updateHash( sn_hash_maps_, node, subtree_neighbors );
            ultra_paralogs = PhylogenyMethods.getUltraParalogousNodes( node );
            updateHash( up_hash_maps_, node, ultra_paralogs );
            node = node.getNextExternalNode();
        }
    } // doInferOrthologs( Phylogeny, Phylogeny )

    // Helper method which performs the actual ortholog inference for
    // the external node with seqname query.
    // (Last modified: 10/07/01)
    private void doInferOrthologs( final Phylogeny gene_tree, final Phylogeny species_tree, final String query ) {
        Phylogeny assigned_tree = null;
        PhylogenyNode node = null;
        final SDIR sdiunrooted = new SDIR();
        List orthologs = null, super_orthologs = null, ultra_paralogs = null, subtree_neighbors = null;
        assigned_tree = sdiunrooted.infer( gene_tree,
                                           species_tree,
                                           RIO.ROOT_BY_MINIMIZING_MAPPING_COST,
                                           RIO.ROOT_BY_MINIMIZING_SUM_OF_DUPS,
                                           RIO.ROOT_BY_MINIMIZING_TREE_HEIGHT,
                                           true,
                                           1 )[ 0 ];
        setExtNodesOfAnalyzedGeneTrees( assigned_tree.getNumberOfExternalNodes() );
        node = assigned_tree.getNode( query );
        orthologs = PhylogenyMethods.getOrthologousNodes( node );
        updateHash( o_hash_maps_, node, orthologs );
        super_orthologs = PhylogenyMethods.getSuperOrthologousNodes( node );
        updateHash( so_hash_maps_, node, super_orthologs );
        subtree_neighbors = PhylogenyMethods.getSubtreeNeighbors( node );
        updateHash( sn_hash_maps_, node, subtree_neighbors );
        ultra_paralogs = PhylogenyMethods.getUltraParalogousNodes( node );
        updateHash( up_hash_maps_, node, ultra_paralogs );
    } // doInferOrthologs( Phylogeny, Phylogeny, String )

    // Helper method for inferredOrthologsToString.
    // inferredOrthologsToArrayList,
    // and inferredUltraParalogsToString.
    // (Last modified: 10/06/01)
    private double getBootstrapValueFromHash( final HashMap<String, Integer> h, final String name ) {
        if ( !h.containsKey( name ) ) {
            return 0.0;
        }
        final int i = h.get( name );
        return ( i * 100.0 / getBootstraps() );
    }

    // Helper method for inferredOrthologTableToFile.
    // Returns individual rows for the table as String.
    // (Last modified: 11/14/00)
    private String inferredOrthologsToTableHelper( final String name2,
                                                   final String[] names,
                                                   final int j,
                                                   final boolean super_orthologs ) {
        HashMap<String, Integer> hashmap = null;
        String name = null, orthologs = new String( "" );
        int value = 0;
        if ( !super_orthologs ) {
            hashmap = getInferredOrthologs( name2 );
        }
        else {
            hashmap = getInferredSuperOrthologs( name2 );
        }
        if ( hashmap == null ) {
            throw new RuntimeException( "Unexpected failure in method inferredOrthologsToTableHelper" );
        }
        for( int i = 0; i < names.length; ++i ) {
            name = names[ i ];
            if ( !hashmap.containsKey( name ) ) {
                value = 0;
            }
            else {
                value = hashmap.get( name );
            }
            if ( i == j ) {
                // Sanity check.
                if ( value != 0 ) {
                    throw new RuntimeException( "Failed sanity check in method inferredOrthologsToTableHelper: value not 0." );
                }
                orthologs += ( " " + "\t" );
            }
            else {
                orthologs += ( value + "\t" );
            }
        }
        return orthologs;
    } // inferredOrthologsToTableHelper

    // Helper for inferredOrthologTableToFile(File).
    // (Last modified: 11/28/00)
    private void inferredOrthologTableToFile( final File outfile, final boolean super_orthologs ) throws IOException {
        String name = "", line = "";
        PrintWriter out = null;
        if ( seq_names_ == null ) {
            throw new IllegalStateException( "inferredOrthologTableToFile: seq_names_ is null." );
        }
        Arrays.sort( seq_names_ );
        out = new PrintWriter( new FileWriter( outfile ), true );
        if ( out == null ) {
            throw new RuntimeException( "inferredOrthologTableToFile: failure to create PrintWriter." );
        }
        line = "\t\t\t\t";
        for( int i = 0; i < seq_names_.length; ++i ) {
            line += ( i + ")\t" );
        }
        line += "\n";
        out.println( line );
        for( int i = 0; i < seq_names_.length; ++i ) {
            name = seq_names_[ i ];
            if ( name.length() < 8 ) {
                line = i + ")\t" + name + "\t\t\t";
            }
            else if ( name.length() < 16 ) {
                line = i + ")\t" + name + "\t\t";
            }
            else {
                line = i + ")\t" + name + "\t";
            }
            line += inferredOrthologsToTableHelper( name, seq_names_, i, super_orthologs );
            out.println( line );
        }
        out.close();
    } // inferredOrthologTableToFile( File, boolean )

    /**
     * Sets the numbers of trees analyzed.
     * <p>
     * (Last modified: 02/20/00)
     * 
     * @param the
     *            numbers of trees analyzed
     * @see #inferOrthologs(Phylogeny[],Phylogeny)
     * @see #inferOrthologs(File,Phylogeny)
     */
    private void setBootstraps( int i ) {
        if ( i < 1 ) {
            i = 1;
        }
        bootstraps_ = i;
    }

    /**
     * Sets number of ext nodes in gene trees analyzed (after stripping).
     * <p>
     * (Last modified: 05/02/00)
     * 
     * @param the
     *            number of ext nodes in gene trees analyzed (after stripping)
     * @see #inferOrthologs(Phylogeny[],Phylogeny)
     * @see #inferOrthologs(File,Phylogeny)
     */
    private void setExtNodesOfAnalyzedGeneTrees( int i ) {
        if ( i < 1 ) {
            i = 0;
        }
        ext_nodes_ = i;
    }

    // Helper for doInferOrthologs( Phylogeny, Phylogeny, String )
    // and doInferOrthologs( Phylogeny, Phylogeny ).
    // (Last modified: 08/16/06)
    private void updateHash( final HashMap h, final PhylogenyNode n, final List v ) {
        String name = null;
        int i = 0;
        final HashMap<String, Integer> hash_map = ( HashMap<String, Integer> ) h.get( n.getNodeName() );
        if ( hash_map == null ) {
            throw new RuntimeException( "Unexpected failure in method updateHash." );
        }
        for( int j = 0; j < v.size(); ++j ) {
            name = ( ( PhylogenyNode ) v.get( j ) ).getNodeName();
            if ( hash_map.containsKey( name ) ) {
                i = hash_map.get( name );
                i++;
            }
            else {
                hash_map.put( name, 1 );
            }
        }
    }

    /**
     * Rounds a double to a int.
     * <p>
     * (Last modified: 03/15/01)
     * 
     * @param d
     *            a double to be rounded to a int
     * @return d rounded to a int
     */
    public static int roundToInt( final double d ) {
        int i = 0;
        i = ( int ) ( d + 0.5 );
        return i;
    }
}
