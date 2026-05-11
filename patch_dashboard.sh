cat << 'PATCH_EOF' > /tmp/dashboard.patch
--- src/main/resources/templates/serveur/dashboard.html
+++ src/main/resources/templates/serveur/dashboard.html
@@ -120,9 +120,9 @@
                         </div>
                     </div>
                     <div class="card-footer bg-white border-top-0 d-flex gap-2 pb-3">
-                        <a th:href="@{/orders/validate/{id}(id=${cmd.id})}" class="btn btn-success flex-grow-1 fw-bold shadow-sm">
+                        <button type="button" class="btn btn-success flex-grow-1 fw-bold shadow-sm" data-bs-toggle="modal" th:data-bs-target="'#validateModal' + ${cmd.id}">
                             <i class="bi bi-check-circle-fill me-1"></i> Valider
-                        </a>
+                        </button>
                         <a th:href="@{/orders/delete/{id}(id=${cmd.id})}" class="btn btn-outline-danger shadow-sm" title="Refuser / Supprimer" onclick="return confirm('Voulez-vous vraiment refuser cette commande ?');">
                             <i class="bi bi-x-lg"></i>
                         </a>
@@ -130,6 +130,29 @@
                 </div>
             </div>
         </div>
+        <!-- Modals for Table Validation -->
+        <div th:each="cmd : ${commandesAValider}" class="modal fade" th:id="'validateModal' + ${cmd.id}" tabindex="-1" aria-labelledby="validateModalLabel" aria-hidden="true">
+            <div class="modal-dialog">
+                <div class="modal-content">
+                    <form th:action="@{/orders/validate/{id}(id=${cmd.id})}" method="post">
+                        <div class="modal-header bg-success text-white">
+                            <h5 class="modal-title" id="validateModalLabel">Assigner une table</h5>
+                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
+                        </div>
+                        <div class="modal-body">
+                            <p>Veuillez sélectionner une table disponible pour cette commande.</p>
+                            <select name="tableId" class="form-select" required>
+                                <option value="" disabled selected>Sélectionner une table...</option>
+                                <option th:each="table : ${tablesDisponibles}" th:value="${table.identifiant}" th:text="'Table ' + ${table.numeroTable} + ' (' + ${table.nombrePersonne} + ' pers)'"></option>
+                            </select>
+                        </div>
+                        <div class="modal-footer">
+                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Annuler</button>
+                            <button type="submit" class="btn btn-success">Valider et Assigner</button>
+                        </div>
+                    </form>
+                </div>
+            </div>
+        </div>
         <hr class="my-5 text-muted">
     </div>

PATCH_EOF
patch src/main/resources/templates/serveur/dashboard.html < /tmp/dashboard.patch
