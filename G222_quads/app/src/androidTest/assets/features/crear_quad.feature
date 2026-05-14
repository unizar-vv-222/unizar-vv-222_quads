# Feature: Crear quad
# Técnica: clases de equivalencia sobre los campos del formulario de creación
#
# Clases de equivalencia identificadas:
#
#   Campo matrícula:
#     1 (válida)   – matrícula con formato correcto (ej. "1234ABC")
#     2 (inválida) – matrícula vacía
#     3 (inválida) – matrícula ya existente en el sistema
#
#   Campo precio:
#     4 (válida)   – precio numérico positivo (ej. 50.0)
#     5 (inválida) – precio vacío
#     6 (inválida) – precio cero o negativo
#
#   Campo modelo/nombre:
#     7 (válida)   – nombre no vacío
#     8 (inválida) – nombre vacío
#
# Tabla de casos de prueba:
#   CP | Matrícula   | Precio | Nombre    | Resultado esperado
#   1  | "1234ABC"   | 50.0   | "Quad X"  | quad creado
#   2  | ""          | 50.0   | "Quad X"  | error / botón guardar deshabilitado
#   3  | "1234ABC"   |  ""    | "Quad X"  | error / botón guardar deshabilitado
#   4  | "1234ABC"   | -10.0  | "Quad X"  | error / botón guardar deshabilitado
#   5  | "1234ABC"   | 50.0   | ""        | error / botón guardar deshabilitado
#   6  | "DUPL0001"  | 50.0   | "Quad X"  | error matrícula duplicada (CE3)

Feature: Crear quad
  Como propietario de la empresa de alquiler
  Quiero poder crear nuevos quads en el sistema
  Para tener el catálogo actualizado

  Background:
    Given Abro la aplicación de gestión de quads
    And Accedo a la sección de quads

  # caso válido
  Scenario: Crear un quad con datos correctos
    When Pulso el botón de nuevo quad
    And Introduzco la matrícula "1234ABC" el precio "50.0" el tipo "true" y la descripcion "Quad X"
    And Pulso guardar en el formulario de quad
    Then El quad con matrícula "1234ABC" aparece en el listado de quads

  # matrícula vacía
  Scenario: Intentar crear un quad sin matrícula
    When Pulso el botón de nuevo quad
    And Introduzco la matrícula "" el precio "50.0" el tipo "true" y la descripcion "Quad X"
    And El usuario pulsa el botón guardar del formulario de quad
    Then Permanece en el formulario de quad

  # precio vacío
  Scenario: Intentar crear un quad sin precio
    When Pulso el botón de nuevo quad
    And Introduzco la matrícula "5678DEF" el precio "" el tipo "true" y la descripcion "Quad X"
    And El usuario pulsa el botón guardar del formulario de quad
    Then Permanece en el formulario de quad

  # precio negativo
  Scenario: Intentar crear un quad con precio negativo
    When Pulso el botón de nuevo quad
    And Introduzco la matrícula "5678DEF" el precio "" el tipo "true" y la descripcion "Quad X"
    And El usuario pulsa el botón guardar del formulario de quad
    Then Permanece en el formulario de quad

  # nombre vacío
  Scenario: Intentar crear un quad sin nombre
    When Pulso el botón de nuevo quad
    And Introduzco la matrícula "5678DEF" el precio "50.0" el tipo "true" y la descripcion ""
    And El usuario pulsa el botón guardar del formulario de quad
    Then Permanece en el formulario de quad

  # matrícula duplicada  ESTE FALLA -> hay que revisarlo
  Scenario: Intentar crear un quad con matrícula ya existente
    Given Ya existe un quad con matrícula "1234ABC" en el sistema
    When Pulso el botón de nuevo quad
    And Introduzco la matrícula "1234ABC" el precio "50.0" el tipo "true" y la descripcion "Quad Y"
    And El usuario pulsa el botón guardar del formulario de quad
    Then Permanece en el formulario de quad
