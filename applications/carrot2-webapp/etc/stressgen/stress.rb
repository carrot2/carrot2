#!/usr/bin/ruby

#
# Simple stress generator for the webapp.
#

require 'net/http'
require 'thread'
require 'optparse'

#
# Declare options
#

options = {:threads => 1, :mps => 0}
OptionParser.new do |opts|
    opts.banner = "Usage: stress.rb [options]"

    opts.separator ""
    opts.separator "Options:"

    opts.on("-t N", "--threads N", Float, "Number of concurrent threads.") do |n|
        options[:threads] = n
    end

    opts.on("-m N", "--mps N", Float, "Maximum requests per second (total). No limit if zero.") do |mps|
        options[:mps] = mps.to_i
    end
    
    opts.on("-q N", "--queries N", Float, "Number of queries to issue. No limit if zero.") do |queries|
        options[:queries] = queries.to_i
    end

    opts.on("-u URI", "--uri URI", String, "URI prefix.") do |uri|
        options[:uri] = uri
    end

    opts.on("-h", "--help", "Show this message") do
      puts opts
      exit
    end

    opts.separator ""
    opts.separator "Queries are read from the input stream."
end.parse!

#
# Set to a large value if no limits.
#
options[:mps] = 100000 if options[:mps] == 0
options[:queries] = 100000 unless options.has_key? :queries and options[:queries] > 0
options[:uri] = 'http://localhost:8080' unless options.has_key? :uri

#
# Sources and algorithms for a given version of the engine
#

CARROT2_30 = {
	:sources => ["web", "boss-web", "boss-images", "yahoo-news", "msn-web"],
	:algorithms => ["lingo", "stc", "url", "source"],
    :uri_pattern => '/search?source=#{source}&view=tree&skin=fancy-compact&results=100&algorithm=#{algorithm}&query=#{query}&type=CLUSTERS'
}

#
# Sources and algorithms for this run.
#

engine = CARROT2_30
@sources = engine[:sources]
@algorithms = engine[:algorithms]
@base_uri = options[:uri] + engine[:uri_pattern]

#
# Queries are read from standard input, each query in a single line.
#
@queries = []
$stdin.each_line do |line|
	q = line.chop
	next if q.empty?
	@queries << q
end
$stderr.puts "Read: #{@queries.length} queries"

#
# Take random element from an array
#
def rand_elem(array)
	array[rand(array.length)]
end

#
# Create query threads and the queue
#
threads = []

@queries_left = options[:queries]
@waitroom = []
@mutex = Mutex.new
@queue = []

(1..options[:threads]).each do |t|
    threads << Thread.new do
        while @queries_left > 0
            waiting = false
            @mutex.synchronize do
                if @queries_left <= 0
                    break
                end

                entry = { :time => Time.now, :thread => Thread.current }
                if @queue.size + 1 > options[:mps]
                    waiting = true
                    @waitroom << entry
                else
                    @queue << entry
                    @queries_left = @queries_left - 1
                end
            end

            if waiting then
                Thread.stop
                next
            end

            query = rand_elem(@queries)
            query = URI.encode(query)

            source = rand_elem(@sources)
            algorithm = rand_elem(@algorithms)

            uri = eval('"' << @base_uri << '"')
            begin
                res = Net::HTTP.get_response(URI.parse(uri))
                if res.code != '200' 
                    raise "HTTP error: #{res.code}, #{res.message}"
                end
                $stdout.print '.'
                $stdout.flush
            rescue Exception => e
                $stdout.print '#'
                $stdout.flush
            end
        end
    end
end

#
# Main loop. Control the mps.
#
Thread.current.priority = 2
while @queries_left > 0
    @mutex.synchronize do
        deadline = Time.now - 1
        while (not @queue.empty?) and @queue[0][:time] < deadline
            @queue.delete_at(0)
            if @queue.size < options[:mps] and not @waitroom.empty?
                @waitroom.each { |e| e[:thread].wakeup }
                @waitroom.clear
            end
        end
    end
    sleep 0.1
end


