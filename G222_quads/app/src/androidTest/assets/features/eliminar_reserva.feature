# Feature: Eliminar reserva (sin curso alternativo)
# Técnica: clases de equivalencia sobre la acción de eliminación
#
# Clases de equivalencia identificadas:
#
#   Precondición (reserva existente):
#     1 (válida)   – reserva existente en el listado
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
#   CP | Origen    | Diálogo   | Resultado esperado
#   1  | listado   | confirmar | reserva eliminada del listado
#   2  | listado   | cancelar  | reserva permanece en el listado
#   3  | detalle   | confirmar | reserva eliminada, vuelve al listado
#   4  | detalle   | cancelar  | reserva permanece, sigue en detalle

Feature: Eliminar reserva
  Como propietario de la empresa de alquiler
  Quiero poder eliminar reservas del sistema
  Para mantener el listado limpio y actualizado

  Background:
    Given Abro la aplicación de gestión de quads
    And Accedo a la sección de reservas
    And Existe al menos una reserva en el listado

  # eliminar desde listado confirmando
  Scenario: Eliminar una reserva desde el listado confirmando el diálogo
    When Pulso eliminar en la primera reserva del listado de reservas
    And Confirmo la eliminación en el diálogo de reserva
    Then La reserva ya no aparece en el listado de reservas

  # cancelar eliminación desde listado
  Scenario: Cancelar la eliminación de una reserva desde el listado
    When Pulso eliminar en la primera reserva del listado de reservas
    And Cancelo la eliminación en el diálogo de reserva desde el listado
    Then La reserva sigue apareciendo en el listado de reservas

  # eliminar desde detalle confirmando
  Scenario: Eliminar una reserva desde su pantalla de detalle confirmando el diálogo
    When Pulso sobre la primera reserva del listado para ver su detalle
    And Pulso el botón eliminar en el detalle de la reserva
    And Confirmo la eliminación en el diálogo de reserva
    Then Vuelvo al listado de reservas y la reserva eliminada no aparece

  # cancelar eliminación desde detalle
  Scenario: Cancelar la eliminación de una reserva desde su pantalla de detalle
    When Pulso sobre la primera reserva del listado para ver su detalle
    And Pulso el botón eliminar en el detalle de la reserva
    And Cancelo la eliminación en el diálogo de reserva
    Then Sigo en la pantalla de detalle de la reserva
