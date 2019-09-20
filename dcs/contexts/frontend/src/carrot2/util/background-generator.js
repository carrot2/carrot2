import { stackBlurInPlace } from "./blur.js";

export const generateBackground = canvas => {
  const width = canvas.clientWidth, height = canvas.clientHeight;
  const scale = 0.0625;
  canvas.width = width * scale;
  canvas.height = height * scale;

  // We'll use ColorBrewer palettes
  const palettes = [
    ['rgb(255,255,204)','rgb(255,237,160)','rgb(254,217,118)','rgb(254,178,76)','rgb(253,141,60)','rgb(252,78,42)','rgb(227,26,28)','rgb(189,0,38)','rgb(128,0,38)'],
    ['rgb(255,255,229)','rgb(247,252,185)','rgb(217,240,163)','rgb(173,221,142)','rgb(120,198,121)','rgb(65,171,93)','rgb(35,132,67)','rgb(0,104,55)','rgb(0,69,41)'],
    ['rgb(255,255,229)','rgb(255,247,188)','rgb(254,227,145)','rgb(254,196,79)','rgb(254,153,41)','rgb(236,112,20)','rgb(204,76,2)','rgb(153,52,4)','rgb(102,37,6)']
  ];
  const colors = palettes[Math.floor(Math.random() * palettes.length) % palettes.length] ;

  // Throw some random colored circles
  const containerRadius = Math.sqrt(width * width + height * height);
  const sources = colors.reverse().reduce(function (src, color, index) {
    const ratio = 1 - index / colors.length;
    const count = 3;
    for (let i = 0; i < count; i++) {
      src.push({
        x: 0.5 * width  + 0.7 * width  * (0.5 - Math.random()),
        y: 0.75 * height + 0.7 * height * (0.5 - Math.random()),
        r: 0.3 * containerRadius * ratio + 0.2 * containerRadius * ratio * (0.5 - Math.random()),
        color: { hex: color, a: (1 / count) * (0.7 + 0.2 * (0.5 - Math.random() ))}
      });
    }
    return src;
  }, []);

  // Use the darkest color as the background
  const blurCtx = canvas.getContext("2d");
  blurCtx.scale(scale, scale);
  blurCtx.fillStyle = colors[0];
  blurCtx.fillRect(0, 0, width, height);

  // Draw the circles
  sources.forEach(function (source) {
    blurCtx.save();
    blurCtx.fillStyle = source.color.hex;
    blurCtx.globalAlpha = source.color.a;
    blurCtx.beginPath();
    blurCtx.arc(source.x, height * 1.2 - source.y, source.r, 0, Math.PI * 2);
    blurCtx.closePath();
    blurCtx.fill();
    blurCtx.restore();
  });

  // Blur everything
  stackBlurInPlace(canvas, Math.ceil(8 * scale));
};