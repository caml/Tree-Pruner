// $Id: NodePanel.java,v 1.2 2009/06/19 05:32:22 cmzmasek Exp $
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
// WWW: www.phylosoft.org/

package org.forester.archaeopteryx;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.SortedMap;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.Position;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.forester.phylogeny.PhylogenyMethods;
import org.forester.phylogeny.PhylogenyNode;
import org.forester.phylogeny.data.Annotation;
import org.forester.phylogeny.data.BinaryCharacters;
import org.forester.phylogeny.data.Date;
import org.forester.phylogeny.data.Distribution;
import org.forester.phylogeny.data.Event;
import org.forester.phylogeny.data.PhylogenyData;
import org.forester.phylogeny.data.PropertiesMap;
import org.forester.phylogeny.data.Property;
import org.forester.phylogeny.data.Reference;
import org.forester.phylogeny.data.Sequence;
import org.forester.phylogeny.data.Taxonomy;
import org.forester.util.ForesterUtil;

class NodePanel extends JPanel implements TreeSelectionListener {

    private static final String       EVENTS           = "Events";
    private static final String       SEQUENCE         = "Sequence";
    private static final String       TAXONOMY         = "Taxonomy";
    private static final String       BASIC            = "Basic";
    private final static NumberFormat FORMATTER_3      = new DecimalFormat( "#.###" );
    private static final long         serialVersionUID = 5120159904388100771L;
    private final JTree               _tree;
    private final JEditorPane         _pane;

    public NodePanel( final PhylogenyNode phylogeny_node ) {
        String node_name = "";
        if ( !ForesterUtil.isEmpty( phylogeny_node.getNodeName() ) ) {
            node_name = phylogeny_node.getNodeName() + " ";
        }
        final DefaultMutableTreeNode top = new DefaultMutableTreeNode( "Node " + node_name );
        createNodes( top, phylogeny_node );
        _tree = new JTree( top );
        _tree.setEditable( true );
        getJTree().setToggleClickCount( 1 );
        expandPath( BASIC );
        expandPath( TAXONOMY );
        expandPath( SEQUENCE );
        expandPath( EVENTS );
        final JScrollPane tree_view = new JScrollPane( getJTree() );
        _pane = new JEditorPane();
        _pane.setEditable( false );
        final JScrollPane data_view = new JScrollPane( _pane );
        final JSplitPane split_pane = new JSplitPane( JSplitPane.VERTICAL_SPLIT );
        split_pane.setTopComponent( tree_view );
        split_pane.setBottomComponent( data_view );
        data_view.setMinimumSize( Constants.NODE_PANEL_SPLIT_MINIMUM_SIZE );
        tree_view.setMinimumSize( Constants.NODE_PANEL_SPLIT_MINIMUM_SIZE );
        split_pane.setDividerLocation( 400 );
        split_pane.setPreferredSize( Constants.NODE_PANEL_SIZE );
        add( split_pane );
    }

    public void valueChanged( final TreeSelectionEvent e ) {
        // Do nothing.
    }

    private void expandPath( final String name ) {
        final TreePath tp = getJTree().getNextMatch( name, 0, Position.Bias.Forward );
        if ( tp != null ) {
            getJTree().expandPath( tp );
        }
    }

    private JTree getJTree() {
        return _tree;
    }

    private static void addAnnotation( final DefaultMutableTreeNode top, final Annotation ann, final String name ) {
        DefaultMutableTreeNode category;
        category = new DefaultMutableTreeNode( name );
        top.add( category );
        addSubelement( category, "Reference", ann.getRef() );
        addSubelement( category, "Description", ann.getDesc() );
        addSubelement( category, "Source", ann.getSource() );
        addSubelement( category, "Type", ann.getType() );
        addSubelement( category, "Evidence", ann.getEvidence() );
        if ( ann.getConfidence() != null ) {
            addSubelement( category, "Confidence", ann.getConfidence().asText().toString() );
        }
        if ( ann.getProperties() != null ) {
            addProperties( category, ann.getProperties(), "Properties" );
        }
    }

    private static void addAnnotations( final DefaultMutableTreeNode top,
                                        final List<PhylogenyData> annotations,
                                        final DefaultMutableTreeNode category ) {
        if ( ( annotations != null ) && ( annotations.size() > 0 ) ) {
            category.add( new DefaultMutableTreeNode( "Annotations" ) );
            final DefaultMutableTreeNode last = top.getLastLeaf();
            int i = 0;
            for( final PhylogenyData ann : annotations ) {
                addAnnotation( last, ( Annotation ) ann, "Annotation " + ( i++ ) );
            }
        }
    }

