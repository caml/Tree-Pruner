#
# = lib/evo/apps/phylogeny_factory - PhylogenyFactory class
#
# Copyright::  Copyright (C) 2006-2007 Christian M. Zmasek
# License::    GNU Lesser General Public License (LGPL)
#
# $Id: phylogeny_factory.rb,v 1.19 2008/09/16 23:31:39 cmzmasek Exp $

require 'lib/evo/util/constants'
require 'lib/evo/util/util'
require 'lib/evo/util/command_line_arguments'

require 'set'
require 'date'

module Evoruby

    class PhylogenyFactory

        PRG_NAME       = "phylogeny_factory"
        PRG_DATE       = "2008.09.16"
        PRG_DESC       = "automated phylogeny reconstruction using queing system"
        PRG_VERSION    = "1.1"
        COPYRIGHT      = "2007 Christian M Zmasek"
        CONTACT        = "cmzmasek@yahoo.com"
        WWW            = "www.phylosoft.org"

        USE_JOB_SUBMISSION_SYSTEM_OPTION  = 's'
        LOG_FILE                          = '00_phylogeny_factory.log'
        TEMPLATE_FILE                     = '00_phylogeny_factory.template'
        PBS_O_WORKDIR                     = '$PBS_O_WORKDIR/'
        MIN_LENGTH_DEFAULT                = 40

        TMP_CMD_FILE_SUFFIX = '_QSUB'

        HMM                 = 'HMM'
        RSL                 = 'RSL'

        OPTION_OPEN          = '%['
        OPTION_CLOSE          = ']%'

        WAIT                 = 1.0

        NL = Constants::LINE_DELIMITER

        def run

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
                Util.fatal_error( PRG_NAME, "error: " + e.to_s )
            end

            allowed_opts = Array.new
            allowed_opts.push( USE_JOB_SUBMISSION_SYSTEM_OPTION )

            disallowed = cla.validate_allowed_options_as_str( allowed_opts )
            if ( disallowed.length > 0 )
                Util.fatal_error( PRG_NAME,
                    "unknown option(s): " + disallowed,
                    STDOUT )
            end

            if File.exists?( LOG_FILE )
                puts( '[' + PRG_NAME + '] > log file [' + LOG_FILE + '] already exists' )
                exit( -1 )
            end

            if !File.exists?( TEMPLATE_FILE )
                puts( '[' + PRG_NAME + '] > template file [' + TEMPLATE_FILE + '] not found' )
                exit( -1 )
            end

            use_job_submission_system = false
            if cla.is_option_set?( USE_JOB_SUBMISSION_SYSTEM_OPTION )
                use_job_submission_system = true
            end

            log = String.new

            now = DateTime.now
            log << "Program     : " + PRG_NAME + NL
            log << "Version     : " + PRG_VERSION + NL
            log << "Program date: " + PRG_DATE + NL + NL
            log << "Date/time   : " + now.to_s + NL
            log << "Directory   : " + Dir.getwd  + NL + NL

            puts( '[' + PRG_NAME + '] > reading ' + TEMPLATE_FILE )

            paths       = Hash.new  # path placeholder -> full path
            min_lengths = Hash.new  # alignment id -> minimal length
            hmms        = Hash.new  # alignment id -> hmm
            options     = Hash.new  # option placeholder -> option
            ids         = Set.new

            commands    = Array.new

            log <<  "////////////////////////////////////////////////////////////////// #{NL}"
            log << "Template file [" + TEMPLATE_FILE + "]:#{NL}"

            command = String.new

            open( TEMPLATE_FILE ).each { | line |
                log << line
                if ( line =~ /^#/ )
                elsif ( line =~ /^\$\s*(\S+)\s*=\s*(\S+)/ )
                    paths[ $1 ] = $2
                    puts( '[' + PRG_NAME + '] > paths      : ' + $1 + ' => ' + $2 )

                elsif ( line =~ /^%\s*#{HMM}\s*(\S+)\s*=\s*(\S+)/ )
                    hmms[ $1 ] = $2
                    puts( '[' + PRG_NAME + '] > hmms       : ' + $1 + ' => ' + $2 )

                elsif ( line =~ /^%\s*#{RSL}\s*(\S+)\s*=\s*(\S+)/ )
                    min_lengths[ $1 ] = $2
                    puts( '[' + PRG_NAME + '] > min lengths: ' + $1 + ' => ' + $2 )

                elsif ( line =~ /^%\s*(\S+)\s*=\s*(\S+)/ )
                    options[ $1 ] = $2
                    puts( '[' + PRG_NAME + '] > options    : ' + $1 + ' => ' + $2 )

                elsif ( line =~ /^>\s*(.+)/ )
                    command = command + $1 + ";#{NL}"

                elsif ( line =~ /^-/  )
                    commands << prepare( command, paths )
                    command = String.new
                end
            }
            log << "\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\ #{NL}#{NL}"

            files = Dir.entries( "." )

            files.each { | file |
                if ( !File.directory?( file ) &&
                         file !~ /^\./ &&
                         file !~ /#{TEMPLATE_FILE}/ &&
                         file !~ /.bck$/ &&
                         file !~ /.log$/ &&
                         file !~ /^00/ )
                    aln_name = file.to_str
                    id = get_id( aln_name )
                    if !ids.include?( id )
                        ids.add( id )
                    end
                    puts( '[' + PRG_NAME + '] > file [id]  : ' + aln_name + ' [' + id + ']' )
                    commands.each do | cmd |

                        cmd = subst_hmm( cmd, aln_name, hmms )
                        cmd = subst_min_length( cmd, aln_name, min_lengths )
                        cmd = subst_options( cmd, options )
                        if use_job_submission_system
                            cmd = subst_aln_name( cmd, PBS_O_WORKDIR + aln_name )
                        else
                            cmd = subst_aln_name( cmd, aln_name )
                        end

                        if ( cmd =~ /%/ )
                            cmd =~ /(%.*?%)/
                            problem = $1
                            puts( '[' + PRG_NAME + '] > WARNING    : [' + id + '] command still contains placeholder: ' + problem )
                            log << "WARNING: command still contains placeholder: " + cmd + NL
                        else
                            tmp_cmd_file = file.to_str[ 0..4 ] + TMP_CMD_FILE_SUFFIX
                            if ( File.exists?( tmp_cmd_file ) )
                                File.delete( tmp_cmd_file )
                            end
                            if use_job_submission_system
                                open( tmp_cmd_file, 'w' ) do |f|
                                    f.write( cmd )
                                end
                            end

                            log << cmd + NL

                            if use_job_submission_system
                                IO.popen( 'qsub ' + tmp_cmd_file , 'r+' ) do | pipe |
                                    pipe.close_write
                                end
                            else
                                spawn( 'nohup ' + cmd + ' &', STDERR => "/dev/null" )
                            end

                            sleep( WAIT )
                            if ( File.exists?( tmp_cmd_file ) )
                                File.delete( tmp_cmd_file )
                            end
                        end
                    end
                end
            }

            open( LOG_FILE, 'w' ) do | f |
                f.write( log )
            end

            puts()
            puts( '[' + PRG_NAME + '] > OK' )
            puts()

        end # def run

        def prepare( command, paths )
            paths.each_pair{ | name, full |
                command = command.gsub( name, full )
            }
            command
        end

        def subst_options( command, options )
            opt_placeholders = command.scan( /%\[\S+\]%/ )
            opt_placeholders.each { | opt_placeholder |
                opt_placeholder = opt_placeholder.gsub( OPTION_OPEN , '' )
                opt_placeholder = opt_placeholder.gsub( OPTION_CLOSE, '' )
                opt_value = options[ opt_placeholder ]
                if ( opt_value != nil && opt_value.size > 0 )
                    command = command.gsub( OPTION_OPEN + opt_placeholder + OPTION_CLOSE, opt_value )
                end
            }
            command
        end

        def subst_aln_name( command, aln_name )
            command = command.gsub( '$', aln_name )
            command
        end

        def subst_hmm( command, aln_name, hmms )
            id = get_id( aln_name )
            hmm = hmms[ id ]
            if ( hmm != nil && hmm.size > 0 )
                command = command.gsub( OPTION_OPEN + HMM + OPTION_CLOSE, hmm )
            end
            command
        end

        def subst_min_length( command, aln_name, min_lengths )
            id = get_id( aln_name )
            min_length = min_lengths[ id ]
            if ( min_length != nil && min_length.size > 0 )
                command = command.gsub( OPTION_OPEN + RSL + OPTION_CLOSE, min_length )
            else
                command = command.gsub( OPTION_OPEN + RSL + OPTION_CLOSE, MIN_LENGTH_DEFAULT.to_s )
            end
            command
        end

        def get_id( aln_name )
            aln_name =~ /^([^_]+)/
            $1
        end

    end # class PhylogenyFactory

end # module Evoruby
