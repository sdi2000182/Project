import React, { useEffect, useState } from 'react';
import Map from './Map';

function App() {
  const [data, setData] = useState([]);

  useEffect(() => {
    const apiUrl = 'http://localhost:3000/api/data/latest';

    const fetchData = async () => {
      try {
        const response = await fetch(apiUrl);
        if (response.ok) {
          const jsonData = await response.json();

          // Ensure the fetched data is an array (if it's not already)
          const dataArray = Array.isArray(jsonData) ? jsonData : [jsonData];

          setData(dataArray);
          
        } else {
          console.error('Error fetching data:', response.status);
        }
      } catch (error) {
        console.error('Error fetching data:', error);
      }
    };

    // Fetch data immediately when the component mounts
    fetchData();

    // Fetch data every second (1000 milliseconds)
    const intervalId = setInterval(fetchData, 1000);

    // Clear the interval when the component unmounts
    return () => {
      clearInterval(intervalId);
    };
  }, []);

  return (
    <div className="App">
      <h1>Iot and Android Map</h1>
      <Map data={data} />
    </div>
  );
}

export default App;

