const stdin = process.stdin;
const stdout = process.stdout;
const inputChunks = [];

stdin.resume();
stdin.setEncoding("utf8");

stdin.on("data", function(chunk) {
  inputChunks.push(chunk);
});

stdin.on("end", function() {
  const input = inputChunks.join();
  const json = JSON.parse(input);

  Object.keys(json["advisories"]).forEach(a => {
    const advisory = json["advisories"][a];
    console.log(`"${advisory["module_name"]}": "${advisory["patched_versions"].substring(2)}",`);
  });
});