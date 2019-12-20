import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './App';

function render() {
    if (!detectIE())
        ReactDOM.render(<App/>, document.getElementById('root'));
    else {
        alert("Please use Chrome or Firefox for this application.");
    }
}


function detectIE() {
    const agent = window.navigator.userAgent;

    const msie = agent.indexOf('MSIE ');
    if (msie > 0) {
        return parseInt(agent.substring(msie + 5, agent.indexOf('.', msie)), 10);
    }

    const trident = agent.indexOf('Trident/');
    if (trident > 0) {
        let rv = agent.indexOf('rv:');
        return parseInt(agent.substring(rv + 3, agent.indexOf('.', rv)), 10);
    }

    const edge = agent.indexOf('Edge/');
    if (edge > 0) {
        return parseInt(agent.substring(edge + 5, agent.indexOf('.', edge)), 10);
    }

    return false;
}

window.document.addEventListener('DOMContentLoaded', render);