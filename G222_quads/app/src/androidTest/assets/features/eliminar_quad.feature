# Feature: Eliminar quad
# Técnica: clases de equivalencia sobre la acción de eliminación
#
# Clases de equivalencia identificadas:
#
#   Precondición:
#     1 (válida)   – quad existente
#
#   Origen de la acción:
#     2 (válida)   – eliminación desde el listado (botón en ítem)
#     3 (válida)   – eliminación desde la pantalla de detalle
#
#   Confirmación del diálogo:
#     4 (válida)   – usuario confirma la eliminación
#     5 (válida)   – usuario cancela el diálogo de confirmación
#
# Tabla de casos de prueba:
#   CP | Origen     | Acción en diálogo | Resultado esperado
#   1  | Listado    | Confirmar         | quad eliminado del listado
#   2  | Listado    | Cancelar          | quad permanece en el listado
#   3  | Detalle    | Confirmar         | quad eliminado del listado
#   4  | Detalle    | Cancelar          | quad permanece en el listado

Feature: Eliminar quad
  Como propietario de la empresa de alquiler
  Quiero poder eliminar quads del sistema
  Para retirar unidades que ya no están disponibles

  Background:
    Given Abro la aplicación de gestión de quads
    And Accedo a la sección de quads

  # eliminar quad sin reservas desde listado confirmando
  Scenario: Eliminar un quad sin reservas asociadas confirmando el diálogo
    Given Existe un quad sin reservas asociadas en el listado
    When Pulso eliminar en el primer quad del listado
    And Confirmo la eliminación en el diálogo de quad
    Then El quad ya no aparece en el listado de quads

  # cancelar la eliminación desde listado
  Scenario: Cancelar la eliminación de un quad
    Given Existe un quad sin reservas asociadas en el listado
    When Pulso eliminar en el primer quad del listado
    And Cancelo la eliminación en el diálogo de quad
    Then El quad sigue apareciendo en el listado de quads

  # eliminar quad desde detalle confirmando
  Scenario: Eliminar un quad desde la pantalla de detalle confirmando el diálogo
    Given Existe un quad sin reservas asociadas en el listado
    When Pulso sobre el primer quad del listado
    And Pulso eliminar en el detalle del quad
    And Confirmo la eliminación en el diálogo de quad
    Then El quad ya no aparece en el listado de quads

  # cancelar la eliminación desde detalle
  Scenario: Eliminar un quad desde la pantalla de detalle confirmando el diálogo
    Given Existe un quad sin reservas asociadas en el listado
    When Pulso sobre el primer quad del listado
    And Pulso eliminar en el detalle del quad
    And Cancelo la eliminación en el diálogo de quad
    Then Permanece en la pantalla de detalle del quad