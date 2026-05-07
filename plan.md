1. **Update `StockService.java` for Automated Deduction**:
   - Edit the `processStockForPlat` method in `src/main/java/com/example/demo/service/StockService.java`.
   - Update the calculation for `quantiteAUtiliser`. Instead of `ingredient.getQuantite() * ligneCommande.getQuantite()`, it should calculate the fraction used relative to the package quantity. The correct formula is `(ingredient.getQuantite() / ingredient.getProduit().getQuantite()) * ligneCommande.getQuantite()`. Note: handle the case where `ingredient.getProduit().getQuantite()` is null or 0 to avoid division by zero (defaulting to dividing by 1.0).

2. **Verify `StockService.java` changes**:
   - Use `read_file` to verify the modified `processStockForPlat` logic.

3. **Update `StockService.java` for Manual Stock Movements**:
   - Edit the `updateStockQuantity` method in `src/main/java/com/example/demo/service/StockService.java`.
   - Change `double quantiteARajuster = quantiteSaisie * quantiteFormat;` to simply use `quantiteSaisie` so that the adjustment operates on the package level (ignoring `quantiteFormat`). Specifically, replace `double quantiteARajuster = quantiteSaisie * quantiteFormat;` with `double quantiteARajuster = quantiteSaisie;`.

4. **Verify `StockService.java` manual movement changes**:
   - Use `read_file` to verify the modified `updateStockQuantity` logic.

5. **Update UI in `stock/list.html`**:
   - Edit `src/main/resources/templates/stock/list.html`.
   - At line 73, replace `<td class="text-center" th:text="${#numbers.formatDecimal(stock.stockActuel, 1, 2)}"></td>` with `<td class="text-center" th:text="${#numbers.formatDecimal(stock.stockActuel, 1, 3)}"></td>`.

6. **Verify `stock/list.html` changes**:
   - Use `read_file` to check that the decimal formatting in the Thymeleaf template was correctly updated to 3 decimal places.

7. **Run Tests**:
   - Run `mvn clean test` to confirm that the changes have not caused any regressions.

8. **Pre-commit Checks**:
   - Complete pre-commit steps to ensure proper testing, verification, review, and reflection are done.
