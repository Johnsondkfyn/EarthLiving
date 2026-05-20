const canvas = document.querySelector("#world-canvas");
const ctx = canvas.getContext("2d");
const viewToggle = document.querySelector(".view-toggle");

let width = 0;
let height = 0;
let pointerX = 0;
let pointerY = 0;
let time = 0;

const terrain = [
  { x: 0.08, y: 0.7, r: 0.18, color: "rgba(159, 196, 107, 0.42)" },
  { x: 0.26, y: 0.32, r: 0.28, color: "rgba(127, 177, 200, 0.28)" },
  { x: 0.48, y: 0.55, r: 0.22, color: "rgba(211, 154, 98, 0.26)" },
  { x: 0.72, y: 0.28, r: 0.2, color: "rgba(159, 196, 107, 0.34)" },
  { x: 0.87, y: 0.66, r: 0.25, color: "rgba(127, 177, 200, 0.25)" },
];

function resize() {
  const ratio = Math.min(window.devicePixelRatio || 1, 2);
  width = window.innerWidth;
  height = window.innerHeight;
  canvas.width = Math.floor(width * ratio);
  canvas.height = Math.floor(height * ratio);
  canvas.style.width = `${width}px`;
  canvas.style.height = `${height}px`;
  ctx.setTransform(ratio, 0, 0, ratio, 0, 0);
}

function drawGrid(offsetX, offsetY) {
  ctx.strokeStyle = "rgba(245, 241, 232, 0.065)";
  ctx.lineWidth = 1;

  const spacing = 62;
  for (let x = (offsetX % spacing) - spacing; x < width + spacing; x += spacing) {
    ctx.beginPath();
    ctx.moveTo(x, 0);
    ctx.lineTo(x + 80, height);
    ctx.stroke();
  }

  for (let y = (offsetY % spacing) - spacing; y < height + spacing; y += spacing) {
    ctx.beginPath();
    ctx.moveTo(0, y);
    ctx.lineTo(width, y - 80);
    ctx.stroke();
  }
}

function drawTerrain(offsetX, offsetY) {
  terrain.forEach((shape, index) => {
    const pulse = Math.sin(time * 0.0012 + index) * 18;
    const x = width * shape.x + offsetX * (index + 1) * 0.04;
    const y = height * shape.y + offsetY * (index + 1) * 0.04;
    const radius = Math.max(width, height) * shape.r + pulse;

    const gradient = ctx.createRadialGradient(x, y, 0, x, y, radius);
    gradient.addColorStop(0, shape.color);
    gradient.addColorStop(1, "rgba(16, 23, 21, 0)");

    ctx.fillStyle = gradient;
    ctx.beginPath();
    ctx.arc(x, y, radius, 0, Math.PI * 2);
    ctx.fill();
  });
}

function drawRoutes(offsetX, offsetY) {
  ctx.lineWidth = 2;
  ctx.strokeStyle = "rgba(245, 241, 232, 0.16)";
  ctx.beginPath();
  ctx.moveTo(width * 0.08 + offsetX * 0.02, height * 0.68 + offsetY * 0.02);
  ctx.bezierCurveTo(
    width * 0.28,
    height * 0.42,
    width * 0.44,
    height * 0.78,
    width * 0.64,
    height * 0.5
  );
  ctx.bezierCurveTo(
    width * 0.77,
    height * 0.3,
    width * 0.83,
    height * 0.44,
    width * 0.92 + offsetX * 0.02,
    height * 0.28 + offsetY * 0.02
  );
  ctx.stroke();

  ctx.fillStyle = "rgba(245, 241, 232, 0.58)";
  [
    [0.08, 0.68],
    [0.35, 0.56],
    [0.64, 0.5],
    [0.92, 0.28],
  ].forEach(([x, y]) => {
    ctx.beginPath();
    ctx.arc(width * x + offsetX * 0.02, height * y + offsetY * 0.02, 3.5, 0, Math.PI * 2);
    ctx.fill();
  });
}

function render(now) {
  time = now;
  const offsetX = (pointerX - width / 2) * 0.06 + Math.sin(now * 0.00025) * 24;
  const offsetY = (pointerY - height / 2) * 0.06 + Math.cos(now * 0.00022) * 18;

  ctx.clearRect(0, 0, width, height);
  drawTerrain(offsetX, offsetY);
  drawGrid(offsetX, offsetY);
  drawRoutes(offsetX, offsetY);
  requestAnimationFrame(render);
}

window.addEventListener("resize", resize);
window.addEventListener("pointermove", (event) => {
  pointerX = event.clientX;
  pointerY = event.clientY;
});

if (viewToggle) {
  const viewToggleLabel = viewToggle.querySelector(".view-toggle-label");
  const desktopPreviewQuery = window.matchMedia("(min-width: 681px)");
  const setMobilePreview = (enabled, persist = true) => {
    document.body.classList.toggle("mobile-preview", enabled);
    viewToggle.setAttribute("aria-pressed", String(enabled));
    if (viewToggleLabel) {
      viewToggleLabel.textContent = enabled ? "Desktop" : "Mobil";
    }
    if (persist) {
      window.localStorage.setItem("earthliving-view-mode", enabled ? "mobile" : "desktop");
    }
  };
  const syncResponsiveMode = () => {
    const savedMode = window.localStorage.getItem("earthliving-view-mode");
    setMobilePreview(desktopPreviewQuery.matches && savedMode === "mobile", false);
  };

  syncResponsiveMode();
  viewToggle.addEventListener("click", () => {
    setMobilePreview(!document.body.classList.contains("mobile-preview"));
  });
  desktopPreviewQuery.addEventListener("change", syncResponsiveMode);
}

resize();
pointerX = window.innerWidth * 0.5;
pointerY = window.innerHeight * 0.5;
requestAnimationFrame(render);
