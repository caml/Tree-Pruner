#
# = lib/evo/apps/hmmpfam_parser.rb - HmmpfamParser class
#
# Copyright::  Copyright (C) 2006-2007 Christian M. Zmasek
# License::    GNU Lesser General Public License (LGPL)
#
# $Id: hmmpfam_parser.rb,v 1.9 2008/08/28 03:21:31 cmzmasek Exp $
#
# last modified: 06/11/2007

require 'lib/evo/util/constants'
require 'lib/evo/util/util'
require 'lib/evo/util/command_line_arguments'

module Evoruby

    class HmmpfamParser

        PRG_NAME       = "hpp"
        PRG_VERSION    = "1.0.1"
        PRG_DESC       = "hmmpfam parser"
        PRG_DATE       = "2007.12.18"
        COPYRIGHT      = "2007 Christian M Zmasek"
        CONTACT        = "cmzmasek@yahoo.com"
        WWW            = "www.phylosoft.org"

        DELIMITER_OPTION              = "d"
        E_VALUE_THRESHOLD_OPTION      = "e"
        IGNORE_DUF_OPTION             = "i"
        PARSE_OUT_DESCRIPITION_OPTION = "a"
        HELP_OPTION_1                 = "help"
        HELP_OPTION_2                 = "h"

        def initialize
            @domain_counts = Hash.new
        end

        # raises ArgumentError, IOError
        def parse( inpath,
                outpath,
                column_delimiter,
                e_value_threshold,
                ignore_dufs,
                get_descriptions )
            Util.check_file_for_readability( inpath )
            Util.check_file_for_writability( outpath )

            outfile = File.open( outpath, "a" )

            query    = String.new
            desc     = String.new
            model    = String.new
            seq_from = String.new
            seq_to   = String.new
            e_value  = String.new
            saw_model_domain_line  = false
            saw_minus_line         = false

            queries_count = 0

            nl = Constants::LINE_DELIMITER

            File.open( inpath ) do | file |
                while line = file.gets
                    if HmmpfamParser.is_ignorable?( line )
                    elsif ( line =~ /^Query\ssequence:\s*(.+)/ )
                        queries_count += 1
                        query    = $1
                        desc     = ""
                        model    = ""
                        seq_from = ""
                        seq_to   = ""
                        e_value  = ""
                        saw_minus_line = false
                    elsif ( line =~ /^Description:\s*(.+)/ )
                        desc = $1
                    elsif ( line =~ /^Model\s+Domain\s+seq.f\s+seq.t/ )
                        saw_model_domain_line = true
                    elsif ( saw_model_domain_line && line =~ /^\-{8}\s+/ )
                        saw_minus_line = true
                        saw_model_domain_line = false
                    elsif ( line =~ /^Alignments\sof/ || line =~ /^\/{2}/ )
                        saw_minus_line = false
                    elsif ( saw_minus_line )
                        if ( line =~ /^\S+\s+\d/ )
                            line =~ /^(\S+)\s+\d+\D\d+\s+(\d+)\s+(\d+).+\s+(\S+)\s*$/
                            model    = $1
                            seq_from = $2.to_i
                            seq_to   = $3.to_i
                            e_value  = $4.to_f
                            if ( ( ( e_value_threshold < 0.0 ) || ( e_value <= e_value_threshold ) ) &&
                                     ( !ignore_dufs || ( model !~ /^DUF\d+/ ) ) )
                                count_model( model )
                                outfile.print( query +
                                     column_delimiter )
                                if ( get_descriptions )
                                    outfile.print( desc +
                                         column_delimiter )
                                end
                                outfile.print( model +
                                     column_delimiter +
                                     seq_from.to_s +
                                     column_delimiter +
                                     seq_to.to_s +
                                     column_delimiter +
                                     e_value.to_s )
                                outfile.print( nl )
                            end
                        elsif ( line =~ /no\s+hits\s+above\s+threshold/ )
                        else
                            error_msg = "unexpected line: " + line
                            raise IOError, error_msg
                        end
                    end
                end # while line = file.gets
            end
            outfile.flush()
            outfile.close()

            return queries_count

        end # def parse

        def count_model( model )
            if ( @domain_counts.has_key?( model ) )
                count = @domain_counts[ model ].to_i
                count += 1
                @domain_counts[ model ] = count
            else
                @domain_counts[ model ] = 1
            end
        end


        def get_domain_counts()
            return @domain_counts
        end

        def run()

            Util.print_program_information( PRG_NAME,
                PRG_VERSION,
                PRG_DESC,
                PRG_DATE,
                COPYRIGHT,
                CONTACT,
                WWW,
                STDOUT )

            begin
                cla = CommandLineArguments.new( ARGV )
            rescue ArgumentError => e
                Util.fatal_error( PRG_NAME, "error: " + e.to_s, STDOUT )
            end

            if ( cla.is_option_set?( HELP_OPTION_1 ) ||
                     cla.is_option_set?( HELP_OPTION_2 ) )
                print_help
                exit( 0 )
            end

            if ( cla.get_number_of_files != 2 )
                print_help
                exit( -1 )
            end

            allowed_opts = Array.new
            allowed_opts.push( DELIMITER_OPTION )
            allowed_opts.push( E_VALUE_THRESHOLD_OPTION )
            allowed_opts.push( IGNORE_DUF_OPTION )
            allowed_opts.push( PARSE_OUT_DESCRIPITION_OPTION )

            disallowed = cla.validate_allowed_options_as_str( allowed_opts )
            if ( disallowed.length > 0 )
                Util.fatal_error( PRG_NAME,
                    "unknown option(s): " + disallowed,
                    STDOUT )
            end

            inpath = cla.get_file_name( 0 )
            outpath = cla.get_file_name( 1 )

            column_delimiter = "\t"
            if ( cla.is_option_set?( DELIMITER_OPTION ) )
                begin
                    column_delimiter = cla.get_option_value( DELIMITER_OPTION )
                rescue ArgumentError => e
                    Util.fatal_error( PRG_NAME, "error: " + e.to_s, STDOUT )
                end
            end

            e_value_threshold = -1.0
            if ( cla.is_option_set?( E_VALUE_THRESHOLD_OPTION ) )
                begin
                    e_value_threshold = cla.get_option_value_as_float( E_VALUE_THRESHOLD_OPTION )
                rescue ArgumentError => e
                    Util.fatal_error( PRG_NAME, "error: " + e.to_s, STDOUT )
                end
                if ( e_value_threshold < 0.0 )
                    Util.fatal_error( PRG_NAME, "attempt to use a negative E-value threshold", STDOUT )
                end
            end

            ignore_dufs = false
            if ( cla.is_option_set?( IGNORE_DUF_OPTION ) )
                ignore_dufs = true
            end

            parse_descriptions = false
            if ( cla.is_option_set?( PARSE_OUT_DESCRIPITION_OPTION ) )
                parse_descriptions = true
            end

            puts()
            puts( "hmmpfam outputfile: " + inpath )
            puts( "outputfile        : " + outpath )
            if ( e_value_threshold >= 0.0 )
                puts( "E-value threshold : " + e_value_threshold.to_s )
            else
                puts( "E-value threshold : no threshold" )
            end
            if ( parse_descriptions )
                puts( "parse descriptions: true" )
            else
                puts( "parse descriptions: false" )
            end
            if ( ignore_dufs )
                puts( "ignore DUFs       : true" )
            else
                puts( "ignore DUFs       : false" )
            end
            if ( column_delimiter == "\t" )
                puts( "column delimiter  : TAB" )
            else
                puts( "column delimiter  : " + column_delimiter )
            end
            puts()

            begin
                queries_count = parse( inpath,
                    outpath,
                    column_delimiter,
                    e_value_threshold,
                    ignore_dufs,
                    parse_descriptions )
            rescue ArgumentError, IOError => e
                Util.fatal_error( PRG_NAME, "error: " + e.to_s, STDOUT )
            end
            domain_counts = get_domain_counts()

            puts
            puts( "read output for a total of " + queries_count.to_s + " query sequences" )
            puts
            puts( "domain counts (considering potential E-value threshold and ignoring of DUFs):" )
            puts( "(number of different domains: " + domain_counts.length.to_s + ")" )
            puts
            puts( Util.draw_histogram( domain_counts, "#" ) )
            puts
            Util.print_message( PRG_NAME, 'OK' )
            puts

        end # def run()

        def print_help()
            puts( "Usage:" )
            puts()
            puts( "  " + PRG_NAME + ".rb [options] <hmmpfam outputfile> <outputfile>" )
            puts()
            puts( "  options: -" + DELIMITER_OPTION + ": column delimiter for outputfile, default is TAB" )
            puts( "           -" + E_VALUE_THRESHOLD_OPTION  + ": E-value threshold, default is no threshold" )
            puts( "           -" + PARSE_OUT_DESCRIPITION_OPTION  + ": parse query description (in addition to query name)" )
            puts( "           -" + IGNORE_DUF_OPTION  + ": ignore DUFs" )
            puts()
        end


        private

        def HmmpfamParser.is_ignorable?( line )
            return ( line !~ /[A-Za-z0-9\-\/]/ )
        end

    end # class HmmpfamParser

end # module Evoruby