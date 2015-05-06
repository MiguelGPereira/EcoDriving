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

var train = {
  acceleration: 1, //1m/s^2 = 12960 km/h^2
  traction_effort: 185,
  traction_effort_max: 38
};

var loadedData = {};


readAllFiles(function(loadedData) {
  //console.log(fromTimeToHour("00:30:30"));
  //console.log(loadedData);
  //console.log(getMaxVelocity(16));
  //console.log(getCurve(16));
  //loadVelocity();
  getTimeDistBetweenStations("PortoCaide");
  //getTimeDistBetweenStations("CaidePorto");
  //
});

function loadVelocity(callback) {
  for (var i = 0; i < loadedData.Station.length; i++) {
    getMaxVelocity(loadedData.Station[i][2], function(max) {
      loadedData.Station[i].push(max);
    });
  }
}

function getMaxVelocity1(PKBegin, PKFinal, callback) {
  var limit = [];
  var count = 0;
  for (var i = 1; i < loadedData.SpeedLimit.length; i++) {
    if (parseFloat(loadedData.SpeedLimit[i][1]) >= PKBegin && count === 0) {
      count++;
      limit.push({
        "limit": loadedData.SpeedLimit[i][2],
        "point": loadedData.SpeedLimit[i][0],
        "type": "start"
      });
    } else if (parseFloat(loadedData.SpeedLimit[i][0]) < PKFinal && parseFloat(loadedData.SpeedLimit[i][0]) > PKBegin) {
      limit.push({
        "limit": loadedData.SpeedLimit[i][2],
        "point": loadedData.SpeedLimit[i][0],
        "type": "between"
      });
    }
    if (i == (loadedData.SpeedLimit.length - 1)) {
      callback(limit);
      return;
    }
  }
}


function getTimeDistBetweenStations(direction) {

var stream = fs.createWriteStream("my_file.txt");
stream.once('open', function(fd) {
  //1+1 station 0 doesnt matter
  stream.write(loadedData.Station[1][0]+"\n");

  for (var i = 1 + 1; i < loadedData.Station.length; i++) {
    var Start = {
      time: fromTimeToHour(loadedData[direction][i - 1][2]),
      place: parseFloat(loadedData.Station[i - 1][2])
    };
    var End = {
      time: fromTimeToHour(loadedData[direction][i][1]),
      place: parseFloat(loadedData.Station[i][2]),
      stop: loadedData.Station[i][1]
    };
    var TravelTime = Math.abs(End.time - Start.time);
    var TravelDist = End.place - Start.place;
    var Velocity = TravelDist / (TravelTime / 3600);
    stream.write("TravelTime: " + TravelTime + ";TravelDist: " + TravelDist + ";Vel.Media: " + Velocity + "\n");
  
    getMaxVelocity1(Start.place, End.place, function(limits) {
      console.log(limits);
      velocityAcelerationTime(TravelTime, TravelDist * 1000, limits[0].limit * 1000 / 3600, function(v, time) {
        console.log("\tRec:" + v * 3.6 + " \tmax:" + limits[0].limit);
      });
    });
     stream.write(loadedData.Station[i][0] +"\n");
    }
  });
}

function velocityAcelerationTime(time, dist, maximum, callback) {
  console.log(maximum);
  var t = 1;
  var last = 0;
  for (t = 1; t <= time; t++) {
    var dAcc = 1 / 2 * train.acceleration * t * t;
    var vel = t * train.acceleration;
    var currentTime = time - t;
    var constDist = currentTime * vel;
    if (vel < maximum && last > vel) {

    }
    if ((constDist + dAcc >= dist)) {

      callback(vel, t);
      break;
    }
    last = vel;
  }

}

function fromTimeToHour(time) {
  var splited = time.split(':');
  return (parseFloat(splited[0] * 3600) + parseFloat(splited[1] * 60) + parseFloat(splited[2]));

}

function getMaxVelocity(PK, callback) {
  var limit = 0;
  for (var i = 1; i < loadedData.SpeedLimit.length; i++) {
    if (loadedData.SpeedLimit[i][0] <= PK && loadedData.SpeedLimit[i][1] >= PK) {
      limit = loadedData.SpeedLimit[i][2];
      callback(limit);
      break;
    }
  }

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

Array.prototype.insert = function(index, item) {
  this.splice(index, 0, item);
};