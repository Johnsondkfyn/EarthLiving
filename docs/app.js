document.querySelectorAll("[data-placeholder-form]").forEach((form) => {
  form.addEventListener("submit", (event) => {
    event.preventDefault();
    const message = form.querySelector(".success");
    if (message) {
      message.classList.add("is-visible");
    }
    form.reset();
  });
});
