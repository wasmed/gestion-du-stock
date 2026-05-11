import sys

file_path = "src/main/java/com/example/demo/controller/OrderManagementController.java"
with open(file_path, "r") as f:
    content = f.read()

search = """    @PostMapping("/validate/{id}")
    @PreAuthorize("hasRole('SERVEUR')")
    public String validateOrder(@PathVariable Long id, @RequestParam Long tableId, Principal principal, RedirectAttributes redirectAttributes) {
        Commande commande = commandeService.findCommandeById(id);
        if (commande != null && commande.getEtat() == EtatCommande.EN_VALIDATION) {
            User serveur = userService.findUserByEmail(principal.getName());
            commande.setServeur(serveur);
            commande.setEtat(EtatCommande.EN_PREPARATION);

            TableRestaurant table = tableRepository.findById(tableId).orElse(null);
            if (table != null && table.getStatut() == StatutTable.LIBRE) {
                table.setStatut(StatutTable.OCCUPEE);
                table.setServeur(serveur);
                tableRepository.save(table);
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "La table sélectionnée n'est pas disponible.");
                return "redirect:/orders";
            }
            commande.setTable(table);
            commandeService.saveCommande(commande);"""

replace = """    @RequestMapping(value = "/validate/{id}", method = {RequestMethod.GET, RequestMethod.POST})
    @PreAuthorize("hasRole('SERVEUR')")
    public String validateOrder(@PathVariable Long id, @RequestParam(required = false) Long tableId, Principal principal, RedirectAttributes redirectAttributes) {
        Commande commande = commandeService.findCommandeById(id);
        if (commande != null && commande.getEtat() == EtatCommande.EN_VALIDATION) {
            User serveur = userService.findUserByEmail(principal.getName());
            commande.setServeur(serveur);
            commande.setEtat(EtatCommande.EN_PREPARATION);

            if (!Boolean.TRUE.equals(commande.getIsEmporter()) && tableId != null) {
                TableRestaurant table = tableRepository.findById(tableId).orElse(null);
                if (table != null && table.getStatut() == StatutTable.LIBRE) {
                    table.setStatut(StatutTable.OCCUPEE);
                    table.setServeur(serveur);
                    tableRepository.save(table);
                    commande.setTable(table);
                } else {
                    redirectAttributes.addFlashAttribute("errorMessage", "La table sélectionnée n'est pas disponible.");
                    return "redirect:/orders";
                }
            }
            commandeService.saveCommande(commande);"""

content = content.replace(search, replace)

if "import org.springframework.web.bind.annotation.RequestMethod;" not in content:
    content = content.replace("import org.springframework.web.bind.annotation.*;", "import org.springframework.web.bind.annotation.*;\nimport org.springframework.web.bind.annotation.RequestMethod;")

with open(file_path, "w") as f:
    f.write(content)
