import React from 'react';
import './App.css';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Navbar from './component/common/Navbar'; 
import HomePage from './component/home/HomePage';
import FooterComponent from './component/common/Footer';
import AllRoomsPage from './component/booking_rooms/AllRoomsPage';
import FindBookingPage from './component/booking_rooms/FindBookingPage';
import RoomDetailsPage from './component/booking_rooms/RoomDetailsPage';


function App() {
  return (

    <BrowserRouter>
      <div className="App">
        <Navbar />
        <div className="content">
          <Routes>
            <Route path="/" element={<h1> Hello </h1>} />
            <Route path="/home" element={<HomePage />} />
            <Route path="/rooms" element={<AllRoomsPage />} />
             <Route path="/find-booking" element={<FindBookingPage />} />
             <Route path="/room-details-book/:roomId" element={<RoomDetailsPage />} />
            <Route path="*" element={<h1>404 Not Found</h1>} />
          </Routes>
        </div>   
        <FooterComponent />
      </div>
    </BrowserRouter>
  );
}

export default App;
