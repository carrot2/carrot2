#!/usr/bin/ruby

#
# Contact the DCS and fetch an XML for a given source, algorithm, query combination.
#

require 'httpclient'

# run a HTTP POST request to the DCS
def dcs_request(uri, data)
    boundary = Array::new(16) { "%2.2d" % rand(99) }.join()
    extheader = {
        "content-type" => "multipart/form-data; boundary=___#{ boundary }___"
    }

    client = HTTPClient.new
    response = client.post_content(uri, data, extheader)
end

# Certain sources have different names between the webapp and the DCS.
source_mapping = {
  "web" => "etools",
  "yahoo-news" => "boss-news",
  "put" => "",
}

# DCS URI
uri = "http://localhost:8080/dcs/rest"

# Loop through the input and fetch XMLs.
index = 0;

$stdin.each_line { |line|
  next if line.empty?

  (algorithm, source, count, query) = line.split(",");
  query.chop!

  source = source_mapping[source] if source_mapping.has_key? source
  next if source.empty?

  begin
    response = dcs_request(uri, {
        "dcs.source" => source,
        "dcs.algorithm" => algorithm,
        "query" => query,
        "results" => count.to_s,
        "dcs.output.format" => "XML",
        "dcs.clusters.only" => "false"
    })

    file_name = "response-%05i.xml" % index
    response_file = File.new("xmls/#{file_name}", "w")
    begin
      response_file.print response
    ensure
      response_file.close
    end

    puts "Processed: " + file_name
    index = index + 1
  rescue HTTPClient::BadResponseError
    puts "Processing error %s: [%s] %s" % [
      [algorithm, source, count, query].join(","),
      $!.res.header.status_code,
      $!.res.header.reason_phrase
    ] 
  end
}
