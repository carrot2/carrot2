#!/usr/bin/ruby

#
# Simple stress generator for the webapp.
#


require 'net/http'

# Base URI pattern.
host = 'http://localhost:8080';
uri = '/search'
qstring = 'source=#{source}&view=tree&skin=fancy-compact&results=100&algorithm=#{algorithm}&query=#{query}&type=CLUSTERS'
$base_uri = host + uri + "?" + qstring

# Sources and algorithms for a given version of the engine
CARROT2_30 = {
	"sources" => ["web", "boss-web", "boss-images", "yahoo-news", "msn-web", "indeed", "pubmed", "icerocket"],
	"algorithms" => ["lingo", "stc", "url", "source"]
	}

# Sources and algorithms for this run.
$sources = CARROT2_30["sources"]
$algorithms = CARROT2_30["algorithms"]

# Queries are read from standard input, each query in a single line.
$queries = []
$stdin.each_line do |line|
	q = line.chop
	next if q.empty?
	$queries << q
end
$stderr.puts "Read: #{$queries.length} queries"

#
# Take random element from an array
#
def rand_elem(array)
	array[rand(array.length)]
end

#
# Enter the main query loop.
#
while true
	query = rand_elem($queries)
	query = URI.encode(query)

	source = rand_elem($sources)
	algorithm = rand_elem($algorithms)

	uri = eval('"' << $base_uri << '"')
	puts uri
	res = Net::HTTP.get_response(URI.parse(uri))
end


