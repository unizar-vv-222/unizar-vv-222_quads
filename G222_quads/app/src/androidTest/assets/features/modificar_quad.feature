# Feature: Modificar quad
# Técnica: clases de equivalencia sobre los campos editables del formulario
#
# Clases de equivalencia identificadas:
#
#   Precondición (quad existente):
#     1 (válida)   – quad existente seleccionado para editar
#
#   Origen de la acción:
#     2 (válida)   – modificación desde el listado (botón en ítem)
#     3 (válida)   – modificación desde la pantalla de detalle
#
#   Campo tipo:
#     4 (válida)   – tipo True (biplaza)
#     5 (válida)   - tipo False (monoplaza)
#
#   Campo precio:
#     6 (válida)   – precio positivo
#     7 (inválida) – precio cero o negativo
#
#   Campo descripción:
#     8 (válida)   – descripción no vacío
#     9 (inválida) – descripción vacío
#
#   Acción del usuario:
#     10 (válida)   – usuario confirma los cambios (guardar)
#     11 (válida)   – usuario cancela la edición (sin cambios)
#
# Tabla de casos de prueba:
#   CP | Tipo   | Precio | Descripción   | Origen   | Acción   | Resultado esperado
#   1  | True   | 80.0   | "Quad Mod"    | Listado  | guardar  | quad actualizado en listado
#   2  | False  | 80.0   | "Quad Mod"    | Detalle  | guardar  | quad actualizado en listado
#   3  | False  | 80.0   | "Quad Mod"    | Listado  | cancelar | sin cambios en listado
#   4  | False  | -5.0   | "Quad Mod"    | Listado  | guardar  | error / botón guardar deshabilitado
#   5  | False  | 80.0   | ""            | Listado  | guardar  | error / botón guardar deshabilitado

Feature: Modificar quad
  Como propietario de la empresa de alquiler
  Quiero poder modificar los datos de un quad existente
  Para mantener el catálogo actualizado

  Background:
    Given Abro la aplicación de gestión de quads
    And Accedo a la sección de quads
    And Existe al menos un quad en el listado

  # modificaciones válidas
  Scenario: Modificar tipo, precio y descripción de un quad correctamente a biplaza desde listado
    When Pulso editar en el primer quad del listado
    And Cambio el tipo a "true", el precio a "80.0" y la descripcion a "Quad Modificado" del quad
    And Pulso guardar en el formulario de quad
    Then El quad seleccionado muestra en detalle el tipo "true", el precio "80.00" y la descripción "Quad Modificado"

  Scenario: Modificar tipo, precio y descripción de un quad correctamente a monoplaza desde detalle
    When Pulso sobre el primer quad del listado
    And Pulso editar en el detalle del quad
    And Cambio el tipo a "false", el precio a "80.0" y la descripcion a "Quad Modificado" del quad
    And Pulso guardar en el formulario de quad
    Then El quad seleccionado muestra en detalle el tipo "false", el precio "80.00" y la descripción "Quad Modificado"

      # cancelar la edición
  Scenario: Cancelar la modificación de un quad
    When Pulso editar en el primer quad del listado
    And Cambio el tipo a "false", el precio a "999.0" y la descripcion a "No debe guardarse" del quad
    And Pulso cancelar en el formulario de quad
    Then El quad seleccionado no muestra en detalle la descripción "No debe guardarse"
  # precio negativo al modificar
  Scenario: Intentar modificar un quad con precio negativo
    When Pulso editar en el primer quad del listado
    And Cambio el tipo a "false", el precio a "-5.0" y la descripcion a "Quad Modificado" del quad
    And Pulso guardar en el formulario de quad
    Then Permanece en el formulario de quad

  # descripción vacía al modificar
  Scenario: Intentar modificar un quad dejando la descripción vacía
    When Pulso editar en el primer quad del listado
    And Cambio el tipo a "false", el precio a "80.0" y la descripcion a "" del quad
    And Pulso guardar en el formulario de quad
    Then Permanece en el formulario de quad

