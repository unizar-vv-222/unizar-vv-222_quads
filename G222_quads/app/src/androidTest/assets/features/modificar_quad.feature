# Feature: Modificar quad
# Técnica: clases de equivalencia sobre los campos editables del formulario
#
# Clases de equivalencia identificadas:
#
#   Precondición (quad existente):
#     1 (válida)   – quad existente seleccionado para editar
#
#   Campo precio:
#     2 (válida)   – precio numérico positivo distinto al original
#     3 (inválida) – precio vacío
#     4 (inválida) – precio cero o negativo
#
#   Campo nombre:
#     5 (válida)   – nombre no vacío distinto al original
#     6 (inválida) – nombre vacío
#
#   Acción del usuario:
#     7 (válida)   – usuario confirma los cambios (guardar)
#     8 (válida)   – usuario cancela la edición (sin cambios)
#
# Tabla de casos de prueba:
#   CP | Precio nuevo | Nombre nuevo | Acción   | Resultado esperado
#   1  | 80.0         | "Quad Mod"   | guardar  | quad actualizado en listado
#   2  | ""           | "Quad Mod"   | guardar  | error / botón guardar deshabilitado
#   3  | -5.0         | "Quad Mod"   | guardar  | error / botón guardar deshabilitado
#   4  | 80.0         | ""           | guardar  | error / botón guardar deshabilitado
#   5  | 80.0         | "Quad Mod"   | cancelar | quad sin cambios en listado

Feature: Modificar quad
  Como propietario de la empresa de alquiler
  Quiero poder modificar los datos de un quad existente
  Para mantener el catálogo actualizado

  Background:
    Given Abro la aplicación de gestión de quads
    And Accedo a la sección de quads
    And Existe al menos un quad en el listado

  # modificación válida
  Scenario: Modificar precio y nombre de un quad correctamente
    When Pulso editar en el primer quad del listado
    And Cambio el precio a "80.0" y la descripcion a "Quad Modificado" del quad
    And Pulso guardar en el formulario de quad
    Then El listado de quads es visible con al menos un elemento

  # precio vacío al modificar
  Scenario: Intentar modificar un quad dejando el precio vacío
    When Pulso editar en el primer quad del listado
    And Borro el precio del formulario de quad
    And El usuario pulsa el botón guardar del formulario de quad
    Then Permanece en el formulario de quad

  # precio negativo al modificar
  Scenario: Intentar modificar un quad con precio negativo
    When Pulso editar en el primer quad del listado
    And Cambio el precio a "-5.0" y la descripcion a "Quad Modificado" del quad
    And El usuario pulsa el botón guardar del formulario de quad
    Then Permanece en el formulario de quad

  # nombre vacío al modificar
  Scenario: Intentar modificar un quad dejando el nombre vacío
    When Pulso editar en el primer quad del listado
    And Cambio el precio a "80.0" y la descripcion a "" del quad
    And El usuario pulsa el botón guardar del formulario de quad
    Then Permanece en el formulario de quad

  # cancelar la edición
  Scenario: Cancelar la modificación de un quad
    When Pulso editar en el primer quad del listado
    And Cambio el precio a "999.0" y la descripcion a "No debe guardarse" del quad
    And Pulso cancelar en el formulario de quad
    Then El quad no muestra el nombre "No debe guardarse" en el listado
