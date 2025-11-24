import React from 'react';
import './App.css';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Navbar from './component/common/Navbar'; 
import HomePage from './component/home/HomePage';
import FooterComponent from './component/common/Footer';


function App() {
  return (

    <BrowserRouter>
      <div className="App">
        <Navbar />
        <div className="content">
          <Routes>
            <Route path="/" element={<h1> Hello </h1>} />
            <Route path="/home" element={<HomePage />} />
          </Routes>
        </div>   
        <FooterComponent />
      </div>
    </BrowserRouter>
  );
}

export default App;
