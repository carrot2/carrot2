import { stackBlurInPlace } from "./blur.js";

// We'll use ColorBrewer palettes
const palettes = [
  [
    "hsl(60,100%,95%)",
    "hsl(64,92%,86%)",
    "hsl(78,72%,79%)",
    "hsl(96,54%,71%)",
    "hsl(121,41%,62%)",
    "hsl(136,45%,46%)",
    "hsl(140,58%,33%)",
    "hsl(152,100%,20%)",
    "hsl(156,100%,14%)"
  ],
  [
    "hsl(60,100%,95%)",
    "hsl(53,100%,87%)",
    "hsl(45,98%,78%)",
    "hsl(40,99%,65%)",
    "hsl(32,99%,58%)",
    "hsl(26,85%,50%)",
    "hsl(22,98%,40%)",
    "hsl(19,95%,31%)",
    "hsl(19,89%,21%)"
  ]
];

export const generatePalette = () => {
  return palettes[
    Math.floor(Math.random() * palettes.length) % palettes.length
  ];
};

export const generateBackground = (canvas, colors) => {
  const width = canvas.clientWidth,
    height = canvas.clientHeight;
  const scale = 0.0625;
  canvas.width = width * scale;
  canvas.height = height * scale;

  // Throw some random colored circles
  const containerRadius = Math.sqrt(width * width + height * height);
  const sources = colors
    .slice(0)
    .reverse()
    .reduce(function (src, color, index) {
      const ratio = 1 - index / colors.length;
      const count = 3;
      for (let i = 0; i < count; i++) {
        src.push({
          x: 0.5 * width + 0.7 * width * (0.5 - Math.random()),
          y: 0.75 * height + 0.7 * height * (0.5 - Math.random()),
          r:
            0.3 * containerRadius * ratio +
            0.2 * containerRadius * ratio * (0.5 - Math.random()),
          color: {
            hex: color,
            a: (1 / count) * (0.7 + 0.2 * (0.5 - Math.random()))
          }
        });
      }
      return src;
    }, []);

  // Use the darkest color as the background
  const blurCtx = canvas.getContext("2d");
  blurCtx.scale(scale, scale);
  blurCtx.fillStyle = colors[colors.length - 1];
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
