import React, { useEffect, useRef, useState } from 'react';
import { generateBackground } from "../util/background-generator.js";

export const Backdrop = () => {
  const ref = useRef(null);
  const [visible, setVisible] = useState(false);

  useEffect(() => {
    generateBackground(ref.current);
    setVisible(true);
  }, [ ref, setVisible ]);

  return (
    <canvas className={`Backdrop ${visible ? "" : " hidden"}`} ref={ref}
            style={{opacity: visible}} />
  );
};