import React from 'react';
import ReactDOM from 'react-dom';
import SearchApp from './SearchApp';

it('renders without crashing', () => {
  const div = document.createElement('div');
  ReactDOM.render(<SearchApp />, div);
  ReactDOM.unmountComponentAtNode(div);
});
