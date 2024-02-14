const express = require('express');
const mysql = require('mysql2/promise');
const cors = require('cors');

const app = express();
const port = 3000;

app.use(cors());


const db = mysql.createPool({     //MYSQL Database Connection
  host: 'localhost',
  user: 'root',
  password: 'project123',
  database: 'project',
});


app.get('/api/data/latest', async (req, res) => {
  try {

    const connection = await db.getConnection();


    const [rows, fields] = await connection.execute('SELECT * FROM Events ORDER BY Timestamp DESC LIMIT 1'); // SQL query to retrieve the most recent row


    connection.release();


    res.json(rows[0]); // Send the most recent data as a JSON response
  } catch (error) {
    console.error('Error:', error);
    res.status(500).json({ error: 'Internal Server Error' });
  }
});

// Retrieve the latest Android data
app.get('/api/androiddata/latest', async (req, res) => {
  try {
    const connection = await db.getConnection();

    const [rows, fields] = await connection.execute('SELECT * FROM Android_Data ORDER BY Timestamp DESC LIMIT 1');

    connection.release();

    res.json(rows[0]); // Send the most recent Android data as a JSON response
  } catch (error) {
    console.error('Error:', error);
    res.status(500).json({ error: 'Internal Server Error' });
  }
});

// Start the server
app.listen(port, () => {
  console.log(`Server is running on port ${port}`);
});
