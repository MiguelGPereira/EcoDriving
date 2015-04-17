var csv = require('fast-csv'); // require fast-csv module
var fs = require('fs'); // require the fs, filesystem module
var uniqueindex = 0; // just an index for our array
var dataJSON = {}; // our JSON object, (make it an array if you wish)

var CaidePorto = "content/Caide-Porto.csv";
var PortoCaide = "content/Porto-Caide.csv";



csv
  .fromPath(PortoCaide, {
    delimiter: ';'
  })
  .on("data", function(data) {
    console.log(data[0]);
  })
  .on("end", function() {
    console.log("done");
  });