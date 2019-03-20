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

connect.connect(function(error){
    if(!!error){
        console.log('Erroare la conexiune');
    }else{
        console.log('Connected');
    }
});

router.get('/', (req, res, next) =>{
    connect.query("select * from Pollution.Quality_Indexes q "+
                "inner join ( select LocationName, max(DataSiOra) as MaxDataSiOra from Pollution.Quality_Indexes group by LocationName) "+
                "tm on q.LocationName= tm.LocationName and q.DataSiOra = tm.MaxDataSiOra; ", function(error, rows, fields){
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

module.exports = router;