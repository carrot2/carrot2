import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import { SearchApp } from './carrot2/search-app/ui/SearchApp';
import * as serviceWorker from './serviceWorker';

ReactDOM.render(<SearchApp />, document.getElementById('root'));

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: http://bit.ly/CRA-PWA
serviceWorker.unregister();
