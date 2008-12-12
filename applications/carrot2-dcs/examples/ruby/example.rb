
#
# Ruby example of calling the DCS.
#
# Requires the following gems:
#  gem install json_pure
#  gem install httpclient
#

require 'httpclient'
require 'json'

def dump(jsonResponse)
	response = JSON.parse(jsonResponse)

	descriptions = response['clusters'].map do
        |cluster| "%s [%i document(s)]" % [cluster['phrases'].join(", "), cluster['documents'].length]
    end
	puts descriptions.join("\n")
end

def dcs_request(uri, data)
    boundary = Array::new(16) { "%2.2d" % rand(99) }.join()
    extheader = {
        "content-type" => "multipart/form-data; boundary=___#{ boundary }___"
    }

    client = HTTPClient.new
    response = client.post_content(uri, data, extheader)
end

#
# Perform an external 'document source' request.
#

uri = "http://localhost:8080/dcs/rest"

puts "\n## Clustering data from external source...\n"
dump(dcs_request(uri, {
     "dcs.source" => "boss-web",
	 "query" => "data mining",
	 "dcs.output.format" => "JSON",
	 "dcs.clusters.only" => "false"
}))

#
# Perform an XML upload request.
#

puts "\n## Clustering data from a file...\n"
dump(dcs_request(uri, {
     "dcs.c2stream"   => open("data-mining.xml"),        
	 "query" => "data mining",
	 "dcs.output.format" => "JSON",
	 "dcs.clusters.only" => "true"
}))

