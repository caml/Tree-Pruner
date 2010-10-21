
package org.forester.phylogeny.data;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;

import org.forester.io.parsers.phyloxml.PhyloXmlMapping;
import org.forester.util.ForesterUtil;

public class Point implements PhylogenyData {

    private final String       _geodetic_datum;
    private final BigDecimal   _lat;
    private final BigDecimal   _long;
    private final BigDecimal   _alt;
    private final String       _alt_unit;
    public static final String UNKNOWN_GEODETIC_DATUM = "?";

    public Point() {
        this( UNKNOWN_GEODETIC_DATUM, new BigDecimal( 0 ), new BigDecimal( 0 ), null, "" );
    }

    public Point( final String geodetic_datum, final BigDecimal lat, final BigDecimal longitude ) {
        this( geodetic_datum, lat, longitude, null, "" );
    }

    public Point( final String geodetic_datum,
                  final BigDecimal lat,
                  final BigDecimal longitude,
                  final BigDecimal alt,
                  final String alt_unit ) {
        if ( ForesterUtil.isEmpty( geodetic_datum ) ) {
            throw new IllegalArgumentException( "illegal attempt to use empty geodetic datum" );
        }
        else if ( lat == null ) {
            throw new IllegalArgumentException( "illegal attempt to use empty latitude" );
        }
        else if ( longitude == null ) {
            throw new IllegalArgumentException( "illegal attempt to use empty longitude" );
        }
        if ( ( alt != null ) && ForesterUtil.isEmpty( alt_unit ) ) {
            throw new IllegalArgumentException( "altitude must hava a unit" );
        }
        _geodetic_datum = geodetic_datum;
        _lat = lat;
        _long = longitude;
        _alt = alt;
        _alt_unit = alt_unit;
    }

    @Override
    public StringBuffer asSimpleText() {
        if ( getAltitude() == null ) {
            return new StringBuffer( "[" + getLatitude().toPlainString() + ", " + getLongitude() + "]" );
        }
        else {
            return new StringBuffer( "[" + getLatitude().toPlainString() + ", " + getLongitude() + ", " + getAltitude()
                    + getAltiudeUnit() + "]" );
        }
    }

    @Override
    public StringBuffer asText() {
        return asSimpleText();
    }

    @Override
    public PhylogenyData copy() {
        return new Point( new String( getGeodeticDatum() ),
                          new BigDecimal( getLatitude().toPlainString() ),
                          new BigDecimal( getLongitude().toPlainString() ),
                          getAltitude() == null ? null : new BigDecimal( getAltitude().toPlainString() ),
                          new String( getAltiudeUnit() ) );
    }

    public BigDecimal getAltitude() {
        return _alt;
    }

    public String getAltiudeUnit() {
        return _alt_unit;
    }

    public String getGeodeticDatum() {
        return _geodetic_datum;
    }

    public BigDecimal getLatitude() {
        return _lat;
    }

    public BigDecimal getLongitude() {
        return _long;
    }

    @Override
    public boolean isEqual( final PhylogenyData point ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public StringBuffer toNHX() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void toPhyloXML( final Writer writer, final int level, final String indentation ) throws IOException {
        writer.write( ForesterUtil.LINE_SEPARATOR );
        writer.write( indentation );
        if ( getAltitude() != null ) {
            PhylogenyDataUtil.appendOpen( writer,
                                          PhyloXmlMapping.POINT,
                                          PhyloXmlMapping.POINT_GEODETIC_DATUM,
                                          getGeodeticDatum(),
                                          PhyloXmlMapping.POINT_ALTITUDE_UNIT_ATTR,
                                          getAltiudeUnit() );
        }
        else {
            PhylogenyDataUtil.appendOpen( writer,
                                          PhyloXmlMapping.POINT,
                                          PhyloXmlMapping.POINT_GEODETIC_DATUM,
                                          getGeodeticDatum() );
        }
        PhylogenyDataUtil.appendElement( writer,
                                         PhyloXmlMapping.POINT_LATITUDE,
                                         getLatitude().toPlainString(),
                                         indentation );
        PhylogenyDataUtil.appendElement( writer,
                                         PhyloXmlMapping.POINT_LONGITUDE,
                                         getLongitude().toPlainString(),
                                         indentation );
        if ( getAltitude() != null ) {
            PhylogenyDataUtil.appendElement( writer,
                                             PhyloXmlMapping.POINT_ALTITUDE,
                                             getAltitude().toPlainString(),
                                             indentation );
        }
        writer.write( ForesterUtil.LINE_SEPARATOR );
        writer.write( indentation );
        PhylogenyDataUtil.appendClose( writer, PhyloXmlMapping.POINT );
    }

    @Override
    public String toString() {
        return asSimpleText().toString();
    }
}
