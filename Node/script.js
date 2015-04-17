var fs = require("fs"),
  readline = require("readline"),
  csv = require("fast-csv");

var CaidePorto = "content/Caide-Porto.csv";
var PortoCaide = "content/Porto-Caide.csv";

csv
  .fromPath(PortoCaide)
  .on("data", function(data) {
    console.log(data);
  })
  .on("end", function() {
    console.log("done");
  });