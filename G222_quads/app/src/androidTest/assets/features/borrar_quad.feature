# Feature: Borrar quad
# Técnica: clases de equivalencia sobre la acción de eliminación
#
# Clases de equivalencia identificadas:
#
#   Precondición (quad existente):
#     1 (válida)   – quad existente sin reservas asociadas
#     2 (inválida) – quad existente con reservas activas asociadas
#
#   Confirmación del diálogo:
#     3 (válida)   – usuario confirma la eliminación
#     4 (válida)   – usuario cancela el diálogo de confirmación
#
# Tabla de casos de prueba:
#   CP | Quad tiene reservas | Acción en diálogo | Resultado esperado
#   1  | No                  | Confirmar         | quad eliminado del listado
#   2  | No                  | Cancelar          | quad permanece en el listado
#   3  | Sí                  | Confirmar         | error / no se permite eliminar

Feature: Borrar quad
  Como propietario de la empresa de alquiler
  Quiero poder eliminar quads del sistema
  Para retirar unidades que ya no están disponibles

  Background:
    Given Abro la aplicación de gestión de quads
    And Accedo a la sección de quads

  # eliminar quad sin reservas confirmando
  Scenario: Eliminar un quad sin reservas asociadas confirmando el diálogo
    Given Existe un quad sin reservas asociadas en el listado
    When Pulso eliminar en ese quad del listado
    And Confirmo la eliminación en el diálogo de quad
    Then El quad ya no aparece en el listado de quads

  # cancelar la eliminación
  Scenario: Cancelar la eliminación de un quad
    Given Existe un quad sin reservas asociadas en el listado
    When Pulso eliminar en ese quad del listado
    And Cancelo la eliminación en el diálogo de quad
    Then El quad sigue apareciendo en el listado de quads

  # quad con reservas activas (Este test va completamente borracho, no debe pasar y a veces pasa por amor al arte)
  Scenario: Intentar eliminar un quad que tiene reservas activas
    Given Existe un quad con reservas activas asociadas en el listado
    When Pulso eliminar en ese quad del listado
    Then Se muestra un error indicando que el quad tiene reservas y no puede eliminarse
