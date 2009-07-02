// $Id: Taxonomy.java,v 1.44 2009/02/13 23:03:31 cmzmasek Exp $
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

package org.forester.phylogeny.data;

import java.io.IOException;
import java.io.Writer;

import org.forester.io.parsers.nhx.NHXtags;
import org.forester.io.parsers.phyloxml.PhyloXmlMapping;
import org.forester.util.ForesterUtil;

public class Taxonomy implements PhylogenyData {

    private String     _scientific_name;
    private String     _common_name;
    private Identifier _identifier;
    private String     _taxonomy_code;
    private String     _rank;
    private String     _type;
    private Uri        _uri;

    public Taxonomy() {
        init();
    }

    public StringBuffer asSimpleText() {
        return asText();
    }

    public StringBuffer asText() {
        final StringBuffer sb = new StringBuffer();
        if ( getIdentifier() != null ) {
            sb.append( "[" );
            sb.append( getIdentifier().asSimpleText() );
            sb.append( "]" );
        }
        if ( !ForesterUtil.isEmpty( getTaxonomyCode() ) ) {
            if ( sb.length() > 0 ) {
                sb.append( " " );
            }
            sb.append( "[" );
            sb.append( getTaxonomyCode() );
            sb.append( "]" );
        }
        if ( !ForesterUtil.isEmpty( getScientificName() ) ) {
            if ( sb.length() > 0 ) {
                sb.append( " " );
            }
            sb.append( getScientificName() );
        }
        if ( !ForesterUtil.isEmpty( getCommonName() ) ) {
            if ( sb.length() > 0 ) {
                sb.append( " " );
            }
            sb.append( getCommonName() );
        }
        return sb;
    }

    public PhylogenyData copy() {
        final Taxonomy t = new Taxonomy();
        t.setTaxonomyCode( new String( getTaxonomyCode() ) );
        t.setScientificName( new String( getScientificName() ) );
        t.setCommonName( new String( getCommonName() ) );
        if ( getIdentifier() != null ) {
            t.setIdentifier( ( Identifier ) getIdentifier().copy() );
        }
        else {
            t.setIdentifier( null );
        }
        t.setRank( new String( getRank() ) );
        t.setType( new String( getType() ) );
        if ( getUri() != null ) {
            t.setUri( ( Uri ) getUri().copy() );
        }
        else {
            t.setUri( null );
        }
        return t;
    }

    @Override
    public boolean equals( final Object o ) {
        if ( this == o ) {
            return true;
        }
        else if ( o == null ) {
            return false;
        }
        else if ( o.getClass() != this.getClass() ) {
            throw new IllegalArgumentException( "attempt to check [" + this.getClass() + "] equality to " + o + " ["
                    + o.getClass() + "]" );
        }
        else {
            return isEqual( ( Taxonomy ) o );
        }
    }

    public String getCommonName() {
        return _common_name;
    }

    public Identifier getIdentifier() {
        return _identifier;
    }

    public String getRank() {
        return _rank;
    }

    public String getScientificName() {
        return _scientific_name;
    }

    public String getTaxonomyCode() {
        return _taxonomy_code;
    }

    public String getType() {
        return _type;
    }

    public Uri getUri() {
        return _uri;
    }

    @Override
    public int hashCode() {
        if ( getIdentifier() != null ) {
            return getIdentifier().hashCode();
        }
        else if ( !ForesterUtil.isEmpty( getTaxonomyCode() ) ) {
            return getTaxonomyCode().hashCode();
        }
        else if ( !ForesterUtil.isEmpty( getScientificName() ) ) {
            return getScientificName().toLowerCase().hashCode();
        }
        else if ( !ForesterUtil.isEmpty( getCommonName() ) ) {
            return getCommonName().toLowerCase().hashCode();
        }
        throw new IllegalStateException( "hash code calculation not possible with empty fields" );
    }

    public void init() {
        setScientificName( "" );
        setCommonName( "" );
        setIdentifier( null );
        setRank( "" );
        setTaxonomyCode( "" );
        setType( "" );
        setUri( null );
    }

    public boolean isEmpty() {
        return ( ( getIdentifier() == null ) && ForesterUtil.isEmpty( getTaxonomyCode() )
                && ForesterUtil.isEmpty( getCommonName() ) && ForesterUtil.isEmpty( getScientificName() )
                && ForesterUtil.isEmpty( getRank() ) && ( getUri() == null ) && ( ForesterUtil.isEmpty( getType() ) ) );
    }

