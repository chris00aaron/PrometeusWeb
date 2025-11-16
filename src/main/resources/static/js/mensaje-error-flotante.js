function mostrarErrorFlotante(contenedorId = 'container-mensaje-error') {
    const container = document.getElementById(contenedorId);
    if (!container) return;  // Si no existe el contenedor, salir de la función

    const errorMessage = container ? container.getAttribute('data-error-message') : "Se ha producido un error inesperado.";
    const toastHtml = `
                <div class="toast align-items-center text-white bg-danger border-0" 
                     role="alert" 
                     aria-live="assertive" 
                     aria-atomic="true" 
                     style="position: fixed; top: 20px; right: 20px; z-index: 1050;">
                    <div class="d-flex">
                        <div class="toast-body">
                            ${errorMessage.trim()}
                        </div>
                        <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
                    </div>
                </div>
            `;

    document.body.insertAdjacentHTML('beforeend', toastHtml);

    const newToastEl = document.body.lastElementChild;
    const toast = new bootstrap.Toast(newToastEl, {
        autohide: true,
        delay: 5000
    });
    toast.show();
}