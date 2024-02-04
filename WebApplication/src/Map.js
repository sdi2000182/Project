import React, { useEffect, useState } from 'react';
import redDangerIcon from './red-danger.png';
import orangeDangerIcon from './orange-danger.png';
import androidIcon from './android.png';

function Map({ data, androidData }) {



  const [map, setMap] = useState(null);
  const [markers, setMarkers] = useState([]);
  const [infoWindows, setInfoWindows] = useState([]);
  const [circles, setCircles] = useState([]);
  const [deviceDanger, setDeviceDanger] = useState({
    1: 'low',
    2: 'low',
  })
  const [polygons, setPolygons] = useState([]);

  // Icons for the markers
  const redIcon = {
    url: redDangerIcon,
    scaledSize: new window.google.maps.Size(40, 40),
  };

  const orangeIcon = {
    url: orangeDangerIcon,
    scaledSize: new window.google.maps.Size(40, 40),
  };

  const androidMarkerIcon = {
    url: androidIcon,
    scaledSize: new window.google.maps.Size(40, 40),
  };


  // Map rendering
  useEffect(() => {
    if (!map) {
      const newMap = new window.google.maps.Map(document.getElementById('map'), {
        center: { lat: 0, lng: 0 },
        zoom: 3,
      });
      setMap(newMap);
    }
  }, [map]);

  // Iot Devices handling
  useEffect(() => {

    if (map && data) {
      data.forEach((deviceData) => {
        const deviceID = deviceData.Iot_Device;
        // Update the danger level for the specific device
        deviceDanger[deviceID] = deviceData.Danger;


        const existingMarker = markers.find((marker) => marker.get('Iot_Device') === deviceData.Iot_Device);

        if (existingMarker) {
          existingMarker.setPosition(new window.google.maps.LatLng(deviceData.Latitude, deviceData.Longitude));
          const index = markers.indexOf(existingMarker);
          if (index !== -1) {
            const infoWindow = infoWindows[index];
            if (infoWindow) {
              const infoContent = getInfoContent(deviceData);
              infoWindow.setContent(infoContent);
            }
          }

          const circle = circles[index];
          if (circle) {
            updateCircle(circle, deviceData);
          }
        } else {
          const newMarker = new window.google.maps.Marker({
            position: { lat: deviceData.Latitude, lng: deviceData.Longitude },
            map,
            title: `Device ID: ${deviceData.Iot_Device}`,
            icon: getMarkerIcon(deviceData.Danger),
          });
          newMarker.set('Iot_Device', deviceData.Iot_Device);
          markers.push(newMarker);

          const newInfoWindow = new window.google.maps.InfoWindow();
          infoWindows.push(newInfoWindow);

          const newCircle = createCircle(deviceData);
          circles.push(newCircle);

          newMarker.addListener('click', () => {
            infoWindows.forEach((infoWindow) => infoWindow.close());
            const index = markers.indexOf(newMarker);
            if (index !== -1) {
              const infoWindow = infoWindows[index];
              if (infoWindow) {
                const infoContent = getInfoContent(deviceData);
                infoWindow.setContent(infoContent);
                infoWindow.open(map, newMarker);
              }
            }
          });
        }

        circles.forEach((circle, index) => {
          if (!markers.includes(markers[index])) {
            circle.setMap(null);
            circles.splice(index, 1);
          }
        });

        setDeviceDanger(deviceDanger);

        const bothDevicesInHighDanger = (deviceDanger[1] === 'high' || deviceDanger[1] === 'medium') &&
          (deviceDanger[2] === 'high' || deviceDanger[2] === 'medium');


        if (bothDevicesInHighDanger) {
          drawPolygon();
        }
      })
    }
  }, [map, data, markers, infoWindows, circles, deviceDanger]);


  // Android Handling
  useEffect(() => {

    const androidMarkers = [];

    if (map && androidData) {

      androidMarkers.forEach((marker) => {
        marker.setMap(null);
      });

      androidData.forEach((androidDeviceData) => {
        const androidMarker = new window.google.maps.Marker({
          position: { lat: parseFloat(androidDeviceData.Latitude), lng: parseFloat(androidDeviceData.Longitude) },
          map,
          title: `Android Device ID: ${androidDeviceData.Android_ID}`,
          icon: androidMarkerIcon,
        });

        const infoWindowContent = `
          <strong>Android Device ID:</strong> ${androidDeviceData.Android_ID}<br />
          <strong>Latitude:</strong> ${androidDeviceData.Latitude}<br />
          <strong>Longitude:</strong> ${androidDeviceData.Longitude}<br />
          <strong>Timestamp:</strong> ${androidDeviceData.Timestamp}<br />
        `;

        const infoWindow = new window.google.maps.InfoWindow({
          content: infoWindowContent,
        });

        androidMarker.addListener('click', () => {
          infoWindow.open(map, androidMarker);
        });


        androidMarkers.push(androidMarker);
      });
    }
  }, [map, androidData, androidMarkerIcon]);



  const getMarkerIcon = (danger) => {
    if (danger === 'high') {
      return redIcon; // Red icon for high danger
    } else if (danger === 'medium') {
      return orangeIcon; // Orange icon for medium danger
    } else {

      return null;
    }
  };

  const getInfoContent = (deviceData) => {
    return `
      <div>
      <strong>Timestamp:</strong> ${deviceData.Timestamp}<br />
        <strong>Device ID:</strong> ${deviceData.Iot_Device}<br />
        <strong>Latitude:</strong> ${deviceData.Latitude}<br />
        <strong>Longitude:</strong> ${deviceData.Longitude}<br />
        <strong>Smoke:</strong> ${deviceData.Smoke}<br />
        <strong>Gas:</strong> ${deviceData.Gas}<br />
        <strong>Temperature:</strong> ${deviceData.Temperature}<br />
        <strong>UV:</strong> ${deviceData.UV}<br />
        <strong>Danger:</strong> ${deviceData.Danger}<br />
      </div>
    `;
  };

  // Circle creating for IoT Devices
  const createCircle = (deviceData) => {
    const fillColor = isDeviceActive(deviceData) ? '#00FF00' : '#FF0000';
    const circle = new window.google.maps.Circle({
      strokeColor: '#FF0000',
      strokeOpacity: 0.8,
      strokeWeight: 2,
      fillColor,
      fillOpacity: 0.35,
      map,
      center: new window.google.maps.LatLng(deviceData.Latitude, deviceData.Longitude),
      radius: 50000,
    });
    return circle;
  };

  const updateCircle = (circle, deviceData) => {
    circle.setCenter(new window.google.maps.LatLng(deviceData.Latitude, deviceData.Longitude));
    const fillColor = isDeviceActive(deviceData) ? '#00FF00' : '#FF0000';
    updateCircleColor(circle, fillColor);
  };

  const updateCircleColor = (circle, fillColor) => {
    circle.setOptions({ fillColor });
  };

  const isDeviceActive = (deviceData) => {
    return (
      deviceData.Smoke > 0 ||
      deviceData.Gas > 0 ||
      deviceData.Temperature > 0 ||
      deviceData.UV > 0
    );
  };


  // Red danger box creation
  const drawPolygon = () => {
    const device1Marker = markers.find((marker) => marker.get('Iot_Device') === 1);
    const device2Marker = markers.find((marker) => marker.get('Iot_Device') === 2);

    polygons.forEach((polygon) => polygon.setMap(null));


    if (device1Marker && device2Marker && device1Marker.getPosition() && device2Marker.getPosition()) {
      const position1 = device1Marker.getPosition();
      const position2 = device2Marker.getPosition();

      const latitudes = [position1.lat(), position2.lat()];
      const longitudes = [position1.lng(), position2.lng()];

      const minLat = Math.min(...latitudes);
      const maxLat = Math.max(...latitudes);
      const minLng = Math.min(...longitudes);
      const maxLng = Math.max(...longitudes);

      const polygonCoords = [
        { lat: maxLat, lng: maxLng },
        { lat: minLat, lng: maxLng },
        { lat: minLat, lng: minLng },
        { lat: maxLat, lng: minLng },
      ];

      const polygon = new window.google.maps.Polygon({
        paths: polygonCoords,
        strokeColor: '#FF0000',
        strokeOpacity: 0.8,
        strokeWeight: 2,
        fillColor: '#FF0000',
        fillOpacity: 0.35,
        map,
      });

      setPolygons([...polygons, polygon]);
    }
  };


  return <div id="map" style={{ height: '700px' }}></div>;
}

export default Map;








