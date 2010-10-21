// $Id: PhylogeneticInferenceOptions.java,v 1.1 2010/10/02 02:54:33 cmzmasek Exp
// $
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

package org.forester.archaeopteryx;

import java.io.File;

import org.forester.evoinference.distance.PairwiseDistanceCalculator.PWD_DISTANCE_METHOD;

public final class PhylogeneticInferenceOptions {

    private static final int                 BOOTSTRAP_RESAMPLES_DEFAULT          = 100;
    private static final PWD_DISTANCE_METHOD PWD_DISTANCE_METHOD_DEFAULT          = PWD_DISTANCE_METHOD.KIMURA_DISTANCE;
    private static final long                RANDOM_NUMBER_SEED_DEFAULT           = 42L;
    private static final boolean             PERFORM_BOOTSTRAP_RESAMPLING_DEFAULT = false;
    private int                              _bootstrap_samples;
    private PWD_DISTANCE_METHOD              _pwd_distance_method;
    private long                             _random_number_generator_seed;
    private boolean                          _perform_bootstrap_resampling;
    private String                           _intermediate_files_base;

    public synchronized String getIntermediateFilesBase() {
        return _intermediate_files_base;
    }

    public synchronized void setIntermediateFilesBase( final String intermediate_files_base ) {
        _intermediate_files_base = new String( intermediate_files_base );
    }

    public PhylogeneticInferenceOptions() {
        init();
    }

    // Deep copy.
    public synchronized PhylogeneticInferenceOptions copy() {
        final PhylogeneticInferenceOptions o = new PhylogeneticInferenceOptions();
        o._bootstrap_samples = _bootstrap_samples;
        o._pwd_distance_method = _pwd_distance_method;
        o._random_number_generator_seed = _random_number_generator_seed;
        o._perform_bootstrap_resampling = _perform_bootstrap_resampling;
        o._intermediate_files_base = new String( _intermediate_files_base );
        return o;
    }

    private synchronized void init() {
        _bootstrap_samples = BOOTSTRAP_RESAMPLES_DEFAULT;
        _pwd_distance_method = PWD_DISTANCE_METHOD_DEFAULT;
        _random_number_generator_seed = RANDOM_NUMBER_SEED_DEFAULT;
        _perform_bootstrap_resampling = PERFORM_BOOTSTRAP_RESAMPLING_DEFAULT;
        _intermediate_files_base = "";
    }

    public synchronized void setBootstrapSamples( final int bootstrap_samples ) {
        _bootstrap_samples = bootstrap_samples;
    }

    public synchronized int getBootstrapSamples() {
        return _bootstrap_samples;
    }

    public synchronized void setPwdDistanceMethod( final PWD_DISTANCE_METHOD pwd_distance_method ) {
        _pwd_distance_method = pwd_distance_method;
    }

    public synchronized PWD_DISTANCE_METHOD getPwdDistanceMethod() {
        return _pwd_distance_method;
    }

    public synchronized void setRandomNumberGeneratorSeed( final long random_number_generator_seed ) {
        _random_number_generator_seed = random_number_generator_seed;
    }

    public synchronized long getRandomNumberGeneratorSeed() {
        return _random_number_generator_seed;
    }

    public synchronized void setPerformBootstrapResampling( final boolean perform_bootstrap_resampling ) {
        _perform_bootstrap_resampling = perform_bootstrap_resampling;
    }

    public synchronized boolean isPerformBootstrapResampling() {
        return _perform_bootstrap_resampling;
    }

    public static PhylogeneticInferenceOptions createInstance( final Configuration configuration ) {
        final PhylogeneticInferenceOptions o = new PhylogeneticInferenceOptions();
        if ( configuration.getDefaultBootstrapSamples() >= 0 ) {
            o.setBootstrapSamples( configuration.getDefaultBootstrapSamples() );
        }
        return o;
    }

    public File getTempDir() {
        //TODO
        return new File( "/Users/zma/Desktop/tmp/");
    }
}