    private static void addBasics( final DefaultMutableTreeNode top,
                                   final PhylogenyNode phylogeny_node,
                                   final String name ) {
        final DefaultMutableTreeNode category = new DefaultMutableTreeNode( name );
        top.add( category );
        addSubelement( category, "Name", phylogeny_node.getNodeName() );
        if ( phylogeny_node.getDistanceToParent() != PhylogenyNode.DISTANCE_DEFAULT ) {
            addSubelement( category, "Branch length", String.valueOf( FORMATTER_3.format( ForesterUtil
                    .round( phylogeny_node.getDistanceToParent(), 3 ) ) ) );
        }
        if ( phylogeny_node.getBranchData().isHasConfidences() ) {
            for( final PhylogenyData conf : phylogeny_node.getBranchData().getConfidences() ) {
                addSubelement( category, "Confidence", conf.asText().toString() );
            }
        }
        if ( !phylogeny_node.isExternal() ) {
            addSubelement( category, "Children", String.valueOf( phylogeny_node.getNumberOfDescendants() ) );
            addSubelement( category, "External children", String.valueOf( phylogeny_node.getAllExternalDescendants()
                    .size() ) );
            final int tax_count = PhylogenyMethods.calculateSumOfDistinctTaxonomies( phylogeny_node );
            if ( tax_count > 0 ) {
                addSubelement( category, "Distinct external taxonomies", String.valueOf( tax_count ) );
            }
        }
        if ( !phylogeny_node.isRoot() ) {
            addSubelement( category, "Depth", String.valueOf( PhylogenyMethods.calculateDepth( phylogeny_node ) ) );
            final double d = PhylogenyMethods.calculateDistanceToRoot( phylogeny_node );
            if ( d > 0 ) {
                addSubelement( category, "Distance to root", String.valueOf( FORMATTER_3
                        .format( ForesterUtil.round( d, 3 ) ) ) );
            }
        }
    }

    private static void addBinaryCharacters( final DefaultMutableTreeNode top,
                                             final BinaryCharacters bc,
                                             final String name ) {
        DefaultMutableTreeNode category;
        category = new DefaultMutableTreeNode( name );
        top.add( category );
        addSubelement( category, "Gained", String.valueOf( bc.getGainedCount() ) );
        addSubelement( category, "Lost", String.valueOf( bc.getLostCount() ) );
        addSubelement( category, "Present", String.valueOf( bc.getPresentCount() ) );
        final DefaultMutableTreeNode chars = new DefaultMutableTreeNode( "Lists" );
        category.add( chars );
        addSubelement( chars, "Gained", bc.getGainedCharactersAsStringBuffer().toString() );
        addSubelement( chars, "Lost", bc.getLostCharactersAsStringBuffer().toString() );
        addSubelement( chars, "Present", bc.getPresentCharactersAsStringBuffer().toString() );
    }

    private static void addDate( final DefaultMutableTreeNode top, final Date date, final String name ) {
        DefaultMutableTreeNode category;
        category = new DefaultMutableTreeNode( name );
        top.add( category );
        addSubelement( category, "Description", date.getDesc() );
        addSubelement( category, "Value", String.valueOf( date.getValue() ) );
        addSubelement( category, "Unit", date.getUnit() );
        addSubelement( category, "Range", String.valueOf( date.getRange() ) );
    }

    private static void addDistribution( final DefaultMutableTreeNode top, final Distribution dist, final String name ) {
        DefaultMutableTreeNode category;
        category = new DefaultMutableTreeNode( name );
        top.add( category );
        addSubelement( category, "Description", dist.getDesc() );
        addSubelement( category, "Geodetic datum", dist.getGeodeticDatum() );
        addSubelement( category, "Latitude", String.valueOf( dist.getLatitude() ) );
        addSubelement( category, "Longitude", String.valueOf( dist.getLongitude() ) );
        addSubelement( category, "Altitude", String.valueOf( dist.getAltitude() ) );
    }

    private static void addEvents( final DefaultMutableTreeNode top, final Event events, final String name ) {
        DefaultMutableTreeNode category;
        category = new DefaultMutableTreeNode( name );
        top.add( category );
        if ( events.getNumberOfDuplications() > 0 ) {
            addSubelement( category, "Duplications", String.valueOf( events.getNumberOfDuplications() ) );
        }
        if ( events.getNumberOfSpeciations() > 0 ) {
            addSubelement( category, "Speciations", String.valueOf( events.getNumberOfSpeciations() ) );
        }
        if ( events.getNumberOfGeneLosses() > 0 ) {
            addSubelement( category, "Gene losses", String.valueOf( events.getNumberOfGeneLosses() ) );
        }
        addSubelement( category, "Type", events.getEventType().toString() );
        if ( events.getConfidence() != null ) {
            addSubelement( category, "Confidence", events.getConfidence().asText().toString() );
        }
    }

