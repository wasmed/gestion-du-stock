# language: fr
Fonctionnalité: Gestion des échecs de paiement
  Afin de ne pas perdre la trace d'une commande qui a été préparée et consommée
  En tant que gérant du restaurant
  Je veux qu'un paiement échoué ajoute un avertissement mais ne supprime pas la commande

  Scénario: Un paiement échoué ne supprime pas une commande servie
    Etant donné une commande existante avec des lignes à l'état "SERVIE"
    Quand un webhook Mollie notifie un échec de paiement pour cette commande
    Alors la commande n'est pas supprimée
    Et la commande contient une note d'échec dans son commentaire
