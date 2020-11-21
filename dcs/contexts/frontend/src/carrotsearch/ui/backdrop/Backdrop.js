import React, { useEffect, useRef, useState } from "react";
import { generateBackground, generatePalette } from "./background-generator.js";

const colors = generatePalette();
const color = colors[6];
const hue = /hsl\((\d+),.*/.exec(color)[1];
document.documentElement.style.setProperty("--backdrop-color-hue", hue);

export const Backdrop = () => {
  const ref = useRef(null);
  const [visible, setVisible] = useState(false);

  useEffect(() => {
    generateBackground(ref.current, colors);
    setVisible(true);
  }, [ref, setVisible]);

  return (
    <canvas
      className={`Backdrop ${visible ? "" : " hidden"}`}
      ref={ref}
      style={{ opacity: visible }}
    />
  );
};