    /**
     * 
     * If this and taxonomy 'data' has an identifier, comparison will be based on that.
     * Otherwise,  if this and taxonomy 'data' has a code, comparison will be based on that.
     * Otherwise,  if Taxonomy 'data' has a scientific name, comparison will be
     * based on that (case insensitive!).
     * Otherwise,  if Taxonomy 'data' has a common  name, comparison will be
     * based on that (case insensitive!).
     * (Note. This is important and should not be change without a very good reason.)
     * 
     */
    public boolean isEqual( final PhylogenyData data ) {
        final Taxonomy tax = ( Taxonomy ) data;
        if ( ( getIdentifier() != null ) && ( tax.getIdentifier() != null ) ) {
            return getIdentifier().isEqual( tax.getIdentifier() );
        }
        else if ( !ForesterUtil.isEmpty( getTaxonomyCode() ) && !ForesterUtil.isEmpty( tax.getTaxonomyCode() ) ) {
            return getTaxonomyCode().equals( tax.getTaxonomyCode() );
        }
        else if ( !ForesterUtil.isEmpty( getScientificName() ) && !ForesterUtil.isEmpty( tax.getScientificName() ) ) {
            return getScientificName().equalsIgnoreCase( tax.getScientificName() );
        }
        else if ( !ForesterUtil.isEmpty( getCommonName() ) && !ForesterUtil.isEmpty( tax.getCommonName() ) ) {
            return getCommonName().equalsIgnoreCase( tax.getCommonName() );
        }
        else if ( !ForesterUtil.isEmpty( getScientificName() ) && !ForesterUtil.isEmpty( tax.getCommonName() ) ) {
            return getScientificName().equalsIgnoreCase( tax.getCommonName() );
        }
        else if ( !ForesterUtil.isEmpty( getCommonName() ) && !ForesterUtil.isEmpty( tax.getScientificName() ) ) {
            return getCommonName().equalsIgnoreCase( tax.getScientificName() );
        }
        throw new IllegalStateException( "comparison not possible with empty fields" );
    }

    public void setCommonName( final String common_name ) {
        _common_name = common_name;
    }

    public void setIdentifier( final Identifier identifier ) {
        _identifier = identifier;
    }

    public void setRank( final String rank ) {
        _rank = rank;
    }

    public void setScientificName( final String scientific_name ) {
        _scientific_name = scientific_name;
    }

    public void setTaxonomyCode( final String taxonomy_code ) {
        _taxonomy_code = taxonomy_code;
    }

    public void setType( final String type ) {
        _type = type;
    }

    public void setUri( final Uri uri ) {
        _uri = uri;
    }

    public StringBuffer toNHX() {
        final StringBuffer sb = new StringBuffer();
        if ( getIdentifier() != null ) {
            sb.append( ':' + NHXtags.TAXONOMY_ID );
            sb.append( ForesterUtil.replaceIllegalNhxCharacters( getIdentifier().getValue() ) );
        }
        final StringBuffer species = new StringBuffer();
        if ( !ForesterUtil.isEmpty( getTaxonomyCode() ) ) {
            species.append( ForesterUtil.replaceIllegalNhxCharacters( getTaxonomyCode() ) );
        }
        if ( !ForesterUtil.isEmpty( getScientificName() ) ) {
            ForesterUtil.appendSeparatorIfNotEmpty( species, '|' );
            species.append( ForesterUtil.replaceIllegalNhxCharacters( getScientificName() ) );
        }
        if ( !ForesterUtil.isEmpty( getCommonName() ) ) {
            ForesterUtil.appendSeparatorIfNotEmpty( species, '|' );
            species.append( ForesterUtil.replaceIllegalNhxCharacters( getCommonName() ) );
        }
        if ( species.length() > 0 ) {
            sb.append( ':' + NHXtags.SPECIES_NAME );
            sb.append( species );
        }
        return sb;
    }

    public void toPhyloXML( final Writer writer, final int level, final String indentation ) throws IOException {
        if ( isEmpty() ) {
            return;
        }
        writer.write( ForesterUtil.LINE_SEPARATOR );
        writer.write( indentation );
        if ( !ForesterUtil.isEmpty( getType() ) ) {
            PhylogenyDataUtil.appendOpen( writer, PhyloXmlMapping.TAXONOMY, PhyloXmlMapping.TAXONOMY_TYPE, getType() );
        }
        else {
            PhylogenyDataUtil.appendOpen( writer, PhyloXmlMapping.TAXONOMY );
        }
        if ( getIdentifier() != null ) {
            getIdentifier().toPhyloXML( writer, level, indentation );
        }
        if ( !ForesterUtil.isEmpty( getTaxonomyCode() ) ) {
            PhylogenyDataUtil.appendElement( writer, PhyloXmlMapping.TAXONOMY_CODE, getTaxonomyCode(), indentation );
        }
        if ( !ForesterUtil.isEmpty( getScientificName() ) ) {
            PhylogenyDataUtil.appendElement( writer,
                                             PhyloXmlMapping.TAXONOMY_SCIENTIFIC_NAME,
                                             getScientificName(),
                                             indentation );
        }
        if ( !ForesterUtil.isEmpty( getCommonName() ) ) {
            PhylogenyDataUtil
                    .appendElement( writer, PhyloXmlMapping.TAXONOMY_COMMON_NAME, getCommonName(), indentation );
        }
        if ( !ForesterUtil.isEmpty( getRank() ) ) {
            PhylogenyDataUtil.appendElement( writer, PhyloXmlMapping.TAXONOMY_RANK, getRank(), indentation );
        }
        if ( getUri() != null ) {
            getUri().toPhyloXML( writer, level, indentation );
        }
        writer.write( ForesterUtil.LINE_SEPARATOR );
        writer.write( indentation );
        PhylogenyDataUtil.appendClose( writer, PhyloXmlMapping.TAXONOMY );
    }

    @Override
    public String toString() {
        return asText().toString();
    }
}
