// $Id: Options.java,v 1.12 2009/06/15 18:31:33 cmzmasek Exp $
// FORESTER -- software libraries and applications
// for evolutionary biology research and applications.
//
// Copyright (C) 2009 Christian M. Zmasek
// Copyright (C) 2009 Burnham Institute for Medical Research
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
/**
 * NOTE - The original file was obtained from SourceForge.net (ATV Version 4.1.04) on 2009.07.02
 *  and was modified by the LANL Influenza Sequence Database IT team (flu@lanl.gov)
 */
import java.awt.Font;

import org.forester.util.ForesterUtil;

//******************************************START**********************************************************//
import com.lanl.application.TPTD.applet.AppletParams;
//********************************************END**********************************************************//

/*
 * This is to hold changeable options.
 */
//******************************************START CHANGED**********************************************************//
  public class Options {
 //final class Options { // final->public - changed
 //********************************************END**********************************************************//

    static final double             MIN_CONFIDENCE_DEFAULT = 0.0;
    private boolean                 _show_node_boxes;
    private boolean                 _show_branch_length_values;
    private boolean                 _internal_number_are_confidence_for_nh_parsing;
    private boolean                 _show_scale;
    private boolean                 _show_overview;
    private boolean                 _antialias_screen;
    private boolean                 _antialias_print;
    private boolean                 _graphics_export_visible_only;
    private int                     _print_size_x;
    private int                     _print_size_y;
    private double                  _min_confidence_value;
    private boolean                 _print_black_and_white;
    private boolean                 _print_using_actual_size;
    private boolean                 _graphics_export_using_actual_size;
    private PHYLOGENY_GRAPHICS_TYPE _phylogeny_graphics_type;
    private OVERVIEW_PLACEMENT_TYPE _ov_placement;
    private NODE_LABEL_DIRECTION    _node_label_direction;
    private Font                    _base_font;
    private boolean                 _match_whole_terms_only;
    private boolean                 _search_case_sensitive;
    private boolean                 _non_lined_up_cladogram;
    private double                  _print_line_width;
    private boolean                 _inverse_search_result;

    private Options() {
        init();
    }

    Font getBaseFont() {
        return _base_font;
    }

    double getMinConfidenceValue() {
        return _min_confidence_value;
    }

    NODE_LABEL_DIRECTION getNodeLabelDirection() {
        return _node_label_direction;
    }

    OVERVIEW_PLACEMENT_TYPE getOvPlacement() {
        return _ov_placement;
    }

    PHYLOGENY_GRAPHICS_TYPE getPhylogenyGraphicsType() {
        return _phylogeny_graphics_type;
    }

    double getPrintLineWidth() {
        return _print_line_width;
    }

    int getPrintSizeX() {
        return _print_size_x;
    }

    int getPrintSizeY() {
        return _print_size_y;
    }

    boolean isAntialiasPrint() {
        return _antialias_print;
    }

    boolean isAntialiasScreen() {
        return _antialias_screen;
    }

    boolean isGraphicsExportUsingActualSize() {
        return _graphics_export_using_actual_size;
    }

    boolean isGraphicsExportVisibleOnly() {
        return _graphics_export_visible_only;
    }

    boolean isInternalNumberAreConfidenceForNhParsing() {
        return _internal_number_are_confidence_for_nh_parsing;
    }

    boolean isInverseSearchResult() {
        return _inverse_search_result;
    }

    boolean isMatchWholeTermsOnly() {
        return _match_whole_terms_only;
    }

    boolean isNonLinedUpCladogram() {
        return _non_lined_up_cladogram;
    }

    boolean isPrintBlackAndWhite() {
        return _print_black_and_white;
    }

    boolean isPrintUsingActualSize() {
        return _print_using_actual_size;
    }

    boolean isSearchCaseSensitive() {
        return _search_case_sensitive;
    }

    boolean isShowBranchLengthValues() {
        return _show_branch_length_values;
    }

    boolean isShowNodeBoxes() {
        return _show_node_boxes;
    }

    boolean isShowOverview() {
        return _show_overview;
    }

    boolean isShowScale() {
        return _show_scale;
    }

    void setAntialiasPrint( final boolean antialias_print ) {
        _antialias_print = antialias_print;
    }

    void setAntialiasScreen( final boolean antialias_screen ) {
        _antialias_screen = antialias_screen;
    }

    void setBaseFont( final Font base_font ) {
        _base_font = base_font;
    }

    void setGraphicsExportUsingActualSize( final boolean graphics_export_using_actual_size ) {
        _graphics_export_using_actual_size = graphics_export_using_actual_size;
        if ( !graphics_export_using_actual_size ) {
            setGraphicsExportVisibleOnly( false );
        }
    }

    void setGraphicsExportVisibleOnly( final boolean graphics_export_visible_only ) {
        _graphics_export_visible_only = graphics_export_visible_only;
        if ( graphics_export_visible_only ) {
            setGraphicsExportUsingActualSize( true );
        }
    }

    void setInternalNumberAreConfidenceForNhParsing( final boolean internal_number_are_confidence_for_nh_parsing ) {
        _internal_number_are_confidence_for_nh_parsing = internal_number_are_confidence_for_nh_parsing;
    }

    void setInverseSearchResult( final boolean inverse_search_result ) {
        _inverse_search_result = inverse_search_result;
    }

    void setMatchWholeTermsOnly( final boolean search_whole_words_only ) {
        _match_whole_terms_only = search_whole_words_only;
    }

    void setMinConfidenceValue( final double min_confidence_value ) {
        _min_confidence_value = min_confidence_value;
    }

    void setNodeLabelDirection( final NODE_LABEL_DIRECTION node_label_direction ) {
        _node_label_direction = node_label_direction;
    }

    void setNonLinedUpCladogram( final boolean non_lined_up_cladogram ) {
        _non_lined_up_cladogram = non_lined_up_cladogram;
    }

    void setOvPlacement( final OVERVIEW_PLACEMENT_TYPE ov_placement ) {
        _ov_placement = ov_placement;
    }

    void setPhylogenyGraphicsType( final PHYLOGENY_GRAPHICS_TYPE phylogeny_graphics_type ) {
        _phylogeny_graphics_type = phylogeny_graphics_type;
    }

    void setPrintBlackAndWhite( final boolean print_black_and_white ) {
        _print_black_and_white = print_black_and_white;
    }

    void setPrintLineWidth( final double print_line_width ) {
        _print_line_width = print_line_width;
    }

    void setPrintSizeX( final int print_size_x ) {
        _print_size_x = print_size_x;
    }

    void setPrintSizeY( final int print_size_y ) {
        _print_size_y = print_size_y;
    }

    void setPrintUsingActualSize( final boolean print_using_actual_size ) {
        _print_using_actual_size = print_using_actual_size;
    }

    void setSearchCaseSensitive( final boolean search_case_sensitive ) {
        _search_case_sensitive = search_case_sensitive;
    }

    void setShowBranchLengthValues( final boolean show_branch_length_values ) {
        _show_branch_length_values = show_branch_length_values;
    }

    void setShowNodeBoxes( final boolean show_node_boxes ) {
        _show_node_boxes = show_node_boxes;
    }

    void setShowOverview( final boolean show_overview ) {
        _show_overview = show_overview;
    }

    void setShowScale( final boolean show_scale ) {
        _show_scale = show_scale;
    }

    private void init() {
    	//******************************************START **********************************************************//
    	if(AppletParams.isTreeDecorator()){
    		_show_node_boxes = true;
    	}
    	else
    	//********************************************END**********************************************************//
        _show_node_boxes = false;
        _show_branch_length_values = false;
        _internal_number_are_confidence_for_nh_parsing = false;
        _show_scale = false;
        _antialias_screen = true;
        _antialias_print = true;
        _graphics_export_visible_only = false;
        if ( Util.isUsOrCanada() ) {
            _print_size_x = Constants.US_LETTER_SIZE_X;
            _print_size_y = Constants.US_LETTER_SIZE_Y;
        }
        else {
            _print_size_x = Constants.A4_SIZE_X;
            _print_size_y = Constants.A4_SIZE_Y;
        }
        _min_confidence_value = MIN_CONFIDENCE_DEFAULT;
        _print_black_and_white = false;
        _print_using_actual_size = false;
        _graphics_export_using_actual_size = true;
        _phylogeny_graphics_type = PHYLOGENY_GRAPHICS_TYPE.RECTANGULAR;
        _base_font = new Font( Configuration.getDefaultFontFamilyName(), Font.PLAIN, 10 );
        _match_whole_terms_only = false;
        _search_case_sensitive = false;
        _print_line_width = Constants.PDF_LINE_WIDTH_DEFAULT;
        _show_overview = true;
        _ov_placement = OVERVIEW_PLACEMENT_TYPE.UPPER_LEFT;
        _node_label_direction = NODE_LABEL_DIRECTION.HORIZONTAL;
        _inverse_search_result = false;
    }

    static Options createDefaultInstance() {
        return new Options();
    }

    static Options createInstance( final Configuration configuration ) {
        final Options instance = createDefaultInstance();
        if ( configuration != null ) {
            instance.setAntialiasScreen( configuration.isAntialiasScreen() );
            instance.setShowScale( configuration.isShowScale() );
            instance.setShowBranchLengthValues( configuration.isShowBranchLengthValues() );
            instance.setShowOverview( configuration.isShowOverview() );
            instance.setNonLinedUpCladogram( configuration.isNonLinedUpCladogram() );
            instance.setOvPlacement( configuration.getOvPlacement() );
            instance.setPrintLineWidth( configuration.getPrintLineWidth() );
            instance.setNodeLabelDirection( configuration.getNodeLabelDirection() );
            if ( configuration.getMinConfidenceValue() != MIN_CONFIDENCE_DEFAULT ) {
                instance.setMinConfidenceValue( configuration.getMinConfidenceValue() );
            }
            if ( configuration.getGraphicsExportX() > 0 ) {
                instance.setPrintSizeX( configuration.getGraphicsExportX() );
            }
            if ( configuration.getGraphicsExportY() > 0 ) {
                instance.setPrintSizeY( configuration.getGraphicsExportY() );
            }
            if ( configuration.getBaseFontSize() > 0 ) {
                instance.setBaseFont( instance.getBaseFont().deriveFont( ( float ) configuration.getBaseFontSize() ) );
            }
            if ( !ForesterUtil.isEmpty( configuration.getBaseFontFamilyName() ) ) {
                instance.setBaseFont( new Font( configuration.getBaseFontFamilyName(), Font.PLAIN, instance
                        .getBaseFont().getSize() ) );
            }
            if ( configuration.getPhylogenyGraphicsType() != null ) {
                instance.setPhylogenyGraphicsType( configuration.getPhylogenyGraphicsType() );
            }
        }
        return instance;
    }

    public enum NODE_LABEL_DIRECTION {
        HORIZONTAL, RADIAL;
    }

    enum OVERVIEW_PLACEMENT_TYPE {
        UPPER_LEFT( "upper left" ),
        UPPER_RIGHT( "upper right" ),
        LOWER_LEFT( "lower left" ),
        LOWER_RIGHT( "lower right" );

        private final String _name;

        private OVERVIEW_PLACEMENT_TYPE( final String name ) {
            _name = name;
        }

        @Override
        public String toString() {
            return _name;
        }

        public String toTag() {
            return toString().replaceAll( " ", "_" );
        }
    }

  //******************************************START CHANGED**********************************************************//
         public enum PHYLOGENY_GRAPHICS_TYPE {
         //enum PHYLOGENY_GRAPHICS_TYPE {  // default->public - changed
  //********************************************END**********************************************************//
        RECTANGULAR, TRIANGULAR, EURO_STYLE, CONVEX, CURVED, UNROOTED, CIRCULAR;
    }
    
       //******************************************START**********************************************************//
	public boolean is_print_usingActualSize() {
		return _print_using_actual_size;
	}

	public int get_print_sizeX() {
		return _print_size_x;
	}

	public int get_print_sizeY() {
		return _print_size_y;
	}

	public double get_print_lineWidth() {
		return _print_line_width;
	}

	public boolean is_graphics_exportUsingActualSize() {
		return _graphics_export_using_actual_size;
	}

	public boolean is_graphics_exportVisibleOnly() {
		return _graphics_export_visible_only;
	}

	public boolean is_antialias_print() {
		return _antialias_print;
	}
      //********************************************END**********************************************************//
}