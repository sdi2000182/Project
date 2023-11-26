const express = require('express');
const mysql = require('mysql2/promise'); // Use 'mysql2/promise' for async/await support
const cors = require('cors'); // Import the cors package

const app = express();
const port = 3000; // Define the port you want to run the server on

app.use(cors());

// Create a MySQL database connection
const db = mysql.createPool({
  host: 'localhost',
  user: 'root',
  password: 'project123',
  database: 'project',
});

// Define a route to fetch the most recent data from the MySQL database
app.get('/api/data/latest', async (req, res) => {
  try {
    // Connect to the database
    const connection = await db.getConnection();

    // Perform an SQL query to retrieve the most recent row
    const [rows, fields] = await connection.execute('SELECT * FROM Events ORDER BY Timestamp DESC LIMIT 1');

    // Release the connection
    connection.release();

    // Send the most recent data as a JSON response
    res.json(rows[0]); // Assuming you expect a single row as the result
  } catch (error) {
    console.error('Error:', error);
    res.status(500).json({ error: 'Internal Server Error' });
  }
});

// Start the server
app.listen(port, () => {
  console.log(`Server is running on port ${port}`);
});
