import React, { Component } from 'react';
import logo from './logo.svg';
import './App.css';
import API from './API';

const APIUrl = "http://localhost:8080/WebRegisterAPI/";
const api = new API(APIUrl);

class App extends Component {
  constructor() {
    super();
    
    this.state = {};
  }
  
  api() {
    return api;
  }
  
  render() {
    
    return (
      <div className="App">
        <header className="App-header">
          <img src={logo} className="App-logo" alt="logo" />
          <h1 className="App-title">Welcome to React</h1>
        </header>
        <p className="App-intro">
          To get started, edit <code>src/App.js</code> and save to reload.
        </p>
      
      </div>
    );
  }
}

export default App;
