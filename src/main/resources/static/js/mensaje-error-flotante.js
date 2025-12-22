function mostrarErrorFlotante(containerId = 'container-mensaje-error') {
    const container = document.getElementById(containerId);

    // 1. Obtener el mensaje de error de Thymeleaf
    const errorMessage = container ? container.getAttribute('data-error-message') : 'Acaba de suceder un error inesperado';

    // 3. Crear el elemento Toast dinámicamente
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

    // 4. Insertar el HTML del Toast en el cuerpo del documento
    document.body.insertAdjacentHTML('beforeend', toastHtml);

    // 5. Inicializar y mostrar el Toast
    const newToastEl = document.body.lastElementChild;
    const toast = new bootstrap.Toast(newToastEl, {
        autohide: true,
        delay: 5000
    });
    toast.show();
}