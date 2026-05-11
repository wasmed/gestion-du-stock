import sys

file_path = "src/main/resources/templates/serveur/dashboard.html"
with open(file_path, "r") as f:
    content = f.read()

search_button = """                    <div class="card-footer bg-white border-top-0 d-flex gap-2 pb-3">
                        <a th:href="@{/orders/validate/{id}(id=${cmd.id})}" class="btn btn-success flex-grow-1 fw-bold shadow-sm">
                            <i class="bi bi-check-circle-fill me-1"></i> Valider
                        </a>
                        <a th:href="@{/orders/delete/{id}(id=${cmd.id})}" class="btn btn-outline-danger shadow-sm" title="Refuser / Supprimer" onclick="return confirm('Voulez-vous vraiment refuser cette commande ?');">
                            <i class="bi bi-x-lg"></i>
                        </a>
                    </div>"""

replace_button = """                    <div class="card-footer bg-white border-top-0 d-flex gap-2 pb-3">
                        <button type="button" class="btn btn-success flex-grow-1 fw-bold shadow-sm" data-bs-toggle="modal" th:data-bs-target="'#validateModal' + ${cmd.id}">
                            <i class="bi bi-check-circle-fill me-1"></i> Valider
                        </button>
                        <a th:href="@{/orders/delete/{id}(id=${cmd.id})}" class="btn btn-outline-danger shadow-sm" title="Refuser / Supprimer" onclick="return confirm('Voulez-vous vraiment refuser cette commande ?');">
                            <i class="bi bi-x-lg"></i>
                        </a>
                    </div>"""

content = content.replace(search_button, replace_button)

search_modal = """        <hr class="my-5 text-muted">
    </div>

    <!-- Section Commandes À Emporter -->"""

replace_modal = """        <div th:each="cmd : ${commandesAValider}" class="modal fade" th:id="'validateModal' + ${cmd.id}" tabindex="-1" aria-labelledby="validateModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <form th:action="@{/orders/validate/{id}(id=${cmd.id})}" method="post">
                        <div class="modal-header bg-success text-white">
                            <h5 class="modal-title" id="validateModalLabel">Assigner une table</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <p>Veuillez sélectionner une table disponible pour cette commande.</p>
                            <select name="tableId" class="form-select" required>
                                <option value="" disabled selected>Sélectionner une table...</option>
                                <option th:each="table : ${tablesDisponibles}" th:value="${table.identifiant}" th:text="'Table ' + ${table.numeroTable} + ' (' + ${table.nombrePersonne} + ' pers)'"></option>
                            </select>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Annuler</button>
                            <button type="submit" class="btn btn-success">Valider et Assigner</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
        <hr class="my-5 text-muted">
    </div>

    <!-- Section Commandes À Emporter -->"""

content = content.replace(search_modal, replace_modal)

with open(file_path, "w") as f:
    f.write(content)
