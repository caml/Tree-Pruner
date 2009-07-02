#
# = lib/evo/io/parser/hmmsearch_domain_extractor.rb - HmmsearchDomainExtractor class
#
# Copyright::  Copyright (C) 2006-2008 Christian M. Zmasek
# License::    GNU Lesser General Public License (LGPL)
#
# $Id: hmmsearch_domain_extractor.rb,v 1.18 2008/01/04 07:45:11 cmzmasek Exp $


require 'lib/evo/util/constants'
require 'lib/evo/msa/msa_factory'
require 'lib/evo/io/msa_io'
require 'lib/evo/io/writer/fasta_writer'
require 'lib/evo/io/parser/fasta_parser'


module Evoruby

    class HmmsearchDomainExtractor
        
        TRIM_BY = 2

        def initialize
        end

        # raises ArgumentError, IOError, StandardError
        def parse( hmmsearch_output,
                fasta_sequence_file,
                outfile,
                passed_seqs_outfile,
                failed_seqs_outfile,
                e_value_threshold,
                length_threshold,
                add_position,
                add_domain_number,
                add_domain_number_as_digit,
                add_domain_number_as_letter,
                trim_name,
                log )

            Util.check_file_for_readability( hmmsearch_output )
            Util.check_file_for_readability( fasta_sequence_file )
            Util.check_file_for_writability( outfile )
            Util.check_file_for_writability( passed_seqs_outfile )
            Util.check_file_for_writability( failed_seqs_outfile )

            in_msa = nil
            factory = MsaFactory.new()
            in_msa = factory.create_msa_from_file( fasta_sequence_file, FastaParser.new() )

            if ( in_msa == nil || in_msa.get_number_of_seqs() < 1 )
                error_msg = "could not find fasta sequences in " + fasta_sequence_file
                raise IOError, error_msg
            end

            out_msa = Msa.new
            failed_seqs = Msa.new
            passed_seqs = Msa.new

            ld = Constants::LINE_DELIMITER

            saw_model_sequence_line = false
            saw_minus_line          = false
            domain_pass_counter     = 0
            domain_fail_counter     = 0
            proteins_with_passing_domains = 0
            proteins_with_failing_domains = 0
            max_domain_copy_number_per_protein = -1
            max_domain_copy_number_sequence    = ''
            failed_species_counts         = Hash.new
            passed_species_counts         = Hash.new

            File.open( hmmsearch_output ) do | file |
                while line = file.gets
                    if is_ignorable?( line )
                    elsif ( line =~ /^\s*Sequence\s+Domain\s+seq.f\s+seq.t/i )
                        saw_model_sequence_line = true
                    elsif ( saw_model_sequence_line && line =~ /^\s*\-+/ )
                        saw_minus_line = true
                        saw_model_sequence_line = false
                    elsif ( line =~ /^\s*Alignments of/i )
                        break
                    elsif ( saw_minus_line )
                        if ( line =~ /^\s*\S+\s+\d/ )
                            line =~ /^\s*(\S+)\s+(\d+)\D(\d+)\s+(\d+)\s+(\d+).+\s+(\S+)\s*$/
                            sequence = $1
                            number   = $2.to_i
                            out_of   = $3.to_i
                            seq_from = $4.to_i
                            seq_to   = $5.to_i
                            e_value  = $6.to_f
                            if ( number > max_domain_copy_number_per_protein )
                                max_domain_copy_number_sequence    = sequence
                                max_domain_copy_number_per_protein = number
                            end     
                            if ( ( ( e_value_threshold.to_f < 0.0 ) || ( e_value.to_f <= e_value_threshold ) ) &&
                                     ( ( length_threshold.to_f <= 0 )   || ( seq_to - seq_from + 1 ) >= length_threshold.to_f )  )
                                HmmsearchDomainExtractor.extract_domain( sequence,
                                    number,
                                    out_of,
                                    seq_from,
                                    seq_to,
                                    in_msa,
                                    out_msa,
                                    add_position,
                                    add_domain_number,
                                    add_domain_number_as_digit,
                                    add_domain_number_as_letter,
                                    trim_name )
                                domain_pass_counter += 1
                                count_species( sequence, passed_species_counts )
                                if !passed_seqs.has?( sequence, true, false )
                                    HmmsearchDomainExtractor.add_sequence( sequence, in_msa, passed_seqs )
                                    proteins_with_passing_domains += 1
                                end
                            else
                                print( domain_fail_counter.to_s + ": " + sequence.to_s + " did not meet threshold(s)" )
                                log << domain_fail_counter.to_s + ": " + sequence.to_s + " did not meet threshold(s)"
                                if ( ( e_value_threshold.to_f >= 0.0 ) && ( e_value.to_f > e_value_threshold ) )
                                    print( " E=" + e_value.to_s )
                                    log << " E=" + e_value.to_s
                                end
                                if ( ( length_threshold.to_f > 0 ) && ( seq_to - seq_from + 1 ) < length_threshold.to_f )
                                    le = seq_to - seq_from + 1
                                    print( " l=" + le.to_s )
                                    log << " l=" + le.to_s
                                end
                                print( Constants::LINE_DELIMITER )
                                log << Constants::LINE_DELIMITER
                                domain_fail_counter  += 1
                                count_species( sequence, failed_species_counts )
                                if !failed_seqs.has?( sequence, true, false )
                                    HmmsearchDomainExtractor.add_sequence( sequence, in_msa, failed_seqs )
                                    proteins_with_failing_domains += 1
                                end
                            end
                        elsif ( line =~ /no\s+hits\s+above\s+threshold/i )
                        else
                            error_msg = "unexpected line: " + line
                            raise IOError, error_msg
                        end
                    end
                end
            end
            
            if domain_pass_counter < 1
                error_msg = "no domain sequences were extracted"
                raise StandardError, error_msg
            end                 
            
            log << Constants::LINE_DELIMITER
            puts( "Max domain copy number per protein : " + max_domain_copy_number_per_protein.to_s )
            log << "Max domain copy number per protein : " + max_domain_copy_number_per_protein.to_s             
            log << Constants::LINE_DELIMITER
            
            if ( max_domain_copy_number_per_protein > 1 )
                puts( "First protein with this copy number: " + max_domain_copy_number_sequence )
                log << "First protein with this copy number: " + max_domain_copy_number_sequence
                log << Constants::LINE_DELIMITER
            end
            
            io = MsaIO.new()
            w = FastaWriter.new()
            w.set_line_width( 60 )
            w.clean( true )

            begin
                io.write_to_file( out_msa, outfile, w )
            rescue Exception
                error_msg = "could not write to \"" + outfile + "\""
                raise IOError, error_msg
            end

            begin
                io.write_to_file( passed_seqs, passed_seqs_outfile, w )
            rescue Exception
                error_msg = "could not write to \"" + passed_seqs_outfile + "\""
                raise IOError, error_msg
            end

            begin
                io.write_to_file( failed_seqs, failed_seqs_outfile, w )
            rescue Exception
                error_msg = "could not write to \"" + failed_seqs_outfile + "\""
                raise IOError, error_msg
            end

            log << ld
            log << "passing domains              : " + domain_pass_counter.to_s + ld
            log << "failing domains              : " + domain_fail_counter.to_s + ld
            log << "proteins with passing domains: " + proteins_with_passing_domains.to_s + ld
            log << "proteins with failing domains: " + proteins_with_failing_domains.to_s + ld
            log << ld
            log << 'passing domains counts per species: ' << ld
            passed_species_counts.each_pair { | species, count | log << "#{species}: #{count}" << ld }
            log << ld
            log << 'failing domains counts per species: ' << ld
            failed_species_counts.each_pair { | species, count | log << "#{species}: #{count}" << ld }
            log << ld
            return domain_pass_counter

        end # parse

        private


        def HmmsearchDomainExtractor.add_sequence( sequence_name, in_msa, add_to_msa )
            seqs = in_msa.find_by_name( sequence_name, true, false )
            if ( seqs.length < 1 )
                error_msg = "sequence \"" + sequence_name + "\" not found in sequence file"
                raise StandardError, error_msg
            end
            if ( seqs.length > 1 )
                error_msg = "sequence \"" + sequence_name + "\" not unique in sequence file"
                raise StandardError, error_msg
            end
            seq = in_msa.get_sequence( seqs[ 0 ] )
            add_to_msa.add_sequence( seq )
        end

        # raises ArgumentError, StandardError
        def HmmsearchDomainExtractor.extract_domain( sequence,
                number,
                out_of,
                seq_from,
                seq_to,
                in_msa,
                out_msa,
                add_position,
                add_domain_number,
                add_domain_number_as_digit,
                add_domain_number_as_letter,
                trim_name )
            if ( number < 1 || out_of < 1 || number > out_of )
                error_msg = "impossible: number=" + number.to_s + ", out of=" + out_of.to_s
                raise ArgumentError, error_msg
            end
            if ( seq_from < 1 || seq_to < 1 || seq_from >= seq_to )
                error_msg = "impossible: seq-f=" + seq_from.to_s + ", seq-t=" + seq_to.to_s
                raise ArgumentError, error_msg
            end
            seqs = in_msa.find_by_name( sequence, true, false )
            if ( seqs.length < 1 )
                error_msg = "sequence \"" + sequence + "\" not found in sequence file"
                raise StandardError, error_msg
            end
            if ( seqs.length > 1 )
                error_msg = "sequence \"" + sequence + "\" not unique in sequence file"
                raise StandardError, error_msg
            end
            # hmmsearch is 1 based, wheres sequences are 0 bases in this package.
            seq = in_msa.get_sequence( seqs[ 0 ] ).get_subsequence( seq_from - 1, seq_to - 1 )
            if ( add_position )
                seq.set_name( seq.get_name + "_" + seq_from.to_s + "-" + seq_to.to_s )
            end

            if ( trim_name )
                seq.set_name( seq.get_name[ 0, seq.get_name.length - TRIM_BY ] )
            end

            if ( out_of != 1 )
                if ( add_domain_number_as_digit )
                    seq.set_name( seq.get_name + number.to_s )
                elsif ( add_domain_number_as_letter )
                    if number > 25
                        error_msg = 'too many identical domains per sequence, cannot use letters to distinguish them'
                        raise StandardError, error_msg
                    end
                    seq.set_name( seq.get_name + ( number + 96 ).chr )
                elsif ( add_domain_number )
                    seq.set_name( seq.get_name + "~" + number.to_s + "-" + out_of.to_s )
                end
            end
            
            if ( seq.get_name.length > 10 )
                error_msg = "sequence name [" + seq.get_name + "] is longer than 10 characters"
                raise StandardError, error_msg               
            end
            
            out_msa.add_sequence( seq )
        end

        def count_species( sequence, species_counts_map )
            species = get_species( sequence )
            if species != nil
                if !species_counts_map.has_key?( species )
                    species_counts_map[ species ] = 1
                else
                    species_counts_map[ species ] = species_counts_map[ species ] + 1
                end
            end
        end

        def get_species( sequence_name )
            if sequence_name =~ /^.+_(.+)$/
                return $1
            else
                return nil
            end
        end

        def is_ignorable?( line )
            return ( line !~ /[A-Za-z0-9-]/ )
        end

    end # class HmmsearchDomainExtractor

end # module Evoruby

