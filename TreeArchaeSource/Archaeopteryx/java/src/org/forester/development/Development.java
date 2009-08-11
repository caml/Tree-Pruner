// $Id: Development.java,v 1.6 2009/01/13 19:49:30 cmzmasek Exp $
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

package org.forester.development;

/*
 * @author Christian M. Zmasek
 */
final public class Development {

    private static boolean _debug = false;
    private static boolean _time  = false;

    private Development() {
    }

    public static boolean isDebug() {
        return Development._debug;
    }

    public static boolean isTime() {
        return Development._time;
    }

    public static void setDebug( final boolean debug ) {
        Development._debug = debug;
    }

    public static void setTime( final boolean time ) {
        Development._time = time;
    }
}
