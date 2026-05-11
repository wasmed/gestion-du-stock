1. **Fix SQL `NOT IN` NULL Trap in `TableRestaurantRepository`**
   - Update the `findAvailableTablesNotInActiveOrders()` query to explicitly filter out null tables from the subquery (`SELECT c.table FROM Commande c JOIN c.lignesCommande lc WHERE c.table IS NOT NULL AND ...`). This prevents the query from failing when there are takeaway orders.
2. **Fix Validation Endpoint Regression for Takeaway Orders**
   - In `OrderManagementController.validateOrder`, make `tableId` an optional parameter (`@RequestParam(required = false) Long tableId`). Ensure that takeaway orders (or orders not requiring a table) can still be validated. Wait, if it's a GET request for takeaway orders from the dashboard, maybe I should leave it as POST and make the takeaway validation also use a form? Or allow both? I will make it accept `GET` and `POST` by using `@RequestMapping(value = "/validate/{id}", method = {RequestMethod.GET, RequestMethod.POST})`. Alternatively, I can just change the takeaway validation link in the dashboard to use a POST form without a `tableId` field. Let's make the endpoint `@RequestMapping(value = "/validate/{id}", method = {RequestMethod.GET, RequestMethod.POST})` and `tableId` optional.
3. **Fix JPA Collection Corruption in `ClientController`**
   - In `ClientController.validateCart`, ensure that `lignes.add(ligne);` is only called when a *new* `LigneCommande` is created, not when updating an existing one. It should be inside the `else` block.
4. **Fix Stock Double-Deduction in `OrderManagementController` (`editOrder`)**
   - In `OrderManagementController.editOrder`, when an existing line is updated, only the *delta* (the newly added `quantite`) should be deducted from stock, OR a new temporary `LigneCommande` object should be created just for the `processStockDecrementForLigne` call with the delta quantity. Let's see: `stockService.processStockDecrementForLigne(ligne)` deducts based on `ligne.getQuantite()`. If we update the existing line, it's probably better to just create a dummy line with the delta quantity to pass to the stock service, or modify `processStockDecrementForLigne`? No, simpler: `LigneCommande deltaLigne = new LigneCommande(); deltaLigne.setPlat(plat); deltaLigne.setQuantite(quantite); stockService.processStockDecrementForLigne(deltaLigne);`
5. **Verify Fixes**
   - Run `mvn test` again.
6. **Pre-commit Instructions**
   - Complete pre-commit steps.
7. **Submit**
   - Submit the change.
