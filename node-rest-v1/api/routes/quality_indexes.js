const express = require('express');
const router = express.Router();
const apps = express();
const http = require('http');
let sql; 

const mysql = require('mysql');
const connect = mysql.createConnection({
    host: 'paulcsoft.com',
    port: '3308',
    user: 'root',
    password: 'tomidb',
});


  
function handleDisconnect() {    
    connect.connect(function(err) {              // The server is either down
        if(err)
        {                                     // or restarting (takes a while sometimes).
            console.log('error when connecting to db:', err);
            setTimeout(handleDisconnect, 2000); // We introduce a delay before attempting to reconnect,
        }                                     // to avoid a hot loop, and to allow our node script to
    });                                     // process asynchronous requests in the meantime.
                                            // If you're also serving http, display a 503 error.
    connect.on('error', function(err) {
        console.log('db error', err);
            if(err.code === 'PROTOCOL_CONNECTION_LOST') { // Connection to the MySQL server is usually
            handleDisconnect();                         // lost due to either server restart, or a
        } else {                                      // connnection idle timeout (the wait_timeout
            throw err;                                  // server variable configures this)
        }
    });
}
  
  handleDisconnect();



connect.connect(function(error){
    if(!!error){
        console.log('Erroare la conexiune');
    }else{
        console.log('Connected');
    }
});

router.get('/', (req, res, next) =>{
    connect.query("SELECT * FROM Pollution.Quality_Indexes;", function(error, rows, fields){
        if(!!error){
            res.status(200).json({
                message: 'Eroare la conexiunea cu baza de date'
            });    
        }
        res.status(200).json({
            message: 'Get a mers bine cu tot cu conexiunea la baza de date',
            message: rows
        });
    });
});

router.post('/', (req, res, next) =>{
    const index = {
        locationname: req.body.locationname,
        latitude: req.body.latitude,
        longitude: req.body.longitude,
        PM10: req.body.PM10,
        PM25: req.body.PM25,
        UserSender: req.body.UserSender  
    };
    var current_date = new Date();
    current_date.setHours( current_date.getHours() + 2);
    var datetime = (current_date).toJSON().slice(0, 19).replace(/[-T]/g, ':');
    //datetime.setHours( datetime.getHours() + 1);

    sql = "INSERT INTO Pollution.Quality_Indexes (LocationName, Latitude, Longitude, PM10, PM25, DataSiOra, UserSender) VALUES ('"+index.locationname+"', '"+ index.latitude+"', '"+ index.longitude +"', '"+ index.PM10+"', '"+ index.PM25 +"', '"+ datetime  +"', '"+index.UserSender+"');";
    connect.query(sql);
    res.status(201).json({
        message: 'Handling POST requests to /quality_indexes',
        createdindex : index
    });    
});

router.get('/:LocationName', (req, res, next) =>{
    const locationName = req.params.LocationName;
    /*if (id === 'special'){
        res.status(200).json({
            message: 'You discovered the special ID',
            id: id
        });
    }
    else{
        res.status(200).json({
            message: 'You passed an ID'
        });
    }*/
    connect.query("SELECT * FROM Pollution.Quality_Indexes WHERE LocationName = '"+locationName+"';", function(error, rows, fields){
        if(!!error){
            res.status(200).json({
                message: 'Eroare la conexiunea cu baza de date'
            });    
        }
        res.status(200).json({
            message: 'Get a mers bine cu tot cu conexiunea la baza de date',
            message: rows
        });
    });
});

router.patch('/:indexId', (req, res, next) =>{
    res.status(200).json({
        message: 'Updated Product'
    });
});

router.delete('/:indexId', (req, res, next) =>{
    res.status(200).json({
        message: 'Deleted Product'
    });
});

module.exports = router;