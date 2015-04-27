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
  getTimeDistBetweenStations("PortoCaide");
  //getTimeDistBetweenStations("CaidePorto");
  //
});

function getTimeDistBetweenStations(direction) {

  //1+1 station 0 doesnt matter
  console.log(loadedData.Station[1][0]);

  for (var i = 1 + 1; i < loadedData.Station.length; i++) {
    Start = {
      time: fromTimeToHour(loadedData[direction][i - 1][2]),
      place: loadedData.Station[i - 1][2]
    };
    End = {
      time: fromTimeToHour(loadedData[direction][i][1]),
      place: loadedData.Station[i][2],
      stop: loadedData.Station[i][1]
    };
    TravelTime = Math.abs(End.time - Start.time);
    TravelDist = End.place - Start.place;
    Velocity = TravelDist / (TravelTime / 3600);
    console.log("\tTravelTime: " + TravelTime + "s \tTravelDist: " + TravelDist + "km \tVelocity: " + Velocity + " km/h");
    //stream.write("" + TravelTime + ";" + TravelDist + "\n");
    getMaxVelocity(End.place, function(max) {
      velocityAcelerationTime(TravelTime, TravelDist * 1000, max * 1000, function(v) {
        console.log("\tRec:" + v * 3.6 + " \tmax:" + max);
      });
    });
    console.log(loadedData.Station[i][0]);
  }
}

function velocityAcelerationTime(time, dist, maximum, callback) {
  var t = 1;
  for (t = 1; t <= time; t++) {
    dAcc = 1 / 2 * train.acceleration * t * t;
    vel = t * train.acceleration;
    currentTime = time - t;
    constDist = currentTime * vel;
    if (constDist + dAcc >= dist) {
      callback(vel);
      break;
    }
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