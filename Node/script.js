var csv = require('fast-csv'); // require fast-csv module
var fs = require('fs'); // require the fs, filesystem module
var async = require('async');

var files = [{
  file: "CaidePorto",
  path: "content/Caide-Porto.csv"
}, {
  file: "PortoCaide",
  path: "content/Porto-Caide.csv"
}, {
  file: "Curve",
  path: "content/Curve.csv"
}, {
  file: "SpeedLimit",
  path: "content/Limit.csv"
}, {
  file: "Station",
  path: "content/Station.csv"
}, {
  file: "Gradient",
  path: "content/Gradient.csv"
}];

var loadedData = {};

readAllFiles(function(loadedData) {
  //console.log(loadedData);
  console.log(getMaxVelocity(16));
  console.log(getCurve(16));
});

function getMaxVelocity(PK) {
  for (var i = 1; i < loadedData.SpeedLimit.length; i++) {
    if (loadedData.SpeedLimit[i][0] <= PK && loadedData.SpeedLimit[i][1] >= PK) {
      return loadedData.SpeedLimit[i][2];
    }
  }
  return 0;
}

function getCurve(PK) {
  for (var i = 1; i < loadedData.Curve.length; i++) {
    if (loadedData.Curve[i][0] <= PK && loadedData.Curve[i][1] >= PK) {
      return loadedData.Curve[i][2];
    }
  }
  return 0;
}

function getGradient(PK) {
  for (var i = 1; i < loadedData.Gradient.length; i++) {
    if (loadedData.Gradient[i][0] <= PK && loadedData.Gradient[i][1] >= PK) {
      return loadedData.Gradient[i][2];
    }
  }
  return 0;
}

function readAllFiles(callback) {
  async.each(files, function(current_file, fileCallback) {
    readToArray(current_file.path, function(data) {
      loadedData[current_file.file] = data;
      fileCallback();
    });
  }, function(error) {
    if (error) {

    } else {
      callback(loadedData);
    }
  });
}

function readToArray(filename, callback) {
  var returnData = [];
  csv
    .fromPath(filename, {
      delimiter: ';'
    })
    .on("data", function(data) {
      returnData.push(data);
    })
    .on("end", function() {
      console.log("Filename: " + filename);
      callback(returnData);
    });
}