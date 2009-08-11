#!/usr/local/bin/ruby -w
#
# = exe/hpp
#
# Copyright::  Copyright (C) 2006-2007 Christian M. Zmasek
# License::    GNU Lesser General Public License (LGPL)
#
# $Id: hpp.rb,v 1.3 2008/08/28 17:09:06 cmzmasek Exp $
#
# last modified: 06/11/2007

require 'lib/evo/apps/hmmpfam_parser'

module Evoruby

    hpp = HmmpfamParser.new()

    hpp.run()

end  # module Evoruby
