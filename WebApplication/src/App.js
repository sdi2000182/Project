import React, { useEffect, useState } from 'react';
import Map from './Map';

function App() {
  const [data, setData] = useState([]);
  const [androidData, setAndroidData] = useState([]);

  useEffect(() => {
    const apiUrlData = 'http://localhost:3000/api/data/latest';
    const apiUrlAndroidData = 'http://localhost:3000/api/androiddata/latest';

    const fetchData = async (apiUrl, setDataFunction) => {
      try {
        const response = await fetch(apiUrl);
        if (response.ok) {
          const jsonData = await response.json();

          const dataArray = Array.isArray(jsonData) ? jsonData : [jsonData];
          setDataFunction(dataArray);
        } else {
          console.error('Error fetching data:', response.status);
        }
      } catch (error) {
        console.error('Error fetching data:', error);
      }
    };

    // Fetch data on mount
    fetchData(apiUrlData, setData);
    fetchData(apiUrlAndroidData, setAndroidData);

    // Fetch data every second (1000 milliseconds)
    const intervalId = setInterval(() => {
      fetchData(apiUrlData, setData);
      fetchData(apiUrlAndroidData, setAndroidData);
    }, 1000);

    // Clear the interval when the component unmounts
    return () => {
      clearInterval(intervalId);
    };
  }, []);

  return (
    <div className="App">
      <h1>Iot and Android Map</h1>
      <Map data={data} androidData={androidData} />
    </div>
  );
}

export default App;

