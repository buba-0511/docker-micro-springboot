const notas = [];

document.getElementById("formulario").addEventListener("submit", (event) => {
  event.preventDefault();
  const valor = parseFloat(document.getElementById("input").value);
  if (!isNaN(valor)) {
    notas.push(valor);
    const li = document.createElement("li");
    li.textContent = `Nota ${notas.length}: ${valor}`;
    document.getElementById("lista").appendChild(li);
    document.getElementById("input").value = "";
    actualizarPromedio();
  }
});

document.getElementById("limpiar").addEventListener("click", () => {
  notas.length = 0;
  document.getElementById("lista").innerHTML = "";
  document.getElementById("resultado").textContent = "";
});

function actualizarPromedio() {
  const suma = notas.reduce((a, b) => a + b, 0);
  const promedio = suma / notas.length;
  document.getElementById("resultado").textContent =
    `Promedio: ${promedio.toFixed(2)}`;
}