    private static void addProperties( final DefaultMutableTreeNode top,
                                       final PropertiesMap properties,
                                       final String string ) {
        final SortedMap<String, Property> properties_map = properties.getProperties();
        final DefaultMutableTreeNode category = new DefaultMutableTreeNode( "Properties " );
        top.add( category );
        for( final String key : properties_map.keySet() ) {
            final Property prop = properties_map.get( key );
            category.add( new DefaultMutableTreeNode( prop.getRef() + " " + prop.getValue() + " " + prop.getUnit()
                    + " [" + prop.getAppliesTo().toString() + "]" ) );
        }
    }

    private static void addReference( final DefaultMutableTreeNode top, final Reference ref, final String name ) {
        final DefaultMutableTreeNode category = new DefaultMutableTreeNode( name );
        top.add( category );
        addSubelement( category, "DOI", ref.getDoi() );
        addSubelement( category, "Description", ref.getValue() );
    }

    private static void addSequence( final DefaultMutableTreeNode top, final Sequence seq, final String name ) {
        final DefaultMutableTreeNode category = new DefaultMutableTreeNode( name );
        top.add( category );
        addSubelement( category, "Name", seq.getName() );
        addSubelement( category, "Symbol", seq.getSymbol() );
        if ( seq.getAccession() != null ) {
            addSubelement( category, "Accession", seq.getAccession().asText().toString() );
        }
        addSubelement( category, "Location", seq.getLocation() );
        addSubelement( category, "Type", seq.getType() );
        addSubelement( category, SEQUENCE, seq.getMolecularSequence() );
        addAnnotations( top, seq.getAnnotations(), category );
    }

    private static void addSubelement( final DefaultMutableTreeNode node, final String name, final String value ) {
        if ( !ForesterUtil.isEmpty( value ) ) {
            node.add( new DefaultMutableTreeNode( name + ": " + value ) );
        }
    }

    private static void addTaxonomy( final DefaultMutableTreeNode top, final Taxonomy tax, final String name ) {
        final DefaultMutableTreeNode category = new DefaultMutableTreeNode( name );
        top.add( category );
        addSubelement( category, "Code", tax.getTaxonomyCode() );
        addSubelement( category, "Scientific name", tax.getScientificName() );
        addSubelement( category, "Common name", tax.getCommonName() );
        if ( tax.getIdentifier() != null ) {
            addSubelement( category, "Identifier", tax.getIdentifier().asText().toString() );
        }
        addSubelement( category, "Rank", tax.getRank() );
    }

    private static void createNodes( final DefaultMutableTreeNode top, final PhylogenyNode phylogeny_node ) {
        addBasics( top, phylogeny_node, BASIC );
        // Taxonomy
        if ( phylogeny_node.getNodeData().isHasTaxonomy() ) {
            addTaxonomy( top, phylogeny_node.getNodeData().getTaxonomy(), TAXONOMY );
        }
        // Sequence
        if ( phylogeny_node.getNodeData().isHasSequence() ) {
            addSequence( top, phylogeny_node.getNodeData().getSequence(), SEQUENCE );
        }
        // Events
        if ( phylogeny_node.getNodeData().isHasEvent() ) {
            addEvents( top, phylogeny_node.getNodeData().getEvent(), EVENTS );
        }
        // Date
        if ( phylogeny_node.getNodeData().isHasDate() ) {
            addDate( top, phylogeny_node.getNodeData().getDate(), "Date" );
        }
        // Distribution
        if ( phylogeny_node.getNodeData().isHasDistribution() ) {
            addDistribution( top, phylogeny_node.getNodeData().getDistribution(), "Distribution" );
        }
        // Reference
        if ( phylogeny_node.getNodeData().isHasReference() ) {
            addReference( top, phylogeny_node.getNodeData().getReference(), "Reference" );
        }
        // BinaryCharacters
        if ( phylogeny_node.getNodeData().isHasBinaryCharacters() ) {
            addBinaryCharacters( top, phylogeny_node.getNodeData().getBinaryCharacters(), "Binary characters" );
        }
        // Properties
        if ( phylogeny_node.getNodeData().isHasProperties() ) {
            addProperties( top, phylogeny_node.getNodeData().getProperties(), "Properties" );
        }
    }
}
